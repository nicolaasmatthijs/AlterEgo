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
 * Default implementation {@code IExceptionEntryID}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class ExceptionEntryID implements IExceptionEntryID {

    final String fSurface;
    final POS fPOS;

    /**
     * @throws NullPointerException if either argument is <code>null</code>
     */
    public ExceptionEntryID(String lemma, POS pos) {
    	if(pos == null) throw new NullPointerException();
    	
    	// all exception entries are lower-case
    	// this call also checks for null
        fSurface = lemma.toLowerCase(); 
        fPOS = pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.mit.wordnet.core.data.IIndexWordID#getLemma()
     */
    public String getSurfaceForm() {
        return fSurface;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
    public POS getPOS() {
        return fPOS;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "EID-" + fSurface + "-" + fPOS.getTag();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + fSurface.hashCode();
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
        if(!(obj instanceof IExceptionEntryID)) return false;
        final IExceptionEntryID other = (IExceptionEntryID) obj;
        if (!fSurface.equals(other.getSurfaceForm())) return false;
        if (!fPOS.equals(other.getPOS())) return false;
        return true;
    }



}
