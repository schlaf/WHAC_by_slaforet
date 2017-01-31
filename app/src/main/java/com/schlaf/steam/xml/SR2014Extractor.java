/**
 * 
 */
package com.schlaf.steam.xml;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.steamroller.SteamRollerSingleton;
import com.schlaf.steam.data.Mission;

/**
 * @author S0085289
 * 
 */
public class SR2014Extractor {
	
	private boolean doLog = false;
	
	private static final String TAG = "SR2014Extractor";

	private static final String MISSIONS = "missions";
	private static final String MISSION = "mission";
	private static final String PACKET = "packet";
	private static final String TYPE = "type";
	private static final String NUMBER = "number";
	private static final String NAME = "name";
	private static final String VICTORY = "victory";
	private static final String SPECIALRULES = "specialRules";
	private static final String TACTICALTIPS = "tacticalTips";
	private static final String MAP = "map";
	private static final String OBJECTIVE = "objective";
	
	
	int[] XML_FILES = new int[] {
//            R.xml.sr_2014_missions,
            R.xml.sr_2016_missions};

	/** access to local resources */
	Resources res;
	SteamPunkRosterApplication parentApplication;

	public SR2014Extractor(Resources res,
			SteamPunkRosterApplication parentApplication) {
		// initial treatment?
		this.res = res;
		this.parentApplication = parentApplication;
	}

	public void doExtract() {

		for (int resource : XML_FILES) {
			extractScenarii(resource);
		}

		if (doLog) { Log.d (TAG,"extraction done");}

	}

	private void extractScenarii(int resourceFileId) {
		try {
			
			XmlResourceParser xppScenar = res.getXml(resourceFileId);
			xppScenar.next();
			int eventType = xppScenar.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (MISSIONS.equals(xppScenar.getName())) {
						loadMissions(xppScenar);
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
	 * charge la partie "missions" du fichier xml à partir du parser positionné
	 * sur le tag de début
	 * 
	 * @param xpp
	 */
	private void loadMissions(XmlPullParser xpp) {
		if (doLog) { Log.d (TAG,"loadArmies - start");}
		try {
			// xpp.next();
			int eventType = xpp.getEventType();
			while (!(eventType == XmlPullParser.END_TAG && MISSIONS
					.equals(xpp.getName()))) {
				// until the end of tag factions
				if (eventType == XmlPullParser.START_TAG) {
					if (MISSION.equals(xpp.getName())) {
						loadMission(xpp);
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
	 * charge la partie "mission" du fichier xml à partir du parser positionné sur
	 * le tag de début d'une army
	 * 
	 * @param xpp
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void loadMission(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		if (doLog) { Log.d (TAG,"loadMission - start");}

		Mission mission = new Mission();
		
		int eventType = xpp.getEventType();
		while (!(eventType == XmlPullParser.END_TAG && MISSION
				.equals(xpp.getName()))) {
			if (eventType == XmlPullParser.START_TAG) {
				if (PACKET.equals(xpp.getName())) {
					String packet = xpp.nextText();
					mission.setPacket(packet);
				} else if (TYPE.equals(xpp.getName())) {
					String type = xpp.nextText();
					mission.setType(type);
				}  else if (NUMBER.equals(xpp.getName())) {
					String number = xpp.nextText();
					mission.setNumber(number);
				} else if (NAME.equals(xpp.getName())) {
					String name = xpp.nextText();
					mission.setName(name);
				} else if (VICTORY.equals(xpp.getName())) {
					String victory = xpp.nextText();
					mission.setVictoryText(victory);
				} else if (SPECIALRULES.equals(xpp.getName())) {
					String specialRules = xpp.nextText();
					mission.setSpecialRulesText(specialRules);
				}  else if (TACTICALTIPS.equals(xpp.getName())) {
					String tacticalTipsText = xpp.nextText();
					mission.setTacticalTipsText(tacticalTipsText);
				} else if (MAP.equals(xpp.getName())) {
					String mapName = xpp.nextText();
					int mapResourceId = parentApplication.getResources().getIdentifier(mapName, "drawable", parentApplication.getPackageName());
					mission.setMapResourceId(mapResourceId);
				} else if (OBJECTIVE.equals(xpp.getName())) {
					String objectiveName = xpp.nextText();
					int objectiveResourceId = parentApplication.getResources().getIdentifier(objectiveName, "drawable", parentApplication.getPackageName());
					mission.setObjectiveResourceId(objectiveResourceId);
				}
			}
			eventType = xpp.next();
		}
		
		SteamRollerSingleton.getInstance().getScenarii().add(mission);
		
		if (doLog) { Log.d (TAG,"loadMission - end");}
	}

}
