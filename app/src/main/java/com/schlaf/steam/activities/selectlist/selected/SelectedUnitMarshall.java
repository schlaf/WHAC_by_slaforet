package com.schlaf.steam.activities.selectlist.selected;

import java.util.ArrayList;
import java.util.List;

public class SelectedUnitMarshall extends SelectedUnit implements JackMarshall {

	/**
	 * serial
	 */
	private static final long serialVersionUID = -7933573357320481899L;
	
	/** max jacks attached to unit */
	private int maxJacks = 2;
	
	/** this unit can be jack marshall only if has UA with this capacity */
	private boolean marshallViaUA;
	
	public SelectedUnitMarshall(String id, String label, boolean minSize, boolean marshallViaUA) {
		super(id, label, minSize);
		this.marshallViaUA = marshallViaUA;
	}

	public SelectedUnitMarshall(String id, String label, boolean minSize, boolean marshallViaUA, int maxJacks) {
		this(id, label, minSize, marshallViaUA);
		this.maxJacks = maxJacks;
	}


	/**
	 * return the concatenation of UA, WA and eventually warjacks of warbeasts attached
	 * @return
	 */
	public List<SelectedEntry> getChilds() {
		List<SelectedEntry> result = super.getChilds();
		result.addAll(attachedJacks);
		return result;
	}	
	
	
	/** warjacks */
	List<SelectedWarjack> attachedJacks= new ArrayList<SelectedWarjack>(10);

	@Override
	public List<SelectedWarjack> getJacks() {
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
	public int getMaxJacks() {
		return maxJacks;
	}

	@Override
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toFullString());
		if (attachedJacks.size() > 0) {
			sb.append(" + ").append(attachedJacks.size()).append(" jack");
			if (attachedJacks.size()>1) {
				sb.append("s");
			}
		}
		return sb.toString();
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

	public boolean isMarshallViaUA() {
		return marshallViaUA;
	}

	public void setMarshallViaUA(boolean marshallViaUA) {
		this.marshallViaUA = marshallViaUA;
	}

	public void setMaxJacks(int maxJacks) {
		this.maxJacks = maxJacks;
	}
	
	public int getModelCount() {
		int modelCount = super.getModelCount();
		
		modelCount += getJacks().size();
		
		return modelCount;
	}

	
	@Override
	public int getTotalCost() {
		int cost = super.getTotalCost();
		if (! getJacks().isEmpty()) {
			for (SelectedWarjack jack : getJacks()) {
				cost += jack.getCost();
			}
		}		
		return cost;
	}
	
	public String getAttachString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(super.getAttachString());
		sb.append(" + ").append(getJacks().size());
		sb.append(" warjack");
		if (getJacks().size()>1) {
			sb.append("s");
		}
		return sb.toString();
	}
	
}
