<!--
English: Copyright © 2007 Sun Microsystems, Inc.  All rights reserved. 
U.S. Government Rights - Commercial software.  Government users are
subject to the Sun Microsystems, Inc. standard license agreement and
applicable provisions of the FAR and its supplements.  Use is subject to
license terms.  Sun,  Sun Microsystems,  the Sun logo and  Java are
trademarks or registered trademarks of Sun Microsystems, Inc. in the
U.S. and other countries.

French: Copyright © 2007 Sun Microsystems, Inc. Tous droits
rÈservÈs.L'utilisation est soumise aux termes du contrat de licence.Sun,
 Sun Microsystems,  le logo Sun et  Java sont des marques de fabrique ou
des marques dÈposÈes de Sun Microsystems, Inc. aux Etats-Unis et dans
d'autres pays.
-->

<!--
$Id: index.jsp,v 1.2 2007/05/30 18:22:59 horanb Exp $
-->

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

   <html>
       <head>
           <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
           <title>COHSE portlet</title>
           <style type="text/css">
               body {
               font-family: Geneva, Arial, Helvetica, san-serif;
               font-size: 0.9 em;
               margin: 60 60 60 60;
               }
               #main_div{
               position: absolute;
               text-align: justify;
               top:  20px;
               left: 20px;
               width: 700px;
               border: solid 1px #FF9966;
               padding:5px 5px 5px 5px;
               background:#F0F0F0;
               }
               h1{
               padding-top: 0px;
               padding-bottom: 10px;
               font-size:1.35em;
               border-bottom:solid 1px #FF9966;
               }
               .desc{
               font-weight:bold;
               }
           </style>
       </head>
       <body>


   <div id="main_div">
       <h1>COHSE Portlet Application</h1>
       <p>This Web application serves the KAIN forwarding proxy and the COHSE portlet. This portlet operates by using the following services:
           <dl>
               <dt class="desc">KAIN Forwarding proxy</dt><dd> This is a re-writing proxy that is used by the portlet to re-write all URLs in a page. 
This service is available <a href="kain">here</a></dd>
               <dt class="desc">COHSE Portlet</dt><dd> This portlet uses the KAIN proxy and dynamically link Web pages.</dd>
           </dl>
       </p>
   <div>
    
    <%--
    This example uses JSTL, uncomment the taglib directive above.
    To test, display the page like this: index.jsp?sayHello=true&name=Murphy
    --%>
    <%--
    <c:if test="${param.sayHello}">
        <!-- Let's welcome the user ${param.name} -->
        Hello ${param.name}!
    </c:if>
    --%>
    
    </body>
</html>
