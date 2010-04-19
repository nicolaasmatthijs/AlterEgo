package org.cl.nm417.db;

import java.sql.DriverManager;

import com.mysql.jdbc.Connection;

public class Database {

	public static Connection openConnection(){
		Connection conn = null;
        try {
            String userName = "root";
            String password = "root";
            String url = "jdbc:mysql://127.0.0.1:8889/alterego";
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            conn = (Connection) DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");
            return conn;
        } catch (Exception e) {
            System.err.println ("Cannot connect to database server");
        }
        return null;
	}
	
	public static void closeConnection(Connection conn){
		 if (conn != null)
         {
             try
             {
                 conn.close ();
                 System.out.println ("Database connection terminated");
             }
             catch (Exception e) { /* ignore close errors */ }
         }
	}
	
}
