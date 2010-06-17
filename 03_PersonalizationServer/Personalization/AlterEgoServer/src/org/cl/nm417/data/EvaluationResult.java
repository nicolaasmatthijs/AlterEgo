/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.data;

/**
 * Class that represents one evaluated profile generation and result re-ranking
 * strategy from the relevance judgements offline evaluation experiment. These will
 * be used to process the results obtained in that experiment
 */
public class EvaluationResult {

	/*
	 *  Parameter Configuration
	 */
	// Human readable configuration name
	private String name = "";
	
	// Weighting method (TF, TF-IDF, BM25)
	private String method = "";
	// Is title used as input data
	private String title = "";
	// Is the metadata description used as input data
	private String metadescription = "";
	// Are the metadata keywords used as input data
	private String metakeywords = "";
	// Are extracted terms used as input data
	private String terms = "";
	// Is plain text used as input data
	private String text = "";
	// Is parsed text used as input data
	private String ccparse = "";
	// Filtering method used (No, Google N-Gram, WordNet)
	private String filtering = "";
	
	// Re-ranking method used (Matching, Unique Matching, LM, PClick)
	private String rerank = "";
	// Keep Google rank into account
	private boolean lookatrank;
	// Give extra weight to visited URLs
	private boolean visited;
	
	// Scores of all participating users
	private double Nicolaas;
	private double Fie;
	private double Christian;
	private double Simon;
	private double Oszkar;
	private double Aaron;
	
	// Average NDCG score
	private double average;
	// Number of queries improved
	private int improved;
	
	/*
	 *  Significance test
	 */
	// Significant on 0.05 level using paired T-Test
	private boolean pairedT005;
	// Significant on 0.01 level using paired T-Test
	private boolean pairedT001;
	
	/**
	 * Function that will take 1 evaluated strategy and will parse its configuration and
	 * store it into memory
	 * @param line Line from the results CSV file
	 * 			This has the following format 
	 * 				"TFxIDF, RTitle, RMKeyw, RCCParse, GFilt - LM, Look At Rank, Visited ",
	 *				0.507,0.548,0.636,0.597,0.541,0.608,0.573,48/72 (66.7%),TRUE,TRUE
	 */
	public EvaluationResult(String line){
		
		
		
		String[] scores = null;
		String[] spl = null;
		if (line.contains("__ Google __") || line.contains("__ Teevan __")){
			scores = line.split(",");
		} else {
			spl = line.split("\"");
			scores = spl[2].split(",");
		}
		
		// Extract average NDCG scores out of line
		setFie(Double.parseDouble(scores[1]));
		setNicolaas(Double.parseDouble(scores[2]));
		setSimon(Double.parseDouble(scores[3]));
		setChristian(Double.parseDouble(scores[4]));
		setOszkar(Double.parseDouble(scores[5]));
		setAaron(Double.parseDouble(scores[6]));
		setAverage(Double.parseDouble(scores[7]));
		setImproved(Integer.parseInt(scores[8].split("/")[0]));
		setPairedT005(Boolean.parseBoolean(scores[9]));
		setPairedT001(Boolean.parseBoolean(scores[10]));
		
		if (line.contains("__ Google __")){
			setName("Google");
		} else if (line.contains("__ Teevan __")){
			setName("Teevan");
		} else {
			setName(spl[1]);
			
			// Weighting Method
			if (spl[1].contains("BM25")){
				setMethod("BM25");
			} else if (spl[1].contains("TFxIDF")){
				setMethod("TFxIDF");
			} else {
				setMethod("TF");
			}
			
			// Title
			if (spl[1].contains("RTitle")){
				// Relative weighting
				setTitle("r");
			} else if (spl[1].contains("Title")){
				setTitle("y");
			} else {
				setTitle("n");
			}
			
			// Metadata description
			if (spl[1].contains("RMDescr")){
				// Relative weighting
				setMetadescription("r");
			} else if (spl[1].contains("MDescr")){
				setMetadescription("y");
			} else {
				setMetadescription("n");
			}
			
			// Metadata keywords
			if (spl[1].contains("RMKeyw")){
				// Relative weighting
				setMetakeywords("r");
			} else if (spl[1].contains("MKeyw")){
				setMetakeywords("y");
			} else {
				setMetakeywords("n");
			}
			
			// Plain text
			if (spl[1].contains("RText")){
				// Relative weighting
				setText("r");
			} else if (spl[1].contains("Text")){
				setText("y");
			} else {
				setText("n");
			}
			
			// Extracted tersm
			if (spl[1].contains("RTerms")){
				// Relative weighting
				setTerms("r");
			} else if (spl[1].contains("Terms")){
				setTerms("y");
			} else {
				setTerms("n");
			}
			
			// C&C Parsed Text
			if (spl[1].contains("RCCParse")){
				// Relative weighting
				setCcparse("r");
			} else if (spl[1].contains("CCParse")){
				setCcparse("y");
			} else {
				setCcparse("n");
			}
			
			// Filtering
			if (spl[1].contains("NoFilt")){
				setFiltering("n");
			} else if (spl[1].contains("GFilt")){
				setFiltering("g");
			} else {
				setFiltering("w");
				setName(getName().replaceAll(", -", ", WNFilt -"));
			}
			
			// Re-ranking method
			if (spl[1].contains("LM")){
				setRerank("LM");
			} else if (spl[1].contains("UMatching")){
				setRerank("UMatching");
			} else if (spl[1].contains("Matching")){
				setRerank("Matching");
			} else {
				setRerank("PClick");
			}
			
			// Take Google rank into account
			if (spl[1].contains("Not Look At Rank")){
				setLookatrank(false);
			} else {
				setLookatrank(true);
			}
			
			// Give extra weight to previously visited URLs
			if (spl[1].contains("Not visited")){
				setVisited(false);
			} else {
				setVisited(true);
			}
		}
	}
	
