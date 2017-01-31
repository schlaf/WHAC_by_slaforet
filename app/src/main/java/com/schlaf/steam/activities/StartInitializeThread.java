package com.schlaf.steam.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RadioButton;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.importation.ImportMK3Activity;
import com.schlaf.steam.activities.selectlist.selected.SpellCaster;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Capacity;
import com.schlaf.steam.data.Contract;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Spell;
import com.schlaf.steam.data.Tier;
import com.schlaf.steam.data.TierEntry;
import com.schlaf.steam.data.TierEntryGroup;
import com.schlaf.steam.data.TierFACostBenefit;
import com.schlaf.steam.data.TierLevel;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.json.JsonExtractor;
import com.schlaf.steam.storage.JsonConverter;
import com.schlaf.steam.storage.StorageManager;
import com.schlaf.steam.xml.ContractExtractor;
import com.schlaf.steam.xml.RulesExtractor;
import com.schlaf.steam.xml.SR2014Extractor;
import com.schlaf.steam.xml.TierExtractor;
import com.schlaf.steam.xml.XmlExtractor;

import org.json.compatibility.JSONWriter;

public class StartInitializeThread extends AsyncTask<String, Integer, Boolean> {
	
	private static final String OLD_ARMIES_CONVERTED = "old_armies_converted";
	private static final String CONVERSION_DONE = "done";

    private static final String DATABASE_VO = "whac";
    private static final String DATABASE_VF = "whac_fr";

    /** indicates that the app is currently fetching data */
    private boolean fetchFromInternet = false;

    String[] urls = {"https://api.mlab.com/api/1/databases/%database%_v2/collections/spells?s={'name':1}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/capacities?s={'name':1}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&l=10000",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&l=200",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&sk=200&l=200",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&sk=400&l=200",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&sk=600&l=200",
            "https://api.mlab.com/api/1/databases/%database%_v2/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&sk=800&l=1000"
    };

    String test = "https://api.mlab.com/api/1/databases/whac/collections/models?s={'name':1}&f={'models.mk2capacities':0,'models.mk2spells':0,'weapons.melee_weapons.capacity':0,'weapons.ranged_weapons.capacity':0,'models.spells.__text':0,'models.capacities.__text':0,'models.weapons.melee_weapon.capacities.__text':0,'models.weapons.ranged_weapon.capacities.__text':0}&apiKey=wcadeCXsaFhH5G4__crfJpZBdloyTTAa&l=10";

    SteamPunkRosterApplication application;
	ProgressDialog progressDialog;
	
	private static final String TAG = "StartInitializeThread";
	private boolean checkTiers = false;
	private boolean publishData = false;
	private LoadActivityInterface parent;
	
	public StartInitializeThread(LoadActivityInterface parent, SteamPunkRosterApplication application, ProgressDialog dialog) {
		super();
		this.parent = parent;
		this.application = application;
		this.progressDialog = dialog;
	}
	
	@Override
    protected void onPreExecute() {
      Log.d(TAG, "Pre-Execute");
      super.onPreExecute();
      progressDialog.setMax(8);
      progressDialog.setProgress(0);
      progressDialog.show();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		publishProgress(0);


		// create WHAC dir
		File externalStorageDir = Environment.getExternalStorageDirectory ();
		String whacExternalDirPath = externalStorageDir.getPath() + StorageManager.WHAC_SUBDIR;
		File whacExternalDir = new File(whacExternalDirPath);

		if (! whacExternalDir.exists() ) {
			boolean succeed = whacExternalDir.mkdirs();
			if (!succeed) {
				Log.w(TAG, "failed to create Whac subdir in SD card");
			}
		}


		Log.d(TAG, "doInBackground");
		Resources res = application.getResources();
		if (!ArmySingleton.getInstance().isFullyLoaded() || parent.forceDownload()) {


			boolean dataConnectionIsUp = false;
            boolean dataConnectionIsMobile = false;
            boolean importOnlyFromWifi = true;
			ConnectivityManager connMgr = (ConnectivityManager)
                    ((FragmentActivity) parent).getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				dataConnectionIsUp = true;

                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    dataConnectionIsMobile = true;
                }
			}


            SharedPreferences networkPrefs = application.getSharedPreferences(ImportMK3Activity.IMPORT_NETWORK, Context.MODE_PRIVATE);
            switch (networkPrefs.getString(ImportMK3Activity.NETWORK_KEY, ImportMK3Activity.WIFI) ) {
                case ImportMK3Activity.WIFI : {
                    importOnlyFromWifi = true;
                    break;
                }
                case ImportMK3Activity.MOBILE : {
                    importOnlyFromWifi = false;
                    break;
                }
            }

            if ( dataConnectionIsMobile && importOnlyFromWifi) {
                dataConnectionIsUp = false; // consider no network is available
            }


            // do not even try if no data connection...
			boolean tryToImportFromInternet = dataConnectionIsUp;

