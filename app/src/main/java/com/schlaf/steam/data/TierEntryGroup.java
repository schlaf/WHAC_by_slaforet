package com.schlaf.steam.data;

import java.util.ArrayList;

public class TierEntryGroup {

	private int minCount;
	private String label;
	private boolean inBattlegroup;
	private boolean inJackMarshal;
	
	private ArrayList<TierEntry> entries = new ArrayList<TierEntry>();

	public int getMinCount() {
		return minCount;
	}

	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}

	public boolean isInBattlegroup() {
		return inBattlegroup;
	}

	public void setInBattlegroup(boolean inBattlegroup) {
		this.inBattlegroup = inBattlegroup;
	}

	public boolean isInJackMarshal() {
		return inJackMarshal;
	}

	public void setInJackMarshal(boolean inJackMarshal) {
		this.inJackMarshal = inJackMarshal;
	}

	public ArrayList<TierEntry> getEntries() {
		return entries;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[entry group : at least ").append(minCount).append(" of = [");
		for (TierEntry model : entries) {
			sb.append(model.toString()).append(" ");
		}
		if (inBattlegroup) {
			sb.append(" in Battlegroup ");
		}
		if (inJackMarshal) {
			sb.append(" jackMarshalled ");
		}
		sb.append("]]");
		return sb.toString();		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
