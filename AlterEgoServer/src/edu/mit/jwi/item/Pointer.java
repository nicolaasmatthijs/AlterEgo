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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default, hard-coded, implementation of {@code IPointer} for Wordnet. This
 * class is not implemented as an enum so that clients can instantiate this
 * class with their own, custom pointer types, or create their own subclasses.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class Pointer implements IPointer {
    
	public static final Pointer ALSO_SEE 				= new Pointer("^", 	"Also See");
	public static final Pointer	ANTONYM 				= new Pointer("!", 	"Antonym");
	public static final Pointer	ATTRIBUTE 				= new Pointer("=", 	"Attribute");
	public static final Pointer	CAUSE 					= new Pointer(">", 	"Cause");
	public static final Pointer DERIVATIONALLY_RELATED 	= new Pointer("+", 	"Derivationally related form");
	public static final Pointer DERIVED_FROM_ADJ		= new Pointer("\\", "Derived from adjective");
	public static final Pointer ENTAILMENT 				= new Pointer("*", 	"Entailment");
	public static final Pointer HYPERNYM 				= new Pointer("@", 	"Hypernym");
	public static final Pointer HYPERNYM_INSTANCE 		= new Pointer("@i", "Instance hypernym");
	public static final Pointer HYPONYM 				= new Pointer("~", 	"Hyponym");
	public static final Pointer HYPONYM_INSTANCE 		= new Pointer("~i", "Instance hyponym");
	public static final Pointer HOLONYM_MEMBER 			= new Pointer("#m", "Member holonym");
	public static final Pointer HOLONYM_SUBSTANCE 		= new Pointer("#s", "Substance holonym");
	public static final Pointer HOLONYM_PART 			= new Pointer("#p", "Part holonym");
	public static final Pointer MERONYM_MEMBER 			= new Pointer("%m", "Member meronym");
	public static final Pointer MERONYM_SUBSTANCE 		= new Pointer("%s", "Substance meronym");
	public static final Pointer MERONYM_PART 			= new Pointer("%p", "Part meronym");
	public static final Pointer PARTICIPLE 				= new Pointer("<", 	"Participle");
	public static final Pointer PERTAINYM 				= new Pointer("\\", "Pertainym (pertains to nouns)");
	public static final Pointer REGION 					= new Pointer(";r", "Domain of synset - REGION");
	public static final Pointer REGION_MEMBER 			= new Pointer("-r", "Member of this domain - REGION");
	public static final Pointer SIMILAR_TO 				= new Pointer("&", 	"Similar To");
	public static final Pointer TOPIC 					= new Pointer(";c", "Domain of synset - TOPIC");
	public static final Pointer TOPIC_MEMBER 			= new Pointer("-c", "Member of this domain - TOPIC");
	public static final Pointer USAGE 					= new Pointer(";u", "Domain of synset - USAGE");
	public static final Pointer USAGE_MEMBER 			= new Pointer("-u", "Member of this domain - USAGE");
	public static final Pointer VERB_GROUP 				= new Pointer("$", 	"Verb Group");
	
	private final String fSymbol;
    private final String fName;

    /**
     * Private constructor because this is an enum type.
     */
    public Pointer(String symbol, String name) {
        fSymbol = symbol;
        fName = name;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IPointer#getSymbol()
	 */
    public String getSymbol() {
        return fSymbol;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IPointer#getName()
	 */
    public String getName() {
        return fName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fName.toLowerCase().replace(' ', '_').replace(",", "");
    }

    /**
     * Static map to speed up access
     */
    private static final Map<String, Pointer> pointerMap;
    
    /**
     * All the pointers
     */
    private static final Set<Pointer> pointerSet;
    
    static {

		// get the instance fields
		Field[] fields = Pointer.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>();
		for(Field field : fields){
			if(field.getGenericType() == Pointer.class){
				instanceFields.add(field);
			}
		}
		
		// these are our backing collections
		Set<Pointer> hiddenSet = new LinkedHashSet<Pointer>(instanceFields.size());
		Map<String, Pointer> hiddenMap = new LinkedHashMap<String, Pointer>(instanceFields.size()-1);
		
		Pointer ptr;
		for(Field field : instanceFields){
			try{
				ptr = (Pointer)field.get(null);
				if(ptr == null) continue;
				hiddenSet.add(ptr);
				if(ptr != DERIVED_FROM_ADJ) hiddenMap.put(ptr.getSymbol(), ptr);
			} catch(IllegalAccessException e){
				// Ignore
			}
		}
		
		// make the collections unmodifiable
		pointerSet = Collections.unmodifiableSet(hiddenSet);
		pointerMap = Collections.unmodifiableMap(hiddenMap);
    }
    
	/**
	 * Emulates the Enum.values() function. Returns an unmodifiable collection
	 * of all the pointers declared in this class, in the order they are
	 * declared.
	 */
	public static Collection<Pointer> values(){
		return pointerSet;
	}

	private static final String ambiguousSymbol = "\\";
	
    /**
	 * Returns the pointer type (static final instance) that matches the
	 * specified pointer symbol.
	 * 
	 * @throws IllegalArgumentException
	 *             if the symbol does not correspond to a known pointer.
	 */
    public static Pointer getPointerType(String symbol, POS pos) {
    	if(pos == POS.ADVERB && symbol.equals(ambiguousSymbol)) return DERIVED_FROM_ADJ;
        Pointer pointerType = pointerMap.get(symbol);
        if (pointerType == null) throw new IllegalArgumentException(
                    "No pointer type corresponding to symbol '" + symbol + "'");
        return pointerType;
    }
}
