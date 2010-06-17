/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.google;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.GoogleResult;
import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.util.Logging;

/**
 * Class responsible for everything around Google result re-ranking using the
 * various supported methods
 */
public class GoogleRerank {

	/**
	 * Re-rank search result given a profile using the Matching or Unique Matching method
	 * @param profile			User's profile
	 * @param query				Search query
	 * @param results			Retrieved Google search results
	 * @param interleave		Whether the results should be interleaved or not
	 * @param interleaveMethod	Which interleaving algorithm to use (TeamDraft or Balanced)
	 * @param lookAtGoogleRank  Whether the Google rank should be taken into account or not
	 * @param uniqueMatch		Whether Unique Matching should be used or not (otherwise Matching will be used)
	 * @param visited			Whether extra weight should be given to previously visited pages
	 * @param visitedW			The snippet score will be multiplied by this for previously visited pages
	 * @param log				Log file
	 * @return					Re-ranked search results
	 */
	public static ArrayList<GoogleResult> findCommonalities(Profile profile, String query, ArrayList<GoogleResult> results,
			boolean interleave, String interleaveMethod, boolean lookAtGoogleRank, boolean uniqueMatch,
			boolean visited, int visitedW, Logging log) {
		
		ArrayList<GoogleResult> oldResults = new ArrayList<GoogleResult>();
		for (GoogleResult r: results){
			r.setWeight(0);
			oldResults.add(r);
		}
		
		// Split the Google snippet on whitespace to get a list of terms
		// associated with the search result
		Pattern p = Pattern.compile("\\s+");
		
		HashMap<String, Unigram> hprofile = new HashMap<String, Unigram>();
		for (Unigram uni: profile.getUnigrams()){
			hprofile.put(uni.getText().toLowerCase(), uni);
		}
		
		for (GoogleResult result: results){
			String resultText = (result.getTitle() + " " + result.getSummary()).replaceAll("[.,-/\"':;?()><=ÐÈ|_]", "").toLowerCase();
			ArrayList<String> arlCommon = new ArrayList<String>();
			int common = 0;
			// Check whether this word is part of the user profile
			for (String s: p.split(resultText)){
				if (hprofile.containsKey(s) && !arlCommon.contains(s)){
					// Prevent a match being used several times if unique
					// matching is required
					if (uniqueMatch){
						arlCommon.add(s);
					}
					// Add the term's weight to the overall snippet score
					result.setWeight(result.getWeight() + hprofile.get(s).getWeight());
					common++;
				}
			}
			
			// Normalize by the Google rank to make re-ranking less agressive
			if (lookAtGoogleRank){
				result.setWeight(result.getWeight() / Math.log1p(1 + result.getRank()));
			}

		}
		
		// Re-rank the results based on snippet score
		results = doSort(results);
		
		// Give extra weight to previously visited URLs
		results = clickBased(results, visited, visitedW, profile);
		
		// Interleave new ranking with original one
		if (interleave){
			if (interleaveMethod.equals("balanced")){
				results = balancedInterleaving(oldResults, results, query, profile.getUserId(), log);
			} else if (interleaveMethod.equals("teamdraft")){
				results = teamdraftInterleaving(oldResults, results, query, profile.getUserId(), log);
			}
		}
		
		// Update the result ranks
		for (int i = 0; i < results.size(); i++){
			results.get(i).setRank(i + 1);
		}
		
		return results;
			
	}
	
	/**
	 * Give more weight to previously visited URLs
	 * @param results			Current result ranking
	 * @param visited			Whether extra weight should be given to previously visited pages
	 * @param visitedW			The snippet score will be multiplied by this for previously visited pages
	 * @param profile			User's profile
	 * @return					New ranking after giving extra weight to previously visited URLs
	 */
	private static ArrayList<GoogleResult> clickBased(ArrayList<GoogleResult> results, boolean visited, int visitedW, Profile profile){
		if (visited){
			// Get URLs visited by the current user
			HashMap<String, Integer> arlVisited = profile.getURLs();
			for (GoogleResult result: results){
				// If the current result URL has been visited before
				if (arlVisited.containsKey(result.getUrl().toLowerCase())){
					// Multiply the snippets score by the weight and the number of previous visits
					result.setWeight((result.getWeight() + 1) + visitedW * arlVisited.get(result.getUrl().toLowerCase()));
					System.out.println(result.getUrl() + " => " + arlVisited.get(result.getUrl().toLowerCase()));
				}
			}
			results = GoogleRerank.doSort(results);
		}
		return results;
	}
	
