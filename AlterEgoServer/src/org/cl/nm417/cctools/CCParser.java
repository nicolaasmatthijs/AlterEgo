package org.cl.nm417.cctools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.cl.nm417.data.Document;
import org.cl.nm417.data.Sentence;
import org.cl.nm417.data.SentenceItem;

public class CCParser {

	public static ArrayList<Document> readCCParse(String url, ArrayList<Document> documents){
		
		try{
		    FileInputStream fstream = new FileInputStream(url);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    int i = 0;
		    Pattern pattern = Pattern.compile("\\s+");
		    while ((strLine = br.readLine()) != null)   {
		    	
		    	if (strLine.equals("<c> </alteregodocument>|</alteregodocument>|NN|I-NP|O|N")){
		    		i++;
		    	} else if (strLine.startsWith("<c>")){
		    		String sent = strLine.substring(4).trim();
		    		String[] split = pattern.split(sent);
		    		Sentence sentence = new Sentence();
		    		for (String s: split){
		    			SentenceItem item = new SentenceItem(s);
		    			sentence.getSentence().add(item);
		    		}
		    		ArrayList<String> allWords = new ArrayList<String>();
		    		for (SentenceItem item: sentence.getSentence()){
						if (item.getPOS().startsWith("NN")){
							String toAdd = item.getWord().replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
							if (!allWords.contains(toAdd)){
								allWords.add(toAdd);
							}
						}
					}
		    		documents.get(i).getParsed().add(allWords);
		    	}
		    }
		    //Close the input stream
		    in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return documents;
		
	}
	
}
