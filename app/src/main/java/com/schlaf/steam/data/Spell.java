package com.schlaf.steam.data;

import java.io.Serializable;

public class Spell implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6417596931395560445L;
	private String title;
	private String cost;
	private String range;
	private String aoe;
	private String pow;
	private String upkeep;
	private String offensive;
	
	private String fullText;

	public String getStringResume() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[COST=").append(cost).append("|RNG=").append(range).append("|AOE=").append(aoe)
			.append("|POW=").append(pow).append("|UP=").append(upkeep).append("|OFF=").append(offensive);
		sb.append("] ").append(fullText.replace("\n", ""));
		return sb.toString();
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getAoe() {
		return aoe;
	}

	public void setAoe(String aoe) {
		this.aoe = aoe;
	}

	public String getUpkeep() {
		return upkeep;
	}

	public void setUpkeep(String upkeep) {
		this.upkeep = upkeep;
	}

	public String getOffensive() {
		return offensive;
	}

	public void setOffensive(String offensive) {
		this.offensive = offensive;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getPow() {
		return pow;
	}

	public void setPow(String pow) {
		this.pow = pow;
	}
	
}
