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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents part of speech objects.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public enum POS {
	
	NOUN		("noun", 		'n', 1,	"noun"),
    VERB		("verb", 		'v', 2, "verb"),
    ADJECTIVE	("adjective", 	'a', 3, "adj", "adjective"),
    ADVERB		("adverb", 		'r', 4, "adv", "adverb");
	
	public static final int NUM_NOUN = 1;
	public static final int NUM_VERB = 2;
	public static final int NUM_ADJECTIVE = 3;
	public static final int NUM_ADVERB = 4;
	public static final int NUM_ADJECTIVE_SATELLITE = 5;
	
	public static final char TAG_NOUN = 'n';
	public static final char TAG_VERB = 'v';
	public static final char TAG_ADJECTIVE = 'a';
	public static final char TAG_ADVERB = 'r';
	public static final char TAG_ADJECTIVE_SATELLITE = 's';

	final String fName;
	final char fTag;
	final int fNum;
    final Set<String> fHints;

    private POS(String name, char tag, int type, String... patterns) {
    	fName = name;
    	fTag = tag;
    	fNum = type;
    	fHints = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(patterns)));
    }
    
    /**
     * Returns a set of strings that can be used to identify resource
     * corresponding to objects with this part of speech.
     * 
     * @return String[]
     */
    public Set<String> getResourceNameHints() {
        return fHints;
    }

    /**
     * The tag that is used to indicate this part of speech in Wordnet data
     * files
     * 
     * @return char
     */
    public char getTag() {
    	return fTag;
    }
    
    /** Returns the synset type of this part of speech
     */
    public int getNumber(){
    	return fNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fName;
    }
    
    /**
	 * @return <code>true</code> if the specified number represents an
	 *         adjective satellite, namely, if the number is 5;
	 *         <code>false</code> otherwise.
	 */
    public static boolean isAdjectiveSatellite(int num){
    	return num == 5;
    }
    
    /**
	 * @return <code>true</code> if the specified character represents an
	 *         adjective satellite, namely, if the character is 's' or 'S';
	 *         <code>false</code> otherwise.
	 */
    public static boolean isAdjectiveSatellite(char tag){
    	return tag == 's' || tag == 'S';
    }
    
    /**
     * A convenience method that allows retrieval of the part of speech object
     * given the number.  Accepts both lower and upper case characters.
     * 
     * @return POS the part of speech object corresponding to the
     *         specified tag, or null if none is found
     */
    public static POS getPartOfSpeech(int num) {
    	switch(num){
    	case(1): return NOUN;
    	case(2): return VERB;
    	case(4): return ADVERB;
    	case(5): // special case, '5' for adjective satellite, fall through
    	case(3): return ADJECTIVE;
    	}
        return null;
    }

    /**
     * A convenience method that allows retrieval of the part of speech object
     * given the tag.  Accepts both lower and upper case characters.
     * 
     * @return POS the part of speech object corresponding to the
     *         specified tag, or null if none is found
     */
    public static POS getPartOfSpeech(char tag) {
    	switch(tag){
    	case('N'): // capital, fall through
    	case('n'): return NOUN;
    	case('V'): // capital, fall through
    	case('v'): return VERB;
    	case('R'): // capital, fall through
    	case('r'): return ADVERB;
    	case('s'): // special case, 's' for adjective satellite, fall through
    	case('S'): // capital, fall through
    	case('A'): // capital, fall through
    	case('a'): return ADJECTIVE;
    	}
        return null;
    }
}