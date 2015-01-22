/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.io.Serializable;
import java.util.ArrayList;

import com.schlaf.steam.data.FactionNamesEnum;

/**
 * @author S0085289
 *
 */
public class BattleList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 509193375055535584L;

	/** army name */
	private String armyName;
	
	/** faction */
	private FactionNamesEnum faction;
	
	// tiers? contract?
	
	/**
	 * time elapsed on chrono
	 */
	private long timeElapsed;
	
	/**
	 * all army entries (including current damage status)
	 */
	private ArrayList<BattleEntry> entries;
	
	public BattleList() {
		// rien?
	}

	public String getArmyName() {
		return armyName;
	}

	public void setArmyName(String armyName) {
		this.armyName = armyName;
	}

	public FactionNamesEnum getFaction() {
		return faction;
	}

	public void setFaction(FactionNamesEnum faction) {
		this.faction = faction;
	}

	public long getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public ArrayList<BattleEntry> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<BattleEntry> entries) {
		this.entries = entries;
	}
}
