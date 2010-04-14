var xmlHttp;
var content;

function loadEventsXML()
{
	xmlHttp = getXmlHttpObject(stateChanged);
	xmlHttp.open('GET', linkBase, true);
	xmlHttp.send(null);
}

function stateChanged()
{
	if ( xmlHttp.readyState == 4 || xmlHttp.readyState == 'complete' )
	{
		if ( xmlHttp.status == 200 )
		{
			xmlString = xmlHttp.responseText;
			getLinkBox(xmlString);
		}
		else
		{
			getEmptyLinkBox();
		}
	}
}

function getXmlHttpObject(handler)
{
	var objXmlHttp = null;

	if ( navigator.userAgent.indexOf('Opera') >= 0 )
	{
		objXmlHttp = new XMLHttpRequest();
		return objXmlHttp;
	}

	if ( navigator.userAgent.indexOf('MSIE') >= 0 )
	{
		var strName = 'MSxml2.XMLHTTP';

		if ( navigator.appVersion.indexOf('MSIE 5.5') >= 0 )
		{
			strName = 'Microsoft.XMLHTTP';
		}

		try
		{
			objXmlHttp = new ActiveXObject(strName);
			objXmlHttp.onreadystatechange = handler;
			return objXmlHttp;
		}
		catch ( e )
		{
			alert('Error, scripting for ActiveX might be disabled');
			return;
		}
	}

	if ( navigator.userAgent.indexOf('Mozilla') >= 0 )
	{
		objXmlHttp = new XMLHttpRequest();
		objXmlHttp.onload = handler;
		objXmlHttp.onerror = handler;
		return objXmlHttp;
	}
}

function getLinkBox(xmlString)
{
	var xmlDoc;

	if ( window.ActiveXObject )
	{
		xmlDoc = new ActiveXObject('Microsoft.XMLDOM');
		xmlDoc.async = 'false';
		xmlDoc.loadXML(xmlString);
	}
	else
	{
		var parser = new DOMParser();
		xmlDoc = parser.parseFromString(xmlString, 'text/xml');
	}

	var linkList = xmlDoc.getElementsByTagName('link');

	content = '<div id="mainBox"><div id="boxTitle">Related events</div><div id="linkList">';

	var linkListDiv = '';

	if ( linkList.length == 0 )
	{
		linkListDiv = '<p>No links available!</p>';
	}
	else
	{
		linkListDiv = '<ul>';

		for ( var i = 0; i < linkList.length; i++ )
		{
			var title = linkList[i].getElementsByTagName('title')[0].childNodes[0].nodeValue;
			//var url = serviceURL + unescape(linkList[i].getElementsByTagName('url')[0].childNodes[0].nodeValue);
			var url = unescape(linkList[i].getElementsByTagName('url')[0].childNodes[0].nodeValue);

			linkListDiv = linkListDiv + '<li><a href="' + url + '">' + title + '</a></li>';
		}

		linkListDiv = linkListDiv + '</ul>';
	}

	content = content + linkListDiv + '</div></div>';
}

function getEmptyLinkBox()
{
	content = '<div id="mainBox"><div id="boxTitle">Related events</div><div id="linkList">';
	var linkListDiv = '<p>No links available! (empty)</p>';
	content = content + linkListDiv + '</div></div>';
}


/**
* keyHandler script
* Taken from: http://www.go4expert.com/forums/showthread.php?t=2733 (14/07/2008)
*/
function keyHandler(e)
{
	var pressedKey;
	var pressedChar;

	if ( document.all )
	{
		e = window.event;
	}

	if ( document.layers || e.which )
	{
		pressedKey = e.which;
	}

	if ( document.all )
	{
		pressedKey = e.keyCode;
	}

	pressedChar = String.fromCharCode(pressedKey);

	if ( pressedChar == 'o' )
	{
		document.getElementById("loading").className = (document.getElementById("loading").className == "loading-visible") ? "loading-invisible" : "loading-visible";
	}
}
