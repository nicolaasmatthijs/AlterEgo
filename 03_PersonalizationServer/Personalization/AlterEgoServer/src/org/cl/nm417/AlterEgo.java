/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.Document;
import org.cl.nm417.data.GoogleResult;
import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.google.GoogleRerank;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.profile.UserProfile;
import org.cl.nm417.util.Logging;
import org.cl.nm417.xmlparser.DataParser;

/**
 * Main class used for user profile related tasks like generation, writing
 * the profile to a file, reading a profile, ...
 */
public class AlterEgo {
	
	// Global configuration object
	public static HashMap<String, Object> config = new HashMap<String, Object>();
	
	/**
	 * Generate a user profile given a set of user profile generation parameters
	 * @param params	Given user profile parameters
	 * @param data		User's browsing history
	 * @return			Generated user profile
	 */
	public static Profile generateProfile(HashMap<String, Object> params, DataParser data) {
		
		double start = new Date().getTime();
		config = params;
		
		// Generate the profile given the parameters
		Profile profile = processUser((String)config.get("user"), data);
		
		// Take log of the weights if that option is checked
		if ((Boolean)config.get("takeLog")){
			for (Unigram u: profile.getUnigrams()){
				u.setWeight(Math.log(u.getWeight() + 1));
			}
		}
		
		// Write the profile to a file
		writeProfile(profile);
		
		double end = new Date().getTime();
		System.out.println("User profile generated in " + ((end - start) / 1000) + " seconds");
		return profile;
		
	}
	
	/**
	 * Do a Google search and re-rank the result using an existing profile
	 * @param query				Search query
	 * @param user				User's unique identifier
	 * @param profilename		Name of the profile file that should be used for re-ranking
	 * @param rerank			Whether re-ranking should be attempted at all
	 * @param method			Re-ranking method (Matching, UMatching, LM or PClick)
	 * @param interleave		Whether the new ranking should be interleaved with the original one
	 * @param interleaveMethod	Interleaving algorithm (TeamDraft or Balanced)
	 * @param lookatrank		Whether the Google rank should be taken into account
	 * @param umatching			Whether Unique matching should be used if matching is used
	 * @param visited			Give extra weight to previously visited URLs
	 * @param visitedW			Previously visited URLs will be multiplied by this weight 
	 * @param log				Whether the log of the term weights should be taken
	 * @return					List of re-ranked Google search results
	 * @throws Exception		Throws an error if something goes wrong, which is 
	 * 							caught by the Firefox add-on 
	 */
	public static ArrayList<GoogleResult> SearchGoogle(String query, String user, String profilename, 
			boolean rerank, String method, boolean interleave, String interleaveMethod, 
			boolean lookatrank, boolean umatching, boolean visited, int visitedW, Logging log) throws Exception{
		Profile profile = new Profile();
		profile.setUserId(user);
		// Read and load the user profile
		profile.setUnigrams(readFinalUnigrams(user + "/" + profilename));
		// Load the user's URLs
		profile.setURLs(GoogleRerank.getURLs(user));
		// Do a Google search for the query
		ArrayList<GoogleResult> results = GoogleSearch.doGoogleSearch(query);
		
		// Rerank the results using the provided profile
		results = Rerank(profile, query, results, true, method, interleave,  interleaveMethod, lookatrank, visited, visitedW, new Logging());
		
		profile = null;
		return results;
	}
	
	/**
	 * Re-rank the results of an exisiting Google search using a given profile
	 * @param query					Search query
	 * @param profile				User's generated profile
	 * @param method				Re-ranking method (Matching, UMatching, LM or PClick)
	 * @param interleave			Whether the new ranking should be interleaved with the original one
	 * @param interleaveMethod		Interleaving algorithm (TeamDraft or Balanced)
	 * @param lookatrank			Whether the Google rank should be taken into account
	 * @param visited				Give extra weight to previously visited URLs
	 * @param visitedW				Previously visited URLs will be multiplied by this weight
	 * @param fResults
	 * @return
	 */
	public static ArrayList<GoogleResult> SearchGoogle(String query, Profile profile,
		String method, boolean interleave, String interleaveMethod, 
		boolean lookatrank, boolean visited, int visitedW, ArrayList<GoogleResult> fResults){
			ArrayList<GoogleResult> results = new ArrayList<GoogleResult>();
			for (GoogleResult res: fResults){
				res.setWeight(0);
				results.add(res);
			}
			// Sort the search results on snippet score
			results = GoogleRerank.doSort(results);
			// Rerank the results using the provided profile
			results = Rerank(profile, query, results, true, method, interleave,  interleaveMethod, lookatrank, visited, visitedW, new Logging());
			return results;
	}
	
