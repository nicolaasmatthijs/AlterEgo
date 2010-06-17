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
 * Represents an entry from a {@code sense.index} file.
 * 
 * {@code sense_key synset_offset sense_number tag_cnt}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 9, 2008
 * @since 1.5.0
 */
public interface ISenseEntry extends IHasPOS {

	/**
	 * A sense key is an encoding of the word sense. Programs can construct a
	 * sense key in this format and use it as a binary search key into the sense
	 * index file.
	 */
	public ISenseKey getSenseKey();

	/**
	 * The synset offset is the byte offset that the synset containing the sense
	 * is found at in the database "data" file corresponding to the part of
	 * speech encoded in the sense key. A synset offset if an integer in the
	 * closed range [0, 99999999], and is encoded in the sense file as an 8
	 * digit, zero-filled decimal integer.
	 */
	public int getOffset();

	/**
	 * The sense number is a decimal integer indicating the sense number of the
	 * word, within the part of speech encoded in the sense key, in the WordNet
	 * database.
	 */
	public int getSenseNumber();

	/**
	 * The tag count represents the number of times the sense is tagged in
	 * various semantic concordance texts. A count of 0 indicates that the sense
	 * has not been semantically tagged.
	 */
	public int getTagCount();

}
