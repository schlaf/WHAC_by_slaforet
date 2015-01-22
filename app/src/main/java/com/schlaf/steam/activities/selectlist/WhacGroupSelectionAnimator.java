package com.schlaf.steam.activities.selectlist;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;

public class WhacGroupSelectionAnimator extends DefaultItemAnimator {

	@Override
	public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
			int fromX, int fromY, int toX, int toY) {
		return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
	}

}
