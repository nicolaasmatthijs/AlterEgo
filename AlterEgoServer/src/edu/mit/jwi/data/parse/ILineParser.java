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

package edu.mit.jwi.data.parse;

/**
 * Objects that implement this interface are used to parse lines of data from
 * a data source into data objects that are then manipulated by the
 * dictionary.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface ILineParser<T> {

	/**
	 * Given the line of data, it produces an object of class {@code T}. If
	 * the line is <code>null</code>, or the line is malformed in some way,
	 * the method throws a {@code MisformattedLineException}.
	 * 
	 * @param line
	 *            the line that should be parsed
	 * @return the object resulting from the parse
	 * @throws edu.mit.jwi.data.parse.MisformattedLineException
	 *             if the line is malformed or null
	 */
	public T parseLine(String line);

}
