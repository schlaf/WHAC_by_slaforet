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
public class Solo extends ArmyElement implements Serializable, Restrictable, SpellCaster {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1287377807715612909L;

	private int baseCost;
	
	/** 
	 * if solo must be attached to another model 
	 */
	private boolean warcasterAttached;
	
	/**
	 * must be attached to a mercenary unit (valachev, attendant priest, ...)
	 */
	private boolean mercenaryUnitAttached;
	
	/**
	 * can be attached to some unit, not specifically to one. see "restrictions" to see which unit can have the solo 
	 */
	private boolean genericUnitAttached;
	
	/**
	 * can be attached as a weapon attachment to some unit, not specifically to one. see "restrictions" to see which unit can have the solo 
	 */
	private boolean weaponAttachement;
	
	/** if dragoon, gains an alternate profile (dismounted) and (optionally) an alternate cost (with dismount option) */
	private boolean dragoon;
	
	/** if dragoon and has dismount option */
	private boolean dismountOption;
	
	/** cost with dismount option */
	private int dismountCost;
	
	
	/** if solo is an attachment, list the units it can be attached to */
	private ArrayList<String> allowedUnitsToAttach = new ArrayList<String>();
	
	private ArrayList<Spell> spells = new ArrayList<Spell>(10);
	
	private ArrayList<String> tiersInWhicAllowedToAppear = new ArrayList<String>();
	
	@Override
	public ModelTypeEnum getModelType() {
		if (mercenaryUnitAttached || genericUnitAttached) {
			return ModelTypeEnum.UNIT_ATTACHMENT;
		}
		if (weaponAttachement) {
			return ModelTypeEnum.WEAPON_ATTACHMENT;
		}
		
		return ModelTypeEnum.SOLO;
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

	public boolean isDragoon() {
		return dragoon;
	}

	public void setDragoon(boolean dragoon) {
		this.dragoon = dragoon;
	}

	public boolean isDismountOption() {
		return dismountOption;
	}

	public void setDismountOption(boolean dismountOption) {
		this.dismountOption = dismountOption;
	}

	public int getDismountCost() {
		return dismountCost;
	}

	public void setDismountCost(int dismountCost) {
		this.dismountCost = dismountCost;
	}

	public boolean isWarcasterAttached() {
		return warcasterAttached;
	}

	public void setWarcasterAttached(boolean warcasterAttached) {
		this.warcasterAttached = warcasterAttached;
	}

	public boolean isMercenaryUnitAttached() {
		return mercenaryUnitAttached;
	}

	public void setMercenaryUnitAttached(boolean mercenaryUnitAttached) {
		this.mercenaryUnitAttached = mercenaryUnitAttached;
	}

	public boolean isGenericUnitAttached() {
		return genericUnitAttached;
	}

	public void setGenericUnitAttached(boolean genericUnitAttached) {
		this.genericUnitAttached = genericUnitAttached;
	}

	@Override
	public ArrayList<String> getAllowedEntriesToAttach() {
		return allowedUnitsToAttach;
	}

	@Override
	public ArrayList<Spell> getSpells() {
		return spells;
	}

	@Override
	public void setSpells(ArrayList<Spell> spells) {
		this.spells = spells;
	}

	@Override
	public ArrayList<String> getTiersInWhichAllowedToAppear() {
		return tiersInWhicAllowedToAppear;
	}

	public boolean isWeaponAttachement() {
		return weaponAttachement;
	}

	public void setWeaponAttachement(boolean weaponAttachement) {
		this.weaponAttachement = weaponAttachement;
	}
	
	@Override
	public String getCostString() {
		return String.valueOf(baseCost);
	}	
	
}
