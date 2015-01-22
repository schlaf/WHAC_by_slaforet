package com.schlaf.steam.data;

public class RuleCostAlteration {

	/** the model which has the bonus */
	private String entryId;
	/** cost bonus */
	private int bonus;
	/** apply bonus only if attached to this model */
	private String onlyIfAttachedToId;
	
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	public String getOnlyIfAttachedToId() {
		return onlyIfAttachedToId;
	}
	public void setOnlyIfAttachedToId(String onlyIfAttachedToId) {
		this.onlyIfAttachedToId = onlyIfAttachedToId;
	}
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String toString() {
		return "[if attached to " + onlyIfAttachedToId + " then " + entryId + "has cost - " + bonus + "]";
	}

}
