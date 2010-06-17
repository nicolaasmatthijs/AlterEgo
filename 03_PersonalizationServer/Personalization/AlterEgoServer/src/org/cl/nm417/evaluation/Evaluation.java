/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.evaluation;

import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.cl.nm417.data.GoogleResult;
import org.cl.nm417.data.GoogleSnippet;
import org.cl.nm417.google.GoogleRerank;
import org.cl.nm417.google.GoogleSearch;
import org.cl.nm417.google.Snippets;
import org.cl.nm417.util.Database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

/**
 * Class used for the relevance judgements evaluation session and for the
 * processing of the evaluation results
 */
public class Evaluation {

	/**
	 * Function that finds the next (random) snippet to be evaluated for a particular
	 * user-query combination
	 * @param query		Search query
	 * @param userid	User's unique identifier
	 * @return			Next random Google snippet to evaluate. Null if all
	 * 					documents for this query and user have been evaluated.
	 */
	public static GoogleSnippet getNextSnippet(String query, String userid){
		GoogleSnippet next = null;
		Connection conn = Database.openConnection();
		ArrayList<GoogleSnippet> snippets = Snippets.getSnippets(conn, query, userid);
		if (snippets == null || snippets.size() == 0){
			// Do search and write to database
			ArrayList<GoogleResult> results = GoogleSearch.doGoogleSearch(query);
			Snippets.writeSnippets(conn, results, query, userid);
			snippets = Snippets.getSnippets(conn, query, userid);
		} 
		// Check whether some snippets don't have a relevance judgment yet
		ArrayList<GoogleSnippet> toEvaluate = new ArrayList<GoogleSnippet>();
		for (GoogleSnippet snippet: snippets){
			// All unjudged results have a relevance score of -1
			if (snippet.getRelevance() == -1){
				toEvaluate.add(snippet);
			}
		}
		System.out.println(toEvaluate.size() + " to be evaluated");
		// Select the next snippet for relevance judgment
		if (toEvaluate.size() > 0){
			next = toEvaluate.get((int) (Math.random() * (toEvaluate.size() - 1)));
			next.setRemaining(toEvaluate.size());
		}
		Database.closeConnection(conn);
		return next;
	}
	
	/**
	 * Write a relevance judgement to the database
	 * @param id			Id of the evaluated result in the database
	 * @param selected		Chosen relevance judgement. (0 for Irrelevant,
	 * 						1 for Relevant and 2 for Very Relevant)
	 */
	public static void saveRelevance(String id, String selected){
		if (id != null && selected != null){
			Connection conn = Database.openConnection();
			PreparedStatement stmt;
			try {
				
				stmt = (PreparedStatement) conn.prepareStatement("UPDATE evaluation SET relevance=? WHERE id=?");
				stmt.setInt(1, Integer.parseInt(selected));
				stmt.setInt(2, Integer.parseInt(id));
					
				// Save in the database
				stmt.executeUpdate();
				
				System.out.println("Written relevance of " + selected + " to " + id);
				
			} catch (Exception ex){
				ex.printStackTrace();
			}
			Database.closeConnection(conn);
		}
	}
	
	/**
	 * Helper function that generates a table row given a set of data
	 * @param fields		List of values. Each value will be placed in 1 cell
	 * @param bold			Should the content be bold or not
	 * @param cssClass		CSS class to give to the generated table row
	 * @param type			Indicate whether the cells should be "td" or "th"
	 * @return				HTML string representing a table row
	 */
	public static String getRow(String[] fields, boolean bold, String cssClass, String type){
		if (type == null){
			type = "td";
		}
		String html = "<tr>";
		if (cssClass != null){
			html = "<tr class='" + cssClass + "'>";
		}
		for (String s: fields){
			html += "<" + type + ">";
			if (bold){
				html += "<b>";
			}
			html += s;
			if (bold){
				html += "</b>";
			}
			html += "</" + type + ">";
		}
		html += "</tr>";
		return html;
	}
	
	/**
	 * Calculate a DCG score for a given ranking and relevance judgements
	 * @param snippets		List of search snippets with their relevance scores
	 * @param ranking		Procuded Google result ranking
	 * @return				DCG score for the ranking
	 */
	private static double getDCG(ArrayList<GoogleSnippet> snippets, ArrayList<GoogleResult> ranking){
		double dcg = 0;
		int i = 0;
		for (GoogleResult res: ranking){
			i++;
			dcg += (Math.pow(2, res.getRelevance()) - 1) / (log2((double)(1.00 + i)));
		}
		return dcg;
	}
	
