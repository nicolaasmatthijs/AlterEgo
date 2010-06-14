package org.cl.nm417.google;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.cl.nm417.data.Document;
import org.cl.nm417.json.JSONObject;
import org.cl.nm417.xmlparser.DataParser;

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

	public static double getNumberOfResults(String text) {
		WebFile file;
		double total = 0;
		try {
			file = new WebFile( "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(text, "UTF-8") + "&start=0&rsz=small" );
			String json = (String) file.getContent();
			//System.out.println(json);
			JSONObject object = new JSONObject(json).getJSONObject("responseData");
			total = Double.parseDouble(object.getString("estimatedResultCount"));
		} catch (Exception e) {
			total = 1000000000000.00;
		}
		return total;
	}
	
}
