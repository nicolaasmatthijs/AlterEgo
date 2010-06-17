<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Top part of the relevance judgements evaluation environment. This allows the user
 * to select their unique identifier and choose a query for which documents should be
 * evaluated
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>AlterEgo 0.2 :: Evaluation</title>
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
	
	<div style="padding: 10px 0px; height: 20px;" id="generateDiv">
			<table>
				<tr style="height:25px; vertical-align:top;">
					<td style="width:200px;">
						<select id="user" style="width: 150px;">
							<!-- Unique identifier of all users participating in the 
								 relevance judgements evaluation session  -->
							<option value="usr_3484406">usr_3484406</option>
							<option value="usr_7318689">usr_7318689</option>
							<option value="usr_597873">usr_597873</option>
							<option value="usr_6326553">usr_6326553</option>
							<option value="usr_6422669">usr_6422669</option>
							<option value="usr_2543662">usr_2543662</option>
						</select>
					</td>
					<td style="width: 400px">
						<!--  The query to evaluate -->
						<input type="text" size="50" id="searchquery"/>
					</td>
					<td style="width: 100px">
						<input type="button" value="Evaluate" id="doEvaluate"/>
					</td>
				</tr>
			</table>
		</div>
	
	<script language="JavaScript" type="text/javascript">

		$("#doEvaluate").bind("click", function(ev){
			var search = $("#searchquery").val();
			var userid = $("#user").val();
			// Open the relevance judgements page for the current query
			parent.output.location.href = "evaluate.jsp?query=" + encodeURI(search) + "&userid=" + userid;
		});
	
	</script>
	
</body>
</html>