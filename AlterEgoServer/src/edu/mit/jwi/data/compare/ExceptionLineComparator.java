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

import java.util.regex.Pattern;

import edu.mit.jwi.data.parse.MisformattedLineException;

/**
 * A comparator that captures the ordering of lines in Wordnet exception
 * files (e.g., exc.adv or adv.exc files). These files are ordered
 * alphabetically.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class ExceptionLineComparator implements ILineComparator {

	private static ExceptionLineComparator fInstance;

	/**
	 * Returns the singleton instance of this class.
	 */
	public static ExceptionLineComparator getInstance() {
		if (fInstance == null) fInstance = new ExceptionLineComparator();
		return fInstance;
	}
	
	private final static Pattern spacePattern = Pattern.compile(" ");

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected ExceptionLineComparator() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String line1, String line2) {

		String[] words1 = spacePattern.split(line1);
		String[] words2 = spacePattern.split(line2);

		if (words1.length < 1) throw new MisformattedLineException(line1);
		if (words2.length < 1) throw new MisformattedLineException(line2);

		return words1[0].compareTo(words2[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.content.compare.ILineComparator#getCommentDetector()
	 */
	public ICommentDetector getCommentDetector() {
		return null;
	}
}