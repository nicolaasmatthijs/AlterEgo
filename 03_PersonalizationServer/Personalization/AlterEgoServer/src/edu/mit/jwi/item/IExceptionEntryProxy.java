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

import java.util.List;

/**
 * Represents the data that can be obtained from an exception entry file. Since
 * a full {@link edu.mit.jwi.item.IExceptionEntry} contains the
 * {@link edu.mit.jwi.item.POS} associated with the entry, this object
 * is just a proxy and must be supplemented by the part of speech at some
 * point to make a full {@code IExceptionEntry} object.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public interface IExceptionEntryProxy {

	/**
	 * The surface form (i.e., not root form) of the exception entry.
	 * 
	 * @return String
	 */
	public String getSurfaceForm();

	/**
	 * Acceptable root forms for the surface form.
	 * 
	 * @return A non-null, non-empty, unmodifiable list of root forms
	 */
	public List<String> getRootForms();

}