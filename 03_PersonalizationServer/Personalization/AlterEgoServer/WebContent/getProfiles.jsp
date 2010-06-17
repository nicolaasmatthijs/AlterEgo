<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Get the list of profiles that have been generated for a particular user and
 * send them along as a select list. This will be called by the main personalization
 * User Interface when a new user is selected.
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.cl.nm417.config.ConfigLoader"%><select id="profilename">
<%
	// Extract user profiles to be extracted
	String userid = request.getParameter("userid");
	// Get directory with all of the user's profiles
	File f = new File(ConfigLoader.getConfig().getProperty("profiles") + userid);
	String[] ffiles = f.list();
	for (String s: ffiles){
		// If it starts with usr_ , it is a proper profile
		if (s.startsWith("usr_")){
			// Create a select option
			out.println("<option value='" + s + "'>" + s + "</option>");
		}
	}
%>
</select>