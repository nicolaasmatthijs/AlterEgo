/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.nlp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.Document;
import org.cl.nm417.data.Sentence;
import org.cl.nm417.data.SentenceItem;

/**
 * Class that will read the file produced by the C&C parser containing the parsed
 * text of a user's browsing history. It will then extract all noun phrases and add
 * them to the browsing history
 */
public class CCParser {

	/**
	 * Extract all noun phrases from the parsed browsing history and add them to the
	 * browsing history
	 * @param url			Path to file containing the parse output
	 * @param documents		Browsing history associated with the user
	 * @return				Browsing history associated with the user with extracted noun phrases included
	 */
	public static ArrayList<Document> readCCParse(String url, ArrayList<Document> documents){
		
		try{
			
			// Open parse file
		    FileInputStream fstream = new FileInputStream(url);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    
		    int i = 0;
		    // Split constituent tree on whitespace
		    Pattern pattern = Pattern.compile("\\s+");
		    while ((strLine = br.readLine()) != null)   {
		    	
		    	// Detect whether the document separator is detected
		    	if (strLine.equals("<c> </alteregodocument>|</alteregodocument>|NN|I-NP|O|N")){
		    		i++;
		    	// Detect a parsed sentence
		    	} else if (strLine.startsWith("<c>")){
		    		String sent = strLine.substring(4).trim();
		    		String[] split = pattern.split(sent);
		    		Sentence sentence = new Sentence();
		    		for (String s: split){
		    			SentenceItem item = new SentenceItem(s);
		    			sentence.getSentence().add(item);
		    		}
		    		ArrayList<String> allWords = new ArrayList<String>();
		    		for (SentenceItem item: sentence.getSentence()){
		    			// Check whether the current constituent is a noun
						if (item.getPOS().startsWith("NN")){
							// Remove punctuation and add to list of noun phrases
							String toAdd = item.getWord().replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
							if (!allWords.contains(toAdd)){
								allWords.add(toAdd);
							}
						}
					}
		    		documents.get(i).getParsed().add(allWords);
		    	}
		    }
		    //Close the input stream
		    in.close();
		    
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
		return documents;
		
	}

	/**
	 * Parse a text file using the C&C Parser
	 * @param longpath	Path to the input file
	 */
	public static void CCParse(String longpath) {
		try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(ConfigLoader.getConfig().getProperty("candcparser") + " " +
            		// Models directory
            		"--models " + ConfigLoader.getConfig().getProperty("candcmodels") + " " + 
            		"--input " + longpath + ".txt " + 
            		// Output will be written to path.parsed.txt
            		"--output " + longpath + ".parsed.txt " +
            		// Log output will be written to path.log.txt
            		"--log " + longpath + ".log.txt");

            // Wait until the process has finished
            int exitVal = pr.waitFor();
            System.out.println("Exited parsing with error code "+exitVal);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
	}
	
}
