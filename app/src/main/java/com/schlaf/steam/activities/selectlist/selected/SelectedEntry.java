/**
 * 
 */
package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

import com.schlaf.steam.activities.selectlist.ModelCostCalculator;


/**
 * @author S0085289
 *
 */
public abstract class SelectedEntry implements Comparable<SelectedItem>, Serializable, SelectedItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6339596193397315748L;
	
	/** id of the entry*/
	private String id;
	/** label */
	private String label;
	
	/** indicates that this entry is altered (cost, fa, ...) due to tiers */
	private boolean tiersAltered = false;
	
	/** indicates that this entry is altered (cost, fa, ...) due to special rule (field kommander, beast discount for lesser warlocks, ..) */
	private boolean ruleAltered = false;
	
	/** real cost after tiers alteration */
	private int realCost;
	
	/** indicates a free model cause of tier benefit */
	private boolean freeModel;
	
	private static int orderingOffset = 0;
	
	/** helps sorting various instances */
	public abstract int getOrderingOffset() ;
	
	public SelectedEntry(String id, String label) {
		this.id = id;
		this.label = label;
	}
	
	public String toString() {
		return "[SelectedEntry - " + id +"]";
	}
	
	/**
	 * complete string description : label + model count + cost
	 * @return
	 */
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		sb.append(label);
		return sb.toString();
	}
	
	public String getCostString() {
		StringBuffer sb = new StringBuffer();
		if (tiersAltered || ruleAltered) {
			sb.append("<font color=\"blue\">");
		}
		sb.append("[").append(getCost()).append("PC]");
		if (tiersAltered || ruleAltered) {
			sb.append("</font>");
		}
		return sb.toString();
	}
	
	public String getModelCountString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[1model]");
		return sb.toString();
	}
	
	public String getAttachString() {
		return "[1model]";
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * return cost of model (including alteration for tiers)
	 * @return
	 */
	public int getCost() {
		if (tiersAltered || ruleAltered ) {
			return realCost;
		} else {
			return ModelCostCalculator.getCost(id);	
		}
	}

	/** return the cost of this model + all attached models */
	public int getTotalCost() {
		return getCost();
	}
	

	/**
	 * ordering offset helps macro-sorting. first casters, then units, then others
	 */
	@Override
	public int compareTo(SelectedItem another) {
		int result = getOrderingOffset() - another.getOrderingOffset();
		if (result == 0) {
			result = getLabel().compareTo(another.getLabel());
		}
		return result;
//		if (another instanceof SelectedSection) {
//			return -1;
//		} else {
//			return getOrderingOffset() - ((SelectedEntry)another).getOrderingOffset() + id.compareTo(((SelectedEntry)another).getId());	
//		}
		
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getModelCount() {
		return 1;
	}


	public boolean isTiersAltered() {
		return tiersAltered;
	}


	public void setTiersAltered(boolean tiersAltered) {
		this.tiersAltered = tiersAltered;
	}


	public int getRealCost() {
		return realCost;
	}


	public void setRealCost(int realCost) {
		this.realCost = realCost;
	}


	public boolean isFreeModel() {
		return freeModel;
	}


	public void setFreeModel(boolean freeModel) {
		this.freeModel = freeModel;
	}


	@Override
	public boolean isSection() {
		return false;
	}

	public boolean isRuleAltered() {
		return ruleAltered;
	}

	public void setRuleAltered(boolean ruleAltered) {
		this.ruleAltered = ruleAltered;
	}


}
