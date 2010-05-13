<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.cl.nm417.evaluation.EvaluationResults"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cl.nm417.evaluation.EvaluationResult"%>

<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>AlterEgo 0.2 :: </title>
</head>
<body>

<%

	ArrayList<EvaluationResult> results = (new EvaluationResults()).getResults();

	int googleRank = -1; EvaluationResult googleResult = null; 
	int teevanRank = -1; EvaluationResult teevanResult = null;
	int rel005 = 0; int rel001 = 0;
	
	for (int i = 0; i < results.size(); i++){
		if (results.get(i).getName().equals("Google")){
			googleRank = i + 1;
			googleResult = results.get(i);
		} else if (results.get(i).getName().equals("Teevan")){
			teevanRank = i + 1;
			teevanResult = results.get(i);
		}
	}
	

	for (int i = 0; i < results.size(); i++){
		if (results.get(i).getAverage() > googleResult.getAverage()){
			if (results.get(i).isPairedT005()){
				rel005++;
			}
			if (results.get(i).isPairedT001()){
				rel001++;
			}
		}
	}
	
	DecimalFormat threeDigit = new DecimalFormat("#,##0.000");

%>

<!-- Number of results -->

Number of profile - reranking combinations investigated: <b><%= results.size() - 2 %></b>

<br/><br/>

Number of profile - reranking combination better than Google: <b><%= googleRank - 2 %></b>
<br/>
<%

	int googleConsistent = 0; int teevanConsistent = 0;
	ArrayList<EvaluationResult> teevanBetter = new ArrayList<EvaluationResult>();
	for (int i = 0; i < results.size(); i++){
		EvaluationResult res = results.get(i);
		if (res.getAaron() > googleResult.getAaron() && res.getChristian() > googleResult.getChristian() &&
				res.getFie() > googleResult.getFie() && res.getNicolaas() > googleResult.getNicolaas() &&
				res.getOszkar() > googleResult.getOszkar() && res.getSimon() > googleResult.getSimon()){
			googleConsistent++;
		}
		if (res.getAaron() > teevanResult.getAaron() && res.getChristian() > teevanResult.getChristian() &&
				res.getFie() > teevanResult.getFie() && res.getNicolaas() > teevanResult.getNicolaas() &&
				res.getOszkar() > teevanResult.getOszkar() && res.getSimon() > teevanResult.getSimon()){
			teevanConsistent++;
			teevanBetter.add(res);
		}
	}

%>
Number of profile - reranking significantly (0.05) better than Google: <b><%= rel005 %></b>
<br/>
Number of profile - reranking significantly (0.01) better than Google: <b><%= rel001 %></b>
<br/>
Number of profile - reranking combination better than Google for all users: <b><%= googleConsistent %></b>
<br/><br/>
Number of profile - reranking combination better than Teevan: <b><%= teevanRank - 1 %></b>
<br/>
Number of profile - reranking combination better than Teevan for all users: <b><%= teevanConsistent %></b>

<br/><br/>

Best profile - reranking combination(s) based on average NDCG:
<br/><br/>
<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Method</th>
		<th>Average NDCG</th>
		<th>Improvement over Google</th>
		<th>Improvement over Teevan</th>
		<th>Queries Improved</th>
	</tr>
	<%
	
		double ref = results.get(0).getAverage();
		int prog = 0;
		while (results.get(prog).getAverage() >= ref){

	%>
	
	<tr>
		<td><%= results.get(prog).getName() %></td>
		<td><%= results.get(prog).getAverage() %></td>
		<td><%= threeDigit.format((results.get(prog).getAverage() / googleResult.getAverage()) * 100 - 100) %>%</td>
		<td><%= threeDigit.format((results.get(prog).getAverage() / teevanResult.getAverage()) * 100 - 100) %>%</td>
		<td><%= results.get(prog).getImproved() %> / 72</td>
	</tr>
	
	<%
			
			prog++;

		}
	
	%>
</table>

<br/>

Best profile - reranking combination(s) based on improved number of queries:
<br/><br/>
<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Method</th>
		<th>Average NDCG</th>
		<th>Queries Improved</th>
	</tr>
	<%
	
		double highest = 0;
		for (int i = 0; i < results.size(); i++){
			if (!(results.get(i).getName().equals("Google") || results.get(i).getName().equals("Teevan"))){
				if (results.get(i).getImproved() > highest){
					highest = results.get(i).getImproved();
				}
			}
		}
		for (int i = 0; i < results.size(); i++){
			if (!(results.get(i).getName().equals("Google") || results.get(i).getName().equals("Teevan"))){
				if (results.get(i).getImproved() == highest){

	%>
	
	<tr>
		<td><%= results.get(i).getName() %></td>
		<td><%= results.get(i).getAverage() %></td>
		<td><%= results.get(i).getImproved() %> / 72</td>
	</tr>
	
	<%
	
				}
			}
		}
	
	%>
</table>

