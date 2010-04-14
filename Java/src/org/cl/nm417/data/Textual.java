package org.cl.nm417.data;

public class Textual {

	private String type = "";
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
