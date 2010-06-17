/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data.htmltags;

import java.util.ArrayList;

/**
 * Class that represents a table tag from an HTML document
 */
public class Table {

	// List of cells the table contains
	private ArrayList<Cell> cells = new ArrayList<Cell>();

	public void setCells(ArrayList<Cell> cells) {
		this.cells = cells;
	}

	public ArrayList<Cell> getCells() {
		return cells;
	}
	
	public String toString(){
		String toReturn = "Table\n";
		for (Cell cell: getCells()){
			toReturn += "\t" + cell + "\n";
		}
		return toReturn;
	}
	
}
