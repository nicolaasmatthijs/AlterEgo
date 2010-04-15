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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.data.IContentType;
import edu.mit.jwi.data.WordnetFile;
import edu.mit.jwi.data.compare.ICommentDetector;


/**
 * Default, concrete implementation of the {@link IVersion} interface. This
 * class, much like the {@link Integer} class, caches instances which are
 * created via the {@link #createVersion(int, int, int)} method.
 * <p>
 * Clients should use the {@link #createVersion(int, int, int)} method to
 * instantiate objects of this class.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 22, 2008
 * @since 1.5.0
 */
public class Version implements IVersion {
	
	// only create one instance of any version
	private static final Map<Integer, Version> versionCache = new HashMap<Integer, Version>();
	
	public static final Version ver16 = createVersion(1,6,0); 
	public static final Version ver17 = createVersion(1,7,0); 
	public static final Version ver171 = createVersion(1,7,1); 
	public static final Version ver20 = createVersion(2,0,0); 
	public static final Version ver21 = createVersion(2,1,0); 
	public static final Version ver30 = createVersion(3,0,0); 
	
	public static List<Version> versions = Arrays.asList(new Version[]{ver16, ver17, ver171, ver20, ver21, ver30});
	
	public static final int versionOffset = 803;

	private final int major, minor, bugfix;
	private String toString;
	
	/**
	 * Clients should not instantiate this class directly, but rather
	 * use the {@link #createVersion(int, int, int)} method.
	 */
	protected Version(int major, int minor, int bugfix){
		checkVersionNumber(major, minor, bugfix);
		
		this.major = major;
		this.minor = minor;
		this.bugfix = bugfix;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IVersion#getMajorVersion()
	 */
	public int getMajorVersion() {
		return major;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IVersion#getMinorVersion()
	 */
	public int getMinorVersion() {
		return minor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IVersion#getBugfixVersion()
	 */
	public int getBugfixVersion() {
		return bugfix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode(major, minor, bugfix);
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
		if (!(obj instanceof IVersion)) return false;
		final IVersion other = (IVersion) obj;
		if (bugfix != other.getBugfixVersion()) return false;
		if (major != other.getMajorVersion()) return false;
		if (minor != other.getMinorVersion()) return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (toString == null) toString = makeVersionString(major, minor, bugfix);
		return toString;
	}
	
	/** 
	 * Checks the supplied version numbers.  Throws an {@link IllegalArgumentException} if 
	 * the version numbers are not valid (that is, any are below zero).
	 */
	public static void checkVersionNumber(int major, int minor, int bugfix){
		if(isIllegalVersionNumber(major, minor, bugfix)) throw new IllegalArgumentException("Illegal version number: " + makeVersionString(major, minor, bugfix));
	}
	
	
	/** 
	 * Returns true if this confluence of three numbers constitutes an illegal version
	 * number, namely, if any of the values are negative.
	 */
	public static boolean isIllegalVersionNumber(int major, int minor, int bugfix){
		return major < 0 || minor < 0 || bugfix < 0;
	}
	
	/** 
	 * Creates and caches, or retrieves from the cache, a version object corresponding
	 * to the specified numbers.
	 */
	public static Version createVersion(int major, int minor, int bugfix){
		checkVersionNumber(major, minor, bugfix);
		int hash = hashCode(major, minor, bugfix);
		Version version = versionCache.get(hash);
		if(version == null){
			version = new Version(major, minor, bugfix);
			versionCache.put(version.hashCode(), version);
		}
		return version;
	}

	private static final String point = ".";
	private static final Pattern periodPattern = Pattern.compile("\\Q" + point + "\\E");
	private static final String wordnetStr = "WordNet", copyrightStr = "Copyright";
	private static final Pattern versionPattern = Pattern.compile("WordNet\\s+\\d+\\Q.\\E\\d+(\\Q.\\E\\d+)?\\s+Copyright");
	
	/** 
	 * Creates a version string for the specified version numbers. 
	 */
	public static String makeVersionString(int major, int minor, int bugfix){
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(major));
		sb.append(point);
		sb.append(Integer.toString(minor));
		if(bugfix > 0){
			sb.append(point);
			sb.append(Integer.toString(bugfix));
		}
		return sb.toString();
	}
	
	/** 
	 * Calculates the hash code for a version object with the specified version numbers.
	 */
	public static int hashCode(int major, int minor, int bugfix){
		final int prime = 31;
		int result = 1;
		result = prime * result + bugfix;
		result = prime * result + major;
		result = prime * result + minor;
		return result;
	}
	

	/** 
	 * Extracts a version object from a byte buffer that contains data
	 * with the specified content type.  If no version can be extract, returns
	 * <code>null</code>.
	 */
	public static Version extractVersion(IContentType<?> type, ByteBuffer buffer){
		if(!type.getDataType().hasVersion()) return null;
		
		// first try direct access
		char c;
		StringBuilder sb = new StringBuilder();
		for(int i = versionOffset; i < buffer.limit(); i++){
			c = (char)buffer.get(i);
			if(Character.isWhitespace(c)) break;
			sb.append(c);
		}
		Version version = parseVersion(sb);
		if(version != null) return version;
		
		// if direct access doesn't work, try walking forward in file
		// until we find a string that looks like "WordNet 1.2 Copyright"
		
		
		ICommentDetector cd = type.getLineComparator().getCommentDetector();
		if(cd == null) return null;
		
		int origPos = buffer.position();

		String line = null;
		Matcher m;
		while(buffer.position() < buffer.limit()){
			line = WordnetFile.getLine(buffer);
			if(line == null || !cd.isCommentLine(line)){
				line = null;
				break;
			}
			m = versionPattern.matcher(line);
			if(m.find()){
				line = m.group();
				int start = wordnetStr.length();
				int end = line.length()-copyrightStr.length();
				line = line.substring(start, end);
				break;
			}
		}
		buffer.position(origPos);
		return parseVersion(line);
	}
	
	/** 
	 * Tries to transform the specified character sequence
	 * into a version object.  Returns <code>null</code> if
	 * unable to do so.
	 */
	public static Version parseVersion(CharSequence verStr){
		if(verStr == null) return null;
		String[] parts = periodPattern.split(verStr);
		
		if(parts.length < 2 || parts.length > 3) return null;
		
		try{
			int major = Integer.parseInt(parts[0].trim());
			int minor = Integer.parseInt(parts[1].trim());
			int bugfix = (parts.length < 3) ? 0 : Integer.parseInt(parts[2].trim());
			if(isIllegalVersionNumber(major, minor, bugfix)) return null;
			return createVersion(major, minor, bugfix);
		} catch(NumberFormatException e){
			return null;
		}
	}


}
