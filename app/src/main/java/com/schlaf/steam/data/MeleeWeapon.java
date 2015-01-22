/**
 * 
 */
package com.schlaf.steam.data;

import java.io.Serializable;


/**
 * @author S0085289
 *
 */
public class MeleeWeapon extends Weapon implements Serializable {

	/** serial */
	private static final long serialVersionUID = 7401124131758623722L;
	private boolean reach;
	private boolean openFist;
	private boolean chain;
	
	
	private int pow;
	private int p_plus_s;
	
	public boolean isReach() {
		return reach;
	}
	public void setReach(boolean reach) {
		this.reach = reach;
	}
	public int getPow() {
		return pow;
	}
	public void setPow(int pow) {
		this.pow = pow;
	}
	public int getP_plus_s() {
		return p_plus_s;
	}
	public void setP_plus_s(int p_plus_s) {
		this.p_plus_s = p_plus_s;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(getName()).append("(melee)-POW=").append(pow).append("-P+S=").append(p_plus_s).append("]");
		return sb.toString();
	}

	public boolean isOpenFist() {
		return openFist;
	}
	public void setOpenFist(boolean openFist) {
		this.openFist = openFist;
	}

	public boolean isChain() {
		return chain;
	}
	public void setChain(boolean chain) {
		this.chain = chain;
	}
	
	
}
