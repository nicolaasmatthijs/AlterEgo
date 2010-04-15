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
 * Represents a synset object.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public interface ISynset extends IHasPOS, IItem<ISynsetID> {

	/**
	 * Returns the data file offset of this synset, per the Wordnet
	 * specification.
	 * 
	 * @return int the offset in the associated data source
	 */
	public int getOffset();

	/**
	 * Returns a representation of the lexical file. In wordnet data files, the
	 * lexical file number is a two digit decimal integer representing the name
	 * of the lexicographer file containing the synset for the sense.
	 */
	public ILexFile getLexicalFile();

	/**
	 * Returns the type of the synset, encoded as follows: 1=Noun, 2=Verb,
	 * 3=Adjective, 4=Adverb, 5=Adjective Satellite.
	 */
	public int getType();

	/**
	 * The gloss (brief, plain-English description) of this synset.
	 * 
	 * @return String The gloss
	 */
	public String getGloss();

	/**
	 * Returns an immutable list of the word objects (synset, index word pairs)
	 * associated with this synset.
	 */
	public List<IWord> getWords();
	
	/**
	 * Returns the word with the specified word number.  Words are numbered
	 * sequentially from 1 up to, and including 255.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the word number is not an appropriate word number for this
	 *             synset.
	 */
	public IWord getWord(int wordNumber);
	
	/**
	 * Returns whether this synset is an adjective head or not, per Wordnet
	 * specification.
	 */
	public boolean isAdjectiveHead();

	/**
	 * Returns whether this synset is an adjective satellite or not, per Wordnet
	 * specification.
	 */
	public boolean isAdjectiveSatellite();

	/**
	 * Returns an immutable map from pointers to immutable lists of synsets Note
	 * that this only returns a non-empty result for semantic pointers (i.e.,
	 * non-lexical pointers). To obtain lexical pointers, call
	 * {@link IWord#getRelatedMap()} on the appropriate object.
	 */
	public Map<IPointer, List<ISynsetID>> getRelatedMap();

	/**
	 * Returns an immutable list of the ids of all synsets that are related to
	 * this synset by the specified pointer type. Note that this only returns a
	 * non-empty result for semantic pointers (i.e., non-lexical pointers). To
	 * obtain lexical pointers, call {@link IWord#getRelatedWords()()} on the
	 * appropriate object.
	 */
	public List<ISynsetID> getRelatedSynsets(IPointer ptr);

	/**
	 * Returns an immutable list of synset ids for all synsets that are
	 * connected by pointers to this synset. Note that the related synsets
	 * returned by this call are related by semantic pointers (as opposed to
	 * lexical pointers, which are relationships between
	 * {@link edu.mit.jwi.item.IWord} objects.
	 */
	public List<ISynsetID> getRelatedSynsets();
	




}