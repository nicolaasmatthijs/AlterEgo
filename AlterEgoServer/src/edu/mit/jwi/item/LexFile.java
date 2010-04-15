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
import java.util.List;
import java.util.Map;


/**
 * Concrete implementation of the {@link ILexFile} interface. This class in not
 * implemented as an {@code enum} so that clients may instantiate their own
 * {@link LexFile} objects using this implementation.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 10, 2008
 * @since 1.5.0
 */
public class LexFile implements ILexFile {
	
	public static final LexFile ADJ_ALL 			= new LexFile(0, 	"adj.all", 				"all adjective clusters", 								POS.ADJECTIVE);
	public static final LexFile ADJ_PERT 			= new LexFile(1, 	"adj.pert", 			"relational adjectives (pertainyms)", 					POS.ADJECTIVE);
	public static final LexFile ADV_ALL 			= new LexFile(2, 	"adv.all", 				"all adverbs", 											POS.ADVERB);
	public static final LexFile NOUN_TOPS 			= new LexFile(3, 	"noun.Tops", 			"unique beginner for nouns", 							POS.NOUN);
	public static final LexFile NOUN_ACT 			= new LexFile(4, 	"noun.act", 			"nouns denoting acts or actions", 						POS.NOUN);
	public static final LexFile NOUN_ANIMAL 		= new LexFile(5, 	"noun.animal", 			"nouns denoting animals", 								POS.NOUN);
	public static final LexFile NOUN_ARTIFACT 		= new LexFile(6, 	"noun.artifact", 		"nouns denoting man-made objects", 						POS.NOUN);
	public static final LexFile NOUN_ATTRIBUTE 		= new LexFile(7, 	"noun.attribute", 		"nouns denoting attributes of people and objects", 		POS.NOUN);
	public static final LexFile NOUN_BODY 			= new LexFile(8, 	"noun.body", 			"nouns denoting body parts", 							POS.NOUN);
	public static final LexFile NOUN_COGNITION 		= new LexFile(9, 	"noun.cognition", 		"nouns denoting cognitive processes and contents", 		POS.NOUN);
	public static final LexFile NOUN_COMMUNICATION 	= new LexFile(10,	"noun.communication", 	"nouns denoting communicative processes and contents", 	POS.NOUN);
	public static final LexFile NOUN_EVENT 			= new LexFile(11,	"noun.event", 			"nouns denoting natural events", 						POS.NOUN);
	public static final LexFile NOUN_FEELING 		= new LexFile(12,	"noun.feeling", 		"nouns denoting feelings and emotions", 				POS.NOUN);
	public static final LexFile NOUN_FOOD 			= new LexFile(13,	"noun.food", 			"nouns denoting foods and drinks", 						POS.NOUN);
	public static final LexFile NOUN_GROUP 			= new LexFile(14,	"noun.group", 			"nouns denoting groupings of people or objects", 		POS.NOUN);
	public static final LexFile NOUN_LOCATION 		= new LexFile(15,	"noun.location", 		"nouns denoting spatial position", 						POS.NOUN);
	public static final LexFile NOUN_MOTIVE 		= new LexFile(16,	"noun.motive", 			"nouns denoting goals", 								POS.NOUN);
	public static final LexFile NOUN_OBJECT 		= new LexFile(17,	"noun.object", 			"nouns denoting natural objects (not man-made)", 		POS.NOUN);
	public static final LexFile NOUN_PERSON 		= new LexFile(18,	"noun.person", 			"nouns denoting people", 								POS.NOUN);
	public static final LexFile NOUN_PHENOMENON		= new LexFile(19,	"noun.phenomenon", 		"nouns denoting natural phenomena", 					POS.NOUN);
	public static final LexFile NOUN_PLANT			= new LexFile(20,	"noun.plant", 			"nouns denoting plants", 								POS.NOUN);
	public static final LexFile NOUN_POSSESSION		= new LexFile(21,	"noun.possession", 		"nouns denoting possession and transfer of possession",	POS.NOUN);
	public static final LexFile NOUN_PROCESS		= new LexFile(22,	"noun.process", 		"nouns denoting natural processes", 					POS.NOUN);
	public static final LexFile NOUN_QUANTITY		= new LexFile(23,	"noun.quantity", 		"nouns denoting quantities and units of measure", 		POS.NOUN);
	public static final LexFile NOUN_RELATION		= new LexFile(24,	"noun.relation", 		"nouns denoting relations between people or things or ideas",	POS.NOUN);
	public static final LexFile NOUN_SHAPE			= new LexFile(25,	"noun.shape", 			"nouns denoting two and three dimensional shapes", 		POS.NOUN);
	public static final LexFile NOUN_STATE			= new LexFile(26,	"noun.state", 			"nouns denoting natural processes", 					POS.NOUN);
	public static final LexFile NOUN_SUBSTANCE		= new LexFile(27,	"noun.substance", 		"nouns denoting substances", 							POS.NOUN);
	public static final LexFile NOUN_TIME			= new LexFile(28,	"noun.time", 			"nouns denoting time and temporal relations", 			POS.NOUN);
	public static final LexFile VERB_BODY			= new LexFile(29,	"verb.body", 			"verbs of grooming, dressing and bodily care", 			POS.VERB);
	public static final LexFile VERB_CHANGE			= new LexFile(30,	"verb.change", 			"verbs of size, temperature change, intensifying, etc.",POS.VERB);
	public static final LexFile VERB_COGNITION		= new LexFile(31,	"verb.cognition", 		"verbs of thinking, judging, analyzing, doubting", 		POS.VERB);
	public static final LexFile VERB_COMMUNICATION	= new LexFile(32,	"verb.communication", 	"verbs of telling, asking, ordering, singing", 			POS.VERB);
	public static final LexFile VERB_COMPETITION	= new LexFile(33,	"verb.competition", 	"verbs of fighting, athletic activities", 				POS.VERB);
	public static final LexFile VERB_CONSUMPTION	= new LexFile(34,	"verb.consumption", 	"verbs of eating and drinking", 						POS.VERB);
	public static final LexFile VERB_CONTACT		= new LexFile(35,	"verb.contact", 		"verbs of touching, hitting, tying, digging", 			POS.VERB);
	public static final LexFile VERB_CREATION		= new LexFile(36,	"verb.creation", 		"verbs of sewing, baking, painting, performing", 		POS.VERB);
	public static final LexFile VERB_EMOTION		= new LexFile(37,	"verb.emotion", 		"verbs of feeling", 									POS.VERB);
	public static final LexFile VERB_MOTION			= new LexFile(38,	"verb.motion", 			"verbs of walking, flying, swimming", 					POS.VERB);
	public static final LexFile VERB_PERCEPTION		= new LexFile(39,	"verb.perception", 		"verbs of seeing, hearing, feeling", 					POS.VERB);
	public static final LexFile VERB_POSESSION		= new LexFile(40,	"verb.possession", 		"verbs of buying, selling, owning", 					POS.VERB);
	public static final LexFile VERB_SOCIAL			= new LexFile(41,	"verb.social", 			"verbs of political and social activities and events", 	POS.VERB);
	public static final LexFile VERB_STATIVE		= new LexFile(42,	"verb.stative", 		"verbs of being, having, spatial relations", 			POS.VERB);
	public static final LexFile VERB_WEATHER		= new LexFile(43,	"verb.weather", 		"verbs of raining, snowing, thawing, thundering", 		POS.VERB);
	public static final LexFile ADJ_PPL				= new LexFile(44,	"adj.ppl", 				"participial adjectives", 								POS.ADJECTIVE);
	
