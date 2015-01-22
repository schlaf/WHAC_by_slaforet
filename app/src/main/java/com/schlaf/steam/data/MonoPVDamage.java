package com.schlaf.steam.data;

import com.schlaf.steam.activities.damages.DamageStatus;

/**
 * fake damage handling class for monoPV model
 * @author S0085289
 *
 */
public class MonoPVDamage extends DamageGrid {

	@Override
	public DamageGrid fromString(String damageGridString) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int applyFakeDamages(int damageAmount) {
		return 0;
	}

	@Override
	public int applyFakeDamages(int column, int damageAmount) {
		return 0;
	}

	@Override
	public int getTotalHits() {
		return 1;
	}

	@Override
	public DamageStatus getDamageStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void commitFakeDamages() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void resetFakeDamages() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean heal1point() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean heal1point(int column) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		return this;
	}


	@Override
	public String toStringWithDamageStatus() {
		return "O";
	}

}