<br/>
Different values for the different parameters and their influence in the profiles better than Teevan:
<br/><br/>
<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Weighting method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int tf = 0; int tfrank = 0; double tfscore = 0.0; int tfbest = 0;
	int tfidf = 0; int tfidfrank = 0; double tfidfscore = 0.0; int tfidfbest = 0;
	int bm25 = 0; int bm25rank = 0; double bm25score = 0.0; int bm25best = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getMethod().equals("TF")){
			tf++; tfrank += prog + 1; tfscore += res.getAverage();
			if (tfbest == 0){ tfbest = prog + 1; }
		} else if (res.getMethod().equals("TFxIDF")){
			tfidf++; tfidfrank += prog + 1; tfidfscore += res.getAverage();
			if (tfidfbest == 0){ tfidfbest = prog + 1; }
		} else if (res.getMethod().equals("BM25")){
			bm25++; bm25rank += prog + 1; bm25score += res.getAverage();
			if (bm25best == 0){ bm25best = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>TF</td>
		<td><%= tf %></td>
		<td><%= tfrank / tf %></td>
		<td><%= threeDigit.format(tfscore / tf) %></td>
		<td><%= tfbest %></td>
	</tr>
	<tr>
		<td>TFxIDF</td>
		<td><%= tfidf %></td>
		<td><%= tfidfrank / tfidf %></td>
		<td><%= threeDigit.format(tfidfscore / tfidf) %></td>
		<td><%= tfidfbest %></td>
	</tr>
	<tr>
		<td>BM25</td>
		<td><%= bm25 %></td>
		<td><%= bm25rank / bm25 %></td>
		<td><%= threeDigit.format(bm25score / bm25) %></td>
		<td><%= bm25best %></td>
	</tr>
	<tr>
		<th>Filtering method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int nofilt = 0; int nofiltrank = 0; double nofiltscore = 0.0; int nofiltbest = 0;
	int gngram = 0; int gngramrank = 0; double gngramscore = 0.0; int gngrambest = 0;
	int wn = 0; int wnrank = 0; double wnscore = 0.0; int wnbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getFiltering().equals("n")){
			nofilt++; nofiltrank += prog + 1; nofiltscore += res.getAverage();
			if (nofiltbest == 0){ nofiltbest = prog + 1; }
		} else if (res.getFiltering().equals("g")){
			gngram++; gngramrank += prog + 1; gngramscore += res.getAverage();
			if (gngrambest == 0){ gngrambest = prog + 1; }
		} else if (res.getFiltering().equals("w")){
			wn++; wnrank += prog + 1; wnscore += res.getAverage();
			if (wnbest == 0){ wnbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nofilt %></td>
		<td><%= nofiltrank / nofilt %></td>
		<td><%= threeDigit.format(nofiltscore / nofilt) %></td>
		<td><%= nofiltbest %></td>
	</tr>
	<tr>
		<td>Google NGram</td>
		<td><%= gngram %></td>
		<td><%= gngramrank / gngram %></td>
		<td><%= threeDigit.format(gngramscore / gngram) %></td>
		<td><%= gngrambest %></td>
	</tr>
	<tr>
		<td>WordNet</td>
		<td><%= wn %></td>
		<td><%= wnrank / wn %></td>
		<td><%= threeDigit.format(wnscore / wn) %></td>
		<td><%= wnbest %></td>
	</tr>
	<tr>
		<th>Plain text</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int text = 0; int textrank = 0; double textscore = 0.0; int textbest = 0;
	int rtext = 0; int rtextrank = 0; double rtextscore = 0.0; int rtextbest = 0;
	int ntext = 0; int ntextrank = 0; double ntextscore = 0.0; int ntextbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getText().equals("n")){
			ntext++; ntextrank += prog + 1; ntextscore += res.getAverage();
			if (ntextbest == 0){ ntextbest = prog + 1; }
		} else if (res.getText().equals("y")){
			text++; textrank += prog + 1; textscore += res.getAverage();
			if (textbest == 0){ textbest = prog + 1; }
		} else if (res.getText().equals("r")){
			rtext++; rtextrank += prog + 1; rtextscore += res.getAverage();
			if (rtextbest == 0){ rtextbest = prog + 1; }
		} 
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= ntext %></td>
		<td><%= ntextrank / ntext %></td>
		<td><%= threeDigit.format(ntextscore / ntext) %></td>
		<td><%= ntextbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= text %></td>
		<td><%= textrank / text %></td>
		<td><%= threeDigit.format(textscore / text) %></td>
		<td><%= textbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rtext %></td>
		<td><%= rtextrank / rtext %></td>
		<td><%= threeDigit.format(rtextscore / rtext) %></td>
		<td><%= rtextbest %></td>
	</tr>
	<tr>
		<th>Title</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int title = 0; int titlerank = 0; double titlescore = 0.0; int titlebest = 0;
	int rtitle = 0; int rtitlerank = 0; double rtitlescore = 0.0; int rtitlebest = 0;
	int ntitle = 0; int ntitlerank = 0; double ntitlescore = 0.0; int ntitlebest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getTitle().equals("n")){
			ntitle++; ntitlerank += prog + 1; ntitlescore += res.getAverage();
			if (ntitlebest == 0){ ntitlebest = prog + 1; }
		} else if (res.getTitle().equals("y")){
			title++; titlerank += prog + 1; titlescore += res.getAverage();
			if (titlebest == 0){ titlebest = prog + 1; }
		} else if (res.getTitle().equals("r")){
			rtitle++; rtitlerank += prog + 1; rtitlescore += res.getAverage();
			if (rtitlebest == 0){ rtitlebest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= ntitle %></td>
		<td><%= ntitlerank / ntitle %></td>
		<td><%= threeDigit.format(ntitlescore / ntitle) %></td>
		<td><%= ntitlebest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= title %></td>
		<td><%= titlerank / title %></td>
		<td><%= threeDigit.format(titlescore / title) %></td>
		<td><%= titlebest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rtitle %></td>
		<td><%= rtitlerank / rtitle %></td>
		<td><%= threeDigit.format(rtitlescore / rtitle) %></td>
		<td><%= rtitlebest %></td>
	</tr>
	<tr>
		<th>Meta description</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int md = 0; int mdrank = 0; double mdscore = 0.0; int mdbest = 0;
	int rmd = 0; int rmdrank = 0; double rmdscore = 0.0; int rmdbest = 0;
	int nmd = 0; int nmdrank = 0; double nmdscore = 0.0; int nmdbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getMetadescription().equals("n")){
			nmd++; nmdrank += prog + 1; nmdscore += res.getAverage();
			if (nmdbest == 0){ nmdbest = prog + 1; }
		} else if (res.getMetadescription().equals("y")){
			md++; mdrank += prog + 1; mdscore += res.getAverage();
			if (mdbest == 0){ mdbest = prog + 1; }
		} else if (res.getMetadescription().equals("r")){
			rmd++; rmdrank += prog + 1; rmdscore += res.getAverage();
			if (rmdbest == 0){ rmdbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nmd %></td>
		<td><%= nmdrank / nmd %></td>
		<td><%= threeDigit.format(nmdscore / nmd) %></td>
		<td><%= nmdbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= md %></td>
		<td><%= mdrank / md %></td>
		<td><%= threeDigit.format(mdscore / md) %></td>
		<td><%= mdbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rmd %></td>
		<td><%= rmdrank / rmd %></td>
		<td><%= threeDigit.format(rmdscore / rmd) %></td>
		<td><%= rmdbest %></td>
	</tr>
	<tr>
		<th>Meta keywords</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int mk = 0; int mkrank = 0; double mkscore = 0.0; int mkbest = 0;
	int rmk = 0; int rmkrank = 0; double rmkscore = 0.0; int rmkbest = 0;
	int nmk = 0; int nmkrank = 0; double nmkscore = 0.0; int nmkbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getMetakeywords().equals("n")){
			nmk++; nmkrank += prog + 1; nmkscore += res.getAverage();
			if (nmkbest == 0){ nmkbest = prog + 1; }
		} else if (res.getMetakeywords().equals("y")){
			mk++; mkrank += prog + 1; mkscore += res.getAverage();
			if (mkbest == 0){ mkbest = prog + 1; }
		} else if (res.getMetakeywords().equals("r")){
			rmk++; rmkrank += prog + 1; rmkscore += res.getAverage();
			if (rmkbest == 0){ rmkbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nmk %></td>
		<td><%= nmkrank / nmk %></td>
		<td><%= threeDigit.format(nmkscore / nmk) %></td>
		<td><%= nmkbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= mk %></td>
		<td><%= mkrank / mk %></td>
		<td><%= threeDigit.format(mkscore / mk) %></td>
		<td><%= mkbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rmk %></td>
		<td><%= rmkrank / rmk %></td>
		<td><%= threeDigit.format(rmkscore / rmk) %></td>
		<td><%= rmkbest %></td>
	</tr>
	<tr>
		<th>Terms</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%
 
	int terms = 0; int termsrank = 0; double termsscore = 0.0; int termsbest = 0;
	int rterms = 0; int rtermsrank = 0; double rtermsscore = 0.0; int rtermsbest = 0;
	int nterms = 0; int ntermsrank = 0; double ntermsscore = 0.0; int ntermsbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getTerms().equals("n")){
			nterms++; ntermsrank += prog + 1; ntermsscore += res.getAverage();
			if (ntermsbest == 0){ ntermsbest = prog + 1; }
		} else if (res.getTerms().equals("y")){
			terms++; termsrank += prog + 1; termsscore += res.getAverage();
			if (termsbest == 0){ termsbest = prog + 1; }
		} else if (res.getTerms().equals("r")){
			rterms++; rtermsrank += prog + 1; rtermsscore += res.getAverage();
			if (rtermsbest == 0){ rtermsbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nterms %></td>
		<td><%= ntermsrank / nterms %></td>
		<td><%= threeDigit.format(ntermsscore / nterms) %></td>
		<td><%= ntermsbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= terms %></td>
		<td><%= termsrank / terms %></td>
		<td><%= threeDigit.format(termsscore / terms) %></td>
		<td><%= termsbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rterms %></td>
		<td><%= rtermsrank / rterms %></td>
		<td><%= threeDigit.format(rtermsscore / rterms) %></td>
		<td><%= rtermsbest %></td>
	</tr>
	<tr>
		<th>CCParse</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int ccparse = 0; int ccparserank = 0; double ccparsescore = 0.0; int ccparsebest = 0;
	int rccparse = 0; int rccparserank = 0; double rccparsescore = 0.0; int rccparsebest = 0;
	int nccparse = 0; int nccparserank = 0; double nccparsescore = 0.0; int nccparsebest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getCcparse().equals("n")){
			nccparse++; nccparserank += prog + 1; nccparsescore += res.getAverage();
			if (nccparsebest == 0){ nccparsebest = prog + 1; }
		} else if (res.getCcparse().equals("y")){
			ccparse++; ccparserank += prog + 1; ccparsescore += res.getAverage();
			if (ccparsebest == 0){ ccparsebest = prog + 1; }
		} else if (res.getCcparse().equals("r")){
			rccparse++; rccparserank += prog + 1; rccparsescore += res.getAverage();
			if (rccparsebest == 0){ rccparsebest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nccparse %></td>
		<td><%= nccparserank / nccparse %></td>
		<td><%= threeDigit.format(nccparsescore / nccparse) %></td>
		<td><%= nccparsebest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= ccparse %></td>
		<td><%= ccparserank / ccparse %></td>
		<td><%= threeDigit.format(ccparsescore / ccparse) %></td>
		<td><%= ccparsebest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rccparse %></td>
		<td><%= rccparserank / rccparse %></td>
		<td><%= threeDigit.format(rccparsescore / rccparse) %></td>
		<td><%= rccparsebest %></td>
	</tr>
	<tr>
		<th>Reranking method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int lm = 0; int lmrank = 0; double lmscore = 0.0; int lmbest = 0;
	int matching = 0; int matchingrank = 0; double matchingscore = 0.0; int matchingbest = 0;
	int umatching = 0; int umatchingrank = 0; double umatchingscore = 0.0; int umatchingbest = 0;
	int pclick = 0; int pclickrank = 0; double pclickscore = 0.0; int pclickbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.getRerank().equals("LM")){
			lm++; lmrank += prog + 1; lmscore += res.getAverage();
			if (lmbest == 0){ lmbest = prog + 1; }
		} else if (res.getRerank().equals("UMatching")){
			umatching++; umatchingrank += prog + 1; umatchingscore += res.getAverage();
			if (umatchingbest == 0){ umatchingbest = prog + 1; }
		} else if (res.getRerank().equals("Matching")){
			matching++; matchingrank += prog + 1; matchingscore += res.getAverage();
			if (matchingbest == 0){ matchingbest = prog + 1; }
		} else if (res.getRerank().equals("PClick")){
			pclick++; pclickrank += prog + 1; pclickscore += res.getAverage();
			if (pclickbest == 0){ pclickbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>LM</td>
		<td><%= lm %></td>
		<td><%= lmrank / lm %></td>
		<td><%= threeDigit.format(lmscore / lm) %></td>
		<td><%= lmbest %></td>
	</tr>
	<tr>
		<td>Matching</td>
		<td><%= matching %></td>
		<td><%= matchingrank / matching %></td>
		<td><%= threeDigit.format(matchingscore / matching) %></td>
		<td><%= matchingbest %></td>
	</tr>
	<tr>
		<td>UMatching</td>
		<td><%= umatching %></td>
		<td><%= umatching == 0 ? "N/A" : (umatchingrank / umatching) %></td>
		<td><%= umatching == 0 ? "N/A" : (umatchingscore / umatching) %></td>
		<td><%= umatchingbest %></td>
	</tr>
	<tr>
		<td>PClick</td>
		<td><%= pclick %></td>
		<td><%= pclick == 0 ? "N/A" : (pclickrank / pclick) %></td>
		<td><%= threeDigit.format(pclickscore / pclick) %></td>
		<td><%= pclickbest %></td>
	</tr>
	<tr>
		<th>Extra weight to visited URLs</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int visited = 0; int visitedrank = 0; double visitedscore = 0.0; int visitedbest = 0;
	int nvisited = 0; int nvisitedrank = 0; double nvisitedscore = 0.0; int nvisitedbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.isVisited()){
			visited++; visitedrank += prog + 1; visitedscore += res.getAverage();
			if (visitedbest == 0){ visitedbest = prog + 1; }
		} else {
			nvisited++; nvisitedrank += prog + 1; nvisitedscore += res.getAverage();
			if (nvisitedbest == 0){ nvisitedbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>Yes (* 10)</td>
		<td><%= visited %></td>
		<td><%= visited == 0 ? "N/A" : (visitedrank / visited) %></td>
		<td><%= visited == 0 ? "N/A" : (threeDigit.format(visitedscore / visited)) %></td>
		<td><%= visitedbest %></td>
	</tr>
	<tr>
		<td>No</td>
		<td><%= nvisited %></td>
		<td><%= nvisited == 0 ? "N/A" : (nvisitedrank / nvisited) %></td>
		<td><%= nvisited == 0 ? "N/A" : (threeDigit.format(nvisitedscore / nvisited)) %></td>
		<td><%= nvisitedbest %></td>
	</tr>
	<tr>
		<th>Keep Google rank into account</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	int grank = 0; int grankrank = 0; double grankrankscore = 0.0; int grankbest = 0;
	int ngrank = 0; int ngrankrank = 0; double ngrankrankscore = 0.0; int ngrankbest = 0;
	
	prog = 0;
	
	while (!results.get(prog).getName().equals("Teevan")){
		EvaluationResult res = results.get(prog);
		if (res.isLookatrank()){
			grank++; grankrank += prog + 1; grankrankscore += res.getAverage();
			if (grankbest == 0){ grankbest = prog + 1; }
		} else {
			ngrank++; ngrankrank += prog + 1; ngrankrankscore += res.getAverage();
			if (ngrankbest == 0){ ngrankbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>Yes (* 10)</td>
		<td><%= grank %></td>
		<td><%= grank == 0 ? "N/A" : (grankrank / grank) %></td>
		<td><%= grank == 0 ? "N/A" : (threeDigit.format(grankrankscore / grank)) %></td>
		<td><%= grankbest %></td>
	</tr>
	<tr>
		<td>No</td>
		<td><%= ngrank %></td>
		<td><%= ngrank == 0 ? "N/A" : (ngrankrank / ngrank) %></td>
		<td><%= ngrank == 0 ? "N/A" : (threeDigit.format(ngrankrankscore / ngrank)) %></td>
		<td><%= ngrankbest %></td>
	</tr>
</table>

<br/>
Different values for the different parameters and their influence in the profiles consistently better than Teevan:
<br/><br/>
<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Weighting method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	tf = 0; tfrank = 0; tfscore = 0.0; tfbest = 0;
	tfidf = 0; tfidfrank = 0; tfidfscore = 0.0; tfidfbest = 0;
	bm25 = 0; bm25rank = 0; bm25score = 0.0; bm25best = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getMethod().equals("TF")){
			tf++; tfrank += prog + 1; tfscore += res.getAverage();
			if (tfbest == 0){ tfbest = prog + 1; }
		} else if (res.getMethod().equals("TFxIDF")){
			tfidf++; tfidfrank += prog + 1; tfidfscore += res.getAverage();
			if (tfidfbest == 0){ tfidfbest = prog + 1; }
		} else if (res.getMethod().equals("BM25")){
			bm25++; bm25rank += prog + 1; bm25score += res.getAverage();
			if (bm25best == 0){ bm25best = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>TF</td>
		<td><%= tf %></td>
		<td><%= tfrank / tf %></td>
		<td><%= threeDigit.format(tfscore / tf) %></td>
		<td><%= tfbest %></td>
	</tr>
	<tr>
		<td>TFxIDF</td>
		<td><%= tfidf %></td>
		<td><%= tfidfrank / tfidf %></td>
		<td><%= threeDigit.format(tfidfscore / tfidf) %></td>
		<td><%= tfidfbest %></td>
	</tr>
	<tr>
		<td>BM25</td>
		<td><%= bm25 %></td>
		<td><%= bm25rank / bm25 %></td>
		<td><%= threeDigit.format(bm25score / bm25) %></td>
		<td><%= bm25best %></td>
	</tr>
	<tr>
		<th>Filtering method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	nofilt = 0; nofiltrank = 0; nofiltscore = 0.0; nofiltbest = 0;
	gngram = 0; gngramrank = 0; gngramscore = 0.0; gngrambest = 0;
	wn = 0; wnrank = 0; wnscore = 0.0; wnbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getFiltering().equals("n")){
			nofilt++; nofiltrank += prog + 1; nofiltscore += res.getAverage();
			if (nofiltbest == 0){ nofiltbest = prog + 1; }
		} else if (res.getFiltering().equals("g")){
			gngram++; gngramrank += prog + 1; gngramscore += res.getAverage();
			if (gngrambest == 0){ gngrambest = prog + 1; }
		} else if (res.getFiltering().equals("w")){
			wn++; wnrank += prog + 1; wnscore += res.getAverage();
			if (wnbest == 0){ wnbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nofilt %></td>
		<td><%= nofiltrank / nofilt %></td>
		<td><%= threeDigit.format(nofiltscore / nofilt) %></td>
		<td><%= nofiltbest %></td>
	</tr>
	<tr>
		<td>Google NGram</td>
		<td><%= gngram %></td>
		<td><%= gngramrank / gngram %></td>
		<td><%= threeDigit.format(gngramscore / gngram) %></td>
		<td><%= gngrambest %></td>
	</tr>
	<tr>
		<td>WordNet</td>
		<td><%= wn %></td>
		<td><%= wnrank / wn %></td>
		<td><%= threeDigit.format(wnscore / wn) %></td>
		<td><%= wnbest %></td>
	</tr>
	<tr>
		<th>Plain text</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	text = 0; textrank = 0; textscore = 0.0; textbest = 0;
	rtext = 0; rtextrank = 0; rtextscore = 0.0; rtextbest = 0;
	ntext = 0; ntextrank = 0; ntextscore = 0.0; ntextbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getText().equals("n")){
			ntext++; ntextrank += prog + 1; ntextscore += res.getAverage();
			if (ntextbest == 0){ ntextbest = prog + 1; }
		} else if (res.getText().equals("y")){
			text++; textrank += prog + 1; textscore += res.getAverage();
			if (textbest == 0){ textbest = prog + 1; }
		} else if (res.getText().equals("r")){
			rtext++; rtextrank += prog + 1; rtextscore += res.getAverage();
			if (rtextbest == 0){ rtextbest = prog + 1; }
		} 
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= ntext %></td>
		<td><%= ntextrank / ntext %></td>
		<td><%= threeDigit.format(ntextscore / ntext) %></td>
		<td><%= ntextbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= text %></td>
		<td><%= textrank / text %></td>
		<td><%= threeDigit.format(textscore / text) %></td>
		<td><%= textbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rtext %></td>
		<td><%= rtextrank / rtext %></td>
		<td><%= threeDigit.format(rtextscore / rtext) %></td>
		<td><%= rtextbest %></td>
	</tr>
	<tr>
		<th>Title</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	title = 0; titlerank = 0; titlescore = 0.0; titlebest = 0;
	rtitle = 0; rtitlerank = 0; rtitlescore = 0.0; rtitlebest = 0;
	ntitle = 0; ntitlerank = 0; ntitlescore = 0.0; ntitlebest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getTitle().equals("n")){
			ntitle++; ntitlerank += prog + 1; ntitlescore += res.getAverage();
			if (ntitlebest == 0){ ntitlebest = prog + 1; }
		} else if (res.getTitle().equals("y")){
			title++; titlerank += prog + 1; titlescore += res.getAverage();
			if (titlebest == 0){ titlebest = prog + 1; }
		} else if (res.getTitle().equals("r")){
			rtitle++; rtitlerank += prog + 1; rtitlescore += res.getAverage();
			if (rtitlebest == 0){ rtitlebest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= ntitle %></td>
		<td><%= ntitlerank / ntitle %></td>
		<td><%= threeDigit.format(ntitlescore / ntitle) %></td>
		<td><%= ntitlebest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= title %></td>
		<td><%= titlerank / title %></td>
		<td><%= threeDigit.format(titlescore / title) %></td>
		<td><%= titlebest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rtitle %></td>
		<td><%= rtitlerank / rtitle %></td>
		<td><%= threeDigit.format(rtitlescore / rtitle) %></td>
		<td><%= rtitlebest %></td>
	</tr>
	<tr>
		<th>Meta description</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	md = 0; mdrank = 0; mdscore = 0.0; mdbest = 0;
	rmd = 0; rmdrank = 0; rmdscore = 0.0; rmdbest = 0;
	nmd = 0; nmdrank = 0; nmdscore = 0.0; nmdbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getMetadescription().equals("n")){
			nmd++; nmdrank += prog + 1; nmdscore += res.getAverage();
			if (nmdbest == 0){ nmdbest = prog + 1; }
		} else if (res.getMetadescription().equals("y")){
			md++; mdrank += prog + 1; mdscore += res.getAverage();
			if (mdbest == 0){ mdbest = prog + 1; }
		} else if (res.getMetadescription().equals("r")){
			rmd++; rmdrank += prog + 1; rmdscore += res.getAverage();
			if (rmdbest == 0){ rmdbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nmd %></td>
		<td><%= nmdrank / nmd %></td>
		<td><%= threeDigit.format(nmdscore / nmd) %></td>
		<td><%= nmdbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= md %></td>
		<td><%= mdrank / md %></td>
		<td><%= threeDigit.format(mdscore / md) %></td>
		<td><%= mdbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rmd %></td>
		<td><%= rmdrank / rmd %></td>
		<td><%= threeDigit.format(rmdscore / rmd) %></td>
		<td><%= rmdbest %></td>
	</tr>
	<tr>
		<th>Meta keywords</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	mk = 0; mkrank = 0; mkscore = 0.0; mkbest = 0;
	rmk = 0; rmkrank = 0; rmkscore = 0.0; rmkbest = 0;
	nmk = 0; nmkrank = 0; nmkscore = 0.0; nmkbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getMetakeywords().equals("n")){
			nmk++; nmkrank += prog + 1; nmkscore += res.getAverage();
			if (nmkbest == 0){ nmkbest = prog + 1; }
		} else if (res.getMetakeywords().equals("y")){
			mk++; mkrank += prog + 1; mkscore += res.getAverage();
			if (mkbest == 0){ mkbest = prog + 1; }
		} else if (res.getMetakeywords().equals("r")){
			rmk++; rmkrank += prog + 1; rmkscore += res.getAverage();
			if (rmkbest == 0){ rmkbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nmk %></td>
		<td><%= nmkrank / nmk %></td>
		<td><%= threeDigit.format(nmkscore / nmk) %></td>
		<td><%= nmkbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= mk %></td>
		<td><%= mkrank / mk %></td>
		<td><%= threeDigit.format(mkscore / mk) %></td>
		<td><%= mkbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rmk %></td>
		<td><%= rmkrank / rmk %></td>
		<td><%= threeDigit.format(rmkscore / rmk) %></td>
		<td><%= rmkbest %></td>
	</tr>
	<tr>
		<th>Terms</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%
 
	terms = 0; termsrank = 0; termsscore = 0.0; termsbest = 0;
	rterms = 0; rtermsrank = 0; rtermsscore = 0.0; rtermsbest = 0;
	nterms = 0; ntermsrank = 0; ntermsscore = 0.0; ntermsbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getTerms().equals("n")){
			nterms++; ntermsrank += prog + 1; ntermsscore += res.getAverage();
			if (ntermsbest == 0){ ntermsbest = prog + 1; }
		} else if (res.getTerms().equals("y")){
			terms++; termsrank += prog + 1; termsscore += res.getAverage();
			if (termsbest == 0){ termsbest = prog + 1; }
		} else if (res.getTerms().equals("r")){
			rterms++; rtermsrank += prog + 1; rtermsscore += res.getAverage();
			if (rtermsbest == 0){ rtermsbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nterms %></td>
		<td><%= ntermsrank / nterms %></td>
		<td><%= threeDigit.format(ntermsscore / nterms) %></td>
		<td><%= ntermsbest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= terms %></td>
		<td><%= termsrank / terms %></td>
		<td><%= threeDigit.format(termsscore / terms) %></td>
		<td><%= termsbest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rterms %></td>
		<td><%= rtermsrank / rterms %></td>
		<td><%= threeDigit.format(rtermsscore / rterms) %></td>
		<td><%= rtermsbest %></td>
	</tr>
	<tr>
		<th>CCParse</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	ccparse = 0; ccparserank = 0; ccparsescore = 0.0; ccparsebest = 0;
	rccparse = 0; rccparserank = 0; rccparsescore = 0.0; rccparsebest = 0;
	nccparse = 0; nccparserank = 0; nccparsescore = 0.0; nccparsebest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getCcparse().equals("n")){
			nccparse++; nccparserank += prog + 1; nccparsescore += res.getAverage();
			if (nccparsebest == 0){ nccparsebest = prog + 1; }
		} else if (res.getCcparse().equals("y")){
			ccparse++; ccparserank += prog + 1; ccparsescore += res.getAverage();
			if (ccparsebest == 0){ ccparsebest = prog + 1; }
		} else if (res.getCcparse().equals("r")){
			rccparse++; rccparserank += prog + 1; rccparsescore += res.getAverage();
			if (rccparsebest == 0){ rccparsebest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>No</td>
		<td><%= nccparse %></td>
		<td><%= nccparse == 0 ? "N/A" : nccparserank / nccparse %></td>
		<td><%= nccparse == 0 ? "N/A" : threeDigit.format(nccparsescore / nccparse) %></td>
		<td><%= nccparsebest %></td>
	</tr>
	<tr>
		<td>Yes</td>
		<td><%= ccparse %></td>
		<td><%= ccparse == 0 ? "N/A" : ccparserank / ccparse %></td>
		<td><%= ccparse == 0 ? "N/A" : threeDigit.format(ccparsescore / ccparse) %></td>
		<td><%= ccparsebest %></td>
	</tr>
	<tr>
		<td>Relative</td>
		<td><%= rccparse %></td>
		<td><%= rccparse == 0 ? "N/A" : rccparserank / rccparse %></td>
		<td><%= rccparse == 0 ? "N/A" : threeDigit.format(rccparsescore / rccparse) %></td>
		<td><%= rccparsebest %></td>
	</tr>
	<tr>
		<th>Reranking method</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	lm = 0; lmrank = 0; lmscore = 0.0; lmbest = 0;
	matching = 0; matchingrank = 0; matchingscore = 0.0; matchingbest = 0;
	umatching = 0; umatchingrank = 0; umatchingscore = 0.0; umatchingbest = 0;
	pclick = 0; pclickrank = 0; pclickscore = 0.0; pclickbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.getRerank().equals("LM")){
			lm++; lmrank += prog + 1; lmscore += res.getAverage();
			if (lmbest == 0){ lmbest = prog + 1; }
		} else if (res.getRerank().equals("UMatching")){
			umatching++; umatchingrank += prog + 1; umatchingscore += res.getAverage();
			if (umatchingbest == 0){ umatchingbest = prog + 1; }
		} else if (res.getRerank().equals("Matching")){
			matching++; matchingrank += prog + 1; matchingscore += res.getAverage();
			if (matchingbest == 0){ matchingbest = prog + 1; }
		} else if (res.getRerank().equals("PClick")){
			pclick++; pclickrank += prog + 1; pclickscore += res.getAverage();
			if (pclickbest == 0){ pclickbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>LM</td>
		<td><%= lm %></td>
		<td><%= lm == 0 ? "N/A" : lmrank / lm %></td>
		<td><%= lm == 0 ? "N/A" : threeDigit.format(lmscore / lm) %></td>
		<td><%= lmbest %></td>
	</tr>
	<tr>
		<td>Matching</td>
		<td><%= matching %></td>
		<td><%= matching == 0 ? "N/A" : matchingrank / matching %></td>
		<td><%= matching == 0 ? "N/A" : threeDigit.format(matchingscore / matching) %></td>
		<td><%= matchingbest %></td>
	</tr>
	<tr>
		<td>UMatching</td>
		<td><%= umatching %></td>
		<td><%= umatching == 0 ? "N/A" : (umatchingrank / umatching) %></td>
		<td><%= umatching == 0 ? "N/A" : (umatchingscore / umatching) %></td>
		<td><%= umatchingbest %></td>
	</tr>
	<tr>
		<td>PClick</td>
		<td><%= pclick %></td>
		<td><%= pclick == 0 ? "N/A" : (pclickrank / pclick) %></td>
		<td><%= pclick == 0 ? "N/A" : threeDigit.format(pclickscore / pclick) %></td>
		<td><%= pclickbest %></td>
	</tr>
	<tr>
		<th>Extra weight to visited URLs</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	visited = 0; visitedrank = 0; visitedscore = 0.0; visitedbest = 0;
	nvisited = 0; nvisitedrank = 0; nvisitedscore = 0.0; nvisitedbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.isVisited()){
			visited++; visitedrank += prog + 1; visitedscore += res.getAverage();
			if (visitedbest == 0){ visitedbest = prog + 1; }
		} else {
			nvisited++; nvisitedrank += prog + 1; nvisitedscore += res.getAverage();
			if (nvisitedbest == 0){ nvisitedbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>Yes (* 10)</td>
		<td><%= visited %></td>
		<td><%= visited == 0 ? "N/A" : (visitedrank / visited) %></td>
		<td><%= visited == 0 ? "N/A" : (threeDigit.format(visitedscore / visited)) %></td>
		<td><%= visitedbest %></td>
	</tr>
	<tr>
		<td>No</td>
		<td><%= nvisited %></td>
		<td><%= nvisited == 0 ? "N/A" : (nvisitedrank / nvisited) %></td>
		<td><%= nvisited == 0 ? "N/A" : (threeDigit.format(nvisitedscore / nvisited)) %></td>
		<td><%= nvisitedbest %></td>
	</tr>
	<tr>
		<th>Keep Google rank into account</th>
		<th>Total</th>
		<th>Average rank</th>
		<th>Average score</th>
		<th>Best rank</th>
	</tr>
<%

	grank = 0; grankrank = 0; grankrankscore = 0.0; grankbest = 0;
	ngrank = 0; ngrankrank = 0; ngrankrankscore = 0.0; ngrankbest = 0;
	
	prog = 0;
	
	for (EvaluationResult res: teevanBetter){
		if (res.isLookatrank()){
			grank++; grankrank += prog + 1; grankrankscore += res.getAverage();
			if (grankbest == 0){ grankbest = prog + 1; }
		} else {
			ngrank++; ngrankrank += prog + 1; ngrankrankscore += res.getAverage();
			if (ngrankbest == 0){ ngrankbest = prog + 1; }
		}
		prog++;
	}

%>
	<tr>
		<td>Yes (* 10)</td>
		<td><%= grank %></td>
		<td><%= grank == 0 ? "N/A" : (grankrank / grank) %></td>
		<td><%= grank == 0 ? "N/A" : (threeDigit.format(grankrankscore / grank)) %></td>
		<td><%= grankbest %></td>
	</tr>
	<tr>
		<td>No</td>
		<td><%= ngrank %></td>
		<td><%= ngrank == 0 ? "N/A" : (ngrankrank / ngrank) %></td>
		<td><%= ngrank == 0 ? "N/A" : (threeDigit.format(ngrankrankscore / ngrank)) %></td>
		<td><%= ngrankbest %></td>
	</tr>
</table>

<br/>
Varying one parameter:
<br/><br/>
<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>

<%

	HashMap<String, EvaluationResult> map = new HashMap<String, EvaluationResult>();
	for (EvaluationResult res: results){
		map.put(res.getName().trim(), res);
	}

	double score1 = 0.0; double score2 = 0.0;
	int better1 = 0; int better2 = 0;
	int lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMethod().equals("TF")){
			String name = key.replaceAll("TF,","TFxIDF,");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>TF</td>
		<td>TFxIDF</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMethod().equals("TF")){
			String name = key.replaceAll("TF,","BM25,");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>TF</td>
		<td>BM25</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMethod().equals("TFxIDF")){
			String name = key.replaceAll("TFxIDF,","BM25,");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>TFxIDF</td>
		<td>BM25</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getFiltering().equals("n")){
			String name = key.replaceAll(", NoFilt",", GFilt");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>No Filtering</td>
		<td>Google NGram Filtering</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getFiltering().equals("n")){
			String name = key.replaceAll(", NoFilt",", WNFilt");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>No Filtering</td>
		<td>WordNet Filtering</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getFiltering().equals("w")){
			String name = key.replaceAll(", WNFilt",", GFilt");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			} 
		} 
	}

%>
	<tr>
		<td>WordNet Filtering</td>
		<td>Google NGram Filtering</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getText().equals("y")){
			String name = key.replaceAll(", Text","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			} 
		}
	}

%>
	<tr>
		<td>Plain Text</td>
		<td>No Plain Text</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getText().equals("r")){
			String name = key.replaceAll(", RText","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Relative Plain Text</td>
		<td>No Plain Text</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getTitle().equals("y")){
			String name = key.replaceAll(", Title","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			} else {
				System.out.println("Not found: " + name);
			}
		}
	}

%>
	<tr>
		<td>Title</td>
		<td>No Title</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getTitle().equals("r")){
			String name = key.replaceAll(", RTitle","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			} else {
				System.out.println("Not found: " + name);
			}
		}
	}

%>
	<tr>
		<td>Relative Title</td>
		<td>No Title</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMetadescription().equals("y")){
			String name = key.replaceAll(", MDescr","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Metadescription</td>
		<td>No Metadescription</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMetadescription().equals("r")){
			String name = key.replaceAll(", RMDescr","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Relative Metadescription</td>
		<td>No Metadescription</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMetakeywords().equals("y")){
			String name = key.replaceAll(", MKeyw","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Metakeywords</td>
		<td>No Metakeywords</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getMetakeywords().equals("r")){
			String name = key.replaceAll(", RMKeyw","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Relative Metakeywords</td>
		<td>No Metakeywords</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getTerms().equals("y")){
			String name = key.replaceAll(", Terms","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Terms</td>
		<td>No Terms</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getTerms().equals("r")){
			String name = key.replaceAll(", RTerms","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Relative Terms</td>
		<td>No Terms</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getCcparse().equals("y")){
			String name = key.replaceAll(", CCParse","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>CCParse</td>
		<td>No CCParse</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getCcparse().equals("r")){
			String name = key.replaceAll(", RCCParse","");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Relative CCParse</td>
		<td>No CCParse</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("Matching")){
			String name = key.replaceAll("- Matching","- UMatching");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Matching</td>
		<td>Unique Matching</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("Matching")){
			String name = key.replaceAll("- Matching","- LM");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Matching</td>
		<td>LM</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("Matching")){
			String name = key.replaceAll("- Matching","- PClick");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Matching</td>
		<td>PClick</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("UMatching")){
			String name = key.replaceAll("- UMatching","- LM");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Unique Matching</td>
		<td>LM</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("UMatching")){
			String name = key.replaceAll("- UMatching","- PClick");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Unique Matching</td>
		<td>PClick</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.getRerank().equals("LM")){
			String name = key.replaceAll("- LM","- PClick");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>LM</td>
		<td>PClick</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.isVisited()){
			String name = key.replaceAll(", Visited",", Not visited");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			}
		}
	}

%>
	<tr>
		<td>Extra Weight to Visited URLs</td>
		<td>No Extra Weight to Visited URLs</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
	<tr>
		<th>Parameter 1</th>
		<th>Parameter 2</th>
		<th>Average 1</th>
		<th>Better</th>
		<th>Average 2</th>
		<th>Better</th>
	</tr>
<%

	score1 = 0.0; score2 = 0.0;
	better1 = 0; better2 = 0;
	lookat = 0;
	
	for (String key: map.keySet()){ 
		EvaluationResult res = map.get(key);
		if (res != null && res.isLookatrank()){
			String name = key.replaceAll(", Look At Rank",", Not Look At Rank");
			EvaluationResult res2 = map.get(name);
			if (res2 != null){
				score1 += res.getAverage();
				score2 += res2.getAverage();
				if (res.getAverage() > res2.getAverage()){
					better1++;
				} else if (res.getAverage() < res2.getAverage()){
					better2++;
				}
				lookat++;
			} 
		}
	}

%>
	<tr>
		<td>Keep rank into account</td>
		<td>No Keep rank into account</td>
		<td><%= threeDigit.format(score1 / lookat) %></td>
		<td><%= better1 %></td>
		<td><%= threeDigit.format(score2 / lookat) %></td>
		<td><%= better2 %></td>
	</tr>
</table>

<br/>
Input combinations and their average scores:
<br/><br/>

<table border="1" cellspacing="0" cellpadding="5px">
	<tr>
		<th>Combination</th>
		<th>Average Score</th>
	</tr>
<%

	HashMap<String, Double> values = new HashMap<String, Double>();
	HashMap<String, ArrayList<EvaluationResult>> re = new HashMap<String, ArrayList<EvaluationResult>>();
	
	for (EvaluationResult res: results){
		String val = res.getTitle() + res.getMetadescription() + res.getMetakeywords() + res.getText() + res.getTerms() + res.getCcparse();
		if (re.containsKey(val)){
			re.get(val).add(res);
		} else {
			ArrayList<EvaluationResult> arl = new ArrayList<EvaluationResult>();
			arl.add(res);
			re.put(val, arl);
		}
	}
	
	String[] posvalues = new String[]{"y","n"};

	// Title
	for (String s1: posvalues){
		// MDescr
		for (String s2: posvalues){
			// MKeyw
			for (String s3: posvalues){
				// Text
				for (String s4: posvalues){
					// Terms
					for (String s5: posvalues){
						// CCParse
						for (String s6: posvalues){
							if (!(s1.equals("n") && s2.equals("n") && s3.equals("n") && s4.equals("n") &&
									s5.equals("n") && s6.equals("n"))){
								String val = s1 + s2 + s3 + s4 + s5 + s6;
								ArrayList<EvaluationResult> arl = re.get(val);
								double score = 0.0;
								String clean = "";
								for (EvaluationResult res: arl){
									score += res.getAverage();
								}
								score = score / arl.size();
								val = "";
								if (s1.equals("y")){
									val += "Title ";
								}
								if (s2.equals("y")){
									val += "Metadescription ";
								}
								if (s3.equals("y")){
									val += "Metakeywords ";
								}
								if (s4.equals("y")){
									val += "Text ";
								}
								if (s5.equals("y")){
									val += "Terms ";
								}
								if (s6.equals("y")){
									val += "CCParse ";
								}
								values.put(val, score);
							}
						}
					}
				}
			}
		}
	}
	
	posvalues = new String[]{"r","n"};

	// Title
	for (String s1: posvalues){
		// MDescr
		for (String s2: posvalues){
			// MKeyw
			for (String s3: posvalues){
				// Text
				for (String s4: posvalues){
					// Terms
					for (String s5: posvalues){
						// CCParse
						for (String s6: posvalues){
							if (!(s1.equals("n") && s2.equals("n") && s3.equals("n") && s4.equals("n") &&
									s5.equals("n") && s6.equals("n"))){
								String val = s1 + s2 + s3 + s4 + s5 + s6;
								ArrayList<EvaluationResult> arl = re.get(val);
								double score = 0.0;
								String clean = "";
								for (EvaluationResult res: arl){
									score += res.getAverage();
								}
								score = score / arl.size();
								val = "";
								if (s1.equals("r")){
									val += "RTitle ";
								}
								if (s2.equals("r")){
									val += "RMetadescription ";
								}
								if (s3.equals("r")){
									val += "RMetakeywords ";
								}
								if (s4.equals("r")){
									val += "RText ";
								}
								if (s5.equals("r")){
									val += "RTerms ";
								}
								if (s6.equals("r")){
									val += "RCCParse ";
								}
								values.put(val, score);
							}
						}
					}
				}
			}
		}
	}
	
	int had = 0; int todo = values.size();
	while (had < todo){
		
		double highestest = 0.0;
		String val = "";
		
		for (String s: values.keySet()){
			if (values.get(s) > highestest){
				highestest = values.get(s);
				val = s;
			}
		}
		
%>
	<tr>
		<td><%= val %></td>
		<td><%= threeDigit.format(highestest) %></td>
	</tr>
<%
		
		values.remove(val);
		had++;

	}
	
%>
</table>
</body>
</html>