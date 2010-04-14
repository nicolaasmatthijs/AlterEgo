<?php 

if (isset($_POST["uuid"]) && isset($_POST["duration"]) && isset($_POST["url"]) && isset($_POST["content"])){

	$uuid = $_POST["uuid"];
	$duration = $_POST["duration"];
	$url = $_POST["url"];
	$content = mysql_real_escape_string(gzcompress($_POST["content"], 9));

	$con = mysql_connect("127.0.0.1:8889","root","root");
	if (!$con) {
  		die('Could not connect: ' . mysql_error());
  	}

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