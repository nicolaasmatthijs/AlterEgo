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
 * Represents a version of Wordnet.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 22, 2008
 * @since 1.5.0
 */
public interface IVersion {
	
	/**
	 * @return the major version number, i.e., the '1' in '1.7.2'
	 */
	public int getMajorVersion();
	
	/**
	 * @return the minor version number, i.e., the '7' in '1.7.2'
	 */
	public int getMinorVersion();

	/**
	 * @return the bugfix version number, i.e., the '2' in '1.7.2'
	 */
	public int getBugfixVersion();
	
}
