package com.schlaf.steam.data;

import java.util.ArrayList;

public class TierFAAlteration extends TierFACostBenefit {

	private int faAlteration;

	private ArrayList<TierEntry> forEach = new ArrayList<TierEntry>();
	
	public int getFaAlteration() {
		return faAlteration;
	}

	public void setFaAlteration(int faAlteration) {
		this.faAlteration = faAlteration;
	}

	public ArrayList<TierEntry> getForEach() {
		return forEach;
	}

	public void setForEach(ArrayList<TierEntry> forEach) {
		this.forEach = forEach;
	}
	
}
