/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Retrieve a file on the web as a text document. This class takes
 * care of everything around that. This is used for sending requests
 * to the Google REST APIs
 */
public class WebFile {
	
	// Web file content
	private String content = "";
	// URL of the web file
	private URL url;
	
	private InputStream is;
	private BufferedReader buf;
	private String line;
	
	/**
	 * Retrieve a new file from the web
	 * @param surl URL of the web file to be retrieved
	 */
	public WebFile(String surl){
		
		try {
			// Open the URL
			url = new URL(surl);
		    is = url.openStream();  // throws an IOException
		    buf = new BufferedReader(new InputStreamReader(is));
		    // Read the retrieved file line by line
		    while ((line = buf.readLine()) != null) {
		    	setContent(getContent() + line);
		    }
		} catch (MalformedURLException mue) {
		     mue.printStackTrace();
		} catch (IOException ioe) {
		     ioe.printStackTrace();
		} finally {
		    try {
		        is.close();
		    } catch (IOException ioe) {}
		}
		
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
}
