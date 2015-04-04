package com.schlaf.steam.activities.card;

import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.Restrictable;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.WarbeastPack;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.Warlock;

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

                    StringBuffer faString = new StringBuffer();
                    faString.append("FA:");
                    if (element.isUniqueCharacter()) {
                        faString.append("C");
                    } else if (element.isUnlimitedFA()) {
                        faString.append("U");
                    } else {
                        faString.append(element.getFA());
                    }

                    StringBuffer costString = new StringBuffer(12);
                    if (element instanceof Warcaster) {
                        costString.append("WJ:+").append(Math.abs(element.getBaseCost()));
                    } else if (element instanceof Warlock) {
                        costString.append("WB:+").append(Math.abs(element.getBaseCost()));
                    } else {
                        costString.append("PC:").append(element.getBaseCost());
                    }

                    String unitSize;
                    String cost = "0";
                    if (element instanceof Unit) {
                        if ( ((Unit) element).isVariableSize() ) {
                            Unit unit = (Unit) element;
                            unitSize =  unit.getBaseNumberOfModels() + "/" + unit.getFullNumberOfModels();
                            cost =  "PC:" + unit.getBaseCost() + "/" + unit.getFullCost();
                        } else {
                            unitSize = ((Unit) element).getBaseNumberOfModels() + "" ;
                            cost = "PC:"+ ((Unit) element).getBaseCost();
                        }
                    } else if (element instanceof WarbeastPack) {
                        unitSize= ((WarbeastPack) element).getNbModels() + "";
                    } else {
                        unitSize = "";
                    }


                    if (entry instanceof SelectionUnit) {
                        SelectionUnit unit = (SelectionUnit) entry;
                        map.get(entry.getType()).add(new CardLibraryRowData(entry.getId(), entry.getDisplayLabel(), entry.getQualification(), entry.getBaseFAString(), cost, unitSize, true));
                    } else if (entry instanceof SelectionSolo && ((SelectionSolo) entry).isDragoon()) {
                        cost = "PC:" + ((SelectionSolo) entry).getBaseCost() + "/" + ((SelectionSolo) entry).getDismountCost() ;
                        map.get(entry.getType()).add(new CardLibraryRowData(entry.getId(), entry.getDisplayLabel(), entry.getQualification(), entry.getBaseFAString(), cost, "1", false));
                    } else {
                        map.get(entry.getType()).add(new CardLibraryRowData(entry.getId(), entry.getDisplayLabel(), entry.getQualification(), entry.getBaseFAString(), costString.toString(), "1", false));
                    }


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
