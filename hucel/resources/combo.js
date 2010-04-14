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
 $Id: combo.js,v 1.4 2007/05/30 18:22:58 horanb Exp $
 */


//This is used by the combobox for the portlet
function addOption(){
  oCombo = document.myForm.myCombo;
  oEditable = document.myForm.theurl;
  if(oEditable.value != "") {
  //First search for duplicates
    for(var i = 0; i < oCombo.options.length ; i++){
      if(oCombo.options[i].text == oEditable.value)
	return;
    }
  // Add new one
    oCombo.options[oCombo.options.length] = new Option
    (oEditable.value, oEditable.value);
    }
}

//This is used for tab changes on the portlet
function showTab(tab){
    //Set the current tab to unselected style and current body to display: none.
    currentTab.className = "tabUnselected";
    document.getElementById(currentTab.id + 'Body').style.display = "none";
    //Set the new tab to selected and body to block.
    currentTab = tab;
    currentTab.className = "tabSelected";
    document.getElementById(currentTab.id + 'Body').style.display = "block";
    //This will change the value of the hidden input element that is used to keep track of the active tab.
    tabInput = document.getElementById('tab');
    tabInput.value = currentTab.id;
}

