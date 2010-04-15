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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.data.parse.DataLineParser;
import edu.mit.jwi.data.parse.ExceptionLineParser;
import edu.mit.jwi.data.parse.ILineParser;
import edu.mit.jwi.data.parse.IndexLineParser;
import edu.mit.jwi.data.parse.SenseLineParser;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISynset;

/**
 * A basic implementation of the {@code IDataType} interface. This class
 * provides the data types necessary for basic use of Wordnet in the form
 * of static fields.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class DataType<T> implements IDataType<T> {
	
	public static final DataType<IIndexWord> 			INDEX 		= new DataType<IIndexWord>(				"Index", 	 true,	IndexLineParser.getInstance(),		"index", "idx");
	public static final DataType<ISynset> 				DATA 		= new DataType<ISynset>(			 	"Data", 	 true, 	DataLineParser.getInstance(), 		"data", "dat");
	public static final DataType<IExceptionEntryProxy> 	EXCEPTION 	= new DataType<IExceptionEntryProxy>( 	"Exception", false,	ExceptionLineParser.getInstance(),	"exception", "exc");
	public static final DataType<ISenseEntry> 			SENSE 		= new DataType<ISenseEntry>( 			"Sense", 	 false,	SenseLineParser.getInstance(),		"sense");

    private final String name;
    private final Set<String> hints;
    private final boolean hasVersion;
    private final ILineParser<T> parser;

    public DataType(String userFriendlyName, boolean hasVersion, ILineParser<T> parser, String... hints) {
    	this(userFriendlyName, hasVersion, parser, (hints == null) ? null : Arrays.asList(hints));
    }
    
    public DataType(String userFriendlyName, boolean hasVersion, ILineParser<T> parser, Collection<String> hints) {
    	if(parser == null) throw new NullPointerException();
        this.name = userFriendlyName;
        this.parser = parser;
        this.hasVersion = hasVersion;
        this.hints = (hints == null || hints.isEmpty()) ? Collections.<String>emptySet() : Collections.unmodifiableSet(new HashSet<String>(hints));
    }
    
    /* (non-Javadoc) @see edu.mit.jwi.data.IDataType#hasVersion() */
    public boolean hasVersion(){
    	return hasVersion;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataType#getResourceNameHints()
	 */
    public Set<String> getResourceNameHints() {
        return hints;
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataType#getParser()
	 */
    public ILineParser<T> getParser(){
    	return parser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }
    
	private static final Set<DataType<?>> dataTypes;
	
	static {
		
		// get all the fields containing ContentType
		Field[] fields = DataType.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == DataType.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Set<DataType<?>> hidden = new LinkedHashSet<DataType<?>>(instanceFields.size());
		
		// fill in the backing set
		DataType<?> dataType;
		for(Field field : instanceFields){
			try{
				dataType = (DataType<?>)field.get(null);
				if(dataType == null) continue;
				hidden.add(dataType);
			} catch(IllegalAccessException e){
				// Ignore
			}
		}
		
		// make the value set unmodifiable
		dataTypes = Collections.unmodifiableSet(hidden);
	}
	
	/**
	 * Emulates the Enum.values() function.
	 * 
	 * @return all the static DataType instances listed in the class, in the
	 *         order they are declared.
	 */
	public static Collection<DataType<?>> values(){
		return dataTypes;
	}
}