/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.config;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class responsible for parsing and loading the main configuration
 * file (config.properties)
 */
public class ConfigLoader {
	
	/**
	 * Parse the project's main properties file
	 * @return Object that contains all of the configuration parameters
	 */
	public static Properties getConfig() {
		try {
			Properties mainConfig = new Properties();
			// Load the config.properties file and return the Properties object
			mainConfig.load(new FileInputStream("build/classes/org/cl/nm417/config/config.properties"));
			return mainConfig;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
