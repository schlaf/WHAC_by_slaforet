package com.schlaf.steam.data;

import com.schlaf.steam.R;
import com.schlaf.steam.data.Faction.GameSystem;

public enum FactionNamesEnum {
	CRYX("faction_cryx", R.drawable.cryx, GameSystem.WARMACHINE),
	CYGNAR("faction_cygnar", R.drawable.cygnar, GameSystem.WARMACHINE),
	CYRISS("faction_cyriss", R.drawable.cyriss, GameSystem.WARMACHINE),
	KHADOR("faction_khador", R.drawable.khador, GameSystem.WARMACHINE),
	MENOTH("faction_menoth", R.drawable.menoth, GameSystem.WARMACHINE),
	
	RETRIBUTION("faction_retribution", R.drawable.retribution , GameSystem.WARMACHINE),
	MERCENARIES("faction_mercs", R.drawable.mercs, GameSystem.WARMACHINE),
	ORBOROS("faction_orboros", R.drawable.orboros, GameSystem.HORDES),
	EVERBLIGHT("faction_everblight", R.drawable.everblight, GameSystem.HORDES),
	SKORNE("faction_skorne", R.drawable.skorne, GameSystem.HORDES ),
	TROLLBLOOD("faction_trollblood", R.drawable.trolls, GameSystem.HORDES),
	MINIONS("faction_minions", R.drawable.minion,GameSystem.HORDES),
	OBJECTIVES("faction_objectives", R.drawable.mercs, GameSystem.NONE),
	OBJECTIVES_SR2015("faction_objectives_sr2015", R.drawable.objo, GameSystem.NONE)
	;

	private FactionNamesEnum(String id, int logoResource, GameSystem gameSystem) {
		this.id = id;
		this.logoResource = logoResource;
		this.gameSystem= gameSystem; 
	}
	
	/** identifiant unique */
	private String id;
	/** id du logo */
	private int logoResource;
	private GameSystem gameSystem;
	
	public String getId() {
		return id;
	}
	
	public int getLogoResource() {
		return logoResource;
	}

	/**
	 * renvoie la faction pour l'id donné
	 * @param factionId
	 * @return Names
	 */
	public static FactionNamesEnum getFaction(String factionId) {
		for (FactionNamesEnum name : values()) {
			if (name.getId().equals(factionId)) {
				return name;
			}
		}
		return CYGNAR;
	}

	public GameSystem getGameSystem() {
		return gameSystem;
	}


}