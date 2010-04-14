<?php

	$con = mysql_connect("127.0.0.1:3306","****","****");
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	if (mysql_query("CREATE DATABASE alterego
					COLLATE utf8_unicode_ci",$con)) {
  		echo "Database created";

		if (mysql_query("CREATE TABLE alterego.users (
				id INT NOT NULL AUTO_INCREMENT, 
				PRIMARY KEY(id),
 				uuid VARCHAR(128) NOT NULL, 
 				visitdate DATETIME NOT NULL,
				url TEXT,
				content TEXT,
				duration VARCHAR(128))",$con)) {
  			echo "User table created";
		} else {
  			echo "Error creating user table: " . mysql_error();
  		}

	} else {
  		echo "Error creating database: " . mysql_error();
  	}

	mysql_close($con);

?>