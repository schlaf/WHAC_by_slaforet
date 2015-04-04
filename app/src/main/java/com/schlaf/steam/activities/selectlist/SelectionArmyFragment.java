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
    SelectionGroupAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d("SelectionArmyFragment", "SelectionArmyFragment.onCreateView");

        return inflater.inflate(R.layout.alt_army_selection_fragment,
				container, false);

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

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager((Context) listener);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        adapter = new SelectionGroupAdapter();
        adapter.resetGroups();
        mRecyclerView.setAdapter(adapter);
		
//		
        header.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// adapter.backToGroups(adapter.selectedGroup, v);
				v.setVisibility(View.GONE);
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

    public void notifyGroupRecalculate() {
        if (adapter != null) {
            adapter.resetGroups();
        }
    }
	
	public void notifyDataSetChanged() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	
}
