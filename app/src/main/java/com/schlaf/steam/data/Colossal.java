package com.schlaf.steam.data;

public class Colossal extends Warjack {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2420923079485157927L;
	
	private String leftGrid;
	private String rightGrid;
	private boolean myrmidon;
	/** number of boxes in force field */
	private int forceField;
	
	@Override
	public ModelTypeEnum getModelType() {
		return ModelTypeEnum.COLOSSAL;
	}

	public String getLeftGrid() {
		return leftGrid;
	}

	public void setLeftGrid(String leftGrid) {
		this.leftGrid = leftGrid;
	}

	public String getRightGrid() {
		return rightGrid;
	}

	public void setRightGrid(String rightGrid) {
		this.rightGrid = rightGrid;
	}

	public boolean isMyrmidon() {
		return myrmidon;
	}

	public void setMyrmidon(boolean myrmidon) {
		this.myrmidon = myrmidon;
	}

	public int getForceField() {
		return forceField;
	}

	public void setForceField(int forceField) {
		this.forceField = forceField;
	}
}
