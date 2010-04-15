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

package edu.mit.jwi.data.parse;

import edu.mit.jwi.item.ExceptionEntryProxy;
import edu.mit.jwi.item.IExceptionEntryProxy;

/**
 * Basic implementation of an {@code ILineParser} that takes a line from a
 * Wordnet exception file (e.g., exc.adv or adv.exc files) and produces an
 * {@code IExceptionEntryProxy} object. This parser produces
 * {@code IExceptionEntryProxy} objects instead of {@code IExceptionEntry}
 * objects directly because the exception files do not contain information about
 * part of speech. This needs to be added by the {@code IDictionary} object
 * using this line parser to create a full-fledged {@code IExceptionEntry}
 * object.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class ExceptionLineParser implements ILineParser<IExceptionEntryProxy> {

	private static ExceptionLineParser fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static ExceptionLineParser getInstance() {
		if (fInstance == null) fInstance = new ExceptionLineParser();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected ExceptionLineParser() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String)
	 */
	public IExceptionEntryProxy parseLine(String line) {
		if (line == null) throw new MisformattedLineException(line);

		String[] forms = line.split(" ");
		if (forms.length < 2) throw new MisformattedLineException(line);

		String surface = forms[0].trim();

		String[] trimmed = new String[forms.length - 1];
		for (int i = 1; i < forms.length; i++)
			trimmed[i - 1] = forms[i].trim();

		return new ExceptionEntryProxy(surface, trimmed);
	}
}