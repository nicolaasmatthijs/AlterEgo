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

/**
 * A comparator that captures the ordering of lines in Wordnet index files
 * (e.g., index.adv or adv.idx files). These files are ordered alphabetically.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class IndexLineComparator implements ILineComparator {

	private static final char spaceChar = ' ';
	
	private static IndexLineComparator fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static IndexLineComparator getInstance() {
		if (fInstance == null) fInstance = new IndexLineComparator(CommentComparator.getInstance());
		return fInstance;
	}

	private CommentComparator fDetector = null;

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected IndexLineComparator(CommentComparator detector) {
		fDetector = detector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String s1, String s2) {
		// check for comments
		boolean c1 = fDetector.isCommentLine(s1), c2 = fDetector.isCommentLine(s2);

		if (c1 & c2) {
			// both lines are comments, defer to comment comparator
			return fDetector.compare(s1, s2);
		}
		else if (c1 & !c2) {
			// first line is a comment, should come before the other
			return -1;
		}
		else if (!c1 & c2) {
			// second line is a comment, should come before the other
			return 1;
		}

		// Neither strings are comments, so extract the lemma from the
		// beginnings of both
		// and compare them as two strings.

		int i1 = s1.indexOf(spaceChar), i2 = s2.indexOf(spaceChar);
		if (i1 == -1) i1 = s1.length();
		if (i2 == -1) i2 = s2.length();

		String sub1 = s1.substring(0, i1).toLowerCase(), sub2 = s2.substring(0, i2).toLowerCase();

		return sub1.compareTo(sub2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.content.compare.ILineComparator#getCommentDetector()
	 */
	public ICommentDetector getCommentDetector() {
		return fDetector;
	}
}