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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import edu.mit.jwi.item.POS;

/**
 * Provides simple a simple pattern-based stemming facility based on the "Rules
 * of Detachment" as described in the {@code morphy} man page in the Wordnet
 * distribution, which can be found at <a
 * href="http://wordnet.princeton.edu/man/morphy.7WN.html">
 * http://wordnet.princeton.edu/man/morphy.7WN.html</a> It also attempts to
 * strip "ful" endings. It does not search Wordnet to see if stems actually
 * exist. In particular, quoting from that man page:
 * <p>
 * <h3>Rules of Detachment</h3>
 * <p>
 * The following table shows the rules of detachment used by Morphy. If a word
 * ends with one of the suffixes, it is stripped from the word and the
 * corresponding ending is added. ... No rules are applicable to adverbs.
 * <p>
 * POS Suffix Ending<br>
 * <ul>
 * <li>NOUN "s" ""
 * <li>NOUN "ses" "s"
 * <li>NOUN "xes" "x"
 * <li>NOUN "zes" "z"
 * <li>NOUN "ches" "ch"
 * <li>NOUN "shes" "sh"
 * <li>NOUN "men" "man"
 * <li>NOUN "ies" "y"
 * <li>VERB "s" ""
 * <li>VERB "ies" "y"
 * <li>VERB "es" "e"
 * <li>VERB "es" ""
 * <li>VERB "ed" "e"
 * <li>VERB "ed" ""
 * <li>VERB "ing" "e"
 * <li>VERB "ing" ""
 * <li>ADJ "er" ""
 * <li>ADJ "est" ""
 * <li>ADJ "er" "e"
 * <li>ADJ "est" "e"
 * </ul>
 * <p>
 * <h3>Special Processing for nouns ending with 'ful'</h3>
 * <p>
 * Morphy contains code that searches for nouns ending with ful and performs a
 * transformation on the substring preceding it. It then appends 'ful' back
 * onto the resulting string and returns it. For example, if passed the nouns
 * boxesful, it will return boxful.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5
 */
public class SimpleStemmer implements IStemmer {

	public static final String underscore = "_";
	Pattern whitespace = Pattern.compile("\\s+");

	public static final String SUFFIX_ches = "ches";
	public static final String SUFFIX_ed = "ed";
	public static final String SUFFIX_es = "es";
	public static final String SUFFIX_est = "est";
	public static final String SUFFIX_er = "er";
	public static final String SUFFIX_ful = "ful";
	public static final String SUFFIX_ies = "ies";
	public static final String SUFFIX_ing = "ing";
	public static final String SUFFIX_men = "men";
	public static final String SUFFIX_s = "s";
	public static final String SUFFIX_ses = "ses";
	public static final String SUFFIX_shes = "shes";
	public static final String SUFFIX_xes = "xes";
	public static final String SUFFIX_zes = "zes";

	public static final String ENDING_null = "";
	public static final String ENDING_ch = "ch";
	public static final String ENDING_e = "e";
	public static final String ENDING_man = "man";
	public static final String ENDING_s = SUFFIX_s;
	public static final String ENDING_sh = "sh";
	public static final String ENDING_x = "x";
	public static final String ENDING_y = "y";
	public static final String ENDING_z = "z";

	String[][] nounMappings = new String[][] { 
			new String[] { SUFFIX_s, ENDING_null },
			new String[] { SUFFIX_ses, ENDING_s }, 
			new String[] { SUFFIX_xes, ENDING_x },
			new String[] { SUFFIX_zes, ENDING_z }, 
			new String[] { SUFFIX_ches, ENDING_ch },
			new String[] { SUFFIX_shes, ENDING_sh }, 
			new String[] { SUFFIX_men, ENDING_man },
			new String[] { SUFFIX_ies, ENDING_y }, };

	String[][] verbMappings = new String[][] { 
			new String[] { SUFFIX_s, ENDING_null },
			new String[] { SUFFIX_ies, ENDING_y }, 
			new String[] { SUFFIX_es, ENDING_e },
			new String[] { SUFFIX_es, ENDING_null }, 
			new String[] { SUFFIX_ed, ENDING_e },
			new String[] { SUFFIX_ed, ENDING_null }, 
			new String[] { SUFFIX_ing, ENDING_e },
			new String[] { SUFFIX_ing, ENDING_null }, };

	String[][] adjMappings = new String[][] { 
			new String[] { SUFFIX_er, ENDING_e },
			new String[] { SUFFIX_er, ENDING_null }, 
			new String[] { SUFFIX_est, ENDING_e },
			new String[] { SUFFIX_est, ENDING_null }, };

	/**
	 * Returns a set of possible roots for the specified word, considered as
	 * being a member of the specified part of speech. The Set is sorted in
	 * String order. If no roots are found, this method returns the empty list.
	 * 
	 * @throws NullPointerException
	 *             if the specified string or part of speech is {@code null}
	 */
	public List<String> findStems(String word, POS pos) {
		
		if(pos == null) return findStems(word);

		word = normalize(word);
		boolean isCollocation = word.contains(underscore);

		if (pos == POS.NOUN) {
			return isCollocation ? getNounCollocationRoots(word) : stripNounSuffix(word);
		} else if (pos == POS.VERB) {
			// BUG006: here we check for composites
			return isCollocation ? getVerbCollocationRoots(word) : stripVerbSuffix(word);
			// =====================================
		} else if (pos == POS.ADJECTIVE) { 
			return stripAdjectiveSuffix(word); 
		}
		
		// nothing for adverbs
		return Collections.<String>emptyList();
	}
	
