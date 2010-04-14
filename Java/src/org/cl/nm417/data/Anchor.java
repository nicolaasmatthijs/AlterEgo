package org.cl.nm417.data;

public class Anchor {

	private String text = "";
	private String title = "";
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
