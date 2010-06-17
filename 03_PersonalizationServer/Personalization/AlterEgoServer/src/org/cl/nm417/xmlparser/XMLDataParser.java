/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.xmlparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.*;
import org.cl.nm417.data.htmltags.Anchor;
import org.cl.nm417.data.htmltags.Cell;
import org.cl.nm417.data.htmltags.Heading;
import org.cl.nm417.data.htmltags.Image;
import org.cl.nm417.data.htmltags.List;
import org.cl.nm417.data.htmltags.Table;
import org.cl.nm417.data.htmltags.Textual;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import spiaotools.SentParDetector;

/**
 * Class used to parse a user's XML file containing all of its processed
 * browsing history
 */
public class XMLDataParser extends DefaultHandler {
	
	// List of documents in the user's browsing history
	private ArrayList<Document> documents = new ArrayList<Document>();
	
	// Helpers to keep track of the current tag
	private boolean inURL = false;
	private boolean inDuration = false;
	private boolean inVisitdate = false;
	private boolean inPlaintext = false;
	private boolean inTitle = false;
	private boolean inMetadescription = false;
	private boolean inMetakeyword = false;
	private boolean inTerm = false;
	private boolean inId = false;
	private boolean inAnchor = false;
	private boolean inHeading = false;
	private boolean inTextual = false;
	private boolean inItem = false;
	private boolean inCell = false;
	
	// List of metadata keywords in the current document
	private ArrayList<String> keywords = new ArrayList<String>();
	// List of terms in the current document
	private ArrayList<String> terms = new ArrayList<String>();
	// List of images in the current document
	private ArrayList<Image> images = new ArrayList<Image>();
	// List of links in the current document
	private ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	// List of headings in the current document
	private ArrayList<Heading> headings = new ArrayList<Heading>();
	// List of textual elements in the current document
	private ArrayList<Textual> textuals = new ArrayList<Textual>();
	
	// Helpers used to keep track of the content of the current tag
	// if it spans more than 1 line
	private String currentKeyword = "";
	private String currentTerm;
	private Anchor currentAnchor = new Anchor();
	private Heading currentHeading = new Heading();
	private Textual currentTextual = new Textual();
	private List currentList = new List();
	private Table currentTable = new Table();
	private Cell currentCell = new Cell();
	StringBuilder plaintextBuilder = new StringBuilder();
	StringBuilder anchorBuilder = new StringBuilder();
	StringBuilder headingBuilder = new StringBuilder();
	StringBuilder textualBuilder = new StringBuilder();
	StringBuilder itemBuilder = new StringBuilder();
	StringBuilder cellBuilder = new StringBuilder();
	
	// Sentence splitter
	private SentParDetector det = new SentParDetector();
	
	private BufferedWriter out = null;
	private boolean parseFileExists = false;
	
	// Current web document
	private Document currentDocument = new Document();
	private static final Pattern pattern = Pattern.compile("\\s+");
	
