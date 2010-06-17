<!-- 
/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
 
/**
 * Script that can be called to start generating all possible user profiles for a
 * given user. These profiles can then be used for re-ranking using relevance judgements
 * data
 */
 -->
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.cl.nm417.profile.ProfileGenerator"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Generating Profiles</title>
</head>
<body>

<%
	// Generate profiles for all users in the system
	ProfileGenerator gen = new ProfileGenerator();
	gen.doRun();
%>

</body>
</html>