package com.schlaf.steam.activities.battleselection;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.android.common.view.DepthPageTransformer;
import com.example.android.common.view.SlidingTabLayout;
import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseArmyListener;
import com.schlaf.steam.activities.ChooseBattleListener;
import com.schlaf.steam.activities.battle.BattleActivity;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.BattleListDescriptor;
import com.schlaf.steam.storage.BattleListDirectory;
import com.schlaf.steam.storage.StorageManager;
import com.schlaf.steam.tabs.TabsAdapter;

public class BattleSelector extends ActionBarActivity implements ChooseArmyListener, ChooseBattleListener {

    private static final String BATTLES = "battles";
    private static final String ARMIES = "armies";

	TabHost tHost;
    
	/**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
	private SlidingTabLayout mSlidingTabLayout;
	
	TabsAdapter mTabsAdapter; // the adapter for swiping pages
	ViewPager pager; // the pager that handles fragments swipe

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battle_selector);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE );
        getSupportActionBar().setLogo(R.drawable.ic_battle);
        
        getSupportActionBar().setTitle(R.string.startresumeBattle);
 
		pager = (ViewPager)findViewById(R.id.viewpager);
		pager.setOffscreenPageLimit(3);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			pager.setPageTransformer(true, new DepthPageTransformer());
		}

		
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDividerColors(getResources().getColor(R.color.LightGrey));

		if (mTabsAdapter == null) {
			mTabsAdapter = new TabsAdapter(this, pager);
			// getSupportActionBar().removeAllTabs();
		}
		
		if (mTabsAdapter.getTabIndexForId(BATTLES) == -1) {
			ExistingBattlesFragment battlesFragment = new ExistingBattlesFragment();
			mTabsAdapter.addTab(BATTLES,  getResources().getString(R.string.resumebattle),
					battlesFragment, null);
		}
		
		if (mTabsAdapter.getTabIndexForId(ARMIES) == -1) {
			ExistingArmiesFragment armiesFragment = new ExistingArmiesFragment();
			
			mTabsAdapter.addTabAtPosition(ARMIES, getResources().getString(R.string.createbattlefrom),
					armiesFragment, null, 1); // just after "player 1"
		} 

		
		mTabsAdapter.notifyDataSetChanged();
		
		
	       // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout.setViewPager(pager);
		
        
//        
//        
//        tHost = (TabHost) findViewById(android.R.id.tabhost);
//        tHost.setup();
// 
//        /** Defining Tab Change Listener event. This is invoked when tab is changed */
//        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
// 
//            @Override
//            public void onTabChanged(String tabId) {
//                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
//                ExistingBattlesFragment battlesFragment = (ExistingBattlesFragment) fm.findFragmentByTag("battles");
//                ExistingArmiesFragment armiesFragment = (ExistingArmiesFragment) fm.findFragmentByTag("armies");
//                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
// 
//                /** Detaches the battle fragment if exists */
//                if(battlesFragment!=null)
//                    ft.detach(battlesFragment);
// 
//                /** Detaches the armies fragment if exists */
//                if(armiesFragment!=null)
//                    ft.detach(armiesFragment);
// 
//                /** If current tab is battles */
//                if(tabId.equalsIgnoreCase("battle_lists")){
// 
//                    if(battlesFragment==null){
//                        /** Create AndroidFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new ExistingBattlesFragment(), "battles");
//                    }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(battlesFragment);
//                        battlesFragment.refresh();
//                    }
// 
//                }else{    /** If current tab is armies */
//                    if(armiesFragment==null){
//                        /** Create AppleFragment and adding to fragmenttransaction */
//                        ft.add(R.id.realtabcontent,new ExistingArmiesFragment(), "armies");
//                     }else{
//                        /** Bring to the front, if already exists in the fragmenttransaction */
//                        ft.attach(armiesFragment);
//                    }
//                }
//                ft.commit();
//            }
//        };
// 
//        /** Setting tabchangelistener for the tab */
//        tHost.setOnTabChangedListener(tabChangeListener);
// 
// 
//        /** Defining tab builder for battles tab */
//        TabHost.TabSpec tSpecApple = tHost.newTabSpec("battle_lists");
//        tSpecApple.setIndicator(getResources().getString(R.string.resumebattle),getResources().getDrawable(R.drawable.battle_icon));
//        tSpecApple.setContent(new BattleTab(getBaseContext()));
//        tHost.addTab(tSpecApple);
//
//        /** Defining tab builder for armies tab */
//        TabHost.TabSpec tSpecAndroid = tHost.newTabSpec("army_lists");
//        tSpecAndroid.setIndicator(getResources().getString(R.string.createbattlefrom),getResources().getDrawable(R.drawable.edit_list_icon));
//        tSpecAndroid.setContent(new BattleTab(getBaseContext()));
//        tHost.addTab(tSpecAndroid);

        
    }
    
    
    
    
