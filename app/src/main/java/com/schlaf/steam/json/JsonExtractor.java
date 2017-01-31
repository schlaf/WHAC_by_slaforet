package com.schlaf.steam.json;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.schlaf.steam.R;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.data.ArmyCommander;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.Capacity;
import com.schlaf.steam.data.Colossal;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.MeleeWeapon;
import com.schlaf.steam.data.MonoPVDamage;
import com.schlaf.steam.data.MountWeapon;
import com.schlaf.steam.data.Myrmidon;
import com.schlaf.steam.data.MyrmidonDamageGrid;
import com.schlaf.steam.data.RangedWeapon;
import com.schlaf.steam.data.Restrictable;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Spell;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.Warlock;
import com.schlaf.steam.data.Weapon;

import org.json.compatibility.JSONArray;
import org.json.compatibility.JSONObject;
import org.json.compatibility.JSONString;
import org.json.compatibility.JSONTokener;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by seb on 13/06/16.
 */
public class JsonExtractor {

    public static final String TAG = "JsonExtractor";

    private int extractFromString(String s) {
        if (s == null || s.length() == 0 || s.trim().length() == 0) {
            return 0;
        } else {
            try {
                return Integer.valueOf(s);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private HashMap<String, Capacity> capacitiesMap = new HashMap<>(3000);
    private HashMap<String, Spell> spellsMap = new HashMap<>(3000);

    public void doExtractCapacities(Resources res, Application app, InputStream is) {

        long startTime = System.currentTimeMillis();

        JSONTokener tokener = new JSONTokener(is);
        JSONArray capas = new JSONArray(tokener);


        JSONObject capa;
        for (int i = 0; i < capas.length(); i++) {
            capa = capas.optJSONObject(i);
            JSONObject id = capa.optJSONObject("_id");
            String oid = id.getString("$oid");
            Capacity capacity = new Capacity();
            capacity.setTitle(capa.optString("_title"));
            capacity.setLabel(capa.optString("__text"));
            capacity.setType(capa.optString("_type"));
            capacitiesMap.put(oid, capacity);
        }

        long endTime = System.currentTimeMillis();
        Log.e("CAPA LOAD", "duration = " + (endTime - startTime) );
    }

    public void doExtractSpells(Resources res, Application app, InputStream is) {

        long startTime = System.currentTimeMillis();

        JSONTokener tokener = new JSONTokener(is);
        JSONArray spells = new JSONArray(tokener);

        JSONObject sp;
        JSONObject id;
        for (int i = 0; i < spells.length(); i++) {
            sp = spells.optJSONObject(i);
            id = sp.optJSONObject("_id");
            String oid = id.getString("$oid");
            Spell spell = new Spell();
            spell.setTitle(sp.optString("_name"));
            spell.setCost(sp.optString("_cost"));
            spell.setRange(sp.optString("_rng"));
            spell.setAoe(sp.optString("_aoe"));
            spell.setPow(sp.optString("_pow"));
            spell.setDuration(sp.optString("_duration"));
            spell.setOffensive(sp.optString("_off"));
            spell.setFullText(sp.optString("__text"));
            spellsMap.put(oid, spell);
        }

        long endTime = System.currentTimeMillis();
        Log.e("SPELLS LOAD", "duration = " + (endTime - startTime) );

    }


    public void doExtractModels(Resources res, Application app, InputStream is) {

        long startTime = System.currentTimeMillis();

        for (FactionNamesEnum factionName : FactionNamesEnum.values()) {
            if ( ArmySingleton.getInstance().getFactions().get(factionName.getId()) == null ) {
                Faction faction = new Faction();
                faction.setId(factionName.getId());
                faction.setName(factionName.name());
                faction.setFullName(factionName.name());
                faction.setSystem(factionName.getGameSystem());

                ArmySingleton.getInstance().getFactions().put(faction.getId(),
                        faction);
            }
        }


        try {

//            JsonFactory jsonFactory = new JsonFactory();
//            JsonParser jp = null;
//
//            jp = jsonFactory.createParser(is);
//
//
//            jp.nextToken();
//            // iterate through the array until token equal to "]"
//
//            boolean inModel = false;
//
//            while (jp.nextToken() != JsonToken.END_ARRAY) {
//
//                if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
//
//                    // start model
//
//                }
//
//
//
//                jp.getCurrentName();
//
//
//                while (jp.nextToken() != JsonToken.END_ARRAY) {
//                    // output the array data
//                    System.out.println(jParser.getText());
//                }
//
//
//
//            }

            JSONTokener tokener = new JSONTokener(is);

            long startTimeTokener = System.currentTimeMillis();
            JSONArray models = new JSONArray(tokener);

            long endTimeTokener = System.currentTimeMillis();
            Log.e("MODELS Tokener ", "duration = " + (endTimeTokener - startTimeTokener) );

            for (int i = 0; i < models.length(); i++) {
                JSONObject model = models.optJSONObject(i);

                try {
                    Log.e(TAG, "json models loaded " + model.optString("_id"));

                    Faction faction = ArmySingleton.getInstance().getFactions().get(model.optString("faction"));
                    ArmyElement entry = null;
                    switch (model.optString("type")) {
                        case "warcaster": {
                            entry = new Warcaster();
                            entry.setId(model.optString("_id"));
                            fillWarcaster(model, (Warcaster) entry);

                            faction.getCasters().put(entry.getId(), (Warcaster) entry);
                            break;
                        }
                        case "warlock": {
                            entry = new Warlock();
                            entry.setId(model.optString("_id"));
                            fillWarlock(model, (Warlock) entry);
                            faction.getWarlocks().put(entry.getId(), (Warlock) entry);
                            break;
                        }
                        case "warjack": {
                            if (model.optBoolean("myrmidon")) {
                                entry = new Myrmidon();
                            } else {
                                entry = new Warjack();
                            }
                            ((Warjack) entry).setBaseCost(model.optInt("cost"));
                            entry.setId(model.optString("_id"));
                            faction.getJacks().put(entry.getId(), (Warjack) entry);
                            break;
                        }
                        case "warbeast": {
                            entry = new Warbeast();
                            entry.setId(model.optString("_id"));
                            ((Warbeast) entry).setBaseCost(model.optInt("cost"));
                            faction.getBeasts().put(entry.getId(), (Warbeast) entry);
                            break;
                        }
                        case "colossal": {
                            entry = new Colossal();
                            if (model.optBoolean("myrmidon")) {
                                ((Colossal) entry).setMyrmidon(true);
                            }
                            entry.setId(model.optString("_id"));
                            ((Colossal) entry).setBaseCost(model.optInt("cost"));
                            faction.getJacks().put(entry.getId(), (Colossal) entry);
                            break;
                        }
                        case "CA":
                        case "WA":
                        case "solo": {
                            entry = new Solo();
                            entry.setId(model.optString("_id"));
                            fillSolo(model, (Solo) entry);
                            faction.getSolos().put(entry.getId(), (Solo) entry);
                            break;
                        }
                        case "unit": {
                            entry = new Unit();
                            entry.setId(model.optString("_id"));

                            fillUnit(model, (Unit) entry);


                            faction.getUnits().put(entry.getId(), (Unit) entry);
                            break;
                        }
                        case "battle engine": {
                            entry = new BattleEngine();
                            entry.setId(model.optString("_id"));
                            ((BattleEngine) entry).setBaseCost(model.optInt("cost"));
                            faction.getBattleEngines().put(entry.getId(), (BattleEngine) entry);
                            break;
                        }
                        default: {

                        }

                    }


                    entry.setName(model.optString("name"));
                    entry.setVersion(model.optString("version"));
                    entry.setFullName(model.optString("full_name"));
                    entry.setFaFromXml(model.optString("fa"));
                    entry.setCompleted(true);



                    JSONArray subModels = model.optJSONArray("models");
                    for (int j = 0; j < subModels.length(); j++) {
                        JSONObject subModel = subModels.optJSONObject(j);
                        JSONObject basestats = subModel.optJSONObject("basestats");
                        JSONObject weapons = subModel.optJSONObject("weapons");
                        JSONArray spells = subModel.optJSONArray("spells");
                        JSONArray capacities = subModel.optJSONArray("capacities");
                        SingleModel sModel = new SingleModel();
                        loadSingleModel(entry, sModel, basestats, weapons, spells, capacities);
                        entry.getModels().add(sModel);

                    }


                    entry.setFaction(faction.getEnumValue());
                    ;

                    JSONArray worksFor = model.optJSONArray("works_for");
                    if (worksFor != null && worksFor.length() > 0) {
                        for (int wf = 0; wf < worksFor.length(); wf++) {
                            JSONObject factionFor = worksFor.optJSONObject(wf);
                            entry.getAllowedFactionsToWorkFor().add(factionFor.optString("_id"));
                        }
                    }

                    JSONArray restrictedTo = model.optJSONArray("restricted_to");
                    if (restrictedTo != null) {
                        for (int rt = 0; rt < restrictedTo.length(); rt++) {
                            JSONObject restrict = restrictedTo.optJSONObject(rt);
                            ((Restrictable) entry).getAllowedEntriesToAttach().add(restrict.optString("_id"));
                        }
                    }

                } catch (Exception e) {
                    Log.e("jsonextractor", "error loading model  ", e);
                }

            }

            is.close();

            long endTime = System.currentTimeMillis();
            Log.e("MODELS LOAD", "duration = " + (endTime - startTime) );
            Log.d(TAG,"json models loaded");

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
        }


    }

    private void fillWarcaster(JSONObject model, Warcaster entry) {
        entry.setGeneration(ArmyCommander.Generation.E);
        entry.setGenerationId("E");
        entry.setWarjackPoints(extractFromString(model.optString("wj_points")));
        entry.setCompanionId(null);

        JSONObject feat = model.optJSONObject("feat");
        entry.setFeatTitle(feat.optString("title"));
        entry.setFeatContent(feat.optString("text"));
    }

    private void fillWarlock(JSONObject model, Warlock entry) {
        entry.setWarbeastPoints(extractFromString(model.optString("wb_points")));
        JSONObject feat = model.optJSONObject("feat");
        entry.setFeatTitle(feat.optString("title"));
        entry.setFeatContent(feat.optString("text"));
    }

    private void fillSolo(JSONObject model, Solo entry) {
        entry.setBaseCost(model.optInt("cost"));

        entry.setWarcasterAttached(model.optBoolean("caster_attachment"));
        entry.setGenericUnitAttached("CA".equals(model.optString("type")));
        entry.setWeaponAttachement("WA".equals(model.optString("type")));
        entry.setJourneyMan(model.optBoolean("journeyman_warcaster"));
        entry.setLesserWarlock(model.optBoolean("lesser_warlock"));
    }

    private void fillUnit(JSONObject model, Unit entry) {
        entry.setBaseCost(model.optInt("minCost"));
        entry.setBaseNumberOfModels(model.optInt("minSize"));
        if ("-".equals(model.optString("maxCost"))) {
            entry.setVariableSize(false); // one size fits all ;-)
            entry.setFullCost(model.optInt("minCost"));
            entry.setFullNumberOfModels(model.optInt("minSize"));
        } else {
            entry.setVariableSize(true);
            entry.setFullCost(model.optInt("maxCost"));
            entry.setFullNumberOfModels(model.optInt("maxSize"));
        }
    }

    private void loadSingleModel(ArmyElement entry, SingleModel sModel, JSONObject basestats, JSONObject weapons, JSONArray spells, JSONArray capacities) {

        sModel.setName(basestats.optString("_name"));
        sModel.setSPD(basestats.optInt("_spd"));
        sModel.setSTR(basestats.optInt("_str"));
        sModel.setMAT(basestats.optInt("_mat"));
        sModel.setRAT(basestats.optInt("_rat"));
        sModel.setDEF(basestats.optInt("_def"));
        sModel.setARM(basestats.optInt("_arm"));
        sModel.setCMD(basestats.optInt("_cmd"));

        sModel.setAdvanceDeployment(basestats.optBoolean("_advance_deployment"));
        sModel.setAmphibious(basestats.optBoolean("_amphibious"));
        sModel.setArcNode(basestats.optBoolean("_arc_node"));
        sModel.setAssault(basestats.optBoolean("_assault"));
        sModel.setCavalry(basestats.optBoolean("_cavalry"));
        sModel.setCma(basestats.optBoolean("_cma"));
        sModel.setCra(basestats.optBoolean("_cra"));
        sModel.setConstruct(basestats.optBoolean("_construct"));
        sModel.setEyelessSight(basestats.optBoolean("_eyelesssight"));
        sModel.setFlight(basestats.optBoolean("_flight"));
        sModel.setGunfighter(basestats.optBoolean("_gunfighter"));
        sModel.setIncorporeal(basestats.optBoolean("_incorporeal"));
        sModel.setJackMarshal(basestats.optBoolean("_jack_marshal"));
        // sModel.setLesserWarlock(lesserWarlock));
        // sModel.setJourneyManWarcaster(journeyManWarcaster));
        sModel.setOfficer(basestats.optBoolean("_officer"));
        sModel.setParry(basestats.optBoolean("_parry"));
        sModel.setPathfinder(basestats.optBoolean("_pathfinder"));
        sModel.setSoulless(basestats.optBoolean("_soulless"));
        sModel.setStandardBearer(basestats.optBoolean("_standard_bearer"));
        sModel.setStealth(basestats.optBoolean("_stealth"));
        sModel.setTough(basestats.optBoolean("_tough"));
        sModel.setUndead(basestats.optBoolean("_undead"));


        sModel.setImmunityCorrosion(basestats.optBoolean("_immunity_corrosion"));
        sModel.setImmunityElectricity(basestats.optBoolean("_immunity_electricity"));
        sModel.setImmunityFire(basestats.optBoolean("_immunity_fire"));
        sModel.setImmunityFrost(basestats.optBoolean("_immunity_frost"));

        sModel.setFocus(basestats.optString("_foc"));
        sModel.setFury(basestats.optString("_fur"));
        sModel.setThreshold(extractFromString(basestats.optString("_thr")));
        
        
        
        String hitpoints =  basestats.optString("_hitpoints");
        if (extractFromString(hitpoints) == 0) {
            sModel.setHitpoints(new MonoPVDamage());
        } else {
            sModel.setHitpoints(new ModelDamageLine(extractFromString(hitpoints)));
        }


        String damage_grid = basestats.optString("_damage_grid");
        if (damage_grid != null && damage_grid.length() > 0) {

            DamageGrid grid;
            if (basestats.optBoolean("_myrmidon")) {
                grid = new MyrmidonDamageGrid(sModel);
            } else {
                grid = new WarjackDamageGrid(sModel);
            }
            grid.fromString(damage_grid);
            sModel.setHitpoints(grid);

            if (entry.getId().equals("KW05")) {
                // fucking karchev
                // ((Warjack) entry).setGrid(damage_grid);
            } else {
                ((Warjack) entry).setGrid(damage_grid);
            }


        }

        String l_damage_grid = basestats.optString("_damage_grid_left");
        String r_damage_grid = basestats.optString("_damage_grid_right");
        if (l_damage_grid != null && l_damage_grid.length() > 0) {
            ColossalDamageGrid grid = new ColossalDamageGrid(sModel);
            WarjackDamageGrid gridL = new WarjackDamageGrid(sModel);
            gridL.fromString(l_damage_grid);
            WarjackDamageGrid gridR = new WarjackDamageGrid(sModel);
            gridR.fromString(r_damage_grid);
            grid.setLeftGrid(gridL);
            grid.setRightGrid(gridR);
            sModel.setHitpoints(grid);

            if ( ((Colossal) entry).isMyrmidon())  {
                ((Colossal) entry).setForceField(12);
                ModelDamageLine forceFieldGrid = new ModelDamageLine(12, 0);
                grid.setForceFieldGrid(forceFieldGrid);
            }
            ((Colossal) entry).setLeftGrid(l_damage_grid);
            ((Colossal) entry).setRightGrid(r_damage_grid);
        }


        String damage_spiral = basestats.optString("_damage_spiral");
        if (damage_spiral != null && damage_spiral.length() > 0) {
            DamageGrid spiral = new WarbeastDamageSpiral(sModel);
            spiral.fromString(damage_spiral);
            sModel.setHitpoints(spiral);
            ((Warbeast) entry).setGrid(damage_spiral);
        }



        JSONArray meleeWeapons = weapons.optJSONArray("melee_weapon");
        if (meleeWeapons != null && meleeWeapons.length() > 0) {
            for (int k = 0; k < meleeWeapons.length(); k++) {
                JSONObject meleeWeapon = meleeWeapons.getJSONObject(k);
                MeleeWeapon weapon = new MeleeWeapon();
                weapon.setName(meleeWeapon.optString("_name"));
                weapon.setRange(meleeWeapon.optString("_rng"));

                weapon.setPow(meleeWeapon.optString("_pow"));
                weapon.setP_plus_s(meleeWeapon.optString("_p_plus_s"));
                weapon.setChain(meleeWeapon.optBoolean("_chain"));
                weapon.setOpenFist(meleeWeapon.optBoolean("_open_fist"));

                extractWeaponSpecials(meleeWeapon, weapon);
                extractWeaponCapacities(meleeWeapon, weapon);

                sModel.getWeapons().add(weapon);
            }
        }

        JSONArray rangedWeapons = weapons.optJSONArray("ranged_weapon");
        if (rangedWeapons != null && rangedWeapons.length() > 0) {
            for (int k = 0; k < rangedWeapons.length(); k++) {
                JSONObject rangedWeapon = rangedWeapons.getJSONObject(k);
                RangedWeapon weapon = new RangedWeapon();
                weapon.setName(rangedWeapon.optString("_name"));
                weapon.setRange(rangedWeapon.optString("_rng"));
                weapon.setRof(rangedWeapon.optString("_rof"));
                weapon.setAoe(rangedWeapon.optString("_aoe"));
                weapon.setPow(rangedWeapon.optString("_pow"));

                extractWeaponSpecials(rangedWeapon, weapon);
                extractWeaponCapacities(rangedWeapon, weapon);

                sModel.getWeapons().add(weapon);
            }
        }

        JSONArray mountWeapons = weapons.optJSONArray("mount_weapon");
        if (mountWeapons != null && mountWeapons.length() > 0) {
            for (int k = 0; k < mountWeapons.length(); k++) {
                JSONObject mountWeapon = mountWeapons.getJSONObject(k);
                MountWeapon weapon = new MountWeapon();
                weapon.setName(mountWeapon.optString("_name"));
                weapon.setRange(mountWeapon.optString("_rng"));
                weapon.setPow(mountWeapon.optString("_pow"));

                extractWeaponCapacities(mountWeapon, weapon);

                sModel.getWeapons().add(weapon);
            }
        }

        for (int cap = 0; cap < capacities.length(); cap++) {
            JSONObject jsonCapacity = capacities.optJSONObject(cap);


            JSONObject id = jsonCapacity.optJSONObject("_id");
            String oid = id.getString("$oid");

            if (capacitiesMap.containsKey(oid)) {
                Capacity capaFound = capacitiesMap.get(oid);
                sModel.getCapacities().add(capaFound);
                // Log.d(TAG, "model capa substitued");
            } else {
                Capacity capacity = new Capacity();
                capacity.setTitle(jsonCapacity.optString("_title"));
                capacity.setLabel(jsonCapacity.optString("__text"));
                capacity.setType(jsonCapacity.optString("_type"));
                sModel.getCapacities().add(capacity);
                Log.e(TAG, "model capa *NOT* substitued");
            }

        }

        if (spells != null) {
            for (int sp = 0; sp < spells.length(); sp++) {

                JSONObject jsonSpell= spells.optJSONObject(sp);
                JSONObject id = jsonSpell.optJSONObject("_id");
                String oid = id.getString("$oid");

                if (spellsMap.containsKey(oid)) {
                    Spell spellFound = spellsMap.get(oid);
                    sModel.getSpells().add(spellFound);
//                    Log.d(TAG, "model spell substitued");
                } else {
                    Spell spell = new Spell();
                    spell.setTitle(jsonSpell.optString("_name"));
                    spell.setCost(jsonSpell.optString("_cost"));
                    spell.setRange(jsonSpell.optString("_rng"));
                    spell.setAoe(jsonSpell.optString("_aoe"));
                    spell.setPow(jsonSpell.optString("_pow"));
                    spell.setDuration(jsonSpell.optString("_duration"));
                    spell.setOffensive(jsonSpell.optString("_off"));
                    spell.setFullText(jsonSpell.optString("__text"));
                    sModel.getSpells().add(spell);
                    Log.e(TAG, "model spell *NOT* substitued");
                }
            }
        }

    }

    private void extractWeaponCapacities(JSONObject jsonWeapon, Weapon weapon) {
        JSONArray capacities = jsonWeapon.optJSONArray("capacities");
        if (capacities != null) {
            for (int cap = 0; cap < capacities.length(); cap++) {
                JSONObject jsonCapacity = capacities.optJSONObject(cap);

                JSONObject id = jsonCapacity.optJSONObject("_id");
                String oid = id.getString("$oid");

                if (capacitiesMap.containsKey(oid)) {
                    Capacity capaFound = capacitiesMap.get(oid);
                    weapon.getCapacities().add(capaFound);
//                Log.d(TAG, "weapon capa substitued");
                } else {
                    Capacity capacity = new Capacity();
                    capacity.setTitle(jsonCapacity.optString("_title"));
                    capacity.setLabel(jsonCapacity.optString("__text"));
                    capacity.setType(jsonCapacity.optString("_type"));
                    weapon.getCapacities().add(capacity);
                }
            }
        }
    }

    private void extractWeaponSpecials(JSONObject jsonWeapon, Weapon weapon) {
        weapon.setBlessed(jsonWeapon.optBoolean("_blessed"));

        weapon.setBuckler(jsonWeapon.optBoolean("_buckler"));
        weapon.setCorrosion(jsonWeapon.optBoolean("_corrosion"));
        weapon.setContinuousCorrosion(jsonWeapon.optBoolean("_continuous_corrosion"));
        weapon.setCriticalCorrosion(jsonWeapon.optBoolean("_critical_corrosion"));
        weapon.setDisrupt(jsonWeapon.optBoolean("_disrupt"));
        weapon.setCriticalDisrupt(jsonWeapon.optBoolean("_critical_disrupt"));
        weapon.setElectricity(jsonWeapon.optBoolean("_electricity"));
        weapon.setFire(jsonWeapon.optBoolean("_fire"));
        weapon.setContinuousFire(jsonWeapon.optBoolean("_continuous_fire"));
        weapon.setCriticalFire(jsonWeapon.optBoolean("_critical_fire"));
        weapon.setMagical(jsonWeapon.optBoolean("_magical"));
        weapon.setShield(jsonWeapon.optBoolean("_shield"));
        weapon.setWeaponMaster(jsonWeapon.optBoolean("_weapon_master"));



        weapon.setLocation(jsonWeapon.optString("_location"));
        weapon.setCount(jsonWeapon.optInt("_count"));

    }
}
