package com.schlaf.steam.activities.selectlist.selected;

public class SelectedObjective extends SelectedSolo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7333917005690534311L;

	private static int orderingOffset = SelectedSection.orderingOffsetObjective + 10;
	
	public SelectedObjective(String id, String label) {
		super(id, label);
	}
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}

}