	/**
	 * Read and store the list of URLs a user has visited in his browsing history and how
	 * often each of these pages were visited
	 * @param user			User's unique identifier
	 * @return				URLs the user has visited and associated number of visits
	 * @throws Exception	Throws exception if the user profile does not exist
	 */
	public static HashMap<String, Integer> getURLs(String user) throws Exception {
		Long start = new Date().getTime();
		HashMap<String, Integer> arlVisited = new HashMap<String, Integer>();
		DataInputStream in = null;
		// Delimiter used to sepate URL and number of visits
		Pattern pattern = Pattern.compile(" => ");
		try{
			// Read the user profile URL file
		    FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("profiles") + user + ".url.txt");
		    in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
		      if (!strLine.equals("")){
		    	  // Split the current line in order to separate the URL from
		    	  // the number of visits. The format is the following:
		    	  //	http://www.very.co.uk/powerReview/reviewListing.jsp?productId=prod2590603&rand=1269447562859 => 1
		    	  //	http://gateservice3.dcs.shef.ac.uk/moodle/mod/quiz/attempt.php?q=2&page=9 => 4
		    	  //	...
		    	  String[] split = pattern.split(strLine.toLowerCase());
		    	  arlVisited.put(split[0],Integer.parseInt(split[1]));
		      }
		    }
		    br.close();
		    in.close();
		    fstream.close();
		}catch (Exception e){
			// The profile does not exist
			System.out.println("Throwing an error");
			throw new Exception("Profile was not found");
		}finally {
			// Close the inputstream
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
		Long end = new Date().getTime();
		System.out.println("Obtained URLs in " + (end - start) + " ms");
		return arlVisited;
	}
	
	/**
	 * Re-rank the search results based on their snippet score
	 * @param results	Search results to sort
	 * @return			Sorted search results
	 */
	public static ArrayList<GoogleResult> doSort(ArrayList<GoogleResult> results){
		Collections.sort(results, new Comparator<GoogleResult>(){
			 
			public int compare(GoogleResult g1, GoogleResult g2) {
				if (g1.getWeight() > g2.getWeight()){
	            	return -1;
	            } else if (g1.getWeight() == g2.getWeight()){
	            	// Use the original Google rank if two snippets
	            	// have an equal weight
	            	if (g1.getRank() < g2.getRank()){
	            		return -1;
	            	} else {
	            		return 1;
	            	}
	            } else {
	            	return 1;
	            }
	        }
	 
		});
		return results;
	}
	
	/**
	 * Re-rank the search results based on their relevance score
	 * @param results	Search results to sort
	 * @return			Sorted search results
	 */
	public static ArrayList<GoogleResult> doSortRelevance(ArrayList<GoogleResult> results){
		Collections.sort(results, new Comparator<GoogleResult>(){
			 
			// This is used to come up with an ideal ranking during NDCG score calculation
			public int compare(GoogleResult g1, GoogleResult g2) {
				if (g1.getRelevance() > g2.getRelevance()){
	            	return -1;
	            } else if (g1.getRelevance() == g2.getRelevance()) {
	            	return 0;
	            } else {
	            	return 1;
	            }
	        }
	 
		});
		return results;
	}

	/**
	 * Re-rank search results using Language Model snippet scoring
	 * @param profile			User's profile
	 * @param query				Search query	
	 * @param unigrams			List of unigrams in the user's profile
	 * @param results			Google search results
	 * @param interleave		Whether the results should be interleaved or not
	 * @param interleaveMethod	Which interleaving algorithm to use (TeamDraft or Balanced)
	 * @param lookatrank		Whether the Google rank should be taken into account
	 * @param totalResults		Sum of all term weights in the profile
	 * @param visited			Whether extra weight should be given to previously visited pages
	 * @param visitedW			The snippet score will be multiplied by this for previously visited pages
	 * @param log				Log file
	 * @return					Re-ranked search results
	 */
	public static ArrayList<GoogleResult> applyLM(Profile profile, String query, HashMap<String, Unigram> unigrams, ArrayList<GoogleResult> results, 
			boolean interleave, String interleaveMethod, boolean lookatrank, double totalResults,
			boolean visited, int visitedW, Logging log) {
		
		Long start = new Date().getTime();
		
		Pattern p = Pattern.compile("\\s+");
		
		ArrayList<GoogleResult> oldResults = new ArrayList<GoogleResult>();
		for (GoogleResult r: results){
			oldResults.add(r);
		}
		
		// Calculate the Language Model probability of the current snippet
		for (GoogleResult result: results){
			double weight = 0.0;
			double length = 0.0;
			String res = (result.getSummary() + " " + result.getTitle()).replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
			for (String s: p.split(res)){
				// Use the sum of the logs instead of the log of the products
				weight += getWeight(unigrams, s, totalResults);
				length++;
			}
			result.setWeight(weight / length);
			if (lookatrank){
				result.setWeight(result.getWeight() / Math.log1p(1 + result.getRank()));
			}
			
		}
		// Re-rank results
		results = doSort(results);
		// Give extra weight to visited URLs
		results = clickBased(results, visited, visitedW, profile);
		
		// Do interleaving if required
		if (interleave){
			if (interleaveMethod.equals("balanced")){
				results = balancedInterleaving(oldResults, results, query, profile.getUserId(), log);
			} else if (interleaveMethod.equals("teamdraft")){
				results = teamdraftInterleaving(oldResults, results, query, profile.getUserId(), log);
			}
		}
		
		Long end = new Date().getTime();
		System.out.println("Got apply LM in " + (end - start) + " ms");
		
		return results;
	}
	
	/**
	 * Find the next search result in a list of search results that hasn't been used yet
	 * in the provided interleaved ranking
	 * @param results		Search result ranking
	 * @param interleaved	The current interleaved ranking
	 * @return				The rank of the first unused result
	 */
	private static int getFirstNewElement(ArrayList<GoogleResult> results, ArrayList<GoogleResult> interleaved){
		int rank = 0;
		for (GoogleResult res: results){
			boolean isNew = true;
			// Check whether this result has been used in the interleaved ranking
			for (GoogleResult nres: interleaved){
				if (nres.getOriginalRank() == res.getOriginalRank()){
					isNew = false;
				}
			}
			if (isNew){
				return rank;
			}
			rank++;
		}
		// All results have been used in the interleaved ranking
		return -1;
	}
	
	/**
	 * Implementation of the Team Draft interleaving algorithm that will interleave the original
	 * Google ranking with the new re-ranked version
	 * @param oldResults	Original Google ranking	
	 * @param results		New Google ranking
	 * @param query			Search query
	 * @param user			User's unique identifier
	 * @param log			Log file
	 * @return				New interleaved ranking
	 */
	private static ArrayList<GoogleResult> teamdraftInterleaving(ArrayList<GoogleResult> oldResults, ArrayList<GoogleResult> results, String query, String user, Logging log) {
		ArrayList<GoogleResult> interleaved = new ArrayList<GoogleResult>();
		ArrayList<GoogleResult> teamA = new ArrayList<GoogleResult>();
		ArrayList<GoogleResult> teamB = new ArrayList<GoogleResult>();
		
		// Seed the random with the current user, the current query and the
		// current hour in order not to get different results when the user
		// reloads the page
		Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.MILLISECOND, 0);
		long seed = (long) rightNow.getTime().getTime();
		for (int i = 0; i < query.length(); i++){
			seed += (Math.pow((long) query.charAt(i), 3));
		}
		for (int i = 0; i < user.length(); i++){
			seed += (Math.pow((long) user.charAt(i), 3));
		}
		Random r = new Random(seed);
		
		boolean isNewInA = true; boolean isNewInB = true;
		int randomno = 0;
		do {
			int AFirst = r.nextInt(2);
			// If Team B has more elements or the random bit is 1
			if (teamA.size() < teamB.size() || (teamA.size() == teamB.size() && AFirst == 1)){
				if ((teamA.size() == teamB.size() && AFirst == 1)){
					randomno++;
					log.appendLog("\tRandom " + randomno + ": " + AFirst);
				}
				// Get next unused result from the original ranking
				int k = getFirstNewElement(oldResults, interleaved);
				GoogleResult res = oldResults.get(k);
				res.setTeam("Original");
				// Add it to the interleaved ranking
				interleaved.add(res);
				teamA.add(res);
			} else {
				if (teamA.size() == teamB.size()){
					randomno++;
					log.appendLog("\tRandom " + randomno + ": " + AFirst);
				}
				// Get next unused result from the re-ranked ranking
				int k = getFirstNewElement(results, interleaved);
				GoogleResult res = results.get(k);
				res.setTeam("Reranked");
				// Add it to the interleaved ranking
				interleaved.add(res);
				teamB.add(res);
			}
			
			// Check whether both the original ranking and the re-ranked 
			// ranking still have unused results
			isNewInA = false; isNewInB = false;
			if (getFirstNewElement(oldResults, interleaved) >= 0){
				isNewInA = true;
			}
			if (getFirstNewElement(results, interleaved) >= 0){
				isNewInB = true;
			}
			
		} while (isNewInA && isNewInB);
		
		// Log the original Google Ranking
		log.appendLog("\tGoogle Ranking:");
		int ind = 0;
		for (GoogleResult res: oldResults){
			ind++;
			log.appendLog("\t\t" + ind + ". " + res.getTitle() + " (" + res.getUrl() + ")");
		}
		ind = 0;
		
		// Log the re-ranked ranking		
		log.appendLog("\tReranked Ranking:");
		for (GoogleResult res: results){
			ind++;
			log.appendLog("\t\t" + ind + ". " + res.getTitle() + " (" + res.getUrl() + ")");
		}
		
		return interleaved;
		
	}

	/**
	 * Implementation of the balanced interleaving algorithm that will interleave the original
	 * Google ranking with the new re-ranked version
	 * @param oldResults	Original Google ranking	
	 * @param results		New Google ranking
	 * @param query			Search query
	 * @param user			User's unique identifier
	 * @param log			Log file
	 * @return				New interleaved ranking
	 */
	private static ArrayList<GoogleResult> balancedInterleaving(ArrayList<GoogleResult> oldResults, ArrayList<GoogleResult> results, String query, String user, Logging log) {
		ArrayList<GoogleResult> interleaved = new ArrayList<GoogleResult>();
		int ka = 0; int kb = 0; 
		
		// Seed the random with the current user, the current query and the
		// current hour in order not to get different results when the user
		// reloads the page
		Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.MILLISECOND, 0);
		long seed = (long) rightNow.getTime().getTime();
		for (int i = 0; i < query.length(); i++){
			seed += (Math.pow((long) query.charAt(i), 3));
		}
		for (int i = 0; i < user.length(); i++){
			seed += (Math.pow((long) user.charAt(i), 3));
		}
		int AFirst = new Random(seed).nextInt(2);
		
		// Log the steps of this algorithm
		if (AFirst == 1){
			log.appendLog("\tRandom: Original");
		} else {
			log.appendLog("\tRandom: Reranked");
		}
		// Log the original Google Ranking
		log.appendLog("\tGoogle Ranking:");
		int ind = 0;
		for (GoogleResult res: oldResults){
			ind++;
			log.appendLog("\t\t" + ind + ". " + res.getTitle() + " (" + res.getUrl() + ")");
		}
		ind = 0;
		// Log the re-ranked ranking
		log.appendLog("\tReranked Ranking:");
		for (GoogleResult res: results){
			ind++;
			log.appendLog("\t\t" + ind + ". " + res.getTitle() + " (" + res.getUrl() + ")");
		}
		
		// Until all results have been used
		while (ka < oldResults.size() && kb < results.size()){
			// If more results from the new ranking have been used or if random bit is 1
			if (ka < kb || (ka == kb && AFirst == 1)){
				boolean exists = false;
				// Check whether the next result of the original ranking is not in the interleaved ranking yet
				for (GoogleResult res: interleaved){
					if (res.getOriginalRank() == oldResults.get(ka).getOriginalRank()){
						exists = true;
					}
				}
				// Add it to the interleaved ranking
				if (!exists){
					GoogleResult res = oldResults.get(ka);
					res.setTeam("Original");
					interleaved.add(res);
				}
				ka++;
			} else {
				boolean exists = false;
				// Check whether the next result of the re-ranked ranking is not in the interleaved ranking yet
				for (GoogleResult res: interleaved){
					if (res.getOriginalRank() == results.get(kb).getOriginalRank()){
						exists = true;
					}
				}
				// Add it to the interleaved ranking
				if (!exists){
					GoogleResult res = results.get(kb);
					res.setTeam("Reranked");
					interleaved.add(res);
				}
				kb++;
			}
		}
		return interleaved;
	}

	/**
	 * Calculate the weight for a term appearing in a Google snippet
	 * @param unigrams			User profile
	 * @param term				Current term from snippet
	 * @param totalResults		Total number of terms in the overall browsing history
	 * @return					Calculated Language Model probability
	 */
	private static double getWeight(HashMap<String, Unigram> unigrams, String term, double totalResults){
		if (unigrams.containsKey(term)){
			return Math.log1p(unigrams.get(term).getWeight());
		}
		// Give weight to unseen words as well to avoid a zero probability
		return Math.log1p(1 / totalResults);
	}

	/**
	 * Read and store a user's PClick profile file. The PClick file has the following format:
	 * 	e.g.	webex instant messaging
	 * 			=> http://enterprise.yahoo.com/flash/%20ybm.html
	 *			=> http://www.cisco.com/en/US/products/ps10352/index.html
	 * @param user		User's unique identifier
	 * @return			List of search queries and associated URLs that were clicked for these queries
	 */
	private static HashMap<String, ArrayList<String>> getPClickData(String user){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
			// Open the PClick file
		    FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("profiles") + user + ".pclick.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String prevSearch = "";
			while ((strLine = br.readLine()) != null)   {
				if (!strLine.equals("")){
					// This indicates a clicked URL in the PClick file
			    	if (strLine.toLowerCase().startsWith("=>")){
			    		ArrayList<String> arl = map.get(prevSearch);
			    		arl.add(strLine.substring(3));
			    		map.put(prevSearch, arl);
			    	// This indicates a new search query in the PClick file
			    	} else {
			    		map.put(strLine, new ArrayList<String>());
			    		prevSearch = strLine;
			    	}
			    }
			}
			in.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return map;
	}
	
	/**
	 * Re-ranks a list of search results using PClick re-ranking
	 * @param query				Search query
	 * @param profile			User's profile
	 * @param results			Google search results
	 * @param visited			Whether extra weight should be given to previously visited pages
	 * @param visitedW			The snippet score will be multiplied by this for previously visited pages
	 * @return					Re-ranked search results
	 */
	public static ArrayList<GoogleResult> pClick(String query, Profile profile, ArrayList<GoogleResult> results, 
			boolean visited, int visitedW) {
		HashMap<String, ArrayList<String>> searches = getPClickData(profile.getUserId());
		try {
			for (GoogleResult res: results){
				res.setWeight(0);
			}
			// Replace + by space as Google encodes queries like this in the URL
			String key = URLDecoder.decode(query.toLowerCase().replaceAll("[+]", " "),"utf-8");
			if (searches.containsKey(key)){
				ArrayList<String> urls = searches.get(key);
				for (GoogleResult res: results){
					double clicksA = 1;
					double clicksB = urls.size();
					double beta = 0.5;
					for (String url: urls){
						// Check whether the current result has been clicked before for this query
						if (url.equals(res.getUrl().toLowerCase())){
							clicksA++;
							System.out.println("Visited " + url + " for this search");
						}
					}
					// The PClick formula
					double score = clicksA / (clicksB + beta);
					res.setWeight(score);
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		// Sort the results based on their score
		results = doSort(results);
		// Give extra weight to previously visited URLs
		results = clickBased(results, visited, visitedW, profile);
		return results;
	}

}
