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
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.data.compare.DataLineComparator;
import edu.mit.jwi.data.compare.ExceptionLineComparator;
import edu.mit.jwi.data.compare.ILineComparator;
import edu.mit.jwi.data.compare.IndexLineComparator;
import edu.mit.jwi.data.compare.SenseKeyLineComparator;
import edu.mit.jwi.item.IExceptionEntryProxy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.POS;

/**
 * A concrete implementation of the {@code IContentType} interface. This class
 * provides the content types necessary for Wordnet in the form of static
 * fields. It is not implemented as an {@code enum} so that clients may add
 * their own content types by instantiating this class.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class ContentType<T> implements IContentType<T> {

	public static final ContentType<IIndexWord> 			INDEX_NOUN 				= new ContentType<IIndexWord>(				DataType.INDEX, 		POS.NOUN, 		IndexLineComparator.getInstance());
	public static final ContentType<IIndexWord> 			INDEX_VERB 				= new ContentType<IIndexWord>(				DataType.INDEX, 		POS.VERB, 		IndexLineComparator.getInstance());
	public static final ContentType<IIndexWord> 			INDEX_ADVERB 			= new ContentType<IIndexWord>(				DataType.INDEX, 		POS.ADVERB, 	IndexLineComparator.getInstance());
	public static final ContentType<IIndexWord> 			INDEX_ADJECTIVE 		= new ContentType<IIndexWord>(				DataType.INDEX,			POS.ADJECTIVE, 	IndexLineComparator.getInstance());
	public static final ContentType<ISynset> 				DATA_NOUN 				= new ContentType<ISynset>(					DataType.DATA, 			POS.NOUN, 		DataLineComparator.getInstance());
	public static final ContentType<ISynset> 				DATA_VERB 				= new ContentType<ISynset>(					DataType.DATA, 			POS.VERB, 		DataLineComparator.getInstance());
	public static final ContentType<ISynset> 				DATA_ADVERB 			= new ContentType<ISynset>(					DataType.DATA, 			POS.ADVERB, 	DataLineComparator.getInstance());
	public static final ContentType<ISynset> 				DATA_ADJECTIVE 			= new ContentType<ISynset>(					DataType.DATA, 			POS.ADJECTIVE, 	DataLineComparator.getInstance());
	public static final ContentType<IExceptionEntryProxy> 	EXCEPTION_NOUN 			= new ContentType<IExceptionEntryProxy>(	DataType.EXCEPTION, 	POS.NOUN, 		ExceptionLineComparator.getInstance());
	public static final ContentType<IExceptionEntryProxy> 	EXCEPTION_VERB 			= new ContentType<IExceptionEntryProxy>(	DataType.EXCEPTION, 	POS.VERB, 		ExceptionLineComparator.getInstance());
	public static final ContentType<IExceptionEntryProxy> 	EXCEPTION_ADVERB 		= new ContentType<IExceptionEntryProxy>(	DataType.EXCEPTION, 	POS.ADVERB, 	ExceptionLineComparator.getInstance());
	public static final ContentType<IExceptionEntryProxy> 	EXCEPTION_ADJECTIVE 	= new ContentType<IExceptionEntryProxy>(	DataType.EXCEPTION, 	POS.ADJECTIVE,	ExceptionLineComparator.getInstance());
	public static final ContentType<ISenseEntry> 			SENSE 					= new ContentType<ISenseEntry>(				DataType.SENSE, 		null,			SenseKeyLineComparator.getInstance());

	private final IDataType<T> fType;
	private final POS fPOS;
	private final ILineComparator fComparator;
	private final String fString;

	/**
	 * Constructs a new ContentType
	 */
	public ContentType(IDataType<T> type, POS pos, ILineComparator comparator) {
		fType = type;
		fPOS = pos;
		fComparator = comparator;

		if (pos != null) {
			fString = "[ContentType: " + fType.toString() + "/" + fPOS.toString() + "]";
		}
		else {
			fString = "[ContentType: " + fType.toString() + "]";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IContentType#getDataType()
	 */
	public IDataType<T> getDataType() {
		return fType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasPOS#getPOS()
	 */
	public POS getPOS() {
		return fPOS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IContentType#getLineComparator()
	 */
	public ILineComparator getLineComparator() {
		return fComparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fString;
	}
	
	private static final Set<ContentType<?>> contentTypes;
	
	static {
		
		// get all the fields containing ContentType
		Field[] fields = ContentType.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() instanceof ParameterizedType && 
			   ((ParameterizedType)field.getGenericType()).getRawType() == ContentType.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Set<ContentType<?>> hidden = new LinkedHashSet<ContentType<?>>(instanceFields.size());
		
		// fill in the backing set
		ContentType<?> contentType;
		for(Field field : instanceFields){
			try{
				contentType = (ContentType<?>)field.get(null);
				if(contentType == null) continue;
				hidden.add(contentType);
			} catch(IllegalAccessException e){
				// Ignore
			}
		}
		
		// make the value set unmodifiable
		contentTypes = Collections.unmodifiableSet(hidden);
	}
	
	/**
	 * Emulates the Enum.values() function.
	 * 
	 * @return all the static ContentType instances listed in the class, in the
	 *         order they are declared.
	 */
	public static Collection<ContentType<?>> values(){
		return contentTypes;
	}

	/**
	 * Use this convenience method to retrieve the appropriate
	 * {@code IIndexWord} content type for the specified POS.
	 */
	public static IContentType<IIndexWord> getIndexContentType(POS pos) {
		if (pos == POS.NOUN) return INDEX_NOUN;
		if (pos == POS.VERB) return INDEX_VERB;
		if (pos == POS.ADVERB) return INDEX_ADVERB;
		return INDEX_ADJECTIVE;
	}

	/**
	 * Use this convenience method to retrieve the appropriate {@code ISynset}
	 * content type for the specified POS.
	 */
	public static IContentType<ISynset> getDataContentType(POS pos) {
		if (pos == POS.NOUN) return DATA_NOUN;
		if (pos == POS.VERB) return DATA_VERB;
		if (pos == POS.ADVERB) return DATA_ADVERB;
		return DATA_ADJECTIVE;
	}

	/**
	 * Use this convenience method to retrieve the appropriate
	 * {@code IExceptionEntryProxy} content type for the specified POS.
	 */
	public static IContentType<IExceptionEntryProxy> getExceptionContentType(POS pos) {
		if (pos == POS.NOUN) return EXCEPTION_NOUN;
		if (pos == POS.VERB) return EXCEPTION_VERB;
		if (pos == POS.ADVERB) return EXCEPTION_ADVERB;
		return EXCEPTION_ADJECTIVE;
	}


}