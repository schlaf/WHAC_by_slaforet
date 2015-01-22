package com.schlaf.steam.activities.damages;

import com.schlaf.steam.activities.battle.BattleListAdapter;


/**
 * base class for every widget of type "gridview" displaying damage status (single line damage view, unit damage view, spiral, ...)
 * @author S0085289
 *
 */
public abstract class ViewDamageChangeObserver implements DamageChangeObserver {

	// reference the observer to the parent, so that it can be cleaned upon destruction of the view
	public ViewDamageChangeObserver(BattleListAdapter parent) {
		parent.addObserver(this);
	}
	
}
