<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Page that will generate an average NDCG score based on a relevance judgements experiment
 * for all possible profile and re-ranking combinations and will print them as a list.
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
<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.math.stat.inference.TTest"%>
<%@page import="org.apache.commons.math.stat.inference.TTestImpl"%>
<%@page import="org.cl.nm417.google.GoogleRerank"%>
<%@page import="org.cl.nm417.data.Profile"%>
<%@page import="org.cl.nm417.config.ConfigLoader"%><html
	xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>AlterEgo 0.2 :: Results</title>
<script type="text/javascript" language="JavaScript" src="js/jquery.js"></script>
<style>
html,body,table,tr,td {
	font-size: 12px;
	font-family: "Verdana";
}

table,tr,td {
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
	width: 50px;
}

.tstyle2 td {
	border: 1px solid black;
	text-align: center;
	padding: 5px;
	width: 100px;
}
</style>
<style type="text/css" media="screen">
@import "css/site_jui.ccss";

@import "css/demo_table_jui.css";

@import "css/jquery-ui-1.7.2.custom.css";

/*
 * Override styles needed due to the mix of three different CSS sources! For proper examples
 * please see the themes example in the 'Examples' section of this site
 */
.dataTables_info {
	padding-top: 0;
}

.dataTables_paginate {
	padding-top: 0;
}

.css_right {
	float: right;
}

#example_wrapper .fg-toolbar {
	font-size: 0.8em
}

#theme_links span {
	float: left;
	padding: 2px 10px;
}
</style>

<script type="text/javascript" src="js/complete.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
<script type="text/javascript">
	// This will transform the generated HTML table into one
	// that can be sorted and paged
	$(document).ready(function() {
		$('#example').dataTable( {
			"bJQueryUI" : true,
			"sPaginationType" : "full_numbers",
			"iDisplayLength" : 50000
		});
	});
</script>
</head>
<body>

