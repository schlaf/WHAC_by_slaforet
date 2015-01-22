package com.schlaf.steam.adapters;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.schlaf.steam.activities.selectlist.ArmySelectionChangeListener;
import com.schlaf.steam.activities.selectlist.PopulateArmyListActivity;

/**
 * classe pour vérifier les gestures sur une page, n'intercepte que les scrolls large
 * 
 * @author S0085289
 * 
 */
public abstract class ScrollGestureListener implements OnTouchListener {

	private PopulateArmyListActivity parentActivity;
	private final GestureDetector gdt = new GestureDetector(
			new GestureListener());

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		System.out.println("onTouch (ScrollGestureListener)");
		if (this.gdt.onTouchEvent(event)) {
			System.out.println("return false");
			return false;
		}
		System.out.println("return true");
		return true;
	}

	public ScrollGestureListener(PopulateArmyListActivity parentActivity) {
		super();
		this.parentActivity = parentActivity;
	}


	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SCROLL_MIN_DISTANCE = 100;
		private static final int SCROLL_MAX_VERTICAL= 30;

		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			System.out.println("onfling");
			// don't treat fling events
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			System.out.println("onscroll");
			if (e1.getX() - e2.getX() > SCROLL_MIN_DISTANCE
					&& Math.abs(e1.getY() - e2.getY()) < SCROLL_MAX_VERTICAL) {
				scrollToLeft();
				return true;
			} else if (e2.getX() - e1.getX() > SCROLL_MIN_DISTANCE
					&& Math.abs(e1.getY() - e2.getY()) < SCROLL_MAX_VERTICAL) {
				scrollToRight();
				return true;
			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onShowPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}

	public abstract void scrollToLeft();
	
	public abstract void scrollToRight();
}