<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * This file will generate all possible profile generation and re-ranking combinations
 * for all users that have an XML file. These are then used to calculate NDCG scores for
 * a relevance judgements offline evaluation.
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="org.cl.nm417.xmlparser.DataParser"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.cl.nm417.profile.UserProfile"%>
<%@page import="org.cl.nm417.config.ConfigLoader"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Generating profiles ...</title>
</head>
<body>

<%
	// Get the users for which to generate all the profiles
	String path = ConfigLoader.getConfig().getProperty("profiles");
	File bf = new File(path);
	ArrayList<String> users = new ArrayList<String>();
	for (String s: bf.list()){
		File nf = new File(path + s);
		if (nf.isDirectory()){
			users.add(s);
		}
	}
	
	String[] methods = new String[]{"tf","tfidf","bm25"};
	String[] filtering = new String[]{"posAll","posNoun","googlengram"};
	Boolean[] alternate = new Boolean[]{true, false};
	
	for (String user: users){
		
		out.println("Processing user " + user + "<br/><br/>");
		
		int processed = 0;
		
		AlterEgo.config.put("user", user);
		AlterEgo.config.put("ccparse", true);
		DataParser data = AlterEgo.getDataParser(user);
		
		AlterEgo.config.put("user", user); AlterEgo.config.put("split", true); AlterEgo.config.put("excludeDuplicate", false); 
		AlterEgo.config.put("googlengramW",1000); AlterEgo.config.put("titleW",1); AlterEgo.config.put("metadescriptionW",1); 
		AlterEgo.config.put("metakeywordsW",1); AlterEgo.config.put("plaintextW",1); AlterEgo.config.put("termsW",1); AlterEgo.config.put("ccparseW",1);
		AlterEgo.config.put("takeLog",false); AlterEgo.config.put("posVerb", false); AlterEgo.config.put("posAdjective", false); 
		AlterEgo.config.put("posAdverb", false);
		
		AlterEgo.config.put("ngrams",UserProfile.getGoogleNGrams(data));
		
		// Weighting methods
		for (String method: methods){
			AlterEgo.config.put("weighting",method);
			// Filtering methods
			for (String filter: filtering){
				if (filter.equals("posAll")){
					AlterEgo.config.put("posAll", true);
					AlterEgo.config.put("posNoun", false);
					AlterEgo.config.put("googlengram", false);
					AlterEgo.config.put("dict", null);
					// Collect statistics
					UserProfile.extractStatistics(data, true, true, true, true, true, true);
					AlterEgo.config.put("statistics", true);
				} else if (filter.equals("posNoun")){
					AlterEgo.config.put("posAll", false);
					AlterEgo.config.put("posNoun", true);
					AlterEgo.config.put("googlengram", false);
					AlterEgo.config.put("dict", UserProfile.getWordsInDict(data));
					// Collect statistics
					UserProfile.extractStatistics(data, true, true, true, true, true, true);
					AlterEgo.config.put("statistics", true);
				} else if (filter.equals("googlengram")){
					AlterEgo.config.put("posAll", false);
					AlterEgo.config.put("posNoun", false);
					AlterEgo.config.put("googlengram", true);
					AlterEgo.config.put("dict", null);
					// Collect statistics
					UserProfile.extractStatistics(data, true, true, true, true, true, true);
					AlterEgo.config.put("statistics", true);
				}
				// Relative weighting or not
				for (Boolean b1: alternate){
					AlterEgo.config.put("useRelativeW", b1);
					// Use title
					for (Boolean b2: alternate){
						AlterEgo.config.put("title", b2);
						// Use metadescription
						for (Boolean b3: alternate){
							AlterEgo.config.put("metadescription", b3);
							// Use metakeywords
							for (Boolean b4: alternate){
								AlterEgo.config.put("metakeywords", b4);
								// Use plain text
								for (Boolean b5: alternate){
									AlterEgo.config.put("plaintext", b5);
									// Use terms
									for (Boolean b6: alternate){
										AlterEgo.config.put("terms", b6);
										// Use cc parse
										for (Boolean b7: alternate){
											AlterEgo.config.put("ccparse", b7);
											if (!(b2 == false && b3 == false && b4 == false && b5 == false && b6 == false && b7 == false)){
												
												//Calculate filename
												String extention = user;
												
												// Weighting
												String weighting = (String)AlterEgo.config.get("weighting");
												if (weighting.equals("tf")){
													extention += "_t";
												} else if (weighting.equals("tfidf")){
													extention += "_ti";
												} else if (weighting.equals("bm25")){
													extention += "_b";
												}
												
												// Title
												boolean useRelative = (Boolean)AlterEgo.config.get("useRelativeW");
												boolean title = (Boolean)AlterEgo.config.get("title");
												if (title && useRelative){
													extention += "_r";
												} else if (title){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// Meta description
												boolean md = (Boolean)AlterEgo.config.get("metadescription");
												if (md && useRelative){
													extention += "_r";
												} else if (md){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// Meta keywords
												boolean mk = (Boolean)AlterEgo.config.get("metakeywords");
												if (mk && useRelative){
													extention += "_r";
												} else if (mk){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// Plain text
												boolean pt = (Boolean)AlterEgo.config.get("plaintext");
												if (pt && useRelative){
													extention += "_r";
												} else if (pt){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// Terms
												boolean t = (Boolean)AlterEgo.config.get("terms");
												if (t && useRelative){
													extention += "_r";
												} else if (t){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// C&C Parsed
												boolean cc = (Boolean)AlterEgo.config.get("ccparse");
												if (cc && useRelative){
													extention += "_r";
												} else if (cc){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												// Filtering
												boolean allPos = (Boolean)AlterEgo.config.get("posAll");
												boolean nGram = (Boolean)AlterEgo.config.get("googlengram");
												boolean posNoun = (Boolean)AlterEgo.config.get("posNoun");
												if (nGram){
													extention += "_g";
												} else if (posNoun){
													extention += "_wn";
												} else if (allPos){
													extention += "_n";
												}
												
												// Exclude duplicate pages
												boolean excludeDuplicate = (Boolean)AlterEgo.config.get("excludeDuplicate");
												if (excludeDuplicate){
													extention += "_y";
												} else {
													extention += "_n";
												}
												
												String profilepath = ConfigLoader.getConfig().getProperty("profiles") + user + "/" + extention + ".txt";
												File f = new File(profilepath);
												//Check if it already exists
												if (f.exists()){
													System.out.println("Skipped profile as it exists");
												} else {
													//If not, create the profile
													AlterEgo.generateProfile(AlterEgo.config, data);
												}
												processed++;
												out.println("Processed profile " + processed + " of 1134<br/>");
												out.flush();
											}
										}
									}
								}
							}
						}
					}
				}
			}
	
		}
		
		AlterEgo.config.put("ngrams", null);
		AlterEgo.config.put("statistics", null);
		
		out.println("<hr/><br/>");
	}
%>

</body>
</html>