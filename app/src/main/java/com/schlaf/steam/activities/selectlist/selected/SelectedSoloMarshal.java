package com.schlaf.steam.activities.selectlist.selected;

import java.util.ArrayList;
import java.util.List;


public class SelectedSoloMarshal extends SelectedSolo implements JackMarshall {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8695344535502033455L;

	private int maxJacks = NORMAL_MAX_OF_JACKS;
	
	public SelectedSoloMarshal(String id, String label) {
		super(id, label);
	}

	/** warjacks */
	List<SelectedWarjack> attachedJacks= new ArrayList<SelectedWarjack>(2);

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
	public void removeJack(String jackId) {
		// TODO Auto-generated method stub
		for (SelectedWarjack jack : attachedJacks) {
			if (jack.getId().equals(jackId)) {
				attachedJacks.remove(jack);
				break;
			}
		}
	}

	@Override
	public String getCostString() {
		int cost = getCost();
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(cost);
		if (! getJacks().isEmpty()) {
			for (SelectedWarjack jack : getJacks()) {
				sb.append("+").append(jack.getCost());
			}
		}
		sb.append(" PC]");
		return sb.toString();
	}

	@Override
	public String getModelCountString() {
		StringBuffer sb = new StringBuffer();
		sb.append("1 marshall");
		if (! getJacks().isEmpty()) {
			sb.append(" + ").append(getJacks().size()).append(" warjack");
			if (getJacks().size() > 1) {
				sb.append("s");
			}
		}
		return sb.toString();
	}

	@Override
	public int getCost() {
		return super.getCost();
	}

	@Override
	public int getTotalCost() {
		int cost = getCost();
		if (! getJacks().isEmpty()) {
			for (SelectedWarjack jack : getJacks()) {
                if (! jack.isSpecialist()) {
                    cost += jack.getCost();
                }
			}
		}
		return cost;
	}

    @Override
    public int getTotalSubSpecialistCost() {
        int cost = 0;
        if (! getJacks().isEmpty()) {
            for (SelectedWarjack jack : getJacks()) {
                if (jack.isSpecialist()) {
                    cost += jack.getCost();
                }
            }
        }
        return cost;
    }

	@Override
	public int getModelCount() {
		return 1 + getJacks().size();
	}
	
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getLabel());
		sb.append("+[").append(attachedJacks.size()).append(" jacks]");
		return sb.toString();
	}

	@Override
	public void setMaxJacks(int max) {
		maxJacks = max;
	}

	public String getAttachString() {
		StringBuffer sb = new StringBuffer();
		sb.append("'jack marshall");
		sb.append(" + ").append(getJacks().size());
		sb.append(" warjack");
		if (getJacks().size()>1) {
			sb.append("s");
		}
		return sb.toString();
	}

    @Override
    public void setSpecialist(boolean specialist) {
        super.setSpecialist(specialist);
        if (specialist) {
            for (SelectedWarjack jack : getJacks()) {
                jack.setSpecialist(true);
            }
        }
    }

}
