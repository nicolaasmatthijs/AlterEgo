/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

/**
 * Class that represents a textual tag from an HTML document, like a span
 * or a paragraph
 */
public class Textual {

	// Type of the textual tag. This can either be p (paragraph) or span
	private String type = "";
	// Content of the textual tag
	private String text = "";
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public String toString(){
		return "Textual - Type: " + getType() + " - Text: " + getText(); 
	}
	
}
