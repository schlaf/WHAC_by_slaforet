/**
 * 
 */
package com.schlaf.steam.data;

import java.io.Serializable;

/**
 * @author S0085289
 *
 */
public class RangedWeapon extends Weapon implements Serializable {

	/** serial */
	private static final long serialVersionUID = 3500890056823478091L;
	private boolean spray;
	private int range;
	private int rof;
	private int aoe;
	private int pow;
	
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getRof() {
		return rof;
	}
	public void setRof(int rof) {
		this.rof = rof;
	}
	public int getAoe() {
		return aoe;
	}
	public void setAoe(int aoe) {
		this.aoe = aoe;
	}
	public int getPow() {
		return pow;
	}
	public void setPow(int pow) {
		this.pow = pow;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(getName()).append("(ranged)-RNG=").append(range).append("-ROF=").append(rof).append("-AOE=").append(aoe).append("-POW=").append(pow).append("]");
		return sb.toString();
	}
	public boolean isSpray() {
		return spray;
	}
	public void setSpray(boolean spray) {
		this.spray = spray;
	}
	
	
}