	private final POS pos;
	private final int num;
	private final String name, desc;
	
	public LexFile(int num, String name, String desc, POS pos){
		
		checkLexicalFileNumber(num);
		
		this.pos = pos;
		this.num = num;
		this.name = name;
		this.desc = desc;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ILexFile#getNumber()
	 */
	public int getNumber() {
		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
	public POS getPOS() {
		return pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ILexFile#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.ILexFile#getDescription()
	 */
	public String getDescription() {
		return desc;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
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
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + num;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	/* (non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ILexFile other = (ILexFile) obj;
		if (desc == null) {
			if (other.getDescription() != null) return false;
			
		}
		else if (!desc.equals(other.getDescription())) return false;
		if (name == null) {
			if (other.getName() != null) return false;
		}
		else if (!name.equals(other.getName())) return false;
		if (num != other.getNumber()) return false;
		if (pos == null) {
			if (other.getPOS() != null) return false;
		}
		else if (!pos.equals(other.getPOS())) return false;
		return true;
	}
	
	private static final String[] lexFileNumStrs = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};

	
	/**
	 * Checks the specified lexical file number to see if it is a valid lexical
	 * file number. If not, throws an exception.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified lexical file number is not a legal lexical
	 *             file number
	 */
	public static void checkLexicalFileNumber(int lexFileNum){
		if(isIllegalLexicalFileNumber(lexFileNum)) throw new IllegalArgumentException("'" + lexFileNum + " is an illegal lexical file number: Lexical file numbers must be in the closed range [0,99]");
	}
	
	/**
	 * A lexical file number is always in the closed range [0, 99]. In the
	 * wordnet data files, it is represented as a two digit decimal integer.
	 * 
	 * @return <code>true</code> if the number is an illegal lexical file
	 *         number; <code>false</code> otherwise.
	 */
	public static boolean isIllegalLexicalFileNumber(int lexFileNum) {
		return lexFileNum < 0 || 99 < lexFileNum;
	}

	/**
	 * A lexical file number is always in the closed range [0, 99]. In the
	 * wordnet data files, it is represented as a two digit decimal integer.
	 * This method returns a two-character, zero-filled decimal digit string
	 * representing the
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified integer is not a legal lexical file number
	 */
	public static String getLexicalFileNumberString(int lexFileNum){
		checkLexicalFileNumber(lexFileNum);
		return (lexFileNum < 10) ? lexFileNumStrs[lexFileNum] : Integer.toString(lexFileNum);
	}

	/** Internal static map for quick access to verb frames */
	private static final Map<Integer, LexFile> lexFileMap;
	
	static {
		
		// get instance fields
		Field[] fields = LexFile.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>();
		for(Field field : fields){
			if(field.getGenericType() == LexFile.class){
				instanceFields.add(field);
			}
		}
		
		// backing map
		Map<Integer, LexFile> hidden = new LinkedHashMap<Integer, LexFile>(instanceFields.size());
		
		// get instances
		LexFile lexFile;
		for(Field field : instanceFields){
			try{
				lexFile = (LexFile)field.get(null);
				if(lexFile == null) continue;
				hidden.put(lexFile.getNumber(), lexFile);
			} catch(IllegalAccessException e){
				// Ignore
			}
		}
		
		// make backing map unmodifiable
		lexFileMap = Collections.unmodifiableMap(hidden);
	}
	
	public static Collection<LexFile> values(){
		return lexFileMap.values();
	}
	
    /**
     * A convenience method that allows retrieval of the lexical file
     * given the number.  
     * 
     * @return ILexFile the lexical file corresponding to the
     *         specified tag, or null if none is found
     */
    public static LexFile getLexicalFile(int num) {
    	return lexFileMap.get(num);
    }



}
