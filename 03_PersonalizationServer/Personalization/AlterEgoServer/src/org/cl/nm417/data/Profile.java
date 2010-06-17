/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User profile representing a user's long term interests
 */
public class Profile {

	// The user's unique identifier
	private String userId = "";
	// Number of documents used to generate the user profile
	//   i.e.: Number of documents in the user's browsing history
	private int documents;
	// Time in ms representing the visit time of the last document
	// incorporated in the profile
	private long lastModified;
	
	// Set of unigram representing the user's long term interests
	// This consists of a list of terms and associated weights
	private ArrayList<Unigram> unigrams = new ArrayList<Unigram>();
	// List of URLs visited by the user and the number of visits per URL
	private HashMap<String, Integer> URLs = new HashMap<String, Integer>();
	
	public void setUnigrams(ArrayList<Unigram> unigrams) {
		this.unigrams = unigrams;
	}
	
	public ArrayList<Unigram> getUnigrams() {
		return unigrams;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setDocuments(int documents) {
		this.documents = documents;
	}

	public int getDocuments() {
		return documents;
	}

	public void setURLs(HashMap<String, Integer> uRLs) {
		URLs = uRLs;
	}

	public HashMap<String, Integer> getURLs() {
		return URLs;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLastModified() {
		return lastModified;
	}
	
}