	/**
	 * Calculate an (N)DCG score for a given ranking and relevance judgements
	 * @param snippets		Search snippets for a query
	 * @param ranking		Procuded ranking for that query
	 * @param max			The rank to which the score should be calculated
	 * @param N				Will calculate the normalized DCG if set to true
	 * @return				The (N)DCG score for the produced ranking
	 */
	public static String calculateDCG(ArrayList<GoogleSnippet> snippets, ArrayList<GoogleResult> ranking, int max, boolean N){
		int i = 0;
		ArrayList<GoogleResult> newres = new ArrayList<GoogleResult>();
		ArrayList<GoogleResult> copy = new ArrayList<GoogleResult>();
		for (GoogleResult res: ranking){
			copy.add(res);
		}
		for (GoogleResult res: copy){
			i++;
			if (max == -1 || i <= max){
				newres.add(res);
			}
		}
		double dcg = getDCG(snippets, newres);
		// If Normalized DCG is required, generate the ideal ranking and
		// calculate IDCG
		if (N){
			copy = GoogleRerank.doSortRelevance(copy);
			newres = new ArrayList<GoogleResult>();
			i = 0;
			for (GoogleResult res: copy){
				i++;
				if (max == -1 || i <= max){
					newres.add(res);
				}
			}
			double idcg = getDCG(snippets, newres);
			dcg = dcg / idcg;
		}
		DecimalFormat twoDigit = new DecimalFormat("#,##0.000");
		return "" + twoDigit.format(dcg);
	}
	
	/**
	 * Calculate log base 2 of a number
	 * @param num	Number to take log base 2 of
	 * @return		Log base 2 of num
	 */
	public static double log2(double num) {
		return (Math.log(num)/Math.log(2));
	}
	
	/**
	 * Get the list of stored snippets for a given user-query combination
	 * @param query		Search query
	 * @param userid	User's unique identifier
	 * @return			List of stored snippets
	 */
	public static ArrayList<GoogleSnippet> getSnippets(String query, String userid){
		Connection conn = Database.openConnection();
		ArrayList<GoogleSnippet> snippets = Snippets.getSnippets(conn, query, userid);
		Database.closeConnection(conn);
		return snippets;
	}
	
	/**
	 * Get the list of Google results for a given user-query combination
	 * @param query		Search query
	 * @param userid	User's unique identifier
	 * @return			List of stored results
	 */
	public static ArrayList <GoogleResult> getGoogleRanking(String query, String userid){
		ArrayList <GoogleResult> results = new ArrayList <GoogleResult>();
		Connection conn = Database.openConnection();
		ArrayList<GoogleSnippet> snippets = Snippets.getSnippets(conn, query, userid);
		for (GoogleSnippet snip: snippets){
			GoogleResult res = new GoogleResult();
			res.setOriginalRank(snip.getGRank());
			res.setRank(snip.getGRank());
			res.setSummary(snip.getSummary());
			res.setRelevance(snip.getRelevance());
			res.setTitle(snip.getTitle());
			res.setUrl(snip.getUrl());
			results.add(res);
		}
		Database.closeConnection(conn);
		return results;
	}
	
	/**
	 * Retrieves the list of queries that have been evaluated in a relevance
	 * judgements experiment by a user
	 * @param userid 	User's unique identifier
	 * @return			The list of queries evaluated by a user
	 */
	public static ArrayList<String> getQueries(String userid){
		ArrayList <String> queries = new ArrayList <String>();
		Connection conn = Database.openConnection();
		PreparedStatement stmt = null; ResultSet rs = null;
		
		try {
		  
			// Get a statement from the connection
			stmt = (PreparedStatement) conn.prepareStatement("SELECT query FROM evaluation WHERE userid=? GROUP BY query");
			stmt.setString(1, userid);
			// Execute the query
			rs = (ResultSet) stmt.executeQuery();
			
			// Loop through the result set
			while( rs.next() ) {
				queries.add(URLDecoder.decode(rs.getString("query"),"utf-8"));
			}

		} catch (Exception ex){
		} finally {
			// Close the result set, statement and the connection
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {}
			}
			if (rs != null){
				try {
					rs.close();
				} catch (SQLException e) {}
			}
		}
		Database.closeConnection(conn);
		return queries;
	}
	
}
