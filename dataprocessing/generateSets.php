<?php

	ini_set("memory_limit","256M");

	include 'simple_html_dom.php';
	include 'post_request.php';
	
	if (isset($_GET["uuid"]) && isset($_GET["id"])){
	
		header('Content-Type: application/xml');
	
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
			$html = str_get_html($output);
			
			// id node
			$plaintext = $doc->createElement("id");
			$value = $doc->createTextNode($row2["id"]);
	    	$value = $plaintext->appendChild($value);
			$plaintext = $root->appendChild($plaintext);
			
			// url node
			$plaintext = $doc->createElement("url");
			$value = $doc->createTextNode($row2["url"]);
	    	$value = $plaintext->appendChild($value);
			$plaintext = $root->appendChild($plaintext);
			
			// visitdate node
			$plaintext = $doc->createElement("visitdate");
			$value = $doc->createTextNode($row2["visitdate"]);
	    	$value = $plaintext->appendChild($value);
			$plaintext = $root->appendChild($plaintext);
			
			// duration node
			$plaintext = $doc->createElement("duration");
			$value = $doc->createTextNode($row2["duration"]);
	    	$value = $plaintext->appendChild($value);
			$plaintext = $root->appendChild($plaintext);
			
			// plain text node
			$plaintext = $doc->createElement("plaintext");
			$pagevalue     = $html->plaintext;
			$pagevalue     = str_replace("\\","",$pagevalue);
			$pagevalue     = str_replace("&amp;","&",$pagevalue);
			$pagevalue     = str_replace("&nbsp;","",$pagevalue);
			$pagevalue     = str_replace("&gt;"," ",$pagevalue);
			$pagevalue     = str_replace("&lt;"," ",$pagevalue);
			$pagevalue     = str_replace("/"," ",$pagevalue);
			$value = $doc->createTextNode($pagevalue);
	    	$value = $plaintext->appendChild($value);
			$plaintext = $root->appendChild($plaintext);
			
			//structured
			$structured = $doc->createElement("structured");
				
				//head
				$head = $doc->createElement("head");
					
					// title
					$title = $doc->createElement("title");
					if (count($html->find('title')) > 0){
						$value = $doc->createTextNode($html->find('title',0)->plaintext);
	    				$value = $title->appendChild($value);
					}
					$title = $head->appendChild($title);
					
					//metadescription
					$metadescription = $doc->createElement("metadescription");
					foreach($html->find('meta') as $element){
	       				if (strtolower($element->name) == "description"){
	       					$value = $doc->createTextNode($element->content);
	    					$value = $metadescription->appendChild($value);
	       				};
					}
					$metadescription = $head->appendChild($metadescription);
					
					//metakeywords
					$metakeywords = $doc->createElement("metakeywords");
					foreach($html->find('meta') as $element){
	       				if (strtolower($element->name) == "keywords"){
	       					$split = explode(",", $element->content);
							foreach($split as $skeyword){
								$keyword = $doc->createElement("keyword");
	       						$value = $doc->createTextNode($skeyword);
	    						$value = $keyword->appendChild($value);
								$keyword = $metakeywords->appendChild($keyword);
							}
	       				};
					}
					$metakeywords = $head->appendChild($metakeywords);
				
				$head = $structured->appendChild($head);
				//end head
				
				//images
				$images = $doc->createElement("images");
					
					//img
					foreach($html->find('img') as $element){
						$img = $doc->createElement("img");
	       				$img->setAttribute("alt",$element->alt);
	    				$img->setAttribute("title",$element->title);
						$img = $images->appendChild($img);
					}
				
				$images = $structured->appendChild($images);
				//end images
				
				//anchors
				$anchors = $doc->createElement("anchors");
					
					//a
					foreach($html->find('a') as $element){
						$a = $doc->createElement("a");
	    				$a->setAttribute("title",$element->title);
						$a->setAttribute("href",$element->href);
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $a->appendChild($value);
						$a = $anchors->appendChild($a);
					}
				
				$anchors = $structured->appendChild($anchors);
				//end anchors
				
				//headings
				$headings = $doc->createElement("headings");
					
					//heading 1
					foreach($html->find('h1') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h1");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
					
					//heading 2
					foreach($html->find('h2') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h2");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
					
					//heading 3
					foreach($html->find('h3') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h3");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
					
					//heading 4
					foreach($html->find('h4') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h4");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
					
					//heading 5
					foreach($html->find('h5') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h5");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
					
					//heading 6
					foreach($html->find('h6') as $element){
						$heading = $doc->createElement("heading");
	    				$heading->setAttribute("type","h6");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $heading->appendChild($value);
						$heading = $headings->appendChild($heading);
					}
				
				$headings = $structured->appendChild($headings);
				//end anchors
				
				//textual
				$textual = $doc->createElement("textual");
					
					//text p
					foreach($html->find('p') as $element){
						$text = $doc->createElement("text");
	    				$text->setAttribute("type","p");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $text->appendChild($value);
						$text = $textual->appendChild($text);
					}
					
					//text span
					foreach($html->find('span') as $element){
						$text = $doc->createElement("text");
	    				$text->setAttribute("type","span");
						$value = $doc->createTextNode($element->plaintext);
	    				$value = $text->appendChild($value);
						$text = $textual->appendChild($text);
					}
				
				$textual = $structured->appendChild($textual);
				//end textual
				
				//lists
				$lists = $doc->createElement("lists");
					
					//list ol
					foreach($html->find('ol') as $element){
						$list = $doc->createElement("list");
	    				$list->setAttribute("type","ol");
							foreach($element->find('li') as $element2){
								$item = $doc->createElement("item");
								$value = $doc->createTextNode($element2->plaintext);
	    						$value = $item->appendChild($value);
								$item = $list->appendChild($item);
							}
						$list = $lists->appendChild($list);
					}
					
					//list ul
					foreach($html->find('ul') as $element){
						$list = $doc->createElement("list");
	    				$list->setAttribute("type","ul");
							foreach($element->find('li') as $element2){
								$item = $doc->createElement("item");
								$value = $doc->createTextNode($element2->plaintext);
	    						$value = $item->appendChild($value);
								$item = $list->appendChild($item);
							}
						$list = $lists->appendChild($list);
					}
				
				$lists = $structured->appendChild($lists);
				//end lists
				
				//tables
				$tables = $doc->createElement("tables");
					
					//table
					foreach($html->find('table') as $element){
						$table = $doc->createElement("table");
	    				$table->setAttribute("summary",$element->summary);
							//caption
							foreach($element->find('caption') as $element2){
								$cell = $doc->createElement("cell");
								$cell->setAttribute("type","caption");
								$value = $doc->createTextNode($element2->plaintext);
	    						$value = $cell->appendChild($value);
								$cell = $table->appendChild($cell);
							}
							//th
							foreach($element->find('th') as $element2){
								$cell = $doc->createElement("cell");
								$cell->setAttribute("type","th");
								$value = $doc->createTextNode($element2->plaintext);
	    						$value = $cell->appendChild($value);
								$cell = $table->appendChild($cell);
							}
							//td
							foreach($element->find('td') as $element2){
								$cell = $doc->createElement("cell");
								$cell->setAttribute("type","td");
								$value = $doc->createTextNode($element2->plaintext);
	    						$value = $cell->appendChild($value);
								$cell = $table->appendChild($cell);
							}
						$table = $tables->appendChild($table);
					}
				
				$tables = $structured->appendChild($tables);
				//end tables
				
				//terms
				$pagevalue     = urlencode($pagevalue);
				$str = "context=".$pagevalue;
				
				 $ch=curl_init();
				 curl_setopt($ch,CURLOPT_URL,'http://alterego.caret.cam.ac.uk:8080/terms');
				 curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_0);
				 curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
				 curl_setopt($ch, CURLOPT_POST, 1);
				 curl_setopt($ch, CURLOPT_POSTFIELDS, $str);
				
				 $response = curl_exec($ch);
				 $json = json_decode($response);
				 curl_close($ch);
				
				$terms = $doc->createElement("terms");
				//foreach($html->find('meta') as $element){
	       		//	if (strtolower($element->name) == "keywords"){
	       		//		$split = explode(",", $element->content);
						foreach($json as $sterm){
							$term = $doc->createElement("term");
	       					$value = $doc->createTextNode($sterm);
	    					$value = $term->appendChild($value);
							$term = $terms->appendChild($term);
						}
	       		//	};
				//}
				$terms = $structured->appendChild($terms);
					
				//end terms
				
			$structured = $root->appendChild($structured);
			 
			$xml_string = substr($doc->saveXML(),21);
			echo $xml_string;
		
		}
		
	} else {
		
		echo "Please specify a user id";
		
	}
?>