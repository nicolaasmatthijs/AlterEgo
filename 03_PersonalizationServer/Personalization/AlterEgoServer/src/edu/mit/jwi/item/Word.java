/********************************************************************************
 * MIT Java Wordnet Interface (JWI)
 * Copyright (c) 2007-2008 Massachusetts Institute of Technology
 *
 * This is the non-commercial version of JWI.  This version may *not* be used
 * for commercial purposes.
 * 
 * This program and the accompanying materials are made available by the MIT
 * Technology Licensing Office under the terms of the MIT Java Wordnet Interface 
 * Non-Commercial License.  The MIT Technology Licensing Office can be reached 
 * at 617-253-6966 for further inquiry.
 *******************************************************************************/

package edu.mit.jwi.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Default implementation of the {@code IWord} interface.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5
 * @since 1.0.0, Nov. 16, 2007
 */
public class Word implements IWord {
	
    private final IWordID fWordID;
    private final ISynset fSynset;
    private final String fLemma;
    private final ISenseKey fSenseKey;
    private final AdjMarker fAdjMarker;
    private final int fLexID;
    private final List<IVerbFrame> fFrames;
    private final List<IWordID> allWords;
    private final Map<IPointer, List<IWordID>> wordMap;

    /**
	 * To create a noun or adverb
	 */
    public Word(ISynset synset, int number, String lemma, int lexID,
    		Map<IPointer, ? extends List<IWordID>> pointers) {
        this(synset, number, lemma, lexID, null, null, pointers);
    }

    /**
	 * To create a verb
	 */
    public Word(ISynset synset, int number, String lemma, int lexID, List<IVerbFrame> frames,
    		Map<IPointer, ? extends List<IWordID>> pointers) {
        this(synset, number, lemma, lexID, null, frames, pointers);
    }

    /**
	 * To create an adjective
	 */
    public Word(ISynset synset, int number, String lemma, int lexID, AdjMarker adjMarker,
    		Map<IPointer, ? extends List<IWordID>> pointers) {
        this(synset, number, lemma, lexID, adjMarker, null, pointers);
    }

