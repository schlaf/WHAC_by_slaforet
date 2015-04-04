/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.view.DepthPageTransformer;
import com.example.android.common.view.SlidingTabLayout;
import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.ChooseArmyListDialog;
import com.schlaf.steam.activities.ChooseArmyListener;
import com.schlaf.steam.activities.battle.BattleListFragment.BattleListInterface;
import com.schlaf.steam.activities.battle.EndBattleDialog.EndBattleListener;
import com.schlaf.steam.activities.bluetooth.CommunicationProtocolSingleton;
import com.schlaf.steam.activities.bluetooth.CommunicationProtocolSingleton.ConnectStatusEnum;
import com.schlaf.steam.activities.bluetooth.CommunicationService;
import com.schlaf.steam.activities.bluetooth.EnableBluetoothActivity;
import com.schlaf.steam.activities.card.ChooseCardFromLibraryDialog;
import com.schlaf.steam.activities.card.ViewCardActivity;
import com.schlaf.steam.activities.card.ViewCardFragment;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.chrono.ChronoConfigDialog;
import com.schlaf.steam.activities.chrono.ChronoFragment;
import com.schlaf.steam.activities.chrono.ChronoFragment.ChronoActivityInterface;
import com.schlaf.steam.activities.damages.ColossalGridDamageFragment;
import com.schlaf.steam.activities.damages.DamageListener;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.activities.damages.MultiPVUnitDamageFragment;
import com.schlaf.steam.activities.damages.MyrmidonGridDamageFragment;
import com.schlaf.steam.activities.damages.SingleLineDamageFragment;
import com.schlaf.steam.activities.damages.WarbeastSpiralDamageFragment;
import com.schlaf.steam.activities.damages.WarjackGridDamageFragment;
import com.schlaf.steam.activities.dice.DiceRollFragment;
import com.schlaf.steam.activities.search.SearchHelper;
import com.schlaf.steam.activities.search.SearchSuggestionProvider;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selected.SelectedDragoon;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.steamroller.ScenarioLibraryActivity;
import com.schlaf.steam.activities.steamroller.SteamRollerSingleton;
import com.schlaf.steam.data.ArmyCommander;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.Mission;
import com.schlaf.steam.data.MultiPVUnitGrid;
import com.schlaf.steam.data.MyrmidonDamageGrid;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.WarjackLikeDamageGrid;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.StorageManager;
import com.schlaf.steam.tabs.TabsAdapter;

/**
 * @author S0085289
 *
 */
public class BattleActivity extends ActionBarActivity implements BattleListInterface, ViewCardActivityInterface, DamageListener, ChronoActivityInterface, ChooseArmyListener, EndBattleListener {

	private final static String TAG ="BattleActivity";
	private static boolean D = true;
	
	public static final String INTENT_BATTLE_NAME = "army_name";
	public static final String INTENT_ARMY_PATH = "army_path";
	public static final String INTENT_BATTLE_PATH = "battle_path";
	public static final String INTENT_CREATE_BATTLE_FROM_ARMY = "start";
	public static final String INTENT_CONTINUE_BATTLE = "continue";
	
	private String armyPath; // path of the army file (to create battle)
	private String battleName; // name of the battle (same as army or "name vs. name")
	private String battlePath; // path of the battle file (to reuse)
	
	private boolean FAKE_BT = false;
	
	private static final String PLAYER1_FRAGMENT = "PLAYER1_FRAGMENT";
	private static final String CHRONO_FRAGMENT = "CHRONO_FRAGMENT";
	private static final String PLAYER2_FRAGMENT = "PLAYER2_FRAGMENT";
	private static final String CARD_FRAGMENT = "CARD_FRAGMENT";

	/**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
	private SlidingTabLayout mSlidingTabLayout;
	
	TabsAdapter mTabsAdapter; // the adapter for swiping pages
	ViewPager pager; // the pager that handles fragments swipe

	/** The IntentFilter to listen for the Intent sent by the ComunicationService  */
	IntentFilter comServiceFilter = new IntentFilter(CommunicationService.BLUETOOTH_COMMUNICATION_INTENT_ACTION);

	
	/** The bluetooth client socket */
	BluetoothSocket bluetoothClientSocket = null;
	
	/** To know if the receiver that listen for ComService Intent is registred */
	private boolean isReceiverRegistred = false;
	
	/** The service that handles the bluetooth communication between the devices	 */
	private CommunicationService comService = null;
	
