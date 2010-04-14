package org.cl.nm417.data;

import java.util.ArrayList;

public class Table {

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
