package org.cl.nm417.google;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Unigram;

public class GoogleRerank {

	public static ArrayList<GoogleResult> findCommonalities(Profile profile, ArrayList<GoogleResult> results,
			boolean combineRankings, String interleaveMethod, boolean lookAtGoogleRank, boolean uniqueMatch,
			boolean visited, int visitedW) {
		
		ArrayList<GoogleResult> oldResults = new ArrayList<GoogleResult>();
		for (GoogleResult r: results){
			oldResults.add(r);
		}
				
		for (GoogleResult result: results){
			String resultText = (result.getTitle() + " " + result.getSummary()).replaceAll("[.,-/\"':;?()><=ÐÈ|_]", "").toLowerCase();
			ArrayList<String> arlCommon = new ArrayList<String>();
			int common = 0;
			for (String s: resultText.split("\\s+")){
				for (Unigram uni: profile.getUnigrams()){
					if (uni.getText().toLowerCase().equals(s) && !arlCommon.contains(s)){
						if (uniqueMatch){
							arlCommon.add(s);
						}
						result.setNewWeight(result.getNewWeight() + uni.getWeight());
						common++;
					}
				}
			}
			
			if (lookAtGoogleRank){
				result.setNewWeight(result.getNewWeight() / Math.log(1 + result.getRank()));
			}

		}
		
		results = doSort(results);
		results = clickBased(results, visited, visitedW, profile.getUserId());
			
		if (combineRankings){
			if (interleaveMethod.equals("balanced")){
				results = balancedInterleaving(oldResults, results);
			} else if (interleaveMethod.equals("teamdraft")){
				results = teamdraftInterleaving(oldResults, results);
			}
		}
		
		return results;
			
	}
	
	private static ArrayList<GoogleResult> clickBased(ArrayList<GoogleResult> results, boolean visited, int visitedW, String user){
		if (visited){
			HashMap<String, Integer> arlVisited = getURLs(user);
			for (GoogleResult result: results){
				if (arlVisited.containsKey(result.getUrl().toLowerCase())){
					result.setNewWeight(result.getNewWeight() * visitedW * arlVisited.get(result.getUrl().toLowerCase()));
					System.out.println(result.getUrl() + " => " + arlVisited.get(result.getUrl().toLowerCase()));
				}
			}
			results = GoogleRerank.doSort(results);
		}
		return results;
	}
	
