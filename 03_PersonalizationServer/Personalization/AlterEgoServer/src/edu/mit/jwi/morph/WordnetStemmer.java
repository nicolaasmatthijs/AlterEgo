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

package edu.mit.jwi.morph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.POS;

/**
 * This stemmer adds functionality to the simple pattern-based stemmer
 * {@code SimpleStemmer} by checking to see if possible stems are actually
 * contained in Wordnet. If any stems are found, only these stems are returned.
 * If no prospective stems are found, the word is considered 'unknown', and the
 * result returned is the same as that of the {@code SimpleStemmer} class.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class WordnetStemmer extends SimpleStemmer {

    /**
     *  The dictionary used by this stemmer
     */
    IDictionary fDictionary;

    /**
     * Constructs a WordnetStemmer that, naturally, requires a Wordnet
     * dictionary.
     */
    public WordnetStemmer(IDictionary dictionary) {
        assert dictionary != null;
        fDictionary = dictionary;
    }



    /*
     *  (non-Javadoc)
     * 
     * @see edu.mit.wordnet.morph.SimpleStemmer#getRoots(java.lang.String,
     *      edu.mit.wordnet.data.PartOfSpeech)
     */
    public List<String> findStems(String word, POS pos) {
    	
    	if(pos == null) return findStems(word);

        word = normalize(word);
        SortedSet<String> result = new TreeSet<String>();
        
        // first look for the word in the exception lists
        IExceptionEntry entry = fDictionary.getExceptionEntry(word, pos);
        boolean isException = false;
        if (entry != null){
        	isException = true;
        	result.addAll(entry.getRootForms());
        }

        // then look and see if it's in Wordnet; if so, the form itself is a stem
        if (fDictionary.getIndexWord(word, pos) != null) result.add(word);
        
        if(isException) return new ArrayList<String>(result);

        // go to the simple stemmer and check and see if any of those stems are in WordNet
        List<String> possibles = super.findStems(word, pos);
        
        // check each algorithmically obtained root to see if it's in WordNet
        for (String possible : possibles) {
            if(fDictionary.getIndexWord(possible, pos) != null){
                if (result == null) result = new TreeSet<String>();
                result.add(possible);
            }
        }

        // if there are no exceptions,  
        if(result.isEmpty() && possibles.isEmpty()) return Collections.<String>emptyList();
        return result.isEmpty()? new ArrayList<String>(possibles) : new ArrayList<String>(result);
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.wordnet.morph.SimpleStemmer#getRoots(java.lang.String)
	 */
	public List<String> findStems(String word) {
        SortedSet<String> result = new TreeSet<String>();
        for (POS pos : POS.values()) result.addAll(findStems(word, pos));
        return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);
    }
}