package org.cl.nm417.data;

public class Image {

	private String alt;
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
