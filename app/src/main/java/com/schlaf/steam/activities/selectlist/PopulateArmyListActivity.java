/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.common.view.DepthPageTransformer;
import com.example.android.common.view.SlidingTabLayout;
import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseArmyListener;
import com.schlaf.steam.activities.ChooseFileToSaveDialog;
import com.schlaf.steam.activities.ChooseFileToSaveDialog.ChooseFileToSaveListener;
import com.schlaf.steam.activities.PreferenceConstants;
import com.schlaf.steam.activities.battle.BattleActivity;
import com.schlaf.steam.activities.card.ChooseCardFromLibraryDialog;
import com.schlaf.steam.activities.card.ViewCardActivity;
import com.schlaf.steam.activities.card.ViewCardFragment;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.collection.MyCollectionActivity;
import com.schlaf.steam.activities.selectlist.ChooseArmyOptionsDialog.ArmySettingListener;
import com.schlaf.steam.activities.selectlist.ChooseAttachActivity.ChooseAttachInterface;
import com.schlaf.steam.activities.selectlist.selected.SelectedArmyCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Contract;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.StorageManager;
import com.schlaf.steam.tabs.TabsAdapter;
import com.schlaf.steam.tier.DisplayContractFragment;
import com.schlaf.steam.tier.DisplayTierFragment;

/**
 * @author S0085289
 * 
 */
