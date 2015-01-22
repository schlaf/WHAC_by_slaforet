package com.schlaf.steam.activities.collection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.adapters.CollectionEntryRowAdapter;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.ModelTypeEnumTranslated;
import com.schlaf.steam.storage.StorageManager;

public class MyCollectionActivity extends ActionBarActivity implements OnItemSelectedListener {
	public static final String SHARED_PREF_COLLECTION = "collection";
	Spinner factionSpinner;
	Spinner entryTypeSpinner;
	ListView entriesListView;
	
	private static final boolean D = false; // debug
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (parent.getId() == factionSpinner.getId()) {
			if (factionSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
				// change faction, refilter entries types
				CollectionSingleton.getInstance().setFaction((Faction)factionSpinner.getSelectedItem());
				
				List<ModelTypeEnum> types = CollectionSingleton.getInstance().getNonEmptyEntryType();
				List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
				for (ModelTypeEnum type : types) {
					ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
					typesTranslated.add(translated);
				}
				
				ArrayAdapter<ModelTypeEnumTranslated> adapterEntryType = new ArrayAdapter<ModelTypeEnumTranslated>(this, android.R.layout.simple_spinner_item, android.R.id.text1, typesTranslated);
				adapterEntryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				entryTypeSpinner.setAdapter(adapterEntryType);

				// reselect same type if possible
				if (CollectionSingleton.getInstance().getEntryType() == null) {
					CollectionSingleton.getInstance().setEntryType(types.get(0));	
				} else {
					int selected = types.indexOf(CollectionSingleton.getInstance().getEntryType());
					if (selected == -1) {
						// if type has disappeared, select first by default...
						selected = 0;
					}
					entryTypeSpinner.setSelection(selected, false);
				}
				
			}
		}
		
