package org.cl.nm417.xmlparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.data.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import spiaotools.SentParDetector;

public class XMLDataParser extends DefaultHandler {
	
	private ArrayList<Document> documents = new ArrayList<Document>();
	
	private boolean inURL = false;
	private boolean inDuration = false;
	private boolean inVisitdate = false;
	private boolean inPlaintext = false;
	private boolean inTitle = false;
	private boolean inMetadescription = false;
	private boolean inMetakeyword = false;
	private boolean inTerm = false;
	//private boolean inAnchor = false;
	//private boolean inHeading = false;
	//private boolean inTextual = false;
	//private boolean inItem = false;
	//private boolean inCell = false;
	private ArrayList<String> keywords = new ArrayList<String>();
	private ArrayList<String> terms = new ArrayList<String>();
	//private ArrayList<Image> images = new ArrayList<Image>();
	//private ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	//private ArrayList<Heading> headings = new ArrayList<Heading>();
	//private ArrayList<Textual> textuals = new ArrayList<Textual>();
	private String currentKeyword = "";
	private String currentTerm;
	//private Anchor currentAnchor = new Anchor();
	//private Heading currentHeading = new Heading();
	//private Textual currentTextual = new Textual();
	//private List currentList = new List();
	//private Table currentTable = new Table();
	//private Cell currentCell = new Cell();
	StringBuilder plaintextBuilder = new StringBuilder();
	//StringBuilder anchorBuilder = new StringBuilder();
	//StringBuilder headingBuilder = new StringBuilder();
	//StringBuilder textualBuilder = new StringBuilder();
	//StringBuilder itemBuilder = new StringBuilder();
	//StringBuilder cellBuilder = new StringBuilder();
	private SentParDetector det = new SentParDetector();
	private BufferedWriter out = null;
	private boolean parseFileExists = false;
	
