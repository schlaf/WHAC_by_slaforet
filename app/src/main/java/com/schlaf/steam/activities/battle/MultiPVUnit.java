package com.schlaf.steam.activities.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedUA;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.selectlist.selected.SelectedWA;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.MultiPVUnitGrid;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.UnitAttachment;
import com.schlaf.steam.data.WeaponAttachment;

public class MultiPVUnit extends MultiPVModel {

	/** serial */
	private static final long serialVersionUID = -1821597472654739377L;
	
	List<SingleDamageLineEntry> models = new ArrayList<SingleDamageLineEntry>();
	
	MultiPVUnitGrid grid = new MultiPVUnitGrid();
	
	/** indicates that this unit is just one model : one stat line for everybody */
	private boolean leaderAndGrunts;
	
	public MultiPVUnit(ArmyElement entry, int entryCounter) {
		super(entry, entryCounter);
	}
	
	public MultiPVUnit(SelectedUA entry, UnitAttachment ua, int entryCounter) {
		super(ua, entryCounter);
		// add every single model
		Iterator<SingleModel> iterator = ua.getModels().iterator();
		while (iterator.hasNext()) {
			SingleDamageLineEntry model = new SingleDamageLineEntry(iterator.next(), ua, entryCounter);
			model.setAttached(true);
			model.setParentId(entryCounter);
			models.add(model);
		}
		// override unit label to display model count
		label = ua.getFullName(); // + "[" + ua.getModels().size() + "]";

	}
	
	public MultiPVUnit(SelectedWA entry, WeaponAttachment wa, int waCount, int entryCounter) {
		super(wa, entryCounter);
		int addedModels = 0;
		// add every single model
		Iterator<SingleModel> iterator = wa.getModels().iterator();
		while (iterator.hasNext()) {
			SingleDamageLineEntry model = new SingleDamageLineEntry(iterator.next(), wa, entryCounter);
			model.setAttached(true);
			model.setParentId(entryCounter);
			models.add(model);
			addedModels++;
		}
		
		if (addedModels < waCount) {
			// complete with last model
			while (addedModels < waCount) {
				//SingleDamageLineEntry model = new SingleDamageLineEntry(unit.getModels().get(unit.getModels().size() - 1), nbModelsCreated+1, unit, false);
				SingleDamageLineEntry model = new SingleDamageLineEntry(wa.getModels().get(wa.getModels().size() - 1), addedModels+1 , wa, entryCounter);
				model.setAttached(true);
				model.setParentId(entryCounter);
				models.add(model);
				addedModels++;
			}
		}
		
		// override unit label to display model count
		label = wa.getFullName() + "[" + waCount + "]";

	}

	/**
	 * create a "unit" from any entry which is not a unit (ex : dragoon, caster + attachment auto-included, ...)
	 * @param entry
	 * @param armyEntry
	 */
	public MultiPVUnit(SelectedEntry entry, ArmyElement armyEntry, int entryCounter) {
		super(armyEntry,entryCounter);
		
		// override unit label to display model count
		label = armyEntry.getFullName();
		
		// add every single model with a description
		Iterator<SingleModel> iterator = armyEntry.getModels().iterator();
		while (iterator.hasNext()) {
			SingleDamageLineEntry model = new SingleDamageLineEntry(iterator.next(), armyEntry, entryCounter);	
			model.setAttached(true);
			model.setParentId(entryCounter);
			models.add(model);
		}
		
		for (SingleDamageLineEntry model : models) {
			grid.getDamageLines().add(model.getDamageGrid());	
		}
		
	}	
	
	public MultiPVUnit(SelectedUnit entry, Unit unit, int entryCounter) {
		super(unit, entryCounter);
		
		int nbModelsToCreate = 0;
		if (unit.isVariableSize()) {
			if (entry.isMinSize()) {
				nbModelsToCreate = unit.getBaseNumberOfModels();
			} else {
				nbModelsToCreate = unit.getFullNumberOfModels();
			}
		} else {
			nbModelsToCreate = unit.getBaseNumberOfModels();
		}
		
		// override unit label to display model count
		if (unit.getModels().size() == nbModelsToCreate) {
			// probably character unit, so 1 line per model, no need to display count
			label = unit.getFullName(); 
		} else {
			// standard unit with grunts.. display count
			label = unit.getFullName() + "[" + nbModelsToCreate + "]";	
		}
		
		
		int nbModelsCreated = 0;
		
		leaderAndGrunts = false;
		if (unit.getModels().size() == 1) {
			// units follows the "Leader & grunts" model
			leaderAndGrunts = true;
		}
		
		// add every single model with a description
		Iterator<SingleModel> iterator = unit.getModels().iterator();
		while (iterator.hasNext()) {
			SingleDamageLineEntry model; 
			if (leaderAndGrunts) {
				model= new SingleDamageLineEntry(iterator.next(), 1, unit, true, entryCounter);	
			} else {
				model= new SingleDamageLineEntry(iterator.next(), unit, entryCounter);
			}
			model.setAttached(true);
			model.setParentId(entryCounter);
			models.add(model);
			nbModelsCreated++;
		}
		
		// complete with copies of lastmodel only if hitpoints > 1
		if (unit.getModels().get(unit.getModels().size() - 1).getHitpoints().getTotalHits() > 1 ) {
			while (nbModelsCreated < nbModelsToCreate) {
				SingleDamageLineEntry model;
				if (leaderAndGrunts) {
					model = new SingleDamageLineEntry(unit.getModels().get(unit.getModels().size() - 1), nbModelsCreated+1, unit, false, entryCounter);	
				} else {
					model = new SingleDamageLineEntry(unit.getModels().get(unit.getModels().size() - 1), nbModelsCreated+1, unit, entryCounter);
				}
				model.setAttached(true);
				model.setParentId(entryCounter);
				models.add(model);
				nbModelsCreated++;
			}
		}
		
		
		for (SingleDamageLineEntry model : models) {
			grid.getDamageLines().add(model.getDamageGrid());	
		}
		
	}

	public int getModelCount() {
		return models.size();
	}

	public List<SingleDamageLineEntry> getModels() {
		return models;
	}

	@Override
	public DamageGrid getDamageGrid() {
		return grid;
	}

	public boolean isLeaderAndGrunts() {
		return leaderAndGrunts;
	}
	
	public void setLeaderAndGrunts(boolean leaderAndGrunts) {
		this.leaderAndGrunts = leaderAndGrunts;
	}
	
}
