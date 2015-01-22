/**
 * 
 */
package com.schlaf.steam.activities.card;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;

/**
 * @author S0085289
 * 
 */
public class ViewCardActivity extends ActionBarActivity implements
		ViewCardActivityInterface {
	// implements OnGesturePerformedListener {

	public static final String MODEL_ID = "model_id";

	private ArmyElement armyElement;
	
	// private GestureLibrary gestureLib;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_card_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("ViewCardActivity", "onCreate" );
		super.onCreate(savedInstanceState);
		
		
		overridePendingTransition(R.anim.zoom_enter, R.anim.shrink_card_view);

		setContentView(R.layout.cardlayout);

		// extract data initialisation from Intent
		Intent intent = getIntent();
		String modelId = intent.getStringExtra(MODEL_ID);
		Log.d("ViewCardActivity", "model id = " + modelId);

		armyElement = ArmySingleton.getInstance().getArmyElements().get(modelId);

		FragmentManager fragmentManager = getSupportFragmentManager();

		if (findViewById(R.id.card_zone) != null) {
			// create new fragment
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			LinearLayout cardZone = (LinearLayout) findViewById(R.id.card_zone);
			cardZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(ViewCardFragment.ID));
			}
			ViewCardFragment viewCardFragment = new ViewCardFragment();
			fragmentTransaction.add(R.id.card_zone, viewCardFragment,
					ViewCardFragment.ID);
			fragmentTransaction.commit();
		}
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		switch (item.getItemId()) {
	        case R.id.menu_exit:
	        	close();
	            return true;
	        case android.R.id.home: 
	        	close();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}		
	
	public void close() {
		finish();
	}

	@Override
	public ArmyElement getArmyElement() {
		return armyElement;
	}

	public void removeViewCardFragment(View v) {
		finish();
	}

	@Override
	public boolean isCardfullScreen() {
		return true;
	}

	@Override
	public boolean isCardDoublePane() {
		Log.d("ViewCardActivity", "isCardDoublePane");
		Configuration config = getResources().getConfiguration();
		// two panels side by side if screen large enough && landscape
		DisplayMetrics metrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("ViewCardActivity", "widthPixels = " + metrics.widthPixels + " - density = " + metrics.density);
		if (metrics.widthPixels / metrics.density  >= 800 
				&& config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d("ViewCardActivity", "landscape --> doublePane");
			return true;
		}
		if (metrics.widthPixels / metrics.density  >= 800 
				&& config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.d("ViewCardActivity", "landscape --> doublePane");
			return true;
		}
		Log.d("ViewCardActivity", "not landscape or too small --> singlePane");
		return false;
	}

	@Override
	public void viewModelDetailInNewActivity(View v) {
		// do nothing, already in its own window...
	}

	@Override
	public boolean useSingleClick() {
		return false;
	}

	@Override
	public void viewModelDetail(View v) {
		// do nothing, already in its own window...
	}

	@Override
	public boolean canAddCardToBattle() {
		return false;
	}

	@Override
	public void addModelToBattle(View v) {
		// do nothing
	}

}