<%

	String userid = request.getParameter("userid");

	// Re-ranking configuration to evaluate
	ArrayList<HashMap<String, Object>> methods = new ArrayList<HashMap<String, Object>>();
	String[] reranks = new String[] { "lm", "matching", "umatching" };
	Boolean[] bvalues = new Boolean[] { true, false };
	for (String rerank : reranks) {
		for (Boolean b1 : bvalues) {
			for (Boolean b2 : bvalues) {
				HashMap<String, Object> method = new HashMap<String, Object>();
				method.put("method", rerank);
				method.put("interleave", false);
				method.put("interleaveMethod", "teamdraft");
				method.put("lookatrank", b1);
				method.put("visited", b2);
				method.put("visitedW", 10);
				methods.add(method);
			}
		}
	}

	// PClick
	for (Boolean b1 : bvalues) {
		HashMap<String, Object> method = new HashMap<String, Object>();
		method.put("method", "pclick");
		method.put("interleave", false);
		method.put("interleaveMethod", "teamdraft");
		method.put("lookatrank", true);
		method.put("visited", b1);
		method.put("visitedW", 10);
		methods.add(method);
	}

	DecimalFormat threeDigit = new DecimalFormat("#,##0.000");
	DecimalFormat twoDigit = new DecimalFormat("#,##0.00");
	DecimalFormat oneDigit = new DecimalFormat("#,##0.0");

	// This page can be used in 2 views. One overall view which reports scores for 
	// all users or a view which reports the scores for one particular user
	if (userid != null && userid.length() > 0) {

		ArrayList<String> titles = new ArrayList<String>();
		titles.add("Top 10 results");
		ArrayList<Integer> max = new ArrayList<Integer>();
		max.add(10);

		ArrayList<String> fullNames = new ArrayList<String>();

		int prog = 0;
		for (String title : titles) {

			ArrayList<String> queries = Evaluation.getQueries(userid);

			// Extract user profiles to be extracted
			File f = new File(ConfigLoader.getConfig().getProperty("profiles") + userid);
			String[] ffiles = f.list();
			ArrayList<String> files = new ArrayList<String>();
			for (String s : ffiles) {
				if (s.startsWith("usr_")) {
					files.add(s);
				}
			}

			// Build table
			out.println("<div class='tstyle'><h3>" + title + "</h3>");
			out.println("<table cellspacing='0'>");
			String[] topColumn = new String[(files.size() * methods.size()) + 2];
			topColumn[0] = "Query";
			topColumn[1] = "1";

			fullNames = new ArrayList<String>();
			// First calculate the NDCG scores for Google
			fullNames.add("Google Web Rank");

			for (int i = 0; i < files.size(); i++) {
				String[] fname = files.get(i).substring(0, files.get(i).length() - 4).split("_");
				String name = "";
				for (int c = 2; c <= 10; c++) {
					name += fname[c] + "_";
				}
				int meth = 0;
				for (HashMap<String, Object> m : methods) {
					String fullName = name + " ";

					//Method
					String rer = (String) m.get("method");
					if (rer.equals("lm")) {
						fullName += "l_";
					} else if (rer.equals("matching")) {
						fullName += "m_";
					} else if (rer.equals("umatching")) {
						fullName += "u_";
					} else if (rer.equals("pclick")) {
						fullName += "p_";
					}

					//Interleave
					Boolean interleave = (Boolean) m.get("interleave");
					if (interleave == true) {
						fullName += "y_";
					} else if (interleave == false) {
						fullName += "n_";
					}

					//InterleaveMethod
					String interleaveMethod = (String) m.get("interleaveMethod");
					if (interleave == true) {
						if (interleaveMethod.equals("balanced")) {
							fullName += "b_";
						} else if (interleaveMethod.equals("teamdraft")) {
							fullName += "t_";
						}
					} else {
						fullName += "n_";
					}

					//Lookatrank
					Boolean lookatrank = (Boolean) m.get("lookatrank");
					if (lookatrank == true) {
						fullName += "y_";
					} else if (lookatrank == false) {
						fullName += "n_";
					}

					//Visited
					Boolean visited = (Boolean) m.get("visited");
					if (visited == true) {
						fullName += "y_";
					} else if (visited == false) {
						fullName += "n_";
					}

					//VisitedW
					fullName += (Integer) m.get("visitedW");
					fullNames.add(fullName);

					topColumn[(i * methods.size()) + meth + 2] = "" + ((i * methods.size()) + meth + 2);
					meth++;
				}
			}
			
			out.println(Evaluation.getRow(topColumn, true, null, null));

			ArrayList<ArrayList<Double>> idcgscores = new ArrayList<ArrayList<Double>>();
			for (int i = 0; i <= (files.size() * methods.size()); i++) {
				idcgscores.add(new ArrayList<Double>());
			}

			// Calculate the NDCG score for each of the queries
			for (String query : queries) {

				GoogleSnippet snippet = Evaluation.getNextSnippet(query, userid);

				if (snippet == null) {

					ArrayList<GoogleSnippet> snippets = Evaluation.getSnippets(query, userid);
					ArrayList<GoogleResult> results = Evaluation.getGoogleRanking(query, userid);
					results = GoogleRerank.doSort(results);
					String[] scores = new String[topColumn.length];

					String googleScore = Evaluation.calculateDCG(snippets, results, max.get(prog), true);
					idcgscores.get(0).add(Double.parseDouble(googleScore));
					scores[0] = query;
					scores[1] = googleScore;

					int i = 0;
					// For all profiles
					for (String file : files) {

						int meth = 0;
						for (HashMap<String, Object> m : methods) {

							results = Evaluation.getGoogleRanking(query, userid);
							Profile profile = new Profile();
							profile.setUserId(userid);
							profile.setURLs(GoogleRerank.getURLs(userid));
							profile.setUnigrams(AlterEgo.readFinalUnigrams(userid + "/" + file));

							results = AlterEgo.SearchGoogle(query, profile, (String) m.get("method"), (Boolean) m.get("interleave"), (String) m.get("interleaveMethod"), (Boolean) m.get("lookatrank"), (Boolean) m.get("visited"), (Integer) m.get("visitedW"), results);
							// Calculate the NDCG score
							String calcScore = Evaluation.calculateDCG(snippets, results, max.get(prog), true);
							scores[i * methods.size() + meth + 2] = calcScore;
							idcgscores.get((i * methods.size() + meth) + 1).add(Double.parseDouble(calcScore));
							meth++;

						}

						i++;

					}

					out.println(Evaluation.getRow(scores, false, "gradeA", null));
					out.flush();

				}
			}

			// Calculate the average NDCG over all queries for that user
			String[] avg = new String[topColumn.length];
			avg[0] = "Average IDCG";

			int index = 0;
			for (ArrayList<Double> arl : idcgscores) {
				index++;
				double avgidcgscore = 0.0;
				for (Double d : arl) {
					avgidcgscore += d;
				}
				avgidcgscore = avgidcgscore / arl.size();
				avg[index] = "" + threeDigit.format(avgidcgscore);
			}

			out.println(Evaluation.getRow(avg, true, "gradeA", null));
			out.flush();

			// Calculate the improvement over Google
			String[] impr = new String[topColumn.length];
			impr[0] = "Improvement over Google";
			index = 0;
			for (ArrayList<Double> arl : idcgscores) {
				index++;
				double avgidcgscore = 0.0;
				int iindex = 0;
				for (Double d : arl) {
					if (d == 0) {
						avgidcgscore += 0;
					} else {
						avgidcgscore += (1 - idcgscores.get(0).get(iindex) / d) * 100;
					}
					iindex++;
				}
				avgidcgscore = (avgidcgscore) / arl.size();
				impr[index] = "" + twoDigit.format(avgidcgscore) + "%";
			}
			out.println(Evaluation.getRow(impr, true, "gradeA", null));
			out.flush();

			// Significance testing
			String[] ttest = new String[topColumn.length];
			ttest[0] = "Significant at 95% level";
			String[] ttest2 = new String[topColumn.length];
			ttest2[0] = "Significant at 99% level";
			index = 0;
			double[] compareTo = new double[idcgscores.get(0).size()];
			for (Double d : idcgscores.get(0)) {
				compareTo[index] = d;
				index++;
			}
			index = 0;
			for (ArrayList<Double> arl : idcgscores) {
				double[] compare = new double[arl.size()];
				index++;
				int iindex = 0;
				for (Double d : arl) {
					compare[iindex] = d;
					iindex++;
				}
				TTestImpl tt = new TTestImpl();
				ttest[index] = "" + tt.pairedTTest(compareTo, compare, 0.05);
				ttest2[index] = "" + tt.pairedTTest(compareTo, compare, 0.01);

			}
			out.println(Evaluation.getRow(ttest, true, "gradeA", null));
			out.println(Evaluation.getRow(ttest2, true, "gradeA", null));
			out.flush();

			out.println("</table></div>");
			prog++;

		}

		out.println("<br/><br/><br/><ul>");
		int index = 0;
		for (String s : fullNames) {
			index++;
			out.println("<li>" + index + ". " + s + "</li>");
		}
		out.println("</ul>");

	} else {

		// Calculate NDCG scores for all users at the same time and report an
		// averaged NDCG score
		
		String path = ConfigLoader.getConfig().getProperty("profiles");
		File bf = new File(path);

		ArrayList<String> users = new ArrayList<String>();
		users.add("");
		ArrayList<String> profile = new ArrayList<String>();
		ArrayList<String> profiles = new ArrayList<String>();
		// Google is a baseline system
		profile.add("__ Google __");
		// Teevan is a baseline system
		profile.add("__ Teevan __");
		profiles.add("Google");
		profiles.add("Teevan");

		// Get list of all generated profiles
		for (String s : bf.list()) {

			File nf = new File(path + s);
			if (nf.isDirectory()) {
				users.add(s);
				for (String fn : nf.list()) {
					if (fn.startsWith("usr_")) {
						String[] spl = fn.split("_");
						String pname = "";
						String rname = "";
						for (int i = 2; i < spl.length - 1; i++) {
							pname += spl[i] + "_";
						}

						// Profile properties
						if (spl[2].equals("t")) {
							rname += "TF, ";
						} else if (spl[2].equals("ti")) {
							rname += "TFxIDF, ";
						} else if (spl[2].equals("b")) {
							rname += "BM25, ";
						}
						if (spl[3].equals("y")) {
							rname += "Title, ";
						} else if (spl[3].equals("r")) {
							rname += "RTitle, ";
						}
						if (spl[4].equals("y")) {
							rname += "MDescr, ";
						} else if (spl[4].equals("r")) {
							rname += "RMDescr, ";
						}
						if (spl[5].equals("y")) {
							rname += "MKeyw, ";
						} else if (spl[5].equals("r")) {
							rname += "RMKeyw, ";
						}
						if (spl[6].equals("y")) {
							rname += "Text, ";
						} else if (spl[6].equals("r")) {
							rname += "RText, ";
						}
						if (spl[7].equals("y")) {
							rname += "Terms, ";
						} else if (spl[7].equals("r")) {
							rname += "RTerms, ";
						}
						if (spl[8].equals("y")) {
							rname += "CCParse, ";
						} else if (spl[8].equals("r")) {
							rname += "RCCParse, ";
						}
						if (spl[9].equals("n")) {
							rname += "NoFilt ";
						} else if (spl[9].equals("g")) {
							rname += "GFilt ";
						} else if (spl[9].equals("w")) {
							rname += "WNFilt ";
						}

						pname += spl[spl.length - 1].substring(0, 1);
						if (!profiles.contains(pname)) {
							
							int meth = 0;
							for (HashMap<String, Object> m : methods) {

								String fullName = pname + " ";
								String readableName = rname + "- ";

								//Method
								String rer = (String) m.get("method");
								if (rer.equals("lm")) {
									readableName += "LM, ";
								} else if (rer.equals("matching")) {
									readableName += "Matching, ";
								} else if (rer.equals("umatching")) {
									readableName += "UMatching, ";
								} else if (rer.equals("pclick")) {
									readableName += "PClick, ";
								}

								//Interleave
								Boolean interleave = (Boolean) m.get("interleave");
								//InterleaveMethod
								String interleaveMethod = (String) m.get("interleaveMethod");
								if (interleave == true) {
									if (interleaveMethod.equals("balanced")) {
										readableName += "Balanced Interleave, ";
									} else if (interleaveMethod.equals("teamdraft")) {
										readableName += "Team Draft Interleave, ";
									}
								}

								//Lookatrank
								Boolean lookatrank = (Boolean) m.get("lookatrank");
								if (lookatrank == true) {
									readableName += "Look At Rank, ";
								} else if (lookatrank == false) {
									readableName += "Not Look At Rank, ";
								}

								//Visited
								Boolean visited = (Boolean) m.get("visited");
								if (visited == true) {
									readableName += "Visited ";
								} else if (visited == false) {
									readableName += "Not visited ";
								}

								//VisitedW
								fullName += (Integer) m.get("visitedW");
								profile.add(readableName);
								profiles.add(pname);

							}
						}
					}
				}
			}

		}
		users.add("Average NDCG");
		users.add("Improved");
		users.add("PairedT (0.05)");
		users.add("PairedT (0.01)");

		HashMap<String, HashMap<String, ArrayList<GoogleSnippet>>> mSnippets = new HashMap<String, HashMap<String, ArrayList<GoogleSnippet>>>();
		HashMap<String, HashMap<String, ArrayList<GoogleResult>>> mGoogleResults = new HashMap<String, HashMap<String, ArrayList<GoogleResult>>>();
		HashMap<String, ArrayList<String>> mqueries = new HashMap<String, ArrayList<String>>();
		HashMap<String, Profile> mprofiles = new HashMap<String, Profile>();
		ArrayList<String> usrs = new ArrayList<String>();

		for (int i = 1; i < users.size() - 4; i++) {
			userid = users.get(i);
			ArrayList<String> queries = Evaluation.getQueries(userid);
			mqueries.put(userid, queries);
			usrs.add(userid);
			mSnippets.put(userid, new HashMap<String, ArrayList<GoogleSnippet>>());
			mGoogleResults.put(userid, new HashMap<String, ArrayList<GoogleResult>>());
			Profile pr = new Profile();
			pr.setUserId(userid);
			pr.setURLs(GoogleRerank.getURLs(userid));
			mprofiles.put(userid, pr);
			for (String query : queries) {
				GoogleSnippet snippet = Evaluation.getNextSnippet(query, userid);
				if (snippet == null) {
					ArrayList<GoogleSnippet> snippets = Evaluation.getSnippets(query, userid);
					mSnippets.get(userid).put(query, snippets);
					ArrayList<GoogleResult> results = Evaluation.getGoogleRanking(query, userid);
					mGoogleResults.get(userid).put(query, results);
				}
			}
		}

		out.println("<div class='tstyle2'><table cellpadding='0' cellspacing='0' border='0' class='display' id='example'><thead>");
		out.println(Evaluation.getRow((String[])users.toArray(), true, null, "th"));
		out.println("</thead><tbody>");

		int progress = 0;

		int todo = profiles.size();

		// For each profile
		for (String cProfile : profiles) {

			ArrayList<String> fields = new ArrayList<String>();
			fields.add(profile.get(progress));

			ArrayList<Double> scores = new ArrayList<Double>();
			ArrayList<Double> gscores = new ArrayList<Double>();
			ArrayList<Double> avgs = new ArrayList<Double>();

			// Calculating for Google
			if (cProfile.equals("Google")) {

				for (String cuserid : usrs) {

					userid = cuserid;
					ArrayList<Double> cscores = new ArrayList<Double>();

					for (String query : mqueries.get(userid)) {

						ArrayList<GoogleSnippet> snippets = mSnippets.get(userid).get(query);
						ArrayList<GoogleResult> results = mGoogleResults.get(userid).get(query);
						for (GoogleResult res : results) {
							res.setWeight(0);
						}
						results = GoogleRerank.doSort(results);

						// No re-ranking is required
						String googleScore = Evaluation.calculateDCG(snippets, results, 10, true);
						Double d = Double.parseDouble(googleScore);
						gscores.add(d);
						cscores.add(d);
						scores.add(d);

					}

					double ascore = 0.0;
					for (Double score : cscores) {
						ascore += score;
					}
					avgs.add(ascore / cscores.size());
					fields.add(threeDigit.format(ascore / cscores.size()));

				}

			// Calculating NDCG for Teevan
			} else if (cProfile.equals("Teevan")) {

				for (String cuserid : usrs) {

					userid = cuserid;
					// Use plain text BM25 profile to approximate the Teevan implementation
					File f = new File(ConfigLoader.getConfig().getProperty("profiles") + userid + "/" + userid + "_b_n_n_n_y_n_n_n_n.txt");

					if (f.exists()) {

						// Load profile into memory
						mprofiles.get(userid).setUnigrams(AlterEgo.readFinalUnigrams(userid + "/" + userid + "_b_n_n_n_y_n_n_n_n.txt"));

						ArrayList<Double> cscores = new ArrayList<Double>();

						for (String query : mqueries.get(userid)) {

							ArrayList<GoogleSnippet> snippets = mSnippets.get(userid).get(query);
							ArrayList<GoogleResult> results = mGoogleResults.get(userid).get(query);
							for (GoogleResult res : results) {
								res.setWeight(0);
								res.setRank(res.getOriginalRank());
							}
							results = GoogleRerank.doSort(results);

							// Calculate NDCG score
							String googleScore = Evaluation.calculateDCG(snippets, results, 10, true);
							gscores.add(Double.parseDouble(googleScore));

							results = AlterEgo.SearchGoogle(query, mprofiles.get(userid), "lm", false, null, true, false, 10, results);
							String calcScore = Evaluation.calculateDCG(snippets, results, 10, true);
							Double d = Double.parseDouble(calcScore);
							System.out.println(query + " => " + d);
							cscores.add(d);
							scores.add(d);

						}

						double ascore = 0.0;
						for (Double score : cscores) {
							ascore += score;
						}
						avgs.add(ascore / cscores.size());
						fields.add(threeDigit.format(ascore / cscores.size()));

					} else {

						fields.add("N/A");

					}

				}

			// AlterEgo personalization strategies
			} else {

				for (String cuserid : usrs) {

					userid = cuserid;
					File f = new File(ConfigLoader.getConfig().getProperty("profiles") + userid + "/" + userid + "_" + cProfile + ".txt");
					mprofiles.get(userid).setUnigrams(AlterEgo.readFinalUnigrams(userid + "/" + userid + "_" + cProfile + ".txt"));

					if (f.exists()) {

						ArrayList<Double> cscores = new ArrayList<Double>();

						for (String query : mqueries.get(userid)) {

							ArrayList<GoogleSnippet> snippets = mSnippets.get(userid).get(query);
							ArrayList<GoogleResult> results = mGoogleResults.get(userid).get(query);
							for (GoogleResult res : results) {
								res.setWeight(0);
								res.setRank(res.getOriginalRank());
							}
							results = GoogleRerank.doSort(results);

							String googleScore = Evaluation.calculateDCG(snippets, results, 10, true);
							gscores.add(Double.parseDouble(googleScore));

							HashMap<String, Object> m = methods.get((progress - 2) % methods.size());

							// Calculate NDCG score
							results = AlterEgo.SearchGoogle(query, mprofiles.get(userid), (String) m.get("method"), (Boolean) m.get("interleave"), (String) m.get("interleaveMethod"), (Boolean) m.get("lookatrank"), (Boolean) m.get("visited"), (Integer) m.get("visitedW"), results);
							String calcScore = Evaluation.calculateDCG(snippets, results, 10, true);
							Double d = Double.parseDouble(calcScore);
							cscores.add(d);
							scores.add(d);

						}

						double ascore = 0.0;
						for (Double score : cscores) {
							ascore += score;
						}
						avgs.add(ascore / cscores.size());
						fields.add(threeDigit.format(ascore / cscores.size()));

					} else {

						fields.add("N/A");

					}

				}

			}

			// Get average NDCG
			Double favg = 0.0;
			for (Double avg : avgs) {
				favg += avg;
			}
			fields.add("" + threeDigit.format(favg / avgs.size()));

			// Get number of improved queries
			int better = 0;
			for (int d = 0; d < gscores.size(); d++) {
				//System.out.println(scores.get(d) + " vs " + gscores.get(d));
				if (scores.get(d) > gscores.get(d)) {
					better++;
				}
			}
			fields.add("" + better + "/" + gscores.size() + " (" + oneDigit.format(((double) better / (double) gscores.size()) * 100) + "%)");

			// Preparing to test for significance
			TTestImpl tt = new TTestImpl();
			int index = 0;
			double[] compareTo = new double[gscores.size()];
			for (Double d : gscores) {
				compareTo[index] = d;
				index++;
			}
			index = 0;
			double[] compare = new double[scores.size()];
			for (Double d : scores) {
				compare[index] = d;
				index++;
			}

			// Get 95% relevance mark
			fields.add("" + tt.pairedTTest(compareTo, compare, 0.05));
			// Get 99% relevance mark
			fields.add("" + tt.pairedTTest(compareTo, compare, 0.01));

			out.println(Evaluation.getRow((String[])fields.toArray(), false, "gradeA", "td"));
			out.flush();

			progress++;
			System.out.println("Processed " + progress + " of " + todo + " combinations");

		}

		out.println("</tbody></table></div>");

	}
%>

</body>
</html>