package com.schlaf.steam.data;

import java.util.ArrayList;

public class TierBenefit {

	private String inGameEffect;
	
	private ArrayList<TierFACostBenefit> alterations = new ArrayList<TierFACostBenefit>();
	
	private ArrayList<TierFreeModel> freebies = new ArrayList<TierFreeModel>();

	public String getInGameEffect() {
		return inGameEffect;
	}

	public void setInGameEffect(String inGameEffect) {
		this.inGameEffect = inGameEffect;
	}

	public ArrayList<TierFACostBenefit> getAlterations() {
		return alterations;
	}

	public ArrayList<TierFreeModel> getFreebies() {
		return freebies;
	}
}
