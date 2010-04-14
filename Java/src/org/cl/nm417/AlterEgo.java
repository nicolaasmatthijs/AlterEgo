package org.cl.nm417;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.cl.nm417.data.Unigram;
import org.cl.nm417.extraction.TermExtraction;
import org.cl.nm417.extraction.UserProfile;
import org.cl.nm417.google.GoogleRerank;
import org.cl.nm417.google.GoogleResult;
import org.cl.nm417.google.GoogleSearch;
//import org.cl.nm417.extraction.Wikipedia;
import org.cl.nm417.xmlparser.DataParser;

public class AlterEgo {

	public static void main(String[] args) {
		
		ArrayList<ArrayList<Unigram>> profiles = processSet("development");
		ArrayList<GoogleResult> results = GoogleSearch.doGoogleSearch("design");
		GoogleRerank.findCommonalities(profiles, results, false);
		
		//processUser("usr_2543662");
		
	}
	
	public static String test(){
		return "Nicolaas";
	}
	
	private static ArrayList<Unigram> processUser(String userid){
		
		Long start = new Date().getTime();
		DataParser data = new DataParser(userid);
		Long end = new Date().getTime();
		System.out.println("User document parsed in " + ((end - start) / 1000) + " seconds");
		ArrayList<Unigram> profile = UserProfile.extractUnigramProfile(data, false, true, false, false, false);
		//profile = UserProfile.extractBM25Profile(data, profile, false, true, true, false, false);
		System.out.println("User visited " + data.getDocuments().size() + " documents");
		System.out.println("****************");
		//GoogleSearch.getGoogleSearches(data);
		data = null;
		return profile;
		
	}
	
	private static ArrayList<ArrayList<Unigram>> processSet(String set){
		
		ArrayList<ArrayList<Unigram>> profiles = new ArrayList<ArrayList<Unigram>>();
		String toProcess[] = new String[0];
		if (set.equals("development")){
			toProcess = new String[]{"usr_3484406"};
		} else if (set.equals("test")){
			toProcess = new String[]{"usr_6422669"};
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
			ArrayList<Unigram> profile = processUser(s);
			profiles.add(profile);
		}
		
		return profiles;
		
	}

}
