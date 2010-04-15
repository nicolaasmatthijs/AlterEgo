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

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;

import edu.mit.jwi.data.ContentType;
import edu.mit.jwi.data.FileProvider;
import edu.mit.jwi.data.IContentType;
import edu.mit.jwi.data.IDataProvider;
import edu.mit.jwi.data.IDataSource;
import edu.mit.jwi.data.parse.ILineParser;
import edu.mit.jwi.item.ExceptionEntry;
import edu.mit.jwi.item.ExceptionEntryID;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IHasPOS;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IVersion;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IndexWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Synset;
import edu.mit.jwi.item.SynsetID;

/**
 * Basic implementation of the {@code IDictionary} interface. A path to the
 * Wordnet dictionary files must be provided. If no {@code IDataProvider} is
 * specified, it uses the default implementation provided with the distribution.
 * <p>
 * This dictionary caches items it retrieves. The cache is limited in size by
 * default. See {@link edu.mit.jwi.ItemCache#DEFAULT_MAXIMUM_CAPACITY}. If you
 * find this default maximum size does suit your purposes, you can retrieve the
 * cache via the {@link #getCache()} method and set the maximum cache size via
 * the {@link edu.mit.jwi.ItemCache#setMaximumCapacity(int)} method. If you have
 * a specialized implementation for your cache, you can subclass the
 * {@code Dictionary} class and override the {@link #createCache()} method.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5
 * @since 1.5.0 (Nov. 16, 2007)
 */
public class Dictionary implements IDataSourceDictionary {
	
	private final ItemCache fCache = createCache();
	private IDataProvider fDataProvider = null;

	/**
	 * Constructs a default dictionary that uses the default, filesystem data
	 * provider.
	 */
	public Dictionary(URL url) {
		this(new FileProvider(url));
	}

