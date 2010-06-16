<?php
/*
##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Script used to generate the AlterEgo database

*/

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