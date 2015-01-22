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
public class Warlock extends ArmyCommander implements SpellCaster, Serializable, Restrictable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3632332370368411476L;
	/** = negative army points, can be spent only for warbeasts */
	private int warbeastPoints;
	private int fury;
	
	/** some warlock can have only some beasts */
	private ArrayList<String> allowedUnitsToAttach = new ArrayList<String>();

	
	
	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.WARLOCK;
	}

	@Override
	public boolean hasStandardCost() {
		return false;
	}

	@Override
	public int getBaseCost() {
		// TODO Auto-generated method stub
		return warbeastPoints;
	}

	public int getWarbeastPoints() {
		return warbeastPoints;
	}

	public void setWarbeastPoints(int warbeastPoints) {
		this.warbeastPoints = warbeastPoints;
	}

	public int getFury() {
		return fury;
	}

	public void setFury(int fury) {
		this.fury = fury;
	}
	
	@Override
	public String getCostString() {
		return "*" + warbeastPoints;
	}

	@Override
	public ArrayList<String> getAllowedEntriesToAttach() {
		return allowedUnitsToAttach;
	}


	@Override
	public ArrayList<String> getTiersInWhichAllowedToAppear() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}



}
