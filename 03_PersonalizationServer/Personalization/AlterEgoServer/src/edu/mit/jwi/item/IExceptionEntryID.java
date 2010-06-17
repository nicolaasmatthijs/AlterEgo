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
 * A unique identifier sufficient to retrieve the specified
 * {@link edu.mit.jwi.item.IExceptionEntry} from the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public interface IExceptionEntryID extends IHasPOS, IItemID<IExceptionEntry> {

	/**
	 * The surface form (i.e., not the root form) of the word for which a
	 * morphological exception entry is desired.  Because all surface forms
	 * in the exception files are lower case, so should be the string
	 * returned by this call.
	 */
	public String getSurfaceForm();

}