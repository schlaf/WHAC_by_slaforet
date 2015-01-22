/**
 * 
 */
package com.schlaf.steam.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.AvailableModels;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.data.TierBenefit;
import com.schlaf.steam.data.TierCostAlteration;
import com.schlaf.steam.data.TierEntry;
import com.schlaf.steam.data.TierEntryGroup;
import com.schlaf.steam.data.TierFAAlteration;
import com.schlaf.steam.data.TierFreeModel;
import com.schlaf.steam.data.TierLevel;
import com.schlaf.steam.data.TierMarshalAlteration;
import com.schlaf.steam.storage.StorageManager;

/**
 * @author S0085289
 * 
 */
public class TierExtractor {

	private static final String ONLY_IF_ATTACHED_TO = "onlyIfAttachedTo";
	private static final String TIERSLIST_TAG = "tierslist";
	private static final String TIERS_TAG = "tiers";
	private static final String CASTERID_ATTRIBUTE = "casterId";
	private static final String FACTIONID_ATTRIBUTE="faction";
	private static final String LEVEL_TAG = "level";
	private static final String AVAILABLEMODELS_TAG = "availableModels";
	private static final String NUMBER_ATTRIBUTE = "number";
	
	private static final String ONLY_TAG = "only";
	private static final String FOR_EACH_TAG = "forEach";
	private static final String ENTRY_TAG = "entry";
	
	private static final String BENEFITS_TAG = "benefits";
	private static final String INGAMEEFFECT_TAG = "ingameeffect";
	private static final String ALTERFA_TAG = "alterFA";
	private static final String ALTERCOST_TAG = "alterCost";
	private static final String ALTERMARSHAL_TAG = "alterMarshal";
	private static final String FREEMODEL_TAG = "freeModel";
	private static final String BONUS_ATTRIBUTE = "bonus";
	private static final String ENTRYID_ATTRIBUTE = "entryId";
	
	
	private static final String MUST_HAVE_TAG = "must_have";
	private static final String ENTRYGROUP_TAG = "entrygroup";
	private static final String INBATTLEGROUPONLY_ATTRIBUTE = "inBattlegroupOnly";
	private static final String JACKMARSHALLEDONLY_ATTRIBUTE = "jackMarshalledOnly";
	private static final String MINNUMBER_ATTRIBUTE = "minNumber";
//	private static final String LABEL_ATTRIBUTE = "label";
//	private static final String LABEL_ATTRIBUTE = "label";
//	private static final String BENEFITS_TAG = "benefits";
//	private static final String BENEFITS_TAG = "benefits";
//	private static final String BENEFITS_TAG = "benefits";
	
	private static final int[] TIERS_XML_FILES = new int[]
			{R.xml.tiers_cygnar, 
		R.xml.tiers_cryx,
		R.xml.tiers_cyriss, 
		R.xml.tiers_khador, 
		R.xml.tiers_menoth, 
		R.xml.tiers_retribution,
		R.xml.tiers_mercs,
		R.xml.tiers_everblight,
		R.xml.tiers_orboros,
		R.xml.tiers_skorne,
		R.xml.tiers_troll,
		R.xml.tiers_minion}; 
	
	
	private static final String ID_TAG = "id";
	private static final String NAME_TAG = "name";
	
	private boolean D = false;
	

	/** access to local resources */
	Resources res;
	SteamPunkRosterApplication parentApplication;

	public TierExtractor(Resources res,
			SteamPunkRosterApplication parentApplication) {
		// initial treatment?
		this.res = res;
		this.parentApplication = parentApplication;
	}

