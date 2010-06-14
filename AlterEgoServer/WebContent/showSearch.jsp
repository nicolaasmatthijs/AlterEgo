<%@page import="java.net.URLDecoder"%>
<%@page import="org.cl.nm417.google.GoogleResult"%>
<%@page import="org.cl.nm417.google.GoogleSearch"%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="org.cl.nm417.data.Unigram"%>
<%@page import="org.cl.nm417.data.Profile"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.HashMap"%>
<%
	
		String user = request.getParameter("user");	// Required
		String query = request.getParameter("query"); // Required
		
		String method = "lm";	// Not required
		if (request.getParameter("method") != null){
			method = request.getParameter("method");
		}
		
		String profilename = ""; // Not required
		if (request.getParameter("profilename") != null){
			profilename = request.getParameter("profilename");
		} else {
			ArrayList<String> arl = new ArrayList<String>();
			arl.add(user + "_ti_r_n_r_n_n_r_n_n.txt");
			arl.add(user + "_t_n_n_n_n_r_r_n_n.txt"); 
			arl.add(user + "_b_r_n_r_n_r_n_n_n.txt");
			int index = (int) ( Math.random()*(3) );
			profilename = arl.get(index);
		}
		
		
		int pagen = 1; 	// Not required
		if (request.getParameter("page") != null){ 
			pagen = Integer.parseInt(request.getParameter("page"));
		}
		
		boolean rerank = true; 	// Not required
		if (request.getParameter("rerank") != null && request.getParameter("rerank").equals("false")){
			rerank = false;
		}
		
		boolean interleave = true; 	// Not required
		if (request.getParameter("interleave") != null && request.getParameter("interleave").equals("false")){
			interleave = false;
		}
		
		String interleaveMethod = "teamdraft";  // Not required
		if (request.getParameter("interleaveMethod") != null){
			interleaveMethod = request.getParameter("interleaveMethod");
		}
		
		boolean lookatrank = true;  // Not required
		if (request.getParameter("lookatrank") != null && request.getParameter("lookatrank").equals("false")){
			lookatrank = false;
		}
		
		boolean showall = false;  // Not required
		if (request.getParameter("showall") != null && request.getParameter("showall").equals("true")){
			showall = true;
		}
		
		boolean umatching = false; // Not required
		if (request.getParameter("method") != null && request.getParameter("method").equals("umatching")){
			umatching = true;
		}
		
		boolean visited = true; // Not required
		if (request.getParameter("visited") != null && request.getParameter("visited").equals("false")){
			visited = false;
		}
		
		int visitedW = 10; // Not required
		if (request.getParameter("visitedW") != null){
			visitedW = Integer.parseInt(request.getParameter("visitedW"));
		}
		
		String output = "";
		if (pagen <= 5){
		
			//String output = GoogleSearch.getGoogleHTML(query, pagen);
			output = "<span style='visibility: visible;' id='topstuff'></span><span style='visibility: visible;' id='search'><!--a-->";
			output += "<h2 class='hd'>Search Results</h2><div><ol id='rso'>";
			
			ArrayList<GoogleResult> results = AlterEgo.SearchGoogle(query, user, profilename, rerank, method, interleave, interleaveMethod, lookatrank, umatching, visited, visitedW);
			int record = (pagen - 1) * 10;
			int start = 0;
			int end = 50;
			if (!showall){
				start = record;
				end = record + 10;
			}
			int current = 0;
			for (int i = start; i < end; i++){
				int rank = start + current + 1;
				current++;
				GoogleResult res = results.get(i);
				String realUrl = "http://localhost:9090/AlterEgoServer/storeEval.jsp?userid=" + user + "&query=" + query.replaceAll("'","%27") + "&clickedrank=" + rank + "&origranking=" + res.getOriginalRank() + "&clickedranking=" + res.getTeam() + "&evaluating=" + (profilename.substring(profilename.indexOf("_") + 1)).substring((profilename.substring(profilename.indexOf("_") + 1)).indexOf("_") + 1) + "&url=" + res.getUrl() + "&pagen=" + pagen; 
				output += "<li class=\"g\"><h3 class=\"r\"><a class=\"l\" href=\"" + res.getUrl() + "\" onclick=\"document.location='" + realUrl + "'; return false;\">" + res.getTitle() + "</a></h3><div class=\"s\">" + 
					res.getSummary() + "<br/><cite>" + res.getUrl() + " - </cite><span class=\"gl\"><a href=\"\">Cached</a> - <a href=\"\">Similar</a>"; // - " + rank + "(" + res.getOriginalRank() + ")";
				//if (interleave && rerank){
				//	output += " " + res.getTeam() + " ";
				//}
				output += "</span>" + "</div></li>";
			}
			
			output += "</ol></div><!--z--></span><span style='visibility: visible;' id='botstuff'>  </span>";
		
		}
		
		out.println(output);
		
