<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Page that will be called by the AlterEgo Firefox add-on when the user
 * attempts a Google search. This page will select a random personalization
 * strategy and will re-rank the results according to that profile. The new
 * results will be packaged up in a Google-like HTML structure and will be sent
 * back to the Firefox add-on
 */
 -->
<%@page import="org.cl.nm417.config.ConfigLoader"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.DataOutputStream"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="org.cl.nm417.util.Logging"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Random"%>
<%@page import="java.util.Date"%><%@page import="java.net.URLDecoder"%>
<%@page import="org.cl.nm417.data.GoogleResult"%>
<%@page import="org.cl.nm417.google.GoogleSearch"%><%@ page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="org.cl.nm417.data.Unigram"%>
<%@page import="org.cl.nm417.data.Profile"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.HashMap"%>
<%
	
	Long startt = new Date().getTime();
	Logging log = new Logging();

	// User's unique identifier
	String user = request.getParameter("user"); // Required
	// Query they are searching for
	String query = request.getParameter("query"); // Required

	// Re-ranking method to use. This defaults to Language Model
	String method = "lm"; // Not required
	if (request.getParameter("method") != null) {
		method = request.getParameter("method");
	}

	// User profile to use. This can either be provided or one of the three
	// best ones will be used
	String profilename = ""; // Not required
	if (request.getParameter("profilename") != null) {
		profilename = request.getParameter("profilename");
	} else {
		ArrayList<String> arl = new ArrayList<String>();
		// 3 best profiles
		arl.add(user + "_ti_r_n_r_n_n_r_n_n.txt");
		arl.add(user + "_t_n_n_n_n_r_r_n_n.txt");
		arl.add(user + "_b_r_n_r_n_r_n_n_n.txt");
		
		// Seed the random with the current user, the current query and the
		// current hour in order not to get different results when the user
		// reloads the page
		Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.MILLISECOND, 0);
		long seed = (long) rightNow.getTime().getTime();
		for (int i = 0; i < query.length(); i++) {
			seed += (Math.pow((long) query.charAt(i), 3));
		}
		for (int i = 0; i < user.length(); i++) {
			seed += (Math.pow((long) user.charAt(i), 3));
		}
		int index = new Random(seed).nextInt(3);
		
		// Select the profile
		profilename = arl.get(index);
	}

	// Search page number
	int pagen = 1; // Not required
	if (request.getParameter("page") != null) {
		pagen = Integer.parseInt(request.getParameter("page"));
	}
	
	boolean rerank = true; // Not required
	if (request.getParameter("rerank") != null
			&& request.getParameter("rerank").equals("false")) {
		rerank = false;
	}

	// Whether interleaving should be used. By default, Team-Draft interleaving
	// will be used
	boolean interleave = true; // Not required
	if (request.getParameter("interleave") != null
			&& request.getParameter("interleave").equals("false")) {
		interleave = false;
	}
	String interleaveMethod = "teamdraft"; // Not required
	if (request.getParameter("interleaveMethod") != null) {
		interleaveMethod = request.getParameter("interleaveMethod");
	}

	boolean lookatrank = true; // Not required
	if (request.getParameter("lookatrank") != null
			&& request.getParameter("lookatrank").equals("false")) {
		lookatrank = false;
	}

	boolean showall = false; // Not required
	if (request.getParameter("showall") != null
			&& request.getParameter("showall").equals("true")) {
		showall = true;
	}

	boolean umatching = false; // Not required
	if (request.getParameter("method") != null
			&& request.getParameter("method").equals("umatching")) {
		umatching = true;
	}

	// Whether extra weight should be given to previously visited URLs.
	// By default, the snippet's score for a previously visited URL will be multiplied by 10
	boolean visited = true; // Not required
	if (request.getParameter("visited") != null
			&& request.getParameter("visited").equals("false")) {
		visited = false;
	}
	int visitedW = 10; // Not required
	if (request.getParameter("visitedW") != null) {
		visitedW = Integer.parseInt(request.getParameter("visitedW"));
	}

	// Start creating log entry
	log.appendLog("<!--");
	log.appendLog("\tTime: " + new Date().toString());
	log.appendLog("\tUser: " + user);
	log.appendLog("\tQuery: " + query);
	log.appendLog("\tPage: " + pagen);
	log.appendLog("\tProfile: " + profilename);

	String output = "";
	// As we only request 50 results, we do no re-ranking past page 5
	if (pagen <= 5) {

		output = "<span style='visibility: visible;' id='topstuff'></span><span style='visibility: visible;' id='search'><!--a-->";
		output += "<h2 class='hd'>Search Results</h2><div><ol id='rso'>";

		// Get the re-ranked Google results using the selected configuration
		ArrayList<GoogleResult> results = AlterEgo.SearchGoogle(query,
				user, profilename, rerank, method, interleave,
				interleaveMethod, lookatrank, umatching, visited,
				visitedW, log);
		
		// Calculate the result rankings
		int record = (pagen - 1) * 10;
		int start = 0;
		int end = 50;
		if (!showall) {
			start = record;
			end = record + 10;
		}
		int current = 0;
		
		log.appendLog("\tShown results:");
		
		
		for (int i = start; i < end; i++) {
			int rank = start + current + 1;
			current++;
			GoogleResult res = results.get(i);

			Pattern pattern = Pattern.compile("\\s+");
			String squery = query.replaceAll("[+]", " ");

			ArrayList<String> first = new ArrayList<String>();
			ArrayList<String> next = new ArrayList<String>();

			// If we find words from the search query in the snippet title or summary,
			// we make it bold
			String[] spl = pattern.split(squery);
			for (String s : spl) {

				// Title

				first = new ArrayList<String>();
				next = new ArrayList<String>();

				while (res.getTitle().toLowerCase().indexOf(
						s.toLowerCase()) >= 0) {
					int index = res.getTitle().toLowerCase().indexOf(
							s.toLowerCase());
					first.add(res.getTitle().substring(0, index));
					next.add(res.getTitle().substring(index,
							index + s.length()));
					res.setTitle(res.getTitle().substring(
							index + s.length()));
				}
				first.add(res.getTitle());

				String title = "";
				for (int prog = 0; prog < next.size(); prog++) {
					title += first.get(prog) + "<b>" + next.get(prog)
							+ "</b>";
				}
				title += first.get(first.size() - 1);
				res.setTitle(title.trim());

				// Summary

				first = new ArrayList<String>();
				next = new ArrayList<String>();

				while (res.getSummary().toLowerCase().indexOf(
						s.toLowerCase()) >= 0) {
					int index = res.getSummary().toLowerCase().indexOf(
							s.toLowerCase());
					first.add(res.getSummary().substring(0, index));
					next.add(res.getSummary().substring(index,
							index + s.length()));
					res.setSummary(res.getSummary().substring(
							index + s.length()));
				}
				first.add(res.getSummary());

				String summary = "";
				for (int prog = 0; prog < next.size(); prog++) {
					summary += first.get(prog) + "<b>" + next.get(prog)
							+ "</b>";
				}
				summary += first.get(first.size() - 1);
				res.setSummary(summary.trim());
			}
			
			// Generate the URL which should be opened when the result is clicked, in order to store the vote
			String realUrl = "http://alterego.caret.cam.ac.uk/search/AlterEgoServer/storeEval.jsp?userid="
					+ user
					+ "&query="
					+ query.replaceAll("'", "%27")
					+ "&clickedrank="
					+ rank
					+ "&origranking="
					+ res.getOriginalRank()
					+ "&clickedranking="
					+ res.getTeam()
					+ "&evaluating="
					+ (profilename
							.substring(profilename.indexOf("_") + 1))
							.substring((profilename
									.substring(profilename.indexOf("_") + 1))
									.indexOf("_") + 1)
					+ "&url="
					+ res.getUrl() + "&pagen=" + pagen;
			// Generate the HTML code for the Google result
			output += "<li class=\"g\"><h3 class=\"r\"><a class=\"l\" id='alterego_result_"
					+ rank
					+ "' href=\""
					+ res.getUrl()
					+ "\" onclick=\"document.getElementById('alterego_result_"
					+ rank
					+ "').href='"
					+ realUrl
					+ "';\">"
					+ res.getTitle()
					+ "</a></h3><div class=\"s\">"
					+ res.getSummary()
					+ "<br/><cite>"
					+ res.getUrl()
					+ " - </cite><span class=\"gl\"><a href=\"\">Cached</a> - <a href=\"\">Similar</a>"; // - " + rank + "(" + res.getOriginalRank() + ")";
			output += "</span>" + "</div></li>";
			log.appendLog("\t\t" + rank + ". " + res.getTitle() + " ("
					+ res.getUrl() + " - " + res.getTeam() + " - "
					+ res.getRank() + ")");
		}

		// Close the list
		output += "</ol></div><!--z--></span><span style='visibility: visible;' id='botstuff'>  </span>";

	}

	log.appendLog("-->");
	out.println(output);

	// Write the log entry into the evaluation log file
	try {
		// Create file 
		FileOutputStream file = new FileOutputStream(ConfigLoader.getConfig().getProperty("logfile"), true);
		DataOutputStream dos = new DataOutputStream(file);
		dos.writeBytes(log.getLog());
		dos.close();
		file.close();
	} catch (Exception e) { //Catch exception if any
		System.err.println("Error: " + e.getMessage());
	}

	Long endt = new Date().getTime();
	System.out.println("Finished doing search in " + (endt - startt) + " ms");
%>