public class PopulateArmyListActivity extends ActionBarActivity
		implements ArmySelectionChangeListener, ViewCardActivityInterface,
		ArmySettingListener, ChooseFileToSaveListener, ChooseArmyListener, ChooseAttachInterface {

    private static final String TAG = "PopulateArmyListActiv";

	private static final String CARD_FRAGMENT = "card_fragment";
    private static final String POPULATE_ACTIVITY_STATE = "POPULATE_ACTIVITY_STATE";
    public static String INTENT_START_NEW_ARMY = "start_new_army";
	public static String INTENT_FIRT_START = "first_start";
	public static String INTENT_FACTION = "faction";

	TabsAdapter mTabsAdapter; // the adapter for swiping pages
	ViewPager pager; // the pager that handles fragments swipe


    private SelectionModelSingleton selectionModelSingleton;


	/**
	 * indicate to open card view on single click (if false, use longclick)
	 */
	private boolean selectionModeSingleClick;

	/**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
	private SlidingTabLayout mSlidingTabLayout;

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.build_army_menu, menu);

		handleMenuVisibility(menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// call invalidateOptionsMenu() to force menu reload!
		super.onPrepareOptionsMenu(menu);

		handleMenuVisibility(menu);
		return true;
	}

	/**
	 * handle visibility of various menu depending on application status
	 * 
	 * @param menu
	 */
	private void handleMenuVisibility(Menu menu) {
		if (SelectionModelSingleton.getInstance().getFaction()
				.equals(FactionNamesEnum.MERCENARIES)
				|| SelectionModelSingleton.getInstance().getFaction()
						.equals(FactionNamesEnum.MINIONS)) {
			menu.findItem(R.id.menu_contract).setVisible(true);
		} else {
			menu.findItem(R.id.menu_contract).setVisible(false);
		}

		if (SelectionModelSingleton.getInstance().isSaved()) {
			menu.findItem(R.id.menu_save).setVisible(false);
			menu.findItem(R.id.menu_battle).setVisible(true);
		} else {
			menu.findItem(R.id.menu_save).setVisible(true);
			menu.findItem(R.id.menu_battle).setVisible(false);
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getSupportMenuInflater().inflate(R.menu.build_army_menu, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
//		case R.id.menu_collapse:
//			collapseSelection();
//			return true;
		case R.id.menu_save:
			saveArmy();
			return true;
		case R.id.menu_save_as:
			saveArmyAs();
			return true;
		case R.id.menu_cancel:
			cancel();
			return true;
		case R.id.menu_tiers:
			chooseTier();
			return true;
		case R.id.menu_contract:
			chooseContract();
			return true;
		case R.id.menu_battle:
			battle();
			return true;
		case R.id.menu_prefs:
			chooseOptions(null);
			return true;
		case R.id.menu_card_reference:
			openCardLibrary();
			return true;
		case R.id.menu_export_army_list:
			exportList();
			return true;
		case android.R.id.home:
			navigateHome();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("NewApi")
	private void exportList() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.export_list);
	    builder.setMessage(Html.fromHtml(SelectionModelSingleton.getInstance().getSelectedListResume()));
	    
	    builder.setNegativeButton(R.string.cancel, null);
	    
	    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
	    	builder.setNeutralButton(R.string.copy, new OnClickListener() {
	    		@SuppressLint("NewApi")
				@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    			ClipboardManager clipboard = (ClipboardManager)
	    					getSystemService(Context.CLIPBOARD_SERVICE);
	    			ClipData clip = ClipData.newHtmlText("army content", SelectionModelSingleton.getInstance().getSelectedListResume(), SelectionModelSingleton.getInstance().getSelectedListResume());
	    			clipboard.setPrimaryClip(clip);
	    		}
	    	});
	    }

	    builder.setPositiveButton(R.string.export_mail, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("text/html");
		        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "unknown_recipient@mail.com" });
		        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WHAC army list ");
		        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(SelectionModelSingleton.getInstance().getSelectedListResume()));
		        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.sendmail)));
			}
		});
	    
	    builder.show();
		
	}

	private void openCardLibrary() {

		FragmentManager fm = getSupportFragmentManager();
		ChooseCardFromLibraryDialog dialog = new ChooseCardFromLibraryDialog();
		dialog.setShowsDialog(true);
		dialog.show(fm, "Card library dialog");
	}

	/**
	 * launch the battle activity with current army
	 */
	private void battle() {
		if (!SelectionModelSingleton.getInstance().isSaved()) {
			Toast.makeText(getApplicationContext(),
					"Army not saved : save before launching battle",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// open battle activity
		Intent intent = new Intent(this, BattleActivity.class);
		intent.putExtra(BattleActivity.INTENT_CREATE_BATTLE_FROM_ARMY, true);
		intent.putExtra(BattleActivity.INTENT_BATTLE_NAME, SelectionModelSingleton
				.getInstance().getArmyFileName());
		intent.putExtra(BattleActivity.INTENT_ARMY_PATH, SelectionModelSingleton
				.getInstance().getArmyFilePath());
		startActivity(intent);

	}

	/**
	 * ask for tier (open popup dialog)
	 */
	private void chooseTier() {

		final List<Tier> tiers = ArmySingleton.getInstance().getTiers(
				SelectionModelSingleton.getInstance().getFaction());

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.choose_tier);
		alert.setItems(getTierLabels(tiers),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							SelectionModelSingleton.getInstance()
									.setCurrentTiers(null);
						} else {
							SelectionModelSingleton.getInstance()
									.setCurrentTiers(tiers.get(which - 1));
						}

                        notifyMaybeGroupChange();
						notifyArmyChange();
					}
				});
		alert.show();
	}

	/**
	 * ask for contract (open popup dialog)
	 */
	private void chooseContract() {

		final List<Contract> contracts = ArmySingleton.getInstance()
				.getContracts(
						SelectionModelSingleton.getInstance().getFaction());

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.choose_contract);
		alert.setItems(getContractLabels(contracts),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							SelectionModelSingleton.getInstance()
									.setCurrentContract(null);
						} else {
							SelectionModelSingleton.getInstance()
									.setCurrentContract(
											contracts.get(which - 1));
						}
                        notifyMaybeGroupChange();
                        notifyArmyChange();
					}
				});
		alert.show();
	}

	/**
	 * return available tiers labels
	 * 
	 * @param tiers
	 * @return labels for tiers (aka titles)
	 */
	private String[] getTierLabels(List<Tier> tiers) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getResources().getString(R.string.none));
		for (Tier tier : tiers) {
			// String casterName =
			// ArmySingleton.getInstance().getArmyElement(tier.getCasterId()).getFullName();
			String tiersEntry = tier.getTitle();
			result.add(tiersEntry);
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * return available contract labels
	 * 
	 * @param  contracts
	 * @return String[] labels for contracts (aka titles)
	 */
	private String[] getContractLabels(List<Contract> contracts) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getResources().getString(R.string.none));
		for (Contract contract : contracts) {
			String contractName = contract.getTitle();
			result.add(contractName);
		}
		return result.toArray(new String[result.size()]);
	}

	private void cancel() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("You are about to clean the army list");
		alert.setMessage("Reset current army selection?");
		alert.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				PopulateArmyListActivity.this.resetArmyList();
			}
		});
		alert.setNegativeButton(R.string.cancel,
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();

	}

	private void resetArmyList() {
		Toast.makeText(getApplicationContext(), "Cancel army",
				Toast.LENGTH_SHORT).show();
		SelectionModelSingleton.getInstance().cleanAll();
		notifyArmyChange();
	}

	private void saveArmy() {

		if (!SelectionModelSingleton.getInstance().hasValidFileName()) {
			saveArmyAs();
		} else {
			StorageManager.saveArmyList(
					PopulateArmyListActivity.this.getApplicationContext(),
					SelectionModelSingleton.getInstance());
			Toast.makeText(getApplicationContext(), "Army saved",
					Toast.LENGTH_SHORT).show();
			updateTitles();
		}
	}

	private void saveArmyAs() {

		ChooseFileToSaveDialog dialog = new ChooseFileToSaveDialog();
		
		String proposal_file_name = "new_army";
		for (SelectedEntry entry : SelectionModelSingleton.getInstance()
				.getSelectedEntries()) {
			if (entry instanceof SelectedArmyCommander) {
				SelectionEntry selection = SelectionModelSingleton.getInstance().getSelectionEntryById(entry.getId());
				proposal_file_name = StorageManager.fixFileNameForSave(selection.getShortLabel());

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-kkmm",
						Locale.getDefault());
				if (proposal_file_name.length() > 8) {
					proposal_file_name = proposal_file_name.substring(0, 8);
				}
				proposal_file_name = proposal_file_name + "-" 
						+ sdf.format(new Date());
			}
		}
		
		dialog.setProposedFileName(proposal_file_name);
		
		dialog.show(getSupportFragmentManager(), "ChooseFileToSaveDialog");
		
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//		alert.setTitle(R.string.save_as);
//		alert.setMessage(R.string.choose_filename);
//
//		// Set an EditText view to get user input
//		final EditText input = new EditText(this);
//
//		
//
//
//		input.setText(proposal_file_name);
//
//		alert.setView(input);
//
//		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				String value = input.getText().toString();
//
//				String validFileName = StorageManager.fixFileNameForSave(value);
//				SelectionModelSingleton.getInstance().setArmyFileName(
//						validFileName);
//
//				StorageManager.saveArmyList(
//						PopulateArmyListActivity.this.getApplicationContext(),
//						SelectionModelSingleton.getInstance());
//				Toast.makeText(getApplicationContext(), "Army saved",
//						Toast.LENGTH_SHORT).show();
//
//				// update status
//				PopulateArmyListActivity.this.updateTitles();
//
//			}
//		});
//
//		alert.setNegativeButton(R.string.cancel,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						// Canceled.
//						Toast.makeText(getApplicationContext(),
//								"Army not saved", Toast.LENGTH_SHORT).show();
//					}
//				});
//
//		alert.show();
	}


    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e(TAG, "onRestart");
        // Activity being restarted from stopped state
    }


    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first

        Log.e(TAG, "onStop");
        // Save the note's current draft, because the activity is stopping
        // and we want to be sure the current note progress isn't lost.
//        ContentValues values = new ContentValues();
//        values.put("armyPath", );
        // values.put(NotePad.Notes.COLUMN_NAME_TITLE, getCurrentNoteTitle());

        SharedPreferences settings = getSharedPreferences(POPULATE_ACTIVITY_STATE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("armyPath", SelectionModelSingleton.getInstance().getArmyFilePath());
        // Commit the edits!
        editor.commit();
    }


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate");
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


		setContentView(R.layout.populate_army_list_layout_fragmented);

		Intent intent = getIntent();
		String factionId = intent.getStringExtra(INTENT_FACTION);

		FactionNamesEnum factionEnum = FactionNamesEnum.getFaction(factionId);

		SelectionModelSingleton.getInstance().setFaction(factionEnum);

		// getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO );
		getSupportActionBar().setLogo(R.drawable.ic_edit);
        getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("");

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOffscreenPageLimit(3);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			pager.setPageTransformer(true, new DepthPageTransformer());
		}


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.LightGrey));

