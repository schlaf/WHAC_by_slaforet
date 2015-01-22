package com.schlaf.steam.data;

import java.io.Serializable;

public class MountWeapon extends Weapon implements Serializable {

	/** serial */
	private static final long serialVersionUID = -6863816110225265913L;
	/** pow of mount */
	private int pow;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(getName()).append("(melee)-POW=").append(pow).append("]");
		return sb.toString();
	}	
	
	public int getPow() {
		return pow;
	}

	public void setPow(int pow) {
		this.pow = pow;
	}
}
