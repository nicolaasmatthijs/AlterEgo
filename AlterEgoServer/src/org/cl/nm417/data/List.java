package org.cl.nm417.data;

import java.util.ArrayList;

public class List {

	private String type;
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
