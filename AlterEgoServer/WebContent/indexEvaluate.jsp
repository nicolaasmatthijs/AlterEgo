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
					<td style="width: 400px">
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
			parent.output.location.href = "evaluate.jsp?query=" + encodeURI(search) + "&userid=" + userid;
		});
	
	</script>
	
</body>
</html>