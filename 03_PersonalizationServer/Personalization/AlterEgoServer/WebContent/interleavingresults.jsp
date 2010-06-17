<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * This page will process all votes received in the interleaved evaluation experiment
 * and will display a summary of these results
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.mysql.jdbc.PreparedStatement"%>
<%@page import="com.mysql.jdbc.Connection"%>
<%@page import="com.mysql.jdbc.ResultSet"%>
<%@page import="org.cl.nm417.util.Database"%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.io.File"%>
<%@page import="org.cl.nm417.AlterEgo"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>AlterEgo 0.2 :: Interleaving Results</title>
<style>
	html, body, table, tr, td {
		font-size: 12px;
		font-family: "Verdana";
	}
	table, tr, td {
		font-size: 10px;
		font-family: "Verdana";
	}
	body {
		padding: 10px;
	}
</style>
</head>
<body>

<div style="height:400px">
<div style="width: 300px; float: left;">
<table border="1" cellspacing="0" cellpadding="5">

<%
   
   Connection conn = Database.openConnection();
   PreparedStatement stmt; ResultSet rs;
   
   HashMap<String, Integer> map1 = new HashMap<String, Integer>();
   HashMap<String, Integer> map2 = new HashMap<String, Integer>();
   
   ArrayList<String> queries = new ArrayList<String>();
   
   try {
	    // Get votes from the database
		stmt = (PreparedStatement) conn.prepareStatement("SELECT clickedranking, evaluating, count(id) as cnt FROM interleaving GROUP BY clickedranking, evaluating");
		rs = (ResultSet) stmt.executeQuery();
		while( rs.next() ) {
			String evaluating = rs.getString("evaluating");
			String clickedranking = rs.getString("clickedranking");
			if (clickedranking.equals("Original")){
				map1.put(evaluating, rs.getInt("cnt"));
			} else if (clickedranking.equals("Reranked")){
				map2.put(evaluating, rs.getInt("cnt"));
			}
		}
   } catch (Exception ex){
	  	System.out.println(ex.getMessage());
   }
   
   String perc1 = "";
   String perc2 = "";
   String methods = "";
   
   for (String s: map1.keySet()){
	   
	   // Print a table in which each evaluated profile has an entry with the
	   // Google votes and the vote for that profile
	   
	   methods += "|" + s;
	   perc1 += ((map1.get(s) * 100) / (map1.get(s) + map2.get(s))) + ",";
	   perc2 += ((map2.get(s) * 100) / (map1.get(s) + map2.get(s))) + ",";
	   
%>

	<tr>
		<th colspan="3"><%= s %></th>
	</tr>
	<tr>
		<td>Original</td>
		<td><%= map1.get(s) %></td>
		<td><%= ((map1.get(s) * 100) / (map1.get(s) + map2.get(s)))  %> %</td>
	</tr>
	<tr>
		<td>Reranked</td>
		<td><%= map2.get(s) %></td>
		<td><%= ((map2.get(s) * 100) / (map1.get(s) + map2.get(s)))  %> %</td>
	</tr>

<%  
	 
   }
   
   perc1 = perc1.substring(0, perc1.length() - 1);
   perc2 = perc2.substring(0, perc2.length() - 1);
   Database.closeConnection(conn);
   
%>

	</table>
</div>
<div style="margin-left: 300px;">
	<!-- Use the Google Chart API to display a chart about the evaluation results -->
	<img src="http://chart.apis.google.com/chart?cht=bvg&chs=800x300&chd=t:<%= perc1 %>|<%= perc2 %>&chco=4D89F9,C6D9FD&chdl=Original|Reranked&chbh=70,1,6&chxt=x,y&chxl=0:<%= methods %>"/>
</div>
</div>

<div>
<%

	conn = Database.openConnection();

	// Retrieve the last 5 search queries overall and display them in a list
	try {
		stmt = (PreparedStatement) conn.prepareStatement("SELECT * FROM interleaving");
		rs = (ResultSet) stmt.executeQuery();
		while( rs.next() ) {
			queries.add(0,"| " + rs.getInt("id") + " | " + rs.getString("userid") + " | " + rs.getString("query") + " | " + rs.getInt("clickedrank") + " | " + rs.getString("clickedranking") + " | " + rs.getInt("origranking") + " | " + rs.getString("evaluating") + " | " + rs.getString("url") + " | " + rs.getInt("page") + " | " + rs.getString("clickdate") + " |");   
		}
	} catch (Exception ex){
		ex.printStackTrace();
	}

	for (int i = 0; i < 5; i++){
		out.println(queries.get(i) + "<br/>");
	}
	
	Database.closeConnection(conn);

%>
</div>

</body>
</html>