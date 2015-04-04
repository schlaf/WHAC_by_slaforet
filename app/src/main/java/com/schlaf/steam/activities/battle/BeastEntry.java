package com.schlaf.steam.activities.battle;

import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastDamageSpiral;

public class BeastEntry extends MultiPVModel {

	/** serial */
	private static final long serialVersionUID = 2331845805167531358L;
	private WarbeastDamageSpiral damageGrid;
	
	public BeastEntry(Warbeast beast, int entryCounter, int cost, boolean specialist) {
		this (beast, null, entryCounter, cost, specialist);
	}
	
	public BeastEntry(Warbeast beast, BattleEntry parent, int entryCounter, int cost, boolean specialist) {
		super(beast.getModels().get(0), beast, beast.getFullName(), entryCounter, cost, specialist);
		if (parent != null) {
			attached = true;
			this.parentId = parent.getUniqueId();
		} else {
			attached = false;
		}
		 
		damageGrid = new WarbeastDamageSpiral(beast.getModels().get(0));
		damageGrid.fromString(beast.getGrid());
	}
	
	@Override
	public WarbeastDamageSpiral getDamageGrid() {
		return damageGrid;
	}

}