//		PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pagerTab);
//		tabStrip.setTabIndicatorColor(getResources().getColor(R.color.AndroidBlue));
		
		if (mTabsAdapter == null) {
			mTabsAdapter = new TabsAdapter(this, pager);	
		}
		
		
		if (mTabsAdapter.getTabIndexForId(SelectionArmyFragment.ID) == -1) {
			SelectionArmyFragment selectionFragment = new SelectionArmyFragment();
			mTabsAdapter.addTab(SelectionArmyFragment.ID,  getResources().getString(R.string.selection_choices),
					selectionFragment, null);
		}
		
		if (mTabsAdapter.getTabIndexForId(SelectedArmyFragment.ID) == -1) {
			SelectedArmyFragment selectedFragment = new SelectedArmyFragment();
			mTabsAdapter.addTab(SelectedArmyFragment.ID,  getResources().getString(R.string.selected_army),
					selectedFragment, null);
		}
		mTabsAdapter.notifyDataSetChanged();
		
		// BEGIN_INCLUDE (setup_slidingtablayout)
		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
		// it's PagerAdapter set.
		mSlidingTabLayout.setViewPager(pager);


		
		
//		LinearLayout cardZone = (LinearLayout) findViewById(R.id.card_zone);
//		if (cardZone != null) {
//			FragmentManager fragmentManager = getSupportFragmentManager();
//			if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null
//					&& fragmentManager.findFragmentByTag(ViewCardFragment.ID)
//							.isAdded()) {
//				Log.d("PopulateArmyList",
//						"viewCardFragment here --> cardZone VISIBLE");
//				cardZone.setVisibility(View.VISIBLE);
//			} else {
//				Log.d("PopulateArmyList", "cardZone --> GONE");
//				cardZone.setVisibility(View.GONE);
//			}
//		}
//		// add swiper to buttons if necessary
//		ImageButton toRightButton = (ImageButton) findViewById(R.id.toRightButton);
//		if (toRightButton != null) {
//			toRightButton.setOnTouchListener(new OnTouchListener() {
//				
//				@SuppressLint("NewApi")
//				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					float finalX;
//					switch (event.getAction()) {
//			        case MotionEvent.ACTION_DOWN:
//			            initialX = event.getX();
//			            break;
//			        case MotionEvent.ACTION_MOVE:
//			        	finalX = event.getX();
////			            TranslateAnimation transAnimation= new TranslateAnimation(0, finalX, 0, 0);
////			            transAnimation.setDuration(20);
////			            transAnimation.setFillAfter(true);
////			            v.startAnimation(transAnimation);
//			        case MotionEvent.ACTION_UP:
//			            finalX = event.getX();
//			            if (initialX > finalX) {
//			            	toSelectedArmy(null);
//			                    break;
//			            } else {
//			            	toSelectionArmy(null);
//			            }
//			            break;
//			        }
//					return false;
//				}
//			});
//		}
//
//		
//		ImageButton toLeftButton = (ImageButton) findViewById(R.id.toLeftButton);
//		if (toLeftButton != null) {
//			toLeftButton.setOnTouchListener(new OnTouchListener() {
//				
//				@SuppressLint("NewApi")
//				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					float finalX;
//					switch (event.getAction()) {
//			        case MotionEvent.ACTION_DOWN:
//			            initialX = event.getX();
//			            break;
//			        case MotionEvent.ACTION_MOVE:
//			        	finalX = event.getX();
////			            TranslateAnimation transAnimation= new TranslateAnimation(0, finalX, 0, 0);
////			            transAnimation.setDuration(20);
////			            transAnimation.setFillAfter(true);
////			            v.startAnimation(transAnimation);
//			        case MotionEvent.ACTION_UP:
//			            finalX = event.getX();
//			            if (initialX < finalX) { // from R to L
//			            	toSelectionArmy(null);
//			                    break;
//			            } else {
//			            	toSelectedArmy(null);
//			            }
//			            break;
//			        }
//					return false;
//				}
//			});
//		}
		
		
		// load collection info
		SharedPreferences save = getSharedPreferences(MyCollectionActivity.SHARED_PREF_COLLECTION, MODE_PRIVATE);
		Map<String, ?> entries = save.getAll();

		MyCollectionActivity.extractCollectionFromPrefs(save, entries);

		notifyArmyChange();
	}

	@Override
	protected void onStart() {
		Log.d("PopulateArmyListActivity", "onStart");

        super.onStart();
		boolean startNewArmy = getIntent().getBooleanExtra(
				INTENT_START_NEW_ARMY, false);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		selectionModeSingleClick = sharedPref.getBoolean(
				PreferenceConstants.ACCESS_SIMPLE_CLICK,
				PreferenceConstants.ACCESS_SIMPLE_CLICK_DEFAULT);

		if (startNewArmy) {
			SelectionModelSingleton.getInstance().cleanAll();
			// chooseOptions(null);

			String armySize = sharedPref.getString(
					PreferenceConstants.ARMY_SIZE,
					PreferenceConstants.ARMY_SIZE_DEFAULT);
			String casterCount = sharedPref.getString(
					PreferenceConstants.CASTER_COUNT,
					PreferenceConstants.CASTER_COUNT_DEFAULT);
			changeArmySettings(Integer.valueOf(casterCount),
					Integer.valueOf(armySize));

		} else {
			// // update status, army already loaded in singleton by some
			// previous activity
			// if ( getIntent().getBooleanExtra(INTENT_FIRT_START,true)) {
			// armyName =
			// SelectionModelSingleton.getInstance().getArmyFileName();
			// armySaved = true;
			// } else {
			// // nothing, the window is just re-appearing...
			// }
		}
		// call only one time the popup!
		getIntent().removeExtra(INTENT_START_NEW_ARMY);

		// because onStart is called after onActivityResult or rotate screen,
		// make sure no reinitilisation is done
		getIntent().putExtra(INTENT_FIRT_START, false);

		SelectionModelSingleton.getInstance().rebuildSelectionEntries();
		SelectionModelSingleton.getInstance().recomposeSelectionList();
		SelectionModelSingleton.getInstance().checkAndAlterSelectionList();

		myInvalidateOptionsMenu(); // force menu computing to show tier / contract
									// depending on regular/merc faction

		notifyArmyChange();

	}
	
	@SuppressLint("NewApi")
	private void myInvalidateOptionsMenu() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		} else {
			supportInvalidateOptionsMenu();
		}
	}

    public void notifyMaybeGroupChange() {
        int selectionIndex = mTabsAdapter.getTabIndexForId(SelectionArmyFragment.ID);

        if (selectionIndex != -1) {
            SelectionArmyFragment selectionfragment = (SelectionArmyFragment) mTabsAdapter.getItem(selectionIndex);
            selectionfragment.notifyGroupRecalculate();
        }
    }


    /**
	 * notifie les couches graphique de la modification de sélection
	 */
	public void notifyArmyChange() {

		SelectionModelSingleton.getInstance().recomposeSelectionList();
		SelectionModelSingleton.getInstance().checkAndAlterSelectionList();

		
		int selectionIndex = mTabsAdapter.getTabIndexForId(SelectionArmyFragment.ID);
        int selectionEntriesIndex = mTabsAdapter.getTabIndexForId(SelectionEntriesArmyFragment.ID);
		int selectedIndex = mTabsAdapter.getTabIndexForId(SelectedArmyFragment.ID);

		if (selectionIndex != -1) {
			SelectionArmyFragment selectionfragment = (SelectionArmyFragment) mTabsAdapter.getItem(selectionIndex);
			selectionfragment.notifyDataSetChanged();

		}
        if (selectionEntriesIndex != -1) {
            SelectionEntriesArmyFragment selectionEntriesfragment = (SelectionEntriesArmyFragment) mTabsAdapter.getItem(selectionEntriesIndex);
            selectionEntriesfragment.notifyDataSetChanged();

        }
		if (selectedIndex != -1) {
			SelectedArmyFragment selectedfragment = (SelectedArmyFragment) mTabsAdapter.getItem(selectedIndex);
			selectedfragment.notifyDataSetChanged();
		}

        notifyMaybeGroupChange();

//		SelectionArmyFragment selectionfragment = (SelectionArmyFragment) getSupportFragmentManager()
//				.findFragmentByTag(SelectionArmyFragment.ID);
//		if (selectionfragment != null && selectionfragment.isInLayout()) {
//			
//		}
//
//		SelectedArmyFragment selectedfragment = (SelectedArmyFragment) getSupportFragmentManager()
//				.findFragmentByTag(SelectedArmyFragment.ID);
//		if (selectedfragment != null && selectedfragment.isInLayout()) {
//			
//		}

		updateTitles();

	}

	public void updateTitles() {
		
		
		String compendium = SelectionModelSingleton.getInstance()
				.getSelectedCompendium(this);
		
		getSupportActionBar().setTitle(
				SelectionModelSingleton.getInstance().getArmyFileName());
		
		getSupportActionBar().setSubtitle(Html.fromHtml(compendium));

		// notify of tier change
		if (SelectionModelSingleton.getInstance().isTierLevelJustChanged()) {
			SelectionModelSingleton.getInstance().acknowledgeTierLevelChange();
			Toast.makeText(
					this,
					getString(R.string.tier_level_attained)
							+ SelectionModelSingleton.getInstance()
									.getCurrentTiersLevel(), Toast.LENGTH_SHORT)
					.show();
		}

		displayTierLevel();

		// contract display
		if (SelectionModelSingleton.getInstance().getCurrentContract() != null) {
			findViewById(R.id.contract_icon).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.contract_icon).setVisibility(View.GONE);
		}

		
