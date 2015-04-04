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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.supportv7.widget.decorator.DividerItemDecoration;
import com.schlaf.steam.R;

public class SelectionEntriesArmyFragment extends Fragment {

	public static final String ID = "SelectionEntriesArmyFragment";
	
	/** adapteur de la liste de sélection */
	// ListSelectionAdapter selectionAdapter;

	ArmySelectionChangeListener listener;
	
	RecyclerView mRecyclerView;
	LinearLayoutManager mLayoutManager;
    SelectionElementAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(ID, "SelectionArmyFragment.onCreateView");

        return inflater.inflate(R.layout.army_selection_entries_fragment,
				container, false);

	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(ID, "SelectionArmyFragment.onAttach");
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
        Log.d(ID, "SelectionArmyFragment.onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        final View header = getView().findViewById(R.id.headerDrawer);

        SelectionGroup group = SelectionModelSingleton.getInstance().getSelectedGroup();

        if (group != null) {
            ((TextView) getView().findViewById(R.id.groupLabel)).setText(group.getType().getTitle());
            ((ImageView) getView().findViewById(R.id.groupImage)).setImageResource(group.getFaction().getLogoResource());
        }





        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager((Context) listener);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        // specify an adapter (see also next example)
        adapter = new SelectionElementAdapter();
        mRecyclerView.setAdapter(adapter);

//		
        header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                adapter.backToGroups(v);
                v.setVisibility(View.GONE);
            }
        });
    }
        

	public void notifyDataSetChanged() {
		if (adapter != null) {
            adapter.updateEntries();
			adapter.notifyDataSetChanged();
		}
	}
	


}