	/**
	 * Called when a tag is opened in the XML file
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("documents")){
			// Initialization
			String path = ConfigLoader.getConfig().getProperty("profiles") + (String)AlterEgo.config.get("user") + ".cc.txt";
			if (AlterEgo.config.containsKey("ccfile")){
				path = (String) AlterEgo.config.get("ccfile") + "cc.txt";
			}
			// Only write the file of text to be parsed if necessairy
			if (!new File(path).exists() && (AlterEgo.config.get("writecc") == null || (Boolean) AlterEgo.config.get("writecc"))){
				FileWriter fstream = null;
				try {
					fstream = new FileWriter(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			    out = new BufferedWriter(fstream); 
			} else {
				parseFileExists = true;
			}
		} else if (qName.equals("id")){ 
			inId = true;
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
		} else if (qName.equals("img")){
			Image image = new Image();
			image.setAlt(attributes.getValue("alt"));
			image.setTitle(attributes.getValue("title"));
			images.add(image);
		} else if (qName.equals("a")){
			inAnchor = true;
			currentAnchor.setTitle(attributes.getValue("title"));
			currentAnchor.setHref(attributes.getValue("href"));
		} else if (qName.equals("heading")){
			inHeading = true;
			currentHeading.setType(attributes.getValue("type"));
		} else if (qName.equals("text")){
			inTextual = true;
			currentTextual.setType(attributes.getValue("type"));
		} else if (qName.equals("list")){
			currentList.setType(attributes.getValue("type"));
		} else if (qName.equals("item")){
			inItem = true;
		} else if (qName.equals("cell")){
			currentCell.setType(attributes.getValue("type"));
			inCell = true;
		}
	}

	/**
	 * Called when content is found inside a tag in the XML document
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		String input = new String(ch, start, length);
		if (inURL){
			currentDocument.setUrl(normalize(currentDocument.getUrl() + input));
		} else if (inId){
			String id = input.trim();
			if (id.length() > 0){
				currentDocument.setId(Integer.parseInt(id));
			}
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
		} else if (inAnchor){
			anchorBuilder.append(" " + input);
		} else if (inHeading){
			headingBuilder.append(" " + input);
		} else if (inTextual){
			textualBuilder.append(" " + input);
		} else if (inItem){
			itemBuilder.append(" " + input);
		} else if (inCell){
			cellBuilder.append(" " + input); 
		}
	}

	/**
	 * Remove redundant spaces in a string
	 * @param string	Input string
	 * @return			Input string with redundant spaces removed
	 */
	private String normalize(String string) {
		string = string.replaceAll("\n", " ").replace("[ ][ ]", " ").trim();
		return string;
	}

	/**
	 * Called when a tag is closed in the XML document
	 */
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
			String pt = normalize(plaintextBuilder.toString());
			currentDocument.setPlaintext(pattern.split(pt));
			// Split the sentence and write to the file with sentences to be parsed
			if ((Boolean)AlterEgo.config.get("ccparse") && !parseFileExists){
		    	try {
		    		out.write(det.markupRawText(2, normalize(plaintextBuilder.toString())));
				} catch (Exception ex){
					ex.printStackTrace();
				}
				try {
					// Insert a document separator
					out.write("\n</alteregodocument>\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			currentDocument.setMetakeywords(keywords);
			currentDocument.setTerms(terms);
			currentDocument.setImages(images);
			currentDocument.setAnchors(anchors);
			currentDocument.setHeadings(headings);
			currentDocument.setTextual(textuals); 
			images = new ArrayList<Image>();
			keywords = new ArrayList<String>();
			terms = new ArrayList<String>();
			anchors = new ArrayList<Anchor>();
			headings = new ArrayList<Heading>();
			textuals = new ArrayList<Textual>();
			plaintextBuilder = new StringBuilder();
			currentDocument = new Document();
		} else if (qName.equals("id")){
			inId = false;
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
		} else if (qName.equals("a")){
			inAnchor = false;
			currentAnchor.setText(normalize(anchorBuilder.toString()));
			anchorBuilder = new StringBuilder();
			anchors.add(currentAnchor);
			currentAnchor = new Anchor();
		} else if (qName.equals("heading")){
			inHeading = false;
			currentHeading.setText(normalize(headingBuilder.toString()));
			headingBuilder = new StringBuilder();
			headings.add(currentHeading);
			currentHeading = new Heading();
		} else if (qName.equals("text")){
			inTextual = false;
			currentTextual.setText(normalize(textualBuilder.toString()));
			textualBuilder = new StringBuilder();
			textuals.add(currentTextual);
			currentTextual = new Textual();
		} else if (qName.equals("list")){
			currentDocument.getLists().add(currentList);
			currentList = new List();
		} else if (qName.equals("item")){
			inItem = false;
			currentList.getItems().add(normalize(itemBuilder.toString()));
			itemBuilder = new StringBuilder();
		} else if (qName.equals("table")){
			currentDocument.getTables().add(currentTable);
			currentTable = new Table();
		} else if (qName.equals("cell")){
			inCell = false;
			currentCell.setText(normalize(cellBuilder.toString()));
			cellBuilder = new StringBuilder();
			currentTable.getCells().add(currentCell);
			currentCell = new Cell();
		}
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}
	
}