//		TextView titleSelected = (TextView) findViewById(R.id.selected_army_title);
//		titleSelected.setText(Html.fromHtml(compendium));
//
//		getSupportActionBar().setTitle(
//				SelectionModelSingleton.getInstance().getArmyFileName());
//
//		// compendium for selection list
//		TextView textCompendium = (TextView) findViewById(R.id.compendium);
//		textCompendium.setText(Html.fromHtml(compendium));
//
//		// tier display
//		LinearLayout selectedTitleLayout = (LinearLayout) findViewById(R.id.selected_army_title_layout);
//		LinearLayout selectionTitleLayout = (LinearLayout) findViewById(R.id.selection_army_title_layout);
//
//		displayTierLevel(selectedTitleLayout);
//		displayTierLevel(selectionTitleLayout);
//
//
//		// contract display
//		if (SelectionModelSingleton.getInstance().getCurrentContract() != null) {
//			selectionTitleLayout.findViewById(R.id.contract_icon)
//					.setVisibility(View.VISIBLE);
//		} else {
//			selectionTitleLayout.findViewById(R.id.contract_icon)
//					.setVisibility(View.GONE);
//		}

		// force menu redraw
		myInvalidateOptionsMenu();

	}

	/**
	 * update display of title zone to notify of the tier level attained.
	 * 
	 */
	private void displayTierLevel() {
		ImageView tierLevelIcon = (ImageView) findViewById(R.id.tier_level_icon);

		if (SelectionModelSingleton.getInstance().getCurrentTiers() != null) {
			tierLevelIcon.setVisibility(View.VISIBLE);
			switch (SelectionModelSingleton.getInstance()
					.getCurrentTiersLevel()) {
                case 0:
                    tierLevelIcon.setImageResource(R.drawable.ic_tier0);
                    break;
                case 1:
                    tierLevelIcon.setImageResource(R.drawable.ic_tier1);
                    break;
                case 2:
                    tierLevelIcon.setImageResource(R.drawable.ic_tier2);
                    break;
                case 3:
                    tierLevelIcon.setImageResource(R.drawable.ic_tier3);
                    break;
                case 4:
                    tierLevelIcon.setImageResource(R.drawable.ic_tier4);
                    break;
                default:
                    break;
            }
        } else {
            tierLevelIcon.setVisibility(View.GONE);
        }
	}

	/**
	 * creates a popup dialog to display Tier conditions and benefits
	 * 
	 * @param v
	 */
	public void displayTierInfo(View v) {
		
		FragmentManager fm = getSupportFragmentManager();
		DisplayTierFragment dialog = new DisplayTierFragment();
		dialog.setShowsDialog(true);
		dialog.show(fm, "DisplayTierFragment");
	}

	/**
	 * creates a popup dialog to display Contract conditions and benefits
	 * 
	 * @param v
	 */
	public void displayContractInfo(View v) {
		FragmentManager fm = getSupportFragmentManager();
		DisplayContractFragment dialog = new DisplayContractFragment();
		dialog.setShowsDialog(true);
		dialog.show(fm, "DisplayContractFragment");

	}

	@Override
	public boolean onModelAdded(SelectionEntry entry) {
		boolean directlyAdded = true;
		if (entry.isSelectable()) {
			if (entry.getType() == ModelTypeEnum.UNIT) {
				if (((SelectionUnit) entry).isVariableSize()) {
					directlyAdded = false;
					askForUnitDetails((SelectionUnit) entry);
				} else {
					SelectionModelSingleton.getInstance().addUnit(
							(SelectionUnit) entry, true);

				}
				// return of dialog is treated in onActivityResult() method
			} else if (entry.getType() == ModelTypeEnum.WARJACK
					|| entry.getType() == ModelTypeEnum.COLOSSAL) {
				int candidatesCount = SelectionModelSingleton.getInstance()
						.modelsToWhichAttach(entry).size();
				if (candidatesCount == 0) {
					Toast.makeText(
							getApplicationContext(),
							R.string.no_entry_to_attach,
							Toast.LENGTH_SHORT).show();
				} else if (candidatesCount > 1) {
					directlyAdded = false;
					askWhoToAttach(entry);
					// return of dialog is treated in onActivityResult() method
				} else {
					SelectionModelSingleton.getInstance()
							.addAttachedElementByDefault(entry);
				}
			} else if (entry.getType() == ModelTypeEnum.WARBEAST
					|| entry.getType() == ModelTypeEnum.GARGANTUAN) {
				int candidatesCount = SelectionModelSingleton.getInstance()
						.modelsToWhichAttach(entry).size();
				if (candidatesCount == 0) {
					directlyAdded = false;
					Toast.makeText(
							getApplicationContext(),
							R.string.no_entry_to_attach,
							Toast.LENGTH_SHORT).show();
				} else if (candidatesCount > 1) {
					directlyAdded = false;
					askWhoToAttach(entry);
					// return of dialog is treated in onActivityResult() method
				} else {
					SelectionModelSingleton.getInstance()
							.addAttachedElementByDefault(entry);
				}
			} else if (entry.getType() == ModelTypeEnum.UNIT_ATTACHMENT
					|| entry.getType() == ModelTypeEnum.WEAPON_ATTACHMENT) {
				int candidatesCount = SelectionModelSingleton.getInstance()
						.modelsToWhichAttach(entry).size();
				if (candidatesCount == 0) {
					directlyAdded = false;
					Toast.makeText(
							getApplicationContext(),
							R.string.no_entry_to_attach,
							Toast.LENGTH_SHORT).show();
				} else if (candidatesCount > 1) {
					directlyAdded = false;
					askWhoToAttach(entry);
					// return of dialog is treated in onActivityResult() method
				} else {
					SelectionModelSingleton.getInstance()
							.addAttachedElementByDefault(entry);
				}
			} else if (entry.getType() == ModelTypeEnum.SOLO) {
				SelectionSolo solo = (SelectionSolo) entry;
				if (solo.isWarcasterAttached()) {

					int candidatesCount = SelectionModelSingleton.getInstance()
							.modelsToWhichAttach(entry).size();

					if (candidatesCount == 0) {
						directlyAdded = false;
						Toast.makeText(
								getApplicationContext(),
								R.string.no_entry_to_attach,
								Toast.LENGTH_SHORT).show();
					} else if (SelectionModelSingleton.getInstance()
							.modelsToWhichAttach(entry).size() > 1) {
						directlyAdded = false;
						askWhoToAttach(entry);
						// return of dialog is treated in onActivityResult()
						// method
					} else {
						SelectionModelSingleton.getInstance()
								.addAttachedElementByDefault(entry);
					}
				} else if (solo.isGenericUnitAttached()) {
					int candidatesCount = SelectionModelSingleton.getInstance()
							.modelsToWhichAttach(entry).size();
					if (candidatesCount > 1) {
						directlyAdded = false;
						askWhoToAttach(entry);
						// return of dialog is treated in onActivityResult()
						// method
					} else if (candidatesCount == 1) {
						SelectionModelSingleton.getInstance()
								.addAttachedElementByDefault(entry);
					} else {
						directlyAdded = false;
						Toast.makeText(
								getApplicationContext(),
								R.string.no_entry_to_attach,
								Toast.LENGTH_SHORT).show();
					}
				} else if (solo.isDragoon()) {
					directlyAdded = false;
					askForDismountOption(solo);
				} else {
					SelectionModelSingleton.getInstance().addSolo(
							(SelectionSolo) entry);
				}
			} else if (entry.getType() == ModelTypeEnum.BATTLE_ENGINE) {
				SelectionModelSingleton.getInstance().addBattleEngine(entry);
			} else {
				SelectionModelSingleton.getInstance()
						.addIndependantModel(entry);
			}
		} else {
			directlyAdded = false;
			Toast.makeText(this, "this entry is not selectable",
					Toast.LENGTH_SHORT).show();
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(300);
		}
		// notifyArmyChange();

        if (directlyAdded) {
            notifyMaybeGroupChange();
        }

		return directlyAdded;
	}

	// @Override
	// public void onModelRemoved(SelectionEntry model) {
	// if (model.isSelected()) {
	//
	// if (model.getType() == ModelTypeEnum.WARCASTER || model.getType() ==
	// ModelTypeEnum.WARLOCK) {
	// SelectedArmyCommander caster = (SelectedArmyCommander)
	// SelectionModelSingleton.getInstance().getSelectedEntryById(model.getId());
	// SelectionModelSingleton.getInstance().removeCaster(caster);
	// }
	//
	// if (model.getType() == ModelTypeEnum.WARJACK) {
	// // this jack may be attached to many casters
	// List<JackCommander> modelsWithThisJack =
	// SelectionModelSingleton.getInstance().warjackDeletionChoices(model.getId());
	// if (modelsWithThisJack.size() > 1) {
	// askWhichToDetachFrom(model, modelsWithThisJack);
	// } else {
	// SelectionModelSingleton.getInstance().removeWarjack(model.getId(),
	// modelsWithThisJack.get(0));
	// }
	// }
	//
	// if (model.getType() == ModelTypeEnum.UNIT) {
	// ArrayList<SelectedUnit> deletableUnits =
	// SelectionModelSingleton.getInstance().unitDeletionChoices(model.getId());
	// if ( deletableUnits.size() > 1) {
	// // choose wich unit to remove
	// askWhichToDelete(deletableUnits);
	// } else {
	// // directly delete the entry
	// SelectionModelSingleton.getInstance().removeUnit(deletableUnits.get(0));
	// }
	// }
	//
	// notifyArmyChange();
	// } else {
	// Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	// v.vibrate(300);
	// }
	//
	// }

	@Override
	public void viewSelectionDetail(SelectionEntry model) {

		SelectionModelSingleton.getInstance().setCurrentlyViewedElement(
				ArmySingleton.getInstance().getArmyElements()
						.get(model.getId()));

		viewModelDetail(null);

	}

    @Override
    public void selectedGroup(SelectionGroup group) {

        SelectionModelSingleton.getInstance().setSelectedGroup(group);

        Log.d(TAG, "selectedGroup");

        int tabNumberForEntries = mTabsAdapter.getTabIndexForId(SelectionEntriesArmyFragment.ID);

        if ( tabNumberForEntries != -1) {
            mTabsAdapter.removeTab(tabNumberForEntries);
        }

        SelectionEntriesArmyFragment selectionEntriesFragment = new SelectionEntriesArmyFragment();

        int currentPageNumber = pager.getCurrentItem();
        mTabsAdapter.addTabAtPosition(SelectionEntriesArmyFragment.ID,  getResources().getString(group.getType().getTitle()),
                selectionEntriesFragment, null, currentPageNumber + 1);

        // position the card after the current fragment, so that a swipe "back" returns to the current fragment
        // also, this mean the card fragment is NOT destroyed when going back (due to proximity cache)
        int entriesTabIndex = mTabsAdapter.getTabIndexForId(SelectionEntriesArmyFragment.ID);


        pager.setPageTransformer(true, null);

        mTabsAdapter.selectTab(entriesTabIndex);

        mTabsAdapter.notifyDataSetChanged();

        pager.setPageTransformer(true, new DepthPageTransformer());

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);

    }

    @Override
    public void unselectedGroup() {

        SelectionModelSingleton.getInstance().setSelectedGroup(null);

        mTabsAdapter.selectTab(0);

        int tabId = mTabsAdapter.getTabIndexForId(SelectionEntriesArmyFragment.ID);
        if ( tabId != -1) {
            mTabsAdapter.removeTab(tabId);
        }

        mTabsAdapter.notifyDataSetChanged();

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);
    }


    public void removeViewCardFragment(View v) {
		Log.d("PopulateArmyList", "removeViewCardFragment");
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		FragmentTransaction fragmentTransaction = fragmentManager
//				.beginTransaction();
//
//		if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
//			fragmentTransaction.remove(fragmentManager
//					.findFragmentByTag(ViewCardFragment.ID));
//		}
//		fragmentTransaction.commit();
//
//		LinearLayout cardZone = (LinearLayout) findViewById(R.id.card_zone);
//		if (cardZone != null) {
//			cardZone.setVisibility(View.GONE);
//		}
	}

	public void viewModelDetailInNewActivity(View v) {
		Log.d("viewModelDetailInNewActivity", "viewModelDetailInNewActivity");
		Intent intent = new Intent(this, ViewCardActivity.class);
		intent.putExtra(ViewCardActivity.MODEL_ID, getArmyElement().getId());
		startActivity(intent);
	}

	public void askForDismountOption(SelectionSolo dragoon) {
		Intent intent = new Intent(this, ChooseDismountOptionActivity.class);
		intent.putExtra(ChooseDismountOptionActivity.INTENT_SOLO_ID,
				dragoon.getId());
		startActivityForResult(intent,
				ChooseDismountOptionActivity.CHOOSE_DISMOUNT_OPTIONS_DIALOG);
	}

	public void askForUnitDetails(final SelectionUnit unit) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(getResources().getString(R.string.add_unit) + unit.getFullLabel() );
	    
	    String[] sizes = new String[2];
	    sizes[0] = getResources().getString(R.string.min) + unit.getMinSize() + " " + getResources().getString(R.string.models);
	    sizes[1] = getResources().getString(R.string.max) + unit.getMaxSize() + " " + getResources().getString(R.string.models);
	    
	    builder.setItems(sizes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
				SelectionModelSingleton.getInstance().addUnit(unit, which==0);
				unit.setSelected(true);
				
				// animate view
				int selectionIndex = mTabsAdapter.getTabIndexForId(SelectionArmyFragment.ID);
				if (selectionIndex != -1) {
					SelectionArmyFragment selectionfragment = (SelectionArmyFragment) mTabsAdapter.getItem(selectionIndex);
					// selectionfragment.slideElementToRight(unit);
				}
				
				notifyArmyChange();
            }
        });

	    
