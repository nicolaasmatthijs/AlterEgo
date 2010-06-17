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

import edu.mit.jwi.item.ILexFile;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.LexFile;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.SenseKey;
import edu.mit.jwi.item.UnknownLexFile;

/**
 * Implementation of an {@code ILineParser} that takes a sense key string and
 * produces an {@code ISenseKey} object. This class follows a singleton design
 * pattern, and is not intended to be instantiated directly; rather, call the
 * {@link #getInstance()} method to get the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class SenseKeyParser implements ILineParser<ISenseKey>{
	
	private static final char colon = ':';
	private static final char percent = '%';
	
	private static SenseKeyParser fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static SenseKeyParser getInstance() {
		if (fInstance == null) fInstance = new SenseKeyParser();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected SenseKeyParser() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.parse.ILineParser#parseLine(java.lang.String)
	 */
	public ISenseKey parseLine(String key) {
		
		try{
			int begin = 0, end = 0;
			
			// get lemma
			end = key.indexOf(percent);
			String lemma = key.substring(begin, end);
			
			// get ss_type
			begin = end+1;
			end = key.indexOf(colon, begin);
			int ss_type = Integer.parseInt(key.substring(begin, end));
			POS pos = POS.getPartOfSpeech(ss_type);
			boolean isAdjSat = POS.isAdjectiveSatellite(ss_type);
			
			// get lex_filenum
			begin = end+1;
			end = key.indexOf(colon, begin);
			int lex_filenum = Integer.parseInt(key.substring(begin, end));
			ILexFile lexFile = resolveLexicalFile(lex_filenum);
			
			// get lex_id
			begin = end+1;
			end = key.indexOf(colon, begin);
			int lex_id = Integer.parseInt(key.substring(begin, end));
			
			// if it's not an adjective satellite, we're done
			if(!isAdjSat) return new SenseKey(lemma, lex_id, pos, lexFile, null, -1, key);

			// get head_word
			begin = end+1;
			end = key.indexOf(colon, begin);
			String head_word = key.substring(begin, end);
			
			// get head_id
			begin = end+1;
			int head_id = Integer.parseInt(key.substring(begin));
				
			return new SenseKey(lemma, lex_id, pos, lexFile, head_word, head_id, key);
			
		} catch(Exception e){
			throw new MisformattedLineException(e);
		}
	}
	
	/**
	 * Retrieval of the lexical file objects for the {@link #parseLine(String)}
	 * method is implemented in its own method for ease of subclassing.
	 */
	protected ILexFile resolveLexicalFile(int lexFileNum){
		ILexFile lexFile = LexFile.getLexicalFile(lexFileNum);
		return (lexFile == null) ? UnknownLexFile.getUnknownLexicalFile(lexFileNum) : lexFile;
	}

}
