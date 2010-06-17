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
 * Represents a unique identifier sufficient to retrieve a particular
 * {@link edu.mit.jwi.item.IWord} object from the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IWordID extends IHasPOS, IItemID<IWord> {

	/**
	 * Returns the SynsetID object associated with this IWordID.
	 */
	public ISynsetID getSynsetID();

	/**
	 * The word number, which is a number from 1 to 255 that indicates the order
	 * this word is listed in the Wordnet data files.
	 * 
	 * @return an integer between 1 and 255, inclusive.
	 * @since JWI 2.1.4, Sep 11, 2008
	 */
	public int getSenseNumber();

	/**
	 * Returns the lemma (word root) associated with this index word.
	 */
	public String getLemma();
}
