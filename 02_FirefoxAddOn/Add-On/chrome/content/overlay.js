var alterego = {

  /////////////////////////////
  // Configuration Variables //
  /////////////////////////////
  
  /**
   * URL of the PHP server to which the data storage
   * and re-ranking scripts are deployed
   */
  server: "http://alterego.caret.cam.ac.uk/",

  ////////////////////
  // Help variables //
  ////////////////////
  
  uuid : null,
  currentUrl: "",
  
  /**
   * Invoked when Firefox is started and the add-on is invoked
   */
  onLoad: function() {

    this.initialized = true;
    this.strings = document.getElementById("alterego-strings");

	var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
	prefs = prefs.getBranch("extensions.alterego.");
	// Check whether a unique identifier is already associate to the current installation
	try {
		var value = prefs.getIntPref("uuid"); // get a pref
		if (value){
			alterego.uuid = value;
		} else {
			throw new Error("Invalid uuid");
		}
	} catch (err) {
		// Generate a new unique and identifier
		var value = Math.round(Math.random() * 10000000);
		prefs.setIntPref("uuid", value);
		alterego.uuid = value;
		// Alert the backend of a new plug-in install
		xmlhttp = new XMLHttpRequest();	
		var sendvalue=encodeURIComponent(value);
		var params = "uuid=" + sendvalue;
		xmlhttp.open("POST", alterego.server + "createUser.php",true);
		xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("Content-length", params.length);
		xmlhttp.setRequestHeader("Connection", "close");
		xmlhttp.send(params); 
	}

	// Register the appropriate handlers
	var appcontent = document.getElementById("appcontent");   // browser
    if(appcontent) {
      appcontent.addEventListener("DOMContentLoaded", alterego.handleLoad, true);
	  appcontent.addEventListener("DOMContentLoaded", alterego.handleSearch, true);  
	  appcontent.addEventListener("DOMSubtreeModified", alterego.handleChange, true);
	}
    var messagepane = document.getElementById("messagepane"); // mail
    if(messagepane) {
      messagepane.addEventListener("load", function () { alterego.handleLoad(); }, true);
	}

  },

  /**
   * This function is called when a new page is opened. It will store the
   * current time in order to be able to calculate the total amount of time
   * spent on the current page
   * @param {Object} Event that triggered this function
   */
  handleLoad: function(aEvent) {
    var doc = aEvent.originalTarget;
	doc.startTime = new Date().getTime();
  },

  /**
   * Function that is called when a user leaves a web page. This will capture the
   * unique identifier, current url, length of the HTML code and time spent on the
   * page and create an entry in the user's database table
   * @param {Object} Event that triggered this function
   */
  onPageUnload: function(aEvent) {
    if (aEvent.originalTarget instanceof HTMLDocument) {
		alterego.currentUrl = "";
        var doc = aEvent.originalTarget;
		// Write the entry to the back-end
		xmlhttp = new XMLHttpRequest();	
		var uuid=encodeURIComponent(alterego.uuid);
		var duration=encodeURIComponent(new Date().getTime() - doc.startTime);
		var url=encodeURIComponent(doc.location.href);
		var content=encodeURIComponent(doc.documentElement.innerHTML);
		// Make sure that the current page is an actual web page
		if (url != "about:blank" && url != "about%3Ablank" && !isNaN(duration)){
			// Send to the back-end server
			var params = "uuid=" + uuid + "&duration=" + duration + "&url=" + url + "&content=" + content;
			xmlhttp.open("POST",alterego.server + "createEntry.php",true);
			xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xmlhttp.setRequestHeader("Content-length", params.length);
			xmlhttp.setRequestHeader("Connection", "close");
			xmlhttp.send(params); 
		}
    }
  },

  /**
   * Function that will detect on every page request whether the current page is a Google search
   * page. If it is, the query, page and unique identifier will be sent to the server and the original
   * results will be replaced by the new ones.
   * @param {Object} Event that triggered this function
   */
  handleSearch: function(aEvent) {
	 if (aEvent.originalTarget instanceof HTMLDocument) {
		var doc = aEvent.originalTarget;
		var url = content.wrappedJSObject.document.location.href + "#" + content.wrappedJSObject.document.location.hash;
		// Check whether the URL has changed
		if (alterego.currentUrl != url){
			// Deduce from the URL whether the current page is a Google search page
			//   "tbs" can be found on the non-search result pages
			//   "images" will be found on Google image search pages
			if (url.indexOf("www.google.") != -1 && url.indexOf("q=") != -1 && url.indexOf("tbs=") == -1 && url.indexOf("/images?") == -1){
				var index = url.indexOf("q=");
				var query = url;
				// Extract the query from the URL
				query = query.substring(index + 2);
				query = query.substring(0, query.indexOf("&"));
				query = decodeURIComponent(query);
				// Extract the page number from the URL
				var page = 1;
				index = url.indexOf("start=");
				alterego.currentUrl = url;
				if (index != -1){
					var start = url.substring(index + 6);
					start = start.substring(0, start.indexOf("&"));
					page = (start / 10) + 1;
					// Show the normal ranking and do no personalization after page 5 as this is out of scope
					if (page > 5){
						content.wrappedJSObject.document.documentElement.removeChild(content.wrappedJSObject.document.getElementById("alterego_search_placeholder"));
						return false;
					}
				}
				
				query= encodeURIComponent(query);
				var params = "user=usr_" + alterego.uuid + "&page=" + page + "&query=" + query;
				
				xmlhttp = new XMLHttpRequest();	
				xmlhttp.onreadystatechange=function() { 
					if(xmlhttp.readyState==4){ 
						// Replace the old results with the reranked ones if the request is successful.
						if (xmlhttp.status == 200){
							content.wrappedJSObject.document.getElementById("res").innerHTML = xmlhttp.responseText;
						}
						// Remove the overlay and show the entire page
						content.wrappedJSObject.document.documentElement.removeChild(content.wrappedJSObject.document.getElementById("alterego_search_placeholder"));
						var el = content.wrappedJSObject.document.createElement("div");
						el.id = url;
						content.wrappedJSObject.document.documentElement.appendChild(el);
					} 
				};
				// Request the personalized results
				xmlhttp.open("POST",alterego.server + "search/AlterEgoServer/showSearch.jsp",true);
				xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
				xmlhttp.setRequestHeader("Content-length", params.length);
				xmlhttp.setRequestHeader("Connection", "close");
				xmlhttp.send(params);

			}
		}
	 }
  },

  /**
   * Function that will detect that a Google search page was requested when the previous page is being
   * unloaded. It will then add a white overlay to the browser in order to prevent flickering.
   * @param {Object} Event that triggered this function
   */
  handleChange: function(aEvent){
	var url = content.wrappedJSObject.document.location.href + "#" + content.wrappedJSObject.document.location.hash;
	// Check whether we've already run through this process for the current URL
	if (alterego.currentUrl != url){
		// Check if the newly requested page is a google search page
		if (url.indexOf("www.google.") != -1 && url.indexOf("q=") != -1 && url.indexOf("tbs=") == -1 && url.indexOf("/images?") == -1){
			// Check whether the placeholder is already there
			if (!content.wrappedJSObject.document.getElementById("alterego_search_placeholder") && !content.wrappedJSObject.document.getElementById(url)){
				// Create a full width and full height div to place on top of the current screen
				var doc = content.wrappedJSObject.document.documentElement;
				var el = content.wrappedJSObject.document.createElement("div");
				el.id = "alterego_search_placeholder";
				el.style.position = "fixed";
				el.style.top = "0px";
				el.style.left = "0px";
				el.style.height = "100%";
				el.style.width = "100%";
				el.style.backgroundColor = "#ffffff";
				// Make sure that it stays on top of the current page
				el.style.zIndex = "100000";
				// Add the div to the viewport
				doc.appendChild(el);
			}
		}
	}
  },
	
  /**
   * Show an alert box with appropriate information about the AlterEgo add-on when the AlterEgo item is clicked under the 
   * Tools menu
   * @param {Object} Event that triggered this function
   */
  onMenuItemCommand: function(e) {
    var promptService = Components.classes["@mozilla.org/embedcomp/prompt-service;1"]
                                  .getService(Components.interfaces.nsIPromptService);
    promptService.alert(window, "AlterEgo 0.2",
                                "Add-on that will attempt to learn your personal interests by observing your browsing behavior. It will now use your personal interests to re-rank the output of search engines.\n\nMore information about the project and this add-on can be found on http://alterego.caret.cam.ac.uk.\n\nYour unique user id is " + alterego.uuid + "\n\nThanks for supporting my research project!");
  },

  /**
   * Show the same alert box as in the Tools menu when the icon in the bottom bar is clicked
   * @param {Object} Event that triggered this function 
   */ 
  onToolbarButtonCommand: function(e) {
    alterego.onMenuItemCommand(e);
  }
};

window.addEventListener("load", alterego.onLoad, false);
window.addEventListener("pageshow", alterego.doReplace, false);
window.addEventListener("pagehide", alterego.onPageUnload, false);