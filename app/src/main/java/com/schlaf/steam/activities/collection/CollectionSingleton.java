package com.schlaf.steam.activities.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.ModelTypeEnum;

public class CollectionSingleton {

	private static  CollectionSingleton singleton = new CollectionSingleton();
	
	private Faction faction;
	private ModelTypeEnum entryType;
	
	HashMap<Faction, HashMap<ModelTypeEnum, List<CollectionEntry>>> entries = new HashMap<Faction, HashMap<ModelTypeEnum,List<CollectionEntry>>>();
	
	HashMap<String, Integer> ownedMap = new HashMap<String, Integer>(300);
	HashMap<String, Integer> paintedMap = new HashMap<String, Integer>(300);
	
	
	public static CollectionSingleton getInstance() {
		return singleton;
	}
	
	private CollectionSingleton() {
		
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
	
	public List<CollectionEntry> getEntries() {
		if ( entries.get(faction) != null) {
			if (entries.get(faction).get(entryType) != null) {
				return entries.get(faction).get(entryType);
			}
		}
		return new ArrayList<CollectionEntry>();
	}
	
	
	private void refreshEntryList() {
		
		if (entries.get(faction) == null) {
			
			HashMap<ModelTypeEnum, List<CollectionEntry>> map = new HashMap<ModelTypeEnum, List<CollectionEntry>>();
			entries.put(faction, map);
			
			List<SelectionEntry> selectionEntries = faction.getAvailableModelsOfFaction();
			for (SelectionEntry entry : selectionEntries) {
				
				if (map.get(entry.getType()) == null) {
					List<CollectionEntry> entriesList = new ArrayList<CollectionEntry>();
					map.put(entry.getType(), entriesList);
				}
				map.get(entry.getType()).add(new CollectionEntry(entry.getId(), entry.getFullLabel()));
			}
			
			for (List<CollectionEntry> list : map.values()) {
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

	public HashMap<String, Integer> getOwnedMap() {
		return ownedMap;
	}

	public void setOwnedMap(HashMap<String, Integer> ownedMap) {
		this.ownedMap = ownedMap;
	}

	public HashMap<String, Integer> getPaintedMap() {
		return paintedMap;
	}

	public void setPaintedMap(HashMap<String, Integer> paintedMap) {
		this.paintedMap = paintedMap;
	}
	
}
