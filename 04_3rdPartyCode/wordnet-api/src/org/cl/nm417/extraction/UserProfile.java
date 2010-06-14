package org.cl.nm417.extraction;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.cl.nm417.data.Document;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.xmlparser.DataParser;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

public class UserProfile {

	private static IDictionary dict = null;
	
	public static ArrayList<Unigram> extractUnigramProfile(DataParser documents, boolean usePlaintext, 
			boolean useMetakeywords, boolean useMetadescription, boolean useTitle, boolean useTerms){
		
		ArrayList<Unigram> arl = new ArrayList<Unigram>();
		ArrayList<String> arlUrl = new ArrayList<String>();
		
		try {
			openDictionary();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		if (useMetakeywords){
			int i = 0;
			for (Document d: documents.getDocuments()){
				i++;
				//System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
				if (!arlUrl.contains(d.getUrl())){
					for (String s: d.getMetakeywords()){
						s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
						//String[] split = s.replaceAll("[.,-/\"':;]", "").toLowerCase().split(" ");
						//for(String kw: split){
						if (!s.equals(null) && !s.equals("")){
							if (inDictionary(s, POS.NOUN)){
								Unigram u = containsTerm(arl, s);
								if (u != null){
									u.setWeight(u.getWeight() + 200);
								} else {
									u = new Unigram();
									u.setText(s);
									arl.add(u);
								}
							}
						}
					}
					arlUrl.add(d.getUrl());
				}
			}
		}
		
		arlUrl = new ArrayList<String>();
		if (useMetadescription){
			int i = 0;
			for (Document d: documents.getDocuments()){
				i++;
				System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
				if (!arlUrl.contains(d.getUrl())){	
					String[] split = d.getMetadescription().split("\\s+");
					for (String s: split){
						s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
						if (!s.equals(null) && !s.equals("")){
						if (inDictionary(s, POS.NOUN)){
							Unigram u = containsTerm(arl, s);
							if (u != null){
								u.setWeight(u.getWeight() + 100);
							} else {
								u = new Unigram();
								u.setText(s);
								arl.add(u);
							}
						}
						}
					}
					arlUrl.add(d.getUrl());
				}
			}
		}
		
		
		arlUrl = new ArrayList<String>();
		if (usePlaintext){
			int i = 0;
			for (Document d: documents.getDocuments()){
				i++;
				System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
				if (!arlUrl.contains(d.getUrl())){
					String[] split = d.getPlaintext().split("\\s+");
					for (String s: split){
						s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
						if (!s.equals(null) && !s.equals("")){
						if (inDictionary(s, POS.NOUN)){
							Unigram u = containsTerm(arl, s);
							if (u != null){
								u.setWeight(u.getWeight() + 1);
							} else {
								u = new Unigram();
								u.setText(s);
								arl.add(u);
							}
						}
						}
					}
					arlUrl.add(d.getUrl());
				}
			}
		}
		
		arlUrl = new ArrayList<String>();
		if (useTitle){
			int i = 0;
			for (Document d: documents.getDocuments()){
				i++;
				System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
				if (!arlUrl.contains(d.getUrl())){
					String[] split = d.getTitle().split("\\s+");
					for (String s: split){
						s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
						if (!s.equals(null) && !s.equals("")){
						if (inDictionary(s, POS.NOUN)){
							Unigram u = containsTerm(arl, s);
							if (u != null){
								u.setWeight(u.getWeight() + 20);
							} else {
								u = new Unigram();
								u.setText(s);
								arl.add(u);
							}
						}
						}
					}
					arlUrl.add(d.getUrl());
				}
			}
		}
		
		try {
		arlUrl = new ArrayList<String>();
		if (useTerms){
			int i = 0;
			for (Document d: documents.getDocuments()){
				i++;
				//System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
				if (!arlUrl.contains(d.getUrl())){
					for (int in = 0; in < d.getTerms().length(); in++){
						String[] split = d.getTerms().getString(in).replaceAll("[.,-/\"':;]", "").toLowerCase().split(" ");
						for (String s: split){
						if (!s.equals(null) && !s.equals("")){
						//if (inDictionary(s, POS.NOUN)){
							Unigram u = containsTerm(arl, s);
							if (u != null){
								u.setWeight(u.getWeight() + 50);
							} else {
								u = new Unigram();
								u.setText(s);
								arl.add(u);
							}
						//}
						}
						}
					}
					arlUrl.add(d.getUrl());
				}
			}
		}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		Collections.sort(arl, new Comparator<Unigram>(){
			 
            public int compare(Unigram u1, Unigram u2) {
            	if (u1.getWeight() > u2.getWeight()){
            		return -1;
            	} else {
            		return 1;
            	}
            }
 
        });
		
		int total = arl.size();
		for (int in = arl.size(); in >= 1; in--){
			Unigram u = arl.get(in - 1);
			System.out.println(total + ") " + u);
			total--;
		}
		
		return arl;
		
	}
	
	private static void openDictionary() throws IOException { 
		     
		     // construct the URL to the Wordnet dictionary directory 
		     String path = "/Users/nicolaas/Desktop/AlterEgo/Java/WordNet-3.0/dict"; 
		     URL url = new URL("file", null, path); 
		     
		     // construct the dictionary object and open it 
		     dict = new Dictionary(url); 
		     dict.open(); 
		 
		     // look up first sense of the word "dog" 
		     //IWordID wordID = idxWord.getWordIDs().get(0); 
		     //IWord word = dict.getWord(wordID); 
		     //System.out.println("Id = " + wordID); 
		     //System.out.println("Lemma = " + word.getLemma()); 
		     //System.out.println("Gloss = " + word.getSynset().getGloss()); 
		 
		  } 
	
	private static boolean inDictionary(String term, POS pos){
		IIndexWord idxWord = dict.getIndexWord(term, pos); 
	    //System.out.println(idxWord);
	    if (idxWord == null){
	    	return false;
	    } else {
	    	return true;
	    }
	}

	
	private static Unigram containsTerm(ArrayList<Unigram> arl, String term){
		for (Unigram uni: arl){
			if (uni.getText().toLowerCase().equals(term.toLowerCase())){
				return uni;
			}
		}
		return null;
	}

	public static ArrayList<Unigram> extractBM25Profile(DataParser documents,
			ArrayList<Unigram> profile, boolean usePlaintext, boolean useMetakeywords, 
			boolean useMetadescription, boolean useTitle, boolean useTerms) {
		
		for (Unigram u: profile){
			
			//http://googleblog.blogspot.com/2008/07/we-knew-web-was-big.html
			double N = 1000000000000.00;
			double ni = GoogleSearch.getNumberOfResults(u.getText());
			double ri = 0;
			double R = documents.getDocuments().size();
			
			boolean cont = true;
			
			ArrayList<String> arlUrl = new ArrayList<String>();
			if (useMetakeywords && cont){
				int i = 0;
				for (Document d: documents.getDocuments()){
					i++;
					if (!arlUrl.contains(d.getUrl())){
						for (String s: d.getMetakeywords()){
							s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
							if (s.equalsIgnoreCase(u.getText())){
								cont = false;
								ri++;
								break;
							}
						}
						arlUrl.add(d.getUrl());
					}
				}
			}
			
			arlUrl = new ArrayList<String>();
			if (useMetadescription && cont){
				int i = 0;
				for (Document d: documents.getDocuments()){
					i++;
					if (!arlUrl.contains(d.getUrl())){	
						String[] split = d.getMetadescription().split("\\s+");
						for (String s: split){
							s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
							if (s.equalsIgnoreCase(u.getText())){
								cont = false;
								ri++;
								break;
							}
							arlUrl.add(d.getUrl());
						}
					}
				}
			}
			
			
			arlUrl = new ArrayList<String>();
			if (usePlaintext && cont){
				int i = 0;
				for (Document d: documents.getDocuments()){
					i++;
					if (!arlUrl.contains(d.getUrl())){
						String[] split = d.getPlaintext().split("\\s+");
						for (String s: split){
							s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
							if (s.equalsIgnoreCase(u.getText())){
								cont = false;
								ri++;
								break;
							}
							arlUrl.add(d.getUrl());
						}
					}
				}
			}
			
			arlUrl = new ArrayList<String>();
			if (useTitle && cont){
				int i = 0;
				for (Document d: documents.getDocuments()){
					i++;
					System.out.println("Processing document " + i + " of " + documents.getDocuments().size());
					if (!arlUrl.contains(d.getUrl())){
						String[] split = d.getTitle().split("\\s+");
						for (String s: split){
							s = s.replaceAll("[.,-/\"':;]", "").toLowerCase();
							if (s.equalsIgnoreCase(u.getText())){
								cont = false;
								ri++;
								break;
							}
						}
						arlUrl.add(d.getUrl());
					}
				}
			}
			
			try {
				arlUrl = new ArrayList<String>();
				if (useTerms && cont){
					int i = 0;
					for (Document d: documents.getDocuments()){
						i++;
						if (!arlUrl.contains(d.getUrl())){
							for (int in = 0; in < d.getTerms().length(); in++){
								String[] split = d.getTerms().getString(in).replaceAll("[.,-/\"':;]", "").toLowerCase().split(" ");
								for (String s: split){
									if (s.equalsIgnoreCase(u.getText())){
										cont = false;
										ri++;
										break;
									}
								}
							}
							arlUrl.add(d.getUrl());
						}
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
			
			double weight = Math.log(((ri + 0.5)*(N - ni + 0.5))/((ni + 0.5)*(R-ri+0.5)));
			u.setWeight(weight);
			
		}
		
		Collections.sort(profile, new Comparator<Unigram>(){
			 
            public int compare(Unigram u1, Unigram u2) {
            	if (u1.getWeight() > u2.getWeight()){
            		return -1;
            	} else {
            		return 1;
            	}
            }
 
        });
		
		return profile;
	}
	
}
