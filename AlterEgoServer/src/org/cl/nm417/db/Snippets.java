package org.cl.nm417.db;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

import org.cl.nm417.google.GoogleResult;
import org.cl.nm417.google.GoogleSnippet;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class Snippets {

	public static ArrayList<GoogleSnippet> getSnippets(Connection conn, String query, String userid){
		ArrayList<GoogleSnippet> results = new ArrayList<GoogleSnippet>();
		PreparedStatement stmt = null; ResultSet rs = null;
		
		try {
		  
			// Get a statement from the connection
			stmt = (PreparedStatement) conn.prepareStatement("SELECT * FROM evaluation WHERE query=? AND userid=?");
			stmt.setString(1, query);
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
				snippet.setRelevance(rs.getInt("relevance"));
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

	public static void writeSnippets(Connection conn, ArrayList<GoogleResult> results, String query, String userid) {
		PreparedStatement stmt;
		try {
			for (GoogleResult res: results){
				
				stmt = (PreparedStatement) conn.prepareStatement("INSERT INTO evaluation " + 
						"(userid, query, grank, title, url, summary, relevance) VALUES (?,?,?,?,?,?,?)");
				stmt.setString(1, userid);
				stmt.setString(2, query);
				stmt.setInt(3, res.getOriginalRank());
				stmt.setString(4, URLEncoder.encode(res.getTitle(),"utf-8"));
				stmt.setString(5, res.getUrl());
				stmt.setString(6, URLEncoder.encode(res.getSummary(),"utf-8"));
				stmt.setInt(7, -1);
				
				// Execute the query
				stmt.executeUpdate();
				
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
}
