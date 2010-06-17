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

import java.util.StringTokenizer;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IndexWord;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.item.WordID;

/**
 * Basic implementation of an {@code ILineParser} that takes a line from a
 * Wordnet index file (e.g., idx.adv or adv.idx files) and produces an
 * {@code IIndexWord} object.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class IndexLineParser implements ILineParser<IIndexWord> {

	private static IndexLineParser fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static IndexLineParser getInstance() {
		if (fInstance == null) fInstance = new IndexLineParser();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected IndexLineParser() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String)
	 */
	public IIndexWord parseLine(String line) {
		if (line == null) throw new MisformattedLineException(line);

		try {
			IIndexWord result = null;
			StringTokenizer tokenizer = new StringTokenizer(line, " ");

			// get lemma
			String lemma = tokenizer.nextToken();

			// get pos
			String posSym = tokenizer.nextToken();
			POS pos = POS.getPartOfSpeech(posSym.charAt(0));
			
			// consume synset_cnt
			tokenizer.nextToken();

			// consume ptr_symbols
			int p_cnt = Integer.parseInt(tokenizer.nextToken());
			for (int i = 0; i < p_cnt; ++i) tokenizer.nextToken();

			// get sense_cnt
			int senseCount = Integer.parseInt(tokenizer.nextToken());

			// get tagged sense count
			int tagSenseCnt = Integer.parseInt(tokenizer.nextToken());

			// get words
			IWordID[] words = new IWordID[senseCount];
			int offset;
			for (int i = 0; i < senseCount; i++) {
				offset = Integer.parseInt(tokenizer.nextToken());
				words[i] = new WordID(new SynsetID(offset, pos), lemma);
			}

			result = new IndexWord(lemma, pos, tagSenseCnt, words);
			return result;
		} catch (Exception e) {
			throw new MisformattedLineException(line, e);
		} 
	}
}