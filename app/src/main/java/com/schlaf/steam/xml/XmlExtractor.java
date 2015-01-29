/**
 * 
 */
package com.schlaf.steam.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.data.ArmyCommander;
import com.schlaf.steam.data.ArmyCommander.Generation;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.Capacity;
import com.schlaf.steam.data.Colossal;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.Faction;
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
import com.schlaf.steam.data.UnitAttachment;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.WarbeastPack;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.Warlock;
import com.schlaf.steam.data.Weapon;
import com.schlaf.steam.data.WeaponAttachment;
import com.schlaf.steam.storage.StorageManager;

/**
 * @author S0085289
 * 
 */
public class XmlExtractor {
	
	private boolean doLog = false;
	

	private static final String WARBEAST_POINTS_ATTRIBUTE = "warbeast_points";

	private static final String FOCUS_ATTRIBUTE = "focus";

	private static final String WARJACK_POINTS_ATTRIBUTE = "warjack_points";

	private static final String GENERATION_ATTRIBUTE = "generation";

	private static final String TAG = "XmlExtractor";

	private static final String WEAPON_MASTER_ATTRIBUTE = "weapon_master";
	private static final String JACK_MARSHAL = "jack_marshal";
	private static final String JOURNEYMAN_WARCASTER = "journeyman_warcaster";
	private static final String LESSER_WARLOCK = "lesser_warlock";
	private static final String FACTIONS_TAG = "factions";
	private static final String FACTION_TAG = "faction";
	private static final String ARMIES_TAG = "armies";
	private static final String ARMY_TAG = "army";
	private static final String WARCASTERS_TAG = "warcasters";
	private static final String WARCASTER_TAG = "warcaster";
	private static final String WARJACKS_TAG = "warjacks";
	private static final String WARJACK_TAG = "warjack";
	private static final String WARLOCKS_TAG = "warlocks";
	private static final String WARLOCK_TAG = "warlock";
	private static final String WARBEASTS_TAG = "warbeasts";
	private static final String WARBEAST_TAG = "warbeast";
	private static final String COLOSSALS_TAG = "colossals";
	private static final String COLOSSAL_TAG = "colossal";
	private static final String BATTLE_ENGINES_TAG = "battleEngines";
	private static final String BATTLE_ENGINE_TAG = "battleEngine";
	private static final String UNITS_TAG = "units";
	private static final String UNIT_TAG = "unit";
	private static final String UA_TAG = "unit_attachment";
	private static final String WA_TAG = "weapon_attachment";
	private static final String MODEL_TAG = "model";
	private static final String BASESTATS_TAG = "basestats";
	private static final String SOLOS_TAG = "solos";
	private static final String SOLO_TAG = "solo";
	
	private static final String ID_TAG = "id";
	private static final String NAME_TAG = "name";
	private static final String FULL_NAME_TAG = "full_name";
	private static final String QUALIFICATION_TAG = "qualification";
	private static final String FA_TAG = "fa";
	private static final String COMPLETED_TAG = "completed";
	private static final String COST_TAG = "cost";
	private static final String CAPACITY_TAG = "capacity";
	private static final String CAPACITY_TITLE_TAG = "title";
	private static final String CAPACITY_TYPE_TAG = "type";
	
	private static final String WORKS_FOR_TAG = "works_for";
	private static final String WORKS_FOR_FACTION_ID_ATTRIBUTE = "id";
	
	private static final String RESTRICTED_TO_TAG = "restricted_to";
	private static final String RESTRICTED_TO_ID_ATTRIBUTE = "id";
	
	private static final String ONLY_IN_TIER_TAG = "only_in_tier";
	private static final String ONLY_IN_TIER_ID_ATTRIBUTE = "id";
	
	private static final String COMPANION_ID_ATTRIBUTE = "companionId";
	
	private static final String SPELL_TAG = "spell";
	private static final String ANIMUS_TAG = "animus";
	private static final String SPELL_TITLE_TAG = "name";
	private static final String SPELL_COST_TAG = "cost";
	private static final String SPELL_RANGE_TAG = "rng";
	private static final String SPELL_AOE_TAG = "aoe";
	private static final String SPELL_POW_TAG = "pow";
	private static final String SPELL_UP_TAG = "up";
	private static final String SPELL_OFF_TAG = "off";
	
	
	
	// feat
	private static final String FEAT_TAG = "feat";
	
	private static final String FULL_TEXT_TAG = "full_text";
	
	// base stats
	private static final String SPD_ATTRIBUTE = "spd";
	private static final String STR_ATTRIBUTE = "str";
	private static final String MAT_ATTRIBUTE = "mat";
	private static final String RAT_ATTRIBUTE = "rat";
	private static final String DEF_ATTRIBUTE = "def";
	private static final String ARM_ATTRIBUTE = "arm";
	private static final String CMD_ATTRIBUTE = "cmd";
	
	private static final String IS_MYRMIDON_ATTRIBUTE = "is_myrmidon"; 
	private static final String DAMAGE_GRID_ATTRIBUTE = "damage_grid";
	private static final String DAMAGE_GRID_LEFT_ATTRIBUTE = "damage_grid_left";
	
	private static final String DAMAGE_GRID_RIGHT_ATTRIBUTE = "damage_grid_right";
	
	private static final String DAMAGE_SPIRAL_ATTRIBUTE = "damage_spiral";
	private static final String FURY_ATTRIBUTE = "fury";
	private static final String THRESHOLD_ATTRIBUTE = "threshold";
	private static final String IS_WARBEASTPACK = "isWarbeastPack";
	private static final String NB_MODELS_IN_PACK = "nbModelsInPack";
	
	// weapons
	private static final String WEAPONS_TAG = "weapons";
	private static final String MELEE_WEAPON_TAG = "melee_weapon";
	private static final String COUNT_ATTRIBUTE = "count";
	private static final String RANGED_WEAPON_TAG = "ranged_weapon";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String POW_ATTRIBUTE = "pow";
	private static final String P_PLUS_S_ATTRIBUTE = "p_plus_s";
	private static final String RNG_ATTRIBUTE = "rng";
	private static final String ROF_ATTRIBUTE = "rof";
	private static final String AOE_ATTRIBUTE = "aoe";
	private static final String MOUNT_WEAPON_TAG = "mount_weapon";
	
	private static final String MAGICAL_ATTRIBUTE = "magical";
	private static final String REACH_ATTRIBUTE = "reach";
	private static final String SHIELD_ATTRIBUTE = "shield";
	private static final String OPENFIST_ATTRIBUTE = "open_fist";
	private static final String LOCATION_ATTRIBUTE = "location";
	
	
	private static final String FIRE_ATTRIBUTE = "fire";
	private static final String CRITICAL_FIRE_ATTRIBUTE = "critical_fire";
	private static final String CORROSION_ATTRIBUTE = "corrosion";
	private static final String CRITICAL_CORROSION_ATTRIBUTE = "critical_corrosion";
	private static final String FROST_ATTRIBUTE = "frost";
	private static final String ELECTRICITY_ATTRIBUTE = "electricity";

	private static final String CONTINUOUS_FIRE_ATTRIBUTE = "continuous_fire";
	private static final String CONTINUOUS_CORROSION_ATTRIBUTE = "continuous_corrosion";

	private static final String CHAIN_ATTRIBUTE = "chain";

	private static final String BUCKLER_ATTRIBUTE = "buckler";
	
//	int[] XML_FILES = new int[] {R.xml.cygnar_completed_angus, R.xml.khador_v3_1, R.xml.menoth_08_06_14, R.xml.cryx_10_06_14,
//			R.xml.retribution_complete, R.xml.cyriss_completed, R.xml.mercenaries_completed ,
//			R.xml.orboros_09_10_14, R.xml.everblight_09_10_14, R.xml.skorne_09_10_14, R.xml.trollbloods_completed, R.xml.minions_09_10_14, R.xml.objectives};

