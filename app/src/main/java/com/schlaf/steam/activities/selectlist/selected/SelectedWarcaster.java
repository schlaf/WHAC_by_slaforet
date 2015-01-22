package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SelectedWarcaster extends SelectedArmyCommander implements WarcasterInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4124086661760930459L;

	public SelectedWarcaster(String id, String label) {
		super(id, label);
	}

	/** warjacks */
	List<SelectedWarjack> attachedJacks= new ArrayList<SelectedWarjack>(10);

	@Override
	public List<SelectedWarjack> getJacks() {
		return attachedJacks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelectedModel> getAttachedModels() {
		// TODO Auto-generated method stub
		return (List<SelectedModel>) (List<?>) attachedJacks;
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
	public String getCostString() {
		int cost = 0; // casters are free! getCost();
		if (getAttachment() != null) {
			cost += getAttachment().getCost();
		}
		if (! getJacks().isEmpty()) {
			int jackCost = 0;
			for (SelectedWarjack jack : getJacks()) {
				jackCost += jack.getCost();
			}
			if (jackCost > getCost()) {
				jackCost -= getCost();
				cost += jackCost;
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(cost).append("PC]");
		return sb.toString();
	}

	@Override
	public String getModelCountString() {
		StringBuffer sb = new StringBuffer();
		sb.append("1 caster");
		if (getAttachment() != null) {
			sb.append(" + 1 attachment");
		}
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
		sb.append("warcaster");
		sb.append(" + ").append(getJacks().size());
		sb.append(" warjack");
		if (getJacks().size()>1) {
			sb.append("s");
		}
		return sb.toString();
	}
	
}
