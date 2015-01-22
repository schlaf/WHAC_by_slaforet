/**
 * 
 */
package com.schlaf.steam.data;

import java.io.Serializable;

/**
 * @author S0085289
 *
 */
public class UnitOption implements Serializable {

	/** if true, unit is min-sized, else max-sized */
	boolean minSize;
	/** add Unit Attachment */
	boolean addUA;
	/** add weapon Attachment */
	boolean addWA;
	/** weapon attachment count */
	private int countWA;
	
	
	
	public boolean isMinSize() {
		return minSize;
	}
	public void setMinSize(boolean minSize) {
		this.minSize = minSize;
	}
	public boolean isAddUA() {
		return addUA;
	}
	public void setAddUA(boolean addUA) {
		this.addUA = addUA;
	}
	public boolean isAddWA() {
		return addWA;
	}
	public void setAddWA(boolean addWA) {
		this.addWA = addWA;
	}
	public int getCountWA() {
		return countWA;
	}
	public void setCountWA(int countWA) {
		this.countWA = countWA;
	}
	
	
}
