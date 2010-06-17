/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

/**
 * Class that represents a header tag from an HTML document
 */
public class Heading {

	// Type of header tag. This can be h1, h2, h3, h4, h5 or h6
	private String type = "";
	// Text of the header tag
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
		return "Heading - Type = " + getType() + " - Text = " + getText();
	}
	
}
