package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;
import java.util.ArrayList;


public class SelectedJourneyManWarcaster extends SelectedSolo implements WarcasterInterface, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1727335399617272558L;

	public SelectedJourneyManWarcaster(String id, String label) {
		super(id, label);
		// TODO Auto-generated constructor stub
	}

	/** warjacks */
	ArrayList<SelectedWarjack> attachedJacks= new ArrayList<SelectedWarjack>(10);

	@Override
	public ArrayList<SelectedWarjack> getJacks() {
		return attachedJacks;
	}
	
	@Override
	public boolean hasJackInAttachment(String jackId) {
		for (SelectedWarjack jack : attachedJacks) {
			if (jack.getId().equals(jackId)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void removeJack(String jackId) {
		for (SelectedWarjack jack : attachedJacks) {
			if (jack.getId().equals(jackId)) {
				attachedJacks.remove(jack);
				break;
			}
		}
	}
	
	@Override 
	public int getTotalCost() {
		int result = getCost();
		for (SelectedModel attached : getJacks()) {
			result += attached.getCost();
		}
		return result;
	}
	
	@Override
	public int getModelCount() {
		return 1 + getJacks().size();
	}
	
	@Override
	public String getModelCountString() {
		StringBuffer sb = new StringBuffer();
		sb.append("1 journeyman");
		if (! getJacks().isEmpty()) {
			sb.append(" + ").append(getJacks().size());
			sb.append(" warjack");
			if (getJacks().size()>1) {
				sb.append("s");
			}
		}
		return sb.toString();
	}
	
	public String getAttachString() {
		StringBuffer sb = new StringBuffer();
		sb.append("journeyman warcaster");
		sb.append(" + ").append(getJacks().size());
		sb.append(" warjack");
		if (getJacks().size()>1) {
			sb.append("s");
		}
		return sb.toString();
	}
	
	@Override
	public String getCostString() {
		int cost = getTotalCost();
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(cost).append("PC]");
		return sb.toString();
	}
}
