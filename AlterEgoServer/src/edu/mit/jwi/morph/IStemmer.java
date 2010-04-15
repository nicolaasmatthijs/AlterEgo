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

package edu.mit.jwi.morph;

import java.util.List;

import edu.mit.jwi.item.POS;

/**
 * Governs objects that can transform surface forms of words (plus an optional
 * part of speech) into a stem. Namely, given "running" as POS.VERB, will return
 * "run".
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Mar 13, 2008
 * @since 1.5.0
 */
public interface IStemmer {

	/**
	 * Takes the surface form of a word, as it appears in the text, and the
	 * assigned Wordnet part of speech. The surface form may or may not contain
	 * whitespace or underscores, and may be in mixed case. The part of speech
	 * may be <code>null</code>, which means that all parts of speech should
	 * be considered.  Returns a list of stems, in preferred order.  No stem should
	 * be repeated in the list.
	 * 
	 * If no stems are found, this call returns an empty list. It will never
	 * return <code>null</code>.
	 * 
	 * @throws NullPointerException
	 *             if the specified surface form is <code>null</code>.
	 */
	public List<String> findStems(String surfaceForm, POS pos);

}
