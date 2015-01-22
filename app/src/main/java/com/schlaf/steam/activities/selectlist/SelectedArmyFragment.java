package com.schlaf.steam.activities.selectlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedItem;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

public class SelectedArmyFragment extends Fragment {

	public static final String ID = "SelectedArmyFragment";

	/** adapteur de la liste de sélectionnés */
	ListSelectedAdapter selectedAdapter;
	ExpandableListView selectedList;
	ArmySelectionChangeListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d("SelectedArmyFragment", "SelectedArmyFragment.onCreateView");

		View view = inflater.inflate(R.layout.army_selected_fragment,
				container, false);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("SelectedArmyFragment", "SelectedArmyFragment.onAttach");
		super.onAttach(activity);
		if (activity instanceof ArmySelectionChangeListener
				&& activity instanceof ViewCardActivityInterface) {
			listener = (ArmySelectionChangeListener) activity;
		} else {
			throw new UnsupportedOperationException(
					activity.toString()
							+ " must implemenet ArmySelectionChangeListener + ViewCardActivityInterface");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	// method to expand all groups
	private void expandAll() {
		int count = selectedAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			selectedList.expandGroup(i);
		}
	}

	public void notifyDataSetChanged() {
		if (selectedAdapter != null) {
			selectedAdapter.notifyDataSetChanged();
			expandAll();
		}
	}

	/**
	 * private listener, can handle single and long click to display card view
	 * 
	 * @author S0085289
	 * 
	 */
	private class MyClickListener implements
			ExpandableListView.OnGroupClickListener,
			ExpandableListView.OnChildClickListener,
			AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			if (((ViewCardActivityInterface) listener).useSingleClick()) {
				return false;
			} else {
				return handleClick(position);
			}

		}

		private boolean handleClick(int position) {
			long packedPosition = selectedList
					.getExpandableListPosition(position);
			int type = ExpandableListView.getPackedPositionType(packedPosition);
			int groupPosition = ExpandableListView
					.getPackedPositionGroup(packedPosition);
			int childPosition = ExpandableListView
					.getPackedPositionChild(packedPosition);
			return handleClick(groupPosition, childPosition, type);
		}

		private boolean handleClick(int groupPosition, int childPosition,
				int type) {
			Log.d("SelectedArmyFragment", "handleClick");
			SelectedItem currentEntry = null;
			if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				currentEntry = (SelectedItem) selectedAdapter
						.getGroup(groupPosition);
			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				currentEntry = (SelectedItem) selectedAdapter.getChild(
						groupPosition, childPosition);
			}
			if (currentEntry != null && currentEntry instanceof SelectedEntry) {
				SelectionEntry model = SelectionModelSingleton.getInstance()
						.getSelectionEntryById(
								((SelectedEntry) currentEntry).getId());
				listener.viewSelectionDetail(model);
			}

			return true;
		}

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (((ViewCardActivityInterface) listener).useSingleClick()) {
				handleClick(groupPosition, childPosition,
						ExpandableListView.PACKED_POSITION_TYPE_CHILD);
			}
			return true;
		}

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			Log.d("SelectedArmyFragment", "onGroupClick");
			if (((ViewCardActivityInterface) listener).useSingleClick()) {
				handleClick(groupPosition, 0,
						ExpandableListView.PACKED_POSITION_TYPE_GROUP);
			}
			return true; // to prevent expand/collapse group
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// gestion de la liste sélectionnée
		selectedList = (ExpandableListView) getView().findViewById(
				R.id.army_list_selected);
		selectedAdapter = new ListSelectedAdapter(getActivity());
		selectedList.setAdapter(selectedAdapter);

		TextView emptyView = (TextView) getView().findViewById(android.R.id.empty);
		selectedList.setEmptyView(emptyView);
		
		MyClickListener listener = new MyClickListener();
		selectedList.setOnGroupClickListener(listener);
		selectedList.setOnChildClickListener(listener);
		selectedList.setOnItemLongClickListener(listener);
		
//		selectedList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
//			@Override
//			public void onGroupCollapse(int groupPosition) {
//				// TODO Auto-generated method stub
//				expandAll();
//			}
//		});

		expandAll();
	}

	
	public void deleteElement(SelectedEntry entry) {
		
		ExpandableListView selectedList = (ExpandableListView) getView()
				.findViewById(R.id.army_list_selected);

		
		int first = selectedList.getFirstVisiblePosition();
		int last = selectedList.getLastVisiblePosition();
		
		for (int i = first; i <= last; i++) {
			selectedList.getItemAtPosition(first);
			View v = selectedList.getChildAt(i - first);
			if (v.getTag() != null && v.getTag() instanceof SelectedEntry) {
				if ( ((SelectedEntry) v.getTag()) == entry) {
					
					Log.e("SelectedArmyFragment", "item added at position : " + selectedList.getPositionForView(v));
					
						// play transition
						final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
						anim.setAnimationListener(new Animation.AnimationListener() {              

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {}

							@Override
							public void onAnimationEnd(Animation animation) {
							}
						});
						
						v.startAnimation(anim);
				}
			}
		}
		
	}
	
}
