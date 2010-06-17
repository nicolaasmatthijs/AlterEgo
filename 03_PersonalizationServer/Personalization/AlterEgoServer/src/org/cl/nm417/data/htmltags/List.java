/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

import java.util.ArrayList;

/**
 * Class that represents a list tag from an HTML document
 */
public class List {

	// Type of the list tag. This can be an ordered list (ol) or unordered list (ul)
	private String type;
	// List items associated with this list tag. This is a list of strings representing
	// the values of the list items
	private ArrayList<String> items = new ArrayList<String>();
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}

	public ArrayList<String> getItems() {
		return items;
	}
	
	public String toString(){
		String toReturn = "List - Type: " + getType() + "\n";
		for (String s: items){
			toReturn += "\tItem: " + s + "\n";
		}
		return toReturn;
	}
	
}
