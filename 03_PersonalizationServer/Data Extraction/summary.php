<?php
/*

##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# File that will give an overview of the number of users participating
# and summarize each of their browsing histories

*/

	include 'config.php';

	$con = mysql_connect($dburl,$dbuser,$dbpass);
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	mysql_select_db("alterego", $con);

	// List of all participating users
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
				<th>Yahoo Searches</th>
				<th>Unique pages</th>
			</tr>

<?php

	$i = 1;
	// Print details for each user
	while($row = mysql_fetch_array($result)) {
		// Number of page visits
		echo "<tr><td>";
		echo "User " . $i;
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		// Number of Google searches
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%www.google%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		// Number of Bing searches
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%ww.bing%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		} 
		// Number of Wikipedia pages
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%wikipedia%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		}
		// Number of Yahoo searches
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url LIKE '%search.yahoo.%'");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2['COUNT(id)'];
		}  
		$varia = 0;
		// Number of unique page visits
		$result2 = mysql_query("SELECT COUNT(id) FROM " . $row['Tables_in_alterego'] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' GROUP BY url");
		echo "</td><td>";
		while($row2 = mysql_fetch_array($result2)) {
			$varia += 1;
		}  
		echo $varia;
		echo "</td></tr>";
		flush();
		$i++;
	}

	mysql_close($con);

?>

	</table>

</body>
</html>