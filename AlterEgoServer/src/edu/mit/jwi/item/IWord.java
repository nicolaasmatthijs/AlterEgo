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
import java.util.Map;

/**
 * Represents a word (a pair of an {@link edu.mit.jwi.item.IIndexWord} and
 * {@link edu.mit.jwi.item.ISynset}) in the Wordnet database.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface IWord extends IHasPOS, IItem<IWordID> {

	/**
	 * Returns the root form of this word.
	 */
	public String getLemma();
	
	/**
	 * Returns the id of the synset uniquely identified with this word.
	 */
	public ISynset getSynset();
	
	/** 
	 * Returns the sense key for this word.  Will never return
	 * <code>null</code>; however, the sense key that is returned
	 * <em>may</em> not yet have it's head lemma and head lexical id
	 * set yet, and so may throw an exception on some calls.
	 * 
	 * @see ISenseKey#needsHeadSet()
	 * @see ISenseKey#setHead(String, int)
	 */
	public ISenseKey getSenseKey();

	/**
	 * A integer in the closed range [0,15] that, when appended onto lemma,
	 * uniquely identifies a sense within a lexicographer file. Lexical id
	 * numbers usually start with 0, and are incremented as additional senses of
	 * the word are added to the same file, although there is no requirement
	 * that the numbers be consecutive or begin with 0. Note that a value of 0
	 * is the default, and therefore is not present in lexicographer files. In
	 * the wordnet data files the lexical id is represented as a one digit
	 * hexadecimal integer.
	 */
	public int getLexicalID();
		
	/**
	 * Returns an immutable map of from pointers to immutable maps. Note that
	 * this only returns IWords related by lexical pointers (i.e., not semantic
	 * pointers) To retrieve items related by semantic pointers, call
	 * {@link ISynset#getRelatedMap()} on the appropriate object.
	 */
	public Map<IPointer, List<IWordID>> getRelatedMap();

	/**
	 * Returns an immutable list of all word ids related to this word by the
	 * specified pointer type. Note that this only returns IWords related by
	 * lexical pointers (i.e., not semantic pointers) To retrieve items related
	 * by semantic pointers, call {@link ISynset#getRelatedSynsets()}.  If
	 * this word has no targets for the for the specified pointer, this
	 * method returns an empty list.  This method never returns {@code null}. 
	 */
	public List<IWordID> getRelatedWords(IPointer ptr);

	/**
	 * Returns an immutable list of all word ids related to this word by
	 * pointers in the database. Note this only returns words related to this
	 * word by lexical pointers, i.e., not semantic pointers. To retrieve
	 * synsets related to the synset for this word by semantic pointers, call
	 * {@link ISynset#getRelatedSynsets()} on the <code>ISynset</code> for
	 * this word.
	 */
	public List<IWordID> getRelatedWords();

	/**
	 * Returns an immutable list of all verb frames associated with this word.
	 */
	public List<IVerbFrame> getVerbFrames();

	/**
	 * Returns the adjective marker of this word, as specified in the Wordnet
	 * database.  If this word has no adjective marker, returns {@code null}
	 */
	public AdjMarker getAdjectiveMarker();

}
