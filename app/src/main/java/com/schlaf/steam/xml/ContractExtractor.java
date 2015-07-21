/**
 * 
 */
package com.schlaf.steam.xml;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.AvailableModels;
import com.schlaf.steam.data.Contract;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.data.TierBenefit;
import com.schlaf.steam.data.TierCostAlteration;
import com.schlaf.steam.data.TierEntry;
import com.schlaf.steam.data.TierFAAlteration;
import com.schlaf.steam.data.TierFreeModel;

/**
 * @author S0085289
 * 
 */
public class ContractExtractor {

	private static final String CONTRACTSLIST_TAG = "contractslist";
	private static final String CONTRACT_TAG = "contract";
	private static final String CONTRACTID_ATTRIBUTE="contractId";
	private static final String FACTIONID_ATTRIBUTE="faction";
	private static final String DESCRIPTION_ATTRIBUTE="description";
	
	private static final String ONLY_TAG = "only";
	private static final String ENTRY_TAG = "entry";
	
	private static final String BENEFITS_TAG = "benefits";
	private static final String LABEL_ATTRIBUTE = "label";
	private static final String INGAMEEFFECT_TAG = "ingameeffect";
	private static final String ALTERFA_TAG = "alterFA";
	private static final String ALTERCOST_TAG = "alterCost";
	private static final String FREEMODEL_TAG = "freeModel";
	private static final String BONUS_ATTRIBUTE = "bonus";
	private static final String ENTRYID_ATTRIBUTE = "entryId";
	
	private static final String ID_TAG = "id";
	private static final String NAME_TAG = "name";

    private static final String AVAILABLEMODELS_TAG = "availableModels";
	
	private boolean D = false;
	
	/** access to local resources */
	Resources res;
	SteamPunkRosterApplication parentApplication;

	public ContractExtractor(Resources res,
			SteamPunkRosterApplication parentApplication) {
		// initial treatment?
		this.res = res;
		this.parentApplication = parentApplication;
	}

