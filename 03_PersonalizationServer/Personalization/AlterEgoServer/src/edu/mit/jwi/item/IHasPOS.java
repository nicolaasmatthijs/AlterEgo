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
 * Indicates that implementing objects have an associated part of speech.
 * 
 * @author Mark Alan Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public interface IHasPOS {

	/**
	 * Returns which part of speech this object pertains to. May be
	 * <code>null</code>, if the object is not specific to any particular
	 * part of speech.
	 */
	public POS getPOS();
}
