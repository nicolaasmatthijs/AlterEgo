/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.profile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.data.Document;
import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.xmlparser.DataParser;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

/**
 * Class responsible for generating the different types of user profiles
 */
public class UserProfile {

	private static IDictionary dict = null;
	private static final Pattern pattern = Pattern.compile("\\s+");
	
	// Number of metadata keywords in the user's browsing history
	private static int metakeywords = 0;
	// Number of metadata description unigrams in the user's browsing history
	private static int metadescription = 0;
	// Number of extracted terms in the user's browsing history
	private static int terms = 0;
	// Number of title unigrams in the user's browsing history
	private static int titles = 0;
	// Number of plain text unigrams in the user's browsing history
	private static int plaintexts = 0;
	// Number of extracted noun phrase N-Grams in the user's browsing history
	private static int ccparses = 0;
	// Total number of terms in the user's browsing history
	private static int total = 0;
	
	// Variable keeping track of whether the dictionary has already been loaded
	private static boolean isOpen = false;
	
	/**
	 * Generate a Term Frequency based profile for a user given a browsing
	 * history
	 * @param documents				User's browsing history
	 * @param usePlaintext			Whether plain text should be used
	 * @param useMetakeywords		Whether metadata keywords should be used
	 * @param useMetadescription	Whether metadata description should be used
	 * @param useTitle				Whether title should be used
	 * @param useTerms				Whether extracted terms should be used
	 * @param useCCParse			Whether extracted noun phrases should be used
	 * @param plaintextW			Weight for plain text unigrams
	 * @param metakeywordsW			Weight for metadata keyword N-grams
	 * @param metadescriptionW		Weight for metadata description unigrams
	 * @param titleW				Weight for title unigrams
	 * @param termsW				Weight for extracted terms N-Grams
	 * @param CCParseW				Weight for extracted noun phrases N-Grams
	 * @return						A list of weights and associated frequencies
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Unigram> extractUnigramProfile(DataParser documents, boolean usePlaintext, 
			boolean useMetakeywords, boolean useMetadescription, boolean useTitle, boolean useTerms,
			boolean useCCParse, int plaintextW, int metakeywordsW, int metadescriptionW, 
			int titleW, int termsW, int CCParseW){
		
		HashMap<String, Unigram> map = new HashMap<String, Unigram>();
		ArrayList<String> arlUrl = new ArrayList<String>();
		
		try {
			if (!isOpen){
				openDictionary();
				isOpen = true;
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		if (!AlterEgo.config.containsKey("statistics") && (Boolean)AlterEgo.config.get("useRelativeW")){
			extractStatistics(documents, useMetakeywords, useMetadescription, useTitle, useTerms, usePlaintext, useCCParse);
		}
		
		for (Document d: documents.getDocuments()){
			
			//Meta keywords
			map = addUnigrams(useMetakeywords, metakeywordsW, map, arlUrl, d, d.getMetakeywords(), metakeywords);
			
			//Meta description
			ArrayList<String> desc = new ArrayList<String>();
			String[] splMD = pattern.split(d.getMetadescription());
			for (String s: splMD){
				desc.add(s);
			}
			map = addUnigrams(useMetadescription, metadescriptionW, map, arlUrl, d, desc, metadescription);
			
			//Plaintext
			ArrayList<String> plain = new ArrayList<String>();
			for (String s: d.getPlaintext()){
				plain.add(s);
			}
			map = addUnigrams(usePlaintext, plaintextW, map, arlUrl, d, plain, plaintexts);
			
			//Title
			ArrayList<String> title = new ArrayList<String>();
			String[] splT = pattern.split(d.getTitle());
			for (String s: splT){
				title.add(s);
			}
			map = addUnigrams(useTitle, titleW, map, arlUrl, d, title, titles);
			
			//Terms
			map = addUnigrams(useTerms, termsW, map, arlUrl, d, d.getTerms(), terms);
			
			//CC Parse Results
			if (useCCParse){
				ArrayList<String> phrases = new ArrayList<String>();
				for (ArrayList<String> s: d.getParsed()){
					for (String word: s){
						phrases.add(word);
					}
				}
				map = addUnigrams(useCCParse, CCParseW, map, arlUrl, d, phrases, ccparses);
			}
			
			// Prevent that duplicate URLs are included
			if ((Boolean)AlterEgo.config.get("excludeDuplicate")){
				arlUrl.add(d.getUrl());
			}
			
		}
		
		HashMap<String, Double> freq = new HashMap<String, Double>();
		if ((Boolean)AlterEgo.config.get("googlengram") || ((String)AlterEgo.config.get("weighting")).equals("tfidf")){
			//Get IDFs
			if (AlterEgo.config.get("ngrams") == null){
				ArrayList<String> words = new ArrayList<String>();
				for (String s: map.keySet()){
					words.add(s);
				}
				freq = GoogleSearch.getNumberOfResults(words);
			} else {
				freq = (HashMap<String, Double>) AlterEgo.config.get("ngrams");
			}
		}
		
		// Google N-Gram based filtering
		if ((Boolean)AlterEgo.config.get("googlengram")){
			int limit = (Integer)AlterEgo.config.get("googlengramW");
			HashMap<String, Unigram> newMap = new HashMap<String, Unigram>();
			for (String s: map.keySet()){
				if (freq.containsKey(s.toLowerCase())){
					if (freq.get(s.toLowerCase()) >= limit){
						newMap.put(s, map.get(s));
					}
				} 
			}
			map = newMap;
		}
		
		// Divide by the Document Frequencies if TF-IDF weighting is required
		if (((String)AlterEgo.config.get("weighting")).equals("tfidf")){	
			
			for (String s: map.keySet()){
				Unigram u = map.get(s);
				double ni = 220680773.0;
				if (freq.containsKey(s.toLowerCase())){
					ni = freq.get(s.toLowerCase());
				}
				u.setWeight(u.getWeight() / Math.log(ni));		
			}
			
		}
		
		ArrayList<Unigram> arl = new ArrayList<Unigram>();
		for (Unigram u: map.values()){
			arl.add(u);
		}
		
		// Sort the profile terms based on weight
		Collections.sort(arl, new Comparator<Unigram>(){
			 
            public int compare(Unigram u1, Unigram u2) {
            	if (u1.getWeight() > u2.getWeight()){
            		return -1;
            	} else {
            		return 1;
            	}
            }
 
        });
		
		return arl;
		
	}
	
	/**
	 * Find the total number of terms available in the different input data sources
	 * over the user's entire browsing history
	 * @param documents				User's browsing history
	 * @param useMetakeywords		Whether metadata keywords should be used
	 * @param useMetadescription	Whether metadata description should be used
	 * @param useTitle				Whether tile should be used
	 * @param useTerms				Whether extracted terms should be used
	 * @param usePlaintext			Whether plain text should be used
	 * @param useCCParse			Whether extracted noun phrases should be used
	 */
	public static void extractStatistics(DataParser documents, boolean useMetakeywords, boolean useMetadescription,
			boolean useTitle, boolean useTerms, boolean usePlainText, boolean useCCParse) {
		
		metakeywords = 0;
		metadescription = 0;
		terms = 0;
		titles = 0;
		plaintexts = 0;
		ccparses = 0;
		total = 0;
		
		for (Document d: documents.getDocuments()){
			
			//Meta keywords
			for (String s: d.getMetakeywords()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
						metakeywords++;
						total++;
					} else if (!(Boolean)AlterEgo.config.get("posNoun")){
						metakeywords++;
						total++;
					}
				}
			}
			
