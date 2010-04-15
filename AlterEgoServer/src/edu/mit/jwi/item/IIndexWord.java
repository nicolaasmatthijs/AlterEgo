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

import java.util.List;

/**
 * Represents an index word object.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public interface IIndexWord extends IHasPOS, IItem<IIndexWordID> {

	/**
	 * @return the lemma (word root) associated with this index word.
	 */
	public String getLemma();

	/**
	 * @return an immutable list of {@code IWordID} objects associated with this
	 *         index word.
	 */
	public List<IWordID> getWordIDs();
	
	/**
	 * @return Number of senses of lemma that are ranked according to their
	 *         frequency of occurrence in semantic concordance texts.
	 */
	public int getTagSenseCount();

}
