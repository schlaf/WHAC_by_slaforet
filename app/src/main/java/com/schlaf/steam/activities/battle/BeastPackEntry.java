package com.schlaf.steam.activities.battle;

import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.WarbeastPack;

public class BeastPackEntry extends MultiPVUnit {

    public BeastPackEntry(SelectedEntry entry, ArmyElement armyEntry, BattleEntry parent, int entryCounter, int cost, boolean specialist) {
		super(armyEntry,entryCounter, cost, specialist);
		
		if (parent != null) {
			attached = true;
			this.parentId = parent.getUniqueId();
		} else {
			attached = false;
		}

		int nbModelsToCreate = ((WarbeastPack) armyEntry).getNbModels();
		// standard unit with grunts.. display count
		label = armyEntry.getFullName() + "[" + nbModelsToCreate + "]";	
		
		int nbModelsCreated = 0;
		
		// add leader
		SingleModel zeModel = armyEntry.getModels().get(0);
		SingleDamageLineEntry model; 
		model= new SingleDamageLineEntry(zeModel, 1, armyEntry, true, entryCounter, 0, false);
		model.setAttached(true);
		model.setParentId(entryCounter);
		models.add(model);
		nbModelsCreated++;

		// add grunts
		while (nbModelsCreated < nbModelsToCreate) {
			model = new SingleDamageLineEntry(zeModel, nbModelsCreated+1, armyEntry, false, entryCounter, 0, false);
			model.setAttached(true);
			model.setParentId(entryCounter);
			models.add(model);
			nbModelsCreated++;
		}
		
		for (SingleDamageLineEntry aModel : models) {
			grid.getDamageLines().add(aModel.getDamageGrid());	
		}		
	}

	public boolean isLeaderAndGrunts() {
		return true;
	}
	
}
