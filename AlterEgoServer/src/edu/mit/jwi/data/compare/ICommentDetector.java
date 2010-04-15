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

package edu.mit.jwi.data.compare;

import java.util.Comparator;


/**
 * Objects that implement this interface act as both detectors for comment lines
 * in data resources, and comparators that say how comment lines are ordered, if
 * at all.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov 16, 2007
 * @since 1.5.0
 */
public interface ICommentDetector extends Comparator<String> {

	/**
	 * @return <code>true</code> if the specified string is a comment line,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCommentLine(String line);

}