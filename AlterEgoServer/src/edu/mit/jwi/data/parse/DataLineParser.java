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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import edu.mit.jwi.item.AdjMarker;
import edu.mit.jwi.item.ILexFile;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IVerbFrame;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.LexFile;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Synset;
import edu.mit.jwi.item.SynsetID;
import edu.mit.jwi.item.UnknownLexFile;
import edu.mit.jwi.item.VerbFrame;
import edu.mit.jwi.item.Word;
import edu.mit.jwi.item.WordID;

/**
 * Basic implementation of an object that takes a line from a Wordnet data
 * file (e.g., data.adv or adv.dat files) and produces an {@code ISynset}
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
public class DataLineParser implements ILineParser<ISynset> {

	private static DataLineParser fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static DataLineParser getInstance() {
		if (fInstance == null) fInstance = new DataLineParser();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected DataLineParser() {}
	
	/**
	 * Retrieval of the verb frames for the {@link #parseLine(String)} method is
	 * implemented in its own method for ease of subclassing.
	 */
	protected IVerbFrame resolveVerbFrame(int frameNum) {
		return VerbFrame.getFrame(frameNum);
	}
	
	/**
	 * Retrieval of the lexical file objects for the {@link #parseLine(String)}
	 * method is implemented in its own method for ease of subclassing.
	 */
	protected ILexFile resolveLexicalFile(int lexFileNum){
		ILexFile lexFile = LexFile.getLexicalFile(lexFileNum);
		return (lexFile == null) ? UnknownLexFile.getUnknownLexicalFile(lexFileNum) : lexFile;
	}
	
	/**
	 * Retrieval of the pointer objects for the {@link #parseLine(String)}
	 * method is implemented in its own method for ease of subclassing.
	 */
	protected IPointer resolvePointer(String symbol, POS pos){
		return Pointer.getPointerType(symbol, pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String)
	 */
	public ISynset parseLine(String line) {
		if (line == null) throw new MisformattedLineException(line);

		try {
			StringTokenizer tokenizer = new StringTokenizer(line, " ");

			// Get offset
			int offset = Integer.parseInt(tokenizer.nextToken());

			// Consume lex_filenum
			int lex_filenum = Integer.parseInt(tokenizer.nextToken());
			ILexFile lexFile = resolveLexicalFile(lex_filenum);
			
			// Get part of speech
			POS synset_pos;
			char synset_tag = tokenizer.nextToken().charAt(0);
			synset_pos = POS.getPartOfSpeech(synset_tag);

			ISynsetID synsetID = new SynsetID(offset, synset_pos);

			// Determine if it is an adjective satellite
			boolean isAdjSat = (synset_tag == 's');
			
			// a synset is an adjective head if it is the 00 lexical file
			// is not a adjective satellite, and it has an antonym
			// the Wordnet definition says head synsets have to have an antonym,
			// but this is actually violated (perhaps mistakenly) in a small number of cases,
			// e.g., in Wordnet 3.0:
			// 01380267 aerial (no antonyms), with satellite 01380571 free-flying
			// 01380721 marine (no antonyms), with satellite 01380926 deep-sea, etc.
			boolean isAdjHead = !isAdjSat && lex_filenum == 0;

			// Get word count
			int wordCount = Integer.parseInt(tokenizer.nextToken(), 16);

			// Get words
			String lemma;
			AdjMarker marker;
			int lexID;
			WordProxy[] wordProxies = new WordProxy[wordCount];
			for (int i = 0; i < wordCount; i++) {
				// consume next word
				lemma = tokenizer.nextToken();

				// if it is an adjective, it may be followed by a marker
				marker = null;
				if (synset_pos == POS.ADJECTIVE) {
					for(AdjMarker adjMarker : AdjMarker.values()){
						if(lemma.endsWith(adjMarker.getSymbol())){
							marker = adjMarker;
							lemma = lemma.substring(0, lemma.length()-adjMarker.getSymbol().length());
						}
					}
				}

				// parse lex_id
				lexID = Integer.parseInt(tokenizer.nextToken(), 16);

				wordProxies[i] = new WordProxy(i + 1, lemma, lexID, marker);
			}

			// Get pointer count
			int pointerCount = Integer.parseInt(tokenizer.nextToken());

			Map<IPointer, ArrayList<ISynsetID>> synsetPointerMap = null;

			// Get pointers
			IPointer pointer_type;
			int target_offset;
			POS target_pos;
			int source_target_num, source_num, target_num;
			ArrayList<ISynsetID> pointerList;
			IWordID target_word_id;
			ISynsetID target_synset_id;
			for (int i = 0; i < pointerCount; i++) {
				// get pointer symbol
				pointer_type = resolvePointer(tokenizer.nextToken(), synset_pos);

				// get synset target offset
				target_offset = Integer.parseInt(tokenizer.nextToken());

				// get target synset part of speech
				target_pos = POS.getPartOfSpeech(tokenizer.nextToken().charAt(0));

				target_synset_id = new SynsetID(target_offset, target_pos);

				// get source/target numbers
				source_target_num = Integer.parseInt(tokenizer.nextToken(), 16);

				// this is a semantic pointer if the source/target numbers are
				// zero
				if (source_target_num == 0) {
					if (synsetPointerMap == null) synsetPointerMap = new HashMap<IPointer, ArrayList<ISynsetID>>();
					pointerList = synsetPointerMap.get(pointer_type);
					if (pointerList == null) {
						pointerList = new ArrayList<ISynsetID>();
						synsetPointerMap.put(pointer_type, pointerList);
					}
					pointerList.add(target_synset_id);
				}
				else {
					// this is a lexical pointer
					source_num = source_target_num / 256;
					target_num = source_target_num & 255;
					target_word_id = new WordID(target_synset_id, target_num);
					wordProxies[source_num - 1].addRelatedWord(pointer_type, target_word_id);
				}
			}
			
			// trim pointer lists
			if(synsetPointerMap != null){
				for(ArrayList<ISynsetID> list : synsetPointerMap.values()) list.trimToSize();
			}

			// parse verb frames
			if (synset_pos == POS.VERB) {
				int frame_num, word_num;
				int verbFrameCount = Integer.parseInt(tokenizer.nextToken());
				IVerbFrame frame;
				for (int i = 0; i < verbFrameCount; i++) {
					// Consume '+'
					tokenizer.nextToken();
					// Get frame number
					frame_num = Integer.parseInt(tokenizer.nextToken());
					frame = resolveVerbFrame(frame_num);
					// Get word number
					word_num = Integer.parseInt(tokenizer.nextToken(), 16);
					if (word_num > 0) {
						wordProxies[word_num - 1].addVerbFrame(frame);
					}
					else {
						for (WordProxy proxy : wordProxies) {
							proxy.addVerbFrame(frame);
						}
					}
				}
			}

			// Get gloss
			String gloss = "";
			int index = line.indexOf('|');
			if (index > 0) {
				gloss = line.substring(index + 2).trim();
			}
			
			// create synset and words
			IWord[] wordArray = new IWord[wordProxies.length];
			List<IWord> words = Arrays.asList(wordArray);
			ISynset synset = new Synset(synsetID, lexFile, isAdjSat, isAdjHead, gloss, words, synsetPointerMap);
			for (int i = 0; i < wordProxies.length; i++){
				wordProxies[i].trimLists();
				wordArray[i] = wordProxies[i].instantiateWord(synset);
			}
			return synset;
			
		} catch (NumberFormatException e) {
			throw new MisformattedLineException(line, e);
		} catch (NoSuchElementException e) {
			throw new MisformattedLineException(line, e);
		}
	}

	/**
	 * This inner class is used to hold information about words before they are
	 * instantiated.
	 * 
	 * @author Mark A. Finlayson
	 * @version 1.2, Nov. 16, 2007
	 * @since 1.5.0
	 */
	protected class WordProxy {

		final int num, lexID;
		final String lemma;
		final AdjMarker marker;

		Map<IPointer, ArrayList<IWordID>> relatedWords;
		ArrayList<IVerbFrame> verbFrames;

		public WordProxy(int num, String lemma, int lexID, AdjMarker marker) {
			this.num = num;
			this.lemma = lemma;
			this.lexID = lexID;
			this.marker = marker;
		}

		public void addRelatedWord(IPointer type, IWordID word_id) {
			if (type == null | word_id == null) throw new NullPointerException();
			if (relatedWords == null) relatedWords = new HashMap<IPointer, ArrayList<IWordID>>();
			ArrayList<IWordID> words = relatedWords.get(type);
			if (words == null) {
				words = new ArrayList<IWordID>();
				relatedWords.put(type, words);
			}
			words.add(word_id);
		}

		public void addVerbFrame(IVerbFrame frame) {
			if (verbFrames == null) verbFrames = new ArrayList<IVerbFrame>();
			verbFrames.add(frame);
		}

		public IWord instantiateWord(ISynset synset) {
			return new Word(synset, num, lemma, lexID, marker, verbFrames, relatedWords);
		}
		
		public void trimLists(){
			if(relatedWords != null){
				for(ArrayList<IWordID> list : relatedWords.values()) list.trimToSize();
			}
			if(verbFrames != null) verbFrames.trimToSize();
		}

	}
}