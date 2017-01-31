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
import android.net.LinkAddress;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.schlaf.steam.activities.ChangeEntryTypeListener;
import com.schlaf.steam.activities.ChangeFactionListener;
import com.schlaf.steam.activities.ChooseArmyListDialog;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.adapters.CardLibrayRowAdapter;
import com.schlaf.steam.adapters.EntryTypeRowAdapter;
import com.schlaf.steam.adapters.FactionHorizontalRowAdapter;
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
public class ChooseCardFromLibraryDialog extends DialogFragment implements ChangeFactionListener, ChangeEntryTypeListener, OnItemClickListener, OnItemLongClickListener {

	public static final String ID = "ChooseCardFromLibraryDialog";
    private static final String TAG = "ChooseCardFromLibraryDialog";

    RecyclerView factionRecyclerView;
    RecyclerView entryTypeRecyclerView;

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

		if (factions.get(faction) == null) { // to handle last preference on SR2015 objectives, which do not exist anymore.
			faction = FactionNamesEnum.CYGNAR.getId();
		}

        if (factions.isEmpty() ) {
            // app closed, singleton empty, quit
            return;
        }
		CardLibrarySingleton.getInstance().setFaction(factions.get(faction));

		String entryType = save.getString(CardLibraryActivity.LIBRARY_PREF_ENTRY_TYPE_KEY, ModelTypeEnum.WARCASTER.name());
		CardLibrarySingleton.getInstance().setEntryType(ModelTypeEnum.valueOf(entryType));

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


        factionRecyclerView = (RecyclerView) view.findViewById(R.id.factionRV);
        entryTypeRecyclerView = (RecyclerView) view.findViewById(R.id.entryTypeRV);

        if (! ArmySingleton.getInstance().isFullyLoaded()) {
            return view;
        }


		HashMap<String, Faction> factions = ArmySingleton.getInstance().getFactions();
		List<Faction> factionsList= new ArrayList<Faction>();
		factionsList.addAll(factions.values());
		Collections.sort(factionsList);

        FactionHorizontalRowAdapter factionAdapter = new FactionHorizontalRowAdapter(getActivity(), this, factionsList);
        factionRecyclerView.setAdapter(factionAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        factionRecyclerView.setLayoutManager(manager);

		if (CardLibrarySingleton.getInstance().getFaction() == null) {
			CardLibrarySingleton.getInstance().setFaction(factionsList.get(0));	
		} else {
			int selected = factionsList.indexOf(CardLibrarySingleton.getInstance().getFaction());
            factionRecyclerView.scrollToPosition(selected);
		}




		List<ModelTypeEnum> types = CardLibrarySingleton.getInstance().getNonEmptyEntryType();
		List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
		for (ModelTypeEnum type : types) {
			ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
			typesTranslated.add(translated);
		}


        EntryTypeRowAdapter entriesAdapter= new EntryTypeRowAdapter(getActivity(), this, typesTranslated);
        entryTypeRecyclerView.setAdapter(entriesAdapter);
        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        entryTypeRecyclerView.setLayoutManager(manager2);

        if (CardLibrarySingleton.getInstance().getEntryType() == null) {
            CardLibrarySingleton.getInstance().setEntryType(types.get(0));
        } else {
            int selected = types.indexOf(CardLibrarySingleton.getInstance().getEntryType());
            entryTypeRecyclerView.scrollToPosition(selected);
        }


        entriesListView = (ListView) view.findViewById(R.id.listView1);

        CardLibrayRowAdapter adapterEntry =
                new CardLibrayRowAdapter(getActivity(), CardLibrarySingleton.getInstance().getEntries());
        entriesListView.setAdapter(adapterEntry);
		entriesListView.setOnItemClickListener(this);
		
		if (openForAddingCard) {
			view.findViewById(R.id.textViewLabelToAdd).setVisibility(View.VISIBLE);
			entriesListView.setOnItemLongClickListener(this);
		} else {
			view.findViewById(R.id.textViewLabelToAdd).setVisibility(View.GONE);
			entriesListView.setOnItemLongClickListener(null);
		}

		return view;
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


    @Override
    public void onChangeFaction(Faction newFaction) {
        Log.e(TAG, "onChangeFaction, new = " + newFaction.getFullName());
        CardLibrarySingleton.getInstance().setFaction(newFaction);
        List<ModelTypeEnum> types = CardLibrarySingleton.getInstance().getNonEmptyEntryType();


        List<ModelTypeEnumTranslated> typesTranslated = new ArrayList<ModelTypeEnumTranslated>();
        for (ModelTypeEnum type : types) {
            ModelTypeEnumTranslated translated = new ModelTypeEnumTranslated(type, getString(type.getTitle()));
            typesTranslated.add(translated);
        }

        factionRecyclerView.getAdapter().notifyDataSetChanged();

        EntryTypeRowAdapter entriesAdapter= new EntryTypeRowAdapter(getActivity(), this, typesTranslated);
        entryTypeRecyclerView.swapAdapter(entriesAdapter, true);



        // reselect same entry type if possible
        if (CardLibrarySingleton.getInstance().getEntryType() == null) {
            CardLibrarySingleton.getInstance().setEntryType(types.get(0));
        } else {
            int selected = types.indexOf(CardLibrarySingleton.getInstance().getEntryType());
            if (selected != -1) {
                onChangeModelType(typesTranslated.get(selected));
                entryTypeRecyclerView.scrollToPosition(selected);
            }
        }

    }

    @Override
    public void onChangeModelType(ModelTypeEnumTranslated newType) {
        Log.e(TAG, "onChangeModelType, new = " + newType.toString());
        CardLibrarySingleton.getInstance().setEntryType(newType.getType());

        entryTypeRecyclerView.getAdapter().notifyDataSetChanged();

        CardLibrayRowAdapter adapterEntry =
                new CardLibrayRowAdapter(getActivity(), CardLibrarySingleton.getInstance().getEntries());
        entriesListView.setAdapter(adapterEntry);

    }
}
