alterego.onFirefoxLoad = function(event) {
  document.getElementById("contentAreaContextMenu")
          .addEventListener("popupshowing", function (e){ alterego.showFirefoxContextMenu(e); }, false);
};

alterego.showFirefoxContextMenu = function(event) {
  // show or hide the menuitem based on what the context menu is on
  document.getElementById("context-alterego").hidden = gContextMenu.onImage;
};

window.addEventListener("load", alterego.onFirefoxLoad, false);
