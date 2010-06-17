/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.evaluation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.EvaluationResult;

/**
 * Class used to read the CSV file that contains all of the evaluated personalization
 * strategies and their NDCG scores for all users. The file is structured like this:
 * 		"TFxIDF, RTitle, RMKeyw, RCCParse, GFilt - LM, Look At Rank, Visited ",0.507,0.548,0.636,0.597,0.541,0.608,0.573,48/72 (66.7%),TRUE,TRUE
 *  	"TFxIDF, RTitle, RMKeyw, RCCParse, NoFilt - LM, Look At Rank, Visited ",0.506,0.548,0.636,0.602,0.541,0.608,0.573,48/72 (66.7%),TRUE,TRUE
 * 		"TF, RMDescr, RTerms, - LM, Look At Rank, Visited ",0.52,0.592,0.608,0.599,0.514,0.602,0.573,44/72 (61.1%),TRUE,TRUE
 * 		"TFxIDF, RTitle, RMKeyw, RTerms, RCCParse, NoFilt - LM, Look At Rank, Visited ",0.505,0.541,0.632,0.609,0.539,0.604,0.572,48/72 (66.7%),TRUE,TRUE
 * 		...
 */
public class EvaluationResults {

	// List of results with an entry for each personalization strategy
	private ArrayList<EvaluationResult> results = new ArrayList<EvaluationResult>();
	
	public EvaluationResults(){
		readResults();
	}

	/**
	 * Read the CSV file and store the results into a list
	 */
	private void readResults(){
		DataInputStream in = null;
		try{
			// Read the CSV file line by line
		    FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("csvresults"));
		    in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
		      // Make sure that we don't try to parse empty lines
		      if (!strLine.equals("")){
		    	  EvaluationResult result = new EvaluationResult(strLine);
		    	  results.add(result);
		      }
		    }
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			 if (in != null){
			    try {
					in.close();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
	}
	
	// Getters and setters
	
	public void setResults(ArrayList<EvaluationResult> results) {
		this.results = results;
	}

	public ArrayList<EvaluationResult> getResults() {
		return results;
	}
	
}
