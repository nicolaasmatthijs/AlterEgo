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
 * Represents a unique identifier for a {@link edu.mit.jwi.item.ISynset},
 * sufficient to retrieve the synset from the Wordnet database. It consists of a
 * part of speech and an offset.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface ISynsetID extends IHasPOS, IItemID<ISynset> {

	/**
	 * Returns the offset for the specified synset.
	 */
	public int getOffset();

}