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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.IExceptionEntryID;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IIndexWordID;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;

/**
 * A Synchronized LRU cache for objects in JWI
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public class ItemCache {
	
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final int DEFAULT_MAXIMUM_CAPACITY = 500;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * Flag that records whether caching is enabled for this dictionary. Default
	 * starting state is <code>true</code>.
	 */
	boolean fCachingEnabled = true;
	
	/**
	 * Initial capacity of the caches. If this is set to less than zero, then
	 * the system default is used.
	 */
	int fInitialCapacity;

	/**
	 * Maximum capacity of the caches. If this is set to less than zero, then
	 * the cache size is unlimited.
	 */
	int fMaximumCapacity;
	
	// The caches themselves
	protected Map<IIndexWordID, IIndexWord> indexCache;
	protected Map<IWordID, IWord> wordCache;
	protected Map<ISenseKey, IWord> keyCache;
	protected Map<ISynsetID, ISynset> synsetCache;
	protected Map<IExceptionEntryID, IExceptionEntry> entryCache;
	protected Map<ISenseKey, ISenseEntry> senseCache;

	/**
	 * Default constructor that initializes the dictionary with caching enabled.
	 */
	public ItemCache() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAXIMUM_CAPACITY, true);
	}

	/**
	 * Caller can specify both the initial size, maximum size, and the initial state
	 * of caching.
	 */
	public ItemCache(int initialCapacity, int maxCapacity, boolean enabled) {
		setInitialCapacity(initialCapacity);
		setMaximumCapacity(maxCapacity);
		setCachingEnabled(enabled);
	}
	
	/** 
	 * Initializes the cache.
	 */
	public void init(){
		clear();
		if(fInitialCapacity < 0){
			indexCache = this.<IIndexWordID,IIndexWord>makeCache();
			wordCache = this.<IWordID,IWord>makeCache();
			keyCache = this.<ISenseKey,IWord>makeCache();
			synsetCache = this.<ISynsetID,ISynset>makeCache();
			entryCache = this.<IExceptionEntryID,IExceptionEntry>makeCache();
			senseCache = this.<ISenseKey,ISenseEntry>makeCache();
		} else {
			indexCache = this.<IIndexWordID,IIndexWord>makeCache(fInitialCapacity);
			wordCache = this.<IWordID,IWord>makeCache(fInitialCapacity);
			keyCache = this.<ISenseKey,IWord>makeCache(fInitialCapacity);
			synsetCache = this.<ISynsetID,ISynset>makeCache(fInitialCapacity);
			entryCache = this.<IExceptionEntryID,IExceptionEntry>makeCache(fInitialCapacity);
			senseCache = this.<ISenseKey,ISenseEntry>makeCache(fInitialCapacity);
		}
	}
	
	protected <K,V> Map<K,V> makeCache(){
		return makeCache(DEFAULT_INITIAL_CAPACITY);
	}
	
	protected <K,V> Map<K,V> makeCache(int initialCapacity){
		Map<K,V> map = new LinkedHashMap<K,V>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true);
		return Collections.synchronizedMap(map);
	}
	
	/** 
	 * Removes all entries from the cache.
	 */
	public void clear(){
		if(indexCache != null) indexCache.clear();
		if(wordCache != null) wordCache.clear();
		if(keyCache != null) keyCache.clear();
		if(synsetCache != null) synsetCache.clear();
		if(entryCache != null) entryCache.clear();
		if(senseCache != null) senseCache.clear();
	}

	public boolean isCachingEnabled() {
		return fCachingEnabled;
	}
	
	public int getInitialCapacity() {
		return fInitialCapacity;
	}
	
	public int getMaximumCapacity() {
		return fMaximumCapacity;
	}

	public void setCachingEnabled(boolean cachingEnabled) {
		fCachingEnabled = cachingEnabled;
	}
	
	public void setInitialCapacity(int capacity){
		fInitialCapacity = capacity;
	}
	
	public void setMaximumCapacity(int capacity) {
		int oldCapacity = fMaximumCapacity;
		fMaximumCapacity = capacity;
		if (fMaximumCapacity < 0 || oldCapacity <= fMaximumCapacity) return;
		reduceCacheSize(indexCache);
		reduceCacheSize(wordCache);
		reduceCacheSize(keyCache);
		reduceCacheSize(synsetCache);
		reduceCacheSize(entryCache);
		reduceCacheSize(senseCache);
	}
	
	/** 
	 * Returns the number of total items stored in all internal caches.
	 */
	public int size(){
		return indexCache.size() + wordCache.size() + synsetCache.size() + entryCache.size()+ senseCache.size();
	}
	
	public void cacheIndexWord(IIndexWord indexWord){
		if(!isCachingEnabled()) return;
		indexCache.put(indexWord.getID(), indexWord);
		reduceCacheSize(indexCache);
	}

	public void cacheWord(IWord word){
		if(!isCachingEnabled()) return;
		wordCache.put(word.getID(), word);
		keyCache.put(word.getSenseKey(), word);
		reduceCacheSize(wordCache);
		reduceCacheSize(keyCache);
	}
	
	public void cacheSynset(ISynset synset){
		if(!isCachingEnabled()) return;
		synsetCache.put(synset.getID(), synset);
		reduceCacheSize(synsetCache);
	}

	public void cacheExceptionEntry(IExceptionEntry entry){
		if(!isCachingEnabled()) return;
		entryCache.put(entry.getID(), entry);
		reduceCacheSize(entryCache);
	}
	
	public void cacheSenseEntry(ISenseEntry entry){
		if(!isCachingEnabled()) return;
		senseCache.put(entry.getSenseKey(), entry);
		reduceCacheSize(senseCache);
	}
	
	protected void reduceCacheSize(Map<?,?> cache){
		if (cache == null || cache.size() < fMaximumCapacity)  return;
		synchronized(cache){
			int remove = cache.size() - fMaximumCapacity;
			Iterator<?> itr = cache.keySet().iterator();
			for (int i = 0; i <= remove; i++) {
				if (!itr.hasNext()) break;
				itr.next();
				itr.remove();
			}
		}
	}

	public IIndexWord retrieveIndexWord(IIndexWordID id){
		return indexCache.get(id);
	}

	public IWord retrieveWord(IWordID id){
		return wordCache.get(id);
	}
	
	public IWord retrieveWord(ISenseKey key){
		return keyCache.get(key);
	}
	
	public ISynset retrieveSynset(ISynsetID id){
		return synsetCache.get(id);
	}
	
	public IExceptionEntry retrieveExceptionEntry(IExceptionEntryID id){
		return entryCache.get(id);
	}
	
	public ISenseEntry retrieveSenseEntry(ISenseKey key){
		return senseCache.get(key);
	}
}