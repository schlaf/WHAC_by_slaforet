package com.schlaf.steam.activities.damages;

import com.schlaf.steam.data.DamageGrid;

public interface DamageCommitObserver {

	/**
	 * do something on grid value change
	 * @param grid
	 */
	public void onCommitChangeDamageStatus(DamageGrid grid) ;
}
