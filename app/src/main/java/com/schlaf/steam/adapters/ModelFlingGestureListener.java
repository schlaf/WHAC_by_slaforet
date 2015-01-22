package com.schlaf.steam.adapters;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.ArmySelectionChangeListener;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

/**
 * classe pour vérifier les gestures sur une entrée de model dans la liste
 * 
 * @author S0085289
 * 
 */
public class ModelFlingGestureListener implements OnTouchListener {

	private ArmySelectionChangeListener parentActivity;
	private GestureDetector gdt;
	private View view;

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (this.gdt.onTouchEvent(event)) {
			return false;
		}
		return true;
	}

	public ModelFlingGestureListener(View v, Activity parentActivity) {
		super();
		if ( ! (parentActivity instanceof ArmySelectionChangeListener)) {
			throw new UnsupportedOperationException("ModelFlingGestureListener must receive a ArmySelectionChangeListener as parent activity");
		}
		this.parentActivity = (ArmySelectionChangeListener) parentActivity;
		this.view = v;
		gdt = new GestureDetector(parentActivity , new GestureListener());
	}

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 60;
		private static final int SCROLL_MIN_DISTANCE = 100;
		private static final int SCROLL_MAX_VERTICAL= 30;
		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onRightToLeft();
				return true;
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onLeftToRight();
				return true;
			}
			if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				onBottomToTop();
				return true;
			} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				onTopToBottom();
				return true;
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			viewModelDetail();
		}
		
		@Override
        public boolean onDoubleTapEvent(MotionEvent e) {
			// doubleTapToAdd();
			return true;
        }


		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (e1.getX() - e2.getX() > SCROLL_MIN_DISTANCE
					&& Math.abs(e1.getY() - e2.getY()) < SCROLL_MAX_VERTICAL) {
				// onRightToLeft();
				return true;
			} else if (e2.getX() - e1.getX() > SCROLL_MIN_DISTANCE
					&& Math.abs(e1.getY() - e2.getY()) < SCROLL_MAX_VERTICAL) {
				// onLeftToRight();
				return true;
			}
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (parentActivity instanceof ViewCardActivityInterface) {
				if ( ((ViewCardActivityInterface) parentActivity).useSingleClick() ) {
					viewModelDetail();
				}
			}
			return false; // event consumed
		}
		
 

	}

	/**
	 * notify the parent to open the detail view of the model
	 */
	private void viewModelDetail() {
		
		if (view.getTag() != null) {
			if ( view.getTag() instanceof SelectionEntry) {
				parentActivity.viewSelectionDetail((SelectionEntry) view.getTag());		
			} else if (view.getTag() instanceof SelectedEntry) {
				SelectionEntry model = SelectionModelSingleton.getInstance()
						.getSelectionEntryById(
								((SelectedEntry) view.getTag() ).getId());
				parentActivity.viewSelectionDetail(model);
			}
		}
		
		
	}
	
	public void scrollToLeft() {
		System.out.println("scrollToLeft");
		//parentActivity.toSelectedArmy();
	}
	
	public void scrollToRight() {
		System.out.println("scrollToRight");
		//parentActivity.toSelectionArmy();
	}

	public void onRightToLeft() {
	}

	public void onLeftToRight() {
	}

	public void onBottomToTop() {
	}

	public void onTopToBottom() {
	}
	
	public void doubleTapToAdd() {
		// parentActivity.onModelAdded(model);
	}

}