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
	private boolean openFist;
	private boolean chain;
	
	
	private String pow;
    private String range;
    private String p_plus_s;
	
	public String getPow() {
		return pow;
	}
	public void setPow(String pow) {
		this.pow = pow;
	}
	public String getP_plus_s() {
		return p_plus_s;
	}
	public void setP_plus_s(String p_plus_s) {
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
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
	
}
