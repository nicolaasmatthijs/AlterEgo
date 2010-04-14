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
		<title>AlterEgo 0.1 Summary</title>
	</head>
	<style>
		th	{ width: 200px; }
		td	{ text-align: center; }
	</style>
	<body>

		<table border="1">
			<tr>
				<th>User No</th>
				<th>Page Visits</th>
				<th>Google Searches</th>
				<th>Bing Searches</th>
				<th>Wikipedia Pages</th>
			</tr>

<?php

	$i = 1;
	while($row = mysql_fetch_array($result)) {
		echo "<tr><td>";
		echo "User " . $i;
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%www.google%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%ww.bing%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%wikipedia%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		echo "</td></tr>";
		flush();
		$i++;
	}

	mysql_close($con);

?>

	</table>

</body>
</html>