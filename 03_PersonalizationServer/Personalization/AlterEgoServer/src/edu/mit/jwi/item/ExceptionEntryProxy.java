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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation {@code IExceptionEntryProxy}
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public class ExceptionEntryProxy implements IExceptionEntryProxy {

	final String fSurface;
	final List<String> fRoots;

	/**
	 * @throws NullPointerException if the argument is <code>null</code>
	 */
	public ExceptionEntryProxy(IExceptionEntryProxy proxy) {
		fSurface = proxy.getSurfaceForm();
		fRoots = proxy.getRootForms();
	}

	/**
	 * The arguments, and the contents of the array, cannot be null, otherwise
	 * it will throw an NullPointerException or IllegalArgumentException.
	 */
	public ExceptionEntryProxy(String surfaceForm, String ... rootForms) {
		if (surfaceForm == null) throw new NullPointerException();
		for(String form : rootForms){
			if (form == null || form.length() == 0)	throw new IllegalArgumentException();
		}
		
		fSurface = surfaceForm;
		fRoots = Collections.unmodifiableList(Arrays.asList(rootForms));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.data.IExceptionEntry#getSurfaceForm()
	 */
	public String getSurfaceForm() {
		return fSurface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.data.IExceptionEntry#getRootForms()
	 */
	public List<String> getRootForms() {
		return fRoots;
	}

	private static String prefix = "EXC-";
	private static String rightBracket = "[";
	private static String leftBracket = "]";
	private static String comma = ", ";

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(fSurface);
		sb.append(rightBracket);
		for (Iterator<String> i = fRoots.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) sb.append(comma);
		}
		sb.append(leftBracket);
		return sb.toString();
	}
}