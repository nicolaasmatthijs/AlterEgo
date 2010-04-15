package org.cl.nm417.extraction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.cl.nm417.json.JSONArray;

public class TermExtraction {

	public static JSONArray extractTerms(String content){
		try {
		    // Construct data
		    String data = URLEncoder.encode("context", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
		    
		    // Send data
		    URL url = new URL("http://alterego.caret.cam.ac.uk:8080/terms");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String output = "";
		    while ((line = rd.readLine()) != null) {
		    	output += line;
		    }
		    wr.close();
		    rd.close();
		    System.out.println(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
		    System.out.println(output);
		    System.out.println(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
		    return new JSONArray(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
}
