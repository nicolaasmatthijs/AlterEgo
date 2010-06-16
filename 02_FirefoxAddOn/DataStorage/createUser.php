<?php 
/*
##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Script used to create a table for a new user. This table will then
# be used to save that user's browsing history.

*/

include 'config.php';

// Check whether a user id is provided
if (isset($_POST["uuid"])){

	$uuid = $_POST["uuid"];
	$con = mysql_connect($dburl,$dbuser,$dbpass);
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	// Create a new table for this user
	if (mysql_query("CREATE TABLE alterego.usr_" . $uuid . "(
			id INT NOT NULL AUTO_INCREMENT, 
			PRIMARY KEY(id),
 			uuid VARCHAR(128) NOT NULL, 
 			visitdate DATETIME NOT NULL,
			url TEXT,
			content TEXT,
			duration VARCHAR(128))",$con)) {
  		echo "User tabel for " . $uuid . " created";
	} else {
  		echo "Error creating table: " . mysql_error();
  	}

	mysql_close($con);

} else {
	echo "No user id has been set";
}

?>