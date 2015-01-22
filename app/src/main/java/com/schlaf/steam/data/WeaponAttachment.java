package com.schlaf.steam.data;


public class WeaponAttachment extends ArmyElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 182490286293520781L;
	private int cost;
	
	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.WEAPON_ATTACHMENT;
	}

	@Override
	public boolean hasStandardCost() {
		return true;
	}

	@Override
	public int getBaseCost() {
		return cost;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String getCostString() {
		return String.valueOf(cost);
	}

}
