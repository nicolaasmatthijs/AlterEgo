package org.cl.nm417.google;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Pattern;

import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;

public class GoogleNGram {

	public static void loadGoogleNGram(){
		
		try {
			
			long start = new Date().getTime();
			
			 JRedis jredis = new JRedisClient("localhost", 6379);
			 String res = new String(jredis.get("nicolaas"));
			// Check whether it already exists
			
			if (res == null){
				//Fill it with dictionary
				FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/ngrams/vocab");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				//Read File Line By Line
				Pattern pattern = Pattern.compile("\\s+");
				//ArrayList<DocumentFrequency> freqs = new ArrayList<DocumentFrequency>();
				while ((strLine = br.readLine()) != null)   {
					if (strLine.length() != 0){
				    	String[] split = pattern.split(strLine.toLowerCase());
				    	//DocumentFrequency freq = new DocumentFrequency();
				    	//freq.setTerm(split[0].toLowerCase());
				    	//freq.setFrequency(Double.parseDouble(split[1]));
				    	if(!jredis.exists(split[0].toLowerCase())) {
				    		jredis.set(split[0].toLowerCase(), split[1]);
				    	}
				    	//freqs.add(freq);
				    	//c.set(split[0], 3600, split[1]);
					}
				}
				in.close();
				
			}

			// Retrieve a value (synchronously).
			//Object myObject=c.get("someKey");
			
			long end = new Date().getTime();
			System.out.println("Loaded dictionary in " + ((end - start) / 1000) + " seconds");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
}
