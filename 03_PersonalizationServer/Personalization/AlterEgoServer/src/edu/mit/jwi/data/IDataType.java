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

package edu.mit.jwi.data;

import java.util.Set;

import edu.mit.jwi.data.compare.ILineComparator;
import edu.mit.jwi.data.parse.ILineParser;

/**
 * Objects that implement this interface represent possible types of data that
 * occur in the dictionary data directory.
 * <p>
 * In the standard Wordnet distributions, data types would include, but
 * would not be limited to, <i>Index</i> files, <i>Data</i> files, and
 * <i>Exception</i> files. The objects implementing this interface are then
 * paired with an {@link edu.mit.jwi.item.POS} instance and
 * {@link ILineComparator} instance to form an instance of an
 * {@link IContentType} class, which identifies the specific
 * data contained in the file. Note that here, 'data' refers not to an actual
 * file, but to an instance of the {@code IDataSource} interface that provides
 * access to the data, be it a file in the file system, a socket connection to a
 * database, or something else.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IDataType<T> {

	/**
	 * Returns the line parser that can be used to process lines of data
	 * retrieved from an {@code IDataSource} file with this type.
	 */
	public ILineParser<T> getParser();
	
	/**
	 * Indicates whether this content type usually has wordnet version
	 * information encoded in its header.
	 * 
	 * @return <code>true</code> if the content file that underlies this
	 *         content usually has wordnet version information in its comment
	 *         header; <code>false</code> otherwise.
	 */
	public boolean hasVersion();

	/**
	 * Returns an immutable set of strings that can be used as keywords to
	 * identify resources that are of this type.
	 * 
	 * @return List<String> list of resource name fragments
	 */
	public Set<String> getResourceNameHints();
}