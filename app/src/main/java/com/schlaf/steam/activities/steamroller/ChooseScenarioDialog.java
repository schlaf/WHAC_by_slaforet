/**
 * 
 */
package com.schlaf.steam.activities.steamroller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.steamroller.ViewScenarioFragment.ViewScenarioActivityInterface;
import com.schlaf.steam.adapters.ScenarioRowAdapter;
import com.schlaf.steam.data.Mission;

/**
 * @author S0085289
 * 
 */
public class ChooseScenarioDialog extends DialogFragment {

	public static final String ID = "ChooseScenarioDialog";
	
	private ViewScenarioActivityInterface mListener;
    RecyclerView recyclerView;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ViewScenarioActivityInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ViewScenarioActivityInterface");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = createView(inflater);
		
		if (getShowsDialog()) {
			getDialog().setTitle("Choose scenario");
		}
		
		return view;
	}



	private View createView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.choose_scenario_options, null);
		
		ScenarioRowAdapter adapterEntry = 
				new ScenarioRowAdapter(getActivity(), this, SteamRollerSingleton.getInstance().getScenarii());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setAdapter(adapterEntry);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

		return view;
	}


	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
	}

	
	boolean firstSelect = true;
	
	public void showScenario(Mission mission, int position) {

        SteamRollerSingleton.getInstance().setCurrentMission(mission);
        recyclerView.getAdapter().notifyDataSetChanged();
        mListener.viewScenario(mission);
	}


}