	public void doExtract() {

		for (int resourceId : TIERS_XML_FILES) {
			XmlResourceParser xppTiers = res.getXml(resourceId);
			try {
				xppTiers.next();
				int eventType = xppTiers.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {
					} else if (eventType == XmlPullParser.START_TAG) {
						if (TIERSLIST_TAG.equals(xppTiers.getName())) {
							loadTiers(xppTiers);
						}
					} else if (eventType == XmlPullParser.END_TAG) {
					} else if (eventType == XmlPullParser.TEXT) {
					}
					eventType = xppTiers.next();
				}
				
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		

	}
	
	
	public void extractImportedTiers(Context applicationContext, boolean verbose) {
		
		File importFilesDir = applicationContext.getDir(StorageManager.IMPORT_FILES_DIR, Context.MODE_PRIVATE);
		
		File[] files = importFilesDir.listFiles();
		
		for (File importFile : files) {
			
			String extension = "";

			int i = importFile.getName().lastIndexOf('.');
			if (i >= 0) {
			    extension = importFile.getName().substring(i+1);
			}
			
			if (StorageManager.TIER_EXTENSION.equalsIgnoreCase(extension)) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(importFile);

					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xppTiers = factory.newPullParser();

					xppTiers.setInput(fis, "UTF-8");
					xppTiers.next();
					int eventType = xppTiers.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_DOCUMENT) {
						} else if (eventType == XmlPullParser.START_TAG) {
							if (TIERSLIST_TAG.equals(xppTiers.getName())) {
								loadTiers(xppTiers);
							}
						} else if (eventType == XmlPullParser.END_TAG) {
						} else if (eventType == XmlPullParser.TEXT) {
						}
						eventType = xppTiers.next();
					}


				} catch (Exception e) {
					e.printStackTrace();
					Log.e("TierExtractor", "deleting corrupted imported tier file : " + importFile.getName());
					importFile.delete();
					// Toast.makeText(applicationContext, "Data file import failed : " + importFile.getName() + " -- this file will be deleted!", Toast.LENGTH_SHORT).show();
				} finally {
				}
			}
		}
		
	}
	
	public boolean extractImportedFile(Context applicationContext, File content) {
		
		InputStream is;
		try {
			is = new FileInputStream(content);

			XmlPullParserFactory factory = XmlPullParserFactory
					.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xppTiers = factory.newPullParser();

			xppTiers.setInput(is, "UTF-8");
			xppTiers.next();
			int eventType = xppTiers.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					if (TIERSLIST_TAG.equals(xppTiers.getName())) {
						loadTiers(xppTiers);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
				} else if (eventType == XmlPullParser.TEXT) {
				}
				eventType = xppTiers.next();
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

	/**
	 * charge la partie "armies" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadTiers(XmlPullParser xpp) {
		if (D) Log.d("TiersExtractor","loadTiers - start");
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && TIERSLIST_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (TIERS_TAG.equals(xpp.getName())) {
						Tier tier = loadTier(xpp);
						ArmySingleton.getInstance().getTiers().put(tier.getTitle(), tier);
						if (D) Log.d("TiersExtractor", tier.toString());
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (D) Log.d("TiersExtractor","loadTiers - end");
	}

	
	/**
	 * charge la partie "armies" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private Tier loadTier(XmlPullParser xpp) {
		if (D) Log.d("TiersExtractor","loadTier - start");
		
		Tier tier = new Tier();
		String tierName = xpp.getAttributeValue(null, NAME_TAG);;
		String caster = xpp.getAttributeValue(null, CASTERID_ATTRIBUTE);
		String factionId = xpp.getAttributeValue(null, FACTIONID_ATTRIBUTE);
		
		tier.setCasterId(caster);
		tier.setTitle(tierName);
		tier.setFactionId(factionId);
		
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && TIERS_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (AVAILABLEMODELS_TAG.equals(xpp.getName())) {
						loadAvailableModels(xpp, tier);
					}
					if (LEVEL_TAG.equals(xpp.getName())) {
						TierLevel level = loadLevel(xpp);
						tier.getLevels().add(level);
					}
				}
				eventType = xpp.next();
			}
			
			// adjust "only" models to each level from previous level if no specification
			if (! tier.getLevels().isEmpty()) {
				ArrayList<TierEntry> only = tier.getLevels().get(0).getOnlyModels();
				
				for (TierLevel level : tier.getLevels()) {
					if (level.getLevel() > 1) {
						if (level.getOnlyModels().isEmpty()) {
							level.getOnlyModels().addAll(only);	
							level.setInheritOnlyModelsFromPreviousLevel(true);
						} else {
							only = level.getOnlyModels();
							level.setInheritOnlyModelsFromPreviousLevel(false);
						}
					}
				}
			}
			
			
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (D) Log.d("TiersExtractor","loadTier - end");
		return tier;
	}	
	
	private void loadAvailableModels(XmlPullParser xpp, Tier tier) {

		if (D) Log.d("TiersExtractor","loadAvailableModels - start");
		ArrayList<AvailableModels> models = new ArrayList<AvailableModels>();
		try {
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && AVAILABLEMODELS_TAG.equals(xpp
					.getName()))) {
				if ("type".equals(xpp.getName())) {
					AvailableModels subType = loadSubType(xpp);
					models.add(subType);
				} 
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (D) Log.d("TiersExtractor","loadAvailableModels - end");

		tier.setAvailableModels(models);
	}

	private AvailableModels loadSubType(XmlPullParser xpp) {
		if (D) Log.d("TiersExtractor","loadSubType - start");
		String models = "";
		String type = xpp.getAttributeValue(null, "type");
		try {
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && "type".equals(xpp
					.getName()))) {
				if ("models".equals(xpp.getName())) {
					models = xpp.nextText();
				}
				eventType = xpp.next();
			}
		}catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AvailableModels result = new AvailableModels(type, models);
		if (D) Log.d("TiersExtractor","loadSubType - end");
		return result;
		
	}

	/**
	 * charge la partie "level" du fichier xml é partir du parser positionné sur
	 * le tag de début d'un level
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private TierLevel loadLevel(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		if (D) Log.d("TiersExtractor","loadLevel - start");
		
		TierLevel level = new TierLevel();
		
		String levelNumber = xpp.getAttributeValue(null, NUMBER_ATTRIBUTE);
		
		level.setLevel(Integer.parseInt(levelNumber));
		
		// xpp.next();
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && LEVEL_TAG.equals(xpp
				.getName()))) {
			if (ONLY_TAG.equals(xpp.getName())) {
				loadOnly(xpp, level);
			} else if (MUST_HAVE_TAG.equals(xpp.getName())) {
				loadMustHave(xpp, level);
			} else if (BENEFITS_TAG.equals(xpp.getName())) {
				loadBenefits(xpp, level);
			}
			eventType = xpp.next();
		}
		if (D) Log.d("TiersExtractor","loadlevel - end");
		return level;
	}	
	
	
	private void loadBenefits(XmlPullParser xpp, TierLevel level) throws XmlPullParserException, IOException  {
		TierBenefit benefit = new TierBenefit();
		level.setBenefit(benefit);
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && BENEFITS_TAG.equals(xpp
				.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (INGAMEEFFECT_TAG.equals(xpp.getName())) {
					String inGameEffect = xpp.nextText(); // xpp.getAttributeValue(null, LABEL_ATTRIBUTE);
					benefit.setInGameEffect(inGameEffect);
				} else if (ALTERFA_TAG.equals(xpp.getName())) {
					loadFaAlteration(xpp, benefit);
				} else if (ALTERMARSHAL_TAG.equals(xpp.getName())) {
					loadMarshalAlteration(xpp, benefit);
				} else if (ALTERCOST_TAG.equals(xpp.getName())) {
					TierCostAlteration alteration = new TierCostAlteration();
					String bonus = xpp.getAttributeValue(null, BONUS_ATTRIBUTE);
					String entryId = xpp.getAttributeValue(null, ENTRYID_ATTRIBUTE);
					String onlyIfAttachedTo = xpp.getAttributeValue(null, ONLY_IF_ATTACHED_TO);
					
					alteration.setEntry(new TierEntry(entryId));
					alteration.setCostAlteration(Integer.parseInt(bonus));
					if (onlyIfAttachedTo != null && onlyIfAttachedTo.length() > 0) {
						alteration.setRestricted(true);
						alteration.setRestrictedToId(onlyIfAttachedTo);
					}
					benefit.getAlterations().add(alteration);
				} else if (FREEMODEL_TAG.equals(xpp.getName())) {
					loadFreeModel(xpp, benefit);
				} 
			}
			eventType = xpp.next();
		}
	}

	private void loadFreeModel(XmlPullParser xpp, TierBenefit benefit) throws XmlPullParserException, IOException {
		TierFreeModel freeModel = new TierFreeModel();
		loadFreeModels(xpp, freeModel);
		benefit.getFreebies().add(freeModel);
	}

	private void loadFaAlteration(XmlPullParser xpp, TierBenefit benefit) throws XmlPullParserException, IOException  {
		TierFAAlteration alteration = new TierFAAlteration();
		String bonus = xpp.getAttributeValue(null, BONUS_ATTRIBUTE);
		String entryId = xpp.getAttributeValue(null, ENTRYID_ATTRIBUTE);
		alteration.setEntry(new TierEntry(entryId));
		alteration.setFaAlteration(parseFA(bonus));
		benefit.getAlterations().add(alteration);
		
		int eventType = xpp.getEventType();
		while (! (eventType == XmlPullParser.END_TAG && ALTERFA_TAG.equals(xpp
				.getName()))) {
			if (FOR_EACH_TAG.equals(xpp.getName())) {
				loadForEachFAAlter(xpp, alteration);
			}
			eventType = xpp.next();
		}
		
	}
	
	private void loadMarshalAlteration (XmlPullParser xpp, TierBenefit benefit) throws XmlPullParserException, IOException  {
		TierMarshalAlteration alteration = new TierMarshalAlteration();
		String marshallNbJacksAlteration = xpp.getAttributeValue(null, BONUS_ATTRIBUTE);
		String entryId = xpp.getAttributeValue(null, ENTRYID_ATTRIBUTE);
		alteration.setEntry(new TierEntry(entryId));
		alteration.setMarshallNbJacksAlteration(Integer.parseInt(marshallNbJacksAlteration));
		benefit.getAlterations().add(alteration);
	}
	private int parseFA(String bonus) {
		if ("U".equals(bonus)) {
			return ArmyElement.MAX_FA;
		}
		return Integer.parseInt(bonus);
	}

	private void loadFreeModels(XmlPullParser xpp, TierFreeModel freeModel) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && FREEMODEL_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					freeModel.getFreeModels().add(entry);
				}
				
				if (FOR_EACH_TAG.equals(xpp.getName())) {
						loadForEachFreeModel(xpp, freeModel);
				}
			}
			eventType = xpp.next();
		}
	}
	
	private void loadMustHave(XmlPullParser xpp, TierLevel level) throws XmlPullParserException, IOException  {
		if (D) Log.d("TiersExtractor","loadMustHave - start");
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && MUST_HAVE_TAG.equals(xpp
				.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRYGROUP_TAG.equals(xpp.getName())) {
					TierEntryGroup group = new TierEntryGroup();
					loadEntryGroup(xpp, group);
					
					if (group.isInBattlegroup()) {
						level.getMustHaveModelsInBG().add(group);
					} else if (group.isInJackMarshal()) {
						level.getMustHaveModelsInMarshal().add(group);
					} else {
						level.getMustHaveModels().add(group);	
					}
					
					
				}
			}
			eventType = xpp.next();
		}
	}

	private void loadOnly(XmlPullParser xpp, TierLevel level) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && ONLY_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					level.getOnlyModels().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}
	
	
	private void loadForEachFAAlter(XmlPullParser xpp, TierFAAlteration faAlteration) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		faAlteration.setForEach(new ArrayList<TierEntry>());
		while (!(eventType == XmlPullParser.END_TAG && FOR_EACH_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					faAlteration.getForEach().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}

	private void loadForEachFreeModel(XmlPullParser xpp, TierFreeModel freeModel) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		freeModel.setForEach(new ArrayList<TierEntry>());
		while (!(eventType == XmlPullParser.END_TAG && FOR_EACH_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					freeModel.getForEach().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}
	
	private void loadEntryGroup(XmlPullParser xpp, TierEntryGroup group) throws XmlPullParserException, IOException  {

		String groupMin = xpp.getAttributeValue(null, MINNUMBER_ATTRIBUTE);
		String inBG = xpp.getAttributeValue(null, INBATTLEGROUPONLY_ATTRIBUTE);
		String isJackMarshaled = xpp.getAttributeValue(null, JACKMARSHALLEDONLY_ATTRIBUTE);
		group.setMinCount(Integer.parseInt(groupMin));
		group.setInBattlegroup(Boolean.valueOf(inBG));
		group.setInJackMarshal(Boolean.valueOf(isJackMarshaled));
		
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && ENTRYGROUP_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					group.getEntries().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}	
	
	private TierEntry loadEntry(XmlPullParser xpp) {
		String id = xpp.getAttributeValue(null, ID_TAG);;
		TierEntry entry = new TierEntry(id);
		return entry;
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
