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
 * A comparator that captures the ordering of lines in Wordnet data files
 * (e.g., data.adv or adv.dat files). These files are ordered by offset, which
 * is an eight-digit zero-filled decimal number that is assumed to start the line.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class DataLineComparator implements ILineComparator {
	
	private static final char spaceChar = ' ';

	private static DataLineComparator fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static DataLineComparator getInstance() {
		if (fInstance == null) fInstance = new DataLineComparator(CommentComparator.getInstance());
		return fInstance;
	}

	private CommentComparator fDetector = null;

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected DataLineComparator(CommentComparator detector) {
		fDetector = detector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String s1, String s2) {

		boolean c1 = fDetector.isCommentLine(s1);
		boolean c2 = fDetector.isCommentLine(s2);

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

		// Neither strings are comments, so extract the offset from the
		// beginnings of both and compare them as two ints.

		int i1 = s1.indexOf(spaceChar);
		int i2 = s2.indexOf(spaceChar);
		
		if (i1 == -1) i1 = s1.length();
		if (i2 == -1) i2 = s2.length();

		String sub1 = s1.substring(0, i1);
		String sub2 = s2.substring(0, i2);

		int l1 = Integer.parseInt(sub1);
		int l2 = Integer.parseInt(sub2);
		
		if (l1 < l2) return -1;
		else if (l1 > l2) return 1;
		return 0;
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