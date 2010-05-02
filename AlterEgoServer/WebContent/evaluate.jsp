<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.cl.nm417.google.GoogleSnippet"%>
<%@page import="org.cl.nm417.Evaluation"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cl.nm417.google.GoogleResult"%>
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
		
			query = request.getParameter("query").toLowerCase();
			String userid = request.getParameter("userid");
			String id = request.getParameter("id");
			String selected = request.getParameter("selected");
			
			Evaluation.saveRelevance(id, selected);
			GoogleSnippet snippet = Evaluation.getNextSnippet(query, userid);
			if (snippet != null){
			
				//out.println("<div style='font-size:12px;'><b>" + snippet.getTitle() + "</b><br/>");
				//out.println("<div style='width: 500px'>" + snippet.getSummary() + "</div>");
				//out.println("<a href=\"" + snippet.getUrl() + "\">" + snippet.getUrl() + "</a></div><br/>");
				//out.println("<table><tr><td><input type='radio' name='relevance' value='0' id='rel_0' CHECKED/></td><td>Irrelevant (0)</td></tr>");
				//out.println("<tr><td><input type='radio' name='relevance' value='1' id='rel_1'/></td><td>Somewhat relevant (1)</td></tr>");
				//out.println("<tr><td><input type='radio' name='relevance' value='2' id='rel_2'/></td><td>Relevant (2)</td></tr>");
				//out.println("<tr><td><input type='radio' name='relevance' value='3' id='rel_3'/></td><td>Very relevant (2)</td></tr>");
				//out.println("<tr><td colspan='2' style='height:30px;'><input type='button' value='Next' id='next'/></td></tr></table>");
				
		%>
		
		<input type='hidden' id='rel_id' value='<%=snippet.getId() %>'/>
		<input type='hidden' id='rel_query' value='<%=query %>'/>
		<input type='hidden' id='rel_userid' value='<%=userid %>'/>
		
		<input type='button' value='Irrelevant' id='rel_not' disabled="disabled"/>
		<input type='button' value='Relevant' id='rel_rel' disabled="disabled"/>
		<input type='button' value='Very relevant' id='rel_vrel' disabled="disabled"/>
		
		<div style="float:right; margin-top: -20px;">
			<div style="height: 20px; background-color: #B0171F; width: 100px; border: 1px solid black;"></div>
			<div style="position: relative; left: -40px; top: -22px; margin-left: 40px; height: 20px; background-color: #00CD66; width: <%= "" + Math.round(100 * (56 - snippet.getRemaining()) / 56) %>px; border: 1px solid black;"></div>
		</div>
		
		<br/><br/>
		
		<script type="text/javascript" language="JavaScript">
		
			parent.win.location = "<%=URLDecoder.decode(snippet.getUrl(),"utf-8") %>";
		
			var doSave = function(selected){
				
				var id = $("#rel_id").val();
				var search = $("#rel_query").val();
				var userid = $("#rel_userid").val();

				$("#rel_not").attr("disabled", true);
				$("#rel_rel").attr("disabled", true);
				$("#rel_vrel").attr("disabled", true);
				
				parent.output.location.href = "evaluate.jsp?id=" + id + "&selected=" + selected + "&query=" + encodeURI(search) + "&userid=" + userid;
			
			};

			var doActive = function(){
				$("#rel_not").attr("disabled", false);
				$("#rel_rel").attr("disabled", false);
				$("#rel_vrel").attr("disabled", false);
			}

			setTimeout(doActive, 3000);
	
			$("#rel_not").bind("click", function(ev){
				doSave("0");
			});	
	
			$("#rel_rel").bind("click", function(ev){
				doSave("1");
			});	
	
			$("#rel_vrel").bind("click", function(ev){
				doSave("3");
			});						
		
		</script>
		
		<%
				
			} else {
				
				//ArrayList <GoogleSnippet> snippets = Evaluation.getSnippets(query, userid);
				//ArrayList <GoogleResult> results = Evaluation.getGoogleRanking(query, userid);
				
				//out.println("<div class='tstyle'>");
				//out.println("<table cellspacing='0'>");
				//out.println(Evaluation.getRow(new String[]{"Method","Title","Meta description","Meta keywords","Plain text","Terms","C&C Parsed","Filtering","Exclude dup pages","Re-ranking","Keep Google Rank into account","Extra weight to visited URLs","DCG","NDCG","DCG10","NDCG10"},true));
				//out.println(Evaluation.getRow(new String[]{"Google","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A","N/A", Evaluation.calculateDCG(snippets, results, -1, false), Evaluation.calculateDCG(snippets, results, -1, true), Evaluation.calculateDCG(snippets, results, 10, false), Evaluation.calculateDCG(snippets, results, 10, true)}, false));
				//out.flush();
				
				//File f = new File("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + userid);
				//String[] files = f.list();
				
				//for (String file: files){
					
				//	try {
					
				//	results = AlterEgo.finalSearchGoogle(query, userid, "lm", false, null, true, true, 10, file, results);
					
				//	String[] split = file.substring(0, file.length() - 4).split("_");
					
				//	//Method
				//	String method = "";
				//	if (split[2].equals("t")){
				//		method = "TF";
				//	} else if (split[2].equals("ti")){
				//		method = "TFxIDF";
				//	} else if (split[2].equals("b")){
				//		method = "BM25";
				//	}
					
					//Title
				//	String title = "";
				//	if (split[3].equals("n")){
						//		title = "X";
				//	} else if (split[3].equals("y")){
				//		title = "V";
						//	} else if (split[3].equals("r")){
				//		title = "R";
				//	}
					
					//Meta description
				//	String md = "";
				//	if (split[4].equals("n")){
				//		md = "X";
				//	} else if (split[4].equals("y")){
				//		md = "V";
				//	} else if (split[4].equals("r")){
				//		md = "R";
				//	}
					
					//Meta keywords
				//	String mk = "";
				//	if (split[5].equals("n")){
				//		mk = "X";
				//	} else if (split[5].equals("y")){
				//		mk = "V";
				//	} else if (split[5].equals("r")){
				//		mk = "R";
				//	}
					
					//Plain text
				//	String pt = "";
				//	if (split[6].equals("n")){
				//		pt = "X";
				//	} else if (split[6].equals("y")){
				//		pt = "V";
				//	} else if (split[6].equals("r")){
				//		pt = "R";
				//	}
					
					//Terms
				//	String terms = "";
				//	if (split[7].equals("n")){
				//		terms = "X";
				//	} else if (split[7].equals("y")){
				//		terms = "V";
				//	} else if (split[7].equals("r")){
				//		terms = "R";
				//	}
					
					//C&C Parse
				//	String cc = "";
				//	if (split[8].equals("n")){
				//		cc = "X";
				//	} else if (split[8].equals("y")){
				//		cc = "V";
				//	} else if (split[8].equals("r")){
				//		cc = "R";
				//	}
					
					//Filtering
				//	String filt = "";
				//	if (split[9].equals("n")){
				//		filt = "X";
				//	} else if (split[9].equals("g")){
				//		filt = "Google NGram";
				//	} else if (split[9].equals("wn")){
				//		filt = "WN Nouns";
				//	}
					
					//Exclude Duplicate pages
				//	String exl = "";
				//	if (split[10].equals("n")){
				//		exl = "X";
				//	} else if (split[10].equals("y")){
				//		exl = "V";
				//	}
					
				//	out.println(Evaluation.getRow(new String[]{method, title, md, mk, pt, terms, cc, filt, exl, "Language Model", "V", "V", Evaluation.calculateDCG(snippets, results, -1, false), Evaluation.calculateDCG(snippets, results, -1, true), Evaluation.calculateDCG(snippets, results, 10, false), Evaluation.calculateDCG(snippets, results, 10, true)}, false));
				//	out.flush();
				//	
				//	} catch (Exception ex){
						
				//	}
					
				//}
				
				//out.println("</table></div>");
				
	%>
	
		<h1>Please provide a new search query</h1>
		
		<script type="text/javascript" language="JavaScript">
		
			parent.win.location = 'about:blank';

		</script>
	
	<%
	
			}
		}
	
	%>
	

</body>
</html>