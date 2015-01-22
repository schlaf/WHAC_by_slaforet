package com.schlaf.steam.activities.selectlist.selected;

import com.schlaf.steam.activities.selectlist.ModelCostCalculator;

public class SelectedDragoon extends SelectedSolo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6550853379961234047L;
	private boolean withDismountOption;
	
	public SelectedDragoon(String id, String label, boolean withDismountOption) {
		super(id, label);
		this.withDismountOption = withDismountOption;
		// TODO Auto-generated constructor stub
	}

	public boolean isWithDismountOption() {
		return withDismountOption;
	}

	@Override
	public int getCost() {
		return ModelCostCalculator.getDragoonCost(getId(), withDismountOption);
	}

	
}
