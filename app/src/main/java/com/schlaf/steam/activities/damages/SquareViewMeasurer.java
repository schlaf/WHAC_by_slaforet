package com.schlaf.steam.activities.damages;

import android.view.View.MeasureSpec;

public final class SquareViewMeasurer {
	private int preferredSize;
	private int chosenDimension;
	
	public SquareViewMeasurer(int preferredSize) {
		this.preferredSize = preferredSize;
	}
	
	public int getChosenDimension() {
		return chosenDimension;
	}
	
	public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		chosenDimension = Math.min(chosenWidth, chosenHeight);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}

	private int getPreferredSize() {
		return preferredSize;
	}
}