	int[] XML_FILES = new int[] {R.xml.cygnar, R.xml.khador, R.xml.menoth, R.xml.cryx,
			R.xml.retribution, R.xml.cyriss, R.xml.mercenaries_corrected,
			R.xml.orboros, R.xml.everblight, R.xml.skorne, R.xml.trollbloods, R.xml.minions, R.xml.objectives	};

	/** access to local resources */
	Resources res;
	SteamPunkRosterApplication parentApplication;

	public XmlExtractor(Resources res,
			SteamPunkRosterApplication parentApplication) {
		// initial treatment?
		this.res = res;
		this.parentApplication = parentApplication;
	}

	public void doExtract() {

		XmlResourceParser xppFactions = res.getXml(R.xml.factions);

		extractFactions(xppFactions);

		for (int resource : XML_FILES) {
			extractArmies(resource);
		}

		if (doLog) { Log.d (TAG,"extraction done");}

		ArmySingleton.getInstance().computeArmyElements();
		
		
	}

	private void extractArmies(int resourceFileId) {
		try {
			
			XmlResourceParser xppArmies = res.getXml(resourceFileId);
			xppArmies.next();
			int eventType = xppArmies.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (ARMIES_TAG.equals(xppArmies.getName())) {
						loadArmies(xppArmies);
					}
				} 
				eventType = xppArmies.next();
			}
			
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean extractImportedFile(Context applicationContext, File content) {
		
		InputStream is;
		try {
			is = new FileInputStream(content);

			XmlPullParserFactory factory = XmlPullParserFactory
					.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(is, "UTF-8");
			xpp.next();
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (ARMIES_TAG.equals(xpp.getName())) {
						loadArmies(xpp);
					}
				} 
				eventType = xpp.next();
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			
		}
		