	private boolean serviceIsBound = false;
	/** The serviceConnection object 	 */
	private ServiceConnection onService = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG, "onServiceDisconnected");
			unregisterDamageGridToBlueTooth();
			comService = null;
			serviceIsBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(TAG, "onServiceConnected");
			serviceIsBound = true;
			comService = ((CommunicationService.LocalBinder) service)
					.getService();
			
			BattleCommunicationObject msg = CommunicationProtocolSingleton.getInstance().getNextMessageToSend();
			
			if (msg != null) {
				comService.sendMessage(msg);
			}
			
			
			registerDamageGridToBlueTooth();

			
			
		}
	};
	
	/**
	 * The one that listen for the intent sent by the service
	 */
	private BroadcastReceiver communicationServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// just retrieve the message and use it:
			handleInComingMessage((BattleCommunicationObject) intent.getSerializableExtra(CommunicationService.BLUETOOTH_MESSAGE));
		}

	};
	
	/**
	 * @param readMessage
	 */
	private void handleInComingMessage(BattleCommunicationObject readMessage) {
		
		BattleCommunicationObject response = CommunicationProtocolSingleton.getInstance().handleInComingMessage(readMessage);
		
		if (response != null) {
			if (comService != null) {
				comService.sendMessage(response);	
			}
		}
		
		handleBlueToothStatus();
		
	}
	
	private void handleBlueToothStatus() {
		
		if (CommunicationProtocolSingleton.getInstance().getCurrentConnectStatus() == ConnectStatusEnum.NOT_CONNECTED) {
			getSupportActionBar().setSubtitle("bluetooth disconnected");
			blueToothDisconnect();
		} else {
			switch( CommunicationProtocolSingleton.getInstance().getCurrentDataStatus() ) {
			case  SENDING_ARMY :
				if (D) Log.d(TAG, "receivingArmy");
		    	getSupportActionBar().setSubtitle(R.string.sending_army_list_);
		    	break;
			case  RECEIVING_ARMY :
				if (D) Log.d(TAG, "receivingArmy");
		    	getSupportActionBar().setSubtitle(R.string.receiving_army_list_);
		    	break;
			case ARMY_RECEIVED : 
				if (D) Log.d(TAG, "receivedArmy");
		    	getSupportActionBar().setSubtitle(R.string.received_army_list);
		    	
				updateTwoPlayerLayout();
				updateTitleAndSaveBattle(BattleSingleton.getInstance().getArmy(BattleSingleton.PLAYER2).getFilename());
				
				registerDamageGridToBlueTooth();
				pager.setCurrentItem(0);
		    	
				if (CommunicationProtocolSingleton.getInstance().getNextMessageToSend() == null) {
					CommunicationProtocolSingleton.getInstance().okNowWait();
				}
				
		    	break;
			default:
				break;
			}
			
			
			updateChronoLayout(true);
		}
		
		
		
	}

	private void blueToothDisconnect() {
		
		Toast.makeText(this, R.string.bluetooth_disconnection, Toast.LENGTH_SHORT).show();
		BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false); // to retransmit if reconnection!
		
		// because we don't want to try to use this service to send damages, after the BT connection is closed
		unregisterDamageGridToBlueTooth();
		
		SteamPunkRosterApplication.getInstance().resetBluetoothSocket();
		
		try {
			unbindService(onService);
			serviceIsBound = false;
		} catch (Exception e) {
			Log.e(TAG, "error when unbinding service upon deconnection", e);
		}


	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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



		battleName = getIntent().getStringExtra(INTENT_BATTLE_NAME);
		armyPath = getIntent().getStringExtra(INTENT_ARMY_PATH);
		battlePath = getIntent().getStringExtra(INTENT_BATTLE_PATH);
		boolean createBattleFromArmyList = getIntent().getBooleanExtra(INTENT_CREATE_BATTLE_FROM_ARMY, false);
		
		// BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false);
		
		if (createBattleFromArmyList) {

			BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false);
			
			// transcode path from army to battle
			String armiesDir = StorageManager.getArmyListsPath(getApplicationContext());
			String battlesDir = StorageManager.getBattlesPath(getApplicationContext());
			battlePath = armyPath.replace(armiesDir, battlesDir);
			
			if (! StorageManager.isExistingBattle(getApplicationContext(), battlePath) ) {
				// no existing battle, creating one from army
				StorageManager.createBattleFromArmy(getApplicationContext(), armyPath, BattleSingleton.getInstance(), BattleSingleton.PLAYER1);
				
				// clean player2 list
				BattleSingleton.getInstance().setArmy(null, BattleSingleton.PLAYER2);
				BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER2).clear();

                boolean hasSpecialist = false;
                for (BattleEntry entry : BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1)) {
                    if (entry.isSpecialist()) {
                        hasSpecialist = true;
                        break;
                    }
                }

                if (hasSpecialist) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Specialist");
                    alert.setMessage("This army contains specialist. Hit the specialist menu to select your entries...");
                    alert.show();
                }



            } else {
				// default : load existing battle...
				boolean complete = StorageManager.loadExistingBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());
				
				if (!complete) {
					// load battle failed, revert to creation from army list
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("error");
					alert.setMessage("an error occured while loading battle : recreating from army list");
					alert.show();
					recreateBattleFromScratch();
				} else {
					// ask if force re-creation of battle.
					AlertDialog.Builder alert = new AlertDialog.Builder(this);

					alert.setTitle(R.string.battle_exists);
					alert.setMessage(R.string.recreate_battle);

					alert.setPositiveButton(R.string.erase_recreate, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							BattleActivity.this.recreateBattleFromScratch();
						}
					});

					alert.setNegativeButton(R.string.reuse, new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
						  // do nothing, already reused by default;
					  }
					});

					alert.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							  // do nothing, already reused by default;
						}
					});
					
					alert.show();				
				}
			}
		} else {
			// load existing battle from previous screen
			// OR (!!!!)
			// reload battle after screen rotation!
			if (getIntent().getBooleanExtra(INTENT_CONTINUE_BATTLE, false)) {
				Log.d(TAG, "onCreate - continue battle");
				// do nothing
			} else {
				// load
				Log.d(TAG, "onCreate - load battle");
				BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false);
				boolean complete = StorageManager.loadExistingBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());
				if (!complete) {
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("error");
					alert.setMessage("an error occured while loading battle. ");
					alert.show();
				}
			}
			
		}
		
		if (! getIntent().getBooleanExtra(INTENT_CONTINUE_BATTLE, false)) {
			// erase battle plan
			BattleSingleton.getInstance().getBattlePlanningEntries().clear();
		}
		
		getSupportActionBar().setLogo(R.drawable.battle_icon);
		getSupportActionBar().setTitle(battleName);
		
		// prevents reloading when activity recreated (screen orientation change)
		getIntent().removeExtra(INTENT_CREATE_BATTLE_FROM_ARMY);
		
		// from now, we pursue on same battle
		getIntent().putExtra(INTENT_CONTINUE_BATTLE, true);
		getIntent().putExtra(INTENT_BATTLE_PATH, battlePath); // be sure to remain!
		
		setContentView(R.layout.battlelayout_fragmented);
		
		
		pager = (ViewPager)findViewById(R.id.viewpager);
		pager.setOffscreenPageLimit(3);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			pager.setPageTransformer(true, new DepthPageTransformer());
		}

		
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.LightGrey));

		
//		PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pagerTab);
//		
//		tabStrip.setTabIndicatorColor(getResources().getColor(R.color.AndroidBlue));
		
		
		// getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);


//		// real display is in fragment...
//		BattleListFragment player1Fragment = new BattleListFragmentPlayer1();
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		ft.replace(R.id.fragment_player1_placeholder, player1Fragment, BattleListFragmentPlayer1.ID);
//		
//		if (BattleSingleton.getInstance().hasPlayer2()) {
//	    	BattleListFragment player2Fragment = new BattleListFragmentPlayer2();
//			ft.replace(R.id.fragment_player2_placeholder, player2Fragment, BattleListFragmentPlayer2.ID);
//		} else {
//			// delete fragment?
//			FragmentManager fragmentManager = getSupportFragmentManager();
//			if (fragmentManager.findFragmentByTag(BattleListFragmentPlayer2.ID) != null) {
//				ft.remove(fragmentManager.findFragmentByTag(BattleListFragmentPlayer2.ID));
//			}
//		}
//
//		ft.commit();
		
		
		updateTwoPlayerLayout();
		// updateChronoLayout();
		
		// createBlueToothServer();
		
	}
 	
	
 	protected void recreateBattleFromScratch() {
		Log.d(TAG, "recreateBattleFromScratch");
		StorageManager.createBattleFromArmy(getApplicationContext(), armyPath, BattleSingleton.getInstance(), BattleSingleton.PLAYER1);
		
		BattleSingleton.getInstance().setArmy(null, BattleSingleton.PLAYER2);
		BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER2).clear();
		
		// notify fragments
		BattleListFragment listFragment = (BattleListFragment) mTabsAdapter.getItem(0); // first tab
		listFragment.refreshAllList();
		
		int chronoPageNumber = mTabsAdapter.getTabIndexForId(CHRONO_FRAGMENT);
		((ChronoFragment) mTabsAdapter.getItem(chronoPageNumber)).updateDisplay();