	/**
	 * Constructs a dictionary with a caller-specified {@code IDataProvider}.
	 * 
	 * @throws NullPointerException
	 *             if the specified data provider is <code>null</code>
	 */
	public Dictionary(IDataProvider provider) {
		if(provider == null) throw new NullPointerException();
		setDataProvider(provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDataSourceDictionary#setDataProvider(edu.mit.jwi.data.IDataProvider)
	 */
	public boolean setDataProvider(IDataProvider provider) {
		if (provider == null || isOpen()) return false;
		fDataProvider = provider;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDataSourceDictionary#getDataProvider()
	 */
	public IDataProvider getDataProvider() {
		return fDataProvider;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasVersion#getVersion()
	 */
	public IVersion getVersion() {
		checkOpen();
		return fDataProvider.getVersion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#open()
	 */
	public boolean open() {
		try {
			fDataProvider.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		getCache().init();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#close()
	 */
	public void close() {
		getCache().clear();
		fDataProvider.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#isOpen()
	 */
	public boolean isOpen() {
		if (fDataProvider == null) return false;
		return fDataProvider.isOpen();
	}

	/**
	 * An internal method for assuring compliance with the dictionary interface
	 * that says that methods will throw {@code DictionaryClosedException}s if
	 * the dictionary has not yet been opened.
	 * 
	 * @throws DictionaryClosedException
	 *             if the dictionary is closed.
	 */
	protected void checkOpen() {
		if (!isOpen()) throw new DictionaryClosedException();
	}
	
	/**
	 * This operation creates the cache that is used by the dictionary. It is
	 * set inside it's own method for ease of subclassing. It is called only
	 * when an instance of this class is created, to assign a value to the final
	 * {@code fCache} field. It is marked protected for ease of subclassing.
	 */
	protected ItemCache createCache(){
		return new ItemCache();
	}

	/**
	 * Returns the cache used by this dictionary, so that it may
	 * be configured or manipulated directly.
	 */
	public ItemCache getCache() {
		return fCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getIndexWord(java.lang.String,
	 *      edu.mit.jwi.item.POS)
	 */
	public IIndexWord getIndexWord(String lemma, POS pos) {
		checkOpen();
		return getIndexWord(new IndexWordID(lemma, pos));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getIndexWord(edu.mit.jwi.item.IIndexWordID)
	 */
	public IIndexWord getIndexWord(IIndexWordID id) {
		checkOpen();
		if (id == null) return null;

		IIndexWord result = getCache().retrieveIndexWord(id);

		if (result == null) {
			IContentType<IIndexWord> content = resolveIndexContentType(id.getPOS());
			IDataSource<?> file = fDataProvider.getSource(content);
			String line = file.getLine(id.getLemma());
			if (line == null) return null;
			result = content.getDataType().getParser().parseLine(line);
			getCache().cacheIndexWord(result);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getWord(edu.mit.jwi.item.IWordID)
	 */
	public IWord getWord(IWordID id) {
		checkOpen();
		if (id == null) return null;

		IWord result = getCache().retrieveWord(id);

		if (result == null) {
			ISynset synset = getSynset(id.getSynsetID());
			if(synset == null) return null;
			
			// One or the other of the WordID number or lemma may not exist,
			// depending on whence the word id came, so we have to check 
			// them before trying.
			if (id.getSenseNumber() > 0) {
				result = synset.getWords().get(id.getSenseNumber() - 1);
			} 
			else if (id.getLemma() != null) {
				for (IWord word : synset.getWords()) {
					if (word.getLemma().equalsIgnoreCase(id.getLemma())) {
						result = word;
						break;
					}
				}
			} 
			else {
				throw new IllegalArgumentException("Not enough information in IWordID instance to retrieve word.");
			}
			// no need to cache result, as this will have been
			// done in the call to getSynset()
		}

		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getWord(edu.mit.jwi.item.ISenseKey)
	 */
	public IWord getWord(ISenseKey key) {
		checkOpen();
		if (key == null) return null;

		// try to get it from the cache first
		IWord word = getCache().retrieveWord(key);
		if(word != null) return word;
		
		// no need to cache result from the following calls as this will have been
		// done in the call to getSynset()
		ISenseEntry entry = getSenseEntry(key);
		if(entry != null){
			ISynset synset = getSynset(new SynsetID(entry.getOffset(), entry.getPOS()));
			if(synset != null){
				for(IWord synonym : synset.getWords()){
					if(synonym.getSenseKey().equals(key)) return synonym;
				}
			}
		}
			
		if(word == null){
			// sometimes the sense.index file doesn't have the sense key entry
			// so try an alternate method of retrieving words by sense keys
			// We have to search the synonyms of the words returned from the
			// index word search because some synsets have lemmas that differ only in case
			// e.g., {earth, Earth} or {south, South}, and so separate entries
			// are not found in the index file
			IIndexWord indexWord = getIndexWord(key.getLemma(), key.getPOS());
			if(indexWord != null){
				IWord possibleWord;
				for(IWordID wordID : indexWord.getWordIDs()){
					possibleWord = getWord(wordID);
					for(IWord synonym : possibleWord.getSynset().getWords()){
						if(synonym.getSenseKey().equals(key)){
							word = synonym;
							if(synonym.getLemma().equals(key.getLemma())) return synonym;
						}
					}
				}
			}
		}

		return word;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getSenseEntry(edu.mit.jwi.item.ISenseKey)
	 */
	public ISenseEntry getSenseEntry(ISenseKey key) {
		checkOpen();
		if (key == null) return null;

		ISenseEntry entry = getCache().retrieveSenseEntry(key);
		
		if(entry == null){
			IContentType<ISenseEntry> content = resolveSenseContentType();
			IDataSource<ISenseEntry> file = fDataProvider.getSource(content);
			String line = file.getLine(key.toString());
			if (line == null) return null;
			entry = content.getDataType().getParser().parseLine(line);
			if(entry == null) return null;
			
			// cache result
			getCache().cacheSenseEntry(entry);
		}
		
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.dict.IDictionary#getSynset(edu.mit.wordnet.core.data.ISynsetID)
	 */
	public ISynset getSynset(ISynsetID id) {
		checkOpen();
		if (id == null) return null;

		ISynset result = getCache().retrieveSynset(id);

		if (result == null) {
			IContentType<ISynset> content = resolveDataContentType(id.getPOS());
			IDataSource<ISynset> file = fDataProvider.getSource(content);
			String zeroFilledOffset = Synset.zeroFillOffset(id.getOffset());
			String line = file.getLine(zeroFilledOffset);
			if (line == null) return null;
			result = content.getDataType().getParser().parseLine(line);
			if(result == null) return null;

			setHeadWord(result);
			
			// cache result
			getCache().cacheSynset(result);
			for(IWord word : result.getWords()) getCache().cacheWord(word);
		}

		return result;
	}
	
	/**
	 * This method sets the head word on the specified synset by searching in
	 * the dictionary to find the head of its cluster. We will assume the head
	 * is the first adjective head synset related by an '&' pointer (SIMILAR_TO)
	 * to this synset.
	 */
	protected void setHeadWord(ISynset synset){

		// head words are only needed for adjective satellites
		if(!synset.isAdjectiveSatellite()) return;
		
		// go find the head word
		ISynset headSynset;
		IWord headWord = null;
		for(ISynsetID simID : synset.getRelatedSynsets(Pointer.SIMILAR_TO)){
			headSynset = getSynset(simID);
			// assume first 'similar' adjective head is the right one
			if(headSynset.isAdjectiveHead()){
				headWord = headSynset.getWords().get(0);
				break;
			}
		}
		if(headWord == null) return;
		
		// set head word, if we found it
		String headLemma = headWord.getLemma();
		
		// version 1.6 of Wordnet adds the adjective marker symbol 
		// on the end of the head word lemma
		IVersion ver = getVersion();
		boolean isVer16 = (ver == null) ? false :  ver.getMajorVersion() == 1 && ver.getMinorVersion() == 6;
		if(isVer16 && headWord.getAdjectiveMarker() != null) headLemma += headWord.getAdjectiveMarker().getSymbol(); 
		
		// set the head word for each word
		for(IWord word : synset.getWords()){
			if(word.getSenseKey().needsHeadSet()) word.getSenseKey().setHead(headLemma, headWord.getLexicalID());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getExceptionEntry(java.lang.String,
	 *      edu.mit.jwi.item.POS)
	 */
	public IExceptionEntry getExceptionEntry(String surfaceForm, POS pos) {
		return getExceptionEntry(new ExceptionEntryID(surfaceForm, pos));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getExceptionEntry(edu.mit.jwi.item.IExceptionEntryID)
	 */
	public IExceptionEntry getExceptionEntry(IExceptionEntryID id) {
		checkOpen();
		if (id == null) return null;

		IExceptionEntry result = getCache().retrieveExceptionEntry(id);

		if (result == null) {
			IContentType<IExceptionEntryProxy> content = resolveExceptionContentType(id.getPOS());
			IDataSource<IExceptionEntryProxy> file = fDataProvider.getSource(content);
			if(file == null) return null; // fix for bug 010
			String line = file.getLine(id.getSurfaceForm());
			if (line == null) return null;
			IExceptionEntryProxy proxy = content.getDataType().getParser().parseLine(line);
			if (proxy != null) {
				result = new ExceptionEntry(proxy, id.getPOS());
				getCache().cacheExceptionEntry(result); // moved this inside if statement while fixing bug 010
			}
		}

		return result;
	}
	
	/** 
	 * This method retrieves the appropriate content type for exception entries,
	 * and is marked protected for ease of subclassing.
	 */
	protected IContentType<IIndexWord> resolveIndexContentType(POS pos){
		return ContentType.getIndexContentType(pos);
	}
	
	/** 
	 * This method retrieves the appropriate content type for exception entries,
	 * and is marked protected for ease of subclassing.
	 */
	protected IContentType<ISynset> resolveDataContentType(POS pos){
		return ContentType.getDataContentType(pos);
	}
	
	/** 
	 * This method retrieves the appropriate content type for exception entries,
	 * and is marked protected for ease of subclassing.
	 */
	protected IContentType<IExceptionEntryProxy> resolveExceptionContentType(POS pos){
		return ContentType.getExceptionContentType(pos);
	}
	
	/** 
	 * This method retrieves the appropriate content type for sense entries,
	 * and is marked protected for ease of subclassing.
	 */
	protected IContentType<ISenseEntry> resolveSenseContentType(){
		return ContentType.SENSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getIndexWordIterator(edu.mit.jwi.item.POS)
	 */
	public Iterator<IIndexWord> getIndexWordIterator(POS pos) {
		checkOpen();
		return new IndexFileIterator(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getIndexWordPatternIterator(edu.mit.jwi.item.POS,
	 *      java.lang.String)
	 */
	public Iterator<IIndexWord> getIndexWordPatternIterator(POS pos, String pattern) {
		checkOpen();
		return new IndexFilePatternIterator(pos, pattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getSynsetIterator(edu.mit.jwi.item.POS)
	 */
	public Iterator<ISynset> getSynsetIterator(POS pos) {
		checkOpen();
		return new DataFileIterator(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getExceptionEntryIterator(edu.mit.jwi.item.POS)
	 */
	public Iterator<IExceptionEntry> getExceptionEntryIterator(POS pos) {
		checkOpen();
		return new ExceptionFileIterator(pos);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.IDictionary#getSenseEntryIterator()
	 */
	public Iterator<ISenseEntry> getSenseEntryIterator() {
		checkOpen();
		return new SenseEntryFileIterator();
	}

	/**
	 * Returns the initial part of this string that <b>must</b> match, i.e.,
	 * the part of the string up to but not including the first wildcard.
	 */
	public static String getPatternRoot(String pattern, boolean ignoreWildCards) {
		if (!ignoreWildCards) {
			// strip off first wildcard and everything after
			int idxQ = pattern.indexOf('?');
			if (idxQ == -1) idxQ = pattern.length();
			int idxS = pattern.indexOf('*');
			if (idxS == -1) idxS = pattern.length();
			int idx = Math.min(idxQ, idxS);
			return pattern.substring(0, idx);
		}
		else {
			return pattern;
		}
	}

	/**
	 * Abstract class used for iterating over line-based files.
	 */
	public abstract class FileIterator<T, N> implements Iterator<N>, IHasPOS {

		protected final IDataSource<T> fFile;
		protected final Iterator<String> iterator;
		protected final ILineParser<T> fParser;
		protected String currentLine;

		public FileIterator(IContentType<T> content) {
			this(content, null);
		}

		public FileIterator(IContentType<T> content, String startKey) {
			this.fFile = fDataProvider.getSource(content);
			this.fParser = content.getDataType().getParser();
			this.iterator = fFile.iterator(startKey);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.data.IHasPartOfSpeech#getPartOfSpeech()
		 */
		public POS getPOS() {
			return fFile.getContentType().getPOS();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return iterator.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public N next() {
			currentLine = iterator.next();
			return parseLine(currentLine);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			iterator.remove();
		}

		/** Parses the line using a parser provided at construction time */
		public abstract N parseLine(String line);
	}
	
	/** 
	 * TODO: Write Comment
	 *
	 * @author Mark A. Finlayson
	 * @version 2.1.5
	 * @since JWI 2.1.5
	 */
	public abstract class FileIterator2<T> extends FileIterator<T, T> {

		/** 
		 * TODO: Write Comment
		 *
		 * @since JWI 2.1.5
		 */
		public FileIterator2(IContentType<T> content) {
			super(content);
		}
		
		/** 
		 * TODO: Write Comment
		 *
		 * @since JWI 2.1.5
		 */
		public FileIterator2(IContentType<T> content, String startKey) {
			super(content, startKey);
		}
		
	}

	/**
	 * Iterates over index files.
	 */
	public class IndexFileIterator extends FileIterator2<IIndexWord> {

		public IndexFileIterator(POS pos) {
			this(pos, "");
		}

		public IndexFileIterator(POS pos, String pattern) {
			super(resolveIndexContentType(pos), pattern);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.core.base.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public IIndexWord parseLine(String line) {
			return fParser.parseLine(line);
		}

	}

	/**
	 * Iterates over index files. This is a look-ahead iterator that uses a
	 * pattern.
	 */
	public class IndexFilePatternIterator extends IndexFileIterator {

		IIndexWord previous, next;
		StringMatcher matcher = null;
		Comparator<String> fComparator;
		String fPatternRoot;

		public IndexFilePatternIterator(POS pos, String pattern) {
			super(pos, getPatternRoot(pattern, false));
			if (pattern == null) throw new IllegalArgumentException("Pattern cannot be null in IndexFilePatterIterator");
			matcher = new StringMatcher(pattern, true, false);
			fPatternRoot = matcher.getPatternRoot();
			fComparator = fFile.getContentType().getLineComparator();
			advance();
		}

		/**
		 * Advances to the next match that will be returned by the iterator.
		 */
		protected void advance() {
			do {
				currentLine = iterator.hasNext() ? iterator.next() : null;
				if (currentLine == null) {
					next = null;
					return;
				}
				next = parseLine(currentLine);
				if (next == null) break;
				if (!next.getLemma().startsWith(fPatternRoot)) {
					next = null;
					break;
				}
			} while (!matcher.match(next.getLemma()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return next != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public IIndexWord next() {
			previous = next;
			advance();
			return previous;
		}
	}
	
	/**
	 * Iterates over the sense file.
	 */
	public class SenseEntryFileIterator extends FileIterator2<ISenseEntry> {

		public SenseEntryFileIterator() {
			super(resolveSenseContentType());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.core.base.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public ISenseEntry parseLine(String line) {
			return fParser.parseLine(line);
		}

	}

	/**
	 * Iterates over data files.
	 */
	public class DataFileIterator extends FileIterator2<ISynset> {

		public DataFileIterator(POS pos) {
			super(resolveDataContentType(pos));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.core.base.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public ISynset parseLine(String line) {
			if(getPOS() == POS.ADJECTIVE){
				ISynset synset = fParser.parseLine(line);
				setHeadWord(synset);
				return synset;
			} else {
				return fParser.parseLine(line);
			}
		}

	}

	/**
	 * Iterates over exception files.
	 */
	public class ExceptionFileIterator extends FileIterator<IExceptionEntryProxy, IExceptionEntry> {

		public ExceptionFileIterator(POS pos) {
			super(resolveExceptionContentType(pos));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.mit.wordnet.dict.Dictionary.FileIterator#parseLine(java.lang.String)
		 */
		public IExceptionEntry parseLine(String line) {
			IExceptionEntryProxy proxy = fParser.parseLine(line);
			return (proxy == null) ? null : new ExceptionEntry(proxy, getPOS());
		}
	}


	



}
