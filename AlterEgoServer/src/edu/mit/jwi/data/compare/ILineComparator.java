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
 * Classes that implement this interface are objects that are {@link String}
 * comparators and also may have an associated comment detector.
 * <p>
 * The {@link java.util.Comparator#compare(Object, Object)} method of this class will
 * throw a {@link edu.mit.jwi.data.parse.MisformattedLineException} if the line
 * data passed to that method is ill-formed.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov 16, 2007
 * @since 1.5.0
 */
public interface ILineComparator extends Comparator<String> {
	
	/**
	 * Returns the comment detector instance associated with this line
	 * comparator, or {@code null} if one does not exist.
	 */
	public ICommentDetector getCommentDetector();

}