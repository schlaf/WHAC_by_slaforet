package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.HashMap;

public class TierLevel {

	private int level;
	private String description;
	
	private boolean inheritOnlyModelsFromPreviousLevel;
	
	private ArrayList<TierEntry> onlyModels = new ArrayList<TierEntry>();
	private ArrayList<TierEntryGroup> mustHaveModels = new ArrayList<TierEntryGroup>();
	private ArrayList<TierEntryGroup> mustHaveModelsInBG = new ArrayList<TierEntryGroup>();
	private ArrayList<TierEntryGroup> mustHaveModelsInMarshal = new ArrayList<TierEntryGroup>();
	
	private TierBenefit benefit;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TierBenefit getBenefit() {
		return benefit;
	}
	public void setBenefit(TierBenefit benefit) {
		this.benefit = benefit;
	}
	public ArrayList<TierEntry> getOnlyModels() {
		return onlyModels;
	}
	
	/** the map for allowed models */
	private HashMap<String, TierEntry> onlyModelsById;
	
	/** returns the map (after implicitly feeding the map) */
	public HashMap<String, TierEntry> getOnlyModelsById() {
		if (onlyModelsById == null) {
			onlyModelsById = new HashMap<String, TierEntry>();

			for (TierEntry entry : onlyModels) {
				onlyModelsById.put(entry.getId(), entry);
			}

		}
		return onlyModelsById;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[level ").append(level).append(" - onlyModels = [");
		for (TierEntry model : onlyModels) {
			sb.append(model.toString()).append(" ");
		}
		sb.append("]");
		sb.append( " - must have : [");
		for (TierEntryGroup group: mustHaveModels) {
			sb.append(group.toString()).append(" ");
		}
		sb.append("]]");
		return sb.toString();
	}
	public ArrayList<TierEntryGroup> getMustHaveModels() {
		return mustHaveModels;
	}
	public ArrayList<TierEntryGroup> getMustHaveModelsInBG() {
		return mustHaveModelsInBG;
	}
	public ArrayList<TierEntryGroup> getMustHaveModelsInMarshal() {
		return mustHaveModelsInMarshal;
	}
	public boolean isInheritOnlyModelsFromPreviousLevel() {
		return inheritOnlyModelsFromPreviousLevel;
	}
	public void setInheritOnlyModelsFromPreviousLevel(
			boolean inheritOnlyModelsFromPreviousLevel) {
		this.inheritOnlyModelsFromPreviousLevel = inheritOnlyModelsFromPreviousLevel;
	}

	
}
