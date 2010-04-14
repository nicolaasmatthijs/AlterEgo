<?php

	if (isset($_GET["uuid"])){
		
		$uuid = $_GET["uuid"];
		
		$con = mysql_connect("127.0.0.1:8889","root","root");
		if (!$con) {
  			die('Could not connect: ' . mysql_error());
  		}

		mysql_select_db("alterego", $con);

		$result = mysql_query("SELECT * FROM usr_" . $uuid);

		while($row = mysql_fetch_array($result)) {
  			echo $row['id'] . " " . $row['uuid'] . " " . $row['visitdate']  . " " . $row['url'] . " " . gzuncompress($row['content']) . " " . $row['duration'];
  			echo "<br />";
  		}

		mysql_close($con);

	} else {
		echo "Please specify a user";
	}

?>