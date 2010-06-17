/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

/**
 * Class that represents an image tag from an HTML document
 */
public class Image {

	// The alt attribute associated to the image tag. This is the text
	// shown when the image can't be found
	private String alt;
	// The title attribute associated to the image tag (tooltip)
	private String title;
	
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public String getAlt() {
		return alt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public String toString(){
		return "Image - Alt = " + getAlt() + " - Title = " + getTitle();
	}
	
}
