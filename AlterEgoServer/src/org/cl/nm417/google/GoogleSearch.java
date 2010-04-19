package org.cl.nm417.google;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import org.cl.nm417.data.Document;
import org.cl.nm417.data.DocumentFrequency;
import org.cl.nm417.json.JSONObject;
import org.cl.nm417.xmlparser.DataParser;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;

public class GoogleSearch {

	public static ArrayList<GoogleResult> doGoogleSearch(String query){
		
		ArrayList<GoogleResult> results = new ArrayList<GoogleResult>();
		results.addAll(doRestSearch(query, 0));
		results.addAll(doRestSearch(query, 8));
		results.addAll(doRestSearch(query, 16));
		results.addAll(doRestSearch(query, 24));
		results.addAll(doRestSearch(query, 32));
		results.addAll(doRestSearch(query, 40));
		results.addAll(doRestSearch(query, 48));
		return results;
		
	}
	
	private static ArrayList<GoogleResult> doRestSearch(String query, int start){
		ArrayList<GoogleResult> results = new ArrayList<GoogleResult>();
		
		WebFile file;
		try {
			file = new WebFile( "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(query, "UTF-8") + "&start=" + start + "&rsz=large" );
			String json = (String) file.getContent();
			JSONObject object = new JSONObject(json).getJSONObject("responseData");
			for (int i = 0; i < object.getJSONArray("results").length(); i++){				
				JSONObject obj = object.getJSONArray("results").getJSONObject(i);
				GoogleResult result = new GoogleResult();
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

	public static void getGoogleSearches(DataParser data) {
		for (Document d: data.getDocuments()){
			if (d.getUrl().toLowerCase().contains("google.") && d.getUrl().toLowerCase().contains("/search?")){
				System.out.println(d.getUrl());
			}
		}
		System.out.println("*************************************");
	}

	public static ArrayList<DocumentFrequency> getNumberOfResults(ArrayList<String> words) {
		
		try {
			long start = new Date().getTime();
			ArrayList<DocumentFrequency> freq = new ArrayList<DocumentFrequency>();
			JRedis jredis = new JRedisClient("localhost", 6379);
			for (String s: words){
				if (!jredis.exists(s.toLowerCase())){
					DocumentFrequency df = new DocumentFrequency();
		  		  	df.setTerm(s.toLowerCase());
		  		  	df.setFrequency(220680773);
		  		  	freq.add(df);
				} else {
					double n = Double.parseDouble(new String(jredis.get(s.toLowerCase())));
					DocumentFrequency df = new DocumentFrequency();
		  		  	df.setTerm(s.toLowerCase());
		  		  	df.setFrequency(n);
		  		  	freq.add(df);
				}
			}
			long end = new Date().getTime();
			System.out.println("Lookup of " + freq.size() + " in " + ((end - start) / 1000) + " seconds");
			return freq;
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
		
		/* HashMap<String, String> map = new HashMap<String, String>(1000);
		ArrayList<DocumentFrequency> freq = new ArrayList<DocumentFrequency>();
		try{
		    FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/ngrams/vocab");
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    int contains = 0;
		    Pattern pattern = Pattern.compile("\\s+");
		    while ((strLine = br.readLine()) != null)   {
		      if (strLine.length() != 0){
		    	  String[] split = pattern.split(strLine.toLowerCase());
		    	  map.put(split[0], split[1]);
		    	  contains++;
		    	  if (contains >= 1000){
		    		  ArrayList<String> toRemove = new ArrayList<String>();
		    		  for (String s: words) {
		    			  if (map.containsKey(s)){
		    				  DocumentFrequency df = new DocumentFrequency();
		    				  df.setTerm(s);
		    				  df.setFrequency(Integer.parseInt(map.get(s)));
		    				  freq.add(df);
		    				  toRemove.add(s);
		    			  }
			    	  }
		    		  for (String s: toRemove){
		    			  words.remove(s);
		    		  }
		    		  map = new HashMap<String, String>(1000);
		    		  contains = 0;
		    	  }
		      }
		    }
		    in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		for (String s: words){
			DocumentFrequency df = new DocumentFrequency();
  		  	df.setTerm(s);
  		  	df.setFrequency(220680773);
  		  	freq.add(df);
		}
		return freq; */

	}
	
	public static String getGoogleHTML(String query, int page){
		String output = "";
		WebFile file;
		try {
			file = new WebFile( "http://www.google.co.uk/search?q=" + URLEncoder.encode(query, "UTF-8") + "&start=" + ((page - 1) *10));
			output = ((String) file.getContent()).replaceAll("/images/", "http://www.google.co.uk/images/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
}
