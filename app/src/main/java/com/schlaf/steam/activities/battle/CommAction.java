package com.schlaf.steam.activities.battle;

public enum CommAction {
	INIT_BLUETOOTH, // notify that the bluetooth comm channel is online
	HELLO, // first message
	RESPONSE_OK, // this is an acknoledge message
	START_ARMY_LIST, // will next send army entries
	SEND_ARMY_STORE, // send the army store (full)
	ADD_ENTRY, // send ONE battle entry
	END_ARMY_LIST, // we have finished sending the army
	SEND_ME_YOUR_LIST, // ask opponent to send list
	
	
	// those events are triggered by UI, not the basic exchange at startup
	MODIFY_DAMAGE_GRID,
	PLAYER1_PLAY,
	PLAYER2_PLAY,
	CHRONO_PAUSE,
	BLUETOOTH_STATUS_MESSAGE
	
}
