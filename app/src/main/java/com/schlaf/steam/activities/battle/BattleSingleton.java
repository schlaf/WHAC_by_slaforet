/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.schlaf.steam.activities.battleplanner.BattlePlanningEntry;
import com.schlaf.steam.activities.chrono.ChronoData;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.Mission;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.storage.ArmyStore;

/**
 * @author S0085289
 *
 */
public class BattleSingleton {

	private static String TAG = "BattleSingleton";  //$NON-NLS-1$
	
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = 2;
	
	private static BattleSingleton singleton;
	
	/** current army */
	private ArmyStore player1Army;
	private ArmyStore player2Army;
	
	private Mission scenario;
	
	/**
	 * the actual army list
	 */
	private List<BattleEntry> player1entries = new ArrayList<BattleEntry>();
	private List<BattleEntry> player2entries = new ArrayList<BattleEntry>();
	
	
	private List<BattlePlanningEntry> battlePlanningEntries = new ArrayList<BattlePlanningEntry>();
	
	private ChronoData player1Chrono = new ChronoData();
	private ChronoData player2Chrono = new ChronoData();
	
	/** show full screen chrono */
	private boolean fullScreenChrono;
	
	
	private boolean player1ArmyTransmitted;
	private boolean player2ArmyReceived;
	
	/**
	 * indicates that we are receiving army2 content
	 */
	private boolean loadingArmy2;
	private List<BattleEntry> player2loadingEntries = new ArrayList<BattleEntry>();
	
	
	/**
	 * the entry viewed/edited
	 */
	private BattleEntry currentEntry;
	
	/** currently viewed element */
	ArmyElement currentArmyElement;

	/** currently edited damage grid */
	DamageGrid currentGrid;
	
	MultiPVModel currentModel;
	
	/** number of model selected in unit (for multi-pv units) */
	private int modelNumber;
	
	private BattleSingleton() {
		super();
	}
	
	public static BattleSingleton getInstance() {
		if (singleton == null) {
			singleton = new BattleSingleton();
		}
		return singleton;
	}

	/**
	 * pause all chrono and reinit initial time
	 */
	public void reInitAndConfigChrono(int nbMinutes) {
		Log.d("BattleSingleton", "reInitAndConfigChrono nbMinutes = " + nbMinutes); //$NON-NLS-1$ //$NON-NLS-2$
		long nbMillis = nbMinutes * 60 * 1000;
		Log.d("BattleSingleton", "reInitAndConfigChrono nbMillis = " + nbMillis); //$NON-NLS-1$ //$NON-NLS-2$
		getPlayer1Chrono().setPaused(true);
		getPlayer1Chrono().setInitialPlayerTimeInMillis(nbMillis);
		if (nbMinutes > 20) {
			getPlayer1Chrono().setNotified20MinutesLeft(false);
		} else {
			getPlayer1Chrono().setNotified20MinutesLeft(true);
		}
		if (nbMinutes > 10) {
			getPlayer1Chrono().setNotified10MinutesLeft(false);	
		} else {
			getPlayer1Chrono().setNotified10MinutesLeft(true);
		}
		
		
		getPlayer2Chrono().setPaused(true);
		getPlayer2Chrono().setInitialPlayerTimeInMillis(nbMillis);
		if (nbMinutes > 20) {
			getPlayer2Chrono().setNotified20MinutesLeft(false);
		} else {
			getPlayer2Chrono().setNotified20MinutesLeft(true);
		}
		if (nbMinutes > 10) {
			getPlayer2Chrono().setNotified10MinutesLeft(false);	
		} else {
			getPlayer2Chrono().setNotified10MinutesLeft(true);
		}
	}
	
	public List<BattleEntry> getEntries(int nbPlayer) {
		if (nbPlayer == 1) {
			return player1entries;
		} else {
			return player2entries;
		}
	}
	
	public BattleEntry getCurrentEntry() {
		return currentEntry;
	}

	public void setCurrentEntry(BattleEntry currentEntry) {
		this.currentEntry = currentEntry;
	}

	public ArmyElement getCurrentArmyElement() {
		return currentArmyElement;
	}

