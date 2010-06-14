package org.cl.nm417.data;

import java.util.ArrayList;

import org.cl.nm417.json.JSONArray;

public class Document {

	private String duration = "";
	private String url = "";
	private String visitdate = "";
	
	private String plaintext = "";
	
	private String metadescription = "";
	private ArrayList<String> metakeywords = new ArrayList<String>();
	private String title = "";
	
	private ArrayList<Image> images = new ArrayList<Image>();
	private ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	private ArrayList<Heading> headings = new ArrayList<Heading>();
	private ArrayList<Textual> textual = new ArrayList<Textual>();
	private ArrayList<List> lists = new ArrayList<List>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	
	private JSONArray terms;
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public void setMetadescription(String metadescription) {
		this.metadescription = metadescription;
	}

	public String getMetadescription() {
		return metadescription;
	}

	public void setMetakeywords(ArrayList<String> metakeywords) {
		this.metakeywords = metakeywords;
	}

	public ArrayList<String> getMetakeywords() {
		return metakeywords;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public void setAnchors(ArrayList<Anchor> anchors) {
		this.anchors = anchors;
	}

	public ArrayList<Anchor> getAnchors() {
		return anchors;
	}

	public void setHeadings(ArrayList<Heading> headings) {
		this.headings = headings;
	}

	public ArrayList<Heading> getHeadings() {
		return headings;
	}

	public void setTextual(ArrayList<Textual> textual) {
		this.textual = textual;
	}

	public ArrayList<Textual> getTextual() {
		return textual;
	}

	public void setLists(ArrayList<List> lists) {
		this.lists = lists;
	}

	public ArrayList<List> getLists() {
		return lists;
	}

	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}

	public ArrayList<Table> getTables() {
		return tables;
	}

	public void setVisitdate(String visitdate) {
		this.visitdate = visitdate;
	}

	public String getVisitdate() {
		return visitdate;
	}

	public void setTerms(JSONArray terms) {
		this.terms = terms;
	}

	public JSONArray getTerms() {
		return terms;
	}
	
}
