<?php
/*
##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Page that gives an overview of all of the pages in a particular
# user's browsing history

*/

	include 'config.php';
	
	if (isset($_GET["uuid"])){
		
		$uuid = $_GET["uuid"];
		
		$con = mysql_connect($dburl,$dbuser,$dbpass);
		if (!$con) {
  			die('Could not connect: ' . mysql_error());
  		}

		mysql_select_db("alterego", $con);

		$result = mysql_query("SELECT * FROM usr_" . $uuid);

		// Get page id, user id, date of visit, HTML content and page visit
		// duration and display on screen
		while($row = mysql_fetch_array($result)) {
  			echo $row['id'] . " " . $row['uuid'] . " " . $row['visitdate']  . " " . $row['url'] . " " . gzuncompress($row['content']) . " " . $row['duration'];
  			echo "<br />";
  		}

		mysql_close($con);

	} else {
		echo "Please specify a user";
	}

?>