package org.cl.nm417.google;

public class GoogleResult {

	private String title;
	private String summary;
	private String url;
	private int rank;
	private double newWeight;
	private int common;
	
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

	public void setNewWeight(double newWeight) {
		this.newWeight = newWeight;
	}

	public double getNewWeight() {
		return newWeight;
	}

	public void setCommon(int common) {
		this.common = common;
	}

	public int getCommon() {
		return common;
	}
	
}
