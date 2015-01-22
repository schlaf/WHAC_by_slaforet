package com.schlaf.steam.data;

public abstract class TierFACostBenefit {

	protected TierEntry entry;

	/** this bonus is restricted : only apply if the model is attached to restrictedToId */
	private boolean restricted;
	
	/** the bonus applies if "entry" is attached to "restrictedToId" */
	private String restrictedToId;
	
	public TierEntry getEntry() {
		return entry;
	}

	public void setEntry(TierEntry entry) {
		this.entry = entry;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public String getRestrictedToId() {
		return restrictedToId;
	}

	public void setRestrictedToId(String restrictedToId) {
		this.restrictedToId = restrictedToId;
	}

}
