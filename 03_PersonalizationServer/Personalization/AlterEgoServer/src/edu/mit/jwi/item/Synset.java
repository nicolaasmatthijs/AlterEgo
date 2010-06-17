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
 * Default implementation of the {@code ISynset} interface.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class Synset implements ISynset {

    final ISynsetID fID;
    final ILexFile fLexFile;
    final String fGloss;
    final boolean fIsAdjSat, fIsAdjHead;
    final List<ISynsetID> allRelated;
    final Map<IPointer, List<ISynsetID>> synsetMap;
    final List<IWord> fWords;

    /**
     * @throws NullPointerException if any argument is <code>null</code>
     */
    public Synset(ISynsetID id, ILexFile lexFile, boolean isAdjSat, boolean isAdjHead, String gloss, 
    		List<IWord> words, Map<IPointer, ? extends List<ISynsetID>> ids){
    	
    	if(id == null || lexFile == null || gloss == null) throw new NullPointerException();
        if(isAdjSat && isAdjHead) throw new IllegalArgumentException();
        if((isAdjSat || isAdjHead) && lexFile.getNumber() != 0) throw new IllegalArgumentException();
        
        fID = id;
        fLexFile = lexFile;
        fGloss = gloss;
        fIsAdjSat = isAdjSat;
        fIsAdjHead = isAdjHead;

        // copy words
        fWords = Collections.unmodifiableList(words); // takes care of null pointer in words

        Set<ISynsetID> hiddenSet = null;
        Map<IPointer, List<ISynsetID>> hiddenMap = null;
        // fill synset map
        if(ids != null){
        	hiddenSet = new LinkedHashSet<ISynsetID>();
        	hiddenMap = new HashMap<IPointer, List<ISynsetID>>(ids.size());
        	for(Entry<IPointer, ? extends List<ISynsetID>> entry : ids.entrySet()){
        		if(entry.getValue() == null || entry.getValue().isEmpty()) continue;
        		hiddenMap.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<ISynsetID>(entry.getValue())));
        		hiddenSet.addAll(entry.getValue());
        	}
        }
        allRelated = (hiddenSet != null && !hiddenSet.isEmpty()) ? Collections.unmodifiableList(new ArrayList<ISynsetID>(hiddenSet)) : Collections.<ISynsetID>emptyList();
        synsetMap = (hiddenMap != null && !hiddenMap.isEmpty()) ? Collections.unmodifiableMap(hiddenMap) : Collections.<IPointer, List<ISynsetID>>emptyMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IHasID#getID()
     */
    public ISynsetID getID() {
        return fID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.ISynset#getOffset()
     */
    public int getOffset() {
        return fID.getOffset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getPartOfSpeech()
     */
    public POS getPOS() {
        return fID.getPOS();
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISynset#getType()
	 */
	public int getType() {
		POS pos = getPOS();
		if(pos == POS.NOUN) return 1;
		if(pos == POS.VERB) return 2;
		if(pos == POS.ADVERB) return 4;
		return isAdjectiveSatellite() ? 5 : 3;
	}

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getGloss()
     */
    public String getGloss() {
        return fGloss;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getWords()
     */
    public List<IWord> getWords() {
        return fWords;
    }
    
	/* (non-Javadoc) @see edu.mit.jwi.item.ISynset#getWord(int) */
	public IWord getWord(int wordNumber) {
		return fWords.get(wordNumber-1);
	}
    
    

    /* (non-Javadoc) @see edu.mit.jwi.item.ISynset#getRelatedMap(edu.mit.jwi.item.IPointer) */
	public Map<IPointer, List<ISynsetID>> getRelatedMap() {
		return synsetMap;
	}

	/*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getRelatedSynsets(edu.mit.wordnet.core.data.IPointerType)
     */
    public List<ISynsetID> getRelatedSynsets(IPointer type) {
    	if(synsetMap == null) return Collections.<ISynsetID>emptyList();
        List<ISynsetID> result = synsetMap.get(type);
        return result != null ? result : Collections.<ISynsetID>emptyList(); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#getAllRelatedSynsets()
     */
    public List<ISynsetID> getRelatedSynsets() {
        return allRelated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynset#isAdjectiveSatellite()
     */
    public boolean isAdjectiveSatellite() {
        return fIsAdjSat;
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISynset#isAdjectiveHead()
	 */
	public boolean isAdjectiveHead() {
		return fIsAdjHead;
	}

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((fGloss == null) ? 0 : fGloss.hashCode());
        result = PRIME * result + (fIsAdjSat ? 1231 : 1237);
        result = PRIME * result + fID.hashCode();
        result = PRIME * result + fWords.hashCode();
        result = PRIME * result + synsetMap.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (!(obj instanceof ISynset)) return false;
        final ISynset other = (ISynset) obj;
        
        if (fGloss == null) {
            if (other.getGloss() != null) return false;
        } else if (!fGloss.equals(other.getGloss())) return false;
        
        if (!fID.equals(other.getID())) return false;
        if (!fWords.equals(other.getWords())) return false;
        if (fIsAdjSat != other.isAdjectiveSatellite()) return false;
        if (!synsetMap.equals(other.getRelatedMap())) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SYNSET{");
        buffer.append(fID.toString());
        buffer.append(" : Words[");
        for (IWord word : fWords) {
            buffer.append(word.toString());
            buffer.append(", ");
        }
        buffer.replace(buffer.length() - 2, buffer.length(), "]}");

        return buffer.toString();
    }

	/* (non-Javadoc) @see edu.mit.jwi.item.ISynset#getLexicalFileNumber() */
	public ILexFile getLexicalFile() {
		return fLexFile;
	}
	
    private static char zero = '0';
    
    /**
	 * Takes an integer in the closed range [0,99999999] and converts it into an
	 * eight decimal digit zero-filled string. E.g., "1" becomes "00000001",
	 * "1234" becomes "00001234", and so on. This is used for the generation of
	 * Synset and Word ID numbers.
	 */
    public static String zeroFillOffset(int offset){
    	checkOffset(offset);
    	StringBuilder sb = new StringBuilder(8);
    	String offsetStr = Integer.toString(offset);
    	int numZeros = 8-offsetStr.length();
    	for(int i = 0; i < numZeros; i++) sb.append(zero);
    	sb.append(offsetStr);
    	return sb.toString();
    }
    
    public static void checkOffset(int offset){
    	if(isIllegalOffset(offset)) throw new IllegalArgumentException("'" + offset + "' is not a valid offset; offsets must be in the closed range [0,99999999]");
    }
    
    /** 
     * @return <code>true</code> if the specified offset is in the closed
     * range [0, 99999999]; <code>false</code> otherwise.
     */
    public static boolean isIllegalOffset(int offset){
    	return offset < 0 || 99999999 < offset;
    }



}
