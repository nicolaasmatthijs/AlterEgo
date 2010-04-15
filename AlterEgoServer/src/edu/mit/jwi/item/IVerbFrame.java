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
 * Represents a verb frame drawn from the verb frames data file in the Wordnet
 * distribution
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IVerbFrame {

	/**
	 * The id number of this verb frame. Should always return 1 or greater.
	 */
	public int getNumber();

	/**
	 * The string form of the template, drawn directly from the data file.
	 * Will never return <code>null</code>
	 */
	public String getTemplate();

	/**
	 * Takes the supplied surface form of a verb and instantiates it into the
	 * template for the verb frame. This is a convenience method for the simple
	 * string replace operation required to effect this. The method does no
	 * morphological processing, nor even does it check to see if the passed in
	 * word is actually a verb.
	 */
	public String instantiateTemplate(String verb);

}