		ArmySingleton.getInstance().recomputeArmyElements();
		return true;

	}
	
	public void extractImportedArmies(Context applicationContext, boolean verbose) {
		
		File importFilesDir = applicationContext.getDir(StorageManager.IMPORT_FILES_DIR, Context.MODE_PRIVATE);
		
		File[] files = importFilesDir.listFiles();
		
		// import files from oldest to newest
		Arrays.sort( files, new Comparator<File>() {
		    public int compare(File f1, File f2) {
		    	return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
		    }
		}); 
		
		
		for (File importFile : files) {

            String extension = StorageManager.extractFileExtension(importFile);

			if (StorageManager.WHAC_EXTENSION.equalsIgnoreCase(extension)) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(importFile);

					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();

					xpp.setInput(fis, "UTF-8");
					xpp.next();
					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							if (ARMIES_TAG.equals(xpp.getName())) {
								loadArmies(xpp);
								if (verbose) {
									Toast.makeText(applicationContext, "Data file correctly imported : " + importFile.getName(), Toast.LENGTH_SHORT).show();
								}
							}
						} 
						eventType = xpp.next();
					}


				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "deleting corrupted imported data file : " + importFile.getName());
					importFile.delete();
					if (verbose) {
						Toast.makeText(applicationContext, "Data file import failed : " + importFile.getName() + " -- this file will be deleted!", Toast.LENGTH_SHORT).show();
					}
				} finally {
				}				
			}

		}
		
		ArmySingleton.getInstance().recomputeArmyElements();

	}

	private void extractFactions(XmlResourceParser xppFactions) {
		try {
			xppFactions.next();
			int eventType = xppFactions.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (FACTIONS_TAG.equals(xppFactions.getName())) {
						loadFactions(xppFactions);
					}
				} 
				eventType = xppFactions.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * charge la partie "armies" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadArmies(XmlPullParser xpp) {
		if (doLog) { Log.d (TAG,"loadArmies - start");}
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && ARMIES_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (ARMY_TAG.equals(xpp.getName())) {
						loadArmy(xpp);
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doLog) { Log.d (TAG,"loadArmies - end");}
	}

	/**
	 * charge la partie "factions" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadFactions(XmlResourceParser xpp) {
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && FACTIONS_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (FACTION_TAG.equals(xpp.getName())) {
						Faction faction = loadFaction(xpp);
						ArmySingleton.getInstance().getFactions().put(faction.getId(),
								faction);
					} else {
						// cagade!
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * charge la partie "faction" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 */
	private Faction loadFaction(XmlResourceParser xpp) {
		Faction faction = new Faction();
		String id = xpp.getAttributeValue(null, "id");
		String name = xpp.getAttributeValue(null, "name");
		String fullName = xpp.getAttributeValue(null, "full_name");
		String system = xpp.getAttributeValue(null, "system");
		faction.setId(id);
		faction.setName(name);
		faction.setFullName(fullName);
		faction.setSystem(Faction.GameSystem.valueOf(system));
		return faction;
	}

	/**
	 * charge la partie "army" du fichier xml é partir du parser positionné sur
	 * le tag de début d'une army
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private void loadArmy(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		if (doLog) { Log.d (TAG,"loadArmy - start");}
		String faction_id = xpp.getAttributeValue(null, "faction");

		Faction faction = ArmySingleton.getInstance().getFactions().get(faction_id);
		
		//xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && ARMY_TAG
				.equals(xpp.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (WARCASTERS_TAG.equals(xpp.getName())) {
					loadWarcasters(xpp, faction);
				} else if (WARLOCKS_TAG.equals(xpp.getName())) {
					loadWarlocks(xpp, faction);
				}  else if (WARJACKS_TAG.equals(xpp.getName())) {
					loadWarjacks(xpp, faction);
				} else if (WARBEASTS_TAG.equals(xpp.getName())) {
					loadWarbeasts(xpp, faction);
				} else if (COLOSSALS_TAG.equals(xpp.getName())) {
					loadColossals(xpp, faction);
				} else if (BATTLE_ENGINES_TAG.equals(xpp.getName())) {
					loadBattleEngines(xpp, faction);
				}  else if (UNITS_TAG.equals(xpp.getName())) {
					loadUnits(xpp, faction);
				} else if (SOLOS_TAG.equals(xpp.getName())) {
					loadSolos(xpp, faction);
				}
			}
			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"loadArmy - end");}
	}

	/**
	 * charge la partie "warcasters" du fichier xml é partir du parser
	 * positionné sur le tag de début
	 * 
	 * @param xpp
	 * @param faction 
	 * @throws org.xmlpull.v1.XmlPullParserException
	 * @throws java.io.IOException
	 */
	private void loadWarcasters(XmlPullParser xpp, Faction faction) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadWarcasters - start");}
		// xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARCASTERS_TAG
				.equals(xpp.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (WARCASTER_TAG.equals(xpp.getName())) {
					Warcaster caster = loadWarcaster(xpp);
					caster.setFaction(faction.getEnumValue());
					faction.getCasters().put(caster.getId(), caster);
				} else {
					// cagade!
				}
			}
			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"loadWarcasters - end");}
	}


	/**
	 * charge la partie "Warlocks" du fichier xml é partir du parser
	 * positionné sur le tag de début
	 * 
	 * @param xpp
	 * @param faction 
	 * @throws org.xmlpull.v1.XmlPullParserException
	 * @throws java.io.IOException
	 */
	private void loadWarlocks(XmlPullParser xpp, Faction faction) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadWarlocks - start");}
		// xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARLOCKS_TAG
				.equals(xpp.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (WARLOCK_TAG.equals(xpp.getName())) {
					Warlock warlock = loadWarlock(xpp);
					warlock.setFaction(faction.getEnumValue());
					faction.getWarlocks().put(warlock.getId(), warlock);
				} else {
					// cagade!
				}
			}
			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"loadWarlocks - end");}
	}

	/**
	 * charge la partie "warcasters" du fichier xml é partir du parser
	 * positionné sur le tag de début
	 * 
	 * @param xpp
	 * @param faction 
	 * @throws org.xmlpull.v1.XmlPullParserException
	 * @throws java.io.IOException
	 */
	private void loadUnits(XmlPullParser xpp, Faction faction) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadUnits- start");}
		// xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && UNITS_TAG
				.equals(xpp.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (UNIT_TAG.equals(xpp.getName())) {
					Unit unit = loadUnit(xpp);
					unit.setFaction(faction.getEnumValue());
					faction.getUnits().put(unit.getId(), unit);
					if (unit.getUnitAttachment() !=null) {
						unit.getUnitAttachment().setFaction(faction.getEnumValue());
					}
					if (unit.getWeaponAttachment() != null) {
						unit.getWeaponAttachment().setFaction(faction.getEnumValue());
					}
				} else {
					// cagade!
				}
			}
			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"loadUnits - end");}
	}
	
	/**
	 * charge la partie "warcasters" du fichier xml é partir du parser
	 * positionné sur le tag de début
	 * 
	 * @param xpp
	 * @param faction 
	 * @throws org.xmlpull.v1.XmlPullParserException
	 * @throws java.io.IOException
	 */
	private void loadSolos(XmlPullParser xpp, Faction faction) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadSolos- start");}
		// xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && SOLOS_TAG
				.equals(xpp.getName()))) {
			// until the end of tag solos
			if (eventType == XmlPullParser.START_TAG) {
				if (SOLO_TAG.equals(xpp.getName())) {
					Solo solo = loadSolo(xpp);
					solo.setFaction(faction.getEnumValue());
					faction.getSolos().put(solo.getId(), solo);
				} else {
					// cagade!
				}
			}
			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"loadSolos - end");}
	}
	
	/**
	 * charge la partie "warjacks" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadWarjacks(XmlPullParser xpp, Faction faction) {
		if (doLog) { Log.d (TAG,"loadWarjacks - start");}
		try {
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && WARJACKS_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (WARJACK_TAG.equals(xpp.getName())) {
						Warjack jack = loadWarjack(xpp);
						jack.setFaction(faction.getEnumValue());
						faction.getJacks().put(jack.getId(), jack);
					} else {
						// cagade!
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doLog) { Log.d (TAG,"loadWarjacks - end");}

	}

	private void loadWarbeasts(XmlPullParser xpp, Faction faction) {
		if (doLog) { Log.d (TAG,"loadWarbeasts - start");}
		try {
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && WARBEASTS_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (WARBEAST_TAG.equals(xpp.getName())) {
						Warbeast beast = loadWarbeast(xpp);
						beast.setFaction(faction.getEnumValue());
						faction.getBeasts().put(beast.getId(), beast);
					} else {
						// cagade!
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doLog) { Log.d (TAG,"loadWarbeasts - end");}

	}

	
	/**
	 * charge la partie "battle engine" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadBattleEngines(XmlPullParser xpp, Faction faction) {
		if (doLog) { Log.d (TAG,"loadBattleEngines - start");}
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && BATTLE_ENGINES_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (BATTLE_ENGINE_TAG.equals(xpp.getName())) {
						BattleEngine be = loadBattleEngine(xpp);
						be.setFaction(faction.getEnumValue());
						faction.getBattleEngines().put(be.getId(), be);
					} else {
						// cagade!
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doLog) { Log.d (TAG,"loadBattleEngines - end");}

	}

	/**
	 * charge la partie "warjacks" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadColossals(XmlPullParser xpp, Faction faction) {
		if (doLog) { Log.d (TAG,"loadColossals - start");}
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && COLOSSALS_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (COLOSSAL_TAG.equals(xpp.getName())) {
						Colossal colossal = loadColossal(xpp);
						colossal.setFaction(faction.getEnumValue());
						faction.getJacks().put(colossal.getId(), colossal);
					} else {
						// cagade!
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doLog) { Log.d (TAG,"loadColossals - end");}

	}	
	/**
	 * charge la partie "faction" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Warcaster loadWarcaster(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadWarcaster - start");}
		
		Warcaster caster = new Warcaster();
		loadElementId(xpp, caster);

		String generation = xpp.getAttributeValue(null, GENERATION_ATTRIBUTE);
		String generationId = xpp.getAttributeValue(null, "generation_id");
		String warjackPoints = xpp.getAttributeValue(null, WARJACK_POINTS_ATTRIBUTE);
		String focus = xpp.getAttributeValue(null, FOCUS_ATTRIBUTE);
		String companionId = xpp.getAttributeValue(null, COMPANION_ID_ATTRIBUTE);
		
		caster.setGeneration(Generation.valueOf(generation));
		caster.setGenerationId(generationId);
		caster.setWarjackPoints(extractFromString(warjackPoints));
		caster.setFocus(extractFromString(focus));
		caster.setCompanionId(companionId);
		
		SingleModel model = new SingleModel();
		caster.getModels().add(model);
		
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp,model);
		


		
		

		// load spells and capacities
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARCASTER_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && FEAT_TAG.equals(xpp.getName())) {
				loadFeat(xpp,caster);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}
			if (eventType == XmlPullParser.START_TAG && FULL_TEXT_TAG.equals(xpp.getName())) {
				loadFullText(xpp, caster);
			}


			
			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && SPELL_TAG.equals(xpp.getName())) {
				Spell spell = loadSpell(xpp);
				caster.getSpells().add(spell);
			}
			if (eventType == XmlPullParser.START_TAG && MODEL_TAG.equals(xpp.getName())) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				caster.getModels().add(additionnalModel);
			}
			
			if (eventType == XmlPullParser.START_TAG && WORKS_FOR_TAG.equals(xpp.getName())) {
				loadWorksFor(xpp, caster);
			}
			
			eventType = xpp.next();
		}

		if (doLog) { Log.d (TAG,"loadWarcaster - end");}
		return caster;
	}
	
	/**
	 * charge la partie "faction" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Warlock loadWarlock(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadWarlock - start");}
		
		Warlock warlock = new Warlock();
		loadElementId(xpp, warlock);

		String generation = xpp.getAttributeValue(null, GENERATION_ATTRIBUTE);
		String generationId = xpp.getAttributeValue(null, "generation_id");
		String warbeastPoints = xpp.getAttributeValue(null, WARBEAST_POINTS_ATTRIBUTE);
		String fury = xpp.getAttributeValue(null, FURY_ATTRIBUTE);
		String companionId = xpp.getAttributeValue(null, COMPANION_ID_ATTRIBUTE);
		
		warlock.setGeneration(com.schlaf.steam.data.ArmyCommander.Generation.valueOf(generation));
		warlock.setGenerationId(generationId);
		warlock.setWarbeastPoints(extractFromString(warbeastPoints));
		warlock.setFury(extractFromString(fury));
		warlock.setCompanionId(companionId);
		
		SingleModel model = new SingleModel();
		warlock.getModels().add(model);
		
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp,model);
//		loadFeat(xpp,warlock);

		// load spells and capacities
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARLOCK_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && FEAT_TAG.equals(xpp.getName())) {
				loadFeat(xpp,warlock);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}
			
			if (eventType == XmlPullParser.START_TAG && FULL_TEXT_TAG.equals(xpp.getName())) {
				loadFullText(xpp, warlock);
			}

			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && SPELL_TAG.equals(xpp.getName())) {
				Spell spell = loadSpell(xpp);
				warlock.getSpells().add(spell);
			}
			if (eventType == XmlPullParser.START_TAG && MODEL_TAG.equals(xpp.getName())) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				warlock.getModels().add(additionnalModel);
			}
			
			if (eventType == XmlPullParser.START_TAG && RESTRICTED_TO_TAG.equals(xpp.getName())) {
				loadRestrictedTo(xpp, warlock);
			}

			
			if (eventType == XmlPullParser.START_TAG && WORKS_FOR_TAG.equals(xpp.getName())) {
				loadWorksFor(xpp, warlock);
			}
			
			eventType = xpp.next();
		}

		if (doLog) { Log.d (TAG,"loadWarlock - end");}
		return warlock;
	}
	
	/**
	 * charge la partie "battle engine" du fichier xml é partir du parser positionné
	 * sur le tag de début d'un solo
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private BattleEngine loadBattleEngine(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadBattleEngine - start");}
		
		BattleEngine be = new BattleEngine();
		loadElementId(xpp, be);

		String cost = xpp.getAttributeValue(null, COST_TAG);
		be.setBaseCost(extractFromString(cost));
		
		SingleModel model = new SingleModel();
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp,model);
		be.getModels().add(model);
		
		loadFullText(xpp,be);

		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && BATTLE_ENGINE_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}
			
			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}

			if (eventType == XmlPullParser.START_TAG && WORKS_FOR_TAG.equals(xpp.getName())) {
				loadWorksFor(xpp, be);
			}

			if (eventType == XmlPullParser.START_TAG && MODEL_TAG.equals(xpp.getName())) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				be.getModels().add(additionnalModel);
			}
			
			eventType = xpp.next();
		}

		if (doLog) { Log.d (TAG,"loadBattleEngine - end");}
		return be;
	}	

	

	/**
	 * charge la partie "solo" du fichier xml é partir du parser positionné
	 * sur le tag de début d'un solo
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Solo loadSolo(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadSolo - start");}
		
		Solo solo = new Solo();
		loadElementId(xpp, solo);

		String cost = xpp.getAttributeValue(null, COST_TAG);
		solo.setBaseCost(extractFromString(cost));
		
		String warcaster_attachment = xpp.getAttributeValue(null, "warcaster_attachment");
		String mercenary_attachment = xpp.getAttributeValue(null, "mercenary_attachment");
		String unit_attachment = xpp.getAttributeValue(null, "unit_attachment");
		String weapon_attachment = xpp.getAttributeValue(null, "weapon_attachment");
		solo.setWarcasterAttached(Boolean.valueOf(warcaster_attachment));
		solo.setMercenaryUnitAttached(Boolean.valueOf(mercenary_attachment));
		solo.setGenericUnitAttached(Boolean.valueOf(unit_attachment));
		solo.setWeaponAttachement(Boolean.valueOf(weapon_attachment));
		
		String dragoon = xpp.getAttributeValue(null, "dragoon");
		solo.setDragoon(Boolean.valueOf(dragoon));
		
		String dismountOption = xpp.getAttributeValue(null, "dismount_option");
		solo.setDismountOption(Boolean.valueOf(dismountOption));
		
		String dismountCost = xpp.getAttributeValue(null, "dismount_cost");
		
		String companionId = xpp.getAttributeValue(null, COMPANION_ID_ATTRIBUTE);
		solo.setCompanionId(companionId);
		
		SingleModel model = new SingleModel();
		solo.getModels().add(model);
		
		
		
		loadFullText(xpp,solo);

		ArrayList<Spell> spells = new ArrayList<Spell>();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && SOLO_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}

			if (eventType == XmlPullParser.START_TAG && SPELL_TAG.equals(xpp.getName())) {
				Spell spell = loadSpell(xpp);
				spells.add(spell);
			}
			
			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}

			if (eventType == XmlPullParser.START_TAG && WORKS_FOR_TAG.equals(xpp.getName())) {
				loadWorksFor(xpp, solo);
			}
			
			if (eventType == XmlPullParser.START_TAG && RESTRICTED_TO_TAG.equals(xpp.getName())) {
				loadRestrictedTo(xpp, solo);
			}
			
			if (eventType == XmlPullParser.START_TAG && ONLY_IN_TIER_TAG.equals(xpp.getName())) {
				loadOnlyInTier(xpp, solo);
			}


			if (eventType == XmlPullParser.START_TAG && MODEL_TAG.equals(xpp.getName())) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				if (solo.isDragoon()) {
					if (solo.isDismountOption()) {
						solo.setDismountCost(extractFromString(dismountCost));
					}
				} 
				solo.getModels().add(additionnalModel);
			}
			
			eventType = xpp.next();
		}
		
		solo.setSpells(spells);

		if (doLog) { Log.d (TAG,"loadSolo - end");}
		return solo;
	}	
	
	/**
	 * charge la partie "faction" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private SingleModel loadSingleModel(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadSingleModel - start");}
		
		SingleModel model = new SingleModel();
		
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && MODEL_TAG.equals(xpp
				.getName()))) {
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}
			
			// special capacities of current model
			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			
			eventType = xpp.next();
		}

		if (doLog) { Log.d (TAG,"loadSingleModel - end");}
		return model;
	}	
	
	private void loadElementId(XmlPullParser xpp, ArmyElement element) {
		String id = xpp.getAttributeValue(null, ID_TAG);
		String name = xpp.getAttributeValue(null, NAME_TAG);
		String fullName = xpp.getAttributeValue(null, FULL_NAME_TAG);
		String qualification = xpp.getAttributeValue(null, QUALIFICATION_TAG);
		String fa = xpp.getAttributeValue(null, FA_TAG);
		String complete = xpp.getAttributeValue(null, COMPLETED_TAG);

		if (doLog) { Log.d ("XML extractor", "loadElementId - id = " + id + " - name = " + name);}
		
		element.setId(id);
		element.setName(name);
		element.setFullName(fullName);
		element.setQualification(qualification);
		element.setFaFromXml(fa);
		element.setCompleted(Boolean.valueOf(complete));

		
	}

	
	/**
	 * charge la partie "unit" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une unité
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Unit loadUnit(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadUnit - start");}
		
		Unit unit = new Unit();
		loadElementId(xpp, unit);
		String baseCost =  xpp.getAttributeValue(null, "base_cost");
		String fullCost =  xpp.getAttributeValue(null, "full_cost");
		String baseCount =  xpp.getAttributeValue(null, "baseCount");
		String fullCount =  xpp.getAttributeValue(null, "fullCount");
		String hasUA =  xpp.getAttributeValue(null, "has_unit_attachment");
		String hasWA =  xpp.getAttributeValue(null, "has_weapon_attachment");
		
		unit.setBaseCost(extractFromString(baseCost));
		unit.setBaseNumberOfModels(extractFromString(baseCount));
		if ("-".equals(fullCost)) {
			unit.setVariableSize(false); // one size fits all ;-)
			unit.setFullCost(extractFromString(baseCost));
			unit.setFullNumberOfModels(extractFromString(baseCount));
		} else {
			unit.setVariableSize(true); 
			unit.setFullCost(extractFromString(fullCost));	
			unit.setFullNumberOfModels(extractFromString(fullCount));
		}
		if (Boolean.valueOf(hasUA)) {
			unit.setUnitAttachmentAllowed(true);
		}
		if (Boolean.valueOf(hasWA)) {
			unit.setWeaponAttachmentAllowed(true);
		}
		
		SingleModel model = new SingleModel();
		unit.getModels().add(model);
		
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp, model);
//		loadFullText(xpp,unit);

		// loading the remains, depending of what we find
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && UNIT_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}

			// special capacities of current model
			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}

			if (eventType == XmlPullParser.START_TAG && WORKS_FOR_TAG.equals(xpp.getName())) {
				loadWorksFor(xpp, unit);
			}

			
			// si l'unité comprend des modéles variés, on les ajoute é la volée
			if (MODEL_TAG.equals(xpp.getName()) && eventType == XmlPullParser.START_TAG) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				unit.getModels().add(additionnalModel);
			}
			
			
			if (eventType == XmlPullParser.START_TAG
					&& UA_TAG.equals(xpp.getName())) {
				loadUA(xpp, unit);
			}
			
			if (eventType == XmlPullParser.START_TAG
					&& WA_TAG.equals(xpp.getName())) {
				loadWA(xpp, unit);
			}
			eventType = xpp.next();

		}

		if (model.isJackMarshal()) {
			unit.setJackMarshall(true);
		}
		
		// UA & WA works samely as unit!
		if (unit.getUnitAttachment() != null && ! unit.getAllowedFactionsToWorkFor().isEmpty()) {
			unit.getUnitAttachment().getAllowedFactionsToWorkFor().addAll(unit.getAllowedFactionsToWorkFor());
		}
		
		if (unit.getWeaponAttachment() != null && ! unit.getAllowedFactionsToWorkFor().isEmpty()) {
			unit.getWeaponAttachment().getAllowedFactionsToWorkFor().addAll(unit.getAllowedFactionsToWorkFor());
		}
		
		if (doLog) { Log.d (TAG,"loadUnit - end");}
		return unit;
	}	


	/**
	 * charge la partie "warjack" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Warjack loadWarjack(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadWarjack - start");}
		
		Warjack jack ;
		
		String is_myrmidon = xpp.getAttributeValue(null, IS_MYRMIDON_ATTRIBUTE);
		boolean myrmidon = Boolean.valueOf(is_myrmidon);
		if (myrmidon) {
			jack = new Myrmidon(); 
		} else {
			jack = new Warjack();
		}
		
		loadElementId(xpp, jack);
		
		String cost = xpp.getAttributeValue(null, COST_TAG);
		String damage_grid = xpp.getAttributeValue(null, DAMAGE_GRID_ATTRIBUTE);
		
		
		jack.setBaseCost(extractFromString(cost));
		jack.setGrid(damage_grid);

		SingleModel model = new SingleModel();
		
		jack.getModels().add(model);
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp, model);



		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARJACK_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}

			
			if (eventType == XmlPullParser.START_TAG && FULL_TEXT_TAG.equals(xpp.getName())) {
				loadFullText(xpp, jack);
			}

			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			
			if (eventType == XmlPullParser.START_TAG && RESTRICTED_TO_TAG.equals(xpp.getName())) {
				loadRestrictedTo(xpp, jack);
			}
			
			if (eventType == XmlPullParser.START_TAG && ONLY_IN_TIER_TAG.equals(xpp.getName())) {
				loadOnlyInTier(xpp, jack);
			}
			
			eventType = xpp.next();

		}
		
		DamageGrid grid ;
		if (myrmidon) {
			grid = new MyrmidonDamageGrid(model);
		} else {
			grid = new WarjackDamageGrid(model);
		}
		grid.fromString(damage_grid);
		model.setHitpoints(grid);

		if (doLog) { Log.d (TAG,"loadWarjack - end");}
		return jack;
	}	
	
	
	/**
	 * charge la partie "warbeast" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une faction
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Warbeast loadWarbeast(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadWarbeast - start");}
		
		Warbeast beast = new Warbeast();
		
		String isWarbeastPack = xpp.getAttributeValue(null, IS_WARBEASTPACK);
		boolean isPack = Boolean.valueOf(isWarbeastPack);
		if (isPack) {
			 beast = new WarbeastPack();	
		}
		
		loadElementId(xpp, beast);
		
		String cost = xpp.getAttributeValue(null, COST_TAG);
		String damage_spiral = xpp.getAttributeValue(null, DAMAGE_SPIRAL_ATTRIBUTE);
		String fury = xpp.getAttributeValue(null, FURY_ATTRIBUTE);
		String threshold = xpp.getAttributeValue(null, THRESHOLD_ATTRIBUTE);
		
		beast.setBaseCost(extractFromString(cost));
		if (!isPack) {
			beast.setGrid(damage_spiral);	
		}
		beast.setFury(extractFromString(fury));
		beast.setThreshold(extractFromString(threshold));
		
		if (isPack) {
			String nbInPack = xpp.getAttributeValue(null, NB_MODELS_IN_PACK);
			((WarbeastPack) beast).setNbModels(extractFromString(nbInPack));
		}

		SingleModel model = new SingleModel();
		
		beast.getModels().add(model);
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp, model);


		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && WARBEAST_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}

			
			if (eventType == XmlPullParser.START_TAG && FULL_TEXT_TAG.equals(xpp.getName())) {
				loadFullText(xpp, beast);
			}

			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			
			if (eventType == XmlPullParser.START_TAG && ANIMUS_TAG.equals(xpp.getName())) {
				Spell spell = loadAnimus(xpp);
				beast.setAnimus(spell);
			}
			
			
			if (eventType == XmlPullParser.START_TAG && RESTRICTED_TO_TAG.equals(xpp.getName())) {
				loadRestrictedTo(xpp, beast);
			}

			
			
			eventType = xpp.next();

		}
		
		if (isPack) {
			// the model has basic hitpoints, nothing to do
		} else {
			DamageGrid grid ;
			grid = new WarbeastDamageSpiral(model);
			grid.fromString(damage_spiral);
			model.setHitpoints(grid);
		}
		


		if (doLog) { Log.d (TAG,"loadWarbeast - end");}
		return beast;
	}		
	/**
	 * charge la partie "faction" du fichier xml é partir du parser positionné
	 * sur le tag de début d'un colosse
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private Colossal loadColossal(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		
		if (doLog) { Log.d (TAG,"loadColossal - start");}
		
		Colossal colossal = new Colossal();
		loadElementId(xpp, colossal);
		
		String cost = xpp.getAttributeValue(null, COST_TAG);
		String damage_grid_left = xpp.getAttributeValue(null, DAMAGE_GRID_LEFT_ATTRIBUTE);
		String damage_grid_right = xpp.getAttributeValue(null, DAMAGE_GRID_RIGHT_ATTRIBUTE);
		String is_myrmidon = xpp.getAttributeValue(null, IS_MYRMIDON_ATTRIBUTE);
		String force_field = xpp.getAttributeValue(null, "force_field");
		
		colossal.setBaseCost(extractFromString(cost));
		colossal.setRightGrid(damage_grid_right);
		colossal.setLeftGrid(damage_grid_left);
		
		colossal.setMyrmidon(Boolean.valueOf(is_myrmidon));
		if (colossal.isMyrmidon()) {
			colossal.setForceField(extractFromString(force_field));
		}
		

		SingleModel model = new SingleModel();
		
		colossal.getModels().add(model);
//		loadBaseStats(xpp, model);
//		loadWeapons(xpp, model);


		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && COLOSSAL_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				loadBaseStats(xpp, model);
			}
			if (eventType == XmlPullParser.START_TAG && WEAPONS_TAG.equals(xpp.getName())) {
				loadWeapons(xpp,model);
			}
			
			if (eventType == XmlPullParser.START_TAG && FULL_TEXT_TAG.equals(xpp.getName())) {
				loadFullText(xpp, colossal);
			}

			if (eventType == XmlPullParser.START_TAG && CAPACITY_TAG.equals(xpp.getName())) {
				loadModelCapacity(xpp, model);
			}
			
			if (eventType == XmlPullParser.START_TAG && RESTRICTED_TO_TAG.equals(xpp.getName())) {
				loadRestrictedTo(xpp, colossal);
			}
			
			if (eventType == XmlPullParser.START_TAG && ONLY_IN_TIER_TAG.equals(xpp.getName())) {
				loadOnlyInTier(xpp, colossal);
			}
			
			
			if (eventType == XmlPullParser.START_TAG && MODEL_TAG.equals(xpp.getName())) {
				SingleModel additionnalModel = loadSingleModel(xpp);
				colossal.getModels().add(additionnalModel);
			}

			eventType = xpp.next();
		}
		
		ColossalDamageGrid grid = new ColossalDamageGrid(model);
		WarjackDamageGrid leftGrid = new WarjackDamageGrid(model);
		leftGrid.fromString(damage_grid_left);
		WarjackDamageGrid rightGrid = new WarjackDamageGrid(model);
		rightGrid.fromString(damage_grid_right);
		grid.setLeftGrid(leftGrid);
		grid.setRightGrid(rightGrid);
		
		if (colossal.isMyrmidon()) {
			ModelDamageLine forceFieldGrid = new ModelDamageLine(colossal.getForceField(), 0);
			grid.setForceFieldGrid(forceFieldGrid);
		}
		
		model.setHitpoints(grid);


		if (doLog) { Log.d (TAG,"loadColossal - end");}
		return colossal;
	}		
	
	/**
	 * charge la partie "basestats" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadBaseStats(XmlPullParser xpp, SingleModel container)
			throws XmlPullParserException, IOException {

		int eventType = xpp.getEventType();
		
		while (!(eventType == XmlPullParser.END_TAG && BASESTATS_TAG.equals(xpp
				.getName()))) {
			//xpp.next();
			
			if (eventType == XmlPullParser.START_TAG && BASESTATS_TAG.equals(xpp.getName())) {
				String name= xpp.getAttributeValue(null, NAME_ATTRIBUTE);
				String spd = xpp.getAttributeValue(null, SPD_ATTRIBUTE);
				String str = xpp.getAttributeValue(null, STR_ATTRIBUTE);
				String mat = xpp.getAttributeValue(null, MAT_ATTRIBUTE);
				String rat = xpp.getAttributeValue(null, RAT_ATTRIBUTE);
				String def = xpp.getAttributeValue(null, DEF_ATTRIBUTE);
				String arm = xpp.getAttributeValue(null, ARM_ATTRIBUTE);
				String cmd = xpp.getAttributeValue(null, CMD_ATTRIBUTE);

				container.setName(name);
				container.setSPD(extractFromString(spd));
				container.setSTR(extractFromString(str));
				container.setMAT(extractFromString(mat));
				container.setRAT(extractFromString(rat));
				container.setDEF(extractFromString(def));
				container.setARM(extractFromString(arm));
				container.setCMD(extractFromString(cmd));
				
				
				// load hitpoints
				String hitpoints =  xpp.getAttributeValue(null, "hitpoints");
				if (extractFromString(hitpoints) == 0) {
					container.setHitpoints(new MonoPVDamage());
				} else {
					container.setHitpoints(new ModelDamageLine(extractFromString(hitpoints)));
				}
				

				// special karchev!
				String damage_grid = xpp.getAttributeValue(null, DAMAGE_GRID_ATTRIBUTE);
				if (damage_grid != null && damage_grid.length() > 0) {
					DamageGrid grid = new WarjackDamageGrid(container);
					grid.fromString(damage_grid);
					container.setHitpoints(grid);
				}
				
				
				
				// load special abilities
				String abomination =  xpp.getAttributeValue(null, "abomination");
				String advanceDeployment =  xpp.getAttributeValue(null, "advance_deployment");
				String arcNode =  xpp.getAttributeValue(null, "arc_node");
				String cra =  xpp.getAttributeValue(null, "cra");
				String cma =  xpp.getAttributeValue(null, "cma");
				String commander =  xpp.getAttributeValue(null, "commander");
				String construct =  xpp.getAttributeValue(null, "construct");
				String eyelessSight =  xpp.getAttributeValue(null, "eyelesssight");
				String fearless =  xpp.getAttributeValue(null, "fearless");
				String gunfighter =  xpp.getAttributeValue(null, "gunfighter");
				String incorporeal =  xpp.getAttributeValue(null, "incorporeal");
				String jackMarshal =  xpp.getAttributeValue(null, JACK_MARSHAL);
				String journeyManWarcaster =  xpp.getAttributeValue(null, JOURNEYMAN_WARCASTER);
				String lesserWarlock = xpp.getAttributeValue(null, LESSER_WARLOCK);
				String officer =  xpp.getAttributeValue(null, "officer");
				String pathfinder =  xpp.getAttributeValue(null, "pathfinder");
				String standardBearer =  xpp.getAttributeValue(null, "standard_bearer");
				String stealth =  xpp.getAttributeValue(null, "stealth");
				String terror =  xpp.getAttributeValue(null, "terror");
				String tough =  xpp.getAttributeValue(null, "tough");
				String undead =  xpp.getAttributeValue(null, "undead");
				
				String immunityFire =  xpp.getAttributeValue(null, "immunity_fire");
				String immunityFrost =  xpp.getAttributeValue(null, "immunity_frost");
				String immunityElectricity =  xpp.getAttributeValue(null, "immunity_electricity");
				String immunityCorrosion =  xpp.getAttributeValue(null, "immunity_corrosion");
				
				String focus = xpp.getAttributeValue(null, "focus");
				String fury = xpp.getAttributeValue(null, "fury");
				

				container.setAbomination(Boolean.valueOf(abomination));
				container.setAdvanceDeployment(Boolean.valueOf(advanceDeployment));
				container.setArcNode(Boolean.valueOf(arcNode));
				container.setCra(Boolean.valueOf(cra));
				container.setCma(Boolean.valueOf(cma));
				container.setCommander(Boolean.valueOf(commander));
				container.setConstruct(Boolean.valueOf(construct));
				container.setEyelessSight(Boolean.valueOf(eyelessSight));
				container.setFearless(Boolean.valueOf(fearless));
				container.setGunfighter(Boolean.valueOf(gunfighter));
				container.setIncorporeal(Boolean.valueOf(incorporeal));
				container.setJackMarshal(Boolean.valueOf(jackMarshal));
				container.setLesserWarlock(Boolean.valueOf(lesserWarlock));
				container.setJourneyManWarcaster(Boolean.valueOf(journeyManWarcaster));
				container.setOfficer(Boolean.valueOf(officer));
				container.setPathfinder(Boolean.valueOf(pathfinder));
				container.setStandardBearer(Boolean.valueOf(standardBearer));
				container.setStealth(Boolean.valueOf(stealth));
				container.setTerror(Boolean.valueOf(terror));
				container.setTough(Boolean.valueOf(tough));
				container.setUndead(Boolean.valueOf(undead));			
				
				container.setImmunityCorrosion(Boolean.valueOf(immunityCorrosion));
				container.setImmunityElectricity(Boolean.valueOf(immunityElectricity));
				container.setImmunityFire(Boolean.valueOf(immunityFire));
				container.setImmunityFrost(Boolean.valueOf(immunityFrost));
				
				container.setFocus(extractFromString(focus));
				container.setFury(extractFromString(fury));
				
			}
	
//			
//			// run to the end
//			while (!(eventType == XmlPullParser.END_TAG && BASESTATS_TAG
//					.equals(xpp.getName()))) {
//				xpp.next();
//				eventType = xpp.getEventType();
//			}
			
			eventType = xpp.next();
			//eventType = xpp.getEventType();
			
		}
	}
	
	/**
	 * charge la partie "feat" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadFeat(XmlPullParser xpp, ArmyCommander caster)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load feat - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& FEAT_TAG.equals(xpp.getName())) {
			String featTitle = xpp.getAttributeValue(null, "title");
			String feat = xpp.nextText();
			if (doLog) { Log.d (TAG,"feat = " + feat);}
			caster.setFeatTitle(featTitle);
			caster.setFeatContent(feat);

			// nextText() runs to the end of tag.. no need to iterate over
			
		}
	}	
	
	/**
	 * charge la partie "fullText" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadFullText(XmlPullParser xpp, ArmyElement element)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load full text - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& FULL_TEXT_TAG.equals(xpp.getName())) {
			String other = xpp.nextText();
			if (doLog) { Log.d (TAG,"other = " + other);}
			element.setCardFullText(other);
		}
		
	}
	
	/**
	 * charge la partie "basestats" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadWeapons(XmlPullParser xpp, SingleModel container)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load weapons - start");}
		int eventType = xpp.getEventType();
		
		Weapon currentWeapon = null;
		
		while (!(eventType == XmlPullParser.END_TAG && WEAPONS_TAG.equals(xpp
				.getName()))) {
			
			eventType = xpp.getEventType();
		
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (MELEE_WEAPON_TAG.equals(xpp.getName())) {
					MeleeWeapon weapon = loadMeleeWeapon(xpp);
					container.getWeapons().add(weapon);
					currentWeapon = weapon;
				} else if (RANGED_WEAPON_TAG.equals(xpp.getName())){
					RangedWeapon weapon = loadRangedWeapon(xpp);
					container.getWeapons().add(weapon);
					currentWeapon = weapon;
				} else if (MOUNT_WEAPON_TAG.equals(xpp.getName())){
					MountWeapon weapon = loadMountWeapon(xpp);
					container.getWeapons().add(weapon);
					currentWeapon = weapon;
				} else if (CAPACITY_TAG.equals(xpp.getName())){
					if (currentWeapon != null) {
						loadWeaponCapacity(xpp, currentWeapon);
					}
				} else {
					// cagade
				}
			}
			

			eventType = xpp.next();
		}
		if (doLog) { Log.d (TAG,"load weapons - end");}
	}	

	private Spell loadSpell(XmlPullParser xpp) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadSpell - start");}
		int eventType = xpp.getEventType();
		Spell spell = new Spell();
		if (eventType == XmlPullParser.START_TAG
				&& SPELL_TAG.equals(xpp.getName())) {
			String spellTitle = xpp.getAttributeValue(null, SPELL_TITLE_TAG);
			String spellCost = xpp.getAttributeValue(null, SPELL_COST_TAG);
			String spellRange = xpp.getAttributeValue(null, SPELL_RANGE_TAG);
			String spellAOE = xpp.getAttributeValue(null, SPELL_AOE_TAG);
			String spellPOW = xpp.getAttributeValue(null, SPELL_POW_TAG);
			String spellUpkeep = xpp.getAttributeValue(null, SPELL_UP_TAG);
			String spellOff = xpp.getAttributeValue(null, SPELL_OFF_TAG);
			String spellLabel = xpp.nextText();
			
			spell.setTitle(spellTitle);
			spell.setCost(spellCost);
			spell.setRange(spellRange);
			spell.setAoe(spellAOE);
			spell.setPow(spellPOW);
			spell.setUpkeep(spellUpkeep);
			spell.setOffensive(spellOff);
			spell.setFullText(spellLabel);
		}
		return spell;
	}	

	private Spell loadAnimus(XmlPullParser xpp) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadAnimus - start");}
		int eventType = xpp.getEventType();
		Spell spell = new Spell();
		if (eventType == XmlPullParser.START_TAG
				&& ANIMUS_TAG.equals(xpp.getName())) {
			String spellTitle = xpp.getAttributeValue(null, SPELL_TITLE_TAG);
			String spellCost = xpp.getAttributeValue(null, SPELL_COST_TAG);
			String spellRange = xpp.getAttributeValue(null, SPELL_RANGE_TAG);
			String spellAOE = xpp.getAttributeValue(null, SPELL_AOE_TAG);
			String spellPOW = xpp.getAttributeValue(null, SPELL_POW_TAG);
			String spellUpkeep = xpp.getAttributeValue(null, SPELL_UP_TAG);
			String spellOff = xpp.getAttributeValue(null, SPELL_OFF_TAG);
			String spellLabel = xpp.nextText();
			
			spell.setTitle(spellTitle);
			spell.setCost(spellCost);
			spell.setRange(spellRange);
			spell.setAoe(spellAOE);
			spell.setPow(spellPOW);
			spell.setUpkeep(spellUpkeep);
			spell.setOffensive(spellOff);
			spell.setFullText(spellLabel);
		}
		return spell;
	}	
	
	
	private void loadModelCapacity(XmlPullParser xpp, SingleModel currentModel) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadModelCapacity - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& CAPACITY_TAG.equals(xpp.getName())) {
			String capacityTitle = xpp.getAttributeValue(null, CAPACITY_TITLE_TAG);
			String capacityType = xpp.getAttributeValue(null, CAPACITY_TYPE_TAG);
			String capacityLabel = xpp.nextText();
			if (doLog) { Log.d (TAG,"capacityLabel = " + capacityLabel);}
			Capacity capacity = new Capacity();
			capacity.setTitle(capacityTitle);
			if (capacityType != null && capacityType.length() > 0) {
				capacity.setType(capacityType.replace('*', '\u2605'));
			} 
			capacity.setLabel(capacityLabel);
			currentModel.getCapacities().add(capacity);
		}		
	}	
	
	
	private void loadWorksFor(XmlPullParser xpp, ArmyElement model) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadWorksFor - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& WORKS_FOR_TAG.equals(xpp.getName())) {
			String factionId = xpp.getAttributeValue(null, WORKS_FOR_FACTION_ID_ATTRIBUTE);
			model.getAllowedFactionsToWorkFor().add(factionId);
		}		
	}		
	
	
	private void loadRestrictedTo(XmlPullParser xpp, Restrictable entry) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadRestrictedTo - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& RESTRICTED_TO_TAG.equals(xpp.getName())) {
			String casterId = xpp.getAttributeValue(null, RESTRICTED_TO_ID_ATTRIBUTE);
			entry.getAllowedEntriesToAttach().add(casterId);
		}		
	}		
	
	private void loadOnlyInTier(XmlPullParser xpp, Restrictable entry) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadOnyInTier - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& ONLY_IN_TIER_TAG.equals(xpp.getName())) {
			String tierId = xpp.getAttributeValue(null, ONLY_IN_TIER_ID_ATTRIBUTE);
			entry.getTiersInWhichAllowedToAppear().add(tierId);
		}		
	}		
	
	private void loadWeaponCapacity(XmlPullParser xpp, Weapon currentWeapon) throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"loadWeaponCapacity - start");}
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& "capacity".equals(xpp.getName())) {
			String capacityTitle = xpp.getAttributeValue(null, CAPACITY_TITLE_TAG);
			String capacityType = xpp.getAttributeValue(null, CAPACITY_TYPE_TAG);
			String capacityLabel = xpp.nextText();
			if (doLog) { Log.d (TAG,"capacityLabel = " + capacityLabel);}
			Capacity capacity = new Capacity();
			capacity.setTitle(capacityTitle);
			if (capacityType != null && capacityType.length() > 0) {
				capacity.setType(capacityType.replace('*', '\u2605'));
			} 
			capacity.setLabel(capacityLabel);
			currentWeapon.getCapacities().add(capacity);
		}		
	}

	/**
	 * charge la partie "UA" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadUA(XmlPullParser xpp, Unit container)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load UA - start");}
		
		
		// xpp.next();
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& UA_TAG.equals(xpp.getName())) {

			UnitAttachment ua = new UnitAttachment();
			ua.setAttachment(true);
			ua.setModels(new ArrayList<SingleModel>());
			
			container.setUnitAttachment(ua);

			// load base infos
			loadElementId(xpp, ua);
			
			String cost = xpp.getAttributeValue(null, COST_TAG);
			ua.setCost(extractFromString(cost));

			
			// load models
			while (!(eventType == XmlPullParser.END_TAG && UA_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (MODEL_TAG.equals(xpp.getName())) {
						SingleModel model = loadSingleModel(xpp);
						
						if (model.isJackMarshal()) {
							container.setJackMarshallViaUA(true);
						}
						
						ua.getModels().add(model);
					} else if ( FULL_TEXT_TAG.equals(xpp.getName()) ){
						loadFullText(xpp, ua);
					}
				}
				eventType = xpp.next();
			}
		}
		if (doLog) { Log.d (TAG,"load UA - end");}
	}		
	
	/**
	 * charge la partie "WA" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renseigne l'unité concernée
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private void loadWA(XmlPullParser xpp, Unit container)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load WA - start");}
		
		
		int eventType = xpp.getEventType();
		if (eventType == XmlPullParser.START_TAG
				&& WA_TAG.equals(xpp.getName())) {

			WeaponAttachment wa = new WeaponAttachment();
			wa.setAttachment(true);
			wa.setModels(new ArrayList<SingleModel>());
			
			container.setWeaponAttachment(wa);

			// load base infos
			loadElementId(xpp, wa);
			
			String cost = xpp.getAttributeValue(null, COST_TAG);
			wa.setCost(extractFromString(cost));

			String max_wa = xpp.getAttributeValue(null, "max_wa");
			container.setMaxWAAllowed(extractFromString(max_wa));
			
			// load models
			while (!(eventType == XmlPullParser.END_TAG && WA_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (MODEL_TAG.equals(xpp.getName())) {
						SingleModel model = loadSingleModel(xpp);
						wa.getModels().add(model);
					} else {
						// cagade
					}
				}
				eventType = xpp.next();
			}
		}
		if (doLog) { Log.d (TAG,"load WA - end");}
	}			
	
	/**
	 * charge la partie "melee_weapon" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renvoit l'objet renseigné
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private MeleeWeapon loadMeleeWeapon(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load melee weapon - start");}
		int eventType = xpp.getEventType();
		MeleeWeapon weapon = new MeleeWeapon();

		if (eventType == XmlPullParser.START_TAG
				&& MELEE_WEAPON_TAG.equals(xpp.getName())) {
			String name = xpp.getAttributeValue(null, NAME_ATTRIBUTE);
			String pow = xpp.getAttributeValue(null, POW_ATTRIBUTE);
			String p_plus_s = xpp.getAttributeValue(null, P_PLUS_S_ATTRIBUTE);
			
			
			weapon.setName(name);
			weapon.setPow(extractFromString(pow));
			weapon.setP_plus_s(extractFromString(p_plus_s));
			
			String reach = xpp.getAttributeValue(null, REACH_ATTRIBUTE);
			weapon.setReach(Boolean.valueOf(reach));

			String chain = xpp.getAttributeValue(null, CHAIN_ATTRIBUTE);
			weapon.setChain(Boolean.valueOf(chain));

			String openFist = xpp.getAttributeValue(null, OPENFIST_ATTRIBUTE);
			weapon.setOpenFist(Boolean.valueOf(openFist));

			extractWeaponSpecials(xpp, weapon);
			
		}
		if (doLog) { Log.d (TAG,"weapon loaded : " + weapon);}
		return weapon; 
	}

	/**
	 * read special caracteristics from weapon (magical, reach, damage type, ...)
	 * @param xpp
	 * @param weapon
	 */
	private void extractWeaponSpecials(XmlPullParser xpp, Weapon weapon) {

		String magical = xpp.getAttributeValue(null, MAGICAL_ATTRIBUTE);
		String fire = xpp.getAttributeValue(null, FIRE_ATTRIBUTE);
		String criticalFire = xpp.getAttributeValue(null, CRITICAL_FIRE_ATTRIBUTE);
		String corrosion = xpp.getAttributeValue(null, CORROSION_ATTRIBUTE);
		String criticalCorrosion = xpp.getAttributeValue(null, CRITICAL_CORROSION_ATTRIBUTE);
		String frost = xpp.getAttributeValue(null, FROST_ATTRIBUTE);
		String electricity = xpp.getAttributeValue(null, ELECTRICITY_ATTRIBUTE);
		String continuousFire = xpp.getAttributeValue(null, CONTINUOUS_FIRE_ATTRIBUTE);
		String continuousCorrosion = xpp.getAttributeValue(null, CONTINUOUS_CORROSION_ATTRIBUTE);
		String count = xpp.getAttributeValue(null, COUNT_ATTRIBUTE);
		String location = xpp.getAttributeValue(null, LOCATION_ATTRIBUTE);
		String weaponMaster = xpp.getAttributeValue(null, WEAPON_MASTER_ATTRIBUTE);
		
		String shield = xpp.getAttributeValue(null, SHIELD_ATTRIBUTE);
		String buckler = xpp.getAttributeValue(null, BUCKLER_ATTRIBUTE);

		
		
		weapon.setMagical(Boolean.valueOf(magical));
		weapon.setFire(Boolean.valueOf(fire));
		weapon.setContinuousFire(Boolean.valueOf(continuousFire));
		weapon.setCorrosion(Boolean.valueOf(corrosion));
		weapon.setContinuousCorrosion(Boolean.valueOf(continuousCorrosion));
		weapon.setFrost(Boolean.valueOf(frost));
		weapon.setElectricity(Boolean.valueOf(electricity));
		weapon.setCriticalFire(Boolean.valueOf(criticalFire));
		weapon.setCriticalCorrosion(Boolean.valueOf(criticalCorrosion));
		weapon.setShield(Boolean.valueOf(shield));
		weapon.setBuckler(Boolean.valueOf(buckler));

		
		weapon.setLocation(location);
		weapon.setWeaponMaster(Boolean.valueOf(weaponMaster));
		if (extractFromString(count) > 1) {
			weapon.setCount(extractFromString(count));
		}
	}	
	
	
	/**
	 * charge la partie "ranged_weapon" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renvoit l'objet renseigné
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private RangedWeapon loadRangedWeapon(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load ranged weapon - start");}
		int eventType = xpp.getEventType();
		RangedWeapon weapon = new RangedWeapon();

		if (eventType == XmlPullParser.START_TAG
				&& RANGED_WEAPON_TAG.equals(xpp.getName())) {
			String name = xpp.getAttributeValue(null, NAME_ATTRIBUTE);
			// String spray = xpp.getAttributeValue(null, SPRAY_ATTRIBUTE);
			String rng = xpp.getAttributeValue(null, RNG_ATTRIBUTE);
			String rof = xpp.getAttributeValue(null, ROF_ATTRIBUTE);
			String aoe = xpp.getAttributeValue(null, AOE_ATTRIBUTE);
			String pow = xpp.getAttributeValue(null, POW_ATTRIBUTE);

			weapon.setName(name);
			extractRangeFromString(rng, weapon);
//			weapon.setSpray(Boolean.valueOf(spray));
//			weapon.setRange(extractFromString(rng));
			weapon.setRof(extractFromString(rof));
			weapon.setAoe(extractFromString(aoe));
			weapon.setPow(extractFromString(pow));
			
			extractWeaponSpecials(xpp, weapon);
		}
		if (doLog) { Log.d (TAG,"weapon loaded : " + weapon);}
		return weapon; 
	}	

	/**
	 * charge la partie "mount_weapon" du fichier xml é partir du parser positionné
	 * sur le tag de début d'une ligne, et renvoit l'objet renseigné
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */	
	private MountWeapon loadMountWeapon(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		if (doLog) { Log.d (TAG,"load ranged weapon - start");}
		int eventType = xpp.getEventType();
		MountWeapon weapon = new MountWeapon();

		if (eventType == XmlPullParser.START_TAG
				&& MOUNT_WEAPON_TAG.equals(xpp.getName())) {
			String name = xpp.getAttributeValue(null, NAME_ATTRIBUTE);
			String pow = xpp.getAttributeValue(null, POW_ATTRIBUTE);

			weapon.setName(name);
			weapon.setPow(extractFromString(pow));
			
			extractWeaponSpecials(xpp, weapon);
		}
		if (doLog) { Log.d (TAG,"weapon loaded : " + weapon);}
		return weapon; 
	}		

	private void extractRangeFromString(String rangeString, RangedWeapon weapon) {
		if (rangeString == null || rangeString.length() == 0 || rangeString.trim().length() == 0) {
			weapon.setRange(0);
		} else {
			try {
				if (rangeString.startsWith("SP")) {
					weapon.setSpray(true);
					String range = rangeString.substring(2);
					weapon.setRange(Integer.valueOf(range));
				} else {
					weapon.setRange(Integer.valueOf(rangeString));	
				}
			} catch (Exception e) {
				weapon.setRange(0);
			}
		}
	}

	
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

}
