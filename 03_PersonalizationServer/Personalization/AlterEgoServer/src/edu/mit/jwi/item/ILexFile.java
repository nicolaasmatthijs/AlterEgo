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
 * An interface that describes a lexical file. This is usually not the actual
 * lexicographer's file, but rather a descriptor, giving the name, number, and a
 * brief description.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 9, 2008
 * @since 1.5.0
 */
public interface ILexFile extends IHasPOS {

	/**
	 * The lexicographer file number. This is used in sense keys and the data
	 * files.
	 */
	public int getNumber();

	/**
	 * The lexicographer file name.
	 */
	public String getName();

	/**
	 * A brief description of the lexicographer file's contents
	 */
	public String getDescription();

}
