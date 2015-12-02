package com.schlaf.steam.storage;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.compatibility.JSONArray;
import org.json.compatibility.JSONObject;
import org.json.compatibility.JSONTokener;
import org.json.compatibility.JSONWriter;

import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.battle.BeastEntry;
import com.schlaf.steam.activities.battle.JackEntry;
import com.schlaf.steam.activities.battle.KarchevEntry;
import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.activities.battle.MultiPVModel;
import com.schlaf.steam.activities.battle.MultiPVUnit;
import com.schlaf.steam.activities.battle.SingleDamageLineEntry;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.activities.selectlist.selected.BeastCommander;
import com.schlaf.steam.activities.selectlist.selected.JackCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedArmyCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedBattleEngine;
import com.schlaf.steam.activities.selectlist.selected.SelectedCasterAttachment;
import com.schlaf.steam.activities.selectlist.selected.SelectedDragoon;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedJourneyManWarcaster;
import com.schlaf.steam.activities.selectlist.selected.SelectedLesserWarlock;
import com.schlaf.steam.activities.selectlist.selected.SelectedModel;
import com.schlaf.steam.activities.selectlist.selected.SelectedObjective;
import com.schlaf.steam.activities.selectlist.selected.SelectedRankingOfficer;
import com.schlaf.steam.activities.selectlist.selected.SelectedSolo;
import com.schlaf.steam.activities.selectlist.selected.SelectedSoloMarshal;
import com.schlaf.steam.activities.selectlist.selected.SelectedUA;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnitMarshall;
import com.schlaf.steam.activities.selectlist.selected.SelectedWA;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarbeast;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarbeastPack;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarcaster;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarjack;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarlock;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.AvailableModels;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.Contract;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.MultiPVUnitGrid;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.data.TierCostAlteration;
import com.schlaf.steam.data.TierEntry;
import com.schlaf.steam.data.TierEntryGroup;
import com.schlaf.steam.data.TierFAAlteration;
import com.schlaf.steam.data.TierFACostBenefit;
import com.schlaf.steam.data.TierFreeModel;
import com.schlaf.steam.data.TierLevel;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.UnitAttachment;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.Warlock;
import com.schlaf.steam.data.WeaponAttachment;

public class JsonConverter {

	private static final String PLAYER2_TIME = "player2Time";
	private static final String PLAYER1_TIME = "player1Time";
	private static final String MODELS = "MODELS";
	private static final String MODEL_NUMBER = "MODEL_NUMBER";
	private static final String LEADER_AND_GRUNTS = "leader_and_grunts";
	private static final String MULTIPV_UNIT = "MULTIPV_UNIT";
	private static final String KARCHEV = "KARCHEV";
	private static final String JACK = "JACK";
	private static final String BEAST = "BEAST";
	private static final String SINGLE_LINE = "SINGLE_LINE";
	private static final String MONO_PV = "MONO_PV";
	private static final String DESC = "DESC";
	private static final String DAMAGES = "DAMAGES";
	private static final String PARENT_ID = "PARENT_ID";
	private static final String BATTLE_ID = "BATTLE_ID";
	private static final String ARM = "ARM";
	private static final String DEF = "DEF";
	private static final String NAME = "NAME";
	private static final String ENTRIES1 = "ENTRIES1";
	private static final String ENTRIES2 = "ENTRIES2";
	private static final String ARMY2 = "ARMY2";
	private static final String ARMY1 = "ARMY1";
	private static final String ATTACHMENT = "attachment";
	private static final String UNIT_WITH_MARSHAL = "unitWithMarshal";
	private static final String ATTACHMENTS = "attachments";
	private static final String MAX_SIZE = "maxSize";
	private static final String MIN_SIZE = "minSize";
	private static final String UNIT = "unit";
	private static final String BATTLEENGINE = "battleengine";
	private static final String UNIT_ATTACHMENT = "unitAttachment";
	private static final String WEAPON_ATTACHMENT = "weaponAttachment";
	private static final String SOLO_DRAGOON = "soloDragoon";
    private static final String SOLO_DRAGOON_DISMOUNT_OPTION = "soloDragoonDismountOption";
	private static final String SOLO_OBJECTIVE = "soloObjective";
	private static final String SOLO_WITH_MARSHAL = "soloWithMarshal";
	private static final String RANKING_OFFICER = "rankingOfficer";
	private static final String CASTER_ATTACHMENT = "casterAttachment";
	private static final String WARBEAST = "warbeast";
	private static final String WARBEAST_PACK = "warbeastPack";
	private static final String WARJACK = "warjack";
	private static final String WARBEASTS = "warbeasts";
	private static final String LESSER_WARLOCK = "lesserWarlock";
	private static final String WARLOCK = "warlock";
	private static final String WARJACKS = "warjacks";
	private static final String JOURNEYMAN_WARCASTER = "journeymanWarcaster";
	private static final String WARCASTER = "warcaster";
	private static final String SOLO = "solo";
	private static final String TYPE = "type";
	private static final String ALTERED_BY_TIER_OR_CONTRACT = "alteredByTierOrContract";
	private static final String FREE = "free";
	private static final String COST = "cost";
	private static final String REALCOST = "realcost";
	private static final String LABEL = "label";
	private static final String ID = "id";
	private static final String ENTRIES = "entries";
	private static final String NB_POINTS = "nbPoints";
	private static final String NB_CASTERS = "nbCasters";
	private static final String FACTION_ID = "factionId";
	private static final String ARMY = "army";
	private static final String TIER_ID = "tierId";
	private static final String CONTRACT_ID = "contractId";
	private static final String MARSHAL_VIA_UA = "marshal_via_ua";
    private static final String SPECIALIST = "specialist";

