package com.schlaf.steam.activities.damages;

public class CoordsColumn {
	int colNumber;
	private int x;
	private int y;

	public CoordsColumn(int colNumber, int x, int y) {
		this.colNumber = colNumber;
		this.x = x;
		this.y = y;
	}

	public int distanceCarreeFrom(int xx, int yy) {
		return ((xx - x) * (xx - x) + (yy - y) * (yy - y));
	}

	public CoordsColumn populate(int colNum, int xAxis, int cy) {
		this.colNumber = colNum;
		this.x = xAxis;
		this.y = cy;
		return this;
	}
	
}
