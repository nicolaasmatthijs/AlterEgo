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
 * Represents the three different possible syntactic markers indicating
 * limitations on the syntactic position an adjective may have in relation to
 * the noun it modifies.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Jan 9, 2008
 * @since 1.5.0
 */
public enum AdjMarker {

	PREDICATE	("(p)", "predicate position"), 
	PRENOMINAL	("(a)", "prenominal (attributive) position"), 
	POSTNOMINAL	("(ip)","immediately postnominal position");

	private final String symbol, description;

	private AdjMarker(String symbol, String description) {
		this.symbol = symbol;
		this.description = description;
	}

	/**
	 * Returns the adjective marker symbol, as found appended to the ends of
	 * adjective words in the data files, parenthesis included.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Returns a user-readable description of the type of marker, drawn from the
	 * Wordnet specification.
	 */
	public String getDescription() {
		return description;
	}

}
