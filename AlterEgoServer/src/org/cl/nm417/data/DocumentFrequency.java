package org.cl.nm417.data;

public class DocumentFrequency {

	private String term = "";
	private int frequency;
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}
	
}
