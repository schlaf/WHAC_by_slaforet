package com.schlaf.steam.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.battle.BattleSingleton;

public class BattleStore implements Serializable {

	private static final long serialVersionUID = -109115861957576199L;

	private String filename;
	private String title;
	
	private List<BattleEntry> battleEntriesPlayer1 = new ArrayList<BattleEntry>();
	private List<BattleEntry> battleEntriesPlayer2 = new ArrayList<BattleEntry>();
	
	private ArmyStore player1Army;
	private ArmyStore player2Army;
	
	private long player1TimeRemaining;
	private long player2TimeRemaining;
	
	
	public BattleStore() {
	}

	public List<BattleEntry> getBattleEntries(int playerNumber) {
		if (playerNumber == BattleSingleton.PLAYER1) {
			return battleEntriesPlayer1;
		} else {
			return battleEntriesPlayer2;
		}
	}

	public void setBattleEntries(List<BattleEntry> battleEntries, int playerNumber) {
		if (playerNumber == BattleSingleton.PLAYER1) {
			battleEntriesPlayer1 = battleEntries;
		} else {
			battleEntriesPlayer2 = battleEntries;
		}
	}
	
	public ArmyStore getArmy(int playerNumber) {
		if (playerNumber == BattleSingleton.PLAYER1) {
			return player1Army;
		} else {
			return player2Army;
		}
	}
		
	public void setArmy(ArmyStore army, int playerNumber) {
		if (playerNumber == BattleSingleton.PLAYER1) {
			player1Army = army;
		} else {
			player2Army = army;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getPlayer1TimeRemaining() {
		return player1TimeRemaining;
	}

	public void setPlayer1TimeRemaining(long player1TimeRemaining) {
		this.player1TimeRemaining = player1TimeRemaining;
	}

	public long getPlayer2TimeRemaining() {
		return player2TimeRemaining;
	}

	public void setPlayer2TimeRemaining(long player2TimeRemaining) {
		this.player2TimeRemaining = player2TimeRemaining;
	}	
	
	
	
}
