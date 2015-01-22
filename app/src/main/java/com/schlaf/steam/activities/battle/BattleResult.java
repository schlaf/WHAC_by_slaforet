package com.schlaf.steam.activities.battle;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.schlaf.steam.storage.ArmyStore;

public class BattleResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3851142608910405758L;
	
	public static final int PLAYER_1_WINS = 0;
	public static final int PLAYER_2_WINS = 1;
	
	private String filename; // filename for storage
	private String armyName; // name of the primary army list (=name of caster)
	
	ArmyStore army1;
	ArmyStore army2;
	
	private Date battleDate;
	
	int winnerNumber; // 0 = me, 1 = other guy
	String player2name;
	String clockType;
	String scenario;
	String victoryCondition;
	String notes;
	
	public static transient BattleResultDateComparator dateComparator;
	public static transient BattleResultPlayerComparator player2Comparator;
	public static transient BattleResultVictoryComparator victoryComparator;

	
	static {
		dateComparator = new BattleResultDateComparator();
		player2Comparator = new BattleResultPlayerComparator();
		victoryComparator = new BattleResultVictoryComparator();
	}
	
	public String getDescription() {
		StringBuffer desc = new StringBuffer();
		desc.append( army1.getNbPoints()).append(" points - [");
		boolean first = true;
		for (String commander : army1.getCommanders()) {
			if (!first) { desc.append(" - "); }
			desc.append(commander);
			first = false;
		}
		desc.append("]");
		
		
		if (army2 != null) {
			first = true;
			desc.append(" VS. [");
			for (String commander : army2.getCommanders()) {
				if (!first) { desc.append(" - "); }
				desc.append(commander);
			}
			desc.append("]");
		}
		
		desc.append(" on [").append(scenario).append("]");
		
		return desc.toString();
	}
	
	
	public ArmyStore getArmy1() {
		return army1;
	}
	public void setArmy1(ArmyStore army1) {
		this.army1 = army1;
	}
	public ArmyStore getArmy2() {
		return army2;
	}
	public void setArmy2(ArmyStore army2) {
		this.army2 = army2;
	}
	public Date getBattleDate() {
		return battleDate;
	}
	public void setBattleDate(Date battleDate) {
		this.battleDate = battleDate;
	}
	public int getWinnerNumber() {
		return winnerNumber;
	}
	public void setWinnerNumber(int winnerNumber) {
		this.winnerNumber = winnerNumber;
	}
	public String getPlayer2name() {
		return player2name;
	}
	public void setPlayer2name(String player2name) {
		this.player2name = player2name;
	}
	public String getClockType() {
		return clockType;
	}
	public void setClockType(String clockType) {
		this.clockType = clockType;
	}
	public String getScenario() {
		return scenario;
	}
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	public String getVictoryCondition() {
		return victoryCondition;
	}
	public void setVictoryCondition(String victoryCondition) {
		this.victoryCondition = victoryCondition;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public final Comparator<BattleResult> getDateComparator() {
		return dateComparator;
	}
	
	
	public static class BattleResultDateComparator implements Comparator<BattleResult> {
		
		private boolean ascending = true;
		@Override
		public int compare(BattleResult lhs, BattleResult rhs) {
			return (ascending?1:-1) * lhs.getBattleDate().compareTo(rhs.getBattleDate());
		}
		
		public void reverseOrder() {
			ascending = !ascending;
		}
	}
	
	public static class BattleResultPlayerComparator implements Comparator<BattleResult> {
		
		private boolean ascending = true;
		
		@Override
		public int compare(BattleResult lhs, BattleResult rhs) {
			return (ascending?1:-1) * lhs.getPlayer2name().toUpperCase(Locale.getDefault()).compareTo(rhs.getPlayer2name().toUpperCase(Locale.getDefault()));
		}
		
		public void reverseOrder() {
			ascending = !ascending;
		}
	}

	public static class BattleResultVictoryComparator implements Comparator<BattleResult> {
		
		private boolean ascending = true;
		@Override
		public int compare(BattleResult lhs, BattleResult rhs) {
			return (ascending?1:-1) *lhs.getWinnerNumber() - rhs.getWinnerNumber();
		}
		
		public void reverseOrder() {
			ascending = !ascending;
		}
	}

	

	public BattleResultPlayerComparator getPlayer2Comparator() {
		return player2Comparator;
	}


	public BattleResultVictoryComparator getVictoryComparator() {
		return victoryComparator;
	}


	public String getArmyName() {
		return armyName;
	}


	public void setArmyName(String armyName) {
		this.armyName = armyName;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}

	
	
}