	public static void createArmyObject(Writer writer, ArmyStore store) {
		JSONWriter json = new JSONWriter(writer);
		json.object()
			.key(ARMY)
				.value(store.getFilename())
			.key(FACTION_ID)
				.value(store.getFactionId())
			.key(NB_CASTERS)
				.value(store.getNbCasters())
			.key(NB_POINTS)
				.value(store.getNbPoints());
		if (store.getTierId() != null) {
			json.key(TIER_ID)
			.value(store.getTierId());
		}
		if (store.getContractId() != null) {
			json.key(CONTRACT_ID)
			.value(store.getContractId());
		}
		json.key(ENTRIES)
				.array();
			

		for (SelectedEntry entry : store.getEntries()) {
			JSONObject model = writeModel(entry);
			json.value(model);
		}

		json.endArray().endObject();

	}
	
	public static ArmyStore readArmyObject(JSONTokener tokener) {
		ArmyStore result = new ArmyStore("");
		JSONObject source = new JSONObject(tokener);
		
		completeArmyStore(result, source);
		
		return result;
		
	}

	private static void completeArmyStore(ArmyStore result, JSONObject source) {
		result.setFilename(source.getString(ARMY));
		result.setFactionId(source.getString(FACTION_ID));
		result.setNbCasters(source.getInt(NB_CASTERS));
		result.setNbPoints(source.getInt(NB_POINTS));
		
		if (source.has(TIER_ID)) {
			result.setTierId(source.getString(TIER_ID));
		}
		if (source.has(CONTRACT_ID)) {
			result.setContractId(source.getString(CONTRACT_ID));
		}
		
		
		ArrayList<SelectedEntry> selectedEntries = new ArrayList<SelectedEntry>(); 
		JSONArray entries = source.getJSONArray(ENTRIES);
		for (int i = 0; i < entries.length(); i++) {
			JSONObject entry = entries.getJSONObject(i);
			SelectedEntry selected = readEntryObject(entry);
			
			selectedEntries.add(selected);
		}
		
		result.setEntries(selectedEntries);
	}
	
	private static SelectedEntry readEntryObject(JSONObject entry) {
		String type = entry.getString(TYPE);
		String id = entry.getString(ID);
		String label = entry.getString(LABEL);
		
		int realCost = entry.optInt(REALCOST);
		int cost = entry.optInt(COST);
		boolean free = entry.optBoolean(FREE);
		boolean tierAltered = entry.optBoolean(ALTERED_BY_TIER_OR_CONTRACT);
        boolean specialist = entry.optBoolean(SPECIALIST);

		
		
		SelectedEntry result = null;
		
		if (WARCASTER.equals(type)) {
			result = new SelectedWarcaster(id, label);
		}
		if (WARLOCK.equals(type)) {
			result = new SelectedWarlock(id, label);
		}
		if (BATTLEENGINE.equals(type)) {
			result = new SelectedBattleEngine(id, label);
		}
		if (WARJACK.equals(type)) {
			result = new SelectedWarjack(id, label);
		}

		if (WARBEAST.equals(type)) {
			result = new SelectedWarbeast(id, label);
		}
		
		if (WARBEAST_PACK.equals(type)) {
			result = new SelectedWarbeastPack(id, label);
		}
		
		if (SOLO.equals(type)) {
			result = new SelectedSolo(id, label);
		}
		if (CASTER_ATTACHMENT.equals(type)) {
			result = new SelectedCasterAttachment(id, label);
		}
		if (SOLO_DRAGOON.equals(type)) {
            boolean withDismount = entry.optBoolean(SOLO_DRAGOON_DISMOUNT_OPTION, true);
			result = new SelectedDragoon(id, label, withDismount);
		}
		if (SOLO_OBJECTIVE.equals(type)) {
			result = new SelectedObjective(id, label);
		}
		if (SOLO_WITH_MARSHAL.equals(type)) {
			result = new SelectedSoloMarshal(id, label);
		}
		if (JOURNEYMAN_WARCASTER.equals(type)) {
			result = new SelectedJourneyManWarcaster(id, label);
		}
		if (LESSER_WARLOCK.equals(type)) {
			result = new SelectedLesserWarlock(id, label);
		}
		
		if (UNIT.equals(type)) {
			if (entry.has(MIN_SIZE)) {
				result = new SelectedUnit(id, label, true);	
			} else {
				result = new SelectedUnit(id, label, false);
			}
		}
		if (UNIT_WITH_MARSHAL.equals(type)) {
			if (entry.has(MIN_SIZE)) {
				result = new SelectedUnitMarshall(id, label, true, false);	
			} else {
				result = new SelectedUnitMarshall(id, label, false, false);
			}
		}
		if (RANKING_OFFICER.equals(type)) {
			result = new SelectedRankingOfficer(id, label);
		}
		if (WEAPON_ATTACHMENT.equals(type)) {
			result = new SelectedWA(id, label);
		}
		if (UNIT_ATTACHMENT.equals(type)) {
			result = new SelectedUA(id, label);
		}
		
		// read attachment (for caster)
		if (entry.optJSONObject(ATTACHMENT) != null) {
			((SelectedArmyCommander) result).setAttachment( (SelectedModel) readEntryObject(entry.getJSONObject(ATTACHMENT))); 
		}
		
		// read attachments (for units)
		if (entry.optJSONArray(ATTACHMENTS) != null) {
			SelectedUnit unit = (SelectedUnit) result;
			
			JSONArray attachments = entry.getJSONArray(ATTACHMENTS);
			for (int i = 0; i < attachments.length(); i++) {
				
				SelectedEntry attach = readEntryObject( (JSONObject) attachments.get(i));
				
				if (attach instanceof SelectedRankingOfficer) {
					unit.setRankingOfficer( (SelectedRankingOfficer) attach);
				}
				
				if (attach instanceof SelectedUA) {
					unit.setUnitAttachment( (SelectedUA) attach);
				}
				
				if (attach instanceof SelectedWA) {
					unit.getWeaponAttachments().add( (SelectedWA) attach);
				}
				
				// note : warjacks are if unit is marshall ; but do NOT add them cause they are already added because the unit is jack'marshal
				if (attach instanceof SelectedWarjack) {
					// ((SelectedUnitMarshall) unit).getJacks().add( (SelectedWarjack) attach);
					// do nothing
				}
				
			}
			 
		}
		
		
		// read warjacks.. 
		if (entry.optJSONArray(WARJACKS) != null) {
			JSONArray jacks = entry.getJSONArray(WARJACKS);
			for (int i = 0; i < jacks.length(); i++) {
				JSONObject jack = jacks.getJSONObject(i);
				SelectedEntry selected = readEntryObject(jack);
				((JackCommander) result).getJacks().add( (SelectedWarjack) selected);	
			}
		}
		
		// read warbeasts
		if (entry.optJSONArray(WARBEASTS) != null) {
			JSONArray beasts = entry.getJSONArray(WARBEASTS);
			for (int i = 0; i < beasts.length(); i++) {
				JSONObject jack = beasts.getJSONObject(i);
				SelectedEntry selected = readEntryObject(jack);
				((BeastCommander) result).getBeasts().add( (SelectedWarbeast) selected);	
			}
		}
		
		result.setRealCost(realCost);
		result.setFreeModel(free);
		result.setTiersAltered(tierAltered);
        result.setSpecialist(specialist);
		
		return result;
		
	}

