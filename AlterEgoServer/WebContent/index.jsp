<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>AlterEgo 0.2</title>
<style>
	html, body, table, tr, td {
		font-size: 10px;
		font-family: "Verdana";
	}
	.inputCell {
		width: 20px;
	}
	.weight {
		width: 100px;
	}
</style>
<script type="text/javascript" language="JavaScript" src="js/jquery.js"></script>
</head>
<body>
	
	<div style="float:left; width:50%; border-right: 1px solid #aaa; padding: 10px 0px; height: 170px;" id="generateDiv">
		<div style="float: left; width: 33%">
			<table style="width:100%">
				<tr style="height:25px; vertical-align:top;">
					<td colspan="3">
						<select id="user" style="width: 150px;">
							<optgroup label="Development Set">
								<option value="usr_3484406">usr_3484406</option>
							</optgroup>
							<optgroup label="Training Set">
								<option value="usr_2434413">usr_2434413</option>
								<option value="usr_2543662">usr_2543662</option>
								<option value="usr_2554801">usr_2554801</option>
								<option value="usr_261821">usr_261821</option>
								<option value="usr_2917559">usr_2917559</option>
								<option value="usr_406338">usr_406338</option>
								<option value="usr_4362753">usr_4362753</option>
								<option value="usr_4965278">usr_4965278</option>
								<option value="usr_5945189">usr_5945189</option>
								<option value="usr_6145747">usr_6145747</option>
								<option value="usr_6318797">usr_6318797</option>
								<option value="usr_6422669">usr_6422669</option>
								<option value="usr_6989158">usr_6989158</option>
								<option value="usr_7318689">usr_7318689</option>
								<option value="usr_7600860">usr_7600860</option>
								<option value="usr_8002037">usr_8002037</option>
								<option value="usr_9533453">usr_9533453</option>
							</optgroup>
							<optgroup label="Test Set">
								<option value="usr_2296654">usr_2296654</option>
								<option value="usr_2555137">usr_2555137</option>
								<option value="usr_263177">usr_263177</option>
								<option value="usr_3762438">usr_3762438</option>
								<option value="usr_4130066">usr_4130066</option>
								<option value="usr_4593969">usr_4593969</option>
								<option value="usr_5117040">usr_5117040</option>
								<option value="usr_5136203">usr_5136203</option>
								<option value="usr_597873">usr_597873</option>
								<option value="usr_6150712">usr_6150712</option>
								<option value="usr_6326553">usr_6326553</option>
								<option value="usr_6460086">usr_6460086</option>
								<option value="usr_6921645">usr_6921645</option>
								<option value="usr_7865799">usr_7865799</option>
								<option value="usr_8613294">usr_8613294</option>
								<option value="usr_9918599">usr_9918599</option>
							</optgroup>
						</select>
					</td>
				</tr>
				<tr>
					<td colspan="2" style="height:30px;">
						<select id="weightingScheme" style="width: 150px;">
							<optgroup label="Weighting Scheme">
								<option value="tf">TF</option>
								<option value="tfidf">TFxIDF</option>
								<option value="bm25">BM25</option>
							</optgroup>
						</select>
					</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_split" name="extra" value="Split"/>
					</td>
					<td>Split multiword terms</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_duplicate" name="extra" value="Duplicate"/>
					</td>
					<td>Exclude duplicate pages</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_takeLog" name="extra" value="takeLog"/>
					</td>
					<td>Take log</td>
				</tr>
				<tr>
					<td colspan="2">
						<input type="button" value="Generate Profile" id="generate_profile"/>
					</td>	
				</tr>
			</table>
		</div>
		<div style="float: left; width: 33%">
			<table>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_title" name="useData" value="Title"/>
					</td>
					<td>Title</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_title"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_metadescription" name="useData" value="Metadescription"/>
					</td>
					<td>Meta Description</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_metadescription"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_metakeywords" name="useData" value="Metakeywords"/>
					</td>
					<td>Meta Keywords</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_metakeywords"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_plaintext" name="useData" value="Plaintext"/>
					</td>
					<td>Plain text</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_plaintext"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_terms" name="useData" value="Terms"/>
					</td>
					<td>Terms</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_terms"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_ccparse" name="useData" value="CCParse"/>
					</td>
					<td>C&amp;C Parsed Text</td>
					<td class="weight"><input type="text" size="3" value="1" id="nmbr_ccparse"/></td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="chk_useRelativeW" name="useData" value="useRelativeW"/>
					</td>
					<td colspan="2">Use relative weighting</td>
				</tr>
			</table>
		</div>
		<div style="float: left; width: 33%">
			<table>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_all" name="pos" value="allPOS"/>
					</td>
					<td>All POS</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_noun" name="pos" value="POSNoun"/>
					</td>
					<td>Nouns</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_verb" name="pos" value="POSVerb"/>
					</td>
					<td>Verbs</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_adj" name="pos" value="POSAdj"/>
					</td>
					<td>Adjectives</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_adv" name="pos" value="POSAdv"/>
					</td>
					<td>Adverbs</td>
				</tr>
				<tr>
					<td class="inputCell">
						<input type="checkbox" id="pos_googlengram" name="pos" value="googleNGram"/>
					</td>
					<td>
						Google N-Gram
						<input type="text" size="4" value="100" id="nmbr_googlengram"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div style="float:right; width:46%; padding: 10px; height: 155px;" id="searchDiv">
		<table>
			<tr style="height:30px; vertical-align:top;">
				<td colspan="3">
					<input type="button" value="Google Search" id="search"/>
					<input type="text" size="50" id="searchquery"/>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<select id="rerankmethod">
						<option value="matching">Matching</option>
						<option value="umatching">Unique Matching</option>
						<option value="lm">Language Model</option>
						<option value="pclick">P-Click</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="inputCell"><input type="checkbox" id="showAll"/></td>
				<td class="colspan=2">Show all results</td>
			</tr>
			<tr>
				<td class="inputCell"><input type="checkbox" id="lookAtRank"/></td>
				<td colspan="2">Keep Google Rank into account</td>
			</tr>
			<tr>
				<td class="inputCell"><input type="checkbox" id="interleave"/></td>
				<td>Interleave results</td>
				<td>
					<select id="interleavingMethod">
						<option value="balanced">Balanced Interleaving</option>
						<option value="teamdraft">Team-Draft Interleaving</option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="inputCell"><input type="checkbox" id="chk_visited"/></td>
				<td style="width:160px">Extra weight to visited URLs</td>
				<td><input type="text" size="3" value="2" id="nmbr_visited"/></td>
			</tr>
		</table>
		
	</div>
	
	<script language="JavaScript" type="text/javascript">

		$("#pos_googlengram").bind("change", function(ev){
			if ($('#pos_googlengram').is(':checked')) {
		        $("#pos_all").attr('disabled', true);
				$("#pos_noun").attr('disabled', true);
				$("#pos_verb").attr('disabled', true);
				$("#pos_adj").attr('disabled', true);
				$("#pos_adv").attr('disabled', true);
		    } else {
		    	$("#pos_all").removeAttr('disabled');
		    	if (!$('#pos_all').is(':checked')) {
		       		$("#pos_noun").removeAttr('disabled');
					$("#pos_verb").removeAttr('disabled');
					$("#pos_adj").removeAttr('disabled');
					$("#pos_adv").removeAttr('disabled');
		    	}
		    }   
		});

		$("#pos_all").bind("change", function(ev){
			if ($('#pos_all').is(':checked')) {
		        $("#pos_noun").attr('disabled', true);
				$("#pos_verb").attr('disabled', true);
				$("#pos_adj").attr('disabled', true);
				$("#pos_adv").attr('disabled', true);
		    } else {
		        $("#pos_noun").removeAttr('disabled');
				$("#pos_verb").removeAttr('disabled');
				$("#pos_adj").removeAttr('disabled');
				$("#pos_adv").removeAttr('disabled');
		    }   
		});
	
		$("#generate_profile").bind("click", function(ev){

			var obj = {};
			
			obj["user"] = $("#user").val();

			obj["weighting"] = $("#weightingScheme").val();
			obj["split"] = $("#chk_split").attr("checked");
			obj["excludeDuplicate"] = $("#chk_duplicate").attr("checked");

			obj["posAll"] = $("#pos_all").attr("checked");
			obj["posNoun"] = $("#pos_noun").attr("checked");
			obj["posVerb"] = $("#pos_verb").attr("checked");
			obj["posAdjective"] = $("#pos_adj").attr("checked");
			obj["posAdverb"] = $("#pos_adv").attr("checked");
			
			obj["title"] = $("#chk_title").attr("checked");
			obj["metadescription"] = $("#chk_metadescription").attr("checked");
			obj["metakeywords"] = $("#chk_metakeywords").attr("checked");
			obj["plaintext"] = $("#chk_plaintext").attr("checked");
			obj["terms"] = $("#chk_terms").attr("checked");
			obj["ccparse"] = $("#chk_ccparse").attr("checked");

			obj["titleW"] = $("#nmbr_title").val();
			obj["metadescriptionW"] = $("#nmbr_metadescription").val();
			obj["metakeywordsW"] = $("#nmbr_metakeywords").val();
			obj["plaintextW"] = $("#nmbr_plaintext").val();
			obj["termsW"] = $("#nmbr_terms").val();
			obj["ccparseW"] = $("#nmbr_ccparse").val();

			obj["takeLog"] = $("#chk_takeLog").attr("checked");

			obj["googlengram"] = $("#pos_googlengram").attr("checked");
			obj["googlengramW"] = $("#nmbr_googlengram").val();
			obj["useRelativeW"] = $("#chk_useRelativeW").attr("checked");

			var querystring = "";
			for (var key in obj){
				querystring += "&" + key + "=" + obj[key];
			}

			parent.output.location.href = "about:blank";
			parent.output.location.href = "showProfile.jsp?" + querystring;
			parent.output2.location.href = "about:blank";
			
		});

		$("#search").bind("click", function(ev){

			var user = $("#user").val();
			var query = $("#searchquery").val();
			var method = $("#rerankmethod").val();

			var interleave = $("#interleave").attr("checked");
			var interleaveMethod = $("#interleavingMethod").val();
			var showall = $("#showAll").attr("checked");
			var lookatrank = $("#lookAtRank").attr("checked");

			var visited = $("#chk_visited").attr("checked");
			var visitedW = $("#nmbr_visited").val();

			var querystring = "?interleaveMethod=" + interleaveMethod + "&visitedW=" + visitedW + "&visited=" + visited + "&lookatrank=" + lookatrank + "&showall=" + showall + "&interleave=" + interleave + "&user=" + user + "&query=" + escape(query) + "&method=" + method + "&page=1";

			parent.output.location.href = "about:blank";
			parent.output2.location.href = "about:blank";
			
			parent.output.location.href = "showSearch.jsp" + querystring + "&rerank=false";
			parent.output2.location.href = "showSearch.jsp" + querystring + "&rerank=true";
			
		});
	
	</script>
	
</body>
</html>