	private Document currentDocument = new Document();
	
	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("documents")){
			String path = "/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + (String)AlterEgo.config.get("user") + ".cc.txt";
			if (!new File(path).exists()){
				FileWriter fstream = null;
				try {
					/* Tokenizer.main(new String[]{"/Users/nicolaas/Downloads/EnglishTok.bin"}); */
					fstream = new FileWriter(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			    out = new BufferedWriter(fstream); 
			} else {
				parseFileExists = true;
			}
		} else if (qName.equals("url")){
			inURL = true;
		} else if (qName.equals("duration")){
			inDuration = true;
		} else if (qName.equals("plaintext")){
			inPlaintext = true;
		} else if (qName.equals("title")){
			inTitle = true;
		} else if (qName.equals("metadescription")){
			inMetadescription = true;
		} else if (qName.equals("keyword")){
			inMetakeyword = true;
		} else if (qName.equals("term")){
			inTerm = true;
		} else if (qName.equals("visitdate")){
			inVisitdate = true;
		//} else if (qName.equals("img")){
			//Image image = new Image();
			//image.setAlt(attributes.getValue("alt"));
			//image.setTitle(attributes.getValue("title"));
			//images.add(image);
		//} else if (qName.equals("a")){
			//inAnchor = true;
			//currentAnchor.setTitle(attributes.getValue("title"));
			//currentAnchor.setHref(attributes.getValue("href"));
		//} else if (qName.equals("heading")){
			//inHeading = true;
			//currentHeading.setType(attributes.getValue("type"));
		//} else if (qName.equals("text")){
			//inTextual = true;
			//currentTextual.setType(attributes.getValue("type"));
		//} else if (qName.equals("list")){
			//currentList.setType(attributes.getValue("type"));
		//} else if (qName.equals("item")){
			//inItem = true;
		//} else if (qName.equals("cell")){
			//currentCell.setType(attributes.getValue("type"));
			//inCell = true;
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		String input = new String(ch, start, length);
		if (inURL){
			currentDocument.setUrl(normalize(currentDocument.getUrl() + input));
		} else if (inDuration){
			currentDocument.setDuration(normalize(currentDocument.getDuration() + input));
		} else if (inVisitdate){
			currentDocument.setVisitdate(normalize(currentDocument.getVisitdate() + input));
		} else if (inPlaintext){
			plaintextBuilder.append(" " + input);
		} else if (inTitle){
			currentDocument.setTitle(normalize(currentDocument.getTitle() + " " + input));
		} else if (inMetadescription){
			currentDocument.setMetadescription(normalize(currentDocument.getMetadescription() + " " + input));
		} else if (inMetakeyword){
			currentKeyword += " " + input;
		} else if (inTerm){
			currentTerm += " " + input;
		//} else if (inAnchor){
			//anchorBuilder.append(" " + input);
		//} else if (inHeading){
			//headingBuilder.append(" " + input);
		//} else if (inTextual){
			//textualBuilder.append(" " + input);
		//} else if (inItem){
			//itemBuilder.append(" " + input);
		//} else if (inCell){
			//cellBuilder.append(" " + input); 
		}
	}

	private String normalize(String string) {
		string = string.replaceAll("\n", " ").replace("[ ][ ]", " ").trim();
		return string;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("documents")){
			if (!parseFileExists){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (qName.equals("document")){
			currentDocument.setPlaintext(normalize(plaintextBuilder.toString()));
			// Sentence splitting
			if ((Boolean)AlterEgo.config.get("ccparse") && !parseFileExists){
		    	try {
		    		out.write(det.markupRawText(2, currentDocument.getPlaintext()));
		    		/*Tokenization
		    		String sentsplit = det.markupRawText(2, currentDocument.getPlaintext());
		    		for (String s: sentsplit.split("\n")){
		    			System.out.println(s);
		    			Span[] tok = Tokenizer.split(s);
		    			for (Span sp: tok){
		    				System.out.println(s.substring(sp.getStart(),sp.getEnd()));
		    				out.write(s.substring(sp.getStart(),sp.getEnd()));
		    			}
		    		} */
				} catch (Exception ex){
					//ex.printStackTrace();
				}
				try {
					out.write("\n</alteregodocument>\n");
				} catch (IOException e) {}
			}
			currentDocument.setMetakeywords(keywords);
			currentDocument.setTerms(terms);
			// currentDocument.setImages(images);
			//currentDocument.setAnchors(anchors);
			//currentDocument.setHeadings(headings);
			//currentDocument.setTextual(textuals); 
			// images = new ArrayList<Image>();
			keywords = new ArrayList<String>();
			terms = new ArrayList<String>();
			//anchors = new ArrayList<Anchor>();
			//headings = new ArrayList<Heading>();
			//textuals = new ArrayList<Textual>();
			plaintextBuilder = new StringBuilder();
			getDocuments().add(currentDocument);
			currentDocument = new Document();
		} else if (qName.equals("url")){
			inURL = false;
		} else if (qName.equals("duration")){
			inDuration = false;
		} else if (qName.equals("plaintext")){
			inPlaintext = false;
		} else if (qName.equals("title")){
			inTitle = false;
		} else if (qName.equals("metadescription")){
			inMetadescription = false;
		} else if (qName.equals("keyword")){
			keywords.add(normalize(currentKeyword));
			currentKeyword = "";
			inMetakeyword = false;
		} else if (qName.equals("term")){
			terms.add(normalize(currentTerm));
			currentTerm = "";
			inTerm = false;
		} else if (qName.equals("visitdate")){
			inVisitdate = false;
		//} else if (qName.equals("a")){
		//	inAnchor = false;
		//	currentAnchor.setText(normalize(anchorBuilder.toString()));
		//	anchorBuilder = new StringBuilder();
		//	anchors.add(currentAnchor);
		//	currentAnchor = new Anchor();
		//} else if (qName.equals("heading")){
		//	inHeading = false;
		//	currentHeading.setText(normalize(headingBuilder.toString()));
		//	headingBuilder = new StringBuilder();
		//	headings.add(currentHeading);
		//	currentHeading = new Heading();
		//} else if (qName.equals("text")){
		//	inTextual = false;
		//	currentTextual.setText(normalize(textualBuilder.toString()));
		//	textualBuilder = new StringBuilder();
		//	textuals.add(currentTextual);
		//	currentTextual = new Textual();
		//} else if (qName.equals("list")){
		//	currentDocument.getLists().add(currentList);
		//	currentList = new List();
		//} else if (qName.equals("item")){
		//	inItem = false;
		//	currentList.getItems().add(normalize(itemBuilder.toString()));
		//	itemBuilder = new StringBuilder();
		//} else if (qName.equals("table")){
		//	currentDocument.getTables().add(currentTable);
		//	currentTable = new Table();
		//} else if (qName.equals("cell")){
		//	inCell = false;
		//	currentCell.setText(normalize(cellBuilder.toString()));
		//	cellBuilder = new StringBuilder();
		//	currentTable.getCells().add(currentCell);
		//	currentCell = new Cell();
		}
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}
	
}
