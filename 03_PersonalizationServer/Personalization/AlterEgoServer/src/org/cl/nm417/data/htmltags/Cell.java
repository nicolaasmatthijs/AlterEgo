/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

/**
 * Class that represents a table cell from an HTML document
 */
public class Cell {

	// Type of the table cell. This can either be td (data) or th (heading)
	private String type = "";
	// Content of the table cell
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
		return "Cell - Type: " + getType() + " - Text: " + getText();
	}
	
}
