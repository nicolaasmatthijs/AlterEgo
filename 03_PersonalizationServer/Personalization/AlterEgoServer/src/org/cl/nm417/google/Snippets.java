/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.google;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

import org.cl.nm417.data.GoogleResult;
import org.cl.nm417.data.GoogleSnippet;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

/**
 * Class used to save and retrieve the list of search results returned for a query
 * during an offline relevance judgements evaluation experiment
 */
public class Snippets {

	/**
	 * Get all of the stored search results for a particular query and user
	 * @param conn		Database connection
	 * @param query		Search query
	 * @param userid	User's unique identifier
	 * @return			List of 50 GoogleSnippets
	 */
	public static ArrayList<GoogleSnippet> getSnippets(Connection conn, String query, String userid){
		ArrayList<GoogleSnippet> results = new ArrayList<GoogleSnippet>();
		PreparedStatement stmt = null; ResultSet rs = null;
		
		try {
		  
			// Get a statement from the connection
			stmt = (PreparedStatement) conn.prepareStatement("SELECT * FROM evaluation WHERE query=? AND userid=?");
			stmt.setString(1, URLEncoder.encode(query,"utf-8"));
			stmt.setString(2, userid);
			// Execute the query
			rs = (ResultSet) stmt.executeQuery();
			
			// Loop through the result set
			while( rs.next() ) {
				GoogleSnippet snippet = new GoogleSnippet();
				snippet.setId(rs.getInt("id"));
				snippet.setUserid(rs.getString("userid"));
				snippet.setQuery(URLDecoder.decode(rs.getString("query"),"utf-8"));
				snippet.setGRank(rs.getInt("grank"));
				snippet.setTitle(URLDecoder.decode(rs.getString("title"),"utf-8"));
				snippet.setUrl(rs.getString("url"));
				snippet.setSummary(URLDecoder.decode(rs.getString("summary"),"utf-8"));
				int rel = rs.getInt("relevance");
				// As there are only 3 possible judgements instead of the original 4
				if (rel == 2){
					rel = 1;
				} else if (rel == 3){
					rel = 2;
				}
				snippet.setRelevance(rel);
				results.add(snippet);
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
		return results; 
		
	}

	/**
	 * Store a set of retrieved results for a particular query and user into the database
	 * @param conn		Database connection
	 * @param results	List of search snippets to write to the database
	 * @param query		Search query
	 * @param userid	User's unique identifier
	 */
	public static void writeSnippets(Connection conn, ArrayList<GoogleResult> results, String query, String userid) {
		PreparedStatement stmt;
		try {
			// Store all of them one by one
			for (GoogleResult res: results){
				
				stmt = (PreparedStatement) conn.prepareStatement("INSERT INTO evaluation " + 
						"(userid, query, grank, title, url, summary, relevance) VALUES (?,?,?,?,?,?,?)");
				stmt.setString(1, userid);
				stmt.setString(2, URLEncoder.encode(query,"utf-8"));
				stmt.setInt(3, res.getOriginalRank());
				stmt.setString(4, URLEncoder.encode(res.getTitle(),"utf-8"));
				stmt.setString(5, res.getUrl());
				stmt.setString(6, URLEncoder.encode(res.getSummary(),"utf-8"));
				// Indicate that no relevance judgement is given for this result yet
				stmt.setInt(7, -1);
				
				// Execute the query
				stmt.executeUpdate();
				
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
}
