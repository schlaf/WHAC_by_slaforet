package com.schlaf.steam.data;


public class RuleFAAlteration {

	/** the model which has the bonus */
	private String entryId;
	/** FA bonus */
	private int bonus;
	/** apply bonus only if this model is present in army */
	private String onlyIfPresentId;
	
	
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	public String getOnlyIfPresentId() {
		return onlyIfPresentId;
	}
	public void setOnlyIfPresentId(String onlyIfPresentId) {
		this.onlyIfPresentId = onlyIfPresentId;
	}
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String toString() {
		return "[if " + onlyIfPresentId + " then " + entryId + "has FA + " + bonus + "]";
	}
	
}
