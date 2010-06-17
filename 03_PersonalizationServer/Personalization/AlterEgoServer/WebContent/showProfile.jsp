<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * This page will generate and print a profile using the passed profile generation parameters
 */
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="org.cl.nm417.data.Unigram"%>
<%@page import="org.cl.nm417.data.Profile"%>
<%@page import="java.util.ArrayList"%>

<%@page import="java.util.Enumeration"%>
<%@page import="java.util.HashMap"%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Profile</title>
	<style>
		li {
			font-size: 10px;
			font-family: "Verdana";
		}
	</style>
</head>
<body>
	
	<%
	
		// Load passed parameters into a configuration object
		HashMap<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> keys = request.getParameterNames();
	    while (keys.hasMoreElements() )
	    {
	      String key = keys.nextElement();
	      String value = request.getParameter(key);
	      try {
	    	  // Check whether a parameter is an integer
	    	  int val = Integer.parseInt(value);
	    	  map.put(key,val);
	      } catch (Exception ex){
	    	  // Check whether a parameter is a boolean
	    	  if (value.equals("true")){
	    		  map.put(key, true);
	    	  } else if (value.equals("false")){
	    		  map.put(key, false);
	    	  } else {
	    		  map.put(key,value);
	    	  }
	   	  }
	    }
	    
	    // Generate the profile
		Profile profile = AlterEgo.generateProfile(map, null);
	
	    // Print the generated profile as a list of terms and weights
		out.println("<h3>" + profile.getUserId() + "</h3><ul>");
		for (Unigram u: profile.getUnigrams()){
			out.println("<li>" + u.getText() + " (" + u.getWeight() + ")</li>");
		}
		out.println("</ul>");
		
		out.flush();
	
	%>

</body>
</html>