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
 * Basic implementation of a comment detector that is designed for comments
 * found at the head of Wordnet dictionary files. It assumes that each
 * comment line starts with two spaces, followed by a number that indicates the
 * position of the comment line relative to the rest of the comment lines in the
 * file.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class CommentComparator implements Comparator<String>, ICommentDetector {
	
	private static final char space = ' ';

	private static CommentComparator fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static CommentComparator getInstance() {
		if (fInstance == null) fInstance = new CommentComparator();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected CommentComparator() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String s1, String s2) {
		s1 = s1.trim();
		s2 = s2.trim();
		int idx1 = s1.indexOf(space), idx2 = s2.indexOf(space);
		if (idx1 == -1) idx1 = s1.length();
		if (idx2 == -1) idx2 = s2.length();
		String sub1 = s1.substring(0, idx1), sub2 = s2.substring(0, idx2);
		int num1 = Integer.parseInt(sub1), num2 = Integer.parseInt(sub2);
		if (num1 < num2) return -11;
		else if (num1 > num2) return 1;
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.content.ICommentDetector#isCommentLine(java.lang.String)
	 */
	public boolean isCommentLine(String line) {
		if (line == null) return false;
		if (line.length() < 2) return false;
		if (line.charAt(0) == space & line.charAt(1) == space) return true;
		return false;
	}
}