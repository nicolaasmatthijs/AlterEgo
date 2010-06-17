/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

/**
 * Class that represents a Google search result when it is retrieved
 * for a query in an offline relevance judgements experiment and stored
 * in the AlterEgo database. 
 */
public class GoogleSnippet {

	// Id of the snippet in the database
	private int id;
	
	// Unique identifier of the user doing the relevance judgements
	private String userid;
	// Query for which the result was retrieved
	private String query;
	
	// Rank of this result in the overall search results
	private int GRank;
	// Title of the snippet
	private String title;
	// URL for the search result
	private String url;
	// Summary of the snippet as provided by Google
	private String summary;
	
	// Relevance score this result has received during the relevance judgements
	// experiment. This can be 0 (Irrelevant), 1 (Relevant) or 2 (Very Relevant)
	private int relevance;
	// Number of relevance judgements to be done for the current query
	// after this one
	private int remaining;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserid() {
		return userid;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setGRank(int gRank) {
		GRank = gRank;
	}

	public int getGRank() {
		return GRank;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}

	public int getRelevance() {
		return relevance;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public int getRemaining() {
		return remaining;
	}
	
}
