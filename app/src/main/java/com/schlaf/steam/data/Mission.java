package com.schlaf.steam.data;

public class Mission implements Comparable<Mission>{

	private String type;
	private String packet;
	private String number;
	private String name;
	private String victoryText;
	private String specialRulesText;
	private String tacticalTipsText;
	private int mapResourceId;
	private int objectiveResourceId;
	
	public Mission() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPacket() {
		return packet;
	}

	public void setPacket(String packet) {
		this.packet = packet;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVictoryText() {
		return victoryText;
	}

	public void setVictoryText(String victoryText) {
		this.victoryText = victoryText;
	}

	public String getSpecialRulesText() {
		return specialRulesText;
	}

	public void setSpecialRulesText(String specialRulesText) {
		this.specialRulesText = specialRulesText;
	}

	public String getTacticalTipsText() {
		return tacticalTipsText;
	}

	public void setTacticalTipsText(String tacticalTipsText) {
		this.tacticalTipsText = tacticalTipsText;
	}

	public int getMapResourceId() {
		return mapResourceId;
	}

	public void setMapResourceId(int mapResourceId) {
		this.mapResourceId = mapResourceId;
	}

	public int getObjectiveResourceId() {
		return objectiveResourceId;
	}

	public void setObjectiveResourceId(int objectiveResourceId) {
		this.objectiveResourceId = objectiveResourceId;
	}

	@Override
	public int compareTo(Mission another) {
		
		int sortYear = type.compareTo(another.getType());
		if (sortYear != 0) {
			return sortYear;
		}
		return Integer.parseInt(number) - Integer.parseInt(another.getNumber());
	}

}