//    public void deleteExistingBattle(final BattleListDescriptor battle) {
//    	Log.d("BattleSelector","deleteExistingBattle " + battle.getFilename());
//    	
//    	
//    	// 1. Instantiate an AlertDialog.Builder with its constructor
//    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//    	// 2. Chain together various setter methods to set the dialog characteristics
//    	builder.setMessage(getResources().getString(R.string.askDeleteBattle) + battle.getFilename());
//    	builder.setTitle(R.string.confirmdeletebattle);
//
//    	
//    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//            	if (StorageManager.deleteBattle(getApplicationContext(), battle.getFilename())) {
//                	// notify fragment...
//                	android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
//                	ExistingBattlesFragment battlesFragment = (ExistingBattlesFragment) fm.findFragmentByTag("battles");
//                	battlesFragment.notifyBattleListDeletion(battle);
//            	} else {
//            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
//            	}
//            }
//        });
//    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User cancelled the dialog
//            }
//        });
//
//    	
//    	// 3. Get the AlertDialog from create()
//    	AlertDialog dialog = builder.create();
//    	
//    	dialog.show();
//    }

	@Override
	public void onArmyListSelected(ArmyListDescriptor army) {
		
		Toast.makeText(getApplicationContext(), R.string.creatingbattlefromlist , Toast.LENGTH_SHORT).show();
		
		// open battle activity
		Intent intent = new Intent(getApplicationContext(), BattleActivity.class);
		intent.putExtra(BattleActivity.INTENT_BATTLE_NAME, army.getFileName());
		intent.putExtra(BattleActivity.INTENT_ARMY_PATH, army.getFilePath());
		intent.removeExtra(BattleActivity.INTENT_BATTLE_PATH);
		intent.putExtra(BattleActivity.INTENT_CREATE_BATTLE_FROM_ARMY, true);
		startActivity(intent);

	}

	@Override
	public void onArmyListDeleted(final ArmyListDescriptor army) {
    	Log.d("BattleSelector","deleteExistingBattle " + army.getFilePath());
    	
    	
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
            		
            		if ( mTabsAdapter.getTabIndexForId(ARMIES) != -1) {
            			ExistingArmiesFragment armiesFragment = (ExistingArmiesFragment) mTabsAdapter.getItem(pager.getCurrentItem());
                    	armiesFragment.notifyArmyListDeletion(army);
            		}
            	} else {
            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
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
	public void onBattleDirectoryDeleted(final BattleListDirectory directory) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setMessage(getResources().getString(R.string.askDeleteFolder) + directory.getTextualPath());
    	builder.setTitle(R.string.confirmDeleteFolder);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyFolder(getApplicationContext(),directory.getFullpath())) {
                	// notify fragment...
        			ExistingBattlesFragment battlesFragment = (ExistingBattlesFragment) mTabsAdapter.getItem(pager.getCurrentItem());
        			battlesFragment.notifyFolderDeletion(directory);
            	} else {
            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
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
	public void onBattleSelected(BattleListDescriptor battle) {
		
		// open battle activity
		Intent intent = new Intent(getApplicationContext(), BattleActivity.class);
		intent.putExtra(BattleActivity.INTENT_BATTLE_NAME, battle.getTitle());
		intent.putExtra(BattleActivity.INTENT_BATTLE_PATH, battle.getFilePath());
		intent.putExtra(BattleActivity.INTENT_CREATE_BATTLE_FROM_ARMY, false);
		
		startActivity(intent);
		
	}

	@Override
	public void onBattleDeleted(final BattleListDescriptor battle) {
		// TODO Auto-generated method stub
		Log.d("BattleSelector","deleteExistingBattle " + battle.getFilename());


		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(getResources().getString(R.string.askDeleteBattle) + battle.getFilename());
		builder.setTitle(R.string.confirmdeletebattle);


		builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				if (StorageManager.deleteBattle(getApplicationContext(), battle.getFilePath())) {
					// notify fragment...
        			ExistingBattlesFragment battlesFragment = (ExistingBattlesFragment) mTabsAdapter.getItem(pager.getCurrentItem());
					battlesFragment.notifyBattleListDeletion(battle);
				} else {
					Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
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
	public void onArmyDirectoryDeleted(final ArmyListDirectory directory) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setMessage(getResources().getString(R.string.askDeleteFolder) + directory.getTextualPath());
    	builder.setTitle(R.string.confirmDeleteFolder);
    	
    	builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	if (StorageManager.deleteArmyFolder(getApplicationContext(),directory.getFullpath())) {
                	// notify fragment...
                	ExistingArmiesFragment armiesFragment = (ExistingArmiesFragment) mTabsAdapter.getItem(pager.getCurrentItem());
                	armiesFragment.notifyFolderDeleted(directory);
            	} else {
            		Toast.makeText(getApplicationContext(), "deletion failed", Toast.LENGTH_SHORT).show();
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
	protected void onResume() {
		
		int battleTabId = mTabsAdapter.getTabIndexForId(BATTLES);
		if (battleTabId != -1) {
			ExistingBattlesFragment battlesFragment = (ExistingBattlesFragment) mTabsAdapter.getItem(battleTabId);
			if (battlesFragment.isAdded()) {
				battlesFragment.refresh();
			}
		}

		
		super.onResume();
	}
}
