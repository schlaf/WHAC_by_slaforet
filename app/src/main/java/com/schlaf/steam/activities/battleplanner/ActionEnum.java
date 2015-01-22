package com.schlaf.steam.activities.battleplanner;

import com.schlaf.steam.R;

public enum ActionEnum {

	MOVE_AND_HIT (R.string._5, R.drawable.action_move_melee, 1),
	MOVE_AND_SHOOT(R.string._5, R.drawable.action_move_melee, 1),
	RUN(R.string._5, R.drawable.action_move_melee, 1),
	CHARGE(R.string._5, R.drawable.action_move_melee, 1), 
	SLAM(R.string._5, R.drawable.action_move_melee, 1),
	TRAMPLE(R.string._5, R.drawable.action_move_melee, 1),
	SPECIAL(R.string._5, R.drawable.action_move_melee, 2),
	FEAT(R.string._5, R.drawable.action_move_melee, 3),
	SPELLS(R.string._5, R.drawable.action_move_melee, 4);
	
	int ressourceNameId; 
	int resourceImgId;
	int exclusiveGroup;
	
	private ActionEnum(int ressourceNameId, int resourceImgId, int exclusiveGroup) {
		this.ressourceNameId = ressourceNameId;
		this.resourceImgId = resourceImgId;
		this.exclusiveGroup = exclusiveGroup;
	}
}
