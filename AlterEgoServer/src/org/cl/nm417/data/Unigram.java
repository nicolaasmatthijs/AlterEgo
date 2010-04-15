package org.cl.nm417.data;

public class Unigram {

	private String text = "";
	private double weight = 1;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}
	
	public String toString(){
		return getText() + " - " + Math.log(getWeight());
	}
	
}
