package com.schlaf.steam.activities.steamroller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.data.Mission;

public class ViewScenarioFragment extends Fragment {
	
	public static final String ID = "ViewScenarioFragment";

	public interface ViewScenarioActivityInterface {
		public Mission getScenario();

		public void viewScenario(Mission mission);
	}
	
	private static final String TAG = "ViewScenarioFragment";
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof ViewScenarioActivityInterface) {
			Log.d(TAG, "onAttach received " + activity.getClass().getName());
		} else {
			throw new UnsupportedOperationException("ViewScenarioFragment requires a ViewScenarioActivityInterface as parent activity");
		}
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		fillScenario( ((ViewScenarioActivityInterface) getActivity()).getScenario());
	}

	private void fillScenario(Mission scenario) {

        if (scenario == null) {
            return;
        }

		TextView tvTitle = (TextView) getView().findViewById(R.id.textTitle);
		tvTitle.setText(scenario.getName());
		
		WebView tvRules = (WebView) getView().findViewById(R.id.webViewRules);
		// tvRules.loadData(scenario.getSpecialRulesText(), "text/html", "UTF-8");
		tvRules.loadDataWithBaseURL(null, scenario.getSpecialRulesText(), "text/html", "UTF-8", null);
		WebView tvVictory = (WebView) getView().findViewById(R.id.webViewVictory);
		tvVictory.loadDataWithBaseURL(null, scenario.getVictoryText(), "text/html", "UTF-8", null);
		
		WebView tvTips = (WebView) getView().findViewById(R.id.webViewTips);
		tvTips.loadDataWithBaseURL(null, scenario.getTacticalTipsText(), "text/html", "UTF-8", null);
		
		ImageView imageViewMap = (ImageView) getView().findViewById(R.id.imageViewMap);
		imageViewMap.setImageResource(scenario.getMapResourceId());
		
		ImageView imageViewObjective = (ImageView) getView().findViewById(R.id.imageViewCard);
		if (scenario.getObjectiveResourceId() != 0) {
			imageViewObjective.setImageResource(scenario.getObjectiveResourceId());
			imageViewObjective.setVisibility(View.VISIBLE);
		} else {
			imageViewObjective.setVisibility(View.GONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		
		View view = inflater.inflate(R.layout.scenario_fragment,
				container, false);

		return view;		
	}
	
}
