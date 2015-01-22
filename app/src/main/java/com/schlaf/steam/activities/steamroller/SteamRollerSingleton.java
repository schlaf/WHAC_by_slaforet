package com.schlaf.steam.activities.steamroller;

import java.util.ArrayList;

import com.schlaf.steam.data.Mission;

public class SteamRollerSingleton {

	private static SteamRollerSingleton instance;
	
	private ArrayList<Mission> scenarii = new ArrayList<Mission>();
	Mission currentMission;
	
	public static final SteamRollerSingleton getInstance() {
		if (instance == null) {
			instance = new SteamRollerSingleton();
		}
		return instance;
	}
	
	public void setCurrentMission(Mission mission) {
		currentMission = mission;
	}
	
	public Mission getCurrentMission() {
		return currentMission;
	}
	
//	public Mission getMission(String title) {
//		return scenarii.get(title);
//	}
	
	public ArrayList<Mission> getScenarii() {
		return scenarii;
	}
	
}
