package org.cl.nm417.data;

import java.util.ArrayList;

public class Sentence {

	private ArrayList<SentenceItem> sentence = new ArrayList<SentenceItem>();

	public void setSentence(ArrayList<SentenceItem> sentence) {
		this.sentence = sentence;
	}

	public ArrayList<SentenceItem> getSentence() {
		return sentence;
	}
	
}
