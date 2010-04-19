package org.cl.nm417.google;

public class GoogleSnippet {

	private int id;
	private String userid;
	private String query;
	private int GRank;
	private String title;
	private String url;
	private String summary;
	private int relevance;
	
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
	
}
