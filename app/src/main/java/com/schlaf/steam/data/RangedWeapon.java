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
	private String range;
	private String rof;
	private String aoe;
	private String pow;
	
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getRof() {
		return rof;
	}
	public void setRof(String rof) {
		this.rof = rof;
	}
	public String getAoe() {
		return aoe;
	}
	public void setAoe(String aoe) {
		this.aoe = aoe;
	}
	public String getPow() {
		return pow;
	}
	public void setPow(String pow) {
		this.pow = pow;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(getName()).append("(ranged)-RNG=").append(range).append("-ROF=").append(rof).append("-AOE=").append(aoe).append("-POW=").append(pow).append("]");
		return sb.toString();
	}

	
}
