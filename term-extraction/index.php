<?php
if (file_exists(dirname(__FILE__).'/config.php')) {
	require_once(dirname(__FILE__).'/config.php');
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Term Extraction | fivefilters.org</title>
	<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>

	<style>
	body {
		background: #fff url(images/background.png) no-repeat top left;
		font-family: sans-serif;
		margin-left: 3em;
		margin-right: 8em;
		font-size: 100%;
		line-height: 1.4em;
	}
	h1#title { margin-bottom: 0; }
	h1 a { border-bottom: none !important;}
	#content h1 { margin-left: .1em; }
	#menu { margin-left: 10px; }
	#menu a {
		background-color: #000;
		color: #fff;
		padding: 5px;
		margin-right: 1px;
		border-bottom: none;
	}
	a:link {
		color: #000;
		border-bottom: 1px dotted #888;
		text-decoration: none;
	}
	a:visited {
		color: #000;
		border-bottom: 1px dotted #888;
		text-decoration: none;
	}
	a:hover {
		color: #000;
		border-bottom: 1px solid #333;
	}
	h1#title a {
		background: transparent url(images/title.png) no-repeat top left;
		text-indent: -9999px;
		height: 70px;
		display: block;
	}
	p { line-height: 1.5em; }
	h2 { font-size: 1.5em; margin-bottom: .5em; margin-top: 1.5em; }
	h4 { margin-bottom: 0; font-weight: normal; }
	#form { margin-bottom: 3em; }
	#content { width: 700px; margin-left: 2em; margin-top: 2em; }
	#feed_label { margin-bottom: .3em; }
	label { display: block; margin: .2em; cursor:pointer; }
    #text { font-size: 1em; padding: 5px; width: 503px; height: 150px; display: block; background-color: #C8E6A6; margin: 0; }
    #submit { 
		font-size: 1em;
		cursor:pointer; 
		/*background: #405927 url(images/icon_pdf.png) no-repeat scroll 5px center;*/
		background-color: #405927;
		padding: 0.5em .5em .5em .5em;
		font-weight: bold; 
		-moz-border-radius: 0 0 15px 15px; 
		width: 516px; 
		text-align: left; 
		color: #fff; 
		border: 0; 
		margin: 0;
		-webkit-border-top-left-radius: 0;
		-webkit-border-top-right-radius: 0;
		-webkit-border-bottom-left-radius: 15px;
		-webkit-border-bottom-right-radius: 15px;
	}
    /* #submit:hover { border: 1px solid #999; color: #fff; background-color: #666; } */
	#footer { font-size: .8em; margin-top: 2em; }
	table#compare { font-size: .8em; border-spacing: 1px; width: 850px; }
	table#donation-history {font-size: .8em; border-spacing: 1px; width: 500px; }
	thead { background-color: #ddd; }
	th, td { padding: .5em; text-align: left; }
	#donation-history th, #donation-history td { padding: .2em; }
	img { border: 0; }
	.donate th { font-size: .8em; text-align: right; }
    .donate td, .donate th { padding-bottom: .8em; padding-right: .2em; }
	.donate-button {
		background-color: #eee;
		border: 1px solid #666;
		font-size: .8em;
		-moz-border-radius: .2em;
		padding: .2em;
		cursor: pointer;
	}
	.donate-button:hover {
		background-color: lightyellow;
		border: 1px solid red;
	}
	kbd {
		border-style: solid;
		border-width: 1px 2px 2px 1px;
		padding: 0 3px;
	}
	#advanced {
		width: 506px;
		padding: 2px 5px;
		margin: 0;
		border-top: 0;
		border-bottom: 0;
		background-color: #7AA61C;
	}
	#toggle-advanced {
		cursor: pointer;
		width: 506px;
		padding: 2px 5px;
		margin: 0;
		border-top: 0;
		border-bottom: 0;
		background-color: #7AA61C;
		dispdlay: none;
	}
	.alt-feeds { margin-top: .3em; }
	.alt-feeds a {
		background-color: #eee;
		padding: .2em;
	}
	</style>
	<script type="text/javascript">
	$(document).ready(function() {
		$('#advanced').hide();
		$('#toggle-advanced').click(function() {
			if (!$('#advanced').is(':visible')) {
				$('#toggle-advanced label').text('hide options');
				$('#advanced').slideToggle('fast');
			} else {
				$('#toggle-advanced label').text('show options...');
				$('#advanced').slideToggle('fast', function() {
				});
			}
		});
		$('#text-mark-curtis').click(function() {
			$('#text').val('Report by the Joint Intelligence Commitee, "Nationalist and radical movements in the Arabian Peninsula", 10 February 1958\n\n"Arab nationalism, including the urge towards greater Arab unity and the removal of any foreign control, is already the most powerful emotional force in the area and it is beginning to penetrate even the most remote corners of the peninsula... The maintenance of our interests in the Persian Gulf states is dependent on continued stability in the area. At present only the Rulers can provide this. No alternative regimes are in sight, certainly not regimes which could provide the stability on which the maintenance of British interests depends. A failure to support any one of the Rulers would weaken the confidence of the others in our ability and willingness to protect them. It is on this confidence that our special position in the Gulf chiefly rests."');
		return false;
		});
		$('#text-medialens').click(function() {
			$('#text').val('Inevitably, then, corporations do not restrict themselves merely to the arena of economics. Rather, as John Dewey observed, "politics is the shadow cast on society by big business". Over decades, corporations have worked together to ensure that the choices offered by \'representative democracy\' all represent their greed for maximised profits.\n\nThis is a sensitive task. We do not live in a totalitarian society - the public potentially has enormous power to interfere. The goal, then, is to persuade the public that corporate-sponsored political choice is meaningful, that it makes a difference. The task of politicians at all points of the supposed \'spectrum\' is to appear passionately principled while participating in what is essentially a charade.');
		return false;
		});

		$('#text-uk-indymedia').click(function() {
			$('#text').val('Activists from Free Gaza.org and the International Solidarity Movement, determined to break the Israeli blockade on Gaza, gathered in Cyprus and set sail for Gaza on the \'Spirit of Humanity\' on June 29th. The following morning, over 20 miles off the coast of Gaza, the boat was surrounded by Israeli Naval vessels who threatened to fire on the boat unless it turned around. The boat continued towards Gaza, and was then boarded by Israeli soldiers, who took control of the boat, and took all 20 passengers and the captain hostage, along with aid including building materials, medical supplies and childrens art supplies. By attacking the vessel in International Waters, and by preventing it from sailing into Gaza, Israel once again showed that it has scant regard for International Law, and confirmed that it is the de facto occupier of Gaza.');
		return false;
		});
		$('#text-medialens').click();
	});
	</script>
  </head>
  <body>
	<div id="header">
		<h1 id="title"><a href="http://fivefilters.org">fivefilters.org</a></h1>
		<div id="menu">
		<a href="../explore_independent_media.php">explore independent media</a><a href="../pdf-newspaper">pdf newspaper</a><a href="../content-only">full-text rss</a><a href="../term-extraction">term extraction</a>
		</div>
	</div>
	<div id="content">
	<h1>Term Extraction</h1>
    <form method="POST" action="http://term-extraction.appspot.com/terms" id="form">
	<label id="feed_label" for="feed">Enter or paste some text</label>
	<textarea id="text" name="content"></textarea>
	<div id="toggle-advanced" class="advanced-bottom"><label>show options...</label></div>
	<div id="advanced">
		<label>JSON Callback: <input type="text" name="callback" value="" style="width:300px; padding: 2px;" /></label>
	</div>
	<input type="submit" id="submit" name="submit" value="Get Terms" /> 
	<h4>Or choose from extracts below...</h4>
	<div class="alt-feeds">
		<a href="#" id="text-medialens">Medialens</a>
		<a href="#" id="text-uk-indymedia">UK Indymedia</a>
		<a href="#" id="text-mark-curtis">Mark Curtis</a>
	</div>
	</form>
	<h2>About</h2>
	<p>This is a free software project to enable easy term extraction through a web service. Given some text it will return a list of terms with (hopefully) the most relevant first. The list is returned in JSON format. It is a <a href="http://www.gnu.org/philosophy/free-sw.html" title="free as in freedom">free</a> alternative to Yahoo's Term Extraction service. It is being developed as part of the <a href="http://fivefilters.org">Five Filters</a> project to promote alternative, non-corporate media.</p>
	
