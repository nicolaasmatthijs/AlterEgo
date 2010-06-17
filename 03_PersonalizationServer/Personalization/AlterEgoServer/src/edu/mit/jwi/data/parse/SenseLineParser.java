/********************************************************************************
 * MIT Java Wordnet Interface (JWI)
 * Copyright (c) 2007-2008 Massachusetts Institute of Technology
 *
 * This is the non-commercial version of JWI.  This version may *not* be used
 * for commercial purposes.
 * 
 * This program and the accompanying materials are made available by the MIT
 * Technology Licensing Office under the terms of the MIT Java Wordnet Interface 
 * Non-Commercial License.  The MIT Technology Licensing Office can be reached 
 * at 617-253-6966 for further inquiry.
 *******************************************************************************/

package edu.mit.jwi.data.parse;

import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.SenseEntry;

/**
 * Basic implementation of an {@code ILineParser} that takes a line from a
 * Wordnet sense index file (e.g., index.sense or sense.index file) and produces an
 * {@code ISenseEntry} object.
 * <p>
 * This class follows a singleton design pattern, and is not intended to be
 * instantiated directly; rather, call the {@link #getInstance()} method to get
 * the singleton instance.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 10, 2007
 * @since 1.5.0
 */
public class SenseLineParser implements ILineParser<ISenseEntry> {
	
	private static char space = ' ';
	
	private static SenseLineParser fInstance;

	/**
	 * Returns the singleton instance of this class, instantiating it if
	 * necessary.
	 */
	public static SenseLineParser getInstance() {
		if (fInstance == null) fInstance = new SenseLineParser();
		return fInstance;
	}

	/**
	 * Obtain instances of this class via the static {@link #getInstance()}
	 * method. This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated.
	 */
	protected SenseLineParser() {}
	
	private ILineParser<ISenseKey> keyParser;
	
	protected ILineParser<ISenseKey> getSenseKeyParser(){
		if(keyParser == null) keyParser = SenseKeyParser.getInstance();
		return keyParser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.file.ILineParser#parseIndexLine(java.lang.String)
	 */
	public ISenseEntry parseLine(String line) {
		if (line == null) throw new MisformattedLineException(line);

		try {
			int begin = 0, end = 0;
			
			// get sense key
			end = line.indexOf(space, begin);
			String keyStr = line.substring(begin, end);
			ISenseKey sense_key = getSenseKeyParser().parseLine(keyStr);
			
			// get offset
			begin = end+1;
			end = line.indexOf(space, begin);
			int synset_offset = Integer.parseInt(line.substring(begin, end));
			
			// get sense number
			begin = end+1;
			end = line.indexOf(space, begin);
			int sense_number = Integer.parseInt(line.substring(begin, end));
			
			// get tag cnt
			begin = end+1;
			int tag_cnt = Integer.parseInt(line.substring(begin));
			
			return new SenseEntry(sense_key, synset_offset, sense_number, tag_cnt);
		} catch (Exception e) {
			throw new MisformattedLineException(line, e);
		}
	}
}