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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import edu.mit.jwi.item.IHasVersion;


/**
 * Objects that implement this interface serve as managers of the relationship
 * of a {@link edu.mit.jwi.IDictionary} object and the dictionary data. Before
 * using an {@code IDataProvider}, the dictionary must call
 * {@link #setSource(URL)} (or call the appropriate constructor) and
 * {@link #open()}.
 * <p>
 * When the dictionary wants to lookup a piece of data, it requests the
 * {@code IDataSource} object that holds that data by passing the appropriate
 * {@link edu.mit.jwi.data.IContentType} to the {@link #getSource(IContentType)}
 * method. The {@link IDataSource} interface then handles the actual production
 * of the data from whatever resource backs it.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IDataProvider extends IHasVersion {
	
	/**
	 * This method must be called with the appropriate URL that directs the file
	 * provider to the data that backs the dictionary. If the data provider has
	 * already been opened, this call will have no effect until the provider is
	 * closed then opened again.
	 */
	public void setSource(URL url);

	/**
	 * Returns the URL that points to the resource location.
	 */
	public URL getSource();

	/**
	 * Instructs the data provider to perform any initialization.
	 * 
	 * @throws IOException
	 *             if there is a problem during initialization
	 */
	public void open() throws IOException;

	/**
	 * Instructs the provider to dispose of handles to current
	 * {@code IDataSource} objects and perform any other operations necessary
	 * to free the data resources that back the dictionary. A subsequent call to
	 * {@link #open()} should put the provider in a state where it can again
	 * provide {@code IDataSource} objects.
	 */
	public void close();

	/**
	 * Returns <code>true</code> if the provider has finished all of its
	 * initialization activities and is ready to return {@code IDataSource}
	 * objects on command; returns <code>false</code> otherwise.
	 */
	public boolean isOpen();

	/**
	 * Returns an unmodifiable {@code Collection} that contains all
	 * {@code IDataSource} objects to which the provider has access.
	 */
	public Collection<IDataSource<?>> getSources();

	/**
	 * Gets an {@code IDataSource} object for the specified content type.
	 */
	public <T> IDataSource<T> getSource(IContentType<T> type);

}