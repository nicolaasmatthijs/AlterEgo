/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.google;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.Document;
import org.cl.nm417.data.GoogleResult;
import org.cl.nm417.util.WebFile;
import org.cl.nm417.xmlparser.DataParser;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;
import org.json.JSONObject;

/**
 * Helper class used to improve performance of the Google search. As 
 * only 8 results can be retrieved from Google at a time, it was
 * necessary to do these 7 request in parallel.
 */
class RunnableThread implements Runnable {

	// Search query
	String query = "";
	// Rank of the first result for this Google  search
	int start;
	// Retrieved results
	public ArrayList<GoogleResult> results;
	
	Thread runner;
	
	public RunnableThread(String threadName, String q, int s) {
		query = q;
		start = s;
		runner = new Thread(this, threadName); // (1) Create a new thread.
		runner.start(); // (2) Start the thread.
	}
	
	public void run() {
		results = GoogleSearch.doRestSearch(query, start);
	}
}

/**
 * Class that will retrieve Google search results for a given query
 * using the Google REST API.
 */
public class GoogleSearch {

	/**
	 * Retrieve a full 50 result Google ranking for a given query
	 * @param query		Search query
	 * @return			List of 50 search result in the order of the Google ranking
	 */
	public static ArrayList<GoogleResult> doGoogleSearch(String query){
		
		ArrayList<GoogleResult> results = new ArrayList<GoogleResult>();
		Long start = new Date().getTime();
		
		// Create separate thread for each of the Google searches
		Random r = new Random();
		RunnableThread thr0 = new RunnableThread("searchthread" + r.nextInt(10000), query, 0);
		RunnableThread thr1 = new RunnableThread("searchthread" + r.nextInt(10000), query, 8);
		RunnableThread thr2 = new RunnableThread("searchthread" + r.nextInt(10000), query, 16);
		RunnableThread thr3 = new RunnableThread("searchthread" + r.nextInt(10000), query, 24);
		RunnableThread thr4 = new RunnableThread("searchthread" + r.nextInt(10000), query, 32);
		RunnableThread thr5 = new RunnableThread("searchthread" + r.nextInt(10000), query, 40);
		RunnableThread thr6 = new RunnableThread("searchthread" + r.nextInt(10000), query, 48);
		
		// Wait until all of the threads have received their results
		boolean doRet = false;
		do {
			try {
			Thread.sleep(50);
			} catch (Exception ex){}
			boolean intermed = true;
			if (thr0.results == null){
				intermed = false;
			} else if (thr1.results == null){
				intermed = false;
			} else if (thr2.results == null){
				intermed = false;
			} else if (thr3.results == null){
				intermed = false;
			} else if (thr4.results == null){
				intermed = false;
			} else if (thr5.results == null){
				intermed = false;
			} else if (thr6.results == null){
				intermed = false;
			}
			doRet = intermed;
		} while (doRet == false);
		
		// Add the received results to the overall ranking
		results.addAll(thr0.results);
		results.addAll(thr1.results);
		results.addAll(thr2.results);
		results.addAll(thr3.results);
		results.addAll(thr4.results);
		results.addAll(thr5.results);
		results.addAll(thr6.results);
		
		Long end = new Date().getTime();
		System.out.println("Got results in " + (end - start) + " ms");
		
		return results;
		
	}
	
	/**
	 * Use the Google Search REST API to retrieve Google search results
	 * @param query		Search query
	 * @param start		Index of the first result to be retrieved (1 for page 1, 10 for page 2, ...)
	 * @return			Retrieved Google results
	 */
	public static ArrayList<GoogleResult> doRestSearch(String query, int start){
		ArrayList<GoogleResult> results = new ArrayList<GoogleResult>();
		
		WebFile file;
		try {
			// Google REST API
			file = new WebFile( "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(query, "UTF-8") + "&start=" + start + "&rsz=large" );
			String json = (String) file.getContent();
			// The retrieved file is in JSON format. We parse it into a JSON Object
			JSONObject object = new JSONObject(json).getJSONObject("responseData");
			for (int i = 0; i < object.getJSONArray("results").length(); i++){				
				JSONObject obj = object.getJSONArray("results").getJSONObject(i);
				GoogleResult result = new GoogleResult();
				// Extract the necessary data
				result.setRank(start + 1 + i);
				result.setOriginalRank(start + 1 + i);
				result.setUrl(obj.getString("url"));
				result.setTitle(obj.getString("titleNoFormatting"));
				result.setSummary(new String(obj.getString("content").getBytes(),"utf-8").replaceAll("&#39;","'").replaceAll("<b>", "").replaceAll("</b>", "").replaceAll("&amp;","&").replaceAll("&quot;", "\"").replaceAll("&middot;", ""));
				results.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Get a list of Google searches done by a user based on his browsing history
	 * @param data		User's browsing history
	 */
	public static void getGoogleSearches(DataParser data) {
		for (Document d: data.getDocuments()){
			if (d.getUrl().toLowerCase().contains("google.") && d.getUrl().toLowerCase().contains("/search?")){
				System.out.println(d.getUrl());
			}
		}
	}

	/**
	 * Get the number of occurences on the internet for a set of terms 
	 * using the Google N-Gram corpus
	 * @param words			List of words of which we want the frequency count
	 * @return				Map of terms and the number of occurences
	 */
	public static HashMap<String, Double> getNumberOfResults(ArrayList<String> words) {
		
		try {
			long start = new Date().getTime();
			int i = 0;
			HashMap<String, Double> freq = new HashMap<String, Double>();
			JRedis jredis = new JRedisClient(ConfigLoader.getConfig().getProperty("redisserver"), Integer.parseInt(ConfigLoader.getConfig().getProperty("redisport")));
			// Attempt to extract the frequency count for each of the term
			for (String s: words){
				if (s != null && s.length() > 0){
					if (!freq.containsKey(s.toLowerCase())){
						if (jredis.exists(s.toLowerCase())){
							try {
								freq.put(s.toLowerCase(), Double.parseDouble(new String(jredis.get(s.toLowerCase()))));
							} catch (Exception ex){
								// Don't save a frequency count if the word isn't in the corpus
							}
						}
					}
				} else {
					System.out.println("Encountered null");
				}
				i++;
				// Progress update
				if (i % 1000 == 0){
					System.out.println("NGramming " + i + " of " + words.size());
				}
			}
			long end = new Date().getTime();
			System.out.println("Lookup of " + freq.size() + " in " + ((end - start) / 1000) + " seconds");
			return freq;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;

	}
	
}
