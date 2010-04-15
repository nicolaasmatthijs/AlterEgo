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

import edu.mit.jwi.data.parse.SenseKeyParser;

/**
 * A sense key is a unique string that identifies a word. It is represented as:
 * <p>
 * {@code lemma%ss_type:lex_filenum:lex_id:head_word:head_id}
 * <p>
 * To transform a {@link String} representation of a sense key into an actual
 * {@link ISenseKey} object, use the {@link SenseKeyParser} class.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 9, 2008
 * @since 1.5.0
 */
public interface ISenseKey extends IHasPOS, Comparable<ISenseKey> {

	/**
	 * The lemma ({@code lemma}) is the ASCII text of the word or collocation
	 * as found in the WordNet database index file corresponding to part of
	 * speech. Each lemma is in lower case, and collocations are formed by
	 * joining individual words with an underscore (_) character.
	 */
	public String getLemma();

	/**
	 * Synset type ({@code ss_type}) is a one digit decimal integer
	 * representing the synset type for the sense. 1=NOUN, 2=VERB, 3=ADJECTIVE,
	 * 4=ADVERB, 5=ADJECTIVE SATELLITE
	 */
	public int getSynsetType();

	/**
	 * Returns {@code true} if this sense key points to an adjective satellite;
	 * {@code false} otherwise.
	 */
	public boolean isAdjectiveSatellite();

	/**
	 * The lexical file ({@code lex_filenum}) is a two digit decimal integer
	 * representing the name of the lexicographer file containing the synset for
	 * the sense. See lexnames(5WN) for the list of lexicographer file names and
	 * their corresponding numbers.
	 */
	public ILexFile getLexicalFile();

	/**
	 * The lexical id ({@code lex_id}) is a two digit decimal integer that,
	 * when appended onto lemma, uniquely identifies a sense within a
	 * lexicographer file. lex_id numbers usually start with 00 , and are
	 * incremented as additional senses of the word are added to the same file,
	 * although there is no requirement that the numbers be consecutive or begin
	 * with 00 . Note that a value of 00 is the default, and therefore is not
	 * present in lexicographer files. Only non-default lex_id values must be
	 * explicitly assigned in lexicographer files.
	 */
	public int getLexicalID();

	/**
	 * The head word ({@code head_word}) is only present if the sense is in an
	 * adjective satellite synset. It is the lemma of the first word of the
	 * satellite's head synset. If this sense key is not for an adjective
	 * synset, this method returns <code>null</code>.
	 */
	public String getHeadWord();

	/**
	 * The head id ({@code head_id}) is a two digit decimal integer that, when
	 * appended onto head_word , uniquely identifies the sense of head_word
	 * within a lexicographer file, as described for lex_id . There is a value
	 * in this field only if head_word is present. If this sense key is not for
	 * an adjective synset, this method returns <code>-1</code>.
	 */
	public int getHeadID();

	/**
	 * This method is used to set the head for sense keys for adjective
	 * satellites, and it can only be called once, directly after the relevant
	 * word is created. If this method is called on a sense key that has had its
	 * head set already, or is not an adjective satellite, it will throw an
	 * {@link IllegalStateException}.
	 * 
	 * @see #needsHeadSet()
	 * 
	 * @throws IllegalStateException
	 *             if this method is called more than once
	 */
	public void setHead(String headLemma, int headLexID);

	/**
	 * This method will always return <code>false</code> if the
	 * {@link #isAdjectiveSatellite()} returns <code>false</code>. If that
	 * method returns <code>true</code>, this method will only return
	 * <code>true</code> if {@link #setHead(String, int)} has not yet been
	 * called.
	 * 
	 * @return <code>true</code> if the head lemma and lexical id need to be
	 *         set; <code>false</code> otherwise.
	 * 
	 * @see #setHead(String, int)
	 */
	public boolean needsHeadSet();
}
