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

import java.util.Iterator;

import edu.mit.jwi.item.IHasVersion;

/**
 * Objects that implement this interface mediate between a {@code Dictionary}
 * object and the data that is contained in the dictionary data resources. They
 * typically are assigned a name (e.g., <i>verb.data</i>, for the data resource
 * pertaining to verbs) and a content type. A key is used to find a particular
 * piece of data in the resource. The {@code IDataSource} object then returns a
 * text string that can be parsed to produce a data object (e.g., an
 * {@code ISynset} or {@code IIndexWord} object).
 * 
 * The iterator produced by this class should not support the
 * {@link Iterator#remove()} operation.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IDataSource<T> extends IHasVersion, Iterable<String> {

	/**
	 * Returns a string representation of the name of this resource
	 */
	public String getName();

	/**
	 * Returns the assigned content type of the resource that backs this object.
	 */
	public IContentType<T> getContentType();

	/**
	 * Returns a string that contains the data indexed by the specified key. If
	 * the file cannot find the key in its data resource, it returns
	 * <code>null</code>
	 */
	public String getLine(String key);

	/**
	 * Returns an iterator that will iterator over lines in the data resource,
	 * starting at the line specified by the given key. If the key is
	 * <code>null</code>, this is the same as calling the plain
	 * {@link #iterator()} method. If no line starts with the pattern, the
	 * iterator's {@link Iterator#hasNext()} will return <code>false</code>.
	 * The iterator does not support the {@link Iterator#remove()} operation.
	 */
	public Iterator<String> iterator(String key);

}