    /**
	 * Full control
	 */
    public Word(ISynset synset, int number, String lemma, int lexID, AdjMarker adjMarker,
    		List<IVerbFrame> frames, Map<IPointer, ? extends List<IWordID>> pointers) {
    	
    	if(synset == null) throw new NullPointerException();
    	checkLexicalID(lexID);
    	checkWordNumber(number);
    	if(synset.getPOS() != POS.ADJECTIVE && adjMarker != null) throw new IllegalArgumentException();
    	
    	fSynset = synset;
        fWordID = new WordID(synset.getID(), number, lemma);
        fLemma = lemma;
        fLexID = lexID;
        fAdjMarker = adjMarker;
        fSenseKey = new SenseKey(fLemma, lexID, synset);
        
        Set<IWordID> hiddenSet = null;
        Map<IPointer, List<IWordID>> hiddenMap = null;
        // fill synset map
        if(pointers != null){
        	hiddenSet = new LinkedHashSet<IWordID>();
        	hiddenMap = new HashMap<IPointer, List<IWordID>>(pointers.size());
        	for(Entry<IPointer, ? extends List<IWordID>> entry : pointers.entrySet()){
        		if(entry.getValue() == null || entry.getValue().isEmpty()) continue;
        		hiddenMap.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<IWordID>(entry.getValue())));
        		hiddenSet.addAll(entry.getValue());
        	}
        }
        allWords = (hiddenSet != null && !hiddenSet.isEmpty()) ? Collections.unmodifiableList(new ArrayList<IWordID>(hiddenSet)) : Collections.<IWordID>emptyList();
        wordMap = (hiddenMap != null && !hiddenMap.isEmpty()) ? Collections.unmodifiableMap(hiddenMap) : Collections.<IPointer, List<IWordID>>emptyMap();
        fFrames = (frames == null || frames.isEmpty()) ? Collections.<IVerbFrame>emptyList() : Collections.unmodifiableList(new ArrayList<IVerbFrame>(frames));
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getLemma()
     */
    public String getLemma() {
        return fLemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getPartOfSpeech()
     */
    public POS getPOS() {
        return fWordID.getSynsetID().getPOS();
    }

    /* (non-Javadoc) @see edu.mit.wordnet.item.IHasID#getID() */
    public IWordID getID() {
        return fWordID;
    }
    
	/* (non-Javadoc) @see edu.mit.jwi.item.IWord#getLexicalID() */
	public int getLexicalID() {
		return fLexID;
	}
	
	/* (non-Javadoc) @see edu.mit.jwi.item.IWord#getSenseKey() */
	public ISenseKey getSenseKey() {
		return fSenseKey;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IWord#getSynset()
	 */
	public ISynset getSynset() {
		return fSynset;
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IWord#getRelatedMap()
	 */
	public Map<IPointer, List<IWordID>> getRelatedMap() {
		return wordMap;
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getRelatedWords(edu.mit.wordnet.core.data.IPointerType)
     */
    public List<IWordID> getRelatedWords(IPointer type) {
        List<IWordID> result = wordMap.get(type);
        return (result == null) ? Collections.<IWordID>emptyList() : result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getAllRelatedWords()
     */
    public List<IWordID> getRelatedWords() {
    	return (wordMap == null) ? Collections.<IWordID>emptyList() : allWords;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getVerbFrames()
     */
    public List<IVerbFrame> getVerbFrames() {
        return fFrames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWord#getAdjectiveMarker()
     */
    public AdjMarker getAdjectiveMarker() {
        return fAdjMarker;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (fWordID.getSenseNumber() == 0) {
            return "W-" + fWordID.getSynsetID().toString().substring(4) + "-?-"
                    + fLemma;
        } else {
            return "W-" + fWordID.getSynsetID().toString().substring(4) + "-"
                    + fWordID.getSenseNumber() + "-" + fLemma;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result;
        result = PRIME + fFrames.hashCode();
        result = PRIME * result + wordMap.hashCode();
        result = PRIME * result + fWordID.hashCode();
        result = PRIME * result + fLexID;
        result = PRIME * result + ((fAdjMarker == null) ? 0 : fAdjMarker.hashCode());
        return result;
    }
    


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
    	
    	// check nulls
        if (this == obj) return true;
        if (obj == null) return false;
        
        // check interface
        if (!(obj instanceof IWord)) return false;
        final IWord other = (IWord) obj;
        
        // check id
        if (fWordID == null) {
            if (other.getID() != null) return false;
        } else if (!fWordID.equals(other.getID())) return false;
        
        // check lexical id
        if(fLexID != other.getLexicalID()) return false;
        
        // check adjective marker
        if (fAdjMarker == null) {
            if (other.getAdjectiveMarker() != null) return false;
        } else if (!fAdjMarker.equals(other.getAdjectiveMarker())) return false;
        
        // check maps
        if (!fFrames.equals(other.getVerbFrames())) return false;
        if (!wordMap.equals(other.getRelatedMap())) return false;
        return true;
    }
    
    /**
	 * Checks the specified lexical id, and throws an
	 * {@link IllegalArgumentException} if it is not legal.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified lexical id is not in the closed range [0,15]
	 */
	public static void checkWordNumber(int num) {
		if (isIllegalWordNumber(num)) throw new IllegalArgumentException("'" + num + " is an illegal word number: word numbers are in the closed range [1,255]");
	}

    
    /**
	 * Checks the specified lexical id, and throws an
	 * {@link IllegalArgumentException} if it is not legal.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified lexical id is not in the closed range [0,15]
	 */
	public static void checkLexicalID(int id) {
		if (isIllegalLexicalID(id)) throw new IllegalArgumentException("'" + id + " is an illegal lexical id: lexical ids are in the closed range [0,15]");
	}
	
	/**
	 * Lexical id's are always an integer in the closed range [0,15]. In the
	 * wordnet data files, lexical ids are represented as a one digit
	 * hexidecimal integer.
	 * 
	 * @return <code>true</code> if the specified integer is an invalid
	 *         lexical id; <code>false</code> otherwise.
	 */
	public static boolean isIllegalLexicalID(int id) {
		return id < 0 || id > 15;
	}
	
	/**
	 * Word numbers are always an integer in the closed range [1,255]. In the
	 * wordnet data files, the word number is determined by the order of the
	 * word listing.
	 * 
	 * @return <code>true</code> if the specified integer is an invalid
	 *         lexical id; <code>false</code> otherwise.
	 */
	public static boolean isIllegalWordNumber(int num) {
		return num < 1 || num > 255;
	}
	
	/**
	 * Returns a string form of the lexical id as they are written in data
	 * files, which is a single digit hexidecimal number.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified integer is not a valid lexical id.
	 */
	public static String getLexicalIDForDataFile(int lexID) {
		checkLexicalID(lexID);
		return Integer.toHexString(lexID);
	}
	
	private static final String[] lexIDNumStrs = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
	
	/**
	 * Returns a string form of the lexical id as they are written in sense
	 * keys, which is as a two-digit decimal number.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified integer is not a valid lexical id.
	 */
	public static String getLexicalIDForSenseKey(int lexID) {
		checkLexicalID(lexID);
		return (lexID < 10) ? lexIDNumStrs[lexID] : Integer.toString(lexID);
	}
	
    private static final char zero = '0';
    
    /**
	 * Takes an integer in the closed range [1,255] and converts it into an
	 * two hexidecimal digit zero-filled string. E.g., "1" becomes "01",
	 * "10" becomes "0A", and so on. This is used for the generation of
	 * Word ID numbers.
	 */
    public static String zeroFillWordNumber(int offset){
    	if(offset < 0 || 255 < offset) throw new IllegalArgumentException("Word numbers must be non-negative numbers less than two hexidecimal digits long");
    	StringBuilder sb = new StringBuilder(2);
    	String str = Integer.toHexString(offset);
    	int numZeros = 2-str.length();
    	for(int i = 0; i < numZeros; i++) sb.append(zero);
    	for(int i = 0; i < str.length(); i++) sb.append(Character.toUpperCase(str.charAt(i)));
    	return sb.toString();
    }



}