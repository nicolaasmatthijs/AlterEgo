
<%@page import="com.mysql.jdbc.PreparedStatement"%><%@page import="com.mysql.jdbc.Connection"%>
<%@page import="org.cl.nm417.db.Database"%><%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   <%
   
   	// CREATE TABLE `interleaving` (
	// `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
	// `userid` VARCHAR( 256 ) NOT NULL ,
	// `query` VARCHAR( 256 ) NOT NULL ,
	// `clickedrank` INT NOT NULL ,
	// `clickedranking` VARCHAR( 256 ) NOT NULL ,
	// `origranking` INT NOT NULL ,
	// `evaluating` VARCHAR( 256 ) NOT NULL ,
	// `url` VARCHAR( 512 ) NOT NULL ,
	// `page` INT NOT NULL ,
	// `clickdate` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	// ) TYPE = MYISAM ;
   
   String userid = request.getParameter("userid");
   String query = request.getParameter("query");
   String clickedrank = request.getParameter("clickedrank");
   String clickedranking = request.getParameter("clickedranking");
   String evaluating = request.getParameter("evaluating");
   String url = request.getParameter("url");
   String pagen = request.getParameter("pagen");
   String origranking = request.getParameter("origranking");
   
   Connection conn = Database.openConnection();
   PreparedStatement stmt;

   try {
		stmt = (PreparedStatement) conn.prepareStatement("INSERT INTO interleaving " + 
					"(userid, query, clickedrank, origranking, clickedranking, evaluating, url, page) VALUES (?,?,?,?,?,?,?,?)");
		stmt.setString(1, userid);
		stmt.setString(2, query);
		stmt.setInt(3, Integer.parseInt(clickedrank));
		stmt.setInt(4, Integer.parseInt(origranking));
		stmt.setString(5, clickedranking);
		stmt.setString(6, evaluating);
		stmt.setString(7, url);
		stmt.setInt(8, Integer.parseInt(pagen));
			
		// Execute the query
		stmt.executeUpdate();
   } catch (Exception ex){
	  	System.out.println(ex.getMessage());
   }
   
   Database.closeConnection(conn);
   
   response.sendRedirect(url);
   
%>