	public void setCurrentArmyElement(ArmyElement currentArmyElement) {
		this.currentArmyElement = currentArmyElement;
	}

	public DamageGrid getCurrentGrid() {
		return currentGrid;
	}

	public void setCurrentGrid(DamageGrid currentGrid) {
		this.currentGrid = currentGrid;
	}

	public MultiPVModel getCurrentModel() {
		return currentModel;
	}
	
	public void setCurrentModel(MultiPVModel model) {
		currentModel = model;
	}

	public ChronoData getPlayer1Chrono() {
		return player1Chrono;
	}

	public void setPlayer1Chrono(ChronoData player1Chrono) {
		this.player1Chrono = player1Chrono;
	}

	public ChronoData getPlayer2Chrono() {
		return player2Chrono;
	}

	public void setPlayer2Chrono(ChronoData player2Chrono) {
		this.player2Chrono = player2Chrono;
	}

	public ArmyStore getArmy(int nbPlayer) {
		if (nbPlayer == PLAYER1) {
			return player1Army;
		} else {
			return player2Army;
		}
	}
		
	public void setArmy(ArmyStore army, int playerNumber) {
		if (playerNumber == PLAYER1) {
			player1Army = army;
		} else {
			player2Army = army;
		}
	}

	/**
	 * indicates that current battle has 2 players
	 * @return
	 */
	public boolean hasPlayer2() {
		if (player2entries != null && ! player2entries.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean isFullScreenChrono() {
		return fullScreenChrono;
	}

	public void setFullScreenChrono(boolean fullScreenChrono) {
		this.fullScreenChrono = fullScreenChrono;
	}

	public void stopChronos() {
		player1Chrono.pause(SystemClock.elapsedRealtime());
		player2Chrono.pause(SystemClock.elapsedRealtime());
		
	}

	public void startLoadingArmy2() {
		loadingArmy2 = true;
		player2loadingEntries.clear();
	}
	
	public void addArmy2Entry(BattleEntry entry) {
		if (loadingArmy2) {
			player2loadingEntries.add(entry);	
		} else {
			Log.w(TAG, "trying to add an entry to army out of sync"); //$NON-NLS-1$
		}
		
	}
	
	public void finishLoadingArmy2() {
		loadingArmy2 = false;
		
		for (BattleEntry entry : player2loadingEntries) {
			ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
			entry.setReference(element);
		}
		
		player2entries = player2loadingEntries;
	}

	public int getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(int modelNumber) {
		this.modelNumber = modelNumber;
	}
	
	public void createBattlePlanningEntries (SharedPreferences prefs) {
		if (battlePlanningEntries == null || battlePlanningEntries.isEmpty()) {
			battlePlanningEntries = new ArrayList<BattlePlanningEntry>(player1entries.size());
			
			for (BattleEntry entry : player1entries) {
				if (entry.getReference().getModelType() != ModelTypeEnum.UNIT_ATTACHMENT && 
						entry.getReference().getModelType() != ModelTypeEnum.WEAPON_ATTACHMENT) {
					
					BattlePlanningEntry plan = new BattlePlanningEntry(entry, prefs);
					battlePlanningEntries.add(plan);
				}
			}
		}
	}

	public List<BattlePlanningEntry> getBattlePlanningEntries() {
		return battlePlanningEntries;
	}

	public void setBattlePlanningEntries(List<BattlePlanningEntry> battlePlanningEntries) {
		this.battlePlanningEntries = battlePlanningEntries;
	}

	public Mission getScenario() {
		return scenario;
	}

	public void setScenario(Mission scenario) {
		this.scenario = scenario;
	}

	public boolean isPlayer1ArmyTransmitted() {
		return player1ArmyTransmitted;
	}

	public void setPlayer1ArmyTransmitted(boolean player1ArmyTransmitted) {
		this.player1ArmyTransmitted = player1ArmyTransmitted;
	}

	public boolean isPlayer2ArmyReceived() {
		return player2ArmyReceived;
	}

	public void setPlayer2ArmyReceived(boolean player2ArmyReceived) {
		this.player2ArmyReceived = player2ArmyReceived;
	}
	

	
}