	public List<String> findStems(String word) {

		word = normalize(word);

		SortedSet<String> result = new TreeSet<String>();
		List<String> roots;

		roots = stripNounSuffix(word);
		if (roots != null) result.addAll(roots);

		roots = stripVerbSuffix(word);
		if (roots != null) result.addAll(roots);

		roots = stripAdjectiveSuffix(word);
		if (roots != null) result.addAll(roots);

		return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);

	}

	/**
	 * Converts all whitespace runs to single underscores.  Tests first
	 * to see if there is any whitespace before converting.
	 */
	protected String normalize(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isWhitespace(word.charAt(i))) { 
				word = whitespace.matcher(word).replaceAll(underscore); 
			}
		}
		return word.toLowerCase();
	}

	/**
	 * Internal method for stripping noun suffixes.
	 */
	protected List<String> stripNounSuffix(final String noun) {

		int idx;
		String word = noun;
		boolean endsWithFUL = false;
		if (noun.endsWith(SUFFIX_ful)) {
			endsWithFUL = true;
			idx = word.lastIndexOf(SUFFIX_ful);
			word = noun.substring(0, idx);
		}

		SortedSet<String> result = new TreeSet<String>();
		StringBuilder sb = new StringBuilder();
		for (String[] mapping : nounMappings) {
			if (!word.endsWith(mapping[0])) continue;
			idx = word.lastIndexOf(mapping[0]);
			sb.setLength(0);
			for (int i = 0; i < idx; i++) sb.append(word.charAt(i));
			sb.append(mapping[1]);
			if (endsWithFUL) sb.append(SUFFIX_ful);
			result.add(sb.toString());
		}
		return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);
	}
	
	protected List<String> getNounCollocationRoots(String composite){
		
		String[] parts = composite.split(underscore);
		if(parts.length < 2) return Collections.emptyList();
		
		List<List<String>> rootSets = new ArrayList<List<String>>(parts.length);
		for(int i = 0; i < parts.length; i++){
			rootSets.add(findStems(parts[i], POS.NOUN));
		}
		
		// assemble possibilities here; these are all combinations
		// of all possible root parts
		Set<StringBuffer> poss = new HashSet<StringBuffer>();
		
		// seed the set
		List<String> rootSet = rootSets.get(0);
		if(rootSet == null){
			poss.add(new StringBuffer(parts[0]));
		} else {
			for(Object root : rootSet) poss.add(new StringBuffer((String)root));
		}
		
		// make all combinations
		StringBuffer newBuf;
		Set<StringBuffer> replace;
		for(int i = 1; i < rootSets.size(); i++){
			rootSet = rootSets.get(i);
			if(rootSet.isEmpty()){
				for(StringBuffer p : poss){
					p.append("_");
					p.append(parts[i]);
				}
			} else {
				replace = new HashSet<StringBuffer>();
				for(StringBuffer p : poss){
					for(Object root : rootSet){
						newBuf = new StringBuffer();
						newBuf.append(p.toString());
						newBuf.append("_");
						newBuf.append(root);
						replace.add(newBuf);
					}
				}
				poss.clear();
				poss.addAll(replace);
			}
			
		}
		
		if(poss.isEmpty()) return Collections.<String>emptyList();
		
		SortedSet<String> result = new TreeSet<String>();
		for(StringBuffer p : poss) result.add(p.toString());
		return new ArrayList<String>(result);
	}

	/**
	 * Internal method for stripping verb suffixes.
	 */
	protected List<String> stripVerbSuffix(final String word) {
		
		SortedSet<String> result = new TreeSet<String>();
		int idx;
		StringBuffer stem;
		for (String[] mapping : verbMappings) {
			if (!word.endsWith(mapping[0])) continue;
			idx = word.lastIndexOf(mapping[0]);
			stem = new StringBuffer();
			for (int i = 0; i < idx; i++)
				stem.append(word.charAt(i));
			stem.append(mapping[1]);
			result.add(stem.toString());
		}
		return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);
	}
	
	protected List<String> getVerbCollocationRoots(String composite){
		
		String[] parts = composite.split(underscore);
		if(parts.length < 2) return Collections.emptyList();
		
		List<List<String>> rootSets = new ArrayList<List<String>>(parts.length);
		for(int i = 0; i < parts.length; i++){
			rootSets.add(findStems(parts[i], POS.VERB));
		}
		
		SortedSet<String> result = new TreeSet<String>();
		
		StringBuffer rootBuffer = new StringBuffer();
		for(int i = 0; i < parts.length; i++){
			if(rootSets.get(i) == null) continue;
			for(Object partRoot : rootSets.get(i)){
				rootBuffer.replace(0, rootBuffer.length(), "");
				
				for(int j = 0; j < parts.length; j++){
					if(j == i){
						rootBuffer.append((String)partRoot);
					} else {
						rootBuffer.append(parts[j]);
					}
					if(j < parts.length-1) rootBuffer.append(underscore);
				}
				result.add(rootBuffer.toString());
			}
		}
		
		return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);
	}

	/**
	 * Internal method for stripping adjective suffixes.
	 */
	protected List<String> stripAdjectiveSuffix(final String word) {

		SortedSet<String> result = new TreeSet<String>();
		int idx;
		StringBuffer stem;
		for (String[] mapping : adjMappings) {
			if (!word.endsWith(mapping[0])) continue;
			idx = word.lastIndexOf(mapping[0]);
			stem = new StringBuffer();
			for (int i = 0; i < idx; i++) stem.append(word.charAt(i));
			stem.append(mapping[1]);
			result.add(stem.toString());
		}
		return result.isEmpty() ? Collections.<String>emptyList() : new ArrayList<String>(result);
	}
}