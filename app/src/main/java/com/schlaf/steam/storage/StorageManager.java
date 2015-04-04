package com.schlaf.steam.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.compatibility.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.schlaf.steam.activities.PreferenceConstants;
import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.battle.BeastEntry;
import com.schlaf.steam.activities.battle.BeastPackEntry;
import com.schlaf.steam.activities.battle.JackEntry;
import com.schlaf.steam.activities.battle.KarchevEntry;
import com.schlaf.steam.activities.battle.MultiPVUnit;
import com.schlaf.steam.activities.battle.SingleDamageLineEntry;
import com.schlaf.steam.activities.collection.CollectionSingleton;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selected.BeastCommander;
import com.schlaf.steam.activities.selectlist.selected.JackCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedArmyCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedModel;
import com.schlaf.steam.activities.selectlist.selected.SelectedRankingOfficer;
import com.schlaf.steam.activities.selectlist.selected.SelectedSolo;
import com.schlaf.steam.activities.selectlist.selected.SelectedUA;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.selectlist.selected.SelectedWA;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarbeast;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarjack;
import com.schlaf.steam.data.ArmyCommander;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.WarbeastPack;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.WeaponAttachment;

public class StorageManager {
	
	private static final boolean D = false; // for debug
	
	public static final String WHAC_SUBDIR = "/Whac";
	/** directory for army lists */
	public static final String ARMIES_SUBDIR = "armies";
	/** directory for battles */
	public static final String BATTLES_SUBDIR = "battles";
	/** directory for battle results */
	public static final String RESULTS_SUBDIR = "result";
	public static final String DOWNLOAD_SUBDIR = "/download";

	private static final String TAG = "StorageManager";

	public static final String WHAC_EXTENSION = "WHAC";
    public static final String WHAC_EXTENSION_XML = "WHAC.XML";
	public static final String TIER_EXTENSION = "TIER";
    public static final String TIER_EXTENSION_XML = "TIER.XML";
	
