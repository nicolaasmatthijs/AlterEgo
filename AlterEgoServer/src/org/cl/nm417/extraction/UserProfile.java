package org.cl.nm417.extraction;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.data.Document;
import org.cl.nm417.data.DocumentFrequency;
import org.cl.nm417.data.Profile;
import org.cl.nm417.data.Sentence;
import org.cl.nm417.data.SentenceItem;
import org.cl.nm417.data.Unigram;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.xmlparser.DataParser;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

public class UserProfile {

	private static IDictionary dict = null;
	private static final Pattern pattern = Pattern.compile("\\s+");
	
	private static int metakeywords = 0;
	private static int metadescription = 0;
	private static int terms = 0;
	private static int titles = 0;
	private static int plaintexts = 0;
	private static int ccparses = 0;
	private static int total = 0;
	
	public static ArrayList<Unigram> extractUnigramProfile(DataParser documents, boolean usePlaintext, 
			boolean useMetakeywords, boolean useMetadescription, boolean useTitle, boolean useTerms,
			boolean useCCParse, int plaintextW, int metakeywordsW, int metadescriptionW, 
			int titleW, int termsW, int CCParseW){
		
		HashMap<String, Unigram> map = new HashMap<String, Unigram>();
		ArrayList<String> arlUrl = new ArrayList<String>();
		
		try {
			openDictionary();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		if ((Boolean)AlterEgo.config.get("useRelativeW")){
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
			String[] splPT = pattern.split(d.getPlaintext());
			for (String s: splPT){
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
				for (Sentence s: d.getParsed()){
					String phrase = "";
					for (SentenceItem item: s.getSentence()){
						if (item.getPOS().startsWith("NN")){
							phrase += item.getWord() + " ";
						} else if (phrase.length() != 0) {
							phrases.add(phrase);
							phrase = "";
						}
					}
					if (phrase.length() != 0){
						phrases.add(phrase);
					}
				}
				map = addUnigrams(useCCParse, CCParseW, map, arlUrl, d, phrases, ccparses);
			}
			
			if ((Boolean)AlterEgo.config.get("excludeDuplicate")){
				arlUrl.add(d.getUrl());
			}
			
		}
		
		ArrayList<DocumentFrequency> freq = new ArrayList<DocumentFrequency>();
		if ((Boolean)AlterEgo.config.get("googlengram") || ((String)AlterEgo.config.get("weighting")).equals("tfidf")){
			//Get IDFs
			ArrayList<String> words = new ArrayList<String>();
			for (String s: map.keySet()){
				words.add(s);
			}
			freq = GoogleSearch.getNumberOfResults(words);
		}
		
		if ((Boolean)AlterEgo.config.get("googlengram")){
			int limit = (Integer)AlterEgo.config.get("googlengramW");
			HashMap<String, Unigram> newMap = new HashMap<String, Unigram>();
			for (String s: map.keySet()){
				double ni = getNumberOfResults(s, freq);
				if (ni >= limit){
					newMap.put(s, map.get(s));
				}
			}
			map = newMap;
		}
		
		if (((String)AlterEgo.config.get("weighting")).equals("tfidf")){	
			
			for (String s: map.keySet()){
				double ni = Math.log(getNumberOfResults(s, freq));
				Unigram u = map.get(s);
				u.setWeight(u.getWeight() / Math.log(ni));	
			}
			
		}
		
		ArrayList<Unigram> arl = new ArrayList<Unigram>();
		for (Unigram u: map.values()){
			arl.add(u);
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
		
		return arl;
		
	}
	
	private static void extractStatistics(DataParser documents, boolean useMetakeywords, boolean useMetadescription,
			boolean useTitle, boolean useTerms, boolean usePlainText, boolean useCCParse) {
		
		metakeywords = 0;
		metadescription = 0;
		terms = 0;
		titles = 0;
		plaintexts = 0;
		ccparses = 0;
		total = 0;
		
		for (Document d: documents.getDocuments()){
			HashMap<String, Unigram> arl = new HashMap<String, Unigram>();
			
			//Meta keywords
			arl = addUnigrams(useMetakeywords, 1, arl, new ArrayList<String>(), d, d.getMetakeywords(), 1);
			metakeywords += arl.size();
			
			//Meta description
			arl = new HashMap<String, Unigram>();
			ArrayList<String> desc = new ArrayList<String>();
			String[] splMD = pattern.split(d.getMetadescription());
			for (String s: splMD){
				desc.add(s);
			}
			arl = addUnigrams(useMetadescription, 1, arl, new ArrayList<String>(), d, desc, 1);
			metadescription += arl.size();
			
			//Title
			ArrayList<String> title = new ArrayList<String>();
			String[] splT = pattern.split(d.getTitle());
			for (String s: splT){
				title.add(s);
			}
			arl = new HashMap<String, Unigram>();
			arl = addUnigrams(useTitle, 1, arl, new ArrayList<String>(), d, title, 1);
			titles += arl.size();
			
			//Plaintext
			ArrayList<String> plain = new ArrayList<String>();
			String[] splPT = pattern.split(d.getPlaintext());
			for (String s: splPT){
				plain.add(s);
			}
			arl = new HashMap<String, Unigram>();
			arl = addUnigrams(usePlainText, 1, arl, new ArrayList<String>(), d, plain, 1);
			plaintexts += arl.size();
			
			//Terms
			arl = new HashMap<String, Unigram>();
			arl = addUnigrams(useTerms, 1, arl, new ArrayList<String>(), d, d.getTerms(), 1);
			terms += arl.size();
			
			//CCParse
			if (useCCParse){
				ArrayList<String> phrases = new ArrayList<String>();
				for (Sentence s: d.getParsed()){
					for (SentenceItem item: s.getSentence()){
						if (item.getPOS().startsWith("NN")){
							phrases.add(item.getWord());
						} 
					}
				}
				arl = new HashMap<String, Unigram>();
				arl = addUnigrams(useCCParse, 1, arl, new ArrayList<String>(), d, phrases, 1);
				ccparses += arl.size();
			}
			
			total = metakeywords + metadescription + titles + plaintexts + terms + ccparses;
			
		}
		
		System.out.println("Metakeywords => " + metakeywords);
		System.out.println("Metadescription => " + metadescription);
		System.out.println("Terms => " + terms);
		System.out.println("Title => " + titles);
		System.out.println("Plaintext => " + plaintexts);
		System.out.println("CCParse => " + ccparses);
		
	}

	private static HashMap<String, Unigram> addUnigrams(boolean use, int weight, HashMap<String, Unigram> map, 
			ArrayList<String> arlUrl, Document d, ArrayList<String> strings, int relative){
		if (use && !arlUrl.contains(d.getUrl())){
			for (String s: strings){
				if (s != null){
					if ((Boolean)AlterEgo.config.get("split")){
						String[] split = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
						for(String kw: split){
							if (!kw.equals(null) && kw.length() > 1){
								if ((Boolean)AlterEgo.config.get("posAll") || 
										(Boolean)AlterEgo.config.get("googlengram") ||
										((Boolean)AlterEgo.config.get("posNoun") && inDictionary(kw, POS.NOUN)) || 
										((Boolean)AlterEgo.config.get("posVerb") && inDictionary(kw, POS.VERB)) || 
										((Boolean)AlterEgo.config.get("posAdjective") && inDictionary(kw, POS.ADJECTIVE)) || 
										((Boolean)AlterEgo.config.get("posAdverb") && inDictionary(kw, POS.ADVERB))){
									double w = weight;
									if ((Boolean)AlterEgo.config.get("useRelativeW")){
										w = Math.log1p(w / relative * total);
									}
									if (map.containsKey(kw)){
										Unigram u = map.get(kw);
										u.setWeight(u.getWeight() + w);
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
						s = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
						if (!s.equals(null) && s.length() > 1){
							if ((Boolean)AlterEgo.config.get("posAll") || 
									(Boolean)AlterEgo.config.get("googlengram") ||
									((Boolean)AlterEgo.config.get("posNoun") && inDictionary(s, POS.NOUN)) || 
									((Boolean)AlterEgo.config.get("posVerb") && inDictionary(s, POS.VERB)) || 
									((Boolean)AlterEgo.config.get("posAdjective") && inDictionary(s, POS.ADJECTIVE)) || 
									((Boolean)AlterEgo.config.get("posAdverb") && inDictionary(s, POS.ADVERB))){
								double w = weight;
								if ((Boolean)AlterEgo.config.get("useRelativeW")){
									w = Math.log1p(w / relative * total);
								}
								if (map.containsKey(s)){
									Unigram u = map.get(s);
									u.setWeight(u.getWeight() + w);
								} else {
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
	
	private static void openDictionary() throws IOException { 
		     
		// construct the URL to the Wordnet dictionary directory 
		String path = "/Users/nicolaas/Desktop/AlterEgo/Java/WordNet-3.0/dict"; 
	    URL url = new URL("file", null, path); 
		     
	     // construct the dictionary object and open it 
	     dict = new Dictionary(url); 
	     dict.open(); 
		 
	} 
	
	private static boolean inDictionary(String term, POS pos){
		IIndexWord idxWord = dict.getIndexWord(term, pos); 
	    if (idxWord == null){
	    	return false;
	    }
	    return true;
	}

	public static ArrayList<Unigram> extractBM25Profile(DataParser documents,
			Profile profile, boolean usePlaintext, boolean useMetakeywords, 
			boolean useMetadescription, boolean useTitle, boolean useTerms, boolean useCCParse) {
		
		//Get IDFs
		ArrayList<String> words = new ArrayList<String>();
		for (Unigram u: profile.getUnigrams()){
			words.add(u.getText().toLowerCase());
		}
		ArrayList<DocumentFrequency> freq = GoogleSearch.getNumberOfResults(words);
		System.out.println("Got IDFs");
		
		Pattern pattern = Pattern.compile("\\s+");
		
		int i = 0;
		//HashMap<Document, ArrayList<String>> docs = new HashMap<Document, ArrayList<String>>();
		HashMap<String, Long> docs = new HashMap<String, Long>();
		for (Document d: documents.getDocuments()){
			
			i++;
			System.out.println("Doing document " + i + " of " + documents.getDocuments().size());
			
			ArrayList<String> allWords = new ArrayList<String>();
			//Metakeywords
			if (useMetakeywords){
				for (String s: d.getMetakeywords()){
					String[] spl = pattern.split(s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase());
					for (String sp: spl){
						if (!allWords.contains(sp)){
							allWords.add(sp);
						}
					}
				}
			}
			//Metadescription
			if (useMetadescription){
				String[] spl = pattern.split(d.getMetadescription());
				for (String s: spl){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.contains(toAdd)){
						allWords.add(toAdd);
					}
				}
			}
			//Plaintext
			if (usePlaintext){
				String[] spl = pattern.split(d.getPlaintext());
				for (String s: spl){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.contains(toAdd)){
						allWords.add(toAdd);
					}
				}
			}
			//Title
			if (useTitle){
				String[] spl = pattern.split(d.getTitle());
				for (String s: spl){
					String toAdd = s.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
					if (!allWords.contains(toAdd)){
						allWords.add(toAdd);
					}
				}
			}
			//Terms
			if (useTerms){
				for (String s: d.getTerms()){
					String[] spl = pattern.split(s);
					for (String sp: spl){
						String toAdd = sp.replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
						if (!allWords.contains(toAdd)){
							allWords.add(toAdd);
						}
					}
				}
			}
			//CC Parse Results
			if (useCCParse){
				for (Sentence s: d.getParsed()){
					for (SentenceItem item: s.getSentence()){
						if (item.getPOS().startsWith("NN")){
							String toAdd = item.getWord().replaceAll("[.,-/\"':;?()><=ÐÈÝ|_!]", "").toLowerCase();
							if (!allWords.contains(toAdd)){
								allWords.add(toAdd);
							}
						}
					}
				}
			}
			
			for (String s: allWords){
				if (docs.containsKey(s)){
					docs.put(s, docs.get(s) + 1);
				}
			}
			
			//docs.put(d, allWords);
			
		}
		
		i = 0;
		
		for (Unigram u: profile.getUnigrams()){
		
			i++;
			System.out.println("Doing " + i + " out of " + profile.getUnigrams().size());
			
			// IDF of "the"
			double N = 220680773.00;
			// Changed original formula to have log of document frequency
			double ni = Math.log(getNumberOfResults(u.getText().toLowerCase(), freq));
			double ri = 0;
			if (docs.containsKey(u.getText().toLowerCase())){
				ri = docs.get(u.getText().toLowerCase());
			}
			double R = documents.getDocuments().size();
			
			/* ArrayList<String> arlUrl = new ArrayList<String>();
			for (Document d: documents.getDocuments()){
				
				boolean exists = false;
				
				//Prevent the same URL being used again
				if ((Boolean)AlterEgo.config.get("excludeDuplicate") == false ||
						((Boolean)AlterEgo.config.get("excludeDuplicate") == true && !arlUrl.contains(d.getUrl()))){
					
					ArrayList<String> allWords = docs.get(d);
					if (allWords.contains(u.getText().toLowerCase())){
						exists = true;
					}
					
					arlUrl.add(d.getUrl());
				
				}
				
				//If it exists
				if (exists){
					ri++;
				}
				
			} */
			
			double weight = Math.log(((ri + 0.5)*(N - ni + 0.5))/((ni + 0.5)*(R-ri+0.5)));
			u.setWeight(weight);
			
		}
		
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

	private static double getNumberOfResults(String term, ArrayList<DocumentFrequency> freq) {
		for (DocumentFrequency df: freq){
			if (df.getTerm().equalsIgnoreCase(term)){
				return df.getFrequency();
			}
		}
		return 220680773.0;
	}
	
}
