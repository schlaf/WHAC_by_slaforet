package com.schlaf.steam.activities.selectlist.selection;

import com.schlaf.steam.data.Solo;

public class SelectionSolo extends SelectionEntry {

	/**
	 * serial
	 */
	private static final long serialVersionUID = -7080215946966419533L;

	/**
	 * must be attached to caster/warlock
	 */
	private boolean warcasterAttached;
	
	/**
	 * must be attached to a mercenary unit (valachev, attendant priest, ...)
	 */
	private boolean mercenaryUnitAttached;
	
	/** can be attached to some unit, not specifically to one. see "restrictions" to see which unit can have the solo */
	private boolean genericUnitAttached;
	
	/**
	 * can be attached as a weapon attachment to some unit, not specifically to one. see "restrictions" to see which unit can have the solo 
	 */
	private boolean weaponAttachement;
	
	/** this solo is a dragoon with mounted/dismount option */
	private boolean dragoon;
	
	/** cost of dismount option for dragoon */
	private int dismountCost;
	
	private boolean jackMarshall = false;
	private boolean journeyManWarcaster = false;
	private boolean lesserWarlock = false;
	
	
	public SelectionSolo(Solo baseUnit) {
		super(baseUnit);
		this.warcasterAttached = baseUnit.isWarcasterAttached();
		this.mercenaryUnitAttached = baseUnit.isMercenaryUnitAttached();
		this.genericUnitAttached = baseUnit.isGenericUnitAttached();
		this.weaponAttachement = baseUnit.isWeaponAttachement();
		
		if (baseUnit.isDragoon() && baseUnit.isDismountOption()) {
			setDragoon(true);
			setDismountCost(baseUnit.getDismountCost());
		}
		if (baseUnit.getModels().get(0).isJackMarshal()) {
			jackMarshall = true;
		}
		if (baseUnit.getModels().get(0).isJourneyManWarcaster()) {
			journeyManWarcaster = true;
		};
		if (baseUnit.getModels().get(0).isLesserWarlock()) {
			lesserWarlock = true;
		};

	}


	public boolean isDragoon() {
		return dragoon;
	}

	public void setDragoon(boolean dragoon) {
		this.dragoon = dragoon;
	}

	public int getDismountCost() {
		return dismountCost;
	}

	public void setDismountCost(int dismountCost) {
		this.dismountCost = dismountCost;
	}

	public boolean isJackMarshall() {
		return jackMarshall;
	}

	public void setJackMarshall(boolean jackMarshall) {
		this.jackMarshall = jackMarshall;
	}

	public boolean isJourneyManWarcaster() {
		return journeyManWarcaster;
	}

	public void setJourneyManWarcaster(boolean journeyManWarcaster) {
		this.journeyManWarcaster = journeyManWarcaster;
	}

	public boolean isLesserWarlock() {
		return lesserWarlock;
	}

	public void setLesserWarlock(boolean lesserWarlock) {
		this.lesserWarlock = lesserWarlock;
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


	public boolean isWeaponAttachement() {
		return weaponAttachement;
	}


	public void setWeaponAttachement(boolean weaponAttachement) {
		this.weaponAttachement = weaponAttachement;
	}

}