	/**
	 * directory for imported data files
	 */
	public static final String IMPORT_FILES_DIR = "import";
	public static final String ARMY_LISTS_DIR = "armylists";
	public static final String BATTLE_RESULTS_DIR = "battleresults";
	
	
	private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
		'\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', '.', ' ', ',' };

	
	public static boolean createArmyListDirectory(Context context, String fullPath) {
		Log.d(TAG,"createArmyListDirectory");
		try {
			
			File createdDir = new File(fullPath);
			boolean created = createdDir.mkdir();
			return created;
			
		} catch (Exception e) {
			Log.e("StorageManager - createArmyListDirectory", e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return false;
	}
	
	public static List<ArmyListDirectory> getArmyListDirectories(Context context, String fullPath) {
		Log.d(TAG,"getArmyListDirectories");
		
		ArrayList<ArmyListDirectory> result = new ArrayList<ArmyListDirectory>();
		try {
			
			File rootFile = getArmyListsDirFile(context);  
					// context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
			
			File searchDirectory;
			if (fullPath != null) {
				searchDirectory = new File(fullPath);
			} else {
				searchDirectory = rootFile;
			}
			
			File[] armies = searchDirectory.listFiles();
			
			if (fullPath != null && ! fullPath.equals(rootFile.getPath()) ) {
				// create "UP" folder
				result.add(new ArmyListDirectory(searchDirectory.getParent(), true, "Root"));	
			}
			
			for (File dir : armies) {
				
				if (dir.isDirectory()) {
					
					result.add(new ArmyListDirectory(dir.getPath(), dir.getName()));	
				} else {
					// skip
				}
				
			}
			
			Collections.sort(result);
			
			Log.d(TAG,"armies = " + result.toString());
			
		} catch (Exception e) {
			Log.e("StorageManager - getArmyLists", e.toString());
			e.printStackTrace();
		} finally {
		}

		return result;

	}
	
	public static List<ArmyListDescriptor> getArmyLists(Context context) {
		return getArmyLists(context, null);
	}
	
	public static List<ArmyListDescriptor> getArmyLists(Context context, String fullPath) {
		Log.d(TAG,"getArmyLists");
		
		ArrayList<ArmyListDescriptor> result = new ArrayList<ArmyListDescriptor>();
		
		File rootFile = getArmyListsDirFile(context); 
		// context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);

		File searchDirectory;
		if (fullPath != null) {
			searchDirectory = new File(fullPath);
		} else {
			searchDirectory = rootFile;
		}

		// File dirFile = context.getDir(completePath, Context.MODE_PRIVATE);
		File[] armies = searchDirectory.listFiles();

		for (File army : armies) {

			if (army.isDirectory()) {
				// skip
			} else {
				try {
					FileInputStream fis = new FileInputStream(army);
					JSONTokener tokener = new JSONTokener(fis);
					ArmyStore store = JsonConverter.readArmyObject(tokener);
					ArmyListDescriptor descriptor = new ArmyListDescriptor(store, army.getPath());
					result.add(descriptor);
					fis.close();
				} catch (Exception e) {
					Log.e("StorageManager", "getArmyLists : file incorrect", e);
				} 
			}
		}

		Collections.sort(result);

		if (D) Log.d(TAG,"armies = " + result.toString());

		return result;
	}

	public static List<BattleListDirectory> getBattleListDirectories(Context context) {
		return getBattleListDirectories(context, null);
	}
		
	public static List<BattleListDirectory> getBattleListDirectories(Context context, String fullPath) {
		Log.d(TAG,"getBattleListDirectories");
		
		ArrayList<BattleListDirectory> result = new ArrayList<BattleListDirectory>();
		try {
			
			File rootFile = new File(getBattlesPath(context)); 
					// context.getDir(BATTLE_LISTS_DIR, Context.MODE_PRIVATE);
			
			File searchDirectory;
			if (fullPath != null) {
				searchDirectory = new File(fullPath);
			} else {
				searchDirectory = rootFile;
			}
			
			File[] armies = searchDirectory.listFiles();
			
			if (fullPath != null && ! fullPath.equals(rootFile.getPath()) ) {
				// create "UP" folder
				result.add(new BattleListDirectory(searchDirectory.getParent(), true, "Root"));	
			}
			
			for (File dir : armies) {
				
				if (dir.isDirectory()) {
					
					result.add(new BattleListDirectory(dir.getPath(), dir.getName()));	
				} else {
					// skip
				}
				
			}
			
			Collections.sort(result);
			
			Log.d(TAG,"armies = " + result.toString());
			
		} catch (Exception e) {
			Log.e("StorageManager","getBattleListDirectories", e);
			e.printStackTrace();
		} finally {
		}

		return result;

	}
	
	public static ArrayList<BattleListDescriptor> getBattleLists(Context context) {
		return getBattleLists(context, null);
	}
	
	public static ArrayList<BattleListDescriptor> getBattleLists(Context context, String fullPath) {
		Log.d(TAG,"getBattleLists");
		ArrayList<BattleListDescriptor> result = new ArrayList<BattleListDescriptor>();
		
		
		try {
			File rootFile = new File(getBattlesPath(context));
			File searchDirectory;
			if (fullPath != null) {
				searchDirectory = new File(fullPath);
			} else {
				searchDirectory = rootFile;
			}
			
			File[] armies = searchDirectory.listFiles();
			
			for (File army : armies) {
				
				if (army.isDirectory()) {
					// skip
				} else {
					Log.d(TAG,"getBattleLists : army = " + army.getName());
					FileInputStream fis = new FileInputStream(army);
	
					try {
						JSONTokener tokener = new JSONTokener(fis);
						BattleListDescriptor descriptor = JsonConverter.readBattleDescriptor(tokener);
						descriptor.setFilePath(army.getPath());
						result.add(descriptor);
					} catch (Exception e) {
						// file is not a battle 
						Log.w(TAG, "file " + army.getPath() + " is not a valid battle file -- IGNORED");
					}

					fis.close();
				}
			}
			Log.d(TAG,"battles = " + result.toString());
		} catch (Exception e) {
			Log.e("StorageManager - getArmyLists", e.toString());
			e.printStackTrace();
		} finally {
		}

		Collections.sort(result);
		return result;
	}

	public static boolean  existsArmyList(Context context, String filename) {
		try {
			File dirFile = getArmyListsDirFile(context); 
					// context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
			
			File fileToCheck = new File(dirFile, filename);
			
			return fileToCheck.exists();
		} catch (Exception e) {
			Log.e("StorageManager - existsArmyList", e.getMessage());
		} finally {
		}
		return false;
	}

	
	public static boolean  deleteArmyList(Context context, String filename) {
		Log.d(TAG, "deleteArmyList - " + filename);
		try {
			File fileToDelete = new File(filename);
			
			return fileToDelete.delete();
		} catch (Exception e) {
			Log.e("StorageManager - deleteArmyList", e.toString());
			e.printStackTrace();
		} finally {
		}
		return false;
	}
	
	public static boolean  deleteImportedFile(Context context, File fileToDelete) {
		Log.d(TAG, "deleteImportedFile - " + fileToDelete);
		try {
			return fileToDelete.delete();
		} catch (Exception e) {
			Log.e("StorageManager - deleteImportedFile", e.toString());
			e.printStackTrace();
		} finally {
		}
		return false;
	}

	public static boolean  deleteBattle(Context context, String filePath) {
		try {
			File fileToDelete = new File(filePath);
			
			return fileToDelete.delete();
		} catch (Exception e) {
			Log.e("StorageManager - deleteBattle", e.toString());
			e.printStackTrace();
		} finally {
		}
		return false;
	}
	public static boolean  renameArmyList(Context context, String oldFilename, String newFilename) {
		try {
			File dirFile = getArmyListsDirFile(context); 
				// context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
			
			File fileToRename = new File(dirFile, oldFilename);
			File newFile = new File(dirFile, newFilename);
			
			return fileToRename.renameTo(newFile);
		} catch (Exception e) {
			Log.e("StorageManager - renameArmyList", e.getMessage());
		} finally {
		}
		return false;
	}

	public static boolean  copyArmyList(Context context, String oldFilename, String newFilename) {
		Log.d(TAG, "copyArmyList");
		File dirFile = getArmyListsDirFile(context); 
			//context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
		File fileToRead = new File(dirFile, oldFilename);
		File fileToWrite = new File(dirFile, newFilename);
		
		if (!oldFilename.equals(newFilename)) {
			try {
				FileOutputStream outStream = new FileOutputStream(fileToWrite);

				FileInputStream inStream = new FileInputStream(fileToRead);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				inStream.close();
				outStream.close();
				return true;
			} catch (Exception e) {
				Log.e("StorageManager - copyArmyList", e.getMessage());
			} finally {
			}

		}
		return false;
	}	


	public static String fixFileNameForSave(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); ++i) {
			if (!isIllegalFileNameChar(s.charAt(i)))
				sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	private static boolean isIllegalFileNameChar(char c) {
		boolean isIllegal = false;
		for (int i = 0; i < ILLEGAL_CHARACTERS.length; i++) {
			if (c == ILLEGAL_CHARACTERS[i])
				isIllegal = true;
		}
		return isIllegal;
	}
	
	public static boolean saveArmyList(Context applicationContext, SelectionModelSingleton singleton) {
		String armyPath = singleton.getArmyFilePath();
		String armyName = singleton.getArmyFileName();
		Log.d(TAG, "saveArmyList, name = " + armyName);
		
		FileOutputStream fos = null;
		boolean keep = true;

		String validArmyName = armyName; // already fixed!
		

		// write to whac dir.. accessible to user
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalDirPath = externalStorageDir.getPath() + WHAC_SUBDIR;
		File whacExternalDir = new File(whacExternalDirPath);

		
		
		try {
			
			File fileToSave;
			if (armyPath == null) {
				// File dirFile = applicationContext.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
				fileToSave = new File(whacExternalDir, armyName);
			} else {
				fileToSave = new File(armyPath);
			}
			
			// fileToSave.createNewFile();
			fos = new FileOutputStream(fileToSave);
			
//			
//			fos = applicationContext.openFileOutput(validArmyName,
//					Context.MODE_PRIVATE);
//			;
			// oos = new ObjectOutputStream(fos);
			
			ArmyStore store = new ArmyStore(armyName);
			store.setFilename(fileToSave.getName());
			store.setFactionId(singleton.getFaction().getId());
			store.setNbCasters(singleton.getNbCasters());
			store.setNbPoints(singleton.getNbPoints());
			store.setEntries(singleton.getSelectedEntries());
			store.setTierId(singleton.getCurrentTiers()==null?"":singleton.getCurrentTiers().getTitle());
			store.setContractId(singleton.getCurrentContract()==null?"":singleton.getCurrentContract().getTitle());
			
			// oos.writeObject(store);

			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			JsonConverter.createArmyObject(writer, store);
			writer.flush();

			singleton.setArmyFilePath(fileToSave.getPath());
			singleton.setArmyFileName(validArmyName); 
			singleton.setSaved(true);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			keep = false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
				e.printStackTrace();
			}
		}

		Log.d(TAG, "army list saved, name = " + armyName);
		return keep;
	}	
	

	

	
	
	public static boolean saveBattle(Context applicationContext, String armyPath, BattleSingleton singleton) {
		
		Log.d(TAG, "saveBattle, name = " + armyPath);
		
		FileOutputStream fos = null;
		boolean keep = true;

		// write to whac dir.. accessible to user
		// String battleRootDir = getBattlesPath(applicationContext);
		
		try {
			
			File fileToSave = new File(armyPath);
			File parentDir = fileToSave.getParentFile();
			if ( ! parentDir.exists() ) {
				// create dirs
				parentDir.mkdirs();
			}
			
			fileToSave.createNewFile();
			fos = new FileOutputStream(fileToSave);
			
			BattleStore store = new BattleStore();
			store.setFilename(fileToSave.getName());
			store.setTitle(fileToSave.getName());
			store.setArmy(singleton.getArmy(BattleSingleton.PLAYER1), BattleSingleton.PLAYER1);
			store.setArmy(singleton.getArmy(BattleSingleton.PLAYER2), BattleSingleton.PLAYER2);
			store.setBattleEntries(singleton.getEntries(BattleSingleton.PLAYER1), BattleSingleton.PLAYER1);
			store.setBattleEntries(singleton.getEntries(BattleSingleton.PLAYER2), BattleSingleton.PLAYER2);
			
			store.setPlayer1TimeRemaining(singleton.getPlayer1Chrono().getTimeRemainingMillis());
			store.setPlayer2TimeRemaining(singleton.getPlayer2Chrono().getTimeRemainingMillis());

			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			JsonConverter.createBattleObject(writer, store);
			writer.flush();		
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			keep = false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
				e.printStackTrace();
			}
		}
		
		Log.d(TAG, "saved battle, name = " + armyPath);
		// File battleListsDir = applicationContext.getDir(BATTLE_LISTS_DIR, Context.MODE_PRIVATE);
		
//		FileOutputStream fos = null;
//		ObjectOutputStream oos = null;
//		boolean keep = true;
//
//		// String validArmyName = fixFileNameForSave(armyName); already fixed!
//		
//		try {
//			File fileToSave = new File(armyPath);
//			
//			File parentDir = fileToSave.getParentFile();
//			if ( ! parentDir.exists() ) {
//				// create dirs
//				parentDir.mkdirs();
//			}
//			
//			fileToSave.createNewFile();
//			fos = new FileOutputStream(fileToSave);
//			oos = new ObjectOutputStream(fos);
//			
//			BattleStore store = new BattleStore();
//			store.setFilename(fileToSave.getName());
//			store.setTitle(fileToSave.getName());
//			store.setArmy(singleton.getArmy(BattleSingleton.PLAYER1), BattleSingleton.PLAYER1);
//			store.setArmy(singleton.getArmy(BattleSingleton.PLAYER2), BattleSingleton.PLAYER2);
//			store.setBattleEntries(singleton.getEntries(BattleSingleton.PLAYER1), BattleSingleton.PLAYER1);
//			store.setBattleEntries(singleton.getEntries(BattleSingleton.PLAYER2), BattleSingleton.PLAYER2);
//			
//			store.setPlayer1TimeRemaining(singleton.getPlayer1Chrono().getTimeRemainingMillis());
//			store.setPlayer2TimeRemaining(singleton.getPlayer2Chrono().getTimeRemainingMillis());
//			
//			oos.writeObject(store);
//		} catch (Exception e) {
//			keep = false;
//		} finally {
//			try {
//				if (oos != null)
//					oos.close();
//				if (fos != null)
//					fos.close();
//			} catch (Exception e) { /* do nothing */
//				e.printStackTrace();
//			}
//		}

		Log.d(TAG, "battle saved, name = " + armyPath);
		return keep;
	}		
	/**
	 * load army list given by file-name into singleton
	 * @param armyFilePath
	 * @param singleton
	 * @return
	 */
	public static boolean loadArmyList(String armyFilePath, SelectionModelSingleton singleton) {
		FileInputStream fis = null;
		// ObjectInputStream is = null;

		try {
			File fileToRead = new File(armyFilePath);
			
			fis = new FileInputStream(fileToRead);
			// is = new ObjectInputStream(fis);

			JSONTokener tokener = new JSONTokener(fis);
			ArmyStore store = JsonConverter.readArmyObject(tokener);
			
//			ArmyStore store = (ArmyStore) is.readObject();
			
			singleton.setFaction(FactionNamesEnum.getFaction(store.getFactionId()));
			singleton.cleanAll();
			
			singleton.setNbCasters(store.getNbCasters());
			singleton.setNbPoints(store.getNbPoints());
			
			
			singleton.getSelectedEntries().clear();
			singleton.getSelectedEntries().addAll(store.getEntries());
			
			if (store.getTierId() != null && store.getTierId().length() > 0) {
				// actually, the tier id contains the title!
				singleton.setCurrentTiers(ArmySingleton.getInstance().getTier(store.getTierId()));	
			}
			if (store.getContractId() != null && store.getContractId().length() > 0) {
				singleton.setCurrentContract(ArmySingleton.getInstance().getContract(store.getContractId()));
			}
			
			singleton.setArmyFilePath(armyFilePath);
			singleton.setArmyFileName(fileToRead.getName());
			singleton.setSaved(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
//				if (is != null)
//					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}	
	
	/**
	 * indicates that a battle status was saved for army_name
	 * @param applicationContext
	 * @param armyPath
	 * @return
	 */
	public static boolean isExistingBattle(Context applicationContext, String armyPath) {

		try {
			File fileToRead = new File(armyPath);
			return fileToRead.exists();
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
		}
		return false;
	}
	
	/**
	 * load army list given by file-name into singleton
	 * @param applicationContext
	 * @param armyPath
	 * @param singleton
	 * @return
	 */
	public static boolean loadExistingBattle(Context applicationContext, String armyPath, BattleSingleton singleton) {
		Log.d(TAG, "loadExistingBattle, name = " + armyPath);
		FileInputStream fis = null;

		boolean loadComplete = true;
		try {
			
			// File armyListsDir = applicationContext.getDir(BATTLE_LISTS_DIR, Context.MODE_PRIVATE);
			File fileToRead = new File(armyPath);
			
			fis = new FileInputStream(fileToRead);

			JSONTokener tokener = new JSONTokener(fis);
			BattleStore store =JsonConverter.readBattleStore(tokener);
			
			// (BattleStore) is.readObject();
			
			singleton.setArmy(store.getArmy(BattleSingleton.PLAYER1), BattleSingleton.PLAYER1);
			singleton.setArmy(store.getArmy(BattleSingleton.PLAYER2), BattleSingleton.PLAYER2);

			singleton.getEntries(BattleSingleton.PLAYER1).clear();
			singleton.getEntries(BattleSingleton.PLAYER1).addAll(store.getBattleEntries(BattleSingleton.PLAYER1));
			
			singleton.getEntries(BattleSingleton.PLAYER2).clear();
			singleton.getEntries(BattleSingleton.PLAYER2).addAll(store.getBattleEntries(BattleSingleton.PLAYER2));

			singleton.getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
			singleton.getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
			singleton.getPlayer1Chrono().setInitialPlayerTimeInMillis(store.getPlayer1TimeRemaining());
			singleton.getPlayer2Chrono().setInitialPlayerTimeInMillis(store.getPlayer2TimeRemaining());
			
//			for (BattleEntry entry : store.getBattleEntries(BattleSingleton.PLAYER1)) {
//				ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
//				entry.setReference(element);
//			}
//
//			for (BattleEntry entry : store.getBattleEntries(BattleSingleton.PLAYER2)) {
//				ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
//				entry.setReference(element);
//			}

			
		} catch (Exception e) {
			e.printStackTrace();
			loadComplete = false;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.d(TAG, "battle loaded");
		Log.d(TAG, "entries stored in singleton = " + singleton.getEntries(BattleSingleton.PLAYER1).toString());
		return loadComplete;
	}		
	
	
	
	/**
	 * load army list given by file-name into singleton
	 * @param applicationContext
	 * @param armyPath
	 * @param singleton
	 * @return
	 */
	public static boolean createBattleFromArmy(Context applicationContext, String armyPath, BattleSingleton singleton, int playerNumber) {
		Log.d(TAG, "createBattleFromArmy" + armyPath);
		FileInputStream fis = null;
//		ObjectInputStream is = null;

		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		String prefsMinutes = sharedPref.getString(PreferenceConstants.TIMER_MINUTES, "60");
		
		singleton.reInitAndConfigChrono(Integer.parseInt(prefsMinutes));
		
		List<BattleEntry> entries = new ArrayList<BattleEntry>();
		
		int entryCounter = 1;
		
		try {
			
			File fileToRead = new File(armyPath);
			
			fis = new FileInputStream(fileToRead);
			
			JSONTokener tokener = new JSONTokener(fis);
			ArmyStore store = JsonConverter.readArmyObject(tokener);

			
//			is = new ObjectInputStream(fis);
//
//			ArmyStore store = (ArmyStore) is.readObject();
			singleton.setArmy(store, playerNumber);
			
			List<SelectedEntry> result = (List<SelectedEntry>) store.getEntries();
			Log.d(TAG, "read List<SelectedEntry> = "
					+ result.toString());
			
			Collections.sort(result);
			// selected entries are pre-sorted before calcutating ids
			
			for (SelectedEntry entry : result) {
				
				
				
				ArmyElement element = ArmySingleton.getInstance().getArmyElement(entry.getId());
				
				BattleEntry bEntry = null; 
				if (element instanceof ArmyCommander) {
					if (element.getModels().size() == 1) {
						
						if(element.getModels().get(0).getHitpoints() instanceof WarjackDamageGrid) {
							// karchev!
							bEntry = new KarchevEntry(element, entryCounter++, entry.getCost());
						} else {
							bEntry = new SingleDamageLineEntry(element, entryCounter++, 0, false);
						}
						
							
					} else {
						bEntry = new MultiPVUnit(entry, element, entryCounter++, entry.getCost(), entry.isSpecialist());
					}
					entries.add(bEntry);
				} else if (element instanceof Unit) {
					Unit unit = (Unit) element;
					bEntry = new MultiPVUnit((SelectedUnit) entry, unit, entryCounter++, entry.getCost(), entry.isSpecialist());
					entries.add(bEntry);
					
					if ( ((SelectedUnit) entry).getRankingOfficer() != null) {
						SelectedRankingOfficer ra = ((SelectedUnit) entry).getRankingOfficer();
						ArmyElement raDescription = ArmySingleton.getInstance().getArmyElement(ra.getId());
						BattleEntry raEntry = new MultiPVUnit(ra, (ArmyElement) raDescription, entryCounter++, ra.getCost(), ra.isSpecialist());
						raEntry.setAttached(true);
						raEntry.setParentId(bEntry.getUniqueId());
						//bEntry.getChilds().add(raEntry);
						entries.add(raEntry);
					}
					
					if ( ((SelectedUnit) entry).getSoloAttachment() != null) {
						SelectedSolo solo = ((SelectedUnit) entry).getSoloAttachment();
						ArmyElement soloDescription = ArmySingleton.getInstance().getArmyElement(solo.getId());
						BattleEntry soloEntry = new MultiPVUnit(solo, (ArmyElement) soloDescription, entryCounter++, solo.getCost(), solo.isSpecialist());
						soloEntry.setAttached(true);
						soloEntry.setParentId(bEntry.getUniqueId());
						//bEntry.getChilds().add(raEntry);
						entries.add(soloEntry);
					}
					
					if ( ((SelectedUnit) entry).getUnitAttachment() != null) {
						SelectedUA ua = ((SelectedUnit) entry).getUnitAttachment();
						ArmyElement uaDescription = ArmySingleton.getInstance().getArmyElement(ua.getId());
						BattleEntry uaEntry = new MultiPVUnit(ua, (ArmyElement) uaDescription, entryCounter++, ua.getCost(), ua.isSpecialist());
						uaEntry.setAttached(true);
						uaEntry.setParentId(bEntry.getUniqueId());
						//bEntry.getChilds().add(uaEntry);
						entries.add(uaEntry);
					}
					
					if ( ! ((SelectedUnit) entry).getWeaponAttachments().isEmpty() ) {
						for ( SelectedWA wa : ((SelectedUnit) entry).getWeaponAttachments()) {
							ArmyElement waDescription = ArmySingleton.getInstance().getArmyElement(wa.getId());
							BattleEntry waEntry = new MultiPVUnit(wa, waDescription, entryCounter++, wa.getCost(), wa.isSpecialist() );
							waEntry.setAttached(true);
							waEntry.setParentId(bEntry.getUniqueId());
							//bEntry.getChilds().add(waEntry);
							entries.add(waEntry);
						}
					}

				} else if (element instanceof Solo){
					if (element.hasMultiPVModels()) {
						if (element.getModels().size() == 1) {
							bEntry = new SingleDamageLineEntry(element, entryCounter++, entry.getCost(), entry.isSpecialist() );
						} else {
							// dragoon
							bEntry = new MultiPVUnit(entry, element, entryCounter++, entry.getCost(), entry.isSpecialist());
						}
					} else {
						bEntry = new BattleEntry(element, entryCounter++, entry.getCost(), entry.isSpecialist());
					}
					entries.add(bEntry);
				} else if (element instanceof BattleEngine) {
					bEntry = new SingleDamageLineEntry(element, entryCounter++, entry.getCost(), entry.isSpecialist());
					entries.add(bEntry);
				} else {
					bEntry = new BattleEntry(element, entryCounter++, entry.getCost(), entry.isSpecialist());
					entries.add(bEntry);
				}

				// attach descendants
				if (entry instanceof JackCommander) {
					for (SelectedWarjack jack : ((JackCommander) entry).getJacks()) {
						Warjack aJack = (Warjack) ArmySingleton.getInstance().getArmyElement(jack.getId());
						JackEntry jackBattleEntry = new JackEntry(aJack, bEntry, entryCounter++, jack.getCost(), jack.isSpecialist());
						entries.add(jackBattleEntry);
						//bEntry.getChilds().add(jackBattleEntry);
					}
				}
				
				if (entry instanceof BeastCommander) {
					for (SelectedWarbeast beast : ((BeastCommander) entry).getBeasts()) {
						Warbeast aBeast = (Warbeast) ArmySingleton.getInstance().getArmyElement(beast.getId());
						
						if (aBeast instanceof WarbeastPack) {
							BeastPackEntry beastBattleEntry = new BeastPackEntry(beast, aBeast, bEntry,  entryCounter++, beast.getCost(), beast.isSpecialist());
							entries.add(beastBattleEntry);
						} else {
							BeastEntry beastBattleEntry = new BeastEntry(aBeast, bEntry, entryCounter++, beast.getCost(), beast.isSpecialist());
							entries.add(beastBattleEntry);
						}
						
						//bEntry.getChilds().add(beastBattleEntry);
					}
				}
				
				if (entry instanceof SelectedArmyCommander) {
					if ( ((SelectedArmyCommander)entry).getAttachment() != null) {
                        SelectedModel model = ((SelectedArmyCommander) entry).getAttachment();
						Solo attachment = (Solo) ArmySingleton.getInstance().getArmyElement(((SelectedArmyCommander)entry).getAttachment().getId());
						BattleEntry soloEntry = null; 
						if (attachment.hasMultiPVModels()) {
							if (attachment.getModels().size() == 1) {
								soloEntry = new SingleDamageLineEntry(attachment, entryCounter++, model.getCost(), model.isSpecialist() );
							} else {
								// dragoon
								soloEntry = new MultiPVUnit(entry, attachment, entryCounter++, model.getCost(), model.isSpecialist() );
							}
						} else {
							soloEntry = new BattleEntry(attachment, entryCounter++, model.getCost(), model.isSpecialist());
						}
						entries.add(soloEntry);
						soloEntry.setAttached(true);
						soloEntry.setParentId(bEntry.getUniqueId());
						//bEntry.getChilds().add(soloEntry);
					}
				}
				
				singleton.getEntries(playerNumber).clear();
				singleton.getEntries(playerNumber).addAll(entries);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
//				if (is != null)
//					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static ArrayList<File> getImportedDataFiles(Context applicationContext) {
		File importFilesDir = applicationContext.getDir(IMPORT_FILES_DIR, Context.MODE_PRIVATE);
		
//		File externalStorageDir = Environment.getExternalStorageDirectory ();
//		String whacExternalDirPath = externalStorageDir.getPath() + "/Whac";
//		File whacExternalDir = new File(whacExternalDirPath);
		
		
		File[] files = importFilesDir.listFiles();
		
		ArrayList<File> result = new ArrayList<File>();
		for (File file : files) {
			result.add(file);
		}
				
		return result;
	}

	@SuppressLint("NewApi")
	public static List<File> getDataFilesToImport(Context applicationContext, String subdir) {
		File storageDir = null;
		
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
    		if (subdir.equals(DOWNLOAD_SUBDIR)) {
    			storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    		} else {
    			File externalStorageDir = Environment.getExternalStorageDirectory ();	
    			String whacExternalDirPath = externalStorageDir.getPath() + subdir; 
    			storageDir = new File(whacExternalDirPath);
    		}
        } else {
			File externalStorageDir = Environment.getExternalStorageDirectory ();	
			String whacExternalDirPath = externalStorageDir.getPath() + subdir; 
			storageDir = new File(whacExternalDirPath);
        }
		

		File[] files = storageDir.listFiles();
		ArrayList<File> result = new ArrayList<File>();
		if (files != null) {
			for (File file : files) {
				
//				String extension = "";

                if ( file.getName().toUpperCase().endsWith(WHAC_EXTENSION) || file.getName().toUpperCase().endsWith(WHAC_EXTENSION_XML)) {
                    result.add(file);
                }

//				int i = file.getName().lastIndexOf('.');
//				if (i >= 0) {
//				    extension = file.getName().substring(i+1);
//				}
//
//				if (WHAC_EXTENSION.equalsIgnoreCase(extension)) {
//					result.add(file);
//				}
				
			}
		}
		return result;
	}
	
	@SuppressLint("NewApi")
	public static List<File> getTiersFilesToImport(Context applicationContext, String subdir) {
		File storageDir = null;
		
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
    		if (subdir.equals(DOWNLOAD_SUBDIR)) {
    			storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    		} else {
    			File externalStorageDir = Environment.getExternalStorageDirectory ();	
    			String whacExternalDirPath = externalStorageDir.getPath() + subdir; 
    			storageDir = new File(whacExternalDirPath);
    		}
        } else {
			File externalStorageDir = Environment.getExternalStorageDirectory ();	
			String whacExternalDirPath = externalStorageDir.getPath() + subdir; 
			storageDir = new File(whacExternalDirPath);
        }
		

		File[] files = storageDir.listFiles();
		ArrayList<File> result = new ArrayList<File>();
		if (files != null) {
			for (File file : files) {


                if ( file.getName().toUpperCase().endsWith(TIER_EXTENSION) || file.getName().toUpperCase().endsWith(TIER_EXTENSION_XML)) {
                    result.add(file);
                }

//
//                String extension = "";
//
//				int i = file.getName().lastIndexOf('.');
//				if (i >= 0) {
//				    extension = file.getName().substring(i+1);
//				}
//
//				if (TIER_EXTENSION.equalsIgnoreCase(extension)) {
//					result.add(file);
//				}
				
			}
		}
		return result;
	}
	
	public static boolean importDataFiles(Context applicationContext) {
		Log.d(TAG,"importDataFiles");
		File importFilesDir = applicationContext.getDir(IMPORT_FILES_DIR, Context.MODE_PRIVATE);
		
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalDirPath = externalStorageDir.getPath() + WHAC_SUBDIR;
		File whacExternalDir = new File(whacExternalDirPath);

		File[] files = whacExternalDir.listFiles();
		
		for (File fileToRead : files) {
			Log.d(TAG,"copying file : " + fileToRead.toString());
			File fileToWrite = new File(importFilesDir, fileToRead.getName());
			try {
				FileOutputStream outStream = new FileOutputStream(fileToWrite);

				FileInputStream inStream = new FileInputStream(fileToRead);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				inStream.close();
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("StorageManager - importDataFiles", e.getMessage());
			} finally {
			}
		}
		
		return true;

	}
	
	public static boolean importDataFileFromFile(Context applicationContext, String fileName, File content) {
		Log.d(TAG,"importDataFileFromInternet");
		File importFilesDir = applicationContext.getDir(IMPORT_FILES_DIR, Context.MODE_PRIVATE);
		
		Log.d(TAG,"creating file : " + fileName);
		File fileToWrite = new File(importFilesDir, fileName);
		try {
			FileOutputStream outStream = new FileOutputStream(fileToWrite);
			FileInputStream inStream = new FileInputStream(content);
			// copy the file content in bytes
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = inStream.read(buf)) > 0){
				outStream.write(buf, 0, len);
			}
			inStream.close();
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("StorageManager - importDataFileFromInternet", e.getMessage());
		} finally {
		}
		
		return true;

	}

	public static void saveBattleResult(Context applicationContext, BattleResult result, boolean isUpdate) {
		Log.d(TAG, "saveBattleResult, name = " + result.getArmyName());
		
		FileOutputStream fos = null;
		String validArmyName = fixFileNameForSave(result.getArmyName());
		
		if (isUpdate) {
			 // do nothing, the file name is fine
			validArmyName = result.getFilename();
		} else {
			// prepend the date
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-kkmm",
					Locale.getDefault());
			if (validArmyName.length() > 8) {
				validArmyName = validArmyName.substring(0, 8);
			}
			validArmyName = sdf.format(new Date()) + "-" + validArmyName; 
		}
		

		try {
			File rootDir = new File(getBattleResultsPath(applicationContext));
			if ( ! rootDir.exists() ) {
				// create dirs
				rootDir.mkdirs();
			}

			
			File fileToSave = new File(rootDir, validArmyName);
			fileToSave.createNewFile();
			fos = new FileOutputStream(fileToSave);
			
			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			JsonConverter.createBattleResult(writer, result);
			writer.flush();
            writer.close();
			
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
				e.printStackTrace();
			}
		}

		Log.d(TAG, "battle result saved, name = " + result.getArmyName());
	}
	
	public static boolean  deleteBattleResult(Context context, String filename) {
		Log.d(TAG, "deleteBattleResult - " + filename);
		try {
			File dirFile = new File(getBattleResultsPath(context)); 
					// context.getDir(BATTLE_RESULTS_DIR, Context.MODE_PRIVATE);
			
			File fileToDelete = new File(dirFile, filename);
			
			return fileToDelete.delete();
		} catch (Exception e) {
			Log.e("StorageManager - deleteBattleResult", e.toString());
			e.printStackTrace();
		} finally {
		}
		return false;
	}
	

	public static List<BattleResult> getBattleResults(Context applicationContext) {
		Log.d(TAG,"getBattleResults");
		ArrayList<BattleResult> result = new ArrayList<BattleResult>();
		try {
			File dirFile = new File(getBattleResultsPath(applicationContext)); 
					// applicationContext.getDir(BATTLE_RESULTS_DIR, Context.MODE_PRIVATE);
			File[] results = dirFile.listFiles();
			
			for (File battleResult : results) {
				
				Log.d(TAG,"getBattleLists : army = " + battleResult.getName());
				FileInputStream fis = new FileInputStream(battleResult);

				try {
					
					JSONTokener tokener = new JSONTokener(fis);
					BattleResult store = JsonConverter.readBattleResult(tokener); 
					store.setFilename(battleResult.getName());
					 
					result.add(store);
				} catch (Exception e) {
					// can not read file --> delete is
					Log.w(TAG, "file " + battleResult.getName() + " can not be processed : will be deleted");
					try {
						fis.close();
						battleResult.delete();
					} catch (Exception ee) {
						Log.e(TAG, "failed to delete file " + battleResult.getName());
					}
				}
				
				fis.close();
			}
			Log.d(TAG,"results = " + result.toString());
		} catch (Exception e) {
			Log.e("StorageManager - getBattleResults", e.toString());
			e.printStackTrace();
		} finally {
		}

		return result;		
	}

	/**
	 * 
	 * @param applicationContext
	 * @return fileName
	 */
	public static String exportStats(Context applicationContext) {
		Log.d(TAG, "exportStats" );
		String filename = "";
		
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalDirPath = externalStorageDir.getPath() + WHAC_SUBDIR;
		File whacExternalDir = new File(whacExternalDirPath);

		List<BattleResult> results = getBattleResults(applicationContext);
		
		FileOutputStream fos = null;
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		String validArmyName = "export_stats-" + sdf.format(date) + ".csv";
		
		PrintStream ps; 
		
		try {
			File fileToSave = new File(whacExternalDir, validArmyName);
			fileToSave.createNewFile();
			
			filename = fileToSave.getPath();
			fos = new FileOutputStream(fileToSave);
			ps = new PrintStream(fos);
			
			ps.println("date;opponent;win/lose;scenario;clock type;win condition;personal note;battle short description");
			
			for (BattleResult br : results) {
				ps.print(br.getBattleDate());
				ps.print(";");
				ps.print(br.getPlayer2name());
				ps.print(";");
				ps.print(br.getWinnerNumber() == BattleResult.PLAYER_1_WINS ? "WIN":"LOST");
				ps.print(";");
				ps.print(br.getScenario());
				ps.print(";");
				ps.print(br.getClockType());
				ps.print(";");
				ps.print(br.getVictoryCondition());
				ps.print(";");
				
				String protectedNotes = br.getNotes().replace(";", "");
				protectedNotes = protectedNotes.replace("\n", "-");
				ps.print(protectedNotes);
				ps.print(";");
				ps.print(br.getDescription());
				ps.print(";");
				ps.println();	
			}
			
			ps.close();
			
		} catch (Exception e) {
			Log.w(TAG, e);
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
				e.printStackTrace();
			}
		}

		Log.d(TAG, "exportStats ended");
		return filename;
	}

	/**
	 * export the collection as text file
	 * @param applicationContext
	 * @return filename created
	 */
	public static String exportCollection(Context applicationContext) {
		Log.d(TAG, "exportCollection" );
		String filename = "";
		
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalDirPath = externalStorageDir.getPath() + WHAC_SUBDIR;
		File whacExternalDir = new File(whacExternalDirPath);

		FileOutputStream fos = null;
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		String validArmyName = "collection-" + sdf.format(date) + ".txt";
		
		PrintStream ps; 
		
		try {
			File fileToSave = new File(whacExternalDir, validArmyName);
			fileToSave.createNewFile();
			
			filename = fileToSave.getPath();
			fos = new FileOutputStream(fileToSave);
			ps = new PrintStream(fos);
			
			ps.println("my collection : " + DateFormat.getDateTimeInstance().format(date));
			
			generateCollectionText(ps, applicationContext);
			
			ps.close();
			
		} catch (Exception e) {
			Log.w(TAG, e);
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) { /* do nothing */
				e.printStackTrace();
			}
		}

		Log.d(TAG, "exportCollection ended");
		return filename;
	}

	public static void generateCollectionText(PrintStream ps, Context context) {
		HashMap<String, Integer> owned = CollectionSingleton.getInstance().getOwnedMap();
		HashMap<String, Integer> painted = CollectionSingleton.getInstance().getPaintedMap();
		
		HashMap<FactionNamesEnum, Integer> factionOwned = new HashMap<FactionNamesEnum, Integer>();
		HashMap<FactionNamesEnum, Integer> factionPainted = new HashMap<FactionNamesEnum, Integer>();
		
		List<ArmyElement> elementsToExport = new ArrayList<ArmyElement>();
		HashMap<String, String> exportData = new HashMap<String, String>();
		
		int totalOwned = 0;
		int totalPainted = 0;
		
		for (String key : owned.keySet()) {
			ArmyElement entry = ArmySingleton.getInstance().getArmyElement(key);
			
			if (entry != null) {
				if ( factionOwned.get(entry.getFaction()) == null) {
					factionOwned.put(entry.getFaction(), Integer.valueOf(0));
					factionPainted.put(entry.getFaction(), Integer.valueOf(0));
				}
				
				int ownedCount = owned.get(key).intValue();
				if (ownedCount > 0) {
					// don't export empty data... 
					totalOwned += ownedCount;
					factionOwned.put(entry.getFaction(), factionOwned.get(entry.getFaction()).intValue() + ownedCount);
					int paintedCount;
					if (painted.get(key) != null) {
						paintedCount = painted.get(key).intValue();
						totalPainted += paintedCount;
						factionPainted.put(entry.getFaction(), factionPainted.get(entry.getFaction()).intValue() + paintedCount);
					} else {
						paintedCount = 0; 
					}
					StringBuffer sb = new StringBuffer(128);
					
					sb.append(entry.getFullName());
					sb.append(" : " );
					sb.append(paintedCount);
					sb.append("/");
					sb.append(ownedCount);
					
					elementsToExport.add(entry);
					exportData.put(key, sb.toString());
				}				
			}

		}
		
		ps.println("owned total : " + totalOwned);
		ps.println("painted total : " + totalPainted);
		if (totalOwned > 0) {
			ps.println("completion : " + totalPainted * 100 / totalOwned + "%");	
		} else {
			ps.println("completion : irrelevant");
		}
		
		ps.println("");
		ps.println("format : [entry name : painted / owned]");
		
		Collections.sort(elementsToExport);
		FactionNamesEnum previousFaction = null;
		ModelTypeEnum previousType = null;
		for (ArmyElement element : elementsToExport) {
			
			if (element.getFaction() != previousFaction) {
				previousFaction = element.getFaction();
				previousType = element.getModelType();
				ps.println("--------------" + previousFaction.name() + "--------------");
				ps.println("faction progress : painted " +  factionPainted.get(previousFaction) + " / " + factionOwned.get(previousFaction) + " completion = " + factionPainted.get(previousFaction) * 100 / factionOwned.get(previousFaction) + "%" );
				ps.println("[--" + context.getResources().getString(previousType.getTitle()) + "--]");
			}
			if (element.getModelType() != previousType) {
				previousType = element.getModelType();
				ps.println("[--" + context.getResources().getString(previousType.getTitle()) + "--]");
			}
			ps.println(exportData.get(element.getId()));
		}
	}

	public static boolean deleteArmyFolder(Context context,
			String fullPath) {
		Log.d(TAG, "deleteArmyList - " + fullPath );
		try {
			File fileToDelete = new File(fullPath);
			deleteRecursive(fileToDelete);
			return true;
		} catch (Exception e) {
			Log.e("StorageManager - deleteArmyFolder", e.toString());
			e.printStackTrace();
		} finally {
		}
		return false;
	}
	
	private static void deleteRecursive(File file) {

		// Check if file is directory/folder
		if (file.isDirectory()) {
			// Get all files in the folder
			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				// Delete each file in the folder
				deleteRecursive(files[i]);
			}
			// Delete the folder
			file.delete();
		} else {
			// Delete the file if it is not a folder
			file.delete();
		}
	}

	public static String getArmyListsPath(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG,"getArmyListsPath");
		
		// write to whac dir.. accessible to user
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalArmiesPath = externalStorageDir.getPath() + WHAC_SUBDIR + File.separator + ARMIES_SUBDIR;
		File whacExternalDir = new File(whacExternalArmiesPath);
		
		try {
			return whacExternalDir.getPath();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return "";
		}
	}
	
	public static File getArmyListsDirFile(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG,"getArmyListsPath");
		
		// write to whac dir.. accessible to user
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalArmiesPath = externalStorageDir.getPath() + WHAC_SUBDIR + File.separator + ARMIES_SUBDIR;
		File whacExternalDir = new File(whacExternalArmiesPath);
		
		try {
			return whacExternalDir;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	/**
	 * @deprecated
	 * @param context
	 * @return
	 */
	public static String getOldArmyListsPath(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG,"getArmyListsPath");
		
		try {
			File rootFile = context.getDir(ARMY_LISTS_DIR, Context.MODE_PRIVATE);
			return rootFile.getPath();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return "";
		}
	}

	public static String getBattlesPath(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG,"getBattlesPath");
		
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalArmiesPath = externalStorageDir.getPath() + WHAC_SUBDIR + File.separator + BATTLES_SUBDIR;
		File whacExternalDir = new File(whacExternalArmiesPath);
		
		try {
			return whacExternalDir.getPath();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	public static String getBattleResultsPath(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG,"getBattleResultsPath");
		
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalArmiesPath = externalStorageDir.getPath() + WHAC_SUBDIR + File.separator + BATTLE_RESULTS_DIR;
		File whacExternalDir = new File(whacExternalArmiesPath);
		
		try {
			return whacExternalDir.getPath();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	

	public static boolean convertArmyListsToNewFormat(Context context) {
		
		String oldDirRoot = getOldArmyListsPath(context);
		
		File rootDir = new File(oldDirRoot);
		
		convertArmyDir(context, rootDir.getPath());
		
		return true;
	}
	
	private static boolean convertArmyDir(Context context, String oldPath) {
		
		Log.d(TAG, "converting dir : " + oldPath);
		String oldDirRoot = getOldArmyListsPath(context);
		File newDirRoot = getArmyListsDirFile(context);
		
		File rootDir = new File(oldPath);
		File[] files = rootDir.listFiles();
		
		for (File file : files) {
			if (file.isDirectory()) {
				// copy new directory structure, and treat inside files
				String newPath = file.getPath().replace(oldDirRoot, newDirRoot.getPath());
				File newDir = new File(newPath);
				if (! newDir.exists()) {
					Log.d(TAG, "making new dir : " + newPath);
					newDir.mkdir();
				}
				
				convertArmyDir(context, file.getPath());
			} else {
				convertArmyListToNewFormat(file.getPath(), context);
			}
		}
		
		return true;
	}
	
	private static boolean convertArmyListToNewFormat(String oldFilename, Context context) {
		
		Log.d(TAG, "converting army list : " + oldFilename);
		String oldDirRoot = getOldArmyListsPath(context);
		File newDirRoot = getArmyListsDirFile(context);
		
		String newFileName = oldFilename.replace(oldDirRoot, newDirRoot.getPath());
		
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ObjectInputStream is = null;

		try {
			File fileToRead = new File(oldFilename);
			File fileToWrite = new File(newFileName);
			
			fis = new FileInputStream(fileToRead);
			is = new ObjectInputStream(fis);
			fos = new FileOutputStream(fileToWrite);

			ArmyStore store = (ArmyStore) is.readObject();
			Log.d(TAG, "old army read");
			is.close();
			fis.close();
			
			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			JsonConverter.createArmyObject(writer, store);
			writer.flush();
			Log.d(TAG, "new army written");
			
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (is != null)
					is.close();
				if (fos != null) 
					fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
		
		
	}

    /**
     * renvoie l'extension WHAC_EXTENSION ou TIER_EXTENSION ou "" selon le type de fichier
     * @param file
     * @return
     */
    public static String extractFileExtension(File file) {

        if (file.getName().toUpperCase().endsWith(WHAC_EXTENSION) || file.getName().toUpperCase().endsWith(WHAC_EXTENSION_XML)) {
            return WHAC_EXTENSION;
        }

        if (file.getName().toUpperCase().endsWith(StorageManager.TIER_EXTENSION) || file.getName().toUpperCase().endsWith(TIER_EXTENSION_XML)) {
            return TIER_EXTENSION;
        }

//		// extract file extension
//		String extension = "";
//		int i = file.getName().lastIndexOf('.');
//		if (i >= 0) {
//			extension = file.getName().substring(i+1);
//		}
        return "";
    }

}