//

        boolean hasSpecialist = false;
        for (BattleEntry entry : BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1)) {
            if (entry.isSpecialist()) {
                hasSpecialist = true;
                break;
            }
        }

        if (hasSpecialist) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Specialist");
            alert.setMessage("This army contains specialist. Hit the specialist menu to select your entries...");
            alert.show();
        }


//		FragmentManager fragmentManager = getSupportFragmentManager();
//
//		if (fragmentManager.findFragmentByTag(BattleListFragmentPlayer1.ID) != null) {
//			BattleListFragment listFragment = (BattleListFragment) fragmentManager.findFragmentByTag(BattleListFragmentPlayer1.ID);
//			Log.d(TAG, "listFragment.refreshAllList");
//			listFragment.refreshAllList();
//		}
//
//		if (fragmentManager.findFragmentByTag(ChronoFragment.ID) != null) {
//			((ChronoFragment) fragmentManager.findFragmentByTag(ChronoFragment.ID)).updateDisplay();
//		}

		updateTwoPlayerLayout();
	}

	public BattleSingleton getCurrentArmy() {
		return null;
	}
	
	public BattleEntry getCurrentEntry() {
		return BattleSingleton.getInstance().getCurrentEntry();
	}

	@Override
	public BattleList getArmyList() {
		return null;
	}
	
	
	@Override
	public void viewBattleEntryDetail(BattleEntry model) {
		Log.d(TAG, "viewModelDetail");
		SelectionModelSingleton.getInstance().setCurrentlyViewedElement(
				ArmySingleton.getInstance().getArmyElements()
						.get(model.getId()));
		
		viewModelDetail(null);

	}

	@Override
	public ArmyElement getArmyElement() {
		return SelectionModelSingleton.getInstance().getCurrentlyViewedElement();
	}

	public void removeViewCardFragment(View v) {
		Log.d(TAG,"removeViewCardFragment");
	}

	@Override
	public boolean isCardfullScreen() {
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
	public void showDamageDialog(MultiPVModel model) {
		DamageGrid grid = model.getDamageGrid();
		Log.d(TAG, "showDamageDialog");
		
		BattleSingleton.getInstance().setCurrentGrid(grid);
		BattleSingleton.getInstance().setCurrentModel(model);
		
		if (grid instanceof ModelDamageLine ) {
			DialogFragment dialog = new SingleLineDamageFragment();
			dialog.show(getSupportFragmentManager(), "SingleLineDamageFragment");
		} else if (grid instanceof  WarjackLikeDamageGrid) {
			if (grid instanceof ColossalDamageGrid) {
				DialogFragment dialog = new ColossalGridDamageFragment();
				dialog.show(getSupportFragmentManager(), "ColossalDamageFragment");
			} else if (grid instanceof MyrmidonDamageGrid) {
				DialogFragment dialog = new MyrmidonGridDamageFragment();
				dialog.show(getSupportFragmentManager(), "MyrmidonGridDamageFragment");
			} else {
				DialogFragment dialog = new WarjackGridDamageFragment();
				dialog.show(getSupportFragmentManager(), "WarjackGridDamageFragment");
			}
		} else if (grid instanceof WarbeastDamageSpiral) {
			DialogFragment dialog = new WarbeastSpiralDamageFragment();
			dialog.show(getSupportFragmentManager(), "WarbeastDamageFragment");
		} else if (grid instanceof MultiPVUnitGrid) {
			DialogFragment dialog = new MultiPVUnitDamageFragment();
			dialog.show(getSupportFragmentManager(), "GridDamageFragment");
		}
	}

	public MultiPVModel getCurrentModel() {
		return BattleSingleton.getInstance().getCurrentModel();
	}
	
	@Override
	public DamageGrid getCurrentDamageGrid() {
		return BattleSingleton.getInstance().getCurrentGrid();
	}

	@Override
	public void onBackPressed() {
		
		int currentPage = pager.getCurrentItem();
		if (currentPage > 0) {
			pager.setCurrentItem(currentPage-1, true);
			return;
		}
		
		

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.exit_battle_title);
		alert.setMessage(R.string.exit_battle_confirm);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			
			// stop chrono
			BattleSingleton.getInstance().stopChronos();
			
			StorageManager.saveBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());
			
			// unbind bluetooth
			if (SteamPunkRosterApplication.getInstance().isUsesBluetooth()) {
				SteamPunkRosterApplication.getInstance().resetBluetoothSocket();	
			}
			
			BattleActivity.this.finish();
		}
		});

		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			// Toast.makeText(getApplicationContext(), "Exit cancelled", Toast.LENGTH_SHORT).show();
		  }
		});

		alert.show();
	}

	public void viewModelDetailInNewActivity(View v) {
		Log.d("viewModelDetailInNewActivity", "viewModelDetailInNewActivity");
		Intent intent = new Intent(this, ViewCardActivity.class);
		intent.putExtra(ViewCardActivity.MODEL_ID, getArmyElement().getId());
		startActivity(intent);
	}

	@Override
	public void setInitialMinuteCount(int nbMinutes) {
		BattleSingleton.getInstance().reInitAndConfigChrono(nbMinutes);

		int chronoPosition = mTabsAdapter.getTabIndexForId(CHRONO_FRAGMENT);
		if (chronoPosition != -1) {
			ChronoFragment chronoFragment = (ChronoFragment) mTabsAdapter.getItem(chronoPosition);
			chronoFragment.updateDisplay();
		}

	}

	@Override
	public boolean useSingleClick() {
		return false;
	}

	
