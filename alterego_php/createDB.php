<?php

	$con = mysql_connect("127.0.0.1:8889","root","root");
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	if (mysql_query("CREATE DATABASE alterego
					COLLATE utf8_unicode_ci",$con)) {
  		echo "Database created";
	} else {
  		echo "Error creating database: " . mysql_error();
  	}

	mysql_close($con);

?>