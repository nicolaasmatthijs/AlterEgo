<?php
/*

##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Print a list of all users that have the Firefox add-on currently
# installed

*/
		
	include 'config.php';

	$con = mysql_connect($dburl,$dbuser,$dbpass);
	if (!$con) {
 		die('Could not connect: ' . mysql_error());
  	}

	mysql_select_db("alterego", $con);

	$result = mysql_query("SHOW TABLES");

	while($row = mysql_fetch_array($result)) {
		if (substr($row[0],0,3) == "usr"){
  			echo $row[0] . ";";
		}
  	}

	mysql_close($con);

?>