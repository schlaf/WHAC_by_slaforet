package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

/**
 * ranking officer
 * @author S0085289
 *
 */
public class SelectedRankingOfficer extends SelectedSolo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8356816958408373854L;
	private static int orderingOffset = 2000;
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
	
	public SelectedRankingOfficer(String id, String label) {
		super(id, label);
	}
	
}
