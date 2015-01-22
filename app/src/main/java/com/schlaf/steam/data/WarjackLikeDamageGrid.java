package com.schlaf.steam.data;

import java.util.List;

import com.schlaf.steam.activities.damages.DamageStatus;

public abstract class WarjackLikeDamageGrid extends DamageGrid {

	/**
	 * 
	 */
	private static final long serialVersionUID = 527514714271233373L;

	public abstract DamageStatus getNbHitPointsSystem(WarmachineDamageSystemsEnum system);

	public abstract List<WarmachineDamageSystemsEnum> getSystems();

	public abstract List<DamageBox> getJustDamagedBoxes();
}
