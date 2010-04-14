<?php

	$con = mysql_connect("127.0.0.1:3306","root","");
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	mysql_select_db("alterego", $con);

	$result = mysql_query("SHOW TABLES");
	
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 TRANSITIONAL//EN">
<html>

	<head>
		<title>AlterEgo 0.1 Training & Test set</title>
	</head>
	<style>
		th	{ width: 200px; }
		td	{ text-align: center; }
		.type { width: 800px; }
	</style>
	<body>

		<table border="1">
			<tr>
				<th colspan="4" class="type">Training Set</th>
				<th colspan="4" class="type">Test Set</th>
			</tr>
			<tr>
				<th>User Id</th>
				<th>Unique Visits</th>
				<th>Google Searches</th>
				<th>Wikipedia Pages</th>
				<th>User Id</th>
				<th>Unique Visits</th>
				<th>Google Searches</th>
				<th>Wikipedia Pages</th>
			</tr>

<?php

	$arr = array();
	$sarr = array();
	$continue = 0;

	while($row = mysql_fetch_array($result)) {
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%'");
		while($row2 = mysql_fetch_array($result2)) {
			if ($row2['COUNT(id)'] > 500){
				$continue = 1;
				$sarr[count($sarr)] = $row['Tables_in_alterego'];
				$arr[$row['Tables_in_alterego']] = array("visits" => $row2['COUNT(id)']);
			} else {
				$continue = 0;
			}
		} 
		if ($continue == 1){
			$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%www.google%'");
			while($row2 = mysql_fetch_array($result2)) {
				$arr[$row['Tables_in_alterego']]["google"] = $row2['COUNT(id)'];
			} 
			$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%wikipedia%'");
			while($row2 = mysql_fetch_array($result2)) {
				$arr[$row['Tables_in_alterego']]["wikipedia"] = $row2['COUNT(id)'];
			} 
		}
	}

	$arrtraining = array("usr_2434413", "usr_2543662", "usr_2554801", "usr_261821", "usr_2917559", "usr_406338", "usr_4362753", "usr_4965278", "usr_5945189", "usr_6145747", "usr_6318797", "usr_6422669", "usr_6989158", "usr_7318689", "usr_7600860", "usr_8002037", "usr_9533453");
	$arrtest = array("usr_2296654", "usr_2555137", "usr_263177", "usr_3762438", "usr_4130066", "usr_4593969", "usr_5117040", "usr_5136203", "usr_597873", "usr_6150712", "usr_6326553", "usr_6460086", "usr_6921645", "usr_7865799", "usr_8613294", "usr_9918599");

	//$arrtest = array();
	//$arrtraining = array();
	//
	//$training_keys = array_rand($arr, ceil(count($arr) / 2));
	//print_r($training_keys);
	//for ($i = 0; $i < count($arr); $i++){
	//	$isintraining = 0;
	//	for ($t = 0; $t < count($training_keys); $t++){
	//		if ($sarr[$i] == $training_keys[$t]){
	//			$isintraining = 1;
	//		}
	//	}
	//	if ($isintraining == 1){
	//		$arrtraining[count($arrtraining)] = $sarr[$i];
	//	} else {
	//		$arrtest[count($arrtest)] = $sarr[$i];
	//	}
	//}

	for ($i = 0; $i < count($arrtraining); $i++){
		// Training set
		echo "<tr><td>";
		echo $arrtraining[$i];
		echo "</td><td>";
		echo $arr[$arrtraining[$i]]["visits"];
		echo "</td><td>";
		echo $arr[$arrtraining[$i]]["google"];
		echo "</td><td>";
		echo $arr[$arrtraining[$i]]["wikipedia"];
		// Test set
		if ($i >= count($arrtest)){
			echo "</td><td></td><td></td><td></td></tr>";
		} else {
			echo "</td><td>";
			echo $arrtest[$i];
			echo "</td><td>";
			echo $arr[$arrtest[$i]]["visits"];
			echo "</td><td>";
			echo $arr[$arrtest[$i]]["google"];
			echo "</td><td>";
			echo $arr[$arrtest[$i]]["wikipedia"];
			echo "</td></tr>";
		}
	}
	echo "</table>";

	mysql_close($con);

?>

	</table>

</body>
</html>