	public void doExtract() {

		XmlResourceParser xppContracts = res.getXml(R.xml.contracts);
		
		StringBuffer stringBuffer = new StringBuffer();
		try {
			xppContracts.next();
			int eventType = xppContracts.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					stringBuffer.append("--- Start XML ---");
				} else if (eventType == XmlPullParser.START_TAG) {
					stringBuffer.append("\nSTART_TAG: " + xppContracts.getName());
					if (CONTRACTSLIST_TAG.equals(xppContracts.getName())) {
						loadContracts(xppContracts);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					stringBuffer.append("\nEND_TAG: " + xppContracts.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					stringBuffer.append("\nTEXT: " + xppContracts.getText());
				}
				eventType = xppContracts.next();
			}
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stringBuffer.append("\n--- End XML ---");
		if (D) Log.d("ContractsExtractor",stringBuffer.toString());

	}

	/**
	 * charge la partie "contracts" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadContracts(XmlResourceParser xpp) {
		if (D) Log.d("ContractsExtractor","loadTiers - start");
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && CONTRACTSLIST_TAG
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (CONTRACT_TAG.equals(xpp.getName())) {
						Contract contract = loadContract(xpp);
						ArmySingleton.getInstance().getContracts().add(contract);
						if (D) Log.d("ContractsExtractor", contract.toString());
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (D) Log.d("ContractsExtractor","loadTiers - end");
	}

	
	/**
	 * charge la partie "contract" du fichier xml é partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private Contract loadContract(XmlResourceParser xpp) {
		if (D) Log.d("ContractsExtractor","loadTier - start");
		
		Contract contract = new Contract();
		String contractName = xpp.getAttributeValue(null, NAME_TAG);;
		String contractDescription = xpp.getAttributeValue(null, DESCRIPTION_ATTRIBUTE);;
		String contractId = xpp.getAttributeValue(null, CONTRACTID_ATTRIBUTE);
		String factionId = xpp.getAttributeValue(null, FACTIONID_ATTRIBUTE);
		
		contract.setContractId(contractId);
		contract.setDescription(contractDescription);
		contract.setTitle(contractName);
		contract.setFactionId(factionId);
		
		try {
			
			xpp.next();
			int eventType = xpp.getEventType();
			while (eventType == XmlPullParser.START_TAG) {
				if (ONLY_TAG.equals(xpp.getName())) {
					loadOnly(xpp, contract);
				} else if (BENEFITS_TAG.equals(xpp.getName())) {
					loadBenefits(xpp, contract);
				} else if (AVAILABLEMODELS_TAG.equals(xpp.getName())) {
                    loadAvailableModels(xpp, contract);
                }

                eventType = xpp.next();
			}
			
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (D) Log.d("ContractsExtractor","loadTier - end");
		return contract;
	}	
	
	
	private void loadBenefits(XmlResourceParser xpp, Contract contract) throws XmlPullParserException, IOException  {
		TierBenefit benefit = new TierBenefit();
		contract.setBenefit(benefit);
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && BENEFITS_TAG.equals(xpp
				.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (INGAMEEFFECT_TAG.equals(xpp.getName())) {
					String inGameEffect = xpp.nextText();
					benefit.setInGameEffect(inGameEffect);
				} else if (ALTERFA_TAG.equals(xpp.getName())) {
					TierFAAlteration alteration = new TierFAAlteration();
					String bonus = xpp.getAttributeValue(null, BONUS_ATTRIBUTE);
					String entryId = xpp.getAttributeValue(null, ENTRYID_ATTRIBUTE);
					alteration.setEntry(new TierEntry(entryId));
					alteration.setFaAlteration(Integer.parseInt(bonus));
					benefit.getAlterations().add(alteration);
				} else if (ALTERCOST_TAG.equals(xpp.getName())) {
					TierCostAlteration alteration = new TierCostAlteration();
					String bonus = xpp.getAttributeValue(null, BONUS_ATTRIBUTE);
					String entryId = xpp.getAttributeValue(null, ENTRYID_ATTRIBUTE);
					alteration.setEntry(new TierEntry(entryId));
					alteration.setCostAlteration(Integer.parseInt(bonus));
					benefit.getAlterations().add(alteration);
				} else if (FREEMODEL_TAG.equals(xpp.getName())) {
					TierFreeModel freeModel = new TierFreeModel();
					loadFreeModels(xpp, freeModel);
					benefit.getFreebies().add(freeModel);
				} 
			}
			eventType = xpp.next();
		}
	}

	private void loadFreeModels(XmlResourceParser xpp, TierFreeModel freeModel) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && FREEMODEL_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					freeModel.getFreeModels().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}
	


	private void loadOnly(XmlResourceParser xpp, Contract contract) throws XmlPullParserException, IOException  {
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && ONLY_TAG.equals(xpp
				.getName()))) {
			// until the end of tag factions
			if (eventType == XmlPullParser.START_TAG) {
				if (ENTRY_TAG.equals(xpp.getName())) {
					TierEntry entry = loadEntry(xpp);
					contract.getOnlyModels().add(entry);
				}
			}
			eventType = xpp.next();
		}
	}

	private TierEntry loadEntry(XmlResourceParser xpp) {
		String id = xpp.getAttributeValue(null, ID_TAG);;
		TierEntry entry = new TierEntry(id);
		return entry;
	}


    private void loadAvailableModels(XmlPullParser xpp, Contract contract) {

        if (D) Log.d("ContractExtractor" ,"loadAvailableModels - start");
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
        if (D) Log.d("ContractExtractor","loadAvailableModels - end");

        contract.setAvailableModels(models);
    }

    private AvailableModels loadSubType(XmlPullParser xpp) {
        if (D) Log.d("ContractExtractor","loadSubType - start");
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
        if (D) Log.d("ContractExtractor","loadSubType - end");
        return result;

    }


}
