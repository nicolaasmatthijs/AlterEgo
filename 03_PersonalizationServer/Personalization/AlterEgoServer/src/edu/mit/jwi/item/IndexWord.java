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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@code IIndexWord}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class IndexWord implements IIndexWord {

    private final IIndexWordID fID;
    private final int fTagSenseCnt;
    private final List<IWordID> fWordIDs;

    /**
	 * The arguments, and the contents of the array, cannot be null, otherwise
	 * the constructor throws an {@code IllegalArgumentException}
	 * 
	 * @throws NullPointerException
	 *             if lemma, pos, or word array is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the word array is empty
	 * @throws IllegalArgumentException
	 *             if the pointer tag count is less than zero
	 */
    public IndexWord(String lemma, POS pos, int tagSenseCnt, IWordID[] words) {
        this(new IndexWordID(lemma, pos), tagSenseCnt, words);
    }

    /**
	 * Constructs a new index word using the specified index word id and words
	 * 
	 * @throws NullPointerException
	 *             if the id or word array is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the word array is empty
	 * @throws IllegalArgumentException
	 *             if the pointer tag count is less than zero
	 */
    public IndexWord(IIndexWordID id, int tagSenseCnt, IWordID[] words) {
        if (id == null) throw new NullPointerException();
        if (words.length == 0) throw new IllegalArgumentException();
        if(tagSenseCnt < 0) throw new IllegalArgumentException();
        
        fID = id;
        fTagSenseCnt = tagSenseCnt;
        fWordIDs = Collections.unmodifiableList(Arrays.asList(words));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IIndexWord#getLemma()
	 */
    public String getLemma() {
        return fID.getLemma();
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IIndexWord#getWordIDs()
	 */
    public List<IWordID> getWordIDs() {
        return fWordIDs;
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IIndexWord#getTagSenseCount()
	 */
	public int getTagSenseCount() {
		return fTagSenseCnt;
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.item.IHasID#getID()
     */
    public IIndexWordID getID() {
        return fID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
    public POS getPOS() {
        return fID.getPOS();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        String result = "[" + fID.getLemma() + " (" + fID.getPOS()
                + ") ";
        for (IWordID id : fWordIDs) {
            result = result + id.toString() + ", ";
        }
        result = result.substring(0, result.length() - 2) + "]";
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
		result = prime * result + fID.hashCode();
		result = prime * result + fTagSenseCnt;
        result = prime * result + fWordIDs.hashCode();
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
        if (!(obj instanceof IIndexWord)) return false;
        final IIndexWord other = (IndexWord) obj;
        if (!fID.equals(other.getID())) return false;
        if (fTagSenseCnt != other.getTagSenseCount()) return false;
        if (!fWordIDs.equals(other.getWordIDs())) return false;
        return true;
    }
}