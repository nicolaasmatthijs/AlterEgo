<?php 

include 'config.php';

// Check whether all required parameters are set
//   uuid = user's unique identifier
//   duration = time spent on the current web page in ms
//   url = URL of the current page
//   content = length of the source HTML
if (isset($_POST["uuid"]) && isset($_POST["duration"]) && isset($_POST["url"]) && isset($_POST["content"])){

	$uuid = $_POST["uuid"];
	$duration = $_POST["duration"];
	$url = $_POST["url"];
	$content = mysql_real_escape_string(gzcompress($_POST["content"], 9));

	$con = mysql_connect($dburl,$dbuser,$dbpass);
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

	// Create a record in the user's table
	if (mysql_query("INSERT INTO alterego.usr_" . $uuid . " (uuid, visitdate, url, content, duration) 
					 VALUES ('$uuid',NOW(),'$url','$content','$duration')", $con)) {
  		echo "User entry inserted";
	} else {
  		echo "Error inserting entry: " . mysql_error();
  	}

	mysql_close($con);

} else {
	echo "Not all of the required parameters have been set";
}

?>