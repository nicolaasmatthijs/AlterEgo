/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

import java.util.regex.Pattern;

/**
 * Class that represents 1 constituent out of a parsed sentence's 
 * constituent tree
 */
public class SentenceItem {

	// How the word originally appeared in the sentence
	private String origWord = "";
	// Tokenized version of that word 
	//  e.g. "Nicolaas." becomes "Nicolaas"
	private String word = "";
	// Part of speech of the current constituent
	private String POS = "";
	
	public static final Pattern pattern = Pattern.compile("[|]");
	
	/**
	 * Parse and process the outcome of the C&C Parser
	 * @param parse	Parse string output from the C&C Parser
	 * 			e.g. Nicolaas.|Nicolaas|NNZ
	 */
	public SentenceItem(String parse){
		String[] split = pattern.split(parse);
		this.origWord = split[0];
		this.word = split[1].replaceAll("[,.:]", "");
		this.POS = split[2];
	}
	
	public void setOrigWord(String origWord) {
		this.origWord = origWord;
	}
	
	public String getOrigWord() {
		return origWord;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setPOS(String POS) {
		this.POS = POS;
	}

	public String getPOS() {
		return POS;
	}
	
	public String toString(){
		return this.origWord + " - " + this.word + " - " + this.POS;
	}
	
}
