/**
 * 
 */
package com.schlaf.steam.data;

import java.io.Serializable;
import java.util.ArrayList;

import com.schlaf.steam.activities.selectlist.selected.SpellCaster;

/**
 * @author S0085289
 *
 */
public class Warcaster extends ArmyCommander implements SpellCaster, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 531539191810843938L;
	
	private int focus;
	
	/** = negative army points, can be spent only for warjacks */
	private int warjackPoints;

	public Warcaster() {
		super();
		
	}
	
	public int getFocus() {
		return focus;
	}

	public void setFocus(int focus) {
		this.focus = focus;
	}

	public int getWarjackPoints() {
		return warjackPoints;
	}

	public void setWarjackPoints(int warjackPoints) {
		this.warjackPoints = warjackPoints;
	}

	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.WARCASTER;
	}



	@Override
	public boolean hasStandardCost() {
		return false;
	}

	@Override
	public int getBaseCost() {
		return warjackPoints;
	}

	@Override
	public String getCostString() {
		return "*" + warjackPoints;
	}

	
}
