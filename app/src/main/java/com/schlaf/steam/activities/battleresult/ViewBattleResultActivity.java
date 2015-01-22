package com.schlaf.steam.activities.battleresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battleresult.BattleResultDetailFragment.ViewBattleResultActivityInterface;

public class ViewBattleResultActivity extends FragmentActivity implements ViewBattleResultActivityInterface {

	public static final String RESULT = "result";

	private static final String TAG = "ViewResultActivity";
	
	@Override
	public BattleResult getBattleResult() {
		return BattleResultSingleton.getInstance().getCurrentBattleResult();
	}

	@Override
	public void viewBattleResultInNewActivity(View v) {
		// do nothing, already in its window...
		
	}
	

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate" );
		super.onCreate(savedInstanceState);

		setContentView(R.layout.battle_result_layout);

		// extract data initialisation from Intent
		Intent intent = getIntent();
		BattleResult result = getBattleResult(); //intent.getExtra(RESULT);
		Log.d(TAG, "result = " + result.getDescription());

		FragmentManager fragmentManager = getSupportFragmentManager();

	}

}
