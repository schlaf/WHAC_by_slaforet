package com.schlaf.steam.activities.card;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChangeFactionListener;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;

public class CardLibraryActivity extends ActionBarActivity implements ViewCardActivityInterface{
	
	public static final String LIBRARY_PREF = "library_preferences";
	public static final String LIBRARY_PREF_FACTION_KEY = "faction";
	public static final String LIBRARY_PREF_ENTRY_TYPE_KEY = "entryType";
    private static final String TAG = "CardLibraryActivity";

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("CardLibraryActivity", "onCreate" );
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

		setContentView(R.layout.cardlibrary_fragmented);

		FragmentManager fragmentManager = getSupportFragmentManager();

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(R.string.card_library);
		getSupportActionBar().setLogo(R.drawable.ic_cards);
		
		if (findViewById(R.id.choose_card_zone) != null) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FrameLayout chooseCardZone = (FrameLayout) findViewById(R.id.choose_card_zone);
			chooseCardZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(ChooseCardFromLibraryDialog.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(ChooseCardFromLibraryDialog.ID));
			}
			ChooseCardFromLibraryDialog chooseCardFragment = new ChooseCardFromLibraryDialog();
			chooseCardFragment.setShowsDialog(false);
			fragmentTransaction.add(R.id.choose_card_zone, chooseCardFragment,
					ChooseCardFromLibraryDialog.ID);
			fragmentTransaction.commit();

		}
		
		// don't add viewcard fragment at startup!
		
//		if (findViewById(R.id.card_zone) != null) {
//			// create new fragment
//			FragmentTransaction fragmentTransaction = fragmentManager
//					.beginTransaction();
//
//			FrameLayout cardZone = (FrameLayout) findViewById(R.id.card_zone);
//			cardZone.setVisibility(View.VISIBLE);
//
//			if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
//				fragmentTransaction.remove(fragmentManager
//						.findFragmentByTag(ViewCardFragment.ID));
//			}
//			ViewCardFragment viewCardFragment = new ViewCardFragment();
//			fragmentTransaction.add(R.id.card_zone, viewCardFragment,
//					ViewCardFragment.ID);
//			fragmentTransaction.commit();
//		}
		
	}

	@Override
	public ArmyElement getArmyElement() {
		return SelectionModelSingleton.getInstance().getCurrentlyViewedElement();
	}

	@Override
	public boolean isCardfullScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCardDoublePane() {
		// double pane only if : portrait + large screen
		Configuration config = getResources().getConfiguration();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (metrics.widthPixels * metrics.density  > 600 
				&& config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}
		if (metrics.widthPixels * metrics.density  >= 1024 
				&& config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}

		return false;	}

	@Override
	public void viewModelDetailInNewActivity(View v) {
		Intent intent = new Intent(this, ViewCardActivity.class);
		intent.putExtra(ViewCardActivity.MODEL_ID, getArmyElement().getId());
		startActivity(intent);
	}

	@Override
	public boolean useSingleClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void viewModelDetail(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		if (findViewById(R.id.card_zone) != null) {
			// create new fragment
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FrameLayout cardZone = (FrameLayout) findViewById(R.id.card_zone);
			cardZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(ViewCardFragment.ID));
			}
			ViewCardFragment viewCardFragment = new ViewCardFragment();
			fragmentTransaction.add(R.id.card_zone, viewCardFragment,
					ViewCardFragment.ID);
			fragmentTransaction.commit();

		} else {

			// open new activity
			Intent intent = new Intent(this, ViewCardActivity.class);
			intent.putExtra(ViewCardActivity.MODEL_ID, getArmyElement().getId());
			startActivity(intent);

		}
	}
	
	
	public void removeViewCardFragment(View v) {
		Log.d("CardLibraryActivity","removeViewCardFragment");
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
			fragmentTransaction.remove(fragmentManager.findFragmentByTag(ViewCardFragment.ID));
		}
		fragmentTransaction.commit();
		
		View cardZone = findViewById(R.id.card_zone);
		cardZone.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		SharedPreferences save = getSharedPreferences(LIBRARY_PREF, MODE_PRIVATE);
		Editor ed = save.edit();
		ed.putString(LIBRARY_PREF_FACTION_KEY, CardLibrarySingleton.getInstance().getFaction().getId());
		ed.putString(LIBRARY_PREF_ENTRY_TYPE_KEY, CardLibrarySingleton.getInstance().getEntryType().name());
	    ed.commit();

		
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