	private static JSONObject writeModel(SelectedEntry entry) {
		JSONObject result = new JSONObject();

		result.put(ID, entry.getId());
		result.put(LABEL, entry.getLabel());
		if (entry.getRealCost() != entry.getCost()) {
			result.put(REALCOST, entry.getRealCost());
		}
		result.put(COST, entry.getCost());
		if (entry.isFreeModel()) {
			result.put(FREE, true);
		}
		if (entry.isTiersAltered()) {
			result.put(REALCOST, entry.getRealCost());
			result.put(ALTERED_BY_TIER_OR_CONTRACT, true);
		}
        if (entry.isSpecialist()) {
            result.put(SPECIALIST, true);
        }

		if (entry instanceof SelectedSolo) { // put this first so it can be
												// overriden
			result.put(TYPE, SOLO);
		}

		if (entry instanceof SelectedWarcaster) {
			result.put(TYPE, WARCASTER);
			// append warjacks
		}
		if (entry instanceof SelectedJourneyManWarcaster) {
			result.put(TYPE, JOURNEYMAN_WARCASTER);
		}
		if (entry instanceof JackCommander) {
			// append warjacks
			for (SelectedWarjack jack : ((JackCommander) entry).getJacks()) {
				result.append(WARJACKS, writeModel(jack));
			}
		}

		if (entry instanceof SelectedWarlock) {
			result.put(TYPE, WARLOCK);
		}

		if (entry instanceof SelectedLesserWarlock) {
			result.put(TYPE, LESSER_WARLOCK);
		}

		if (entry instanceof BeastCommander) {
			// append warbeasts
			for (SelectedWarbeast beast : ((BeastCommander) entry).getBeasts()) {
				result.append(WARBEASTS, writeModel(beast));
			}
		}

		if (entry instanceof SelectedArmyCommander) {
			// append attachment if any
			SelectedModel attachment = ((SelectedArmyCommander) entry)
					.getAttachment();
			if (attachment != null) {
				result.put(ATTACHMENT, writeModel(attachment));
			}
		}

		if (entry instanceof SelectedWarjack) {
			result.put(TYPE, WARJACK);
		}

		if (entry instanceof SelectedWarbeast) {
			result.put(TYPE, WARBEAST);
		}

		if (entry instanceof SelectedWarbeastPack) {
			result.put(TYPE, WARBEAST_PACK); // overwrite warbeast type...
		}

		
		if (entry instanceof SelectedCasterAttachment) {
			result.put(TYPE, CASTER_ATTACHMENT);
		}

		if (entry instanceof SelectedRankingOfficer) {
			result.put(TYPE, RANKING_OFFICER);
		}

		if (entry instanceof SelectedObjective) {
			result.put(TYPE, SOLO_OBJECTIVE);
		}

		if (entry instanceof SelectedSoloMarshal) {
			result.put(TYPE, SOLO_WITH_MARSHAL);
		}

		if (entry instanceof SelectedDragoon) {
			result.put(TYPE, SOLO_DRAGOON);
            result.put(SOLO_DRAGOON_DISMOUNT_OPTION, ((SelectedDragoon) entry).isWithDismountOption());
		}

		if (entry instanceof SelectedWA) {
			result.put(TYPE, WEAPON_ATTACHMENT);
		}

		if (entry instanceof SelectedUA) {
			result.put(TYPE, UNIT_ATTACHMENT);
		}

		if (entry instanceof SelectedBattleEngine) {
			result.put(TYPE, BATTLEENGINE);
		}

		if (entry instanceof SelectedUnit) {

			SelectedUnit unit = (SelectedUnit) entry;
			result.put(TYPE, UNIT);
			if (unit.isMinSize()) {
				result.put(MIN_SIZE, true);
			} else {
				result.put(MAX_SIZE, true);
			}

			if (!unit.getChilds().isEmpty()) {
				for (SelectedEntry attachment : unit.getChilds()) {
					result.append(ATTACHMENTS, writeModel(attachment));
				}
			}
		}

		if (entry instanceof SelectedUnitMarshall) {
			result.put(TYPE, UNIT_WITH_MARSHAL);
			
			if ( ((SelectedUnitMarshall) entry).isMarshallViaUA()) {
				result.put(MARSHAL_VIA_UA, true);
			}
			
			
		}

		return result;
	}

