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
public class Warbeast extends ArmyElement implements Serializable, Restrictable, SpellCaster {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8801266330061282696L;

	private int baseCost;
	
	/** textual represenation of damage grid */
	private String grid;
	
	private int fury;
	private int threshold;

	private ArrayList<String> allowedCastersToWorkFor = new ArrayList<String>();
	
	private ArrayList<String> tiersInWhicAllowedToAppear = new ArrayList<String>();
	
	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.WARBEAST;
	}

	@Override
	public boolean hasStandardCost() {
		return true;
	}

	@Override
	public int getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(int baseCost) {
		this.baseCost = baseCost;
	}

	public String getGrid() {
		return grid;
	}

	public void setGrid(String grid) {
		this.grid = grid;
	}

	@Override
	public ArrayList<String> getAllowedEntriesToAttach() {
		return allowedCastersToWorkFor;
	}

	public int getFury() {
		return fury;
	}

	public void setFury(int fury) {
		this.fury = fury;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public ArrayList<Spell> getSpells() {
		if (getModels()!=null && getModels().size() > 0) {
			return getModels().get(0).getSpells();
		}
		return new ArrayList<Spell>();
	}

	public void setSpells(ArrayList<Spell> spells) {
		throw new UnsupportedOperationException();
	}


	@Override
	public ArrayList<String> getTiersInWhichAllowedToAppear() {
		return tiersInWhicAllowedToAppear;
	}
	
	
	
	@Override
	public String getCostString() {
		return String.valueOf(baseCost);
	}

	@Override
	public boolean hasSpells() {
		return ! getSpells().isEmpty();
	}

}
