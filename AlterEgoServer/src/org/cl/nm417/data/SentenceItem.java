package org.cl.nm417.data;

import java.util.regex.Pattern;

public class SentenceItem {

	// Add-ons|add-on|NNS|I-NP|O|N/N
	private String origWord = "";
	private String word = "";
	private String POS = "";
	public static final Pattern pattern = Pattern.compile("[|]");
	
	public SentenceItem(String parse){
		String[] split = pattern.split(parse);
		this.origWord = split[0];
		this.word = split[1].replaceAll("[,.:]", "");
		this.POS = split[2];
	}
	
	public void setOrigWord(String origWord) {
		this.origWord = origWord;
	}
	
	public String getOrigWord() {
		return origWord;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setPOS(String POS) {
		this.POS = POS;
	}

	public String getPOS() {
		return POS;
	}
	
	public String toString(){
		return this.origWord + " - " + this.word + " - " + this.POS;
	}
	
}