			SharedPreferences frequencyPrefs = application.getSharedPreferences(ImportMK3Activity.IMPORT_FREQUENCY, Context.MODE_PRIVATE);
			switch (frequencyPrefs.getString(ImportMK3Activity.FREQUENCY_KEY, ImportMK3Activity.NEVER)) {
				case ImportMK3Activity.NEVER: {
					tryToImportFromInternet = false;
					break;
				}
				case ImportMK3Activity.ONCE_PER_WEEK: {
                    // check last import date
                    long lastImportDateInMs = frequencyPrefs.getLong(ImportMK3Activity.LAST_IMPORT_DATE_MS, 0);
                    Date currentDate = new Date();
                    if ( currentDate.getTime() - 7 * 86400 * 100 > lastImportDateInMs ) {
                        tryToImportFromInternet = tryToImportFromInternet && true;
                    } else {
                        // not yet 7 days since last update
                        tryToImportFromInternet = false;
                    }

					break;
				}
				case ImportMK3Activity.ALWAYS: {
					tryToImportFromInternet = tryToImportFromInternet && true;
					break;
				}
			}


            if (parent.forceDownload()) {
                // if call from the import screen, download whatever the global prefs.
                tryToImportFromInternet = true;
            }


            // save new last import date
            Editor editor = frequencyPrefs.edit();
            editor.putLong(ImportMK3Activity.LAST_IMPORT_DATE_MS, (new Date()).getTime());
            editor.commit();


            JsonExtractor extractor = new JsonExtractor();


            String[] datas = {"spells", "capas", "models1", "models2", "models3", "models4", "models5"};



            SharedPreferences langagePrefs = application.getSharedPreferences(ImportMK3Activity.IMPORT_LANGAGE, Context.MODE_PRIVATE);
            String databaseName = ImportMK3Activity.US_EN; // langagePrefs.getString(ImportMK3Activity.LANGAGE_KEY, ImportMK3Activity.US_EN);
            // TODO create french database