	public static void createBattleObject(Writer writer, BattleStore store) {
		JSONWriter json = new JSONWriter(writer);
		
		ArmyStore player1List = store.getArmy(BattleSingleton.PLAYER1);
		ArmyStore player2List = store.getArmy(BattleSingleton.PLAYER2);
		
		json.object();
		json.key("title").value(store.getTitle());
		json.key("filename").value(store.getFilename());
		
		JSONObject army1list = writeArmyStore(player1List);
		json.key(ARMY1).value(army1list);
		
		JSONArray entries1list = writeBattleEntries(store.getBattleEntries(BattleSingleton.PLAYER1));		
		json.key(ENTRIES1).value(entries1list);

		json.key(PLAYER1_TIME).value(store.getPlayer1TimeRemaining());
		json.key(PLAYER2_TIME).value(store.getPlayer2TimeRemaining());

		if (player2List != null) {
			JSONObject army2list = writeArmyStore(player2List);
			json.key(ARMY2).value(army2list);
			JSONArray entries2list = writeBattleEntries(store.getBattleEntries(BattleSingleton.PLAYER2));
			json.key("ENTRIES2").value(entries2list);
		}
		
		json.endObject();
		
		
	}
	
	public static BattleResult readBattleResult(JSONTokener tokener) {
	
		BattleResult result = new BattleResult();
		
		JSONObject source = new JSONObject(tokener);
		
		result.setArmyName(source.getString(ARMY));
		result.setWinnerNumber("ME".equals(source.getString("WINNER"))?0:1);
		result.setBattleDate(source.getString("DATE"));
		result.setClockType(source.getString("CLOCKTYPE"));
		result.setScenario(source.getString("SCENARIO"));
		result.setVictoryCondition(source.getString("VICTORY_CONDITION"));
		result.setPlayer2name(source.getString("PLAYER2_NAME"));
		if (source.optString("NOTES") != null) {
			result.setNotes(source.optString("NOTES"));	
		}

		if (true) {
			JSONObject armyStore1Source = source.getJSONObject(ARMY1);
			ArmyStore store = new ArmyStore("Me");
			completeArmyStore(store, armyStore1Source);
			result.setArmy1(store);
		}
		
		JSONObject armyStore2Source = source.optJSONObject(ARMY2);
		if (armyStore2Source != null) {
			ArmyStore storePlayer2 = new ArmyStore("Other");
			completeArmyStore(storePlayer2, armyStore2Source);
			result.setArmy2(storePlayer2); 
		}
		
		return result;
	}

	public static void createBattleResult(Writer writer, BattleResult result) {
		JSONWriter json = new JSONWriter(writer);
		json.object()
			.key(ARMY) 
				.value(result.getArmyName())
			.key("WINNER")
				.value(result.getWinnerNumber()==0?"ME":"OTHER")
			.key("DATE")
				.value(result.getBattleDate())
			.key("CLOCKTYPE")
				.value(result.getClockType())
			.key("SCENARIO")
				.value(result.getScenario())
			.key("VICTORY_CONDITION")
				.value(result.getVictoryCondition())
			.key("PLAYER2_NAME")
				.value(result.getPlayer2name());

		if (result.getNotes() != null && result.getNotes().length() > 0) {
			json.key("NOTES")
				.value(result.getNotes());
		}

		
		JSONObject army1list = writeArmyStore(result.getArmy1());
		json.key(ARMY1).value(army1list);
		
		if (result.getArmy2() != null) {
			JSONObject army2list = writeArmyStore(result.getArmy2());
			json.key(ARMY2).value(army2list);
		}

		json.endObject();
	}
	
	private static JSONObject writeArmyStore(ArmyStore player1List) {
		JSONObject army1list = new JSONObject();
		army1list.put(ARMY, player1List.getFilename());
		army1list.put(FACTION_ID, player1List.getFactionId());
		army1list.put(NB_CASTERS, player1List.getNbCasters());
		army1list.put(NB_POINTS, player1List.getNbPoints());
		if (player1List.getTierId() != null) {
			army1list.put(TIER_ID, player1List.getTierId());
		}
		if (player1List.getContractId() != null) {
			army1list.put(CONTRACT_ID, player1List.getContractId());
		}
	
		for (SelectedEntry entry : player1List.getEntries()) {
			JSONObject model = writeModel(entry);
			army1list.append(ENTRIES, model);
		}
		return army1list;
	}
	
	
	private static JSONArray writeBattleEntries(List<BattleEntry> list) {
		JSONArray result = new JSONArray();
		for (BattleEntry entry : list) {
			JSONObject model = writeBattleEntry(entry);
			result.put(model);
		}
		return result;
	}

	private static JSONObject writeModelDescription(MiniModelDescription desc) {
		JSONObject result = new JSONObject();
		result.put(NAME, desc.getName());
		result.put(DEF, desc.getDef());
		result.put(ARM, desc.getArm());
		return result;
	}
	
