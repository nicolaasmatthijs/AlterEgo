/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.nlp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.cl.nm417.config.ConfigLoader;
import org.json.JSONArray;

/**
 * Class that will call the Term Extraction script. This script is a re-implementation
 * of the Vu Term Extraction algorithm
 */
public class TermExtraction {

	/**
	 * Extract terms from a text file
	 * @param content	Content of the file on which Term Extraction is done
	 * @return			JSON Array object containing all extracted terms
	 */
	public static JSONArray extractTerms(String content){
		
		String line;
		String output = "";
		
		try {
			
		    // Construct data. The script expects a context parameter
		    String data = URLEncoder.encode("context", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
		    
		    // Send data
		    URL url = new URL(ConfigLoader.getConfig().getProperty("termextrurl"));
		    URLConnection conn = url.openConnection();
		    // We're expecting output to be sent back
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    while ((line = rd.readLine()) != null) {
		    	output += line;
		    }
		    wr.close();
		    rd.close();
		    
		    // Transform the returned string into a JSON Array
		    return new JSONArray(output);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONArray();
		
	}
	
}
