/**
 * 
 */
package com.schlaf.steam.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;
import com.schlaf.steam.activities.selectlist.selection.SelectionUA;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;
import com.schlaf.steam.activities.selectlist.selection.SelectionWA;



/**
 * @author S0085289
 *
 */
public class Faction implements Serializable, Comparable<Faction> {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 3999192191921985215L;


	public enum GameSystem {
		HORDES,
		WARMACHINE,
		NONE;
	}
	
	public enum Regularity {
		REGULAR,
		MERCENARY,
		MINION;
	}
	
	private String id;
	FactionNamesEnum enumValue;
	private String name;
	private String fullName;


    private String version;
	private GameSystem system;
	private Regularity regularity;
	
	
	private HashMap<String, Unit> units;
	private HashMap<String, Warcaster> casters;
	private HashMap<String, Warlock> warlocks;
	private HashMap<String, Warjack> jacks;
	private HashMap<String, Warbeast> beasts;
	private HashMap<String, BattleEngine> battleEngines;
	private HashMap<String, Solo> solos;
	
	public FactionNamesEnum getEnumValue() {
		return enumValue;
	}
	
	
	public Faction() {
		units = new HashMap<String, Unit>();
		casters = new HashMap<String, Warcaster>();
		warlocks = new HashMap<String, Warlock>();
		jacks = new HashMap<String, Warjack>();
		beasts = new HashMap<String, Warbeast>();
		battleEngines = new HashMap<String, BattleEngine>();
		solos = new HashMap<String, Solo>();
	}
	
	public String toString() {
		return fullName;
	}
	
	
	public List<SelectionEntry> getAvailableModelsOfFaction() {
		return getAvailableModels(false, false);
	}
	
	public List<SelectionEntry> getAvailableModels() {
		return getAvailableModels(true, true);
	}
	
	public List<SelectionEntry> getAvailableModels(boolean includeMercs, boolean includeObjectives) {
		
		List<SelectionEntry> models = new ArrayList<SelectionEntry>(units.size() + casters.size() + warlocks.size() + jacks.size() + beasts.size() + battleEngines.size() + solos.size() + 15);
		for (Unit unit : units.values()) {
			
			SelectionUnit model = new SelectionUnit(unit);
			if (unit.isJackMarshall()) {
				model.setJackMarshall(true);
			} 
			if (unit.isJackMarshallViaUA()) {
				model.setJackMarshallViaUA(true);
			}
			
			models.add(model);
			
			if (unit.getUnitAttachment() != null) {
				SelectionUA ua = new SelectionUA(unit.getUnitAttachment(), unit);
				models.add(ua);
			}
			
			if (unit.getWeaponAttachment() != null) {
				SelectionWA wa = new SelectionWA(unit.getWeaponAttachment(), unit);
				models.add(wa);
			}
			
		}
		for (Warcaster caster : casters.values()) {
			SelectionEntry model = new SelectionEntry(caster);
			models.add(model);
		}
		for (Warlock warlock : warlocks.values()) {
			SelectionEntry model = new SelectionEntry(warlock);
			models.add(model);
		}
		for (Warjack jack : jacks.values()) {
			SelectionEntry model = new SelectionEntry(jack);
			models.add(model);
		}
		for (Warbeast beast : beasts.values()) {
			SelectionEntry model = new SelectionEntry(beast);
			models.add(model);
		}
		for (BattleEngine be : battleEngines.values()) {
			SelectionEntry model = new SelectionEntry(be);
			models.add(model);
		}
		for (Solo solo : solos.values()) {
			SelectionEntry model = new SelectionSolo(solo);
			models.add(model);
		}
		
		if (includeMercs) {
			if (! FactionNamesEnum.MERCENARIES.getId().equals(id) && ! FactionNamesEnum.MINIONS.getId().equals(id)) {
				models.addAll(getAvailableMercsMinionsModels());
			} else {
				for (SelectionEntry model : models) {
					model.setMercenaryOrMinion(true);
				}
			}
		}
		
		if (includeObjectives) {
			models.addAll(getAvailableObjectives());
		}
		
		return models;
	}

	/**
	 * retrieve objective models allowed to work for this faction
	 * @return
	 */
	private List<SelectionEntry> getAvailableObjectives() {
		
		Faction objs = ArmySingleton.getInstance().getFactions().get(FactionNamesEnum.OBJECTIVES_SR2016.getId());
		
		List<SelectionEntry> models = new ArrayList<SelectionEntry>(objs.getSolos().size());
		for (Solo solo : objs.getSolos().values()) {
			SelectionEntry model = new SelectionSolo(solo);
			model.setObjective(true);
			models.add(model);
		}
		
		return models;
	}

	
	/**
	 * retrieve mercs/minions models allowed to work for this faction
	 * @return
	 */
	private List<SelectionEntry> getAvailableMercsMinionsModels() {

		List<SelectionEntry> result = new ArrayList<SelectionEntry>(50);
		Faction mercs = ArmySingleton.getInstance().getFactions().get(FactionNamesEnum.MERCENARIES.getId());
		Faction minions = ArmySingleton.getInstance().getFactions().get(FactionNamesEnum.MINIONS.getId());
		List<SelectionEntry> candidates = new ArrayList<SelectionEntry>();
		candidates.addAll(mercs.getAvailableModels());
		candidates.addAll(minions.getAvailableModels());
		for (SelectionEntry model :candidates) {
			ArmyElement element = ArmySingleton.getInstance().getArmyElement(model.getId());
			if (element.getAllowedFactionsToWorkFor().contains(id)) {
				model.setMercenaryOrMinion(true);
				result.add(model);
			}
			if (element instanceof Warjack || element instanceof Warbeast) {
				// mercenary jacks and beasts do not depend on faction choice, but on the caster/warlock ...
				model.setMercenaryOrMinion(true);
				result.add(model);
			}
		}
		return result;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		enumValue = FactionNamesEnum.getFaction(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public GameSystem getSystem() {
		return system;
	}
	public void setSystem(GameSystem system) {
		this.system = system;
	}
	public Regularity getRegularity() {
		return regularity;
	}
	public void setRegularity(Regularity regularity) {
		this.regularity = regularity;
	}
	public HashMap<String, Unit> getUnits() {
		return units;
	}
	public void setUnits(HashMap<String, Unit> units) {
		this.units = units;
	}
	public HashMap<String, Warcaster> getCasters() {
		return casters;
	}
	
	/**
	 * comparaison des noms de base "id" en ordre croissant
	 */
	@Override
	public int compareTo(Faction another) {
		return FactionNamesEnum.getFaction(this.getId()).compareTo(FactionNamesEnum.getFaction(another.getId())); 
	}


	public HashMap<String, Warjack> getJacks() {
		return jacks;
	}


	public HashMap<String, Solo> getSolos() {
		return solos;
	}


	public HashMap<String, BattleEngine> getBattleEngines() {
		return battleEngines;
	}


	public HashMap<String, Warlock> getWarlocks() {
		return warlocks;
	}


	public HashMap<String, Warbeast> getBeasts() {
		return beasts;
	}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
