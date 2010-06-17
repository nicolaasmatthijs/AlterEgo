/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

/**
 * Class representing a single Google result within a list of
 * retrieved search results
 */
public class GoogleResult {

	// Title of the document
	private String title;
	// Snippet of the search result
	private String summary;
	// URL of the search result
	private String url;
	
	// Original rank within the Google result list
	private int originalRank;
	// New rank after personalization
	private int rank;
	
	// Weight associated to this search snippet
	private double weight;
	// Number of terms the user profile and search snippet have in common
	private int common;
	
	// Team (Original or re-ranked) to which this results has been assigned
	// during Team-Draft interleaving 
	private String team;
	
	// Relevance score this result has received during the relevance judgements
	// experiment. This can be 0 (Irrelevant), 1 (Relevant) or 2 (Very Relevant)
	private double relevance;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}

	public void setWeight(double Weight) {
		this.weight = Weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setCommon(int common) {
		this.common = common;
	}

	public int getCommon() {
		return common;
	}

	public void setOriginalRank(int originalRank) {
		this.originalRank = originalRank;
	}

	public int getOriginalRank() {
		return originalRank;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getTeam() {
		return team;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public double getRelevance() {
		return relevance;
	}
	
}
