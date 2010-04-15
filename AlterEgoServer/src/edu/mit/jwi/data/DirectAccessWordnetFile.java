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

package edu.mit.jwi.data;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Basic implementation of the {@code IDataSource} interface, intended for
 * use with the Wordnet distributions.  This particular type of data source
 * is for files on disk, and directly accesses the appropriate byte offset in the file
 * to find requested lines. It is appropriate for Wordnet data files.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class DirectAccessWordnetFile<T> extends WordnetFile<T> {

	public DirectAccessWordnetFile(File file, IContentType<T> contentType) throws IOException {
		super(file, contentType);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.core.file.IDictionaryFile#getLine(java.lang.String)
	 */
	public String getLine(String key) {
		synchronized(fBuffer){
			try{
				Integer byteOffset = Integer.parseInt(key);
				if(fBuffer.limit() <= byteOffset) return null; 
				fBuffer.position(byteOffset);
				String line = getLine(fBuffer);
				return line.startsWith(key) ? line : null;
			} catch(NumberFormatException e){
				return null;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.WordnetFile#iterator()
	 */
	public Iterator<String> iterator() {
		return new DirectLineIterator(fMappedBuffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.WordnetFile#iterator(java.lang.String)
	 */
	public Iterator<String> iterator(String key) {
		return new DirectLineIterator(fMappedBuffer, key);
	}

	/**
	 * Used to iterate over lines in a file. It is a look-ahead iterator.
	 */
	public class DirectLineIterator extends LineIterator {

		ByteBuffer fMyBuffer;
		String previous, next;

		public DirectLineIterator(ByteBuffer file) {
			this(file, null);
		}

		public DirectLineIterator(ByteBuffer buffer, String key) {
			super(buffer, key);
		}

		protected void findFirstLine(String key){
			synchronized(fMyBuffer){
				try{
					Integer byteOffset = Integer.parseInt(key);
					if(fBuffer.limit() <= byteOffset) return; 
					fMyBuffer.position(byteOffset);
					next = getLine(fMyBuffer);
				} catch(NumberFormatException e){
					// Ignore
				}
			}
		}
	}
}