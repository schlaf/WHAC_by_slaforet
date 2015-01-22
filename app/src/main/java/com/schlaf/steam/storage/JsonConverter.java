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
import com.schlaf.steam.data.MultiPVUnitGrid;
import com.schlaf.steam.data.Warbeast;

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
			result = new SelectedDragoon(id, label, true);
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
		result.setBattleDate(new Date(source.getString("DATE")));
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
			
			ArmyElement reference = ArmySingleton.getInstance().getArmyElement(refId);
			
			BattleEntry battleEntry = null;

			if (MONO_PV.equals(type)) {
				battleEntry = new BattleEntry(reference, uniqueId);
			}
			
			if (SINGLE_LINE.equals(type)) {
				battleEntry = new SingleDamageLineEntry(reference, uniqueId);
				String damageGridString = entry.getString(DAMAGES);
				((SingleDamageLineEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
			}
			
			if (MULTIPV_UNIT.equals(type)) {
				battleEntry = new MultiPVUnit(reference, uniqueId);
				
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
					
					SingleDamageLineEntry line = new SingleDamageLineEntry(desc, j, reference, uniqueId, damageLine);
					unit.getModels().add(line);
					
				}
				
			}

            if (KARCHEV.equals(type)) {
                battleEntry = new KarchevEntry(reference, uniqueId);
                String damageGridString = entry.getString(DAMAGES);
                ((JackEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
            }
			
			if (JACK.equals(type)) {
				battleEntry = new JackEntry(reference, uniqueId);
				
				String damageGridString = entry.getString(DAMAGES); 
				((JackEntry) battleEntry).getDamageGrid().fromStringWithDamageStatus(damageGridString);
			}
			if (BEAST.equals(type)) {
				
				
				battleEntry = new BeastEntry( (Warbeast) reference, uniqueId);
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
}
