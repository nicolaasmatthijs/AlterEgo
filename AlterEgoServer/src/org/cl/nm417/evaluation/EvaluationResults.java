package org.cl.nm417.evaluation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class EvaluationResults {

	private ArrayList<EvaluationResult> results = new ArrayList<EvaluationResult>();
	
	public EvaluationResults(){
		readResults();
	}

	private void readResults(){
		DataInputStream in = null;
		try{
		    FileInputStream fstream = new FileInputStream("/Users/nicolaas/Desktop/AlterEgo/AlterEgoResults.csv");
		    in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null)   {
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