	private static MiniModelDescription readModelDescription(JSONObject desc) {
		MiniModelDescription result = new MiniModelDescription(desc.getString(NAME), desc.getInt(DEF), desc.getInt(ARM));
		return result;
	}

	
	private static JSONObject writeBattleEntry(BattleEntry entry) {
		
		JSONObject result = new JSONObject();
		result.put(ID, entry.getId());
		result.put(BATTLE_ID, entry.getUniqueId());
		result.put(LABEL, entry.getLabel());
        result.put(COST, entry.getCost());
        result.put(SPECIALIST, entry.isSpecialist());
		if (entry.getParentId() > 0) {
			result.put(PARENT_ID, entry.getParentId());
		}

		if (entry instanceof MultiPVModel && ! (entry instanceof MultiPVUnit) ) {
			result.put(DAMAGES, ((MultiPVModel) entry).getDamageGrid().toStringWithDamageStatus());
			if (((MultiPVModel) entry).getDamageGrid().getModel() != null) {
				result.put(DESC, writeModelDescription(((MultiPVModel) entry).getDamageGrid().getModel()));
			}
		}

		if (entry instanceof BattleEntry) { 
			// par defaut, �cras� si autre valeur
			result.put(TYPE, MONO_PV);
		}

		if (entry instanceof SingleDamageLineEntry) { 
			result.put(TYPE, SINGLE_LINE);
		}
		if (entry instanceof BeastEntry) { 
			result.put(TYPE, BEAST);
		}
		if (entry instanceof JackEntry) { 
			result.put(TYPE, JACK);
		}
		if (entry instanceof KarchevEntry) { 
			result.put(TYPE, KARCHEV);
		}
		
		if (entry instanceof MultiPVUnit) {
			result.put(TYPE, MULTIPV_UNIT);
			
			JSONArray models = new JSONArray();
			MultiPVUnit unit = (MultiPVUnit) entry;
			
			if (unit.isLeaderAndGrunts()) {
				result.put(LEADER_AND_GRUNTS, true);
			}
			int modelId = 0;
			for (SingleDamageLineEntry model : unit.getModels()) {
				
				JSONObject obj = new JSONObject();
				obj.put(ID, model.getId());
				obj.put(MODEL_NUMBER, modelId++);
				obj.put(BATTLE_ID, model.getUniqueId());
				obj.put(LABEL, model.getLabel());
				
				obj.put(DAMAGES, model.getDamageGrid().toStringWithDamageStatus());
				if (model.getDamageGrid().getModel() != null) {
					obj.put(DESC, writeModelDescription(model.getDamageGrid().getModel()));
				}
				
				models.put(obj);
			}
			result.put(MODELS, models);
		}
		
		return result;
	}

	public static BattleListDescriptor readBattleDescriptor(JSONTokener tokener) {
		
		BattleStore store = new BattleStore();
		
		JSONObject source = new JSONObject(tokener);
		
		store.setTitle(source.getString("title"));
		
		ArmyStore armyStore1 = new ArmyStore("");
		completeArmyStore(armyStore1, source.getJSONObject(ARMY1));
		store.setArmy(armyStore1, BattleSingleton.PLAYER1);
		
		if ( source.optJSONObject(ARMY2) != null) {
			ArmyStore armyStore2 = new ArmyStore("");
			completeArmyStore(armyStore2, source.getJSONObject(ARMY2));
			store.setArmy(armyStore2, BattleSingleton.PLAYER2);
		}
		
		
		BattleListDescriptor result = new BattleListDescriptor(store);
		return result;
	}

	public static BattleStore readBattleStore(JSONTokener tokener) {
		BattleStore store = new BattleStore();
		
		JSONObject source = new JSONObject(tokener);
		
		store.setFilename(source.getString("filename"));
		store.setTitle(source.getString("title"));
		
		ArmyStore armyStore1 = new ArmyStore("");
		completeArmyStore(armyStore1, source.getJSONObject(ARMY1));
		store.setArmy(armyStore1, BattleSingleton.PLAYER1);
		
		if ( source.optJSONObject(ARMY2) != null) {
			ArmyStore armyStore2 = new ArmyStore("");
			completeArmyStore(armyStore2, source.getJSONObject(ARMY2));
			store.setArmy(armyStore2, BattleSingleton.PLAYER2);
		}
		
		store.setPlayer1TimeRemaining(source.getLong(PLAYER1_TIME));
		store.setPlayer2TimeRemaining(source.getLong(PLAYER2_TIME));

		
		// read entries
		JSONArray entries1 = source.getJSONArray(ENTRIES1);
		loadBattleEntries(store, entries1, BattleSingleton.PLAYER1);
		
		JSONArray entries2 = source.optJSONArray(ENTRIES2);
		if (entries2 != null) {
			loadBattleEntries(store, entries2, BattleSingleton.PLAYER2);	
		}
		
		return store;
		
	}

