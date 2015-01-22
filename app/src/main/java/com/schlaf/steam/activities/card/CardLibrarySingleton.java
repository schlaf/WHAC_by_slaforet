package com.schlaf.steam.activities.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.Restrictable;

public class CardLibrarySingleton {

	private static  CardLibrarySingleton singleton = new CardLibrarySingleton();
	
	private Faction faction;
	private ModelTypeEnum entryType;
	
	HashMap<Faction, HashMap<ModelTypeEnum, List<CardLibraryRowData>>> entries = new HashMap<Faction, HashMap<ModelTypeEnum,List<CardLibraryRowData>>>();
	
	public static CardLibrarySingleton getInstance() {
		return singleton;
	}
	
	private CardLibrarySingleton() {
		
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
		refreshEntryList();
	}

	public ModelTypeEnum getEntryType() {
		return entryType;
	}

	public void setEntryType(ModelTypeEnum entryType) {
		this.entryType = entryType;
	}
	
	public List<CardLibraryRowData> getEntries() {
		if ( entries.get(faction) != null) {
			if (entries.get(faction).get(entryType) != null) {
				return entries.get(faction).get(entryType);
			}
		}
		return new ArrayList<CardLibraryRowData>();
	}
	
	
	private void refreshEntryList() {
		
		if (entries.get(faction) == null) {
			
			HashMap<ModelTypeEnum, List<CardLibraryRowData>> map = new HashMap<ModelTypeEnum, List<CardLibraryRowData>>();
			entries.put(faction, map);
			
			List<SelectionEntry> selectionEntries = faction.getAvailableModelsOfFaction();
			for (SelectionEntry entry : selectionEntries) {
				
				if (map.get(entry.getType()) == null) {
					List<CardLibraryRowData> entriesList = new ArrayList<CardLibraryRowData>();
					map.put(entry.getType(), entriesList);
				}
				
				
				boolean shouldAdd = true;
				ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
				if (element instanceof Restrictable) {
					if (! ((Restrictable)element).getTiersInWhichAllowedToAppear().isEmpty() ) {
						// this element is a clone, reserved only for certain tier
						shouldAdd = false;
					}
				}
				
				if (shouldAdd){
					map.get(entry.getType()).add(new CardLibraryRowData(entry.getId(), entry.getDisplayLabel(), entry.getQualification(), entry.isCompleted()));	
				}
				
			}
			
			for (List<CardLibraryRowData> list : map.values()) {
				Collections.sort(list);	
			}
			
		}
		
	}

	public List<ModelTypeEnum> getNonEmptyEntryType() {
		List<ModelTypeEnum> result = new ArrayList<ModelTypeEnum>();
		if (entries.get(faction) != null) {
			for (ModelTypeEnum type : entries.get(faction).keySet()) {
				if (! entries.get(faction).get(type).isEmpty() ) {
					result.add(type);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
}
