<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.cl.nm417.AlterEgo"%>
<%@page import="org.cl.nm417.google.GoogleNGram"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Load Google N-Gram</title>
</head>
<body>

	<%
	
		GoogleNGram.loadGoogleNGram();
		out.println("Finished");
	
	%>

</body>
</html>