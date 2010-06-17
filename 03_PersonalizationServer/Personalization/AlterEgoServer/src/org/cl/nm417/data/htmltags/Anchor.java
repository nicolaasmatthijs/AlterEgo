/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

/**
 * Class that represents an anchor tag from an HTML document
 */
public class Anchor {

	// Text shown for the anchor tag
	private String text = "";
	// Value of the tag's title attribute (tooltip)
	private String title = "";
	// URL this anchor tag is pointing to
	private String href = "";
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public String toString(){
		return "Anchor - Title = " + getTitle() + " - Text = " + getText();
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHref() {
		return href;
	}
	
}
