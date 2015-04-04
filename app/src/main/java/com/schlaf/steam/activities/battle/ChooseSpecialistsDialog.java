/**
 * 
 */
package com.schlaf.steam.activities.battle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseArmyListener;
import com.schlaf.steam.adapters.ArmyListRowAdapter;
import com.schlaf.steam.adapters.SpecialistRowAdapter;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.ArmyListOrDirectory;
import com.schlaf.steam.storage.StorageManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * choose an existing army file on local filesystem
 * 
 * @author S0085289
 *
 */
public class ChooseSpecialistsDialog extends DialogFragment{

	private BattleActivity mListener;
    TextView tvPoints;
	private ListView listView;
	SpecialistRowAdapter adapter;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BattleActivity) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement BattleActivity");
        }
    }
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_specialists);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        // Create the AlertDialog object and return it
        View view = inflater.inflate(R.layout.choose_specialists, null);

        tvPoints = (TextView)view.findViewById(R.id.tvPoints);
        listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new SpecialistRowAdapter(getActivity(), this, BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1));
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(android.R.id.empty));


        notifySpecialistChange(); // to calculate cost and display!

        builder.setView(view);


        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                        List<SpecialistRowAdapter.SpecialistEntry> specialistsSelected = adapter.getSpecialists();

                        // copy specialist status from adapter to singleton

                        List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
                        Iterator<BattleEntry> iter = entries.iterator();

                        for (SpecialistRowAdapter.SpecialistEntry entry : specialistsSelected) {
                            BattleEntry battleEntry = iter.next();
                            battleEntry.setSpecialist(entry.specialist);
                        }

                        mListener.notifySpecialistsChange();
                    }
                });

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		return builder.create();
	}
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// l'affichage de chaque ligne est dédiée par un adapteur spécifique
	}


    public void notifySpecialistChange() {

        List<SpecialistRowAdapter.SpecialistEntry> specialistsSelected = adapter.getSpecialists();
        int specialistPoints = 0;
        for (SpecialistRowAdapter.SpecialistEntry entry : specialistsSelected) {
            if (entry.specialist) {
                specialistPoints += entry.cost;
            }
        }
        tvPoints.setText(String.valueOf(specialistPoints));
    }
}
