package com.schlaf.steam.activities.battle;

import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.WarjackLikeDamageGrid;

public class KarchevEntry extends JackEntry {

	/** serial */
	private static final long serialVersionUID = 6770974809897123347L;
	/** damage grid */
	private WarjackLikeDamageGrid damageGrid;
	
	public KarchevEntry(ArmyElement karchev, int entryCounter, int cost) {
		super(karchev, entryCounter, cost, false);
		attached = false;
		damageGrid = new WarjackDamageGrid(karchev.getModels().get(0));
		damageGrid.fromString(  ((WarjackDamageGrid) karchev.getModels().get(0).getHitpoints()).getDamageGridString() );
	}
	
	public WarjackLikeDamageGrid getDamageGrid() {
		return damageGrid;
	}

}
