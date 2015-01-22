package com.schlaf.steam.activities.selectlist.selected;

import java.util.ArrayList;
import java.util.List;


public class SelectedWarlock extends SelectedArmyCommander implements WarlockInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8533504706132738090L;

	public SelectedWarlock(String id, String label) {
		super(id, label);
		// TODO Auto-generated constructor stub
	}

	/** warbeasts */
	ArrayList<SelectedWarbeast> attachedBeasts= new ArrayList<SelectedWarbeast>(10);

	@Override
	public ArrayList<SelectedWarbeast> getBeasts() {
		return attachedBeasts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelectedModel> getAttachedModels() {
		return (List<SelectedModel>) (List<?>) attachedBeasts;
	}
	
	
	@Override
	public String getCostString() {
		int cost = 0; // casters are free! getCost();
		if (getAttachment() != null) {
			cost += getAttachment().getCost();
		}
		if (! getBeasts().isEmpty()) {
			int beastCost = 0;
			for (SelectedWarbeast beast : getBeasts()) {
				beastCost += beast.getCost();
			}
			if (beastCost > getCost()) {
				beastCost -= getCost();
				cost += beastCost;
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
		if (! getBeasts().isEmpty()) {
			sb.append(" + ").append(getBeasts().size());
			sb.append(" warbeast");
			if (getBeasts().size()>1) {
				sb.append("s");
			}
		}
		return sb.toString();
	}
	
	public String getAttachString() {
		StringBuffer sb = new StringBuffer();
		sb.append("warlock");
		sb.append(" + ").append(getBeasts().size());
		sb.append(" warbeast");
		if (getBeasts().size()>1) {
			sb.append("s");
		}
		return sb.toString();
	}
}
