<?php
/*
##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Script that will get a list of web pages that have been visited since the
# last time the browsing history was processed

*/

	ini_set("memory_limit","256M");

	include 'config.php';
	include 'simple_html_dom.php';
	
	if (isset($_GET["uuid"]) && isset($_GET["id"])){
	
		header('Content-Type: text/plain');
	
		// connect to database
		$con = mysql_connect($dburl,$dbuser,$dbpass);
		if (!$con) {
	  		die('Could not connect: ' . mysql_error());
	  	}
	
		mysql_select_db("alterego", $con);
	
		// get list of page ids greater than last processed page id
		$result2 = mysql_query("SELECT id FROM " . $_GET["uuid"] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url NOT LIKE '%127.0.0.1%' AND id > " . $_GET["id"]);
		while($row2 = mysql_fetch_array($result2)) {
			echo $row2["id"] . "\n";
		}
		
	} else {
		
		echo "Please specify a user id";
		
	}
?>