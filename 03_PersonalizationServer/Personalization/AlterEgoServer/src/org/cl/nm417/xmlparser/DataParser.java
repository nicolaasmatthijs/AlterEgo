/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.Document;
import org.cl.nm417.nlp.CCParser;
import org.xml.sax.SAXException;

/**
 * Class that will initiate the loading of a user's XML file into memory and will
 * start C&C parsing if required
 */
public class DataParser {

	private ArrayList<Document> documents;
	
	/**
	 * Load a user XML file into memory
	 * @param path		Path where the user profile can be found
	 * @param doParse	Determines whether the generated text file (all sentences in the
	 * 					browsing history) should be parsed or not using the C&C Parser
	 */
	public DataParser(String path, boolean doParse){
		
	    try {

	    	// Current user
	    	String userid = (String)AlterEgo.config.get("user");
	    	
	    	// Get a factory
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
		    
			// Parse the file and also register this class for call backs
		    XMLDataParser parser = new XMLDataParser();
		    
		    String xmlPath = "";
	    	String longpath = ""; 
	    	
	    	// Generate the paths to the parse file
		    if (doParse){
		    	xmlPath = path + "tmp2.xml";
		    	longpath = path + "cc"; 
		    } else {
		    	xmlPath = determinePath(userid);
		    	path = ConfigLoader.getConfig().getProperty("profiles");
		    	longpath = path + (String)AlterEgo.config.get("user") + ".cc";
		    }
	    	saxParser.parse(xmlPath, parser);
	    	documents = parser.getDocuments();
	    	
	    	if ((Boolean)AlterEgo.config.get("ccparse")){
	    		if (!new File(longpath + ".parsed.txt").exists() && doParse){
		    		// Parsing all sentences
	    			CCParser.CCParse(longpath);
		    	}
	    		// Add the parsed sentences into the user profile
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

	/**
	 * Determines the path of a user's XML File
	 * @param userid	The user's whose XML file we're trying to find
	 * @return			The path of the XML file
	 */
	private String determinePath(String userid) {
		String basepath = ConfigLoader.getConfig().getProperty("xmlfiles");
		String path = userid + ".xml";
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
