/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.util;

import java.sql.DriverManager;
import java.util.Properties;

import org.cl.nm417.config.ConfigLoader;

import com.mysql.jdbc.Connection;

/**
 * Class that will take care of the Database connection
 */
public class Database {

	/**
	 * Open the database connection
	 * @return opened Database connection
	 */
	public static Connection openConnection() {
		Connection conn = null;
		Properties mainConfig = ConfigLoader.getConfig();
		try {
			String userName = mainConfig.getProperty("dbuser");
			String password = mainConfig.getProperty("dbpass");
			String url = "jdbc:mysql://" + mainConfig.getProperty("dburl")
					+ "/" + mainConfig.getProperty("dbname");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection(url, userName, password);
			return conn;
		} catch (Exception e) {
			System.err.println("Cannot connect to database server");
		}
		return null;
	}

	/**
	 * Close the database connection
	 * @param conn The current open Database connection
	 */
	public static void closeConnection(Connection conn) {
		try {
			// Close the connection if it is open only
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) { /* ignore close errors */ }
	}

}
