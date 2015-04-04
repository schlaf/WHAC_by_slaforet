package com.schlaf.steam.activities.battleresult;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battleresult.BattleResultDetailFragment.ViewBattleResultActivityInterface;
import com.schlaf.steam.activities.battleresult.BattleResultsListFragment.ChooseBattleResultListener;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.storage.StorageManager;

public class BattleResultsActivity extends ActionBarActivity implements ChooseBattleResultListener, ViewBattleResultActivityInterface {

	private static final String TAG = "BattleResultsActivity"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("BattleActivity", "onCreate");
        super.onCreate(savedInstanceState);

        if (! ArmySingleton.getInstance().isFullyLoaded()) {
            Log.e(TAG, "status not clean, exiting" );
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName() );
            Log.e(TAG, "intent = " + i.toString());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(i);

            finish();
            return;
        }



		setContentView(R.layout.battleresults_fragmented);
		
		getSupportActionBar().setTitle(R.string.battle_results);
		
		// mask detail zone at startup
		if (findViewById(R.id.resultDetail_zone) != null) {
			findViewById(R.id.resultDetail_zone).setVisibility(View.GONE);
		}
		
	}

	   /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_sort_date:
				sortByDate();
				return true;
	        case R.id.menu_sort_player:
	        	sortByPlayer();
	            return true;
	        case R.id.menu_sort_winner:
	        	sortByWin();
	            return true;
	        case R.id.menu_export_stats:
	        	exportStats();
	            return true;
	        case R.id.menu_export_by_mail:
	            exportByMail();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	

    
	private void exportStats() {
		StorageManager.exportStats(getApplicationContext());
		Toast.makeText(this, R.string.battle_stats_exported_on_sd_card, Toast.LENGTH_SHORT).show();
	}
	
	private void exportByMail() {
		
		String strFile = StorageManager.exportStats(getApplicationContext());
		
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "unknown_recipient@mail.com" });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WHAC battle results export file");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + strFile));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "this is the export of your battle results");
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
		
	}
	
	private void sortByDate() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentByTag(BattleResultsListFragment.ID) != null) {
			BattleResultsListFragment listFragment = (BattleResultsListFragment) fragmentManager.findFragmentByTag(BattleResultsListFragment.ID);
			listFragment.sortByDate();
		}
		
	}

	private void sortByWin() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentByTag(BattleResultsListFragment.ID) != null) {
			BattleResultsListFragment listFragment = (BattleResultsListFragment) fragmentManager.findFragmentByTag(BattleResultsListFragment.ID);
			listFragment.sortByWin();
		}
		
	}
	
	private void sortByPlayer() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentByTag(BattleResultsListFragment.ID) != null) {
			BattleResultsListFragment listFragment = (BattleResultsListFragment) fragmentManager.findFragmentByTag(BattleResultsListFragment.ID);
			listFragment.sortByPlayer();
		}
		
	}

	
	@Override
	public void viewResultDetail(BattleResult result) {
		Log.d(TAG, "viewResultDetail");

		FragmentManager fragmentManager = getSupportFragmentManager();
		
		
		

		if (findViewById(R.id.resultDetail_zone) != null) {
			// create new fragment
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FrameLayout cardZone = (FrameLayout) findViewById(R.id.resultDetail_zone);
			cardZone.setVisibility(View.VISIBLE);

			if (fragmentManager.findFragmentByTag(BattleResultDetailFragment.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(BattleResultDetailFragment.ID));
			}
			
			// set current battle AFTER closing the fragment, so it can update the "notes" part with the good battle!
			
			BattleResultSingleton.getInstance().setCurrentBattleResult(result);
			
			BattleResultDetailFragment resultDetailFragment = new BattleResultDetailFragment();
			fragmentTransaction.add(R.id.resultDetail_zone, resultDetailFragment,
					BattleResultDetailFragment.ID);
			fragmentTransaction.commit();

		} else {

			// open new activity
			BattleResultSingleton.getInstance().setCurrentBattleResult(result);
			viewBattleResultInNewActivity(null);

		}

	}


	@Override
	public BattleResult getBattleResult() {
		return BattleResultSingleton.getInstance().getCurrentBattleResult();
	}

	public void deleteBattleResult(final BattleResult result) {
		
	  	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(getResources().getString(R.string.delete_the_battle_result) + result.getArmyName());
    	builder.setTitle(R.string.confirm_delete_result);

    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	
//            	if (result.equals(currentBR)) {
//                	android.support.v4.app.FragmentManager fragmentManager =   getSupportFragmentManager();
//                	FragmentTransaction fragmentTransaction = fragmentManager
//        					.beginTransaction();
//                	BattleResultDetailFragment detailFragment = (BattleResultDetailFragment) fragmentManager.findFragmentByTag(BattleResultDetailFragment.ID);
//                	if (detailFragment != null) {
//	            		fragmentTransaction.remove(detailFragment);
//	            		fragmentTransaction.commit();
//                	}
//            	}
            	
                // User clicked OK button
            	if (StorageManager.deleteBattleResult(getApplicationContext(), result.getFilename())) {
                	// notify fragment...
                	android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                	BattleResultsListFragment listFragment = (BattleResultsListFragment) fm.findFragmentByTag(BattleResultsListFragment.ID);
                	listFragment.notifyResultDeletion(result);
            	} else {
            		Toast.makeText(getApplicationContext(), R.string.deletion_failed, Toast.LENGTH_SHORT).show();
            	}
            }
        });
    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
		
	}
	

	@Override
	public void viewBattleResultInNewActivity(View v) {
		Intent intent = new Intent(this, ViewBattleResultActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if (findViewById(R.id.resultDetail_zone) != null) {
			// create new fragment
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();


			if (fragmentManager.findFragmentByTag(BattleResultDetailFragment.ID) != null) {
				fragmentTransaction.remove(fragmentManager
						.findFragmentByTag(BattleResultDetailFragment.ID));
				
				FrameLayout detailZone = (FrameLayout) findViewById(R.id.resultDetail_zone);
				detailZone.setVisibility(View.GONE);
			
				fragmentTransaction.commit();
			} else {
				BattleResultsActivity.this.finish();
			}
		} else {
			BattleResultsActivity.this.finish();
		}
		
	}
	
	
	
}