			//Meta description
			String[] splMD = pattern.split(d.getMetadescription());
			for (String s: splMD){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
						metadescription++;
						total++;
					} else if (!(Boolean)AlterEgo.config.get("posNoun")){
						metadescription++;
						total++;
					}
				}
			}
			
			//Plaintext
			for (String s: d.getPlaintext()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
						plaintexts++;
						total++;
					} else if (!(Boolean)AlterEgo.config.get("posNoun")){
						plaintexts++;
						total++;
					}
				}
			}
			
			//Title
			String[] splT = pattern.split(d.getTitle());
			for (String s: splT){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
						titles++;
						total++;
					} else if (!(Boolean)AlterEgo.config.get("posNoun")){
						titles++;
						total++;
					}
				}
			}
			
			//Terms
			for (String s: d.getTerms()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
						terms++;
						total++;
					} else if (!(Boolean)AlterEgo.config.get("posNoun")){
						terms++;
						total++;
					}
				}
			}
			
			//CC Parse Results
			for (ArrayList<String> s: d.getParsed()){
				for (String word: s){
					String[] split = pattern.split(word.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
					for (String spl: split){
						if ((Boolean)AlterEgo.config.get("posNoun") && inDictionary(spl, POS.NOUN)){
							ccparses++;
							total++;
						} else if (!(Boolean)AlterEgo.config.get("posNoun")){
							ccparses++;
							total++;
						}
					}
				}
			}
			
		}
		
		System.out.println("Metakeywords => " + metakeywords);
		System.out.println("Metadescription => " + metadescription);
		System.out.println("Terms => " + terms);
		System.out.println("Title => " + titles);
		System.out.println("Plaintext => " + plaintexts);
		System.out.println("CCParse => " + ccparses);
		
	}
	
	/**
	 * Collect all terms out of a user's browsing history and find which ones are in
	 * the WordNet dictionary as a noun
	 * @param documents		User's browsing history
	 * @return				Map with all terms found in the user's browsing history 	
	 * 						that were recognized as a noun	
	 */
	public static HashMap<String, String> getWordsInDict(DataParser documents){
		
		try {
			if (!isOpen){
				openDictionary();
				isOpen = true;
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		Long start = new Date().getTime();
		
		HashMap<String, String> map = new HashMap<String, String>();
		for (Document d: documents.getDocuments()){
			
			//Meta keywords
			for (String s: d.getMetakeywords()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
						map.put(spl, "");
					}
				}
			}
			
			//Meta description
			String[] splMD = pattern.split(d.getMetadescription());
			for (String s: splMD){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
						map.put(spl, "");
					}
				}
			}
			
			//Plaintext
			for (String s: d.getPlaintext()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
						map.put(spl, "");
					}
				}
			}
			
			//Title
			String[] splT = pattern.split(d.getTitle());
			for (String s: splT){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
						map.put(spl, "");
					}
				}
			}
			
			//Terms
			for (String s: d.getTerms()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
						map.put(spl, "");
					}
				}
			}
			
			//CC Parse Results
			for (ArrayList<String> s: d.getParsed()){
				for (String word: s){
					String[] split = pattern.split(word.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
					for (String spl: split){
						if (!map.containsKey(spl) && inDictionary(spl, POS.NOUN)){
							map.put(spl, "");
						}
					}
				}
			}
			
		}
		
		Long end = new Date().getTime();
		System.out.println("Got dictionary entries in " + (end - start) + " ms");
		
		return map;
		
	}
	
	/**
	 * Collect all terms out of a user's browsing history and get frequency counts for them
	 * using the Google N-Gram corpus
	 * @param documents		User's browsing history
	 * @return				Map with words and frequency counts for all terms found in the
	 * 						user's browsing history 		
	 */
	public static HashMap<String, Double> getGoogleNGrams(DataParser documents){
		
		Long start = new Date().getTime();
		
		HashMap<String, String> map = new HashMap<String, String>();
		int i = 0;
		for (Document d: documents.getDocuments()){
			
			//Meta keywords
			for (String s: d.getMetakeywords()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl)){
						map.put(spl, "");
					}
				}
			}
			
			//Meta description
			String[] splMD = pattern.split(d.getMetadescription());
			for (String s: splMD){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl)){
						map.put(spl, "");
					}
				}
			}
			
			//Plaintext
			for (String s: d.getPlaintext()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl)){
						map.put(spl, "");
					}
				}
			}
			
			//Title
			String[] splT = pattern.split(d.getTitle());
			for (String s: splT){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl)){
						map.put(spl, "");
					}
				}
			}
			
			//Terms
			for (String s: d.getTerms()){
				String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
				for (String spl: split){
					if (!map.containsKey(spl)){
						map.put(spl, "");
					}
				}
			}
			
			//CC Parse Results
			for (ArrayList<String> s: d.getParsed()){
				for (String word: s){
					String[] split = pattern.split(word.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
					for (String spl: split){
						if (!map.containsKey(spl)){
							map.put(spl, "");
						}
					}
				}
			}
			
			i++;
			System.out.println("Processed " + i + " of " + documents.getDocuments().size());
			
		}
		
		Long end = new Date().getTime();
		
		ArrayList<String> list = new ArrayList<String>();
		for (String s: map.keySet()){
			list.add(s);
		}
		// Get frequencies for all of these terms
		HashMap<String, Double> ngrams = GoogleSearch.getNumberOfResults(list);
		System.out.println("Got Google NGram entries in " + (end - start) + " ms");
		
		return ngrams;
		
	}

	/**
	 * Add all of the terms in a data source of a document to the provisional
	 * list of profile terms
	 * @param use		Whether the current data source should be used
	 * @param weight	Weight set for this input data source
	 * @param map		List of unigrams collected for this profile so far
	 * @param arlUrl	List of URLs processed so far
	 * @param d			Current document
	 * @param strings	All terms present in this document's data source
	 * @param relative	Whether relative weighting should be used or not
	 * @return			List of unigrams collected for this profile so far
	 */
	@SuppressWarnings("unchecked")
	private static HashMap<String, Unigram> addUnigrams(boolean use, int weight, HashMap<String, Unigram> map, 
			ArrayList<String> arlUrl, Document d, ArrayList<String> strings, int relative){
		HashMap<String, String> dict = (HashMap<String, String>)AlterEgo.config.get("dict");
		if (use && !arlUrl.contains(d.getUrl())){
			for (String s: strings){
				if (s != null){
					// If multiword terms have to be split
					if ((Boolean)AlterEgo.config.get("split")){
						// Remove punctuation
						String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
						for(String kw: split){
							if (!kw.equals(null) && kw.length() > 1){
								boolean inDict = false;
								// Check whether the term should be filtered out based on its Part of Speech
								if (((Boolean)AlterEgo.config.get("posNoun"))){
									if (dict != null){
										if (dict.containsKey(kw)){
											inDict = true;
										}
									} else {
										inDict = inDictionary(kw, POS.NOUN);
									}
								}
								// Check whether the term should be filtered out
								if ((Boolean)AlterEgo.config.get("posAll") || 
										(Boolean)AlterEgo.config.get("googlengram") ||
										((Boolean)AlterEgo.config.get("posNoun") && inDict) || 
										((Boolean)AlterEgo.config.get("posVerb") && inDictionary(kw, POS.VERB)) || 
										((Boolean)AlterEgo.config.get("posAdjective") && inDictionary(kw, POS.ADJECTIVE)) || 
										((Boolean)AlterEgo.config.get("posAdverb") && inDictionary(kw, POS.ADVERB))){
									double w = weight;
									// Apply relative weighting if required
									if ((Boolean)AlterEgo.config.get("useRelativeW")){
										w = Math.log1p(w / relative * total);
									}
									// If the current term showed up in a previous document
									if (map.containsKey(kw)){
										Unigram u = map.get(kw);
										u.setWeight(u.getWeight() + w);
									// Unseen term
									} else {
										Unigram u = new Unigram();
										u.setText(kw);
										u.setWeight(w);
										map.put(kw, u);
									}
								}
							}
						}
					} else {
						// Remove punctuation
						s = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
						if (!s.equals(null) && s.length() > 1){
							boolean inDict = false;
							// Check whether the term should be filtered out based on its Part of Speech
							if (((Boolean)AlterEgo.config.get("posNoun"))){
								if (dict != null){
									if (dict.containsKey(s)){
										inDict = true;
									}
								} else {
									inDict = inDictionary(s, POS.NOUN);
								}
							}
							// Check whether the term should be filtered out
							if ((Boolean)AlterEgo.config.get("posAll") || 
									(Boolean)AlterEgo.config.get("googlengram") ||
									((Boolean)AlterEgo.config.get("posNoun") && inDict) || 
									((Boolean)AlterEgo.config.get("posVerb") && inDictionary(s, POS.VERB)) || 
									((Boolean)AlterEgo.config.get("posAdjective") && inDictionary(s, POS.ADJECTIVE)) || 
									((Boolean)AlterEgo.config.get("posAdverb") && inDictionary(s, POS.ADVERB))){
								double w = weight;
								// Apply relative weighting if required
								if ((Boolean)AlterEgo.config.get("useRelativeW")){
									w = Math.log1p(w / relative * total);
								}
								// If the current term showed up in a previous document
								if (map.containsKey(s)){
									Unigram u = map.get(s);
									u.setWeight(u.getWeight() + w);
								} else {
								// Unseen term
									Unigram u = new Unigram();
									u.setText(s);
									u.setWeight(w);
									map.put(s, u);
								}
							}
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * Open the WordNet dictionary using the MIT WordNet API
	 * @throws IOException
	 */
	private static void openDictionary() throws IOException { 
		     
		// construct the URL to the Wordnet dictionary directory 
		String path = ConfigLoader.getConfig().getProperty("dictfile"); 
	    URL url = new URL("file", null, path); 
		     
	     // construct the dictionary object and open it 
	     dict = new Dictionary(url); 
	     dict.open(); 
		 
	} 
	
	/**
	 * Checks whether a given word is in the dictionary as the given Part of Speech
	 * @param term	Word
	 * @param pos	Given Part of Speech
	 * @return		Is in the dictionary with the provided Part of Speech
	 */
	private static boolean inDictionary(String term, POS pos){
		IIndexWord idxWord = dict.getIndexWord(term, pos); 
	    if (idxWord == null){
	    	return false;
	    }
	    return true;
	}

	/**
	 * Generate a user profile using BM25 weighting
	 * @param documents				User's browsing history
	 * @param profile				User's preliminary profile (no weighting)
	 * @param usePlaintext			Whether plain text should be used
	 * @param useMetakeywords		Whether metadata keywords should be used
	 * @param useMetadescription	Whether metadata description should be used
	 * @param useTitle				Whether tile should be used
	 * @param useTerms				Whether extracted terms should be used
	 * @param useCCParse			Whether extracted noun phrases should be used
	 * @return						Full generated user profile
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Unigram> extractBM25Profile(DataParser documents,
			Profile profile, boolean usePlaintext, boolean useMetakeywords, 
			boolean useMetadescription, boolean useTitle, boolean useTerms, boolean useCCParse) {
		
		//Get IDFs for all terms in the profile
		HashMap<String, Double> freq = new HashMap<String, Double>();
		if (AlterEgo.config.get("ngrams") == null){
			ArrayList<String> words = new ArrayList<String>();
			for (Unigram u: profile.getUnigrams()){
				words.add(u.getText().toLowerCase());
			}
			freq = GoogleSearch.getNumberOfResults(words);
		} else {
			freq = (HashMap<String, Double>) AlterEgo.config.get("ngrams");
		}	
		System.out.println("Got IDFs");
		
		Pattern pattern = Pattern.compile("\\s+");
		
		int i = 0;
		HashMap<String, Double> docs = new HashMap<String, Double>();
		for (Document d: documents.getDocuments()){
			
			i++;
			System.out.println("Doing document " + i + " of " + documents.getDocuments().size());
			
			HashMap<String, String> allWords = new HashMap<String, String>();
			
			//Metakeywords
			if (useMetakeywords){
				for (String s: d.getMetakeywords()){
					String[] spl = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
					for (String sp: spl){
						if (!allWords.containsKey(sp)){
							allWords.put(sp,"");
						}
					}
				}
			}
			//Metadescription
			if (useMetadescription){
				String[] spl = pattern.split(d.getMetadescription());
				for (String s: spl){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.containsKey(toAdd)){
						allWords.put(toAdd,"");
					}
				}
			}
			//Plaintext
			if (usePlaintext){
				for (String s: d.getPlaintext()){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.containsKey(toAdd)){
						allWords.put(toAdd,"");
					}
				}
			}
			//Title
			if (useTitle){
				String[] spl = pattern.split(d.getTitle());
				for (String s: spl){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.containsKey(toAdd)){
						allWords.put(toAdd,"");
					}
				}
			}
			//Terms
			if (useTerms){
				for (String s: d.getTerms()){
					String[] spl = pattern.split(s);
					for (String sp: spl){
						String toAdd = sp.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
						if (!allWords.containsKey(toAdd)){
							allWords.put(toAdd,"");
						}
					}
				}
			}
			
			//CC Parse Results
			if (useCCParse){
				for (ArrayList<String> s: d.getParsed()){
					for (String word: s){
						if (!allWords.containsKey(word)){
							allWords.put(word,"");
						}
					}
				}
			}
			
			for (String s: allWords.keySet()){
				if (docs.containsKey(s)){
					docs.put(s, docs.get(s) + 1);
				} else {
					docs.put(s, 1.0);
				}
			}
			
		}
		
		i = 0;
		
		for (Unigram u: profile.getUnigrams()){
		
			i++;
			
			// DF of "the", the most frequent word on the web
			double N = 220680773.00;
			
			// Changed original formula to have log of document frequency
			double ni = 220680773.00;
			if (freq.containsKey(u.getText().toLowerCase())){
				ni = freq.get(u.getText().toLowerCase());
			}
			ni = Math.log(ni);
			double ri = 0;
			// Number of documents in the browsing history that contain the current term
			if (docs.containsKey(u.getText().toLowerCase())){
				ri = docs.get(u.getText().toLowerCase());
			}
			// Number of documents in the user's browsing history
			double R = documents.getDocuments().size();
			
			// Calculate weight according to revised formula
			double weight = Math.log(((ri + 0.5)*(N - ni + 0.5))/((ni + 0.5)*(R-ri+0.5)));
			u.setWeight(weight);
			
		}
		
		// Sort the profile terms based on weight
		Collections.sort(profile.getUnigrams(), new Comparator<Unigram>(){
			 
            public int compare(Unigram u1, Unigram u2) {
            	if (u1.getWeight() > u2.getWeight()){
            		return -1;
            	} else {
            		return 1;
            	}
            }
 
        });
		
		return profile.getUnigrams();
		
	}
	
}
