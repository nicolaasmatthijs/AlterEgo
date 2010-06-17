/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

import java.util.ArrayList;

/**
 * Class that represents the constituent tree of a C&C parsed
 * sentence
 */
public class Sentence {

	// A flat list of all constituents (flat) in the parse tree
	private ArrayList<SentenceItem> sentence = new ArrayList<SentenceItem>();

	public void setSentence(ArrayList<SentenceItem> sentence) {
		this.sentence = sentence;
	}

	public ArrayList<SentenceItem> getSentence() {
		return sentence;
	}
	
}
