<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Page which is opened when a user clicks a Google Search result in the online
 * interleaved evaluation. This will store the received vote in the database and
 * redirect the user to the page he requested
 */
 -->
<%@page import="com.mysql.jdbc.PreparedStatement"%><%@page import="com.mysql.jdbc.Connection"%>
<%@page import="org.cl.nm417.util.Database"%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
   
   // Unique identifier of the user doing the click
   String userid = request.getParameter("userid");
   // Query the user was searching for
   String query = request.getParameter("query");
   // Rank of the clicked result in the overall list
   String clickedrank = request.getParameter("clickedrank");
   // Ranking the user voted for (Google or re-ranking)
   String clickedranking = request.getParameter("clickedranking");
   // Which profile - re-ranking combination was being evaluated
   String evaluating = request.getParameter("evaluating");
   // URL of the clicked result
   String url = request.getParameter("url");
   // Page number of the current search
   String pagen = request.getParameter("pagen");
   // Position of the clicked result in the Google ranking
   String origranking = request.getParameter("origranking");
   
   Connection conn = Database.openConnection();
   PreparedStatement stmt;

   try {
	    // Store the user's vote into the database
		stmt = (PreparedStatement) conn.prepareStatement("INSERT INTO interleaving " + 
					"(userid, query, clickedrank, origranking, clickedranking, evaluating, url, page) VALUES (?,?,?,?,?,?,?,?)");
		stmt.setString(1, userid);
		stmt.setString(2, query);
		stmt.setInt(3, Integer.parseInt(clickedrank));
		stmt.setInt(4, Integer.parseInt(origranking));
		stmt.setString(5, clickedranking);
		stmt.setString(6, evaluating);
		stmt.setString(7, url);
		stmt.setInt(8, Integer.parseInt(pagen));
			
		// Execute the query
		stmt.executeUpdate();
   } catch (Exception ex){
	  	System.out.println(ex.getMessage());
   }
   
   Database.closeConnection(conn);
   
   // Redirect the user to the requested page
   response.sendRedirect(url);
   
%>