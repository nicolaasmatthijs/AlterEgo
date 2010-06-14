package org.cl.nm417.google;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.cl.nm417.data.Unigram;

public class GoogleRerank {

	public static void findCommonalities(ArrayList<ArrayList<Unigram>> profiles, ArrayList<GoogleResult> results,
			boolean combineRankings) {
		ArrayList<GoogleResult> oldResults = new ArrayList<GoogleResult>();
		for (GoogleResult r: results){
			oldResults.add(r);
		}
		
		int rank = 0;
		for (GoogleResult res: results){
			rank++;
			System.out.println(rank + ") " + res.getTitle() + "(" + res.getRank() + " - " + res.getNewWeight() + ")");
		}
		
		for (ArrayList<Unigram> profile: profiles){
			for (GoogleResult result: results){
				ArrayList<String> arlCommon = new ArrayList<String>();
				int common = 0;
				for (String s: result.getTitle().split(" ")){
					s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
					for (Unigram uni: profile){
						if (uni.getText().toLowerCase().equals(s)){
							arlCommon.add(s);
							result.setNewWeight(result.getNewWeight() + Math.log(uni.getWeight()));
							common++;
							break;
						}
					}
				}
				/* for (Unigram uni: profile){
					if (result.getTitle().replaceAll("[.,-/\"':;]", "").toLowerCase().contains(uni.getText().toLowerCase())){
						arlCommon.add(uni.getText().toLowerCase());
						result.setNewWeight(result.getNewWeight() + Math.log(uni.getWeight()));
						common++;
						break;
					}
				} */
				for (String s: result.getSummary().split(" ")){
					s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
					for (Unigram uni: profile){
						if (uni.getText().toLowerCase().equals(s)){
							arlCommon.add(s);
							result.setNewWeight(result.getNewWeight() + Math.log(uni.getWeight()));
							common++;
							break;
						}
					}
				} 
				/* for (Unigram uni: profile){
					if (result.getSummary().replaceAll("[.,-/\"':;]", "").toLowerCase().contains(uni.getText().toLowerCase())){
						arlCommon.add(uni.getText().toLowerCase());
						result.setNewWeight(result.getNewWeight() + Math.log(uni.getWeight()));
						common++;
						break;
					}
				} */
				 
				result.setNewWeight(result.getNewWeight() / Math.log(1 + result.getRank()));
				
				//System.out.println("Number of commonalities for rank " + result.getRank() + "= " + common);
				//System.out.println("\t=>" + result.getTitle());
				//for (String s: arlCommon){
				//	System.out.println("\t" + s);
				//}
				//System.out.println("********************************************************");
			}
			
			Collections.sort(results, new Comparator<GoogleResult>(){
				 
	            public int compare(GoogleResult g1, GoogleResult g2) {
	            	if (g1.getNewWeight() > g2.getNewWeight()){
	            		return -1;
	            	} else if (g1.getNewWeight() == g2.getNewWeight()){
	            		if (g1.getRank() < g2.getRank()){
	            			return -1;
	            		} else {
	            			return 1;
	            		}
	            	} else {
	            		return 1;
	            	}
	            }
	 
	        });
			
			ArrayList<Integer> used = new ArrayList<Integer>();
			ArrayList<GoogleResult> finalRes = new ArrayList<GoogleResult>();
			if (combineRankings){
				for (int i = 0; i < results.size(); i++){
					GoogleResult origres = oldResults.get(i);
					GoogleResult newres = results.get(i);
					if (!used.contains(origres.getRank())){
						finalRes.add(origres);
						used.add(origres.getRank());
					} 
					if (!used.contains(newres.getRank())){
						finalRes.add(newres);
						used.add(newres.getRank());
					}
				}
				results = finalRes;
			}
			
			rank = 0;
			for (GoogleResult res: results){
				rank++;
				System.out.println(rank + ") " + res.getTitle() + "(" + res.getRank() + " - " + res.getNewWeight() + ")");
			}
			
		}
	}

}
