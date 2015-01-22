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
import com.schlaf.steam.data.RuleCostAlteration;
import com.schlaf.steam.data.RuleFAAlteration;
import com.schlaf.steam.data.RulesSingleton;

/**
 * @author S0085289
 * 
 */
public class RulesExtractor {
	
	private boolean doLog = true;
	
	private static final String TAG = "RulesExtractor";

	private static final String RULES = "rules";
	private static final String ALTERFA = "alterFA";
	private static final String ALTERCOST = "alterCost";
	private static final String ENTRYID = "entryId";
	private static final String BONUS = "bonus";
	private static final String ONLYIFPRESENT = "onlyIfPresent";
	private static final String ONLYIFATTACHEDTO = "onlyIfAttachedTo";
	
	
	int[] XML_FILES = new int[] {R.xml.rules};

	/** access to local resources */
	Resources res;
	SteamPunkRosterApplication parentApplication;

	public RulesExtractor(Resources res,
			SteamPunkRosterApplication parentApplication) {
		// initial treatment?
		this.res = res;
		this.parentApplication = parentApplication;
	}

	public void doExtract() {

		for (int resource : XML_FILES) {
			extractRules(resource);
		}

		if (doLog) { Log.d (TAG,"extraction done");}

	}

	private void extractRules(int resourceFileId) {
		try {
			
			XmlResourceParser xppScenar = res.getXml(resourceFileId);
			xppScenar.next();
			int eventType = xppScenar.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (RULES.equals(xppScenar.getName())) {
						loadRules(xppScenar);
					}
				} 
				eventType = xppScenar.next();
			}
			
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * charge la partie "rules" du fichier xml à partir du parser positionné sur
	 * le tag de début d'une army
	 * 
	 * @param xpp
	 * @throws java.io.IOException
	 * @throws org.xmlpull.v1.XmlPullParserException
	 */
	private void loadRules(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		if (doLog) { Log.d (TAG,"loadMission - start");}

		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && RULES
				.equals(xpp.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (ALTERFA.equals(xpp.getName())) {
					String entryId = xpp.getAttributeValue(null, ENTRYID);
					String bonus = xpp.getAttributeValue(null, BONUS);
					String onlyIfPresentId = xpp.getAttributeValue(null, ONLYIFPRESENT);
					
					RuleFAAlteration rule = new RuleFAAlteration();
					rule.setEntryId(entryId);
					rule.setBonus(Integer.parseInt(bonus));
					rule.setOnlyIfPresentId(onlyIfPresentId);
					
					if (RulesSingleton.getInstance().getFaRules().get(onlyIfPresentId) == null) {
						ArrayList<RuleFAAlteration> rules = new ArrayList<RuleFAAlteration>();
						RulesSingleton.getInstance().getFaRules().put(onlyIfPresentId, rules);
					} 
					RulesSingleton.getInstance().getFaRules().get(onlyIfPresentId).add(rule);
					if (doLog) { Log.d (TAG,"loaded RuleFAAlteration " + rule.toString());}
				} else if (ALTERCOST.equals(xpp.getName())) {
					String entryId = xpp.getAttributeValue(null, ENTRYID);
					String bonus = xpp.getAttributeValue(null, BONUS);
					String onlyIfAttachedToId = xpp.getAttributeValue(null, ONLYIFATTACHEDTO);
					
					RuleCostAlteration rule = new RuleCostAlteration();
					rule.setEntryId(entryId);
					rule.setBonus(Integer.parseInt(bonus));
					rule.setOnlyIfAttachedToId(onlyIfAttachedToId);
					
					if (RulesSingleton.getInstance().getCostRules().get(onlyIfAttachedToId) == null) {
						ArrayList<RuleCostAlteration> rules = new ArrayList<RuleCostAlteration>();
						RulesSingleton.getInstance().getCostRules().put(onlyIfAttachedToId, rules);
					} 
					RulesSingleton.getInstance().getCostRules().get(onlyIfAttachedToId).add(rule);
					if (doLog) { Log.d (TAG,"loaded RuleCostAlteration " + rule.toString());}
				}  
			}
			eventType = xpp.next();
		}
		
		if (doLog) { Log.d (TAG,"loadMission - end");}
	}

}
