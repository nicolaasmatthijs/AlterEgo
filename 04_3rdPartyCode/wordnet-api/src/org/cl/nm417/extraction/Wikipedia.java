package org.cl.nm417.extraction;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.cl.nm417.data.Anchor;
import org.cl.nm417.data.Document;
import org.cl.nm417.xmlparser.DataParser;

public class Wikipedia {

	public static void WikipediaStats(DataParser data){
		
		int pages = 0;
		int pagesWithDisambiguation = 0;
		int disambiguationPages = 0;
		
		for (Document d: data.getDocuments()){
			String url = d.getUrl().toLowerCase().replace("://", "");
			if (url.split("/")[0].contains("wikipedia") && url.contains("/wiki/")){
				if (url.contains("(disambiguation)")){
					disambiguationPages++;
					System.out.println("\tDisambiguation page: " + d.getUrl());
				} else {
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
		
		System.out.println("Wikipedia pages visited: " + pages);
		System.out.println("Wikipedia pages with disambiguation page visited: " + pagesWithDisambiguation);
		System.out.println("Wikipedia disambiguation pages visited: " + disambiguationPages);
		
	}
	
}
