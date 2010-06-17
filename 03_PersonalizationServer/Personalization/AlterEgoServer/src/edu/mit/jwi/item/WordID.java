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

/**
 * Default implementation of the {@code IWordID} interface.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class WordID implements IWordID {
	
	public static final String wordIDPrefix = "WID-";
	public static final String unknownLemma = "?";
	public static final String unknownWordNumber = "??";
	
    private final ISynsetID sID;
    private final int fNum;
    private final String fLemma;
    
    public WordID(int offset, POS pos, int number){
    	this(new SynsetID(offset, pos), number);
    }
    
    public WordID(int offset, POS pos, String lemma){
    	this(new SynsetID(offset, pos), lemma);
    }

    /**
     * @throws NullPointerException if the synset id is <code>null</code>
     */
    public WordID(ISynsetID id, int number) {
        if (id == null) throw new NullPointerException();
        Word.checkWordNumber(number);
        sID = id;
        fNum = number;
        fLemma = unknownLemma;
    }

    /**
     * @throws NullPointerException if either argument is <code>null</code>
     */
    public WordID(ISynsetID synsetID, String lemma) {
        if (synsetID == null | lemma == null) throw new NullPointerException();
        sID = synsetID;
        fNum = -1;
        fLemma = lemma.toLowerCase();
    }
    
    /**
     * Allows full control.
     */
    public WordID(ISynsetID id, int number, String lemma){
    	if (id == null) throw new NullPointerException();
    	if(lemma.trim().length() == 0) throw new IllegalArgumentException();
    	Word.checkWordNumber(number);
        sID = id;
        fNum = number;
        fLemma = lemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getSynsetID()
     */
    public ISynsetID getSynsetID() {
        return sID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getNumber()
     */
    public int getSenseNumber() {
        return fNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IWordID#getLemma()
     */
    public String getLemma() {
        return fLemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IWordID#getPartOfSpeech()
     */
    public POS getPOS() {
        return sID.getPOS();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((sID == null) ? 0 : sID.hashCode());
        result = PRIME * result + fNum;
        if (fLemma != null) result = PRIME * result + fLemma.hashCode();
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
        if (getClass() != obj.getClass()) return false;
        final WordID other = (WordID) obj;
        if (sID == null) {
            if (other.sID != null) return false;
        } else if (!sID.equals(other.sID)) return false;
        if (other.fNum != 0 & fNum != 0 & other.fNum != fNum) return false;
        if (other.fLemma != null & fLemma != null) {
            if (!other.fLemma.equals(fLemma)) return false;
        }
        return true;
    }
    
    private static final char hyphen = '-';

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuilder sb = new StringBuilder(16 + fLemma.length());
    	sb.append(wordIDPrefix);
    	sb.append(Synset.zeroFillOffset(sID.getOffset()));
    	sb.append(hyphen);
    	sb.append(Character.toUpperCase(sID.getPOS().getTag()));
    	sb.append(hyphen);
    	sb.append((fNum < 0) ? unknownWordNumber : Word.zeroFillWordNumber(fNum));
        sb.append(hyphen);
        sb.append(fLemma);
        return sb.toString();
    }

    /**
     * Convenience method for transforming the result of the {@link #toString()}
     * method back into an {@code WordID}.
     * 
     * Word id's are always of the following format:
     * 
     * WID-########-P-##-lemma
     * 
     * where ######## is the eight decimal digit zero-filled offset of the associated synset,
     * P is the upper case character representing the part of speech, ## is the two hexidecimal digit
     * zero-filled word number (or ?? if unknown), and lemma is the lemma.
     * 
     * @return WordID The parsed id, or null if the string is malformed
     */
    public static IWordID parseWordID(String value) {
        if (value == null || value.length() < 19) return null;
        if (!value.startsWith("WID-")) return null;
        
        int offset = -1;
        try{
        	offset = Integer.parseInt(value.substring(4,12));
        } catch(Exception e){
        	return null;
        }
        if(offset < 0) return null;
        
        POS pos = null;
        try{
        	pos = POS.getPartOfSpeech(value.charAt(13));
        } catch(Exception e){
        	return null;
        }
        if(pos == null) return null;
        
        ISynsetID id = new SynsetID(offset, pos);
        
        // get word number
        int num = -1;
        try{
        	num = Integer.parseInt(value.substring(15, 17), 16);
        } catch(Exception e){
        	// Ignore
        }
        if(num > 0) return new WordID(id, num);

        // Try to get lemma now
        String lemma = value.substring(18);
        if (lemma.equals(unknownLemma) | lemma.length() == 0) return null;

        return new WordID(id, lemma);
    }
    
}