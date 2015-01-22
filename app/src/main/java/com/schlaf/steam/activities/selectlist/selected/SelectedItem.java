package com.schlaf.steam.activities.selectlist.selected;

public interface SelectedItem extends Comparable<SelectedItem> {

	public boolean isSection();
	
	/** helps sorting various instances */
	public int getOrderingOffset();
	
	public String getLabel();
}