	private static HashMap<String, Integer> getURLs(String user) {
		HashMap<String, Integer> arlVisited = new HashMap<String, Integer>();
		try{
		    FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + user + ".url.txt");
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		      if (!strLine.equals("")){
		    	  String[] split = strLine.toLowerCase().split(" => ");
		    	  arlVisited.put(split[0],Integer.parseInt(split[1]));
		      }
		    }
		    in.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return arlVisited;
	}
	
	public static ArrayList<GoogleResult> doSort(ArrayList<GoogleResult> results){
		Collections.sort(results, new Comparator<GoogleResult>(){
			 
			public int compare(GoogleResult g1, GoogleResult g2) {
				if (g1.getNewWeight() > g2.getNewWeight()){
	            	return -1;
	            } else if (g1.getNewWeight() == g2.getNewWeight()){
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

	public static ArrayList<GoogleResult> applyLM(Profile profile, ArrayList<GoogleResult> results, 
			boolean interleave, String interleaveMethod, boolean lookatrank, double totalResults,
			boolean visited, int visitedW) {
		
		ArrayList<GoogleResult> oldResults = new ArrayList<GoogleResult>();
		for (GoogleResult r: results){
			oldResults.add(r);
		}
		
		for (GoogleResult result: results){
			double weight = 0.0;
			double length = 0.0;
			String res = (result.getSummary() + " " + result.getTitle()).replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
			for (String s: res.split("\\s+")){
				weight += getWeight(profile, s, totalResults);
				length++;
			}
			result.setNewWeight(weight / length);
			if (lookatrank){
				result.setNewWeight(result.getNewWeight() / Math.log(1 + result.getRank()));
			}
		}
		
		results = doSort(results);
		results = clickBased(results, visited, visitedW, profile.getUserId());
		
		if (interleave){
			if (interleaveMethod.equals("balanced")){
				results = balancedInterleaving(oldResults, results);
			} else if (interleaveMethod.equals("teamdraft")){
				results = teamdraftInterleaving(oldResults, results);
			}
		}
		
		return results;
	}
	
	private static int getFirstNewElement(ArrayList<GoogleResult> results, ArrayList<GoogleResult> interleaved){
		int rank = 0;
		for (GoogleResult res: results){
			boolean isNew = true;
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
		return -1;
	}
	
	private static ArrayList<GoogleResult> teamdraftInterleaving(ArrayList<GoogleResult> oldResults, ArrayList<GoogleResult> results) {
		ArrayList<GoogleResult> interleaved = new ArrayList<GoogleResult>();
		ArrayList<GoogleResult> teamA = new ArrayList<GoogleResult>();
		ArrayList<GoogleResult> teamB = new ArrayList<GoogleResult>();
		int AFirst = (int) Math.round(Math.random());
		boolean isNewInA = true; boolean isNewInB = true;
		do {
			
			if (teamA.size() < teamB.size() || (teamA.size() == teamB.size() && AFirst == 1)){
				int k = getFirstNewElement(oldResults, interleaved);
				GoogleResult res = oldResults.get(k);
				res.setTeam("Original");
				interleaved.add(res);
				teamA.add(res);
			} else {
				int k = getFirstNewElement(results, interleaved);
				GoogleResult res = results.get(k);
				res.setTeam("Reranked");
				interleaved.add(res);
				teamB.add(res);
			}
			
			isNewInA = false; isNewInB = false;
			if (getFirstNewElement(oldResults, interleaved) >= 0){
				isNewInA = true;
			}
			if (getFirstNewElement(results, interleaved) >= 0){
				isNewInB = true;
			}
			
		} while (isNewInA && isNewInB);
		
		
		return interleaved;
	}

	private static ArrayList<GoogleResult> balancedInterleaving(ArrayList<GoogleResult> oldResults, ArrayList<GoogleResult> results) {
		ArrayList<GoogleResult> interleaved = new ArrayList<GoogleResult>();
		int ka = 0; int kb = 0; int AFirst = (int) Math.round(Math.random());
		while (ka < oldResults.size() && kb < results.size()){
			if (ka < kb || (ka == kb && AFirst == 1)){
				boolean exists = false;
				for (GoogleResult res: interleaved){
					if (res.getOriginalRank() == oldResults.get(ka).getOriginalRank()){
						exists = true;
					}
				}
				if (!exists){
					GoogleResult res = oldResults.get(ka);
					res.setTeam("Original");
					interleaved.add(res);
				}
				ka++;
			} else {
				boolean exists = false;
				for (GoogleResult res: interleaved){
					if (res.getOriginalRank() == results.get(kb).getOriginalRank()){
						exists = true;
					}
				}
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

	private static double getWeight(Profile profile, String term, double totalResults){
		for (Unigram u: profile.getUnigrams()){
			if (u.getText().equalsIgnoreCase(term)){
				return Math.log1p(u.getWeight());
			}
		}
		return Math.log1p(1 / totalResults);
	}

	private static HashMap<String, ArrayList<String>> getPClickData(String user){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try{
		    FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + user + ".pclick.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			String prevSearch = "";
			while ((strLine = br.readLine()) != null)   {
				if (!strLine.equals("")){
			    	if (strLine.toLowerCase().startsWith("=>")){
			    		ArrayList<String> arl = map.get(prevSearch);
			    		arl.add(strLine.substring(3));
			    		map.put(prevSearch, arl);
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
	
	public static ArrayList<GoogleResult> pClick(String query, Profile profile, ArrayList<GoogleResult> results, 
			boolean interleave, String interleaveMethod, boolean lookatrank, boolean umatching,boolean visited, int visitedW) {
		HashMap<String, ArrayList<String>> searches = getPClickData(profile.getUserId());
		try {
			String key = URLDecoder.decode(query.toLowerCase().replaceAll("[+]", " "),"utf-8");
			if (searches.containsKey(key)){
				ArrayList<String> urls = searches.get(key);
				for (GoogleResult res: results){
					double clicksA = 1;
					double clicksB = urls.size();
					double beta = 0.5;
					for (String url: urls){
						if (url.equals(res.getUrl().toLowerCase())){
							clicksA++;
							System.out.println("Visited " + url + " for this search");
						}
					}
					double score = clicksA / (clicksB + beta);
					res.setNewWeight(score);
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		results = doSort(results);
		results = clickBased(results, visited, visitedW, profile.getUserId());
		return results;
	}

}
