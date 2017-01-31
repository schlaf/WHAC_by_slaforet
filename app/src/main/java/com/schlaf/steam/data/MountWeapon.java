package com.schlaf.steam.data;

import java.io.Serializable;

public class MountWeapon extends Weapon implements Serializable {

	/** serial */
	private static final long serialVersionUID = -6863816110225265913L;

	/** range of mount */
	private String range;

	/** pow of mount */
	private String pow;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(getName()).append("(melee)-POW=").append(pow).append("]");
		return sb.toString();
	}	
	
	public String getPow() {
		return pow;
	}

	public void setPow(String pow) {
		this.pow = pow;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
}
