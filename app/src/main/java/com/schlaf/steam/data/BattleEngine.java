package com.schlaf.steam.data;

public class BattleEngine extends ArmyElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6189146425075381973L;
	private int baseCost;
	
	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.BATTLE_ENGINE;
	}

	@Override
	public boolean hasStandardCost() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getBaseCost() {
		// TODO Auto-generated method stub
		return baseCost;
	}

	public void setBaseCost(int baseCost) {
		this.baseCost = baseCost;
	}

	@Override
	public String getCostString() {
		return String.valueOf(baseCost);
	}
}
