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
 * Default implementation of the {@code ISynsetID} interface
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class SynsetID implements ISynsetID {

	public static final String synsetIDPrefix = "SID-";
	
    private int fOffset = -1;
    private POS fPOS = null;

    /**
	 * @throws NullPointerException
	 *             if the specified part of speech is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified offset is not a legal offset
	 */
    public SynsetID(int offset, POS pos) {
        if (pos == null) throw new NullPointerException();
        Synset.checkOffset(offset);
        
        fOffset = offset;
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynsetID#getOffset()
     */
    public int getOffset() {
        return fOffset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.ISynsetID#getPartOfSpeech()
     */
    public POS getPOS() {
        return fPOS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + fOffset;
        result = PRIME * result + fPOS.hashCode();
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
        if (!(obj instanceof ISynsetID)) return false;
        final ISynsetID other = (ISynsetID) obj;
        if (fOffset != other.getOffset()) return false;
        if (!fPOS.equals(other.getPOS())) return false;
        return true;
    }
    
    private static char hyphen = '-';
    

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(synsetIDPrefix);
    	sb.append(Synset.zeroFillOffset(fOffset));
    	sb.append(hyphen);
    	sb.append(Character.toUpperCase(fPOS.getTag()));
        return sb.toString();
    }
        
    /**
	 * Convenience method for transforming the result of the {@link #toString()}
	 * method back into an {@code ISynsetID}.
	 * 
	 * Synset IDs are always 14 characters long and have the following format:
	 * SID-########-C, where ######## is the zero-filled eight decimal digit
	 * offset of the synset, and C is the upper-case character code indicating
	 * the part of speech.
	 * 
	 * @return SynsetID The parsed id, or null if the string is malformed
	 */
    public static SynsetID parseSynsetID(String value) {
        if (value == null || value.length() != 14) return null;

        if (!value.startsWith("SID-")) return null;

        // get offset
        int offset = 0;
        try {
            offset = Integer.parseInt(value.substring(4, 12));
        } catch (Exception e) {
            return null;
        }
        
        // get pos
        char tag = Character.toLowerCase(value.charAt(13));
        POS pos = null;
        try {
            pos = POS.getPartOfSpeech(tag);
        } catch (Exception e) {
            return null;
        }

        return new SynsetID(offset, pos);
    }
}