<!-- AddThis Button BEGIN -->
<script type="text/javascript">var addthis_pub="k1mk1m";</script>
<a style="border-bottom: none;" href="http://www.addthis.com/bookmark.php?v=20" onmouseover="return addthis_open(this, '', '[URL]', '[TITLE]')" onmouseout="addthis_close()" onclick="return addthis_sendto()"><img src="http://s7.addthis.com/static/btn/lg-share-en.gif" width="125" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/200/addthis_widget.js"></script>
<!-- AddThis Button END -->
	
	<h2>Similar Software</h2>
	<p>Free software for term extraction:</p>
	<ul>
		<li><a href="http://code.google.com/p/maui-indexer/">Maui</a> (Java, GPL)</li>
		<li><a href="http://www.nzdl.org/Kea/">Kea</a> (Java, GPL)</li>
		<li><a href="http://pypi.python.org/pypi/topia.termextract/1.1.0">Topia's Term Extraction</a> (Python, ZPL)</li>
	</ul>
	
	<p>Non-free web services for term extraction:</p>
	<ul>
		<li><a href="http://developer.yahoo.com/search/content/V1/termExtraction.html" rel="nofollow">Yahoo's Term Extraction</a></li>
		<li><a href="http://www.nactem.ac.uk/software/termine/" rel="nofollow">NaCTeM's TerMine</a></li>
		<li><a href="http://www.alchemyapi.com/api/keyword/" rel="nofollow">Orchestr8's Keyword Extraction</a></li>
		<li><a href="http://www.opencalais.com/about" rel="nofollow">OpenCalais</a></li>
	</ul>
	<p><a href="http://www.medelyan.com/">Olena Medelyan</a> has more information and resources on her <a href="http://maui-indexer.blogspot.com/">topic indexing blog</a>. She is involved with the Maui and Kea projects.</p>
	
	
	<h2>Source Code and Technologies</h2>
	<p><a href="https://code.launchpad.net/~keyvan-k1m/fivefilters/term-extraction/">Source code available</a>.<br />The application uses Python, <a href="http://pypi.python.org/pypi/topia.termextract/">Topia's Term Extraction</a> and <a href="http://pypi.python.org/pypi/simplejson/">simplejson</a>.</p>

	<h2>Installation and System Requirements</h2>
	<p>This code should run on most hosts with Python support. You should be able to test it on your own machine using web.py. I had it running on <a href="https://www.nearlyfreespeech.net/">NearlyFreeSpeech.NET</a> but they only offer Python access through CGI - which is quite slow. The version here is running on Google's App Engine.</p>
	<p>I'm not a Python expert so the instruction below only show you how to download the files. Inside the term-extraction directory you'll find two sub-directories: web.py and google_app_engine. If you'd like to test on your own machine try installing <a href="http://webpy.org/install">web.py</a> and running <kbd>python code.py</kbd>. If you'd like to host the code on <a rel="nofollow" href="http://code.google.com/appengine/docs/python/gettingstarted/uploading.html">Google App Engine</a>, use the code inside google_app_engine. To download the code make sure you have the <a href="http://bazaar-vcs.org/">Bazaar client</a> on your own computer.</p>
	<ol style="width: 800px">
		<li>Change to the directory where you want to place the Term Extraction application files</li>
		<li>Enter <kbd>bzr co http://bazaar.launchpad.net/~keyvan-k1m/fivefilters/term-extraction term-extraction</kbd></li>
		<li>Look inside <kbd>term-extraction</kbd> and, depending on where you want to host the files, use the files in either web.py or google_app_engine</li>
	</ol>
	
	<h2>Support</h2>
	<p>I'm happy to help activists/anarchists/progressives set this up. If you don't fall in this category, I offer paid support.</p>
	<p><a href="https://bugs.launchpad.net/fivefilters">Bug reports</a> and <a href="https://answers.launchpad.net/fivefilters">questions</a> welcome.</p>
	
	<h2>License</h2>
	<p><a href="http://en.wikipedia.org/wiki/Affero_General_Public_License" style="border-bottom: none;"><img src="images/agplv3.png" /></a><br />This web application is licensed under the <a href="http://en.wikipedia.org/wiki/Affero_General_Public_License">AGPL version 3</a> (<a href="http://www.clipperz.com/users/marco/blog/2008/05/30/freedom_and_privacy_cloud_call_action">find out why</a>). The bulk of the work, however, is carried out by libraries which are licensed as follows...</p>
	<ul>
		<li>Topia's Term Extraction: <a href="http://www.zope.org/Resources/ZPL">ZPL 2.1</a></li>
		<li>simplejson: <a href="http://en.wikipedia.org/wiki/MIT_License">MIT</a></li>
	</ul>
	
	<h2>Donate</h2>
	<p>I'm a student and I work on this in my spare time. Donations very welcome...</p>	
	
	<table class="donate" style="margin-bottom: .5em;">
		<tr><th>£ (UK Pounds)</th>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="1.00">
