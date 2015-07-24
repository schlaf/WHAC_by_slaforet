package com.schlaf.steam.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.schlaf.steam.SteamPunkRosterApplication;
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
	

	SteamPunkRosterApplication application;
	ProgressDialog progressDialog;
	
	private static final String TAG = "StartInitializeThread";
	private boolean checkTiers = true;
	private boolean publishData = false;
	private StartActivity parent;
	
	public StartInitializeThread(StartActivity parent, SteamPunkRosterApplication application, ProgressDialog dialog) {
		super();
		this.parent = parent;
		this.application = application;
		this.progressDialog = dialog;
	}
	
	@Override
    protected void onPreExecute() {
      Log.d(TAG, "Pre-Execute");
      super.onPreExecute();
      progressDialog.setMax(5);
      progressDialog.setProgress(0);
      progressDialog.show();
    }
	
	@Override
	protected Boolean doInBackground(String... params) {
		// publishProgress( new Integer(1));
		
		Log.d(TAG, "doInBackground");
	       Resources res = application.getResources();
	        if (! ArmySingleton.getInstance().isFullyLoaded()) {

		        XmlExtractor extractor = new XmlExtractor(res, application);
		        extractor.doExtract();
		        Log.d(TAG, "army data loaded");
		        publishProgress( 1);
	        
		        extractor.extractImportedArmies(application, false);
		        Log.d(TAG, "imported army data loaded");
		        publishProgress( 2);
		        
	            TierExtractor tiersExtractor = new TierExtractor(res, application);
	            tiersExtractor.doExtract();
	            tiersExtractor.extractImportedTiers(application, false);
	            
	            Log.d(TAG, "tiers loaded");
	            publishProgress( 3);
	            
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

                if (false) {
                    File externalStorageDir = Environment.getExternalStorageDirectory ();
                    String whacExternalDirPath = externalStorageDir.getPath() + StorageManager.WHAC_SUBDIR;

                    for (Faction faction : ArmySingleton.getInstance().getFactions().values()) {

                        File factionFile = new File(whacExternalDirPath, faction.getId() + ".js");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(factionFile);
                            Writer writer = new OutputStreamWriter(fos, "UTF-8");
                            writer.append(faction.getId() + "_entries = ");
                            JsonConverter.createFactionExport(writer, faction);
                            writer.flush();
                            writer.append(";\n\n");
                            writer.flush();
                            fos.close();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        File factionTierFile = new File(whacExternalDirPath, "tier_" + faction.getId() + ".js");
                        FileOutputStream fosTier = null;

                        try {
                            fosTier = new FileOutputStream(factionFile, true);
                            Writer writer = new OutputStreamWriter(fosTier, "UTF-8");
                            writer.append(faction.getId() + "_tiers = ");
                            JsonConverter.createTierExport(writer, ArmySingleton.getInstance().getTiers(faction.getEnumValue()), ArmySingleton.getInstance().getContracts(faction.getEnumValue()));
                            writer.flush();
                            writer.append(";\n\n");
                            writer.flush();
                            fosTier.close();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }


	            if (publishData) {
	            	
	    			File externalStorageDir = Environment.getExternalStorageDirectory ();
	    			String whacExternalDirPath = externalStorageDir.getPath() + StorageManager.WHAC_SUBDIR;

	            	
	            	File extractFile = new File(whacExternalDirPath, "export_army");
	            	File spellsFile = new File(whacExternalDirPath, "spells");
	            	File capacitiesFile = new File(whacExternalDirPath, "capacities");
	            	try {
						extractFile.createNewFile();
						spellsFile.createNewFile();
						PrintStream ps = new PrintStream(extractFile,"UTF-8");
						
						PrintStream psSpells = new PrintStream(spellsFile, "UTF-8");
						
						PrintStream psCapa = new PrintStream(capacitiesFile, "UTF-8");
						
		            	for (String id : ArmySingleton.getInstance().getArmyElements().keySet()) {
		            		
		            		ArmyElement element = ArmySingleton.getInstance().getArmyElements().get(id);
		            		ps.print(element.getId());
		            		ps.print(";");
		            		ps.print(element.getFullName().toUpperCase().trim());
		            		ps.print(";");
		            		ps.print(element.getQualification());
		            		ps.print(" [FA=");
		            		ps.print(element.getFaString());
		            		ps.print(" Cost=");
		            		ps.print(element.getCostString());
		            		ps.print("] ");

		            		SingleModel model = element.getModels().get(0);
		            		ps.print(model.getHTMLResume());
		            		ps.println();
		            		
		            		for (SingleModel aModel : element.getModels()) {
			            		for (Capacity capacity : aModel.getCapacities()) {
			            			psCapa.print(element.getId());
			            			psCapa.print(";");
			            			String filteredTitle = capacity.getTitle().replace('\u25CF', ' ');
			            			psCapa.print(filteredTitle.toUpperCase().trim());
			            			if (capacity.getType() != null && capacity.getType().length() > 0) {
			            				psCapa.print(" (");
			            				psCapa.print(capacity.getType());
			            				psCapa.print(")");
			            			}
			            			psCapa.print(";");
			            			psCapa.print(capacity.toHTMLLabel());
			            			psCapa.println();
			            		}
		            		}
		            		
		            		if (element instanceof SpellCaster) {
		            			for (Spell spell : ((SpellCaster) element).getSpells()) {
		            				psSpells.print(element.getId());
		            				psSpells.print(";");
		            				psSpells.print(spell.getTitle().toUpperCase().trim());
		            				psSpells.print(";");
		            				psSpells.print(spell.getStringResume());
		            				psSpells.println();
		            			}
		            		}
		            	
		            	}
		            	
	            		ps.close();
	            		psSpells.close();
	            		psCapa.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	            }
	            
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
			
			// create armies dir
			String whacArmiesDirPath = whacExternalDir.getPath() + File.separator + StorageManager.ARMIES_SUBDIR;
			File whacArmiesDir = new File(whacArmiesDirPath);
			
			if (! whacArmiesDir.exists() ) {
				boolean succeed = whacArmiesDir.mkdirs();
				if (!succeed) {
					Log.w(TAG, "failed to create Whac/armies subdir in SD card");
				}
			}
		
		
			// if not done, charge all armies and write in new format
			SharedPreferences  conversionPrefs = application.getSharedPreferences(OLD_ARMIES_CONVERTED, Context.MODE_PRIVATE);
			if (! conversionPrefs.contains(CONVERSION_DONE)) {
				
				boolean success = false;
				
				success = StorageManager.convertArmyListsToNewFormat(application);
				
				if (success) {
					
					Editor editor = conversionPrefs.edit();
					editor.putBoolean(CONVERSION_DONE, true);
					editor.commit();
					
					publishProgress(7);
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
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (progressDialog == null ) {
			return;
		}
		switch (values[0]) {
		case 1 : 
			progressDialog.setMessage("loaded basic data...");
			break;
		case 2 : 
			progressDialog.setMessage("loaded imported data files...");
			break;
		case 3 : 
			progressDialog.setMessage("loaded tier data...");
			break;
		case 4 : 
			progressDialog.setMessage("loaded Steamroller...");
			break;
		case 6 : 
			progressDialog.setMessage("converting old army lists to new format...");
			break;
		case 7 : 
			progressDialog.setMessage("lists have been converted.");
			break;
		}
		progressDialog.setProgress(values[0]);
		
	}

	public StartActivity getParent() {
		return parent;
	}

	public void setParent(StartActivity parent) {
		this.parent = parent;
		if (parent == null) {
			progressDialog = null;
		}
	}


}
