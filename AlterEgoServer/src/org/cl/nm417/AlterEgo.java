package org.cl.nm417;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.cl.nm417.data.Document;
import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.extraction.UserProfile;
import org.cl.nm417.google.GoogleRerank;
import org.cl.nm417.google.GoogleResult;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.xmlparser.DataParser;

public class AlterEgo {
	
	public static HashMap<String, Object> config;
	
	public static Profile generateProfile(HashMap<String, Object> params) {
		
		double start = new Date().getTime();
		config = params;
		
		Profile profile = processUser((String)config.get("user"));
		if ((Boolean)config.get("takeLog")){
			for (Unigram u: profile.getUnigrams()){
				u.setWeight(Math.log(u.getWeight() + 1));
			}
		}
		writeProfile(profile);
		double end = new Date().getTime();
		System.out.println("User profile generated in " + ((end - start) / 1000) + " seconds");
		return profile;
		
	}
	
	public static ArrayList<GoogleResult> SearchGoogle(String query, String user, 
			boolean rerank, String method, boolean interleave, String interleaveMethod, 
			boolean lookatrank, boolean umatching, boolean visited, int visitedW){
		Profile profile = new Profile();
		profile.setUserId(user);
		profile.setUnigrams(readUnigrams(user));
		ArrayList<GoogleResult> results = GoogleSearch.doGoogleSearch(query);
		if (rerank){
			if (method.equals("lm")){
				double totalWords = calculateTotalWords(profile);
				profile = calculateLMStatistics(profile, totalWords);
				results = GoogleRerank.applyLM(profile, results, interleave, interleaveMethod, lookatrank, totalWords, visited, visitedW);
			} else if (method.equals("pclick")){
				results = GoogleRerank.pClick(query, profile, results, interleave, interleaveMethod, lookatrank, umatching, visited, visitedW);
			} else {
				results = GoogleRerank.findCommonalities(profile, results, interleave, interleaveMethod, lookatrank, umatching, visited, visitedW);
			}
		}
		return results;
	}

	private static double calculateTotalWords(Profile profile){
		double totalWords = 0;
		for (Unigram u: profile.getUnigrams()){
			totalWords += u.getWeight();
		}
		return totalWords;
	}
	
	private static Profile calculateLMStatistics(Profile profile, double totalWords) {
		for (Unigram u: profile.getUnigrams()){
			u.setWeight((1 + u.getWeight()) / totalWords);
		}
		return profile;
	}

	private static ArrayList<Unigram> readUnigrams(String user){
		ArrayList<Unigram> unigrams = new ArrayList<Unigram>();
		try{
		    // Open the file that is the first 
		    // command line parameter
		    FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + user + ".txt");
		    // Get the object of DataInputStream
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		      // Print the content on the console
		      if (!strLine.equals("")){
		    	  Unigram u = new Unigram();
		    	  u.setWeight(Double.parseDouble(strLine.split("=>")[0].trim()));
		    	  u.setText(strLine.split("=>")[1].trim());
		    	  unigrams.add(u);
		      }
		    }
		    //Close the input stream
		    in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return unigrams;
	}
	
	private static void writeProfile(Profile profile){
		
		try {
		    FileWriter fstream = new FileWriter("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + profile.getUserId() + ".txt");
		    BufferedWriter out = new BufferedWriter(fstream);
		    for (Unigram u: profile.getUnigrams()){
		    	out.write(u.getWeight() + " => " + u.getText() + "\n");
		    }
		    out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	private static Profile processUser(String userid){
		
		DataParser data = new DataParser(userid);
		Profile profile = new Profile();
		profile.setUserId(userid);
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
		writeURLs(userid, data);
		writePClickData(userid, data);
		profile.setDocuments(data.getDocuments().size());
		data = null;
		return profile;
		
	}
	
	private static void writeURLs(String userid, DataParser data) {
		 HashMap<String, Integer> url = new HashMap<String, Integer>();
		 try {
			 FileWriter fstream = new FileWriter("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + userid + ".url.txt");
			 BufferedWriter out = new BufferedWriter(fstream);
			 for (Document d: data.getDocuments()){
				 if (!url.containsKey(d.getUrl())){
					 url.put(d.getUrl(), 1);
				 } else {
					 url.put(d.getUrl(), url.get(d.getUrl()) + 1);
				 }
			 }
			 for (String s: url.keySet()){
				out.write(s + " => " + url.get(s) + "\n");
			 }
			 out.close();
		 } catch (Exception e){
			 System.err.println("Error: " + e.getMessage());
		 }
	}

	private static void writePClickData(String userid, DataParser data){
		HashMap<String, ArrayList<String>> searches = new HashMap<String, ArrayList<String>>();
		boolean trackNext = false;
    	String prevSearch = "";
    	try {
    		FileWriter fstream = new FileWriter("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + userid + ".pclick.txt");
			BufferedWriter out = new BufferedWriter(fstream);
	    	for (Document d: data.getDocuments()){
	    		String url = d.getUrl().toLowerCase();
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
	    		} else if (trackNext){
	    			trackNext = false;
	    			ArrayList<String> arl = searches.get(prevSearch);
	    			arl.add(d.getUrl());
	    			searches.put(prevSearch, arl);
	    		}
	    	}
	    	
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
	
	public static ArrayList<Profile> processSet(String set){
		
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		String toProcess[] = new String[0];
		if (set.equals("development")){
			toProcess = new String[]{"usr_3484406"};
		} else if (set.equals("train")){
			toProcess = new String[]{"usr_2434413", "usr_2543662", "usr_2554801", "usr_261821", "usr_2917559", 
					"usr_406338", "usr_4362753", "usr_4965278", "usr_5945189", "usr_6145747", "usr_6318797", 
			 		"usr_6422669", "usr_6989158", "usr_7318689", "usr_7600860", "usr_8002037", "usr_9533453"};
		} else if (set.equals("test")){
			toProcess = new String[]{"usr_2296654", "usr_2555137", "usr_263177", "usr_3762438", "usr_4130066", 
					"usr_4593969", "usr_5117040", "usr_5136203", "usr_597873", "usr_6150712", "usr_6326553", 
					"usr_6460086", "usr_6921645", "usr_7865799", "usr_8613294", "usr_9918599"};
		}
		for (String s: toProcess){
			Profile profile = processUser(s);
			profiles.add(profile);
		}
		
		return profiles;
		
	}
	
}
