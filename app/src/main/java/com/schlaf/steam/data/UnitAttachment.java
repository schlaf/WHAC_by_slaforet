package com.schlaf.steam.data;


public class UnitAttachment extends ArmyElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 182490286293520781L;
	private int cost;
	
	/** is jack marshall and grants jack marshall to unit */
	private boolean jackMarshall;

	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.UNIT_ATTACHMENT;
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

	public boolean isJackMarshall() {
		return jackMarshall;
	}

	public void setJackMarshall(boolean jackMarshall) {
		this.jackMarshall = jackMarshall;
	}
	
	@Override
	public String getCostString() {
		return String.valueOf(cost);
	}

}
