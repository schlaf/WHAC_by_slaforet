package com.schlaf.steam.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Weapon implements Serializable {

	/** serial */
	private static final long serialVersionUID = 122020575680122911L;

	private String name;
	
	/** the number of times the model has the weapon */
	private int count;

    private boolean blessed;
    private boolean buckler;
	private boolean frost;
	private boolean electricity;
	private boolean corrosion;
    private boolean criticalCorrosion;
    private boolean continuousCorrosion;
    private boolean disrupt;
    private boolean criticalDisrupt;
    private boolean fire;
    private boolean criticalFire;
	private boolean continuousFire;
    private boolean criticalFrost;
    private boolean magical;
    private boolean shield;
    private boolean weaponMaster;
	
	

	
	/** for warjacks only */
	private String location;
	
	private List<Capacity> capacities = new ArrayList<Capacity>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMagical() {
		return magical;
	}
	public void setMagical(boolean magical) {
		this.magical = magical;
	}
	public boolean isFire() {
		return fire;
	}
	public void setFire(boolean fire) {
		this.fire = fire;
	}
	public boolean isFrost() {
		return frost;
	}
	public void setFrost(boolean frost) {
		this.frost = frost;
	}
	public boolean isElectricity() {
		return electricity;
	}
	public void setElectricity(boolean electricity) {
		this.electricity = electricity;
	}
	public boolean isCorrosion() {
		return corrosion;
	}
	public void setCorrosion(boolean corrosion) {
		this.corrosion = corrosion;
	}
	public boolean isCriticalFire() {
		return criticalFire;
	}
	public void setCriticalFire(boolean criticalFire) {
		this.criticalFire = criticalFire;
	}
	public boolean isCriticalFrost() {
		return criticalFrost;
	}
	public void setCriticalFrost(boolean criticalFrost) {
		this.criticalFrost = criticalFrost;
	}
	public boolean isCriticalCorrosion() {
		return criticalCorrosion;
	}
	public void setCriticalCorrosion(boolean criticalCorrosion) {
		this.criticalCorrosion = criticalCorrosion;
	}
	public boolean isContinuousFire() {
		return continuousFire;
	}
	public void setContinuousFire(boolean continuousFire) {
		this.continuousFire = continuousFire;
	}
	public boolean isContinuousCorrosion() {
		return continuousCorrosion;
	}
	public void setContinuousCorrosion(boolean continuousCorrosion) {
		this.continuousCorrosion = continuousCorrosion;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public boolean hasLocation() {
		if (location != null && location.length() > 0) {
			return true;
		}
		return false;
	}
	public List<Capacity> getCapacities() {
		return capacities;
	}
	public void setCapacities(List<Capacity> capacities) {
		this.capacities = capacities;
	}
	public boolean isWeaponMaster() {
		return weaponMaster;
	}
	public void setWeaponMaster(boolean weaponMaster) {
		this.weaponMaster = weaponMaster;
	}
	public boolean isShield() {
		return shield;
	}
	public void setShield(boolean shield) {
		this.shield = shield;
	}
	public boolean isBuckler() {
		return buckler;
	}
	public void setBuckler(boolean buckler) {
		this.buckler = buckler;
	}

    public boolean isBlessed() {
        return blessed;
    }

    public void setBlessed(boolean blessed) {
        this.blessed = blessed;
    }

    public boolean isDisrupt() {
        return disrupt;
    }

    public void setDisrupt(boolean disrupt) {
        this.disrupt = disrupt;
    }

    public boolean isCriticalDisrupt() {
        return criticalDisrupt;
    }

    public void setCriticalDisrupt(boolean criticalDisrupt) {
        this.criticalDisrupt = criticalDisrupt;
    }

}
