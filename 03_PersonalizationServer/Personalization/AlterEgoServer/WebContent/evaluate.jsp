<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Bottom part of the relevance judgement evaluation environment. In this screen, user have
 * to judge the relevance of a presented web document. 
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.cl.nm417.data.GoogleSnippet"%>
<%@page import="org.cl.nm417.evaluation.Evaluation"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cl.nm417.data.GoogleResult"%>
<%@page import="java.io.File"%>
<%@page import="org.cl.nm417.AlterEgo"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Evaluate</title>
<script type="text/javascript" language="JavaScript" src="js/jquery.js"></script>
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
	.inputCell {
		width: 20px;
	}
	.weight {
		width: 100px;
	}
	.tstyle td {
		border: 1px solid black;
		text-align: center; 
		padding: 5px;
	}
</style>
</head>
<body>

	<%
		String query = request.getParameter("query");
		if (query != null && query.length() != 0){
		
			// Current search query
			query = request.getParameter("query").toLowerCase();
			// Selected user id
			String userid = request.getParameter("userid");
			// Id in the database of the previously judged document
			String id = request.getParameter("id");
			// Relevance score assigned to the previouly judged document
			String selected = request.getParameter("selected");
			
			// Save the previous relevance judgement in the database
			Evaluation.saveRelevance(id, selected);
			
			// Get the next search result to evaluate
			GoogleSnippet snippet = Evaluation.getNextSnippet(query, userid);
			
			// If there is a web page left to evaluate
			if (snippet != null){
			
	%>
		
		<!-- Hidden fields used when submitting the result -->
		<input type='hidden' id='rel_id' value='<%=snippet.getId() %>'/>
		<input type='hidden' id='rel_query' value='<%=query %>'/>
		<input type='hidden' id='rel_userid' value='<%=userid %>'/>
		
		<!-- Possible relevance choices -->
		<input type='button' value='Irrelevant' id='rel_not' disabled="disabled"/>
		<input type='button' value='Relevant' id='rel_rel' disabled="disabled"/>
		<input type='button' value='Very relevant' id='rel_vrel' disabled="disabled"/>
		
		<!-- Progress indicator -->
		<div style="float:right; margin-top: -20px;">
			<div style="height: 20px; background-color: #B0171F; width: 100px; border: 1px solid black;"></div>
			<div style="position: relative; left: -40px; top: -22px; margin-left: 40px; height: 20px; background-color: #00CD66; width: <%= "" + Math.round(100 * (56 - snippet.getRemaining()) / 56) %>px; border: 1px solid black;"></div>
		</div>
		
		<br/><br/>
		
		<script type="text/javascript" language="JavaScript">

			// Open the judged web page in the pop-up
			parent.win.location = "<%=URLDecoder.decode(snippet.getUrl(),"utf-8") %>";
		
			var doSave = function(selected){
				
				var id = $("#rel_id").val();
				var search = $("#rel_query").val();
				var userid = $("#rel_userid").val();

				// Disable the buttons in order to prevent too much clicking
				$("#rel_not").attr("disabled", true);
				$("#rel_rel").attr("disabled", true);
				$("#rel_vrel").attr("disabled", true);

				// Store the judgement and load the next document to judge
				parent.output.location.href = "evaluate.jsp?id=" + id + "&selected=" + selected + "&query=" + encodeURI(search) + "&userid=" + userid;
			
			};

			var doActive = function(){
				// Enable the buttons after 3 second in order to prevent users to
				// click too quickly when a page takes a while to loead
				$("#rel_not").attr("disabled", false);
				$("#rel_rel").attr("disabled", false);
				$("#rel_vrel").attr("disabled", false);
			}

			setTimeout(doActive, 3000);
	
			$("#rel_not").bind("click", function(ev){
				// Not Relevant gives a relevance score of 0
				doSave("0");
			});	
	
			$("#rel_rel").bind("click", function(ev){
				// Relevant gives a relevance score of 1
				doSave("1");
			});	
	
			$("#rel_vrel").bind("click", function(ev){
				// Very Relevant gives a relevance score of 3
				doSave("3");
			});						
		
		</script>
		
	<%
				
			} else {
				
				// If all documents have been judged for the current query
		
	%>
	
				<h1>Please provide a new search query</h1>
				
				<script type="text/javascript" language="JavaScript">
		
					// Empty the popup by removing the last judged web page
					parent.win.location = 'about:blank';
		
				</script>
	
	<%
	
			}
		}
	
	%>
	

</body>
</html>