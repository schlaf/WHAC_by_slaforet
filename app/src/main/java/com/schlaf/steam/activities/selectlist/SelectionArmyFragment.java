package com.schlaf.steam.activities.selectlist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;

import com.example.android.supportv7.widget.decorator.DividerItemDecoration;
import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

public class SelectionArmyFragment extends Fragment {

	public static final String ID = "SelectionArmyFragment";
	
	/** adapteur de la liste de sélection */
	// ListSelectionAdapter selectionAdapter;

	ArmySelectionChangeListener listener;
	
	RecyclerView mRecyclerView;
	LinearLayoutManager mLayoutManager;
	SelectionAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d("SelectionArmyFragment", "SelectionArmyFragment.onCreateView");
		
		View view = inflater.inflate(R.layout.alt_army_selection_fragment,
				container, false);


		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("SelectionArmyFragment", "SelectionArmyFragment.onAttach");
		super.onAttach(activity);
		if (activity instanceof ArmySelectionChangeListener) {
			listener = (ArmySelectionChangeListener) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implemenet ArmySelectionChangeListener");
		}
	}
	
	


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("SelectionArmyFragment", "SelectionArmyFragment.onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        
        final View header = getView().findViewById(R.id.headerDrawer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager((Context) listener);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        
        // specify an adapter (see also next example)
        adapter = new SelectionAdapter();
        mRecyclerView.setAdapter(adapter);
		
//		
        header.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.backToGroups(adapter.selectedGroup);
				v.setVisibility(View.GONE);
			}
		});
        
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

        	@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				
        		super.onScrolled(recyclerView, dx, dy);
        		
        		if (dy > -5 && dy < 15) {
        			return; // to small
        		}
				
				if (adapter.modeSelectGroup) {
					header.setVisibility(View.GONE);
					return;
				}
				
				if (dy < -5) {
					// scroll up
					if (adapter.modeSelectGroup == false &&  mLayoutManager.findFirstVisibleItemPosition() > 0 ) {
						if (header.getVisibility() == View.GONE) {
							final Animation animScrollDown = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.slide_top_to_bottom);
							header.setVisibility(View.VISIBLE);
							header.startAnimation(animScrollDown);
						}
					}
					if (mLayoutManager.findFirstVisibleItemPosition() == 0 ) {
						final Animation animScrollUp = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.slide_bottom_to_top);
						header.setVisibility(View.GONE);
						header.startAnimation(animScrollUp);
					}
				}
				
				if (dy > 15) {
					// scroll down
					if (adapter.modeSelectGroup == false) {
						if (header.getVisibility() == View.VISIBLE) {
							final Animation animScrollUp = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.slide_bottom_to_top);
							header.setVisibility(View.GONE);
							header.startAnimation(animScrollUp);
						}
					}
				}
				
				
			}
        	
		});
        
//		
//		// gestion de la liste sélectionnable
//		ExpandableListView selectionList = (ExpandableListView) getView()
//				.findViewById(R.id.army_list_selection);
//		selectionAdapter = new ListSelectionAdapter(getActivity());
//		selectionList.setAdapter(selectionAdapter);
//		// first verification on FA
//		selectionAdapter.notifyDataSetChanged();
		
	}
	
	public void notifyDataSetChanged() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		
		

	}
	
	/**
	 * collapse all selection groups
	 */
	public void collapseAll() {
//		int count = adapter.getGroupCount();
//		ExpandableListView selectionList = (ExpandableListView) getView()
//				.findViewById(R.id.army_list_selection);
//
//		for (int i = 0; i < count; i++) {
//			selectionList.collapseGroup(i);
//		}
	}
	
	
	public void slideElementToRight(SelectionEntry entry) {
		
	    if (false){ // android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	    	
	    	// weird bug on 2+ android version, so target only 4+ version

			ExpandableListView selectionList = (ExpandableListView) getView()
					.findViewById(R.id.army_list_selection);

			
			int first = selectionList.getFirstVisiblePosition();
			int last = selectionList.getLastVisiblePosition();
			
			for (int i = first; i <= last; i++) {
				selectionList.getItemAtPosition(first);
				final View v = selectionList.getChildAt(i - first);
				if (v.getTag() != null && v.getTag() instanceof SelectionEntry) {
					if ( ((SelectionEntry) v.getTag()).getId().equals(entry.getId())) {
						
						
						Log.e("SelectionArmyFragment", "item added at position : " + selectionList.getPositionForView(v));
						
							// play transition
							final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out);
							final Animation anim2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_top_to_bottom);
							anim.setAnimationListener(new Animation.AnimationListener() {              

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(Animation animation) {}

								@Override
								public void onAnimationEnd(Animation animation) {
									v.startAnimation(anim2);
								}
							});
							
							v.startAnimation(anim);
					}
				}
			}
				    	
	    	
	    }
		

	}

}
