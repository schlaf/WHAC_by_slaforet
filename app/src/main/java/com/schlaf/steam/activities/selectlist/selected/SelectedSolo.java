package com.schlaf.steam.activities.selectlist.selected;

import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;

public class SelectedSolo extends SelectedModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8433542506972983378L;

	private static int orderingOffset = SelectedSection.orderingOffsetSolo + 10;
	
	public SelectedSolo(String id, String label) {
		super(id, label);
		// TODO Auto-generated constructor stub
	}

	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
	
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		
		SelectionSolo selection = (SelectionSolo) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		sb.append(selection.getFullLabel());
		sb.append(getCostString());
		return sb.toString();
	}
}
