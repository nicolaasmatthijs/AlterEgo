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

import java.util.regex.Pattern;

/**
 * Default implementation of {@code IIndexWordID}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class IndexWordID implements IIndexWordID {
	
	public static final Pattern whitespace = Pattern.compile("\\s+");
	public static final String underscore = "_";

    final String fLemma;
    final POS fPOS;

    /**
     * Constructs an index word id object with the specified lemma
     * and part of speech.  Since all index entries are in lower
     * case, with whitespace converted to underscores, this constructor applies
     * this conversion.
     * 
     * @throws NullPointerException if either argument is <code>null</code>.
     */
    public IndexWordID(String lemma, POS pos) {
    	if(pos == null) throw new NullPointerException();
    	fLemma = whitespace.matcher(lemma.toLowerCase()).replaceAll(underscore);
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getLemma()
     */
    public String getLemma() {
        return fLemma;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getPartOfSpeech()
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
        result = PRIME * result + fLemma.hashCode();
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
        if (!(obj instanceof IIndexWordID)) return false;
        final IIndexWordID other = (IIndexWordID) obj;
        if (!fLemma.equals(other.getLemma())) return false;
        if (!fPOS.equals(other.getPOS())) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "XID-" + fLemma + "-" + fPOS.getTag();
    }

    /**
     * Convenience method for transforming the result of the {@link #toString()}
     * method into an {@code IndexWordID}
     * 
     * @return IndexWordID The parsed id, or null if the string is malformed
     */
    public static IndexWordID parseIndexWordID(String value) {
        if (value == null) return null;
        if (!value.startsWith("XID-")) return null;
        int begin, end;

        // Get lemma
        begin = 4;
        end = value.lastIndexOf('-');
        if (end < begin) return null;
        String lemma = value.substring(begin, end);
        if (lemma.length() == 0) return null;

        // get POS
        begin = end + 1;
        if (begin >= value.length()) return null;
        char tag = Character.toLowerCase(value.charAt(begin));
        POS pos = null;
        try {
            pos = POS.getPartOfSpeech(tag);
        } catch (RuntimeException e) {
            return null;
        }
        if (pos == null) return null;

        return new IndexWordID(lemma, pos);
    }
}