<?php

	//ini_set("memory_limit","256M");

	include 'simple_html_dom.php';
	
	if (isset($_GET["uuid"]) && isset($_GET["id"])){
	
		header('Content-Type: text/plain');
	
		// connect to database
		$con = mysql_connect("127.0.0.1:3306","root","");
		if (!$con) {
	  		die('Could not connect: ' . mysql_error());
	  	}
	
		mysql_select_db("alterego", $con);
	
		$result2 = mysql_query("SELECT * FROM " . $_GET["uuid"] . " WHERE url NOT LIKE 'https%' AND url NOT LIKE '%localhost%' AND url NOT LIKE '%facebook%' AND url NOT LIKE '%127.0.0.1%' AND id=" . $_GET["id"]);
		while($row2 = mysql_fetch_array($result2)) {
			
			// create a new XML document
			$doc = new DomDocument('1.0');

			// add document node
			$root = $doc->createElement('document');
			$root = $doc->appendChild($root);
		
			$output = str_replace("\\\"","\"", @gzuncompress($row2["content"]));
			
			echo $output;
					
		}
		
	} else {
		
		echo "Please specify a user id";
		
	}
?>