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
 * Default implementation of {@code IExceptionEntry}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class ExceptionEntry extends ExceptionEntryProxy implements
        IExceptionEntry {

    final POS fPos;
    final IExceptionEntryID fID;

    /**
     * The part of speech must not be null; if it is, the constructor throws an
     * {@code NullPointerException}.
     */
    public ExceptionEntry(IExceptionEntryProxy proxy, POS pos) {
        super(proxy);
        if (pos == null)
            throw new NullPointerException("POS cannot be null");
        fPos = pos;
        fID = new ExceptionEntryID(getSurfaceForm(), fPos);
    }

    /**
     * The part of speech must not be null; if it is, the constructor throws an
     * {@code NullPointerException}.
     */
    public ExceptionEntry(String surfaceForm, POS pos, String... rootForms) {
        super(surfaceForm, rootForms);
        if (pos == null)
            throw new NullPointerException(
                    "POS cannot be null");
        fPos = pos;
        fID = new ExceptionEntryID(getSurfaceForm(), fPos);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
    public POS getPOS() {
        return fPos;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IItem#getID()
	 */
    public IExceptionEntryID getID() {
        return fID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() + "-" + fPos.toString();
    }
}