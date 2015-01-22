package com.schlaf.steam.activities.damages;

import com.schlaf.steam.data.DamageGrid;

/**
 * base class for every widget displaying damage status
 * @author S0085289
 *
 */
public interface DamageChangeObserver {

	/**
	 * do something on grid value change
	 * @param grid
	 */
	public void onChangeDamageStatus(DamageGrid grid) ;
	
	/**
	 * called when committing or cancelling pending damages
	 * @param grid
	 */
	public void onApplyCommitOrCancel(DamageGrid grid);
	
	
}