	/**
	 * Re-rank the results of an exisiting Google search using a given profile
	 * @param profile				User's profile
	 * @param query					Search query
	 * @param results				Provided search results
	 * @param rerank				Whether re-ranking should be attempted at all
	 * @param method				Re-ranking method (Matching, UMatching, LM or PClick)
	 * @param interleave			Whether the new ranking should be interleaved with the original one
	 * @param interleaveMethod		Interleaving algorithm (TeamDraft or Balanced)
	 * @param lookatrank			Whether the Google rank should be taken into account
	 * @param visited				Give extra weight to previously visited URLs
	 * @param visitedW				Previously visited URLs will be multiplied by this weight 
	 * @param log					Whether the log of the term weights should be taken
	 * @return
	 */
	private static ArrayList<GoogleResult> Rerank(Profile profile, String query, ArrayList<GoogleResult> results, 
			boolean rerank, String method, boolean interleave,  String interleaveMethod, boolean lookatrank, 
			boolean visited, int visitedW, Logging log){
		HashMap<String, Unigram> unigrams = new HashMap<String, Unigram>();
		for (Unigram u: profile.getUnigrams()){
			unigrams.put(u.getText().toLowerCase(), u);
		}
		if (rerank){
			// Language Model method
			if (method.equals("lm")){
				// Generate Language Model
				double totalWords = calculateTotalWords(profile);
				profile = calculateLMStatistics(profile, totalWords);
				results = GoogleRerank.applyLM(profile, query, unigrams,results, interleave, interleaveMethod, lookatrank, totalWords, visited, visitedW, new Logging());
			// PClick method
			} else if (method.equals("pclick")){
				results = GoogleRerank.pClick(query, profile, results, visited, visitedW);
			// Unique Matching method
			} else if (method.equals("umatching")) {
				results = GoogleRerank.findCommonalities(profile, query, results, interleave, interleaveMethod, lookatrank, true, visited, visitedW, new Logging());
			// Matching method
			} else {
				results = GoogleRerank.findCommonalities(profile, query, results, interleave, interleaveMethod, lookatrank, false, visited, visitedW, new Logging());
			}
		}
		return results;
	}

	/**
	 * Returns the sum of all term weights in a profile. 
	 */
	private static double calculateTotalWords(Profile profile){
		Long start = new Date().getTime();
		double totalWords = 0;
		for (Unigram u: profile.getUnigrams()){
			totalWords += u.getWeight();
		}
		Long end = new Date().getTime();
		System.out.println("Got total words in " + (end - start) + " ms");
		return totalWords;
	}
	
	/**
	 * This function will re-calculate the weights of user terms by converting them into
	 * a language model. The weights are used as the frequency counts and Add1 smoothing
	 * is applied
	 * @param profile		User's profile
	 * @param totalWords	Sum of all term weights
	 * @return				new user profile with Language Model weights
	 */
	private static Profile calculateLMStatistics(Profile profile, double totalWords) {
		Long start = new Date().getTime();
		Profile newProf = new Profile();
		// Copy profile values into new profile
		newProf.setURLs(profile.getURLs());
		newProf.setUserId(profile.getUserId());
		newProf.setDocuments(profile.getDocuments());
		newProf.setUnigrams(new ArrayList<Unigram>());
		for (Unigram u: profile.getUnigrams()){
			Unigram newU = new Unigram();
			// Add1 smoothing by adding 1 
			newU.setWeight((1 + u.getWeight()) / totalWords);
			newU.setText(u.getText());
			newProf.getUnigrams().add(newU);
		}
		Long end = new Date().getTime();
		System.out.println("Got LM Statistics in " + (end - start) + " ms");
		return newProf;
	}

