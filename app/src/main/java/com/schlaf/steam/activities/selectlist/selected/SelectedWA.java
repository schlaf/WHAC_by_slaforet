package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

/**
 * selected weapon attachment
 * @author S0085289
 *
 */
public class SelectedWA extends SelectedEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -152305464421535811L;
	private static int orderingOffset = 5000;
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}	
	
	public SelectedWA(String id, String label) {
		super(id, label);
	}
	
}
