/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

/**
 * Class that represents a Unigram. This is a term with associated weight and is
 * the basis of the user profiles
 */
public class Unigram {

	// Term
	private String text = "";
	// Term weight
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