	/**
	 * Read a user profile and store the terms and weights into memory
	 * @param path	Path to the user profile
	 * @return		An object containing the user profile's terms and associated weights	
	 */
	public static ArrayList<Unigram> readFinalUnigrams(String path){
		
		Long start = new Date().getTime();
		ArrayList<Unigram> unigrams = new ArrayList<Unigram>();
		DataInputStream in = null;
		try{
			// Split on this delimiter to split term from associated weight
			Pattern pattern = Pattern.compile("=>");
			// Open the profile file
		    FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("profiles") + path);
		    in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		      if (!strLine.equals("")){
		    	  // Create a unigram object for every term in the profile
		    	  Unigram u = new Unigram();
		    	  String[] spl = pattern.split(strLine);
		    	  // Term weight
		    	  u.setWeight(Double.parseDouble(spl[0].trim()));
		    	  // Term content
		    	  u.setText(spl[1].trim());
		    	  unigrams.add(u);
		      }
		    }
		}catch (Exception e){
			System.err.println(e.getMessage());
		} finally {
			 //Close the input stream
			if (in != null){
			    try {
					in.close();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
		Long end = new Date().getTime();
		System.out.println("Got profile in " + (end - start) + " ms");
		return unigrams;
	}
	
	/**
	 * Write a generated user profile to a user profile file
	 * @param profile	Generated user profile
	 */
	private static void writeProfile(Profile profile){
		
		try {
			
			// Generate the filename according to the parameter configuration
			String extention = "";
			
			// Weighting
			String weighting = (String)config.get("weighting");
			if (weighting.equals("tf")){
				extention += "_t";
			} else if (weighting.equals("tfidf")){
				extention += "_ti";
			} else if (weighting.equals("bm25")){
				extention += "_b";
			}
			
			// Title
			boolean useRelative = (Boolean)config.get("useRelativeW");
			boolean title = (Boolean)config.get("title");
			if (title && useRelative){
				extention += "_r";
			} else if (title){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Meta description
			boolean md = (Boolean)config.get("metadescription");
			if (md && useRelative){
				extention += "_r";
			} else if (md){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Meta keywords
			boolean mk = (Boolean)config.get("metakeywords");
			if (mk && useRelative){
				extention += "_r";
			} else if (mk){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Plain text
			boolean pt = (Boolean)config.get("plaintext");
			if (pt && useRelative){
				extention += "_r";
			} else if (pt){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Terms
			boolean t = (Boolean)config.get("terms");
			if (t && useRelative){
				extention += "_r";
			} else if (t){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// C&C Parsed
			boolean cc = (Boolean)config.get("ccparse");
			if (cc && useRelative){
				extention += "_r";
			} else if (cc){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Filtering
			boolean allPos = (Boolean)config.get("posAll");
			boolean nGram = (Boolean)config.get("googlengram");
			boolean posNoun = (Boolean)config.get("posNoun");
			if (nGram){
				extention += "_g";
			} else if (posNoun){
				extention += "_wn";
			} else if (allPos){
				extention += "_n";
			}
			
			// Exclude duplicate pages
			boolean excludeDuplicate = (Boolean)config.get("excludeDuplicate");
			if (excludeDuplicate){
				extention += "_y";
			} else {
				extention += "_n";
			}
			
			// Write the profile in the following format
			// 	term1 => weight1
			//  term2 => weight2
		    FileWriter fstream = new FileWriter(ConfigLoader.getConfig().getProperty("profiles") + profile.getUserId() + "/" + profile.getUserId() + extention + ".txt");
		    BufferedWriter out = new BufferedWriter(fstream);
		    for (Unigram u: profile.getUnigrams()){
		    	out.write(u.getWeight() + " => " + u.getText() + "\n");
		    }
		    out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	/**
	 * Load a user's browsing history into memory
	 * @param userid	User's unique identifier
	 * @return			User's browsing history
	 */
	public static DataParser getDataParser(String userid){
		System.out.println(userid);
		DataParser data = new DataParser(userid, false);
		return data;
	}
	
	/**
	 * Generate a full user profile (terms & weight, URLs and PClick)
	 * @param userid	User's unique identifier
	 * @param data		User's browsing history
	 * @return			Generated profile
	 */
	private static Profile processUser(String userid, DataParser data){
		
		if (data == null){
			data = new DataParser(userid, false);
		}
		Profile profile = new Profile();
		profile.setUserId(userid);
		// Use the personalization parameters stored in the config object
		profile.setUnigrams(UserProfile.extractUnigramProfile(data, (Boolean)config.get("plaintext"), 
				(Boolean)config.get("metakeywords"), (Boolean)config.get("metadescription"), 
				(Boolean)config.get("title"), (Boolean)config.get("terms"), (Boolean)config.get("ccparse"),
				(Integer)config.get("plaintextW"), (Integer)config.get("metakeywordsW"), 
				(Integer)config.get("metadescriptionW"), (Integer)config.get("titleW"), (Integer)config.get("termsW"), 
				(Integer)config.get("ccparseW")));
		if (((String)config.get("weighting")).equals("bm25")){
			profile.setUnigrams(UserProfile.extractBM25Profile(data, profile, (Boolean)config.get("plaintext"), 
					(Boolean)config.get("metakeywords"), (Boolean)config.get("metadescription"), 
					(Boolean)config.get("title"), (Boolean)config.get("terms"), (Boolean)config.get("ccparse")));
		}
		// Write URL file
		writeURLs(userid, data);
		// Write PClick file
		writePClickData(userid, data);
		profile.setDocuments(data.getDocuments().size());
		data = null;
		return profile;
		
	}
	
	/**
	 * Save a list of all URLs visited by a user to a file. This is later on used to give
	 * additional weight to previously visited URLs
	 * @param userid	User's unique identifier
	 * @param data		USer's browsing history
	 */
	private static void writeURLs(String userid, DataParser data) {
		 HashMap<String, Integer> url = new HashMap<String, Integer>();
		 try {
			 FileWriter fstream = new FileWriter(ConfigLoader.getConfig().getProperty("profiles") + userid + ".url.txt");
			 BufferedWriter out = new BufferedWriter(fstream);
			 for (Document d: data.getDocuments()){
				 if (!url.containsKey(d.getUrl())){
					 url.put(d.getUrl(), 1);
				 } else {
					 url.put(d.getUrl(), url.get(d.getUrl()) + 1);
				 }
			 }
			 // The URL file has the following format
			 //		http://www.example.com => 3
			 for (String s: url.keySet()){
				out.write(s + " => " + url.get(s) + "\n");
			 }
			 out.close();
		 } catch (Exception e){
			 System.err.println("Error: " + e.getMessage());
		 }
	}

	/**
	 * Save a user's PClick profile to a file. A PClick profile consists of all
	 * search queries a user has done and the pages clicked for those queries
	 * @param userid	User's unique identifier
	 * @param data		User's browsing history
	 */
	private static void writePClickData(String userid, DataParser data){
		HashMap<String, ArrayList<String>> searches = new HashMap<String, ArrayList<String>>();
		boolean trackNext = false;
    	String prevSearch = "";
    	try {
    		FileWriter fstream = new FileWriter(ConfigLoader.getConfig().getProperty("profiles") + userid + ".pclick.txt");
			BufferedWriter out = new BufferedWriter(fstream);
	    	for (Document d: data.getDocuments()){
	    		String url = d.getUrl().toLowerCase();
	    		// Detecting a Google search happens by looking at the page URL
	    		if (url.contains("www.google") && url.contains("q=")){
	    			String search = url.substring(url.indexOf("q=") + 2);
	    			int index = search.indexOf("&");
	    			if (index == -1){
	    				search = URLDecoder.decode(search.replaceAll("[+]", " "),"utf-8");
	    			} else {
	    				search = URLDecoder.decode(search.substring(0, index).replaceAll("[+]", " "),"utf-8");
	    			}
	    			prevSearch = search;
	    			trackNext = true;
	    			if (!searches.containsKey(search)){
	    				searches.put(search, new ArrayList<String>());
	    			} 
	    		// If the previous page in the browsing history was a search page, we assume the next
	    		// one will be the result clicked by the user
	    		} else if (trackNext){
	    			trackNext = false;
	    			ArrayList<String> arl = searches.get(prevSearch);
	    			arl.add(d.getUrl());
	    			searches.put(prevSearch, arl);
	    		}
	    	}
	    	
	    	// Write each of the search queries and links clicked for these queries 
	    	// to a PClick profile file
	    	for (String s: searches.keySet()){
	    		if (searches.get(s).size() > 0){
		    		out.write(s + "\n");
		    		for (String url: searches.get(s)){
		    			out.write("=> " + url + "\n");
		    		}
	    		}
	    	}
	    	
	    	out.close();
	    	
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}
	
}
