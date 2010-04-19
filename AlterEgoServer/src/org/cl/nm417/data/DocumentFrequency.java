package org.cl.nm417.data;

public class DocumentFrequency {

	private String term = "";
	private double frequency;
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public double getFrequency() {
		return frequency;
	}
	
}
