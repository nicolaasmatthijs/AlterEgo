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

package edu.mit.jwi.item;


/**
 * Concrete, default implementation of the {@link ISenseEntry} interface.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 13, 2008
 * @since 1.5.0
 */
public class SenseEntry implements ISenseEntry {
	
	private final int offset, num, cnt;
	private final ISenseKey key;
	
	public SenseEntry(ISenseKey key, int offset, int num, int cnt){
		if(key == null) throw new NullPointerException();
		Synset.checkOffset(offset);
		
		this.key = key;
		this.offset = offset;
		this.num = num;
		this.cnt = cnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseEntry#getOffset()
	 */
	public int getOffset() {
		return offset;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
	public POS getPOS() {
		return key.getPOS();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseEntry#getSenseNumber()
	 */
	public int getSenseNumber() {
		return num;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseEntry#getSenseKey()
	 */
	public ISenseKey getSenseKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ISenseEntry#getTagCount()
	 */
	public int getTagCount() {
		return cnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cnt;
		result = prime * result + key.hashCode();
		result = prime * result + num;
		result = prime * result + offset;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ISenseEntry)) return false;
		final ISenseEntry other = (ISenseEntry) obj;
		if (cnt != other.getTagCount()) return false;
		if (num != other.getSenseNumber()) return false;
		if (offset != other.getOffset()) return false;
		return key.equals(other.getSenseKey());
	}



}
