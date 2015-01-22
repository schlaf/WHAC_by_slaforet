/**
 * 
 */
package com.schlaf.steam.activities.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseArmyListDialog;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.adapters.CardLibrayRowAdapter;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;
import com.schlaf.steam.data.ModelTypeEnumTranslated;
import com.schlaf.steam.storage.StorageManager;

/**
 * @author S0085289
 * 
 */
public class ChooseCardFromLibraryDialog extends DialogFragment implements OnItemSelectedListener, OnClickListener, OnItemClickListener, OnItemLongClickListener {

	public static final String ID = "ChooseCardFromLibraryDialog";
	
	Spinner factionSpinner;
	Spinner entryTypeSpinner;
	ListView entriesListView;
	
	private ViewCardActivityInterface mListener;
	
	boolean openForAddingCard = false;
	
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ViewCardActivityInterface) activity;
            
            if (mListener.canAddCardToBattle()) {
            	openForAddingCard = true;
            }
            
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ViewCardActivityInterface");
        }
        
        

		// reload from preferences
		SharedPreferences save = activity.getSharedPreferences(CardLibraryActivity.LIBRARY_PREF, Context.MODE_PRIVATE);
		String faction = save.getString(CardLibraryActivity.LIBRARY_PREF_FACTION_KEY, FactionNamesEnum.CRYX.getId());
		HashMap<String, Faction> factions = ArmySingleton.getInstance().getFactions();
		CardLibrarySingleton.getInstance().setFaction(factions.get(faction));


		String entryType = save.getString(CardLibraryActivity.LIBRARY_PREF_ENTRY_TYPE_KEY, ModelTypeEnum.WARCASTER.name());
		CardLibrarySingleton.getInstance().setEntryType(ModelTypeEnum.valueOf(entryType));

    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (parent.getId() == factionSpinner.getId()) {
			if (factionSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
				// change faction, refilter entries types
				CardLibrarySingleton.getInstance().setFaction((Faction)factionSpinner.getSelectedItem());
				List<ModelTypeEnum> types = CardLibrarySingleton.getInstance().getNonEmptyEntryType();
				
				List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
				for (ModelTypeEnum type : types) {
					ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
					typesTranslated.add(translated);
				}
				
				ArrayAdapter<ModelTypeEnumTranslated> adapterEntryType = new ArrayAdapter<ModelTypeEnumTranslated>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, typesTranslated);
				adapterEntryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				entryTypeSpinner.setAdapter(adapterEntryType);
				
				// reselect same type if possible
				if (CardLibrarySingleton.getInstance().getEntryType() == null) {
					CardLibrarySingleton.getInstance().setEntryType(types.get(0));	
				} else {
					int selected = types.indexOf(CardLibrarySingleton.getInstance().getEntryType());
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
				
				CardLibrarySingleton.getInstance().setFaction((Faction)factionSpinner.getSelectedItem());
				CardLibrarySingleton.getInstance().setEntryType( ((ModelTypeEnumTranslated)entryTypeSpinner.getSelectedItem()).getType());
				
				CardLibrayRowAdapter adapterEntry = 
						new CardLibrayRowAdapter(getActivity(), CardLibrarySingleton.getInstance().getEntries());
				entriesListView.setAdapter(adapterEntry);
			}
		} 
		
	}



	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = createView(inflater);
		
		if (getShowsDialog()) {
			getDialog().setTitle(R.string.choose_card);
		}
		
		return view;
	}



	private View createView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.choose_card_options, null);
		
				
		factionSpinner = (Spinner) view.findViewById(R.id.icsSpinnerFaction);
		
		HashMap<String, Faction> factions = ArmySingleton.getInstance().getFactions();
		List<Faction> factionsList= new ArrayList<Faction>();
		factionsList.addAll(factions.values());
		Collections.sort(factionsList);
		
		ArrayAdapter<Faction> adapter = new ArrayAdapter<Faction>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, factionsList);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		factionSpinner.setAdapter(adapter);
		

		if (CardLibrarySingleton.getInstance().getFaction() == null) {
			CardLibrarySingleton.getInstance().setFaction(factionsList.get(0));	
		} else {
			int selected = factionsList.indexOf(CardLibrarySingleton.getInstance().getFaction());
			factionSpinner.setSelection(selected, false);
		}

		
		entryTypeSpinner = (Spinner) view.findViewById(R.id.icsSpinnerEntryType);
		
		List<ModelTypeEnum> types = CardLibrarySingleton.getInstance().getNonEmptyEntryType();
		List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
		for (ModelTypeEnum type : types) {
			ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
			typesTranslated.add(translated);
		}
		
		ArrayAdapter<ModelTypeEnumTranslated> adapterEntryType = new ArrayAdapter<ModelTypeEnumTranslated>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, typesTranslated);
		adapterEntryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		entryTypeSpinner.setAdapter(adapterEntryType);
		
		entriesListView = (ListView) view.findViewById(R.id.listView1);
				
		factionSpinner.setOnItemSelectedListener(this);
		entryTypeSpinner.setOnItemSelectedListener(this);
		entriesListView.setOnItemClickListener(this);
		
		
		if (openForAddingCard) {
			view.findViewById(R.id.textViewLabelToAdd).setVisibility(View.VISIBLE);
			entriesListView.setOnItemLongClickListener(this);
		} else {
			view.findViewById(R.id.textViewLabelToAdd).setVisibility(View.GONE);
			entriesListView.setOnItemLongClickListener(null);
		}
		
		
		
		
		// reselect same type if possible
		if (CardLibrarySingleton.getInstance().getEntryType() == null) {
			CardLibrarySingleton.getInstance().setEntryType(types.get(0));	
		} else {
			int selected = types.indexOf(CardLibrarySingleton.getInstance().getEntryType());
			if (selected == -1) {
				// if type has disappeared, select first by default...
				selected = 0;
			}
			entryTypeSpinner.setSelection(selected, false);
		}

		
		return view;
	}



	@Override
	public void onClick(View v) {
		

	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == entriesListView.getId()) {
			if (entriesListView.getItemAtPosition(position) != null) {
				String entryId = ((CardLibraryRowData) entriesListView.getItemAtPosition(position)).getId();
				SelectionModelSingleton.getInstance().setCurrentlyViewedElement(ArmySingleton.getInstance().getArmyElement(entryId));
				mListener.viewModelDetail(null);
				
				if (getShowsDialog()) {
					
					// save preferences
					SharedPreferences save = ((Activity)mListener).getSharedPreferences(CardLibraryActivity.LIBRARY_PREF, Activity.MODE_PRIVATE);
					Editor ed = save.edit();
					ed.putString(CardLibraryActivity.LIBRARY_PREF_FACTION_KEY, CardLibrarySingleton.getInstance().getFaction().getId());
					ed.putString(CardLibraryActivity.LIBRARY_PREF_ENTRY_TYPE_KEY, CardLibrarySingleton.getInstance().getEntryType().name());
				    ed.commit();

					
					dismiss();
				}
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (parent.getId() == entriesListView.getId()) {
			if (entriesListView.getItemAtPosition(position) != null) {
				String entryId = ((CardLibraryRowData) entriesListView.getItemAtPosition(position)).getId();
				
				final ArmyElement entry = ArmySingleton.getInstance().getArmyElement(entryId);
				
				
				
				
				// 1. Instantiate an AlertDialog.Builder with its constructor
		    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		    	// 2. Chain together various setter methods to set the dialog characteristics
		    	builder.setMessage(getResources().getString(R.string.add_entry_message) + entry.getFullName());
		    	builder.setTitle(R.string.add_entry_title);
		    	
		    	builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                // User clicked OK button
						SelectionModelSingleton.getInstance().setCurrentlyViewedElement(entry);
						mListener.addModelToBattle(null);

		            }
		        });
		    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                // User cancelled the dialog
		            }
		        });
		    	
		    	// 3. Get the AlertDialog from create()
		    	AlertDialog dialog = builder.create();
		    	
		    	dialog.show();				
				
				
				
				
				
				
				
				if (getShowsDialog()) {
					
					// save preferences
					SharedPreferences save = ((Activity)mListener).getSharedPreferences(CardLibraryActivity.LIBRARY_PREF, Activity.MODE_PRIVATE);
					Editor ed = save.edit();
					ed.putString(CardLibraryActivity.LIBRARY_PREF_FACTION_KEY, CardLibrarySingleton.getInstance().getFaction().getId());
					ed.putString(CardLibraryActivity.LIBRARY_PREF_ENTRY_TYPE_KEY, CardLibrarySingleton.getInstance().getEntryType().name());
				    ed.commit();

					
					dismiss();
				}
			}
		}
		return true;
	}


}
