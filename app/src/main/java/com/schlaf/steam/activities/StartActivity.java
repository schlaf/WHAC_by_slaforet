package com.schlaf.steam.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.battleresult.BattleResultsActivity;
import com.schlaf.steam.activities.battleselection.BattleSelector;
import com.schlaf.steam.activities.card.CardLibraryActivity;
import com.schlaf.steam.activities.card.ViewCardActivity;
import com.schlaf.steam.activities.chrono.ChronoActivity;
import com.schlaf.steam.activities.collection.MyCollectionActivity;
import com.schlaf.steam.activities.donation.DonationActivity;
import com.schlaf.steam.activities.importation.ImportMK3Activity;
import com.schlaf.steam.activities.importation.ImportSelector;
import com.schlaf.steam.activities.managelists.ManageArmyListsActivity;
import com.schlaf.steam.activities.preferences.PreferenceActivity;
import com.schlaf.steam.activities.selectlist.PopulateArmyListActivity;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.steamroller.ScenarioLibraryActivity;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.StorageManager;

import java.io.IOException;
import java.io.InputStream;

public class StartActivity extends ActionBarActivity implements ChangeFactionListener, ChooseArmyListener, LoadActivityInterface {
	
	
	private static final String TAG = "StartActivity";
	
	private static final String NOTIFY_NEWS = "notify_news";
	private static final String NOTIFICATION_DONE_V200FINAL = "v2.0.0Final";
	private static final String DONATION = "donation";
	private static final String REMIND_COUNT = "count";
	private static final String REMIND_COUNT_THRESHOLD = "threshold";
	

	
	private int secretActivationCount = 0;
	private final int SECRET_ACTIVATION_THRESHOLD = 7;
	
	StartInitializeThread initThread;
	ProgressDialog progressBar;
	
