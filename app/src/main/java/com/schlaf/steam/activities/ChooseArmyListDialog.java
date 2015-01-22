/**
 * 
 */
package com.schlaf.steam.activities;

import java.util.ArrayList;
import java.util.List;

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
import com.schlaf.steam.adapters.ArmyListRowAdapter;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.ArmyListOrDirectory;
import com.schlaf.steam.storage.StorageManager;

/**
 * 
 * choose an existing army file on local filesystem
 * 
 * @author S0085289
 *
 */
public class ChooseArmyListDialog extends DialogFragment implements OnItemClickListener {

	private ChooseArmyListener mListener;
	private ListView listView;
	ArmyListRowAdapter adapter;
	private TextView currentDirTV;
	
	private String fullCurrentPath; // currently selected directory
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChooseArmyListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ChooseArmyListener");
        }
    }
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_army_list);
		
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    
		// Create the AlertDialog object and return it
	    View view = inflater.inflate(R.layout.choose_army_list, null);
	    
	    listView = (ListView) view.findViewById(android.R.id.list);		
		adapter = new ArmyListRowAdapter(getActivity(), getData());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		
		listView.setEmptyView(view.findViewById(android.R.id.empty));
		
		currentDirTV = (TextView) view.findViewById(R.id.currentDirTV);
		currentDirTV.setText("/");

	    builder.setView(view);
		return builder.create();
	}
	
	public void notifyArmyListDeletion(ArmyListDescriptor listDescriptor) {
		adapter.remove(listDescriptor);
		adapter.notifyDataSetChanged();
	}
	
	public void notifyDirectoryDeletion(ArmyListDirectory directory) {
		adapter.remove(directory);
		adapter.notifyDataSetChanged();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// l'affichage de chaque ligne est dédiée par un adapteur spécifique
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArmyListOrDirectory entry = (ArmyListOrDirectory) listView.getItemAtPosition(position);
		
		if (entry.getType() == ArmyListOrDirectory.TYPES.ARMY) {
			mListener.onArmyListSelected( (ArmyListDescriptor) entry);
			dismiss();
		} else if (entry.getType() == ArmyListOrDirectory.TYPES.DIRECTORY){
			
			adapter = new ArmyListRowAdapter(getActivity(), getData( ((ArmyListDirectory) entry).getFullpath()));
			listView.setAdapter(adapter);
			
			String rootPath = StorageManager.getArmyListsPath(getActivity());
			
			fullCurrentPath = ((ArmyListDirectory) entry).getFullpath();
			
			String visualPath = fullCurrentPath.replace(rootPath, "");
			
			if (visualPath.length() == 0) {
				visualPath = "/";
			}
					
			currentDirTV.setText(visualPath);

			
		}
		
	}
	
	public List<ArmyListOrDirectory> getData(String fullPath) {
		List<ArmyListOrDirectory> result = new ArrayList<ArmyListOrDirectory>();
		
		result.addAll(StorageManager.getArmyListDirectories(getActivity(), fullPath));
		result.addAll(StorageManager.getArmyLists(getActivity(), fullPath));
		
		return  result;
	}
	
	public List<ArmyListOrDirectory> getData() {
		List<ArmyListOrDirectory> result = new ArrayList<ArmyListOrDirectory>();
		
		result.addAll(StorageManager.getArmyListDirectories(getActivity(), null));
		result.addAll(StorageManager.getArmyLists(getActivity()));
		
		
		return  result;
	}

}
