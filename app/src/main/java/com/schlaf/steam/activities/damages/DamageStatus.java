package com.schlaf.steam.activities.damages;

import java.io.Serializable;

public class DamageStatus implements Serializable {

	private static final String[] DAMAGE_COLORS = new String[] {
		"#00FF14",
		"#FFF702",  
		"#FFA906",
		"#FC4A00", 
		"#F72604",
		
	};
	
	/** serial */
	private static final long serialVersionUID = -6579215308235412774L;
	/** total hit points */
	private int hitPoints;
	/** number of damage */
	private int damagedPoints;
	/** convenient label */
	private String label;

	public DamageStatus(int hitPoints, int damagedPoints, String label) {
		super();
		this.hitPoints = hitPoints;
		this.damagedPoints = damagedPoints;
		this.label = label;
	}

	public int getHitPoints() {
		return hitPoints;
	}

	public int getDamagedPoints() {
		return damagedPoints;
	}
	
	public int getRemainingPoints() {
		return hitPoints - damagedPoints;
	}

	public String getLabel() {
		return label;
	}
	
	public float percentageDead() {
		return ( (float)damagedPoints / (float) hitPoints);
	}
	
	public String toString() {
		return (hitPoints - damagedPoints) + " / " + hitPoints;
	}
	
	public String toHTMLString() {
		StringBuffer sb = new StringBuffer(128);
		sb.append("<font color=\"");
		
		int colorIndex = (int) (DAMAGE_COLORS.length * percentageDead()) + 1;
		if (percentageDead() == 0) { // display green only if 0 damages
			colorIndex = 0;
		} else if (colorIndex > DAMAGE_COLORS.length -1 ) {
			colorIndex = DAMAGE_COLORS.length -1;
		}
		sb.append(DAMAGE_COLORS[colorIndex]);
		sb.append("\">");
		sb.append( (hitPoints - damagedPoints)).append("/").append(hitPoints);
		sb.append("</font>");
		
		return sb.toString();
	}

}