//		<!--  
//		
//		int index = output.toLowerCase().indexOf("<ol>");
//			int index2 = output.toLowerCase().lastIndexOf("<li class=g>");
//			String first = output.substring(0, index);
//			String last = output.substring(output.substring(index2).indexOf("</ol>") + index2 + 5);
//			
//			String middle = "<ol>";
//			
//			ArrayList<GoogleResult> results = AlterEgo.SearchGoogle(query, user, profilename, rerank, method, interleave, interleaveMethod, lookatrank, umatching, visited, visitedW);
//			int record = (pagen - 1) * 10;
//			int start = 0;
//			int end = 50;
//			if (!showall){
//				start = record;
//				end = record + 10;
//			}
//			int current = 0;
//			for (int i = start; i < end; i++){
//				int rank = start + current + 1;
//				current++;
//				GoogleResult res = results.get(i);
//				middle += "<li class=\"g\"><h3 class=\"r\"><a class=\"l\" href=\"" + res.getUrl() + "\">" + res.getTitle() + "</a></h3><div class=\"s\">" + 
//					res.getSummary() + "<br/><cite>" + res.getUrl() + " - </cite><span class=\"gl\"><a href=\"\">Cached</a> - <a href=\"\">Similar</a> - " + rank + "(" + res.getOriginalRank() + ")";
//				if (interleave && rerank){
//					middle += " " + res.getTeam() + " ";
//				}
//				middle += "</span>" + "</div></li>";
//			}
//			
//			middle += "</ol>";
//			//output = first + middle + last;
//			
//			index2 = output.indexOf("<table id=nav");
//			first = output.substring(0,index2);
//			last = output.substring(output.substring(index2).indexOf("</table>") + index2 + 8);
//			
//			String querystring = "lookatrank=" + lookatrank + "&showall=" + showall + "&interleave=" + interleave + "&user=" + user + 
//				"&query=" + URLDecoder.decode(query,"UTF-8") + "&method=" + method + "&rerank=" + rerank;
//				
//			if (showall){
//				//output = first + last;
//			} else {
//				middle = "<table align=\"center\" style=\"border-collapse: collapse;\" id=\"nav\"><tbody>" + 
//					"<tr valign=\"top\"><td class=\"b\"><img width=\"18\" height=\"26\" border=\"0\" alt=\"\"" + 
//					" src=\"http://www.google.co.uk/nav_first.gif\"/><br/></td>";
//					if (pagen == 1){
//						middle += "<td class=\"cur\"><img width=\"16\" height=\"26\" border=\"0\" alt=\"\" " +
//							"src=\"http://www.google.co.uk/nav_current.gif\"/><br/>1</td>";
//					} else {
//						middle += "<td><a style=\"text-decoration: none;\" href=\"showSearch.jsp?" + querystring + "&page=1\">" + 
//							"<img width=\"16\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_page.gif\"/><br/>1</a></td>";
//					}
//					if (pagen == 2){
//						middle += "<td class=\"cur\"><img width=\"16\" height=\"26\" border=\"0\" alt=\"\" " +
//							"src=\"http://www.google.co.uk/nav_current.gif\"/><br/>2</td>";
//					} else {
//						middle += "<td><a style=\"text-decoration: none;\" href=\"showSearch.jsp?" + querystring + "&page=2\">" + 
//							"<img width=\"16\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_page.gif\"/><br/>2</a></td>";
//					}
//					if (pagen == 3){
//						middle += "<td class=\"cur\"><img width=\"16\" height=\"26\" border=\"0\" alt=\"\" " +
//							"src=\"http://www.google.co.uk/nav_current.gif\"/><br/>3</td>";
//					} else {
//						middle += "<td><a style=\"text-decoration: none;\" href=\"showSearch.jsp?" + querystring + "&page=3\">" + 
//							"<img width=\"16\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_page.gif\"/><br/>3</a></td>";
//					}
//					if (pagen == 4){
//						middle += "<td class=\"cur\"><img width=\"16\" height=\"26\" border=\"0\" alt=\"\" " +
//							"src=\"http://www.google.co.uk/nav_current.gif\"/><br/>4</td>";
//					} else {
//						middle += "<td><a style=\"text-decoration: none;\" href=\"showSearch.jsp?" + querystring + "&page=4\">" + 
//							"<img width=\"16\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_page.gif\"/><br/>4</a></td>";
//					}
//					if (pagen == 5){
//						middle += "<td class=\"cur\"><img width=\"16\" height=\"26\" border=\"0\" alt=\"\" " +
//							"src=\"http://www.google.co.uk/nav_current.gif\"/><br/>5</td>";
//					} else {
//						middle += "<td><a style=\"text-decoration: none;\" href=\"showSearch?" + querystring + "&page=5\">" + 
//							"<img width=\"16\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_page.gif\"/><br/>5</a></td>";
//					}
//				middle += "<td class=\"b\"><a style=\"text-decoration: none;\" href=\"showSearch.jsp?" + querystring + "&page=" + (pagen+1) + "\">" + 
//					"<img width=\"100\" height=\"26\" border=\"0\" alt=\"\" src=\"http://www.google.co.uk/nav_next.gif\"/><br/>Next</a></td></tr></tbody></table>";
//				
//				//output = first + middle + last;
//			}
//			
//			out.println(output);
		
	
		
%>