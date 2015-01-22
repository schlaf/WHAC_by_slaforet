package com.schlaf.steam.data;

import java.util.ArrayList;

/**
 * choose one of the models for free.
 * @author S0085289
 *
 */
public class TierFreeModel {
	
	private ArrayList<TierEntry> freeModels = new ArrayList<TierEntry>();

	private ArrayList<TierEntry> forEach = new ArrayList<TierEntry>();
	
	public ArrayList<TierEntry> getFreeModels() {
		return freeModels;
	}

	public ArrayList<TierEntry> getForEach() {
		return forEach;
	}

	public void setForEach(ArrayList<TierEntry> forEach) {
		this.forEach = forEach;
	}
	

}
