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

package edu.mit.jwi;

import java.util.Iterator;

import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IHasVersion;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;

/**
 * Objects that implement this interface are intended as the main entry point to
 * accessing the dictionary data. The dictionary must be opened by calling
 * {@code open()} before it is used. The dictionary allows the retrieval of
 * four different types of objects: those that implement
 * {@link edu.mit.jwi.item.IIndexWord} {@link edu.mit.jwi.item.IWord},
 * {@link edu.mit.jwi.item.ISynset}, and
 * {@link edu.mit.jwi.item.IExceptionEntry}. These operations are achieved by
 * constructing the appropriate ID object (that contains the minimum required
 * information to retrieve the said object) and passing it to the appropriate
 * method.
 * <p>
 * Wordnet can be found online at <a
 * href="http://wordnet.princeton.edu/">http://wordnet.princeton.edu/</a>.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public interface IDictionary extends IHasVersion {

	/**
	 * This opens this dictionary by doing an initialization steps required for
	 * the dictionary to be ready to respond to requests.  If this method returns 
	 * <code>false</code>, then subsequent calls to {@link #isOpen()} will return
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if there were no errors in initialization;
	 *         <code>false</code> otherwise.
	 */
	public boolean open();

	/**
	 * This closes the dictionary by disposing of data backing objects or
	 * connections. It should not be irreversible, though: another call to
	 * open() should reopen the dictionary.
	 */
	public void close();

	/**
	 * Returns <code>true</code> if the dictionary is open, that is, ready to
	 * accept queries; returns <code>false</code> otherwise
	 */
	public boolean isOpen();
	
	/**
	 * This method should be identical to
	 * <code>getIndexWord(IIndexWordID)</code> and is provided as a
	 * convenience.
	 */
	public IIndexWord getIndexWord(String lemma, POS pos);

	/**
	 * Fetches the specified index word object from the database. If the
	 * specified lemma/pos combination is not found, returns {@code null}.
	 * <i>Note:</i> This call does no stemming on the specified lemma, it is
	 * taken as specified. That is, if you submit the word "dogs", it will
	 * search for "dogs", not "dog", and find nothing. This is in contrast to
	 * the Wordnet API provided by Princeton. If you want your searches to
	 * capture morphological variation, using the descendants of the
	 * {@link IStemmer} class.
	 */
	public IIndexWord getIndexWord(IIndexWordID id);

	/**
	 * Fetches the word from the database, as specified by the indicated IWordID
	 * object. If the specified word is not found, returns {@code null}
	 */
	public IWord getWord(IWordID id);
	
	/**
	 * Fetches the specified word from the database, as specified by the
	 * indicated {@link ISenseKey} object. If the specified word is not found,
	 * returns {@code null}
	 */
	public IWord getWord(ISenseKey key);
	
	/**
	 * Fetches the specified sense entry from the database, as specified by the
	 * indicated {@link ISenseKey} object. If the specified sense entry is not
	 * found, returns {@code null}
	 */
	public ISenseEntry getSenseEntry(ISenseKey key);

	/**
	 * Fetches the synset from the database, as specified by the indicated
	 * ISynsetID object. If the specified id is not found, returns {@code null}
	 */
	public ISynset getSynset(ISynsetID id);

	/**
	 * This method should be identical to
	 * {@link IDictionary#getExceptionEntry(IExceptionEntryID)}, and is
	 * provided as a convenience.
	 * <p>
	 * Fetches the exception entry from the database, as specified by the
	 * indicated surface form/part of speech pair. If the specified entry is not
	 * found, returns {@code null}
	 */
	public IExceptionEntry getExceptionEntry(String surfaceForm, POS pos);

	/**
	 * Fetches the exception entry from the database, as specified by the
	 * indicated IExceptionEntryID object. If the specified id is not found,
	 * returns <code>null</code>
	 */
	public IExceptionEntry getExceptionEntry(IExceptionEntryID id);

	/**
	 * Returns an iterator that will iterate over all index words of the
	 * specified part of speech.
	 */
	public Iterator<IIndexWord> getIndexWordIterator(POS pos);

	/**
	 * Returns an iterator that will iterate over all index words of the
	 * specified part of speech whose lemmas match the specified pattern.
	 * Wildcards are allowed, and what constitutes a 'match' is implementation
	 * dependent.
	 */
	public Iterator<IIndexWord> getIndexWordPatternIterator(POS pos, String pattern);

	/**
	 * Returns an iterator that will iterate over all synsets of the specified
	 * part of speech.
	 */
	public Iterator<ISynset> getSynsetIterator(POS pos);

	/**
	 * Returns an iterator that will iterate over all exception entries of the
	 * specified part of speech.
	 */
	public Iterator<IExceptionEntry> getExceptionEntryIterator(POS pos);
	
	/**
	 * Returns an iterator that will iterate over all sense entries.
	 */
	public Iterator<ISenseEntry> getSenseEntryIterator();

}