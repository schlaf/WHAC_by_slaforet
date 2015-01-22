package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

/**
 * unit attachment
 * @author S0085289
 *
 */
public class SelectedUA extends SelectedEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7910452935042405604L;
	private static int orderingOffset = 2000;
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
	
	public SelectedUA(String id, String label) {
		super(id, label);
	}
	
}