<input type="hidden" name="currency_code" value="GBP">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate £1" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="2.00">
<input type="hidden" name="currency_code" value="GBP">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate £2" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="5.00">
<input type="hidden" name="currency_code" value="GBP">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate £5" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="currency_code" value="GBP">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate £?" border="0" name="submit" class="donate-button">
</form>
</td>
		</tr>
		<tr><th>€ (Euros)</th>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="1.00">
<input type="hidden" name="currency_code" value="EUR">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate €1" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="2.00">
<input type="hidden" name="currency_code" value="EUR">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate €2" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="5.00">
<input type="hidden" name="currency_code" value="EUR">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate €5" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="currency_code" value="EUR">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate €?" border="0" name="submit" class="donate-button">
</form>
</td>
		</tr>
		<tr><th>$ (US Dollars)</th>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="1.00">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate $1" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="2.00">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate $2" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="amount" value="5.00">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate $5" border="0" name="submit" class="donate-button">
</form>
</td>
			<td><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_donations">
<input type="hidden" name="business" value="keyvan@k1m.com">
<input type="hidden" name="lc" value="GB">
<input type="hidden" name="item_name" value="Term Extraction">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="cn" value="Comments?">
<input type="hidden" name="no_shipping" value="1">
<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_SM.gif:NonHosted">
<input type="submit" value="Donate $?" border="0" name="submit" class="donate-button">
</form>
</td>
		</tr>
	</table>
	
	<h2>Author</h2>
	<p>Created by <a href="http://www.keyvan.net">Keyvan Minoukadeh</a> for the <a href="http://fivefilters.org">Five Filters</a> project.<br />
	Email: keyvan (at) keyvan.net</p>
	
	</div>
  </body>
</html>
