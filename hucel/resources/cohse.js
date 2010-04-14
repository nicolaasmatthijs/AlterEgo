/**
 English: Copyright © 2007 Sun Microsystems, Inc.  All rights reserved. 
U.S. Government Rights - Commercial software.  Government users are
subject to the Sun Microsystems, Inc. standard license agreement and
applicable provisions of the FAR and its supplements.  Use is subject to
license terms.  Sun,  Sun Microsystems,  the Sun logo and  Java are
trademarks or registered trademarks of Sun Microsystems, Inc. in the
U.S. and other countries.

French: Copyright © 2007 Sun Microsystems, Inc. Tous droits
réservés.L'utilisation est soumise aux termes du contrat de licence.Sun,
 Sun Microsystems,  le logo Sun et  Java sont des marques de fabrique ou
des marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
d'autres pays.
 */

/**
 $Id: cohse.js,v 1.9 2007/05/30 18:22:58 horanb Exp $
 */

var _current_box;

//Check if the current browser is Internet Explorer
function checkIE() {
    var agt=navigator.userAgent.toLowerCase();
    return (agt.indexOf("msie") != -1);
}

//To remove the current linkbox
function removeLinkbox(num) {
    var linkbox = document.getElementById(num);
    if ( checkIE() ) {
        linkbox.style.visibility = "hidden";
        _current_box = null;
    } else {
        linkbox.setAttribute("style", "visibility: hidden; "
                                       + "position: absolute;");
        _current_box = null;
    }
}

//Highlight the given span element
function highlight(id) {
    var element = document.getElementById(id);
    if (checkIE()) {
        element.style.background = "#ccc";
        element.style.display = "inline";
        element.style.fontSize = "inherit";
    } else {
        element.setAttribute('style', 'font-size:inherit;background-color: #ccc;display:inline;');
    }
}

//Unhighlight the given span element
function unhighlight(id) {
    var element = document.getElementById(id);
    if (checkIE()) {
        element.style.background = "";
        element.style.display = "inline";
        element.style.fontSize = "inherit";
    } else {
       element.setAttribute('style', 'font-size:inherit;display:inline;background-color: ');
    }
}

/* Takes IDs and then passes them off to a link
   box service. The service will then provide the text that should
   appear in a linkbox. IDs could be anything, possibly UUIDs */

var xmlHttp;

//Main function to call the KAIN service
function linkbox(id,obj) {
    href = null;
    /*To make sure that our SPAN tag is the only child of the Anchor tag, then that means
      we override the original link. In order to solve this problem, we will get the href value and also the
      text node value and send it to our service so that it will be included in the linkbox*/
    if((obj.nodeName=='A' || obj.nodeName=='a') && obj.childNodes.length==1){
        href = obj.getAttribute("href");
    }

    //Change the cursor to show that we will be processing this box
    document.body.style.cursor = "wait";
    xmlHttp=GetXmlHttpObject(stateChanged);
    /*Here we check if we actually got a href value, if we did then the call to service will be different*/
    if(href!=null){
        xmlHttp.open("GET", service+"/kain?getbox="+id+"&href="+href+"&txt="+obj.firstChild.firstChild.nodeValue, true);
    }
    else{
        xmlHttp.open("GET", service+"/kain?getbox="+id , true);
    }
    xmlHttp.send(null);
}

//Check whether the call is completed or not
function stateChanged() 
{ 
    if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete")
    { 
         content = xmlHttp.responseText;
         // Throw up an overlib box with the links in it.
         box = overlib(content, STICKY, HAUTO, VAUTO, CENTER, AUTOSTATUS, FULLHTML); 
         //change the curser to default - box is complexted
        document.body.style.cursor = "default";
    } 
} 

//Try to get the browser independent XMLHttpRequest object
function GetXmlHttpObject(handler)
{ 
    var objXmlHttp=null;

    if (navigator.userAgent.indexOf("Opera")>=0)
    {
        objXmlHttp=new XMLHttpRequest(); 
        return objXmlHttp;
    }
    if (navigator.userAgent.indexOf("MSIE")>=0)
    { 
        var strName="Msxml2.XMLHTTP";
        if (navigator.appVersion.indexOf("MSIE 5.5")>=0)
        {
            strName="Microsoft.XMLHTTP";
        } 
        try
        { 
            objXmlHttp=new ActiveXObject(strName);
            objXmlHttp.onreadystatechange=handler; 
            return objXmlHttp;
        } 
        catch(e)
        { 
            alert("Error. Scripting for ActiveX might be disabled"); 
            return; 
        } 
    } 
    if (navigator.userAgent.indexOf("Mozilla")>=0)
    {
        objXmlHttp=new XMLHttpRequest();
        objXmlHttp.onload=handler;
        objXmlHttp.onerror=handler; 
        return objXmlHttp;
    }
}

function toggle(id)
 {
   var L = document.getElementById("L"+id);
   var I = document.getElementById("I"+id);
   if (L.style.display == "" || L.style.display == "none") {
        L.style.display = "block";
        I.src = service+"/resources/collapse.gif";
    }
  else { 
    L.style.display = "none";
    I.src = service+"/resources/expand.gif";
  }
}

function toggleSpotlight(id)
 {
   var L = document.getElementById("L"+id);
   var I = document.getElementById("I"+id);
   if (L.style.display == "" || L.style.display == "none") {
        L.style.display = "block";
        I.style.display = "none";
    }
  else { 
    L.style.display = "none";
    I.style.display = "block";
  }
}

function toggleSummary(id)
{
  var L = document.getElementById("L"+id);
  var I = document.getElementById("I"+id);
  if (L.style.display == "" || L.style.display == "none") {
    L.style.display = "block";
    I.src = service+"/resources/summary-close.gif";
  }
  else { 
    L.style.display = "none";
    I.src = service+"/resources/summary.gif";
  }
}