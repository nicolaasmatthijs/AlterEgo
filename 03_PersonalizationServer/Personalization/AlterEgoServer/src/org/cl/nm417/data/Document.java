/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

import java.util.ArrayList;

import org.cl.nm417.data.htmltags.Anchor;
import org.cl.nm417.data.htmltags.Heading;
import org.cl.nm417.data.htmltags.Image;
import org.cl.nm417.data.htmltags.List;
import org.cl.nm417.data.htmltags.Table;
import org.cl.nm417.data.htmltags.Textual;

/**
 * Class that represents a web page inside a user's browsing history
 */
public class Document {

	// Page's id in the database
	private int id;
	
	// Time spent on the page
	private String duration = "";
	// URL of the page
	private String url = "";
	// Date and time when this page was visited
	private String visitdate = "";
	
	// The full text of the page split on whitespace stored in an array
	private String[] plaintext = new String[]{};
	
	// The content of the metadata description tag
	private String metadescription = "";
	// The keywords found in the metadata keywords tag
	private ArrayList<String> metakeywords = new ArrayList<String>();
	// The content of the title tag
	private String title = "";
	
	// A list of all images found in the page
	private ArrayList<Image> images = new ArrayList<Image>();
	// A list of all links found in the page
	private ArrayList<Anchor> anchors = new ArrayList<Anchor>();
	// A list of all header tags found in the page
	private ArrayList<Heading> headings = new ArrayList<Heading>();
	// A list of all textual tags found in the page
	private ArrayList<Textual> textual = new ArrayList<Textual>();
	// A list of all lists found in the page
	private ArrayList<List> lists = new ArrayList<List>();
	// A list of all tables found in the page
	private ArrayList<Table> tables = new ArrayList<Table>();
	// A list of all terms extracted from the page using the Vu term extraction algorithm
	private ArrayList<String> terms = new ArrayList<String>();
	// A list of all C&C parsed sentences as constituent trees (direct output from parser)
	private ArrayList<ArrayList<String>> parsed = new ArrayList<ArrayList<String>>();
	
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

	public void setPlaintext(String[] plaintext) {
		this.plaintext = plaintext;
	}

	public String[] getPlaintext() {
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

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setParsed(ArrayList<ArrayList<String>> parsed) {
		this.parsed = parsed;
	}

	public ArrayList<ArrayList<String>> getParsed() {
		return parsed;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