	boolean checkTiers = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.main, null);
        
		setContentView(view);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
		getSupportActionBar().setTitle("W&H Army Creator");


		if (! ArmySingleton.getInstance().isFullyLoaded()) {
			
			progressBar = new ProgressDialog(this);
			progressBar.setCancelable(false);
			progressBar.setMessage(getString(R.string.loading_data));
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(8);

	        initThread = new StartInitializeThread(this, (SteamPunkRosterApplication) getApplication(), progressBar);
	        initThread.execute();
		}
        
		
		final SharedPreferences notifyNews = getSharedPreferences(NOTIFY_NEWS, Context.MODE_PRIVATE);
		if ( ! notifyNews.contains(NOTIFICATION_DONE_V200FINAL)) {
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
	    	String currentVersion = getResources().getString(R.string.currentVersion);
			alert.setTitle("New WHAC version " + currentVersion);
			
			View versionView = inflater.inflate(R.layout.version_layout, null);
			
			TextView tv = (TextView) versionView.findViewById(R.id.tvSite);
		    tv.setText(Html.fromHtml("<a href=http://whac.forumactif.org/> WHAC Official forum "));
		    tv.setMovementMethod(LinkMovementMethod.getInstance());
			
		    WebView wvChanges= (WebView) versionView.findViewById(R.id.wvChanges);
		    
		    try {
	            InputStream fin = getAssets().open("version200.html");
	                byte[] buffer = new byte[fin.available()];
	                fin.read(buffer);
	                fin.close();
	                wvChanges.loadData(new String(buffer), "text/html", "UTF-8");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		    
			alert.setView(versionView);

			alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Editor editor = notifyNews.edit();
					editor.putBoolean(NOTIFICATION_DONE_V200FINAL, true);
					editor.commit();
				}
			});

			alert.show();
		} else {
			// check launch count for donation
			final SharedPreferences donation = getSharedPreferences(DONATION, Context.MODE_PRIVATE);
			
			int remindCount = donation.getInt(REMIND_COUNT, 0);
			int threshold = donation.getInt(REMIND_COUNT_THRESHOLD, 5);
			if ( remindCount > threshold) {
				remindCount = 0;
				threshold++;
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setIcon(R.drawable.donate);
				alert.setTitle(R.string.like_whac);
				alert.setMessage(R.string.would_donate);
				
				alert.setPositiveButton(R.string.yes_donate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						donate(null);
					}
				});
				alert.setNegativeButton(R.string.no_thanks , new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
				alert.show();
			} else {
				remindCount ++;
			}

			Editor editor = donation.edit();
			editor.putInt(REMIND_COUNT, remindCount);
			editor.putInt(REMIND_COUNT_THRESHOLD, threshold);
			editor.commit();
			
		}

	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //getSupportMenuInflater().inflate(R.menu.startup_menu, menu);
        return true;
    }    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_prefs:
	    		Intent intent = new Intent(this, PreferenceActivity.class);
	    		startActivity(intent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	    
    
    public void startArmy(View view) {

    	DialogFragment dialog = new ChooseFactionDialog();
		dialog.show(getSupportFragmentManager(), "ChooseFactionActivity");
    	
//        Intent intent = new Intent(this, ChooseFactionActivity.class);
//        startActivity(intent);
    }

    public void startBattle(View view) {
//        Intent intent = new Intent(this, ChooseArmyListDialog.class);
//        startActivityForResult(intent, BattleActivity.CHOOSE_ARMY_LIST_DIALOG);

        Intent intent = new Intent(this, BattleSelector.class);
        startActivity(intent);

    }
    
    public void cardLibrary(View view) {
    	Intent intent = new Intent(this, CardLibraryActivity.class);
    	startActivity(intent);

    }

    public void scenarioLibrary(View view) {
    	Intent intent = new Intent(this, ScenarioLibraryActivity.class);
    	startActivity(intent);

    }
    
    public void viewCard(View view) {
        Intent intent = new Intent(this, ViewCardActivity.class);
        startActivity(intent);
    }


	public void loadArmy(View v) {
		
		DialogFragment dialog = new ChooseArmyListDialog();
		dialog.show(getSupportFragmentManager(), "ChooseArmyListDialog");
		
//		Intent intent = new Intent(this, ChooseArmyListDialog.class);
//		startActivityForResult(intent, ChooseArmyListDialog.CHOOSE_ARMY_LIST_DIALOG);
		
	}

	public void editArmy(View v) {
		Intent intent = new Intent(this, ManageArmyListsActivity.class);
		startActivityForResult(intent, ManageArmyListsActivity.CHOOSE_ARMY_LIST_DIALOG);
		
	}

	public void chrono(View v) {
		Intent intent = new Intent(this, ChronoActivity.class);
		startActivity(intent);
	}
	
	public void preferences(View v) {
		Intent intent = new Intent(this, PreferenceActivity.class);
		startActivity(intent);
	}
	
	public void battleResults(View v) {
		Intent intent = new Intent(this, BattleResultsActivity.class);
		startActivity(intent);
	}	
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == BattleActivity.CHOOSE_ARMY_LIST_DIALOG) {
//			if (resultCode == RESULT_OK) {
//				String army_name = data.getStringExtra(BattleActivity.INTENT_ARMY);
//				Toast.makeText(this, "Battle selected : " + army_name, Toast.LENGTH_SHORT)
//		          .show();
//				
//				// SelectionModelSingleton.getInstance().loadStatus(getApplicationContext(), army_name);
//				
//				// open battle activity
//				Intent intent = new Intent(this, BattleActivity.class);
//				intent.putExtra(BattleActivity.INTENT_ARMY, army_name);
//				intent.putExtra(BattleActivity.INTENT_CREATE_BATTLE_FROM_ARMY, true);
//				startActivity(intent);
//				
//			}
//		}
//	}	

	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.exit_the_application);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				StartActivity.this.finish();
			}
		});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		alert.show();
	}

	@Override
	public void onChangeFaction(Faction newFaction) {
		Intent intent = new Intent(this, PopulateArmyListActivity.class);
		intent.putExtra(PopulateArmyListActivity.INTENT_FACTION, newFaction.getId());
		intent.putExtra(PopulateArmyListActivity.INTENT_START_NEW_ARMY, true);
		startActivity(intent);
	}

	@Override
	public void onArmyListSelected(ArmyListDescriptor army) {
		String path = army.getFilePath();

		StorageManager.loadArmyList(path,
				SelectionModelSingleton.getInstance());

		// open populate list activity
		FactionNamesEnum faction = SelectionModelSingleton.getInstance()
				.getFaction();
		Intent intent = new Intent(this, PopulateArmyListActivity.class);
		intent.putExtra(PopulateArmyListActivity.INTENT_FACTION,
				faction.getId());
		intent.putExtra(PopulateArmyListActivity.INTENT_START_NEW_ARMY, false);
		startActivity(intent);
	}

	@Override
	public void onArmyListDeleted(final ArmyListDescriptor army) {
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(getResources().getString(R.string.askDeleteList) + army.getFileName());
    	builder.setTitle(R.string.confirmDeleteList);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyList(getApplicationContext(), army.getFilePath())) {
                	// notify fragment...
                	android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                	ChooseArmyListDialog listDialog = (ChooseArmyListDialog) fm.findFragmentByTag("ChooseArmyListDialog");
                	listDialog.notifyArmyListDeletion(army);
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
	
//	@Override
//	public void onArmyListDeleted(ArmyListDescriptor army) {
//		
//		if ( StorageManager.deleteArmyList(getApplicationContext(), army.getFilePath())) {
//			ChooseArmyListDialog chooseArmyDialog = (ChooseArmyListDialog) getSupportFragmentManager().findFragmentByTag("ChooseArmyListDialog");
//			chooseArmyDialog.notifyArmyListDeletion(army);
//		} else {
//			Toast.makeText(getApplicationContext(),
//					"Army deletion failed", Toast.LENGTH_SHORT).show();
//		}
//		
//		
//	}
	
	public void importDataFile(View v) {
		
	       Intent intent = new Intent(this, ImportMK3Activity.class);
	        startActivity(intent);

	}
	
	public void version(View v) {
		
		LayoutInflater inflater = getLayoutInflater();
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	String currentVersion = getResources().getString(R.string.currentVersion);
		alert.setTitle("WHAC version " + currentVersion);
		
		View versionView = inflater.inflate(R.layout.version_layout, null);
		
		TextView tv = (TextView) versionView.findViewById(R.id.tvSite);
	    tv.setText(Html.fromHtml("<a href=http://whac.forumactif.org/> WHAC Official forum "));
	    tv.setMovementMethod(LinkMovementMethod.getInstance());
		
	    WebView wvChanges= (WebView) versionView.findViewById(R.id.wvChanges);
	    
	    try {
            InputStream fin = getAssets().open("version.html");
                byte[] buffer = new byte[fin.available()];
                fin.read(buffer);
                fin.close();
                wvChanges.loadData(new String(buffer), "text/html", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
	    
	    // wvChanges.loadData(getResources().getString(R.string.currentChanges), "text/html", "UTF-8");
	    
		alert.setView(versionView);
		alert.show();

	}
	
	public void collection(View v) {
		Intent intent = new Intent(this, MyCollectionActivity.class);
		startActivity(intent);
	}


	public void donate(View v) {
		Intent intent = new Intent(this, DonationActivity.class);
		startActivity(intent);
	}
	
	
	public void activateSecretImport(View v) {
		secretActivationCount ++;
		if (secretActivationCount > SECRET_ACTIVATION_THRESHOLD) {
			Toast.makeText(getApplicationContext(), "hello secret!", Toast.LENGTH_SHORT ).show();
		}
	}

	@Override
	public void onArmyDirectoryDeleted(final ArmyListDirectory directory) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setMessage(getResources().getString(R.string.askDeleteFolder) + directory.getTextualPath());
    	builder.setTitle(R.string.confirmDeleteFolder);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyFolder(getApplicationContext(),directory.getFullpath())) {
                	// notify fragment...
                	ChooseArmyListDialog chooseArmyDialog = (ChooseArmyListDialog) getSupportFragmentManager().findFragmentByTag("ChooseArmyListDialog");
        			chooseArmyDialog.notifyDirectoryDeletion(directory);
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
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (initThread != null && ! initThread.isCancelled()) {
			Log.e(TAG, "cancel initThread");
			initThread.setParent(null);
			initThread.cancel(true);
		}
	}


    /**
     * disable click on action buttons that can lead to crash if data not present...
     */
    public void blockButtons() {

        findViewById(R.id.button_load_army).setClickable(false);
        findViewById(R.id.button_build_army).setClickable(false);
        findViewById(R.id.button_launch_battle).setClickable(false);
        findViewById(R.id.button_card_library).setClickable(false);
        findViewById(R.id.button_scenario_library).setClickable(false);
        findViewById(R.id.button_battle_results).setClickable(false);
        findViewById(R.id.button_collection).setClickable(false);

        findViewById(R.id.button_load_army).setEnabled(false);
        findViewById(R.id.button_build_army).setEnabled(false);
        findViewById(R.id.button_launch_battle).setEnabled(false);
        findViewById(R.id.button_card_library).setEnabled(false);
        findViewById(R.id.button_scenario_library).setEnabled(false);
        findViewById(R.id.button_battle_results).setEnabled(false);
        findViewById(R.id.button_collection).setEnabled(false);



    }


    @Override
    public boolean forceDownload() {
        // this activity does not force download... use preferences
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ArmySingleton.getInstance().isFullyLoaded()) {
            restaureButtons();
        } else {
            blockButtons();
        }
    }

    public void restaureButtons() {
        findViewById(R.id.button_load_army).setClickable(true);
        findViewById(R.id.button_build_army).setClickable(true);
        findViewById(R.id.button_launch_battle).setClickable(true);
        findViewById(R.id.button_card_library).setClickable(true);
        findViewById(R.id.button_scenario_library).setClickable(true);
        findViewById(R.id.button_battle_results).setClickable(true);
        findViewById(R.id.button_collection).setClickable(true);

        findViewById(R.id.button_load_army).setEnabled(true);
        findViewById(R.id.button_build_army).setEnabled(true);
        findViewById(R.id.button_launch_battle).setEnabled(true);
        findViewById(R.id.button_card_library).setEnabled(true);
        findViewById(R.id.button_scenario_library).setEnabled(true);
        findViewById(R.id.button_battle_results).setEnabled(true);
        findViewById(R.id.button_collection).setEnabled(true);

    }

}