package com.schlaf.steam.activities.battleplanner;

import com.schlaf.steam.data.Spell;

public class PlanningSpell {

	public String label;
	public String cost;
	public boolean canUpkeep;
	public boolean doLaunch;
	public boolean doUpkeep;
	
	public PlanningSpell(Spell spell) {
		label = spell.getTitle();
		cost = spell.getCost();
		canUpkeep = Boolean.valueOf(spell.getDuration());
	}
	
	public String getDescription() {
		return label + " (" + cost + ")";
	}
	
}