//	/**
//	 * switch view to the "selected" panel
//	 * @param view
//	 */
//	public void toPlayer2(View v) {
//		ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
//		if (viewSwitcher.getCurrentView().getId() == R.id.player1_zone  ) {
//			Animation slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
//			viewSwitcher.setInAnimation(slideRightAnimation);
//			viewSwitcher.showNext();	
//		}
//	}
//
//	/**
//	 * switch view to the "selection" panel
//	 * @param view
//	 */
//	public void toPlayer1(View v) {
//		ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
//		if (viewSwitcher.getCurrentView().getId() == R.id.player2_zone ) {
//			Animation slideLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right);
//			viewSwitcher.setInAnimation(slideLeftAnimation);
//			viewSwitcher.showPrevious();	
//		}
//	}
//	

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.battle_menu, menu);
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
     * @param menu
     */
    private void handleMenuVisibility(Menu menu) {
    	
    	if (BattleSingleton.getInstance().hasPlayer2()) {
    		menu.findItem(R.id.menu_add_army).setVisible(false);	
    	} else {
    		menu.findItem(R.id.menu_add_army).setVisible(true);
    	}
    	
    	if (FAKE_BT) {
    		menu.findItem(R.id.startBlueTooth).setVisible(false);	
    	} else {
    		menu.findItem(R.id.startBlueTooth).setVisible(true);
    	}
    	

    	
    }
	
    public void choosePlayer2() {
    	
		DialogFragment dialog = new ChooseArmyListDialog();
		dialog.show(getSupportFragmentManager(), "ChooseArmyListDialog");

//		final List<ArmyListDescriptor> armyLists = StorageManager.getArmyLists(getApplicationContext());
//
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		alert.setTitle(R.string.choose_player2);
//		alert.setItems(getArmyLabels(armyLists), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            	ArmyListDescriptor selectedArmy = armyLists.get(which);
//            	
//            	StorageManager.createBattleFromArmy(getApplicationContext(), selectedArmy.getFileName(), BattleSingleton.getInstance(), BattleSingleton.PLAYER2);
//            	
//            	BattleListFragment player2Fragment = new BattleListFragmentPlayer2();
//        		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        		ft.replace(R.id.fragment_player2_placeholder, player2Fragment);
//        		ft.commit();
//        
//        		
//        		updateTwoPlayerLayout();
//        		updateTitleAndSaveBattle(selectedArmy.getFileName());
//        		}
//		});
//		alert.show();
		
    }

    private void updateTitleAndSaveBattle(String player2FileName) {
    	battleName = battleName + " vs. " + player2FileName;
    	
    	// recompute battle path
    	File f = new File(battlePath);
    	String root = f.getParent();
    	battlePath = root + File.separator + battleName;
    	
    	StorageManager.saveBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());
    	getIntent().putExtra(INTENT_BATTLE_NAME, battleName);
    	getIntent().putExtra(INTENT_BATTLE_PATH, battlePath);
    	
    	getSupportActionBar().setTitle(battleName);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		switch (item.getItemId()) {
//			case R.id.menu_battle_planner: 
//				prepareBattlePlan();
//				return true;
	        case R.id.menu_add_army:
	            choosePlayer2();
	            return true;
	        case R.id.menu_search:
	        	onSearchRequested();
	        	return true;
	        case R.id.menu_chrono:
	        	openChronoConfig(null);
	            return true;
	        case R.id.menu_end:
	        	endBattle(null);
	        	return  true;
	        case R.id.menu_card_reference:
	        	openCardLibrary();
	        	return true;
	        case R.id.menu_dice:
	        	openDiceDialog();
	        	return true;
	        case R.id.menu_steamroller:
	        	openScenarioLibrary();
	        	return true;
            case R.id.menu_specialist:
                chooseSpecialists();
                return true;
	        case R.id.startBlueTooth: 
	        	startActivity(new Intent(this, EnableBluetoothActivity.class));
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


    private void chooseSpecialists() {

        DialogFragment dialog = new ChooseSpecialistsDialog();
        dialog.show(getSupportFragmentManager(), "ChooseSpecialistsDialog");

    }
	
	private void openScenarioLibrary() {
		Intent intent = new Intent(this, ScenarioLibraryActivity.class);
		intent.putExtra(ScenarioLibraryActivity.INTENT_CHOOSE_SCENARIO, true);
		startActivityForResult(intent, ScenarioLibraryActivity.CHOOSE_SCENARIO);
	}

	private void prepareBattlePlan() {
/*
Intent intent = new Intent(this, BattlePlanerActivity.class);
startActivity(intent);
*/
    }
	
	



	private void openDiceDialog() {
		FragmentManager fm = getSupportFragmentManager();
		DiceRollFragment dialog = new DiceRollFragment();
		dialog.setShowsDialog(true);
		dialog.show(fm, "DiceRollFragment");
	}
	
	private void openCardLibrary() {
		
		FragmentManager fm = getSupportFragmentManager();
		ChooseCardFromLibraryDialog dialog = new ChooseCardFromLibraryDialog();
		dialog.setShowsDialog(true);
		dialog.show(fm, "ChooseCardFromLibraryDialog");
	}
	
	
	
	
	/**
	 * force display to accomodate 1 or 2 player depending on layout disposition (large screen, landscape, ...)
	 */
	public void updateTwoPlayerLayout() {
		
		Log.d(TAG, "updateTwoPlayerLayout");
		
		if (mTabsAdapter == null) {
			mTabsAdapter = new TabsAdapter(this, pager);
			// getSupportActionBar().removeAllTabs();
		}
		
		
		if (mTabsAdapter.getTabIndexForId(PLAYER1_FRAGMENT) == -1) {
	 		BattleListFragment player1Fragment = new BattleListFragmentPlayer1();
			mTabsAdapter.addTab(PLAYER1_FRAGMENT,  getResources().getString(R.string.player1),
					player1Fragment, null);
		}
		
		if (BattleSingleton.getInstance().hasPlayer2()) {
			if (mTabsAdapter.getTabIndexForId(PLAYER2_FRAGMENT) == -1) {
	 			BattleListFragment player2Fragment = new BattleListFragmentPlayer2();

				mTabsAdapter.addTabAtPosition(PLAYER2_FRAGMENT, getResources().getString(R.string.player2),
						player2Fragment, null, 1); // just after "player 1"
			}
		} else {
			// remove tab if any
			int tabPlayer2Index = mTabsAdapter.getTabIndexForId(PLAYER2_FRAGMENT);
			if (tabPlayer2Index != -1) {
				mTabsAdapter.removeTab(tabPlayer2Index);
			}
			
		}
		if (mTabsAdapter.getTabIndexForId(CHRONO_FRAGMENT) == -1) {
			ChronoFragment chrono = new ChronoFragment();
			mTabsAdapter.addTab(CHRONO_FRAGMENT, getResources().getString(R.string.chronotab),
					chrono, null);
		}

		
		mTabsAdapter.notifyDataSetChanged();
		
		
	       // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);
		
		
		myInvalidateOptionsMenu();
		
//		
//		ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
//		
//		// player1_zone
//		// toRightButton
//		
//		if (BattleSingleton.getInstance().hasPlayer2()) {
//			Log.d(TAG, "updateTwoPlayerLayout : 2 players");
//			if (viewSwitcher != null) {
//				// switcher, make sure button is visible
//				ImageButton toRightButton = (ImageButton) findViewById(R.id.toRightButton);
//				toRightButton.setVisibility(View.VISIBLE);
//			} else {
//				// fullscreen, make sure placeHolder for player2 is visible
//				FrameLayout player2PlaceHolder = (FrameLayout) findViewById(R.id.fragment_player2_placeholder);
//				player2PlaceHolder.setVisibility(View.VISIBLE);
//			}
//			
//			if ( findViewById(R.id.layout_chrono_and_card) != null) {
//				
//				LinearLayout chronoCardLayout = (LinearLayout) findViewById(R.id.layout_chrono_and_card);
//				LayoutParams params = (LayoutParams) chronoCardLayout.getLayoutParams();
//				params.weight = 1;
//				chronoCardLayout.invalidate();
//				
//				// mask chrono only if 2 players and a card already visible.
//				FragmentManager fragmentManager = getSupportFragmentManager();
//				if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) != null) {
//					if ( findViewById(R.id.chrono_zone) != null) {
//						findViewById(R.id.chrono_zone).setVisibility(View.GONE);
//					}
//				}
//				
//				
//			}
//
//			
//		} else {
//			Log.d(TAG, "updateTwoPlayerLayout : 1 player");
//			if (viewSwitcher != null) {
//				// switcher, make sure button is invisible
//				ImageButton toRightButton = (ImageButton) findViewById(R.id.toRightButton);
//				toRightButton.setVisibility(View.GONE);
//			} else {
//				// fullscreen, make sure placeHolder for player2 is masked
//				FrameLayout player2PlaceHolder = (FrameLayout) findViewById(R.id.fragment_player2_placeholder);
//				player2PlaceHolder.setVisibility(View.GONE);
//				
//				// change layout weight to accomodate more screen for cards
//				if ( findViewById(R.id.layout_chrono_and_card) != null) {
//					
//					LinearLayout chronoCardLayout = (LinearLayout) findViewById(R.id.layout_chrono_and_card);
//					LayoutParams params = (LayoutParams) chronoCardLayout.getLayoutParams();
//					params.weight = 2;
//					chronoCardLayout.invalidate();
//					
//				}
//				
//				
//			}
//			
//		}
//		
//		invalidateOptionsMenu();
//
//		// if card not showing, hide zone
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		if (fragmentManager.findFragmentByTag(ViewCardFragment.ID) == null) {
//			FrameLayout cardZone = (FrameLayout) findViewById(R.id.card_zone);
//			if (cardZone != null) {
//				cardZone.setVisibility(View.GONE);	
//			}
//		}
//		
	}
	
	@SuppressLint("NewApi")
	private void myInvalidateOptionsMenu() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		} else {
			supportInvalidateOptionsMenu();
		}
	}
	
	private void updateChronoLayout(boolean shouldStartChrono) {
		
		int chronoPosition = mTabsAdapter.getTabIndexForId(CHRONO_FRAGMENT);
		if (chronoPosition != -1) {
			ChronoFragment chronoFragment = (ChronoFragment) mTabsAdapter.getItem(chronoPosition);
			chronoFragment.updateDisplay();
			
			if (shouldStartChrono) {
				chronoFragment.notifyStartAnimation();
			}
		}

	}

	@Override
	public void openChronoConfig(View v) {
		ChronoConfigDialog dialog = new ChronoConfigDialog();
		dialog.show(this.getSupportFragmentManager(), "ChronoConfigDialog");
	}


	@Override
	public void onArmyListSelected(ArmyListDescriptor selectedArmy) {
    	
    	StorageManager.createBattleFromArmy(getApplicationContext(), selectedArmy.getFilePath(), BattleSingleton.getInstance(), BattleSingleton.PLAYER2);
    	
//    	BattleListFragment player2Fragment = new BattleListFragmentPlayer2();
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		ft.replace(R.id.fragment_player2_placeholder, player2Fragment);
//		ft.commit();

		
		updateTwoPlayerLayout();
		updateTitleAndSaveBattle(selectedArmy.getFileName());
	
	}

	@Override
	public void onArmyListDeleted(ArmyListDescriptor army) {
	}

	public void endBattle(View v) {
		DialogFragment dialog = new EndBattleDialog();
        dialog.show(getSupportFragmentManager(), "EndBattleDialog");
	}


	@Override
	public void endBattle(int winnerNumber, String player2name,
			String clockType, String scenario, String victoryCondition,
			String notes) {

        SimpleDateFormat sdf = new SimpleDateFormat();

		BattleResult result = new BattleResult();
		
		result.setArmyName(battleName);
		result.setBattleDate(sdf.format(new Date()));
		result.setWinnerNumber(winnerNumber);
		result.setPlayer2name(player2name);
		result.setClockType(clockType);
		result.setScenario(scenario);
		result.setVictoryCondition(victoryCondition);
		result.setNotes(notes);
		
		result.setArmy1(BattleSingleton.getInstance().getArmy(BattleSingleton.PLAYER1));
		
		if (BattleSingleton.getInstance().hasPlayer2()) {
			result.setArmy2(BattleSingleton.getInstance().getArmy(BattleSingleton.PLAYER2));
		}
		
		StorageManager.saveBattleResult(getApplicationContext(), result, false );

		BattleSingleton.getInstance().stopChronos();
		
		StorageManager.saveBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());

		// unbind bluetooth
		if (SteamPunkRosterApplication.getInstance().isUsesBluetooth()) {
			SteamPunkRosterApplication.getInstance().resetBluetoothSocket();	
		}

		
		BattleActivity.this.finish();
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
		
		int currentPageNumber = pager.getCurrentItem();
		
		// position the card after the current fragment, so that a swipe "back" returns to the current fragment
		// also, this mean the card fragment is NOT destroyed when going back (due to proximity cache) 
		mTabsAdapter.addTabAtPosition(CARD_FRAGMENT , tabTitle, viewCardFragment, null, currentPageNumber+1);
		cardTabIndex = mTabsAdapter.getTabIndexForId(CARD_FRAGMENT);
		
		mTabsAdapter.selectTab(cardTabIndex);
		
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);

		
		// select the tab with entry
		//getSupportActionBar().setSelectedNavigationItem(cardTabIndex);
		
		
