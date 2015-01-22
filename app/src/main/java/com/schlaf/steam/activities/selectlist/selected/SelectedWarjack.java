package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

public class SelectedWarjack extends SelectedModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1375160400073595249L;

	public SelectedWarjack(String id, String label) {
		super(id, label);
	}

	@Override
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getLabel()).append(getCostString());
		return sb.toString();
	}

	@Override
	public int getOrderingOffset() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
