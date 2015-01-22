package com.schlaf.steam.activities.selectlist.selection;

import java.io.Serializable;

import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.UnitAttachment;

public class SelectionUA extends SelectionEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7839114315444031243L;
	private String parentUnitId;
	
	public SelectionUA(UnitAttachment ua, Unit unit) {
		super(ua);
		parentUnitId = unit.getId();
	}

	public String getParentUnitId() {
		return parentUnitId;
	}

	public void setParentUnitId(String parentUnitId) {
		this.parentUnitId = parentUnitId;
	}

}
