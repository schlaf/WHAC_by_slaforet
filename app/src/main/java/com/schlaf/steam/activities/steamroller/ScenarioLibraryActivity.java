package com.schlaf.steam.activities.steamroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.steamroller.ViewScenarioFragment.ViewScenarioActivityInterface;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Mission;

public class ScenarioLibraryActivity extends ActionBarActivity implements ViewScenarioActivityInterface {
	
	public static final int CHOOSE_SCENARIO = 3567;
	public static final String INTENT_CHOOSE_SCENARIO = "choose_scenario";
	public static final String SCENARIO_NUMBER = "scenario_number";
    private static final String TAG =  "ScenarioLibraryActivity";

    boolean returnScenarioNumber = false;

    boolean showScenarioContent = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (! ArmySingleton.getInstance().isFullyLoaded()) {
            Log.e(TAG, "status not clean, exiting");
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName() );
            Log.e(TAG, "intent = " + i.toString());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(i);

            finish();
            return;
        }

		setContentView(R.layout.scenariolibrary_fragmented);

		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if (getIntent().getBooleanExtra(INTENT_CHOOSE_SCENARIO, false)) {
			returnScenarioNumber = true;
		}

		// getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.scenariiLibrary);
		getSupportActionBar().setLogo(R.drawable.stat_card);
		
		if (returnScenarioNumber) {
			findViewById(R.id.buttonChooseScenario).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.buttonChooseScenario).setVisibility(View.GONE);
		}
		
		if (findViewById(R.id.choose_scenario_zone) != null) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FrameLayout chooseScenarioZone = (FrameLayout) findViewById(R.id.choose_scenario_zone);
			chooseScenarioZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(ChooseScenarioDialog.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(ChooseScenarioDialog.ID));
			}
			ChooseScenarioDialog chooseScenarioFragment = new ChooseScenarioDialog();
			chooseScenarioFragment.setShowsDialog(false);
			fragmentTransaction.add(R.id.choose_scenario_zone, chooseScenarioFragment,
					ChooseScenarioDialog.ID);
			fragmentTransaction.commit();

		}
		
	}
	
	public void chooseScenario(View v) {
		Intent intent = new Intent();
		intent.putExtra(SCENARIO_NUMBER, "1" );
		setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}

	@Override
	public Mission getScenario() {
		return SteamRollerSingleton.getInstance().getCurrentMission();
	}

	@Override
	public void viewScenario(Mission mission) {

        showScenarioContent = true;
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if (findViewById(R.id.scenario_zone) != null) {

            if (findViewById(R.id.scenario_zone) != null) {
                FrameLayout chooseScenarioZone = (FrameLayout) findViewById(R.id.choose_scenario_zone);
                chooseScenarioZone.setVisibility(View.GONE);
            }

			// create new fragment
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FrameLayout cardZone = (FrameLayout) findViewById(R.id.scenario_zone);
			cardZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(ViewScenarioFragment.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(ViewScenarioFragment.ID));
			}
			ViewScenarioFragment viewScenarioFragment = new ViewScenarioFragment();
			fragmentTransaction.add(R.id.scenario_zone, viewScenarioFragment,
					ViewScenarioFragment.ID);
			fragmentTransaction.commit();

		}

	}

    @Override
    public void onBackPressed() {

        if (showScenarioContent) {
            // hide scenario details, show list
            FrameLayout chooseScenarioZone = (FrameLayout) findViewById(R.id.choose_scenario_zone);
            chooseScenarioZone.setVisibility(View.VISIBLE);

            FrameLayout detailsScenarioZone = (FrameLayout) findViewById(R.id.scenario_zone);
            detailsScenarioZone.setVisibility(View.GONE);
            showScenarioContent = false;
        } else {
            // close
            super.onBackPressed();
        }



    }
}
