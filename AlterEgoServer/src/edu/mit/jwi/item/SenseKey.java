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


/**
 * Concrete, default implementation of the {@link ISenseKey} interface.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 11, 2008
 * @since 1.5.0
 */
public class SenseKey implements ISenseKey {
	
	private final String lemma;
	private final int lexID;
	private final POS pos;
	private final boolean isAdjSat;
	private final ILexFile lexFile;
	
	private boolean isHeadSet;
	private String headLemma = null;
	private int headLexID = -1;
	private String toString;
	
	/**
	 * @throws NullPointerException if any of the arguments are <code>null</code>
	 */
	public SenseKey(String lemma, int lexID, ISynset synset){
		this(lemma, lexID, synset.getPOS(), synset.isAdjectiveSatellite(), synset.getLexicalFile());
	}
	
	/**
	 * @throws NullPointerException if any of the arguments are <code>null</code>
	 */
	public SenseKey(String lemma, int lexID, POS pos, boolean isAdjSat, ILexFile lexFile){
		if(pos == null || lexFile == null) throw new NullPointerException();
		
		// all sense key lemmas are in lower case
		// also checks for null
		this.lemma = lemma.toLowerCase(); 
		this.lexID = lexID;
		this.pos = pos;
		this.isAdjSat = isAdjSat;
		this.lexFile = lexFile;
		this.isHeadSet = !isAdjSat;
	}
	
	/**
	 * @throws NullPointerException if any of the arguments are <code>null</code>
	 */
	public SenseKey(String lemma, int lexID, POS pos, boolean isAdjSat, ILexFile lexFile, String originalKey){
		this(lemma, lexID, pos, isAdjSat, lexFile);
		if(originalKey == null) throw new NullPointerException();
		this.toString = originalKey;
	}
	
	/**
	 * Constructs a new sense key that is fully specified.  If the head lemma is <code>null</code>, 
	 * this 
	 */
	public SenseKey(String lemma, int lexID, POS pos, ILexFile lexFile, String headLemma, int headLexID, String originalKey){
		this(lemma, lexID, pos, (headLemma != null), lexFile);
		
		if(headLemma == null){
			isHeadSet = true;
		} else {
			setHead(headLemma, headLexID);
		}
		
		if(originalKey == null) throw new NullPointerException();
		this.toString = originalKey;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getLemma()
	 */
	public String getLemma() {
		return lemma;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getLexicalID()
	 */
	public int getLexicalID() {
		return lexID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
	public POS getPOS() {
		return pos;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getSynsetType()
	 */
	public int getSynsetType() {
		return isAdjSat ? 5 : pos.getNumber();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#isAdjectiveSatellite()
	 */
	public boolean isAdjectiveSatellite() {
		return isAdjSat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getLexicalFile()
	 */
	public ILexFile getLexicalFile() {
		return lexFile;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#setHead(java.lang.String, int)
	 */
	public void setHead(String headLemma, int headLexID) {
		if(isHeadSet) throw new IllegalStateException();
		
		Word.checkLexicalID(headLexID);
		if(headLemma.trim().length() == 0) throw new IllegalArgumentException();
		
		this.headLemma = headLemma;
		this.headLexID = headLexID;
		this.isHeadSet = true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getHeadWord()
	 */
	public String getHeadWord() {
		if(!isHeadSet) throw new IllegalStateException("Head word and id not yet set");
		return headLemma;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#getHeadID()
	 */
	public int getHeadID() {
		if(!isHeadSet) throw new IllegalStateException("Head word and id not yet set");
		return headLexID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseKey#needsHeadSet()
	 */
	public boolean needsHeadSet(){
		return !isHeadSet;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ISenseKey key) {
		int cmp;
		
		// first sort alphabetically by lemma
		cmp = this.getLemma().compareTo(key.getLemma());
		if(cmp != 0) return cmp;
		
		// then sort by synset type
		cmp = Float.compare(this.getSynsetType(), key.getSynsetType());
		if(cmp != 0) return cmp;
		
		// then sort by lex_filenum
		cmp = Float.compare(this.getLexicalFile().getNumber(), key.getLexicalFile().getNumber());
		if(cmp != 0) return cmp;
		
		// then sort by lex_id
		cmp = Float.compare(this.getLexicalID(), key.getLexicalID());
		if(cmp != 0) return cmp;
		
		if(!this.isAdjectiveSatellite() && !key.isAdjectiveSatellite()) return 0;
		if(!this.isAdjectiveSatellite() & key.isAdjectiveSatellite()) return -1;
		if(this.isAdjectiveSatellite() & !key.isAdjectiveSatellite()) return 1;
		
		// then sort by head_word
		cmp = this.getHeadWord().compareTo(key.getHeadWord());
		if(cmp != 0) return cmp;
		
		// finally by head_id
		return Float.compare(this.getHeadID(), key.getHeadID());
	}

	/* (non-Javadoc) @see java.lang.Object#toString() */
	public String toString(){
		if(!isHeadSet) throw new IllegalStateException("Head word and id not yet set");
		if(toString == null) toString = toString(this);
		return toString;
	}
	
	private static final char colon = ':';
	private static final char percent = '%';
	private static final String unknown = "??";

	/**
	 * This utility method takes an {@link ISenseKey} object and produces
	 * it's standard string representation.
	 * 
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 */
	public static String toString(ISenseKey key){
		
		// figure out appropriate size
		int size = key.getLemma().length() + 10;
		if(key.isAdjectiveSatellite()) size += key.getHeadWord().length() + 2;
		
		// allocate builder
		StringBuilder sb = new StringBuilder(size);
		
		// make string
		sb.append(key.getLemma().toLowerCase());
		sb.append(percent);
		sb.append(key.getSynsetType());
		sb.append(colon);
		sb.append(LexFile.getLexicalFileNumberString(key.getLexicalFile().getNumber()));
		sb.append(colon);
		sb.append(Word.getLexicalIDForSenseKey(key.getLexicalID()));
		sb.append(colon);
		if(key.isAdjectiveSatellite()){
			if(key.needsHeadSet()){
				sb.append(unknown);
			} else {
				sb.append(key.getHeadWord());
			}
		}
		sb.append(colon);
		if(key.isAdjectiveSatellite()){
			if(key.needsHeadSet()){
				sb.append(unknown);
			} else {
				sb.append(Word.getLexicalIDForSenseKey(key.getHeadID()));
			}
		}
		return sb.toString();
	}

	/* (non-Javadoc) @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lemma.hashCode();
		result = prime * result + lexID;
		result = prime * result + pos.hashCode();
		result = prime * result + lexFile.hashCode();
		result = prime * result + (isAdjSat ? 1231 : 1237);
		if(isAdjSat){
			result = prime * result + (headLemma == null ? 0 : headLemma.hashCode());
			result = prime * result + headLexID;	
		}
		return result;
	}

	/* (non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if(!(obj instanceof ISenseKey)) return false;

		final ISenseKey other = (ISenseKey)obj;
		if(!lemma.equals(other.getLemma())) return false;
		if(lexID != other.getLexicalID()) return false;
		if(pos != other.getPOS()) return false;
		if(lexFile.getNumber() != other.getLexicalFile().getNumber()) return false;
		if(isAdjSat != other.isAdjectiveSatellite()) return false;
		if(isAdjSat){
			if(!headLemma.equals(other.getHeadWord())) return false;
			if(headLexID != other.getHeadID()) return false;
		}
		return true;
	}


}