		if (parent.getId() == factionSpinner.getId() || parent.getId() == entryTypeSpinner.getId() ) {
			
			if (factionSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION &&
					entryTypeSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
				
				CollectionSingleton.getInstance().setFaction((Faction)factionSpinner.getSelectedItem());
				// CollectionSingleton.getInstance().setEntryType((ModelTypeEnum)entryTypeSpinner.getSelectedItem());
				CollectionSingleton.getInstance().setEntryType( ((ModelTypeEnumTranslated)entryTypeSpinner.getSelectedItem()).getType());
				
				CollectionEntryRowAdapter adapterEntry = 
						new CollectionEntryRowAdapter(this, CollectionSingleton.getInstance().getEntries());
				entriesListView.setAdapter(adapterEntry);
			}
		} 
		
	}



	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("MycollectionActivity", "restore collection from preferences");
		// restore collection from preferences
		SharedPreferences save = getSharedPreferences(SHARED_PREF_COLLECTION, MODE_PRIVATE);
		Map<String, ?> entries = save.getAll();

		extractCollectionFromPrefs(save, entries);
	    
	    SharedPreferences savePreferredFaction = getSharedPreferences("collection_faction", MODE_PRIVATE);
	    String preferredFactionId = savePreferredFaction.getString("preferred", FactionNamesEnum.CRYX.getId());

	    setContentView(R.layout.collection_manage);
		// View view = inflater.inflate(R.layout.choose_card_options, null);
		
		getSupportActionBar().setTitle(R.string.my_collection);
		getSupportActionBar().setLogo(R.drawable.collection);
				
		factionSpinner = (Spinner) findViewById(R.id.icsSpinnerFaction);
		
		HashMap<String, Faction> factions = ArmySingleton.getInstance().getFactions();
		List<Faction> factionsList= new ArrayList<Faction>();
		factionsList.addAll(factions.values());
		Collections.sort(factionsList);
		
		ArrayAdapter<Faction> adapter = new ArrayAdapter<Faction>(this, android.R.layout.simple_spinner_item, android.R.id.text1, factionsList);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		factionSpinner.setAdapter(adapter);
		

		if (CollectionSingleton.getInstance().getFaction() == null) {
			if ( ArmySingleton.getInstance().getFactions().get(preferredFactionId) != null) {
				CollectionSingleton.getInstance().setFaction(ArmySingleton.getInstance().getFactions().get(preferredFactionId));
			} else {
				CollectionSingleton.getInstance().setFaction(factionsList.get(0));	
			}
			int selected = factionsList.indexOf(CollectionSingleton.getInstance().getFaction());
			factionSpinner.setSelection(selected, false);
		} else {
			int selected = factionsList.indexOf(CollectionSingleton.getInstance().getFaction());
			factionSpinner.setSelection(selected, false);
		}

		
		entryTypeSpinner = (Spinner) findViewById(R.id.icsSpinnerEntryType);
		
		List<ModelTypeEnum> types = CollectionSingleton.getInstance().getNonEmptyEntryType();
		List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
		for (ModelTypeEnum type : types) {
			ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
			typesTranslated.add(translated);
		}
		
		ArrayAdapter<ModelTypeEnumTranslated> adapterEntryType = new ArrayAdapter<ModelTypeEnumTranslated>(this, android.R.layout.simple_spinner_item, android.R.id.text1, typesTranslated);
		adapterEntryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		entryTypeSpinner.setAdapter(adapterEntryType);

		entriesListView = (ListView) findViewById(R.id.listView1);
				
		View header = getLayoutInflater().inflate(R.layout.collection_header, null);
		entriesListView.addHeaderView(header);
		
		factionSpinner.setOnItemSelectedListener(this);
		entryTypeSpinner.setOnItemSelectedListener(this);
		
		// reselect same type if possible
		if (CollectionSingleton.getInstance().getEntryType() == null) {
			CollectionSingleton.getInstance().setEntryType(types.get(0));	
		} else {
			int selected = types.indexOf(CollectionSingleton.getInstance().getEntryType());
			if (selected == -1) {
				// if type has disappeared, select first by default...
				selected = 0;
			}
			entryTypeSpinner.setSelection(selected, false);
		}

	}



	public static void extractCollectionFromPrefs(SharedPreferences save,
			Map<String, ?> entries) {
		HashMap<String, Integer> owned = CollectionSingleton.getInstance().getOwnedMap();
		HashMap<String, Integer> painted = CollectionSingleton.getInstance().getPaintedMap();

	    for (String key : entries.keySet()) {
	    	int count = save.getInt(key, 0);
	    	if (D) Log.d("MycollectionActivity", "loading " + key);
	    	if (key.startsWith("owned_")) {
		    	String id = key.replace("owned_", "");
		    	owned.put(id, Integer.valueOf(count));
		    	if (D) Log.d("MycollectionActivity", "owned =  " + count);
	    	}
	    	if (key.startsWith("painted_")) {
		    	String id = key.replace("painted_", "");
		    	painted.put(id, Integer.valueOf(count));
		    	if (D) Log.d("MycollectionActivity", "painted =  " + count);
	    	}
	    }
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("MycollectionActivity", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
	
	   /**
  * {@inheritDoc}
  */
 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
     super.onCreateOptionsMenu(menu);
     getMenuInflater().inflate(R.menu.collection_menu, menu);
     
     return true;
 }
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		switch (item.getItemId()) {

	        case R.id.menu_export_stats:
	        	exportCollection();
	            return true;
	        case R.id.menu_export_by_mail:
	            exportByMail();
	            return true;
	        case R.id.menu_show_stats:
	        	showStats();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	

	private void showStats() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		StorageManager.generateCollectionText(ps, this);
		ps.close();
		try {
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String content = bos.toString();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Current status of my collection");
	    builder.setMessage(content);
	    builder.show();
		
	}



	private void exportCollection() {
		StorageManager.exportCollection(getApplicationContext());
		Toast.makeText(this, "collection status exported on SD card", Toast.LENGTH_SHORT).show();
	}
	
	private void exportByMail() {
		
		String strFile = StorageManager.exportCollection(getApplicationContext());
		
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "unknown_recipient@mail.com" });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WHAC collection export file");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + strFile));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "this is the export of your collection");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		
	}



	@Override
	protected void onPause() {
		// save state at every pause...
		Log.d("MycollectionActivity", "onPause ... saving collection to preferences");
		super.onPause();
		SharedPreferences save = getSharedPreferences(SHARED_PREF_COLLECTION, MODE_PRIVATE);
		Editor ed = save.edit();

		HashMap<String, Integer> owned = CollectionSingleton.getInstance().getOwnedMap();

	    for (String key : owned.keySet()) {
	    	int count = owned.get(key);
	    	ed.putInt("owned_" + key, count);
	    }

		HashMap<String, Integer> painted = CollectionSingleton.getInstance().getPaintedMap();

	    for (String key : painted.keySet()) {
	    	int count = painted.get(key);
	    	ed.putInt("painted_" + key, count);
	    }

	    
	    ed.commit();
	    
	    
	    SharedPreferences savePreferredFaction = getSharedPreferences("collection_faction", MODE_PRIVATE);
	    
	    Editor ed2 = savePreferredFaction.edit();
	    ed2.putString("preferred", CollectionSingleton.getInstance().getFaction().getEnumValue().getId());
	    ed2.commit();

	}
	
}
