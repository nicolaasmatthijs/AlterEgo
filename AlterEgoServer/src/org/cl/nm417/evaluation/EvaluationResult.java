package org.cl.nm417.evaluation;

public class EvaluationResult {

	// Configuration
	private String name = "";
	
	private String method = "";
	private String title = "";
	private String metadescription = "";
	private String metakeywords = "";
	private String terms = "";
	private String text = "";
	private String ccparse = "";
	private String filtering = "";
	private String rerank = "";
	private boolean lookatrank;
	private boolean visited;
	
	// Scores
	private double Nicolaas;
	private double Fie;
	private double Christian;
	private double Simon;
	private double Oszkar;
	private double Aaron;
	
	private double average;
	private int improved;
	
	// Significance test
	private boolean pairedT005;
	private boolean pairedT001;
	
	// Constructor
	
	public EvaluationResult(String line){
		// "TFxIDF, RTitle, RMKeyw, RCCParse, GFilt - LM, Look At Rank, Visited ",
		// 0.507,0.548,0.636,0.597,0.541,0.608,0.573,48/72 (66.7%),TRUE,TRUE
		
		if (line.contains("__ Google __")){
			setName("Google");
			String[] scores = line.split(",");
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
		} else if (line.contains("__ Teevan __")){
			setName("Teevan");
			String[] scores = line.split(",");
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
		} else {
			
			String[] spl = line.split("\"");
			
			String[] scores = spl[2].split(",");
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
			setName(spl[1]);
			
			if (spl[1].contains("BM25")){
				setMethod("BM25");
			} else if (spl[1].contains("TFxIDF")){
				setMethod("TFxIDF");
			} else {
				setMethod("TF");
			}
			
			if (spl[1].contains("RTitle")){
				setTitle("r");
			} else if (spl[1].contains("Title")){
				setTitle("y");
			} else {
				setTitle("n");
			}
			
			if (spl[1].contains("RMDescr")){
				setMetadescription("r");
			} else if (spl[1].contains("MDescr")){
				setMetadescription("y");
			} else {
				setMetadescription("n");
			}
			
			if (spl[1].contains("RMKeyw")){
				setMetakeywords("r");
			} else if (spl[1].contains("MKeyw")){
				setMetakeywords("y");
			} else {
				setMetakeywords("n");
			}
			
			if (spl[1].contains("RText")){
				setText("r");
			} else if (spl[1].contains("Text")){
				setText("y");
			} else {
				setText("n");
			}
			
			if (spl[1].contains("RTerms")){
				setTerms("r");
			} else if (spl[1].contains("Terms")){
				setTerms("y");
			} else {
				setTerms("n");
			}
			
			if (spl[1].contains("RCCParse")){
				setCcparse("r");
			} else if (spl[1].contains("CCParse")){
				setCcparse("y");
			} else {
				setCcparse("n");
			}
			
			if (spl[1].contains("NoFilt")){
				setFiltering("n");
			} else if (spl[1].contains("GFilt")){
				setFiltering("g");
			} else {
				setFiltering("w");
				setName(getName().replaceAll(", -", ", WNFilt -"));
			}
			
			if (spl[1].contains("LM")){
				setRerank("LM");
			} else if (spl[1].contains("UMatching")){
				setRerank("UMatching");
			} else if (spl[1].contains("Matching")){
				setRerank("Matching");
			} else {
				setRerank("PClick");
			}
			
			if (spl[1].contains("Not Look At Rank")){
				setLookatrank(false);
			} else {
				setLookatrank(true);
			}
			
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
