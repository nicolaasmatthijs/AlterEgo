package org.cl.nm417.data;

import java.util.ArrayList;

public class Profile {

	private ArrayList<Unigram> unigrams = new ArrayList<Unigram>();
	private String userId = "";
	private int documents;
	
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
	
}
