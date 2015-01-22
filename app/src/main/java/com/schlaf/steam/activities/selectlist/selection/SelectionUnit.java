package com.schlaf.steam.activities.selectlist.selection;

import java.io.Serializable;

import com.schlaf.steam.data.Unit;

public class SelectionUnit extends SelectionEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5685755598148053114L;

	private boolean jackMarshall = false;
	private boolean jackMarshallViaUA = false;
	
	public SelectionUnit(Unit unit) {
		super(unit);
		
		if ( unit.isVariableSize()) {
			variableSize = true;
		}
		
		minSize = unit.getBaseNumberOfModels();
		maxSize = unit.getFullNumberOfModels();
		
		minCost = unit.getBaseCost();
		maxCost = unit.getFullCost();
		
		possibleUA = unit.isUnitAttachmentAllowed();
		possibleWA = unit.isWeaponAttachmentAllowed();
		
		maxWACount = unit.getMaxWAAllowed();
	}

	boolean variableSize;
	int minSize;
	int maxSize;
	
	int minCost;
	int maxCost;
	
	boolean possibleUA;
	boolean possibleWA;
	
	int maxWACount;

	public void addOneMinUnit() {
		countSelected ++;
		costSelected += minCost; 
		if (countSelected > 0) {
			setSelected(true);
		}
	}
	
	public void addOneMaxUnit() {
		countSelected ++;
		costSelected += maxCost; 
		if (countSelected > 0) {
			setSelected(true);
		}
	}

	@Override
	public String toHTMLCostString() {
		
		// FA(<font color=\"white\"><B>2</B></font>) &nbsp Cost(<font color=\"white\"><B>5/8</B></font>
		
		StringBuffer sb = new StringBuffer();
		
		int discount = 0;
		if (alteredCost != baseCost) {
			discount = baseCost - alteredCost;
		}
		
		if (discount > 0) {
			sb.append("<font color=\"blue\">");
		} else {
			sb.append("<font color=\"white\">");
		}
		
		if (isVariableSize()) {
			sb.append(minCost-discount).append("/").append(maxCost-discount);
		} else {
			sb.append(minCost-discount);
		}
		sb.append("</font><font color=\"grey\">PC</font>");
		return sb.toString();
	}	
	
	public boolean isVariableSize() {
		return variableSize;
	}

	public void setVariableSize(boolean variableSize) {
		this.variableSize = variableSize;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isPossibleUA() {
		return possibleUA;
	}

	public void setPossibleUA(boolean possibleUA) {
		this.possibleUA = possibleUA;
	}

	public boolean isPossibleWA() {
		return possibleWA;
	}

	public void setPossibleWA(boolean possibleWA) {
		this.possibleWA = possibleWA;
	}

	public int getMaxWACount() {
		return maxWACount;
	}

	public void setMaxWACount(int maxWACount) {
		this.maxWACount = maxWACount;
	}

	public int getMinCost() {
		return minCost;
	}

	public void setMinCost(int minCost) {
		this.minCost = minCost;
	}

	public int getMaxCost() {
		return maxCost;
	}

	public void setMaxCost(int maxCost) {
		this.maxCost = maxCost;
	}

	public boolean isJackMarshall() {
		return jackMarshall;
	}

	public void setJackMarshall(boolean jackMarshall) {
		this.jackMarshall = jackMarshall;
	}

	public boolean isJackMarshallViaUA() {
		return jackMarshallViaUA;
	}

	public void setJackMarshallViaUA(boolean jackMarshallViaUA) {
		this.jackMarshallViaUA = jackMarshallViaUA;
	}
	
}
