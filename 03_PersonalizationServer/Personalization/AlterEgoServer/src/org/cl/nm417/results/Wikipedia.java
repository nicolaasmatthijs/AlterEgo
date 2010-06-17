/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.results;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.cl.nm417.data.Document;
import org.cl.nm417.data.htmltags.Anchor;
import org.cl.nm417.xmlparser.DataParser;

/**
 * Class that will extract statistics about Wikipedia usage from a user's
 * browsing history
 */
public class Wikipedia {

	/**
	 * Extract Wikipedia statistics
	 * @param data The user's browsing history
	 */
	public static void WikipediaStats(DataParser data){
		
		// Total wikipedia pages visited
		int pages = 0;
		// Total number of wikipedia visited that had a link to a disambiguation page
		int pagesWithDisambiguation = 0;
		// Total number of wikipedia disambiguation pages visited
		int disambiguationPages = 0;
		
		// Go through each document of the browsing history
		for (Document d: data.getDocuments()){
			String url = d.getUrl().toLowerCase().replace("://", "");
			// Is the current page a Wikipedia page
			if (url.split("/")[0].contains("wikipedia") && url.contains("/wiki/")){
				// Is the current page a disambiguation page
				if (url.contains("(disambiguation)")){
					disambiguationPages++;
					System.out.println("\tDisambiguation page: " + d.getUrl());
				} else {
					// Check all links on the page to see whether they link out to a disambiguation
					// page
					for (Anchor a: d.getAnchors()){
						try {
							if (URLDecoder.decode(a.getHref().toLowerCase(),"utf-8").contains("(disambiguation)")){
								pagesWithDisambiguation++;
								System.out.println("\tWikipedia page with disambiguation: " + d.getUrl());
								break;
							}
						} catch (UnsupportedEncodingException e) {}
					}
					System.out.println("\tWikipedia page: " + d.getUrl());
					pages++;
				}
			}
		}
		
		// Print out the results
		System.out.println("Wikipedia pages visited: " + pages);
		System.out.println("Wikipedia pages with disambiguation page visited: " + pagesWithDisambiguation);
		System.out.println("Wikipedia disambiguation pages visited: " + disambiguationPages);
		
	}
	
}
