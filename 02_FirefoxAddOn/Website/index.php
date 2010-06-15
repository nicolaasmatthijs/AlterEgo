<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" dir="ltr" class="html-ltr firefox">
<head>
    <title>AlterEgo 0.2 :: Add-on for Firefox</title>

    <link rel="shortcut icon" type="image/x-icon" href="img/alterego_icon_small.png" />
	<link rel="stylesheet" type="text/css" href="css/style.min.css" media="all" />

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="keywords" content="add-ons,addons,Nicolaas,Matthijs,CSTIT,Nicolaas Matthijs,Cambridge,Language Processing,Google,keyword extraction"/>
    <meta name="description" content="Add-on that will attempt to learn your personal interests by observing your browsing behavior. It will use your personal interests to re-rank the output of search engines."/>
    
</head>
<body class="html-ltr firefox user-anon inverse">
        
    <div class="section">
        <div id="header" role="banner">
        	<p id="title">
				<a href="#">Alter Ego 0.2 <em>for</em> <img alt="Firefox" src="img/firefox.png" /> <strong>Firefox</strong></a>
			</p>
		</div>
		<div class="stand-alone-options">
  			<div id="categories" class="categories"></div>
		</div>

		<h4 class="author">by <a href="mailto:nm417@cam.ac.uk" class="profileLink">Nicolaas Matthijs (CSTIT)</a></h4>

		<div id="addon" class="primary" role="main">
  			<div class="featured">
    			<div class="featured-inner object-lead inverse">
    				<div id="addon-summary-wrapper">
        				<div id="addon-summary" class="primary ">
            				<p >
              					Add-on that will attempt to learn your personal interests by observing your browsing behavior. It will use your personal interests to re-rank the output of search engines and thus try to improve your overall browsing experience.	
							</p>
							<div id="addon-install">
                				<div id="install-25448-3f58e41f" class="install install-container">
									<p class="install-button platform-ALL">
            							<a href="alterego.xpi"  id="installTrigger12399-3f58e41f" addonName="Alter Ego 0.2" addonIcon="/img/alterego_icon.png" addonHash="sha256:b0c78d4efc8996b34d06fef30b1ba7fa25c049299c3f553fe85799e900672c8b" jsInstallMethod="browser_app_addon_install" frozen="false" class="button positive significant"><img src="img/plus-green-16x16.gif" alt="" /><span class="install-button-text">Download Now </span></a>
									</p>
									<div class="nonfirefox jqmWindow rounded"></div>
								</div>            
							</div>
                        
            				<table summary="Add-on Information">
              					<tbody>
                                    <tr>
                      					<th>Version</th>
                      					<td>0.2</td>
                    				</tr>
                    				<tr>
                      					<th>Works with</th>
                      					<td>Firefox: 3.0 &ndash; 3.7.*</td>
                    				</tr>
                   	 				<tr>
                      					<th>Updated</th>
                                        <td><span title="February  20, 2010">May  15, 2010</span></td>
                    				</tr>
                                	<tr>
                  						<th>Developer</th>
                 	 					<td><a href="mailto:nm417@cam.ac.uk" class="profileLink">Nicolaas Matthijs (CSTIT)</a></td>
                					</tr>
                                  	<tr>
                    					<th>Homepage</th>
                    					<td><strong><a href="http://alterego.caret.cam.ac.uk/">http://alterego.caret.cam.ac.uk/</a></strong></td>
                  					</tr>
                              </tbody>
            			</table>
          			</div> <!-- addon-summary -->
    			</div> <!-- addon-summary-wrapper -->
				<div class="secondary">
                	<a class="screenshot thumbnail" rel="lightbox" href="#">
						<img alt="" src="img/alterego_logo.png" />
            		</a>
				</div> <!-- secondary -->
    		</div> <!-- featured-inner -->
  		</div> <!-- featured -->

  		<h3>More about this add-on</h3>
  		<div class="article prose userinput">
			<h4>Update: new version of AlterEgo! (May 15, 2010)</h4>
			<p style="margin-bottom:0.4em;">
     			AlterEgo will now try to make the results coming out of Google more relevant to you. This is done by observing your browsing history and trying to learn what your personal interests are. By using this, you will also help me evaluating whether the AlterEgo personalized search strategy works. So please install the latest version of this FireFox add-on, as you'll be providing my research project a huge favor!<br />          
			</p>
			<br/>
			<h4>Can you tell me more about the project?</h4>
			<p style="margin-bottom:0.4em;">
     			Different people look for different things with the same search queries. Classic examples include queries like jaguar, Columbia, flash, mercury. This makes it harder for search engines to come up with results that are relevant to a user’s current information need. My research project will involve creating a <b>personalized search system</b>. The first part of the project involves generating an interest profile for the user. This will in the first instance be based on the browser history and the search queries of that user. In order to capture all of this data, this add-on has been written to provide me with training and testing data. It is very important that you install this add-on, as I need as much data as possible. An initial test of the approach will involve using this user profile to predict which Wikipedia disambiguation is the most interesting to a user. Following this, we plan to use the user profile to re-rank general search engine results.<br />          
			</p>
			<br/>
			<h4>What will this add-on do?</h4>
			<p style="margin-bottom:0.4em;">
     			This add-on will record the URLs you visit, and store them along with an anonymous identifier in my database. Secure urls starting with https:// or http:// urls that require authentication will not be recorded. Your IP address and identity will not be recorded. The add-on will then try to learn your interests from looking at these previously visited pages. When you do a search on Google, the AlterEgo add-on will take the first 50 results and will try to change the order to make them more relevant and interesting to you.<br />  
			</p>
			<br/>
			<h4>How about my privacy?</h4>
			<p style="margin-bottom:0.4em;">
     			When you install the add-on, it will generate an anonymous identifier for your browser instance. After that, the add-on will always use this identifier to communicate with the server. So I have no idea who you are. Secure urls starting with https:// (banking, e-mail, ...) are not being captured. If the URL is a normal http:// one, it will send the URL and an MD5 hash (of the HTML) to the server. The server will then do a request to the same URL and will only save the data in the database if the MD5 hash from the HTML the server gets back is exactly the same as the MD5 hash it received from the add-on. Hence, the add-on will only capture data from websites that are publicly visible. The URLs and anonymous identifiers collected will never be released and will only be potentially seen by myself and my 2 supervisors. The source code for <a href="alterego-firefox.zip">the AlterEgo Add-On</a> and <a href="alterego-php.zip">the PHP back-end</a> has been Open Sourced.<br />          
			</p>
			<br/>
			<h4>Why should I install it?</h4>
			<p style="margin-bottom:0.4em;">
     			It'll be incredibly useful for the project. Because there are no publicly available corpora or datasets for this, I need to collect my own data and I need as much as possible. You're also helping out science and the field of Natural Language Processing and Information Retrieval. Last but not least, at the end of the project 1 of the participants will be awarded with a price worth £50. However, because I want the prize to be one that people really want, I'm hoping that <a href="mailto:nm417@cam.ac.uk">you will tell me</a> what you think a good prize would be. <br />          
			</p>
			<br/>
			<h4>What happens with my data after the project?</h4>
			<p style="margin-bottom:0.4em;">
     			This data will be deleted at the conclusion of this project, or sooner if you request it (just send me an email; but don't forget to include your anonymous identifier which can be found by clicking the Tools > About AlterEgo menu).<br />          
			</p>
			<br/>
			<h4>What's CSTIT?</h4>
			<p style="margin-bottom:0.8em;">
     			CSTIT is an MPhil in <a href="http://www.cl.cam.ac.uk/admissions/cstit/structure.html">Computer Speech, Text and Internet Technology</a> at the University of Cambridge. It's a one-year Masters course on the state-of-the-art in Speech and Language Processing and its application to Internet Technology. The main aim of the MPhil course is to teach the fundamental theory of speech and natural language processing and its use in a variety of advanced applications, especially those related to the Internet.<br/>
</p>
<ul style="margin-bottom:1em;"><li>Speech Processing: analysis, speech recognition, speech synthesis</li><li>
Language Processing (computational linguistics): syntax, parsing, semantics, discourse</li><li>
Applications: information retrieval, information extraction, dialogue systems, machine translation, question answering.</li></ul>
			<p style="margin-bottom:0.4em;">
The CSTIT is a one-year postgraduate course, which combines lectures, practicals, seminars and a substantial research project. It starts off with a term of taught material (lectures and structured practicals) covering the foundations of speech and language processing. In the second term, students attend lectures on more advanced topics, participate in a small group seminar in which they study and present material on a research topic, undertake two longer practicals and start on their research project. A dissertation is submitted in the third week in June and students give presentations on their projects in the last week in June. This website and the offered Firefox add-on are a part of my research project in Personalized Search.<br />          
			</p><br/>
			<h4>I would like to thank ...</h4>
			<p style="margin-bottom:0em;">
     			First of all I would like to thank <b>Dr. Filip Radlinski</b> for having volunteered to supervise my CSTIT research project. I would also like to thank <b>Dr. Stephen Clark</b> (University of Cambridge) for being my internal supervisor and keeping me on track. Special thanks also goes to the <a href="http://www.caret.cam.ac.uk">Center for Applied Research in Education Technologies</a> at the University of Cambridge for providing me with a server for hosting this website and capturing the necessary data.<br />          
			</p>
   		</div> 
	</div> 
</div>

<!-- This version of the add-on will just observe you while browsing. It will run quietly in the background and will not get in your way at any time. Every time you go to a new website the add-on will submit the URL and source HTML of the page you're on to a server capturing this data. Note: if you worry about your privacy, please read "How about my privacy?". All of this will be saved in a database that will later on be used as the basis for creating my training and test set. The plug-in will stop capturing data on the 1st of April. At some point after that, an update for this add-on will become available. This update will use your interest profile to re-rank the output of a search engine and will allow you to evaluate the technology I produce without getting in your way. -->

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-8919077-2");
pageTracker._trackPageview();
} catch(err) {}</script>

</body>
</html>