//	    TextView tv = (TextView) findViewById(R.id.textView1);
//	    tv.setText(unitModel.getFullLabel());
//	    
//	    RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroupUnitSize);
//	    
//	    RadioButton rbMin = (RadioButton) findViewById(R.id.radioButtonMin);
//	    RadioButton rbMax = (RadioButton) findViewById(R.id.radioButtonMax);
//	    
//	    rbMin.setText(unitModel.getMinSize() + " models (" + unitModel.getMinCost() + " PC)");
//	    rbMin.setChecked(false);
//	    rbMax.setText(unitModel.getMaxSize() + " models (" + unitModel.getMaxCost() + " PC)");
//	    rbMax.setChecked(false);

    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();

//		
//		Intent intent = new Intent(this, ChooseUnitSizeActivity.class);
//		intent.putExtra(ChooseUnitSizeActivity.INTENT_UNIT_ID, unit.getId());
//		startActivityForResult(intent,
//				ChooseUnitSizeActivity.CHOOSE_UNIT_OPTIONS_DIALOG);
	}

	public void askWhoToAttach(SelectionEntry entry) {
		
		SelectionModelSingleton.getInstance().setCurrentEntryChooseWhoToAttach(entry);
		
		ChooseAttachActivity dialog = new ChooseAttachActivity();
		dialog.show(getSupportFragmentManager(), "ChooseAttachActivity");
	}

	public void chooseOptions(View v) {

		DialogFragment dialog = new ChooseArmyOptionsDialog();
		dialog.show(getSupportFragmentManager(), "ChooseArmyOptionsDialog");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("PopulateArmyListActivity", "onActivityResult");

//		if (requestCode == ChooseUnitSizeActivity.CHOOSE_UNIT_OPTIONS_DIALOG) {
//			if (resultCode == RESULT_OK) {
//				UnitOption result = new UnitOption();
//
//				String modelId = data
//						.getStringExtra(ChooseUnitSizeActivity.INTENT_UNIT_ID);
//
//				boolean minSize = data.getBooleanExtra(
//						ChooseUnitSizeActivity.INTENT_MIN_SIZE, true);
//
//				result.setMinSize(minSize);
//
//				SelectionUnit unit = (SelectionUnit) SelectionModelSingleton
//						.getInstance().getSelectionEntryById(modelId);
//				SelectionModelSingleton.getInstance().addUnit(unit, minSize);
//				unit.setSelected(true);
//				notifyArmyChange();
//
//			}
//		}

		if (requestCode == ChooseDismountOptionActivity.CHOOSE_DISMOUNT_OPTIONS_DIALOG) {
			if (resultCode == RESULT_OK) {

				String modelId = data
						.getStringExtra(ChooseDismountOptionActivity.INTENT_SOLO_ID);

				boolean dismountOption = data.getBooleanExtra(
						ChooseDismountOptionActivity.INTENT_DISMOUNT_OPTION,
						false);

				SelectionSolo solo = (SelectionSolo) SelectionModelSingleton
						.getInstance().getSelectionEntryById(modelId);
				SelectionModelSingleton.getInstance().addDragoon(solo,
						dismountOption);
				solo.setSelected(true);
				notifyArmyChange();

			}
		}

//		if (requestCode == ChooseAttachActivity.CHOOSE_ATTACH_DIALOG) {
//			if (resultCode == RESULT_OK) {
//
//				String modelId = data
//						.getStringExtra(ChooseAttachActivity.INTENT_ELEMENT_ID);
//				int index = data.getIntExtra(
//						ChooseAttachActivity.INTENT_ELEMENT_NUMBER, 0);
//
//				SelectionEntry entry = SelectionModelSingleton.getInstance()
//						.getSelectionEntryById(modelId);
//				List<SelectedEntry> modelsToAttach = SelectionModelSingleton
//						.getInstance().modelsToWhichAttach(entry);
//				SelectionModelSingleton.getInstance().addAttachedElementTo(
//						entry, modelsToAttach.get(index));
//
//				notifyArmyChange();
//			}
//		}

		// if (requestCode == ChooseDeleteActivity.CHOOSE_DELETE_DIALOG) {
		// if (resultCode == RESULT_OK) {
		// armySaved = false;
		// notifyArmyChange();
		// }
		// }
		//
		// if (requestCode == ChooseDetachActivity.CHOOSE_DETACH_DIALOG) {
		// if (resultCode == RESULT_OK) {
		// armySaved = false;
		// notifyArmyChange();
		// }
		// }

	}

	public void changeArmySettings(int casterCount, int pointCount) {
		SelectionModelSingleton.getInstance().setNbCasters(casterCount);
		SelectionModelSingleton.getInstance().setNbPoints(pointCount);
		notifyArmyChange();
	}

	// private void changeSelectionMode(boolean singleClick) {
	// selectionModeSingleClick = singleClick;
	// }

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onBackPressed() {
		if ( pager.getCurrentItem() == 2) {
			// card view --> selected
			pager.setCurrentItem(1);
			return;
		}
		
		if ( pager.getCurrentItem() == 1) {
			// selected --> selection
			pager.setCurrentItem(0);
			return;
		}
		
		if (!SelectionModelSingleton.getInstance().isSaved()) {

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(this);
			boolean exitSilently = sharedPref.getBoolean(
					PreferenceConstants.EXIT_SILENTLY, false);

			if (exitSilently) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd-kk'H'mm");
				String armyFileName = "temp-" + sdf.format(new Date());

				if (SelectionModelSingleton.getInstance().getSelectedEntries()
						.size() > 0) {
					SelectionModelSingleton.getInstance().setArmyFileName(
							armyFileName);
					StorageManager.saveArmyList(PopulateArmyListActivity.this
							.getApplicationContext(), SelectionModelSingleton
							.getInstance());
					Toast.makeText(getApplicationContext(),
							"Army saved as " + armyFileName, Toast.LENGTH_SHORT)
							.show();
				}

				finish();
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle(R.string.exit_create_army);
				alert.setMessage(R.string.exit_create_army_confirm);

				alert.setPositiveButton(R.string.ok,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								PopulateArmyListActivity.this.finish();
							}
						});

				alert.setNegativeButton(R.string.cancel,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});

				alert.show();

			}

		} else {
			finish();
		}

	}

	public void navigateHome() {
        onBackPressed();
	}

	@Override
	public ArmyElement getArmyElement() {
		return SelectionModelSingleton.getInstance()
				.getCurrentlyViewedElement();
	}

	@Override
	public boolean isCardfullScreen() {
		// card in fragment --> not full screen
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean isCardDoublePane() {
		Configuration config = getResources().getConfiguration();
		// two panels side by side if screen large enough && landscape
		DisplayMetrics metrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("ViewCardActivity", "widthPixels = " + metrics.widthPixels + " - density = " + metrics.density);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			if (config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_NORMAL) && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				Log.d("ViewCardActivity", "landscape --> doublePane");
				return true;
			}
			if (config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE) && config.orientation == Configuration.ORIENTATION_PORTRAIT) {
				Log.d("ViewCardActivity", "landscape --> doublePane");
				return true;
			}
		} else {
			if (metrics.widthPixels / metrics.density  >= 640 
					&& config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				Log.d("ViewCardActivity", "landscape --> doublePane");
				return true;
			}
			if (metrics.widthPixels / metrics.density  >= 960 
					&& config.orientation == Configuration.ORIENTATION_PORTRAIT) {
				Log.d("ViewCardActivity", "landscape --> doublePane");
				return true;
			}
		}
		
		Log.d("ViewCardActivity", "not landscape or too small --> singlePane");
		return false;
	}

	@Override
	public boolean useSingleClick() {
		return selectionModeSingleClick;
	}

	@Override
	public void viewModelDetail(View v) {
		
		
		ArmyElement entry = SelectionModelSingleton.getInstance().getCurrentlyViewedElement();
		
		ViewCardFragment viewCardFragment = new ViewCardFragment();

		String tabTitle = entry.getFullName();
		if (tabTitle.length() > 10) {
			tabTitle = tabTitle.substring(0, 8).trim();
			tabTitle = tabTitle + '\u2025';
		}

		
		// in case a fragment remains after a screen rotation
		for (Fragment fragment : getSupportFragmentManager().getFragments()) {
			// check if viewCardFragment exists
			if (fragment instanceof ViewCardFragment) {
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.detach(fragment); // to force view recreate
				transaction.commit();
				Log.e("PopulateArmyListActivity", "removed ViewCard fragment");
			}
		}
		
		int cardTabIndex = mTabsAdapter.getTabIndexForId(CARD_FRAGMENT);
		if (cardTabIndex != -1) {
			mTabsAdapter.removeTab(cardTabIndex);
		} 
		
		// position the card after the current fragment, so that a swipe "back" returns to the current fragment
		// also, this mean the card fragment is NOT destroyed when going back (due to proximity cache) 
		mTabsAdapter.addTabAtPosition(CARD_FRAGMENT , tabTitle, viewCardFragment, null, 2);
		cardTabIndex = mTabsAdapter.getTabIndexForId(CARD_FRAGMENT);
		
		
		mTabsAdapter.selectTab(cardTabIndex);
		mTabsAdapter.notifyDataSetChanged();
				
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);

	}


	@Override
	public void onDirectoryCreated(String fullPath) {
		if (StorageManager.createArmyListDirectory(getApplicationContext(), fullPath)) {
			Toast.makeText(getApplicationContext(), "directory created", Toast.LENGTH_SHORT).show();
			ChooseFileToSaveDialog dialog = (ChooseFileToSaveDialog) getSupportFragmentManager().findFragmentByTag("ChooseFileToSaveDialog");
			dialog.notifyDirectoryCreation(fullPath);
		}
		
	}


	@Override
	public void onArmyListSelected(ArmyListDescriptor army) {
		// do nothing
	}

	@Override
	public void onArmyListDeleted(final ArmyListDescriptor army) {
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(getResources().getString(R.string.askDeleteList) + army.getFileName());
    	builder.setTitle(R.string.confirmDeleteList);
    	
    	builder.setPositiveButton(R.string.delete, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyList(getApplicationContext(), army.getFilePath())) {
                	// notify fragment...
                	android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                	ChooseFileToSaveDialog listDialog = (ChooseFileToSaveDialog) fm.findFragmentByTag("ChooseFileToSaveDialog");
                	listDialog.notifyArmyListDeletion(army);
            	} else {
            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
	}

	@Override
	public void onArmySaved() {
		
		updateTitles();
	}

	@Override
	public void onArmyDirectoryDeleted(final ArmyListDirectory directory) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setMessage(getResources().getString(R.string.askDeleteFolder) + directory.getTextualPath());
    	builder.setTitle(R.string.confirmDeleteFolder);
    	
    	builder.setPositiveButton(R.string.delete, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyFolder(getApplicationContext(),directory.getFullpath())) {
                	// notify fragment...
                	ChooseFileToSaveDialog chooseArmyDialog = (ChooseFileToSaveDialog) getSupportFragmentManager().findFragmentByTag("ChooseFileToSaveDialog");
        			chooseArmyDialog.notifyDirectoryDeletion(directory);
            	} else {
            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
            	}
            }
        });
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
	}

	@Override
	public SelectionEntry getModelToAttach() {
		return SelectionModelSingleton.getInstance().getCurrentEntryChooseWhoToAttach();
	}

	@Override
	public void setTargetModelForAttachment(SelectedEntry selectedModel) {
		
		SelectionEntry entry = SelectionModelSingleton.getInstance().getCurrentEntryChooseWhoToAttach();
		SelectionModelSingleton.getInstance().addAttachedElementTo(entry, selectedModel);

		// animate view
		int selectionIndex = mTabsAdapter.getTabIndexForId(SelectionArmyFragment.ID);

		if (selectionIndex != -1) {
			SelectionArmyFragment selectionfragment = (SelectionArmyFragment) mTabsAdapter.getItem(selectionIndex);

			selectionfragment.notifyDataSetChanged();

			// selectionfragment.slideElementToRight(entry);


		}
		notifyArmyChange();

	}

	@Override
	public void onEntryRemoved(SelectedEntry entry) {
			// animate view
			int selectionIndex = mTabsAdapter.getTabIndexForId(SelectedArmyFragment.ID);
			
			if (selectionIndex != -1) {
				SelectedArmyFragment selectedfragment = (SelectedArmyFragment) mTabsAdapter.getItem(selectionIndex);
				selectedfragment.deleteElement(entry);
			}

        notifyMaybeGroupChange();
	}

    @Override
    public void onChangeSpecialistValue(SelectedEntry entry, boolean isSpecialist) {
        entry.setSpecialist(isSpecialist);
        notifyArmyChange();
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
