<?php

	include 'config.php';

	$con = mysql_connect($dburl,$dbuser,$dbpass);
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	// Create the general AlterEgo database
	if (mysql_query("CREATE DATABASE alterego
					COLLATE utf8_unicode_ci",$con)) {
  		echo "Database created";
	} else {
  		echo "Error creating database: " . mysql_error();
  	}

	mysql_close($con);

?>