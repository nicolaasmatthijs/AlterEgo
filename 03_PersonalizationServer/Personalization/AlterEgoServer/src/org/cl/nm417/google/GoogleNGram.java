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
import java.util.Date;
import java.util.regex.Pattern;

import org.cl.nm417.config.ConfigLoader;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;

/**
 * Class that will store the Google N-Gram corpus into a Redis cache server.
 * This will allow for easy retrieval of frequency counts.
 */
public class GoogleNGram {

	/**
	 * Load the Google N-Gram corpus into Redis
	 */
	public static void loadGoogleNGram(){
		
		try {
			
			long start = new Date().getTime();
			
			// Get a connection to the Redis server
			JRedis jredis = new JRedisClient(ConfigLoader.getConfig().getProperty("redisserver"), Integer.parseInt(ConfigLoader.getConfig().getProperty("redisport")));
			
			// Check whether it already exists
			String res = new String(jredis.get("nicolaas"));
			if (res == null){
				
				//Fill it with dictionary
				FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("ngramfile"));
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				Pattern pattern = Pattern.compile("\\s+");
				// Read the NGram file line by line
				while ((strLine = br.readLine()) != null)   {
					// Make sure that the line is not empty
					if (strLine.length() != 0){
						// The format of a line is "word freq" (e.g. "nicolaas  1400").
						// We split the string to retrieve both the word and the frequency
				    	String[] split = pattern.split(strLine.toLowerCase());
				    	if(!jredis.exists(split[0].toLowerCase())) {
				    		jredis.set(split[0].toLowerCase(), split[1]);
				    	}
					}
				}
				in.close();
				
			}

			long end = new Date().getTime();
			System.out.println("Loaded dictionary in " + ((end - start) / 1000) + " seconds");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
}
