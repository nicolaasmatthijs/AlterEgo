/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.util;

/**
 * Class that takes care of logging. This is used to log the details
 * of all interleaved searches in order to ensure a correct implementation
 * and accurate usage data.
 */
public class Logging {

	private String log = "";

	public void setLog(String log) {
		this.log = log;
	}

	public String getLog() {
		return log;
	}
	
	/**
	 * Will append a line to the current log file
	 * @param s	String to log
	 */
	public void appendLog(String s){
		log += s + "\n";
	}
	
}
