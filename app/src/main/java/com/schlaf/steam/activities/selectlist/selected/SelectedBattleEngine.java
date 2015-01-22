package com.schlaf.steam.activities.selectlist.selected;

import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

public class SelectedBattleEngine extends SelectedEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1644488807554441363L;
	private static int orderingOffset = SelectedSection.orderingOffsetBE + 10;
	
	public SelectedBattleEngine(String id, String label) {
		super(id, label);
	}

	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		
		SelectionEntry selection = (SelectionEntry) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		sb.append(selection.getFullLabel());
		sb.append(getCostString());
		return sb.toString();
	}
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
}