	private static void loadBattleEntries(BattleStore store, JSONArray entries, int playerNumber) {
		List<BattleEntry> wannabeParents = new ArrayList<BattleEntry>();
		for (int i = 0; i < entries.length(); i++) {
			JSONObject entry = entries.getJSONObject(i);
			
			String type = entry.getString(TYPE);
			String refId = entry.getString(ID);
			int uniqueId = entry.getInt(BATTLE_ID);
			String label = entry.getString(LABEL);
			int parentId = entry.optInt(PARENT_ID);
            int cost = entry.optInt(COST);
            boolean specialist = entry.optBoolean(SPECIALIST);
			
			ArmyElement reference = ArmySingleton.getInstance().getArmyElement(refId);
			
			BattleEntry battleEntry = null;

			if (MONO_PV.equals(type)) {
				battleEntry = new BattleEntry(reference, uniqueId, cost, specialist);
			}
			
			if (SINGLE_LINE.equals(type)) {
				battleEntry = new SingleDamageLineEntry(reference, uniqueId, cost, specialist);
				String damageGridString = entry.getString(DAMAGES);
				((SingleDamageLineEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
			}
			
			if (MULTIPV_UNIT.equals(type)) {
				battleEntry = new MultiPVUnit(reference, uniqueId, cost, specialist);
				
				MultiPVUnit unit = (MultiPVUnit) battleEntry;
				MultiPVUnitGrid grid = (MultiPVUnitGrid) unit.getDamageGrid();
				
				if (entry.optBoolean(LEADER_AND_GRUNTS)) {
					unit.setLeaderAndGrunts(true);
				}

				
				JSONArray models = entry.optJSONArray(MODELS);
				for (int j = 0; j < models.length(); j++) {
					JSONObject model = models.getJSONObject(j);
					String damageGridString = model.getString(DAMAGES);
					
					MiniModelDescription desc = readModelDescription(model.getJSONObject(DESC));
					
					ModelDamageLine damageLine = new ModelDamageLine(desc, damageGridString.length());
					damageLine.fromStringWithDamageStatus(damageGridString);
					grid.getDamageLines().add(damageLine);
					
					SingleDamageLineEntry line = new SingleDamageLineEntry(desc, j, reference, uniqueId, damageLine, 0, false);
					unit.getModels().add(line);
					
				}
				
			}

            if (KARCHEV.equals(type)) {
                battleEntry = new KarchevEntry(reference, uniqueId, 0);
                String damageGridString = entry.getString(DAMAGES);
                ((JackEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
            }
			
			if (JACK.equals(type)) {
				battleEntry = new JackEntry(reference, uniqueId, cost, specialist);
				
				String damageGridString = entry.getString(DAMAGES); 
				((JackEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
			}
			if (BEAST.equals(type)) {
				
				
				battleEntry = new BeastEntry( (Warbeast) reference, uniqueId, cost, specialist);
				String damageGridString = entry.getString(DAMAGES); 
				((BeastEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
			}
			
			
			if (parentId > 0) {
				// search parent
				for (BattleEntry maybeParent : wannabeParents) {
					if (maybeParent.getUniqueId() == parentId) {
						// make sure we have really a parent
						battleEntry.setAttached(true);
						battleEntry.setParentId(parentId);
						break;
					}
				}
			} else {
				wannabeParents.add(battleEntry);
			}

			
			store.getBattleEntries(playerNumber).add(battleEntry);
		}
		
	}

    public static void createTierExport(Writer writer, List<Tier> tiers,List<Contract> contracts) {
        JSONWriter json = new JSONWriter(writer);

        json.object();

        JSONArray tiersArray = new JSONArray();

        for (Tier tier : tiers) {
            JSONObject tierJson = new JSONObject();
            tierJson.put("name", tier.getTitle());
            tierJson.put("casterId", tier.getCasterId());
            tierJson.put("faction", tier.getFactionId());

            exportAvailableModels(tier.getAvailableModels(), tierJson);



            JSONArray levels = new JSONArray();
            for (TierLevel level : tier.getLevels()){
                JSONObject levelJson = new JSONObject();
                levelJson.put("level", level.getLevel());
                levelJson.put("benefit", level.getBenefit().getInGameEffect());

                JSONObject onlyModels = new JSONObject();
                for (TierEntry onlyModel : level.getOnlyModels()) {
                    onlyModels.append("ids", onlyModel.getId());
                }
                levelJson.put("onlyModels", onlyModels);

                JSONArray mustHaveList = new JSONArray();
                for (TierEntryGroup group : level.getMustHaveModels()) {
                    JSONObject mustHave = new JSONObject();
                    mustHave.put("min", group.getMinCount());
                    mustHave.put("inBG", group.isInBattlegroup());
                    mustHave.put("inMarshal", group.isInJackMarshal());
                    for (TierEntry entry : group.getEntries()) {
                        mustHave.append("ids" , entry.getId());
                    }

                    mustHaveList.put(mustHave);
                }
                for (TierEntryGroup group : level.getMustHaveModelsInBG()) {
                    JSONObject mustHave = new JSONObject();
                    mustHave.put("min", group.getMinCount());
                    mustHave.put("inBG", true);
                    mustHave.put("inMarshal", group.isInJackMarshal());
                    for (TierEntry entry : group.getEntries()) {
                        mustHave.append("ids" , entry.getId());
                    }
                    mustHaveList.put(mustHave);
                }
                for (TierEntryGroup group : level.getMustHaveModelsInMarshal()) {
                    JSONObject mustHave = new JSONObject();
                    mustHave.put("min", group.getMinCount());
                    mustHave.put("inBG", false);
                    mustHave.put("inMarshal", true);
                    for (TierEntry entry : group.getEntries()) {
                        mustHave.append("ids" , entry.getId());
                    }
                    mustHaveList.put(mustHave);
                }
                levelJson.put("mustHave", mustHaveList);


                JSONArray faAlterList = new JSONArray();
                JSONArray costAlterList = new JSONArray();
                for (TierFACostBenefit alteration : level.getBenefit().getAlterations()) {
                    if (alteration instanceof TierFAAlteration) {
                        JSONObject fa = new JSONObject();
                        TierFAAlteration faAlteration = (TierFAAlteration) alteration;
                        fa.put("bonus", faAlteration.getFaAlteration());
                        fa.put("id", faAlteration.getEntry().getId());
                        for (TierEntry entry : faAlteration.getForEach()) {
                            fa.append("forEach" , entry.getId());
                        }

                        faAlterList.put(fa);
                    }


                    if (alteration instanceof TierCostAlteration) {
                        JSONObject fa = new JSONObject();
                        TierCostAlteration costAlteration = (TierCostAlteration) alteration;
                        fa.put("bonus", costAlteration.getCostAlteration());
                        fa.put("id", costAlteration.getEntry().getId());
                        if (alteration.isRestricted()) {
                            fa.put("restricted_to", costAlteration.getRestrictedToId());
                        }
                        costAlterList.put(fa);
                    }



                }

                JSONArray freeModelsList = new JSONArray();
                for (TierFreeModel freebee : level.getBenefit().getFreebies()) {
                    JSONObject free = new JSONObject();
                    for (TierEntry entry : freebee.getFreeModels()) {
                        free.append("id", entry.getId());
                    }
                    for (TierEntry entry : freebee.getForEach()) {
                        free.append("forEach" , entry.getId());
                    }
                    freeModelsList.put(free);
                }
                levelJson.put("faAlterations", faAlterList);
                levelJson.put("costAlterations", costAlterList);
                levelJson.put("freeModels", freeModelsList);

                levels.put(levelJson);
            }

            tierJson.put("levels", levels);

            tiersArray.put(tierJson);


        }

        for (Contract contract : contracts) {
            JSONObject tierJson = new JSONObject();
            tierJson.put("name", "Contract : " + contract.getTitle());
            tierJson.put("faction", contract.getFactionId());

            exportAvailableModels(contract.getAvailableModels(), tierJson);

            JSONArray levels = new JSONArray();

            JSONObject levelJson = new JSONObject();
            levelJson.put("level", 1);
            levelJson.put("benefit", contract.getBenefit().getInGameEffect());

            JSONObject onlyModels = new JSONObject();
            for (TierEntry onlyModel : contract.getOnlyModels()) {
                onlyModels.append("ids", onlyModel.getId());
            }
            levelJson.put("onlyModels", onlyModels);


            // no "must have" and "free models", but create placeholders for javascript
            JSONArray mustHaveList = new JSONArray();
            levelJson.put("mustHave", mustHaveList);

            JSONArray freeModelsList = new JSONArray();
            levelJson.put("freeModels", freeModelsList);

            JSONArray faAlterList = new JSONArray();
            JSONArray costAlterList = new JSONArray();
            for (TierFACostBenefit alteration : contract.getBenefit().getAlterations()) {
                if (alteration instanceof TierFAAlteration) {
                    JSONObject fa = new JSONObject();
                    TierFAAlteration faAlteration = (TierFAAlteration) alteration;
                    fa.put("bonus", faAlteration.getFaAlteration());
                    fa.put("id", faAlteration.getEntry().getId());
                    for (TierEntry entry : faAlteration.getForEach()) {
                        fa.append("forEach" , entry.getId());
                    }

                    faAlterList.put(fa);
                }


                if (alteration instanceof TierCostAlteration) {
                    JSONObject fa = new JSONObject();
                    TierCostAlteration costAlteration = (TierCostAlteration) alteration;
                    fa.put("bonus", costAlteration.getCostAlteration());
                    fa.put("id", costAlteration.getEntry().getId());
                    if (alteration.isRestricted()) {
                        fa.put("restricted_to", costAlteration.getRestrictedToId());
                    }
                    costAlterList.put(fa);
                }



            }

            levelJson.put("faAlterations", faAlterList);
            levelJson.put("costAlterations", costAlterList);

            levels.put(levelJson);

            tierJson.put("levels", levels);

            tiersArray.put(tierJson);

        }


        json.key("tiers");
        json.value(tiersArray);


        json.endObject();

    }

    private static void exportAvailableModels(ArrayList<AvailableModels> availableModels, JSONObject tierJson) {
        JSONArray models = new JSONArray();
        for (AvailableModels model : availableModels) {
            JSONObject modelRestriction = new JSONObject();
            modelRestriction.put("type", model.getType().name());
            modelRestriction.put("models", model.getModels());
            models.put(modelRestriction);
        }

        tierJson.put("availableModels", models);
    }



    public static void createFactionExport(Writer writer, Faction faction) {

        JSONWriter json = new JSONWriter(writer);

        json.object();

        JSONArray groups = new JSONArray();

        JSONObject casterGroup = new JSONObject();
        casterGroup.put("id", faction.getEnumValue().getId() + "_warcasters");
        casterGroup.put("label", "Warcasters");
        casterGroup.put("logo", faction.getEnumValue().getId());

        JSONArray casterlist = new JSONArray();
        for (Warcaster entry : faction.getCasters().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "warcaster");
            putBasicValues(faction, entry, result);
            casterlist.put(result);
        }

        casterGroup.put("entries", casterlist);
        groups.put(casterGroup);


        JSONObject warlockGroup = new JSONObject();
        warlockGroup.put("id", faction.getEnumValue().getId() + "_warlocks");
        warlockGroup.put("label", "Warlocks");
        warlockGroup.put("logo", faction.getEnumValue().getId());

        JSONArray warlocklist = new JSONArray();
        for (Warlock entry : faction.getWarlocks().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "warlock");
            putBasicValues(faction, entry, result);
            warlocklist.put(result);
        }

        warlockGroup.put("entries", warlocklist);
        groups.put(warlockGroup);

        JSONObject jacksGroup = new JSONObject();
        jacksGroup.put("id", faction.getEnumValue().getId() +"_warjacks");
        jacksGroup.put("label", "Warjacks");
        jacksGroup.put("logo", faction.getEnumValue().getId());

        JSONArray jacklist = new JSONArray();
        for (Warjack entry : faction.getJacks().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "warjack");
            putBasicValues(faction, entry, result);

            if (! entry.getAllowedEntriesToAttach().isEmpty()) {
                result.put("restricted_to", entry.getAllowedEntriesToAttach());
            }

            jacklist.put(result);
        }
        jacksGroup.put("entries", jacklist);
        groups.put(jacksGroup);


        JSONObject beastsGroup = new JSONObject();
        beastsGroup.put("id", faction.getEnumValue().getId() +"_warbeasts");
        beastsGroup.put("label", "Warbeasts");
        beastsGroup.put("logo", faction.getEnumValue().getId());

        JSONArray beastlist = new JSONArray();
        for (Warbeast entry : faction.getBeasts().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "warbeast");
            putBasicValues(faction, entry, result);

            if (! entry.getAllowedEntriesToAttach().isEmpty()) {
                result.put("restricted_to", entry.getAllowedEntriesToAttach());
            }

            beastlist.put(result);
        }
        beastsGroup.put("entries", beastlist);
        groups.put(beastsGroup);



        JSONObject BEGroup = new JSONObject();
        BEGroup.put("id", faction.getEnumValue().getId() +"_bes");
        BEGroup.put("label", "Battle engines");
        BEGroup.put("logo", faction.getEnumValue().getId());

        JSONArray belist = new JSONArray();
        for (BattleEngine entry : faction.getBattleEngines().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "battleengine");
            putBasicValues(faction, entry, result);
            belist.put(result);
        }
        BEGroup.put("entries", belist);
        groups.put(BEGroup);

        JSONObject unitsGroup = new JSONObject();
        unitsGroup.put("id", faction.getEnumValue().getId() +"_units");
        unitsGroup.put("label", "Units");
        unitsGroup.put("logo", faction.getEnumValue().getId());

        JSONArray unitslist = new JSONArray();
        for (Unit entry : faction.getUnits().values()) {
            JSONObject result = new JSONObject();
            result.put("type", "unit");
            putBasicValues(faction, entry, result);
            if (entry.isVariableSize()) {
                result.put("costMin", entry.getBaseCost());
                result.put("costMax", entry.getFullCost());
                result.put("min", entry.getBaseNumberOfModels());
                result.put("max", entry.getFullNumberOfModels());
            }
            if (entry.isJackMarshall()) {
                result.put("type", "unitMarshall");
            }
            unitslist.put(result);
        }
        unitsGroup.put("entries", unitslist);
        groups.put(unitsGroup);


        JSONObject UAGroup = new JSONObject();
        UAGroup.put("id", faction.getEnumValue().getId() +"_UAs");
        UAGroup.put("label", "Unit Attachments");
        UAGroup.put("logo", faction.getEnumValue().getId());

        JSONArray UAlist = new JSONArray();
        for (Unit entry : faction.getUnits().values()) {

            if (entry.getUnitAttachment() != null) {
                UnitAttachment ua = entry.getUnitAttachment();
                JSONObject result = new JSONObject();
                result.put("type", "UA");
                result.put("restricted_to", entry.getId());
                putBasicValues(faction, ua, result);

                if (ua.isJackMarshall()) {
                    result.put("type", "UAMarshall");
                }
                UAlist.put(result);
            }
        }
        // not put UA here, cause maybe solos can be added later

        JSONObject WAGroup = new JSONObject();
        WAGroup.put("id", faction.getEnumValue().getId() +"_WAs");
        WAGroup.put("label", "Weapon Attachments");
        WAGroup.put("logo", faction.getEnumValue().getId());

        JSONArray WAlist = new JSONArray();
        for (Unit entry : faction.getUnits().values()) {

            if (entry.getWeaponAttachment() != null) {
                WeaponAttachment wa = entry.getWeaponAttachment();
                JSONObject result = new JSONObject();
                result.put("type", "WA");
                result.put("restricted_to", entry.getId());
                putBasicValues(faction, wa, result);
                WAlist.put(result);
            }
        }
        WAGroup.put("entries", WAlist);
        groups.put(WAGroup);



        JSONObject solosGroup = new JSONObject();
        solosGroup.put("id", faction.getEnumValue().getId() +"_solos");
        solosGroup.put("label", "Solos");
        solosGroup.put("logo", faction.getEnumValue().getId());

        JSONArray soloslist = new JSONArray();
        for (Solo entry : faction.getSolos().values()) {
            boolean ua = false; // this solo is a generic UA or a ranking officer
            JSONObject result = new JSONObject();
            result.put("type", "solo");
            putBasicValues(faction, entry, result);
            if (entry.isMercenaryUnitAttached()) {
                result.put("type", "RA");
                ua = true;
            }
            if (entry.isGenericUnitAttached()) {
                result.put("type", "UA");
                ua = true;
            }
            if (! entry.getAllowedEntriesToAttach().isEmpty()) {
                result.put("restricted_to", entry.getAllowedEntriesToAttach());
            }

            if (entry.isWarcasterAttached()) {
                result.put("type", "soloAttachment");
            }
            if (entry.isDragoon() && entry.isDismountOption()) {
                result.put("type", "soloDragoon");
            }
            if (entry.getModels().get(0).isJackMarshal()) {
                result.put("type", "soloMarshall");
            }
            if (entry.getModels().get(0).isJourneyManWarcaster()) {
                result.put("type", "soloJourneyMan");
            };
            if (entry.getModels().get(0).isLesserWarlock()) {
                result.put("type", "soloLesserWarlock");
            };

            if (ua) {
                UAlist.put(result);
            } else {
                soloslist.put(result);
            }

        }

        // put UA now, cause solos have been added
        UAGroup.put("entries", UAlist);
        groups.put(UAGroup);



        solosGroup.put("entries", soloslist);
        groups.put(solosGroup);


        json.key("groups");
        json.value(groups);


        json.endObject();

    }

    private static void putBasicValues(Faction faction, ArmyElement entry, JSONObject result) {
        result.put(ID, entry.getId());
        if (entry.getName().equals(entry.getFullName())) {
            result.put("name", entry.getName());
        } else {
            result.put("name", entry.getName() + "(" + entry.getFullName() + ")");
        }

        result.put("cost", entry.getBaseCost());
        result.put("faction", faction.getId());
//        result.put("selectedFA", 0);
//        result.put("alteredFa", 0);
//        result.put("alteredCost", 0);
        result.put("fa", entry.getFaString());

        if (! entry.getAllowedFactionsToWorkFor().isEmpty()) {
            result.put("works_for", entry.getAllowedFactionsToWorkFor());
        }



    }
}