	// Getters and setters
	
	public void setNicolaas(double nicolaas) {
		Nicolaas = nicolaas;
	}
	
	public double getNicolaas() {
		return Nicolaas;
	}

	public void setFie(double fie) {
		Fie = fie;
	}

	public double getFie() {
		return Fie;
	}

	public void setChristian(double christian) {
		Christian = christian;
	}

	public double getChristian() {
		return Christian;
	}

	public void setSimon(double simon) {
		Simon = simon;
	}

	public double getSimon() {
		return Simon;
	}

	public void setOszkar(double oszkar) {
		Oszkar = oszkar;
	}

	public double getOszkar() {
		return Oszkar;
	}

	public void setAaron(double aaron) {
		Aaron = aaron;
	}

	public double getAaron() {
		return Aaron;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getAverage() {
		return average;
	}

	public void setImproved(int improved) {
		this.improved = improved;
	}

	public int getImproved() {
		return improved;
	}

	public void setPairedT005(boolean pairedT005) {
		this.pairedT005 = pairedT005;
	}

	public boolean isPairedT005() {
		return pairedT005;
	}

	public void setPairedT001(boolean pairedT001) {
		this.pairedT001 = pairedT001;
	}

	public boolean isPairedT001() {
		return pairedT001;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setMetadescription(String metadescription) {
		this.metadescription = metadescription;
	}

	public String getMetadescription() {
		return metadescription;
	}

	public void setMetakeywords(String metakeywords) {
		this.metakeywords = metakeywords;
	}

	public String getMetakeywords() {
		return metakeywords;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getTerms() {
		return terms;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setCcparse(String ccparse) {
		this.ccparse = ccparse;
	}

	public String getCcparse() {
		return ccparse;
	}

	public void setFiltering(String filtering) {
		this.filtering = filtering;
	}

	public String getFiltering() {
		return filtering;
	}

	public void setRerank(String rerank) {
		this.rerank = rerank;
	}

	public String getRerank() {
		return rerank;
	}

	public void setLookatrank(boolean lookatrank) {
		this.lookatrank = lookatrank;
	}

	public boolean isLookatrank() {
		return lookatrank;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean isVisited() {
		return visited;
	}
	
}