            InputStream isData = null;
            if (tryToImportFromInternet) {

                fetchFromInternet = true;
                int publishStatus = 0;

                try {
                    int i = 0;
                    for ( String urlString : urls) {
                        URL url = new URL(urlString.replace("%database%", databaseName));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /* milliseconds */);
                        conn.setConnectTimeout(15000 /* milliseconds */);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        // Starts the query
                        int response = 404;
                        try {
                            conn.connect();
                            response = conn.getResponseCode();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                        if (response == 200) {
                            publishProgress(publishStatus);
                            publishStatus ++;
                            // copy input stream to inner file
                            isData = conn.getInputStream();
                            StorageManager.importDataFromInternet(application, isData, datas[i]);
                            isData.close();
                        }

                        i++;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                fetchFromInternet = false;

            }


            try {
                publishProgress( 1);

                InputStream innerSpellsStream = StorageManager.getDataStream(application, "spells");
                extractor.doExtractSpells(res, application, innerSpellsStream);
                innerSpellsStream.close();

                publishProgress( 2);

                InputStream innerCapasStream = StorageManager.getDataStream(application,  "capas" );
                extractor.doExtractCapacities(res, application, innerCapasStream);
                innerCapasStream.close();;

                publishProgress( 3);

                InputStream innerModelsStream = StorageManager.getDataStream(application,"models1" );
                extractor.doExtractModels(res, application, innerModelsStream);
                innerModelsStream.close();;

                publishProgress( 4);

                innerModelsStream = StorageManager.getDataStream(application,"models2" );
                extractor.doExtractModels(res, application, innerModelsStream);
                innerModelsStream.close();;

                publishProgress( 5);

                innerModelsStream = StorageManager.getDataStream(application,"models3" );
                extractor.doExtractModels(res, application, innerModelsStream);
                innerModelsStream.close();;

                publishProgress( 6);

                innerModelsStream = StorageManager.getDataStream(application,"models4" );
                extractor.doExtractModels(res, application, innerModelsStream);
                innerModelsStream.close();;

                publishProgress( 7);

                innerModelsStream = StorageManager.getDataStream(application,"models5" );
                extractor.doExtractModels(res, application, innerModelsStream);
                innerModelsStream.close();;

                ArmySingleton.getInstance().computeArmyElements();

                publishProgress( 8);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return false;
            }


//		        XmlExtractor extractor = new XmlExtractor(res, application);
//		        extractor.doExtract();
			Log.d(TAG, "army data loaded from internet");


			// extractor.extractImportedArmies(application, false);
			Log.d(TAG, "imported army data loaded");
//		        publishProgress( 2);

//	            TierExtractor tiersExtractor = new TierExtractor(res, application);
//	            tiersExtractor.doExtract();
//	            tiersExtractor.extractImportedTiers(application, false);

			Log.d(TAG, "tiers loaded");
//	            publishProgress( 3);

			ContractExtractor contractExtractor = new ContractExtractor(res, application);
			contractExtractor.doExtract();

			Log.d(TAG, "contracts loaded");
			publishProgress( 4);

			SR2014Extractor scenarioExtractor = new SR2014Extractor(res, application);
			scenarioExtractor.doExtract();

			Log.d(TAG, "scenarios loaded");

			RulesExtractor rulesExtractor = new RulesExtractor(res, application);
			rulesExtractor.doExtract();
			publishProgress( 5);

            if (checkTiers) {
                for ( String tierName : ArmySingleton.getInstance().getTiers().keySet()) {

                    Tier tier = ArmySingleton.getInstance().getTier(tierName);

                    for (TierLevel level : tier.getLevels()) {

                        for ( TierEntry entry : level.getOnlyModels()) {
                            if ( ArmySingleton.getInstance().getArmyElement(entry.getId()) == null) {
                                Log.e("TAG", "entry " + entry.getId() + " not found in tier " + tier.getTitle());
                            }
                        }

                        for ( TierFACostBenefit benefit : level.getBenefit().getAlterations()) {
                            if ( ArmySingleton.getInstance().getArmyElement(benefit.getEntry().getId()) == null) {
                                Log.e("TAG", "entry " + benefit.getEntry().getId() + " not found in tier benefit for level " + level.getLevel());
                            }
                        }

                        for ( TierEntryGroup entryGroup : level.getMustHaveModels()) {
                            for (TierEntry entry : entryGroup.getEntries()) {
                                if ( ArmySingleton.getInstance().getArmyElement(entry.getId()) == null) {
                                    Log.e("TAG", "entry " + entry.getId() + " not found in tier " + tier.getTitle());
                                }
                            }
                        }
                    }

                }

                for ( Contract contract : ArmySingleton.getInstance().getContracts() ) {
                    for ( TierEntry entry : contract.getOnlyModels()) {
                        if ( ArmySingleton.getInstance().getArmyElement(entry.getId()) == null) {
                            Log.e("TAG", "entry " + entry.getId() + " not found in contract " + contract.getTitle());
                        }
                    }
                    for ( TierFACostBenefit benefit : contract.getBenefit().getAlterations()) {
                        if ( ArmySingleton.getInstance().getArmyElement(benefit.getEntry().getId()) == null) {
                            Log.e("TAG", "entry " + benefit.getEntry().getId() + " not found in contract benefit for level ");
                        }
                    }
                }

            }
        }

        // create armies dir
        String whacArmiesDirPath = whacExternalDir.getPath() + File.separator + StorageManager.ARMIES_SUBDIR;
        File whacArmiesDir = new File(whacArmiesDirPath);

        if (! whacArmiesDir.exists() ) {
            boolean succeed = whacArmiesDir.mkdirs();
            if (!succeed) {
                Log.w(TAG, "failed to create Whac/armies subdir in SD card");
            }
        }

        ArmySingleton.getInstance().setFullyLoaded(true);



        return Boolean.TRUE;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPostExecute");
		super.onPostExecute(result);
		if (progressDialog == null ) {
			return;
		}
		progressDialog.setMessage("Initialization OK");
		progressDialog.setCancelable(true);
		progressDialog.dismiss();




        if (! ArmySingleton.getInstance().isFullyLoaded()) {
            ConnectivityManager connMgr = (ConnectivityManager) ((FragmentActivity) parent).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null || ! networkInfo.isConnected()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(((FragmentActivity) parent));
                // alert.setIcon(R.drawable.donate);
                alert.setTitle(R.string.connectivity_missing);
                alert.setMessage(R.string.connectivity_missing_explanation);
                alert.show();
            }

            // desactive tous les boutons.
            parent.blockButtons();
        } else {
            parent.restaureButtons();
        }



	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (progressDialog == null ) {
			return;
		}

        if (fetchFromInternet) {
            progressDialog.setMessage("downloading data from internet...");
        } else {
            switch (values[0]) {
                case 0 :
                    break;
                case 1 :
                    progressDialog.setMessage("loading imported spells...");
                    break;
                case 2 :
                    progressDialog.setMessage("loading imported capacities...");
                    break;
                case 3 :
                    progressDialog.setMessage("loading imported models.");
                    break;
                case 4 :
                    progressDialog.setMessage("loading imported models...");
                    break;
                case 5 :
                    progressDialog.setMessage("loading imported models.....");
                    break;
                case 6 :
                    progressDialog.setMessage("loading imported models.......");
                    break;
                case 7 :
                    progressDialog.setMessage("loading imported models..........");
                    break;
                case 8 :
                    progressDialog.setMessage("loading steamroller...");
                    break;
            }
        }
		progressDialog.setProgress(values[0]);
	}


	public void setParent(StartActivity parent) {
		this.parent = parent;
		if (parent == null) {
			progressDialog = null;
		}
	}


}
