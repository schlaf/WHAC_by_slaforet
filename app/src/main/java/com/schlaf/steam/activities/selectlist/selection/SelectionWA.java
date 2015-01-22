package com.schlaf.steam.activities.selectlist.selection;

import java.io.Serializable;

import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.WeaponAttachment;

public class SelectionWA extends SelectionEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7839114315444031243L;
	private String parentUnitId;
	
	public SelectionWA(WeaponAttachment wa, Unit unit) {
		super(wa);
		parentUnitId = unit.getId();
	}

	public String getParentUnitId() {
		return parentUnitId;
	}

	public void setParentUnitId(String parentUnitId) {
		this.parentUnitId = parentUnitId;
	}

	/**
	 * add one entry (per FA) with multiple count (number of WA in the unit)
	 * @param countAttach
	 */
	public void addOne(int countAttach) {
		countSelected ++;
		costSelected += (baseCost * countAttach); 
		if (countSelected > 0) {
			setSelected(true);
		}
	}

	
}
