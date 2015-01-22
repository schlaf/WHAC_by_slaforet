package com.schlaf.steam.activities.battleresult;

import com.schlaf.steam.activities.battle.BattleResult;

public class BattleResultSingleton {

	private static BattleResultSingleton instance;
	
	private BattleResult currentBattleResult;
	
	private BattleResultSingleton() {
		
	}
	
	public static final BattleResultSingleton getInstance() {
		if (instance == null) {
			instance = new BattleResultSingleton();
		}
		
		return instance;
	}

	public BattleResult getCurrentBattleResult() {
		return currentBattleResult;
	}

	public void setCurrentBattleResult(BattleResult currentBattleResult) {
		this.currentBattleResult = currentBattleResult;
	}
	
}
