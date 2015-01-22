package com.schlaf.steam.data;

import java.io.Serializable;

public class DamageBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1247287896146636419L;

	private boolean damaged;
	
	/** indicates a change is pending, but not committed */
	private boolean currentlyChangePending;
	/** indicates this box is "virtually" damaged (waiting to confirm status) */
	private boolean damagedPending;
	
	private WarmachineDamageSystemsEnum system;
	private DamageGrid parent;
	

	public DamageBox(String systemCode, DamageGrid parent) {
		system = WarmachineDamageSystemsEnum.fromCode(systemCode);
		this.parent = parent;
	}

	public boolean isDamaged() {
		return damaged;
	}

	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
		parent.notifyBoxChange();
	}

	public WarmachineDamageSystemsEnum getSystem() {
		return system;
	}

	public void setSystem(WarmachineDamageSystemsEnum system) {
		this.system = system;
	}
	
	/**
	 * invert damage
	 */
	public void flipFlop() {
		if (currentlyChangePending) {
			currentlyChangePending = false; // restore status
			damagedPending = damaged;
		} else {
			damagedPending = !damaged;
			currentlyChangePending = true;
		}
//		damaged = !damaged;
//		currentlyChangePending = false;
//		damagedPending = damaged;
		parent.notifyBoxChange();
	}
	
	public String toString() {
		if (currentlyChangePending) {
			if (damagedPending) {
				return "[R]";
			} else {
				return "[G]";
			}
		}
		if (damaged) {
			return "[X]";
		} else {
			return "[ ]";
		}
	}

	public boolean isCurrentlyChangePending() {
		return currentlyChangePending;
	}

	public void setCurrentlyChangePending(boolean currentlyChangePending) {
		this.currentlyChangePending = currentlyChangePending;
		parent.notifyBoxChange();
	}

	public boolean isDamagedPending() {
		return damagedPending;
	}

	public void setDamagedPending(boolean damagedPending) {
		this.damagedPending = damagedPending;
		parent.notifyBoxChange();
	}
	
	public void copyFrom(DamageBox source) {
		currentlyChangePending = source.isCurrentlyChangePending();
		damaged = source.isDamaged();
		damagedPending = source.isDamagedPending();
	}
}