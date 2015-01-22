package com.schlaf.steam.activities.chrono;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.chrono.ChronoFragment.ChronoActivityInterface;

public class ChronoActivity extends FragmentActivity implements ChronoActivityInterface {

	private Handler handler = new Handler() {
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("BattleActivity", "onCreate");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.chronolayout);
	}

	@Override
	public void setInitialMinuteCount(int nbMinutes) {
		BattleSingleton.getInstance().reInitAndConfigChrono(nbMinutes);
		ChronoFragment chf = (ChronoFragment) getSupportFragmentManager().findFragmentById(R.id.chronoFragment);
		chf.updateDisplay();
	}

	@Override
	public void openChronoConfig(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playPause(int playerNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void setInitialMinuteCount() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void startChrono1() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void startChrono2() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void pauseChrono1() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void pauseChrono2() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void switchActivePlayer() {
//		// TODO Auto-generated method stub
//		
//	}
}
