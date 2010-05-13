<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<select id="profilename">
<%
	// Extract user profiles to be extracted
	String userid = request.getParameter("userid");
	File f = new File("/Users/nicolaas/Desktop/AlterEgo/dataprocessing/data/profiles/" + userid);
	String[] ffiles = f.list();
	for (String s: ffiles){
		if (s.startsWith("usr_")){
			out.println("<option value='" + s + "'>" + s + "</option>");
		}
	}
%>
</select>