//		FragmentManager fragmentManager = getSupportFragmentManager();
//
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
//
//			if (BattleSingleton.getInstance().hasPlayer2())  {
//				// mask chrono only if 2 players...
//				if ( findViewById(R.id.chrono_zone) != null) {
//					findViewById(R.id.chrono_zone).setVisibility(View.GONE);
//				}
//			}
//
//			
//		} else {
//
//			// open new activity
//			Intent intent = new Intent(this, ViewCardActivity.class);
//			intent.putExtra(ViewCardActivity.MODEL_ID, getArmyElement().getId());
//			startActivity(intent);
//
//		}
	}

    public void registerDamageGridToBlueTooth() {
    	if (comService != null) {
        	Log.e(TAG, "registering com service to each damage grid");
            List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
            for (BattleEntry entry : entries) {
            	if (entry.hasDamageGrid()) {
            		((MultiPVModel) entry).getDamageGrid().registerObserver(comService);
            	}
            }
    	}
    }
    
    public void unregisterDamageGridToBlueTooth() {
    	Log.e(TAG, "unregistering com service to each damage grid");
        List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
        for (BattleEntry entry : entries) {
        	if (entry.hasDamageGrid()) {
        		((MultiPVModel) entry).getDamageGrid().removeObserver(comService);
        	}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// bindService(new Intent(this, CommunicationService.class), onService, BIND_AUTO_CREATE);
		
		
		if (requestCode == ScenarioLibraryActivity.CHOOSE_SCENARIO) {
			if (resultCode == RESULT_OK) {
				Mission scenario = SteamRollerSingleton.getInstance().getCurrentMission();
				Toast.makeText(this, "Scenario selected : " + scenario.getName(), Toast.LENGTH_SHORT).show();
				
				BattleSingleton.getInstance().setScenario(scenario);
				
				boolean listAltered = false;
				List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
				for (BattleEntry entry : entries) {
					if (entry.getReference().getFaction() == FactionNamesEnum.OBJECTIVES) {
						entries.remove(entry);
						listAltered = true;
						break;
					}
				}

				if (scenario.getObjectiveResourceId() != 0) {
					listAltered = true;
					String objectiveName = getResources().getResourceEntryName(scenario.getObjectiveResourceId());
					ArmyElement element = ArmySingleton.getInstance().getArmyElement(objectiveName);
					
					int countEntries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1).size();
					BattleEntry bEntry = new SingleDamageLineEntry(element, countEntries++, 0, false);
					
					BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1).add(bEntry);
				
				}

				if (listAltered) {
					BattleListFragment listFragment = (BattleListFragment) mTabsAdapter.getItem(0); // first tab
					listFragment.refreshAllList();
//					FragmentManager fragmentManager = getSupportFragmentManager();
//					if (fragmentManager.findFragmentByTag(BattleListFragmentPlayer1.ID) != null) {
//						BattleListFragment listFragment = (BattleListFragment) fragmentManager.findFragmentByTag(BattleListFragmentPlayer1.ID);
//						Log.d(TAG, "listFragment.refreshAllList");
//						listFragment.refreshAllList();
//					}				

				}
				
			}
		}
	}

	
	/****************************************************************************************/
	/** Managing connection *******************************************************************/
	/****************************************************************************************/

	/** * Create a server socket connection Acting like a client */
	private void creatingClientSocketConnection() {
		
		SteamPunkRosterApplication.getInstance().setiAmTheServer(false);
		
		try {
			Log.e(TAG, "creatingClientSocketConnection");
			// find the device you want to connect to (already stored in the
			// application)
			// It's the one discovered and choose by the user
			BluetoothDevice bluetoothDevice = SteamPunkRosterApplication.getInstance()
					.getRemoteDevice();
			// if a device has been choosen
			if (bluetoothDevice != null) {
				// Ask to connect to with that UUID
				bluetoothClientSocket = bluetoothDevice
						.createRfcommSocketToServiceRecord(SteamPunkRosterApplication.MY_UUID);
				// The thread that will listen for client to connect
				AsyncTask<Void, String, String> acceptClientThread = new AsyncTask<Void, String, String>() {
					@Override
					public String doInBackground(Void... params) {
						// Cancel discovery because it will slow down the
						// connection
						BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
						try {
							// Connect the device through the socket. This will
							// block
							// until it succeeds or throws an exception
							Log.e(TAG, "creating client connection : socket waiting to connect");
							bluetoothClientSocket.connect();
							Log.e(TAG, "creating client connection : socket CONNECTED : ok!");
							// Set the bluetoothSocket with that socket
							SteamPunkRosterApplication.getInstance().setBluetoothSocket(
									bluetoothClientSocket);
							
							// So the socket has been created and store
							// the CommunicationService is so launched
							// so bind to it
							Log.e(TAG, "socket has been created, bindToService!");
							bindToService();
						} catch (IOException connectException) {
							// Unable to connect; close the socket and get out
							Log.e(TAG, "Unable to connect");
							try {
								SteamPunkRosterApplication.getInstance().setUsesBluetooth(false);
								if (SteamPunkRosterApplication.getInstance()
										.getBluetoothSocket() != null) {
									SteamPunkRosterApplication.getInstance()
											.getBluetoothSocket().close();
								}
							} catch (IOException closeException) {
							}
							return "Connection failed. Is the other device online? ";
						}
						return "connection created";
					}
					
					protected void onPostExecute(String result) {
				        getSupportActionBar().setSubtitle(result);
				    }

				};
				// start the thread
				acceptClientThread.execute();
			}
		} catch (IOException e) {
			Log.e(TAG, "creatingClientSocketConnection is in error : ", e);
		}

	}
	
	/**
	 * Just bind to the CommunicationService service
	 */
	private void bindToService() {
		// the service is already started so bind to it
		bindService(new Intent(this, CommunicationService.class), onService,
				BIND_AUTO_CREATE);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
		
		if (SteamPunkRosterApplication.getInstance().isUsesBluetooth()) {
			Log.e(TAG, "using bluetooth : TRUE");
			// register the receiver that listens for Intent launched by the
			// communication service
			if ( ! isReceiverRegistred) { 
				registerReceiver(communicationServiceReceiver, comServiceFilter);
				isReceiverRegistred = true;
			}
			
			// if the socket is not set yet, => you have to connect to the other
			// device as a client
			// else the other device is already connected to you
			if (SteamPunkRosterApplication.getInstance().getBluetoothSocket() == null) {
				BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false);
				Log.e(TAG, "onResume, actually no Socket connected --> create one");
				// Initialize the connection as a client:
				getSupportActionBar().setSubtitle("Creating connection... wait.");
				creatingClientSocketConnection();
			} else {
				Log.e(TAG, "onResume Socket connected ");
				// make sure we are bound to the communication service
				if (! serviceIsBound) {
					getSupportActionBar().setSubtitle("Connection incoming...");
					bindToService();
				}
			}
		}
		
		// because we unregister upon "onPause"
		// note that when the activity has been destroyed, it is not sufficient as the
		// comm service has been unbound, and at this point it is not yet rebound
		// see onServiceConnected()
		registerDamageGridToBlueTooth();
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.e(TAG, "onPause");
		super.onPause();
		
		if (SteamPunkRosterApplication.getInstance().isUsesBluetooth()) {
			// unregister the receiver that listens for Intent launched by the
			// communication service
			if (isReceiverRegistred) {
				unregisterReceiver(communicationServiceReceiver);
				isReceiverRegistred = false;
			}
			
			
		}

		if (true) {
			Log.e(TAG, "unregister bluetooth observers for grid #1");
			List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
			for (BattleEntry entry : entries) {
				if (entry.hasDamageGrid()) {
					
					((MultiPVModel) entry).getDamageGrid().removeBluetoothObservers();
				}
			}
//			entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER2);
//			for (BattleEntry entry : entries) {
//				if (entry.hasDamageGrid()) {
//					Log.e(TAG, "unregister observers for grid #2");
//					((MultiPVModel) entry).getDamageGrid().removeBluetoothObservers();
//				}
//			}
		}
		
		
		StorageManager.saveBattle(getApplicationContext(), battlePath, BattleSingleton.getInstance());

	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");

		super.onDestroy();

		// unbind to the service
		if (serviceIsBound) {
			unregisterDamageGridToBlueTooth();
			Log.e(TAG, "onDestroy - unbind service"); 
			unbindService(onService);
		}

		
	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
	    	Uri data = intent.getData();
	        showResult(data);
	    } else if (SearchHelper.INTENT_CARD.equals(intent.getAction())) {
	    	Uri uri = intent.getData();
	        String rowId = uri.getLastPathSegment();
	        showModel(rowId);
	    } else if (SearchHelper.INTENT_SPELL.equals(intent.getAction())) {
	    	Uri uri = intent.getData();
	    	String rowId = uri.getLastPathSegment();
	    	showSpell(rowId);
	    } else if (SearchHelper.INTENT_CAPACITY.equals(intent.getAction())) {
	    	Uri uri = intent.getData();
	    	String rowId = uri.getLastPathSegment();
	    	showCapacity(rowId);
	    }
	}

	private void showModel(String rowId) {
		String[] columns = new String[] {
				SearchHelper.KEY_ID,
		        SearchHelper.KEY_WORD,
		        SearchHelper.KEY_DEFINITION};

		SearchHelper mDictionary = new SearchHelper(this);
		
		Cursor result = mDictionary.getWord(rowId, columns);
		result.moveToFirst();
		int columnIndexForId = result.getColumnIndex(SearchHelper.KEY_ID);
		String modelId = result.getString(columnIndexForId);

		ArmyElement element = ArmySingleton.getInstance().getArmyElement(modelId);
		if (element != null) {
			SelectionModelSingleton.getInstance().setCurrentlyViewedElement(element);
			viewModelDetail(null);
		} else {
			Toast.makeText(this, "sorry, cannot view this entry :-(", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showCapacity(String rowId) {
        String[] columns = new String[] {
        		SearchHelper.KEY_ID,
                SearchHelper.KEY_WORD,
                SearchHelper.KEY_DEFINITION};

        SearchHelper mDictionary = new SearchHelper(this);
        
        Cursor result = mDictionary.getWord(rowId, columns);
        result.moveToFirst();
        int columnIndexForIdsUsingSpell = result.getColumnIndex(SearchHelper.KEY_ID);
        int columnIndexForSpellName = result.getColumnIndex(SearchHelper.KEY_WORD);
        int columnIndexForDefinition = result.getColumnIndex(SearchHelper.KEY_DEFINITION);
        String modelsId = result.getString(columnIndexForIdsUsingSpell);
        String capacityName = result.getString(columnIndexForSpellName);
        String description = result.getString(columnIndexForDefinition);

        AlertDialog.Builder capacityDialog = new AlertDialog.Builder(this);
        capacityDialog.setTitle(capacityName);
        capacityDialog.setIcon(R.drawable.ic_menu_attachment);
        capacityDialog.setMessage(description);
        capacityDialog.show();
        
	}
	
	private void showSpell(String rowId) {
		
        String[] columns = new String[] {
        		SearchHelper.KEY_ID,
                SearchHelper.KEY_WORD,
                SearchHelper.KEY_DEFINITION};

        SearchHelper mDictionary = new SearchHelper(this);
        
        Cursor result = mDictionary.getWord(rowId, columns);
        result.moveToFirst();
        int columnIndexForIdsUsingSpell = result.getColumnIndex(SearchHelper.KEY_ID);
        int columnIndexForSpellName = result.getColumnIndex(SearchHelper.KEY_WORD);
        int columnIndexForDefinition = result.getColumnIndex(SearchHelper.KEY_DEFINITION);
        String modelsId = result.getString(columnIndexForIdsUsingSpell);
        String spellName = result.getString(columnIndexForSpellName);
        String description = result.getString(columnIndexForDefinition);

        // users for spell format : YW08,YW06,Yw02
        // spell name : ARCANE BLAST
		// description format : [COST=3|RNG=6|AOE=-|POW=-|UP=NO|OFF=NO] description
        
		String spellStats = description.substring(1, description.indexOf(']'));
		String spellDescription = description.substring(description.indexOf(']')+1, description.length()).trim();
		System.out.println(spellStats);
        
		String[] values = new String[6];
		int i = 0;
        StringTokenizer stk = new StringTokenizer(spellStats, "|");
        while (stk.hasMoreTokens()) {
        	String carac = stk.nextToken();
        	String value = carac.substring(carac.indexOf('=')+1, carac.length());
        	values[i] = value;
        	i++;
        }
        
        StringBuffer sbUsers = new StringBuffer(256);
        StringTokenizer stkModels = new StringTokenizer(modelsId, ",");
        boolean first = true;
        while (stkModels.hasMoreTokens()) {
        	String modelId = stkModels.nextToken();
        	ArmyElement element = ArmySingleton.getInstance().getArmyElement(modelId);
        	if (element != null) {
        		if (!first) {
        			sbUsers.append("; ");
        		}
        		sbUsers.append(element.getFullName());
        		first = false;
        	}
        }
        
		
		AlertDialog.Builder spellDialog = new AlertDialog.Builder(this);
		spellDialog.setTitle(spellName);
		spellDialog.setIcon(R.drawable.magical_weapon_icon);

		LayoutInflater inflater = getLayoutInflater();

		View spellView = (View) inflater.inflate(R.layout.spell_detail, null);
		TableLayout spellTable = (TableLayout) spellView.findViewById(R.id.spellTable);
		spellTable.removeView(spellTable.findViewById(R.id.toRemove1));
		spellTable.removeView(spellTable.findViewById(R.id.toRemove2));

		View spellLineCaracView = inflater.inflate(R.layout.spell_line_carac_dialog, null, false);	
		((TextView) spellLineCaracView.findViewById(R.id.spellTitle)).setText(spellName);
		((TextView) spellLineCaracView.findViewById(R.id.spellCost)).setText(values[0]); 
		((TextView) spellLineCaracView.findViewById(R.id.spellRange)).setText(values[1]);
		((TextView) spellLineCaracView.findViewById(R.id.spellAOE)).setText(values[2]);
		((TextView) spellLineCaracView.findViewById(R.id.spellPOW)).setText(values[3]);
		((TextView) spellLineCaracView.findViewById(R.id.spellUP)).setText(values[4]);
		((TextView) spellLineCaracView.findViewById(R.id.spellOFF)).setText(values[5]);
		spellTable.addView(spellLineCaracView);

		View spellLineTextView = inflater.inflate(R.layout.spell_line_text_dialog, null, false);
		((TextView) spellLineTextView.findViewById(R.id.spellDescription)).setText(Html.fromHtml(spellDescription));
		spellTable.addView(spellLineTextView);

		
		TextView tvUsers = (TextView) spellView.findViewById(R.id.textViewSpellUsers);
		tvUsers.setText(sbUsers.toString());

		spellDialog.setView(spellView);
		spellDialog.show();
	}
	

	private void showResult(Uri data) {
		Toast.makeText(this, "show result " + data.toString(), Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void doMySearch(String query) {
		
		CursorLoader loader = new CursorLoader(this, SearchSuggestionProvider.CONTENT_URI, null, null,new String[] {query}, null);

		Cursor cursor  = loader.loadInBackground();
		
		if (cursor != null) {

			String[] from = new String[] {SearchHelper.KEY_WORD, SearchHelper.KEY_DEFINITION};

			Log.d(TAG, "found " + cursor.getCount() + " entries");
			String value = cursor.getString(0);
			Log.d(TAG, "first value = " + value);
			
			// Create a simple cursor adapter to keep the search data
			
			final SimpleCursorAdapter cursorAdapter;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, new int[]{android.R.id.text1, android.R.id.text2 }, 0);
			} else {
				cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, new int[]{android.R.id.text1, android.R.id.text2});
			}
			

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Search Results")
		    	.setAdapter(cursorAdapter, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
		            	   
		            	   Cursor cursor = (Cursor) cursorAdapter.getItem(which);
		            	   int columnIndexForId= cursor.getColumnIndex(BaseColumns._ID);
		            	   String rowId = cursor.getString(columnIndexForId);
		            	   
		            	   int columnIndexForAction = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
		            	   String action = cursor.getString(columnIndexForAction);
		            	   
		            	   if (SearchHelper.INTENT_CARD.equals(action)) {
		            		   showModel(rowId);   
		            	   } else if (SearchHelper.INTENT_SPELL.equals(action)) {
		            		   showSpell(rowId);
		            	   }
		            	   
		               }
		           });
		    AlertDialog dialog = builder.create();
		    dialog.show();

		}
	}

	@Override
	public void playPause(int playerNumber) {
		Log.e(TAG, "playPause - " + playerNumber);
		if (comService != null) {
			BattleCommunicationObject message = new BattleCommunicationObject();
			if (playerNumber == BattleSingleton.PLAYER1) {
				message.setAction(CommAction.PLAYER1_PLAY);	
			}
			if (playerNumber == BattleSingleton.PLAYER2) {
				message.setAction(CommAction.PLAYER2_PLAY);	
			}
			comService.sendMessage(message);
		}
	}

	@Override
	public void pause() {
		Log.e(TAG, "chrono - pause");
		if (comService != null) {
			BattleCommunicationObject message = new BattleCommunicationObject();
			message.setAction(CommAction.CHRONO_PAUSE);	
			comService.sendMessage(message);
		}
	}

	@Override
	public boolean canAddCardToBattle() {
		return true;
	}

	@Override
	public void addModelToBattle(View v) {
		
		ArmyElement element = SelectionModelSingleton.getInstance().getCurrentlyViewedElement();
		
		// calcul entryCounter
		List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
		
		int entryCounter = 0;
		if (! entries.isEmpty()) {
			BattleEntry lastEntry = entries.get(entries.size() - 1);
			entryCounter = lastEntry.getUniqueId();
			entryCounter ++;
		} else {
			entryCounter = 0;
		}
		
		BattleEntry bEntry = null; 
		if (element instanceof ArmyCommander) {
			// do nothing
		} else if (element instanceof Unit) {
			SelectedUnit selectedUnit = new SelectedUnit(element.getId(), element.getFullName(), false);
			Unit unit = (Unit) element;
			bEntry = new MultiPVUnit((SelectedUnit) selectedUnit, unit, entryCounter++, 0, false);

		} else if (element instanceof Solo){
			if (element.hasMultiPVModels()) {
				if (element.getModels().size() == 1) {
					bEntry = new SingleDamageLineEntry(element, entryCounter++, 0, false);
				} else {
					// dragoon
					SelectedDragoon dragoon = new SelectedDragoon(element.getId(), element.getFullName(), true);
					bEntry = new MultiPVUnit(dragoon, element, entryCounter++, 0, false);
				}
			} else {
				bEntry = new BattleEntry(element, entryCounter++, 0, false);
			}
		} else if (element instanceof BattleEngine) {
			bEntry = new SingleDamageLineEntry(element, entryCounter++, 0, false);
		} else if (element instanceof Warjack) {
			bEntry = new JackEntry( (Warjack) element, null, entryCounter++, 0, false);
		} else if (element instanceof Warbeast) {
			bEntry = new BeastEntry( (Warbeast) element, null, entryCounter++, 0, false);
		} else {
			bEntry = new BattleEntry(element, entryCounter++, 0, false);
		}
		
		BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1).add(bEntry);

		BattleListFragment listFragment = (BattleListFragment) mTabsAdapter.getItem(0); // first tab
		listFragment.refreshAllList();

		
	}		

    public void notifySpecialistsChange() {
        BattleListFragment listFragment = (BattleListFragment) mTabsAdapter.getItem(0); // first tab
        listFragment.refreshAllList();
    }

}
