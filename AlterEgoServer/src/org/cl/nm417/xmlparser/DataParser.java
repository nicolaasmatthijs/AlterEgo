package org.cl.nm417.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.cctools.CCParser;
import org.cl.nm417.data.Document;
import org.xml.sax.SAXException;

public class DataParser {

	private ArrayList<Document> documents;
	
	public DataParser(String userid){
		
	    try {

	    	//get a factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
		    
			//parse the file and also register this class for call backs
		    XMLDataParser parser = new XMLDataParser();
	    	saxParser.parse(determinePath(userid), parser);
	    	documents = parser.getDocuments();
	    	
	    	String path = "/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/";
	    	String longpath = path + (String)AlterEgo.config.get("user") + ".cc"; 
	    	
	    	if ((Boolean)AlterEgo.config.get("ccparse")){
	    		if (!new File(longpath + ".parsed.txt").exists()){
		    		// Parsing
		    		try {
		                Runtime rt = Runtime.getRuntime();
		                Process pr = rt.exec("/Users/nicolaas/Desktop/AlterEgo/CCParser/candc-1.00/bin/candc " +
		                		"--models /Users/nicolaas/Desktop/AlterEgo/CCParser/models " + 
		                		"--input " + longpath + ".txt " + 
		                		"--output " + longpath + ".parsed.txt " +
		                		"--log " + longpath + ".log.txt");
		 
		                int exitVal = pr.waitFor();
		                System.out.println("Exited with error code "+exitVal);
		 
		            } catch(Exception e) {
		                System.out.println(e.toString());
		                e.printStackTrace();
		            }
		    	}
	    		documents = CCParser.readCCParse(longpath + ".parsed.txt", documents);
	    	}

		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private String determinePath(String userid) {
		String basepath = "/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/";
		String path = "eval/" + userid + ".xml";
		if (new File(basepath + path).exists()){
			return basepath + path;
		} 
		path = "development/" + userid + ".xml";
		if (new File(basepath + path).exists()){
			return basepath + path;
		} 
		path = "train/" + userid + ".xml";
		if (new File(basepath + path).exists()){
			return basepath + path;
		}
		path = "test/" + userid + ".xml";
		if (new File(basepath + path).exists()){
			return basepath + path;
		}
		return "";
	}

	public void setDocuments(ArrayList<Document> documents) {
		this.documents = documents;
	}

	public ArrayList<Document> getDocuments() {
		return documents;
	}
	
}
