/**
 * 
 */
package com.schlaf.steam.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.adapters.ArmyListRowAdapter;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.ArmyListOrDirectory;
import com.schlaf.steam.storage.DirectoryCreationRow;
import com.schlaf.steam.storage.StorageManager;

/**
 * 
 * choose an existing army file on local filesystem
 * 
 * @author S0085289
 *
 */
public class ChooseFileToSaveDialog extends DialogFragment implements OnItemClickListener, OnClickListener {

	public interface ChooseFileToSaveListener extends ChooseArmyListener {
		
		public void onDirectoryCreated(String fullPath);
		
		public void onArmySaved();
		
	}
	
	private String fullCurrentPath; // currently selected directory
	
	private ChooseFileToSaveListener mListener;
	private ListView listView;
	ArmyListRowAdapter adapter;
	private TextView currentDirTV;
	EditText armyName;
	
	private String proposedFileName;
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChooseFileToSaveListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ChooseArmyListener");
        }
    }
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_save_destination);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    
		// Create the AlertDialog object and return it
	    View view = inflater.inflate(R.layout.choose_save_army_file, null);
	    
	    listView = (ListView) view.findViewById(R.id.listView1);		
		adapter = new ArmyListRowAdapter(getActivity(), getData());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		currentDirTV = (TextView) view.findViewById(R.id.currentDirTV);
		currentDirTV.setText("/");

		armyName = (EditText) view.findViewById(R.id.army_name);
		armyName.setText(proposedFileName);
		
		Button saveButton = (Button) view.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);
		
		fullCurrentPath = StorageManager.getArmyListsPath(getActivity());
		
		
	    builder.setView(view);
		return builder.create();
	}
	
	public void notifyArmyListDeletion(ArmyListDescriptor listDescriptor) {
		adapter.remove(listDescriptor);
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
		
		if (entry.getType() == ArmyListOrDirectory.TYPES.CREATE_DIR) {
			// do nothing;
		} else if (entry.getType() == ArmyListOrDirectory.TYPES.DIRECTORY){
			adapter = new ArmyListRowAdapter(getActivity(), getData(((ArmyListDirectory) entry).getFullpath()));
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
		result.add(new DirectoryCreationRow(getActivity(), fullPath));
		result.addAll(StorageManager.getArmyListDirectories(getActivity(), fullPath));
		result.addAll(StorageManager.getArmyLists(getActivity(), fullPath));
		
		return  result;
	}
	
	public List<ArmyListOrDirectory> getData() {
		List<ArmyListOrDirectory> result = new ArrayList<ArmyListOrDirectory>();
		
		String armiesPath = StorageManager.getArmyListsPath(getActivity());
		
		result.add(new DirectoryCreationRow(getActivity(), armiesPath));
		result.addAll(StorageManager.getArmyListDirectories(getActivity(), null));
		result.addAll(StorageManager.getArmyLists(getActivity()));
		
		
		return  result;
	}

	public String getProposedFileName() {
		return proposedFileName;
	}

	public void setProposedFileName(String proposedFileName) {
		this.proposedFileName = proposedFileName;
	}

	public void notifyDirectoryCreation(String fullPath) {
		Toast.makeText(getActivity(), "Folder created", Toast.LENGTH_SHORT).show();
		File file = new File(fullPath);
		File parentDir = new File(file.getParent());
		adapter = new ArmyListRowAdapter(getActivity(), getData(parentDir.getPath()));
		listView.setAdapter(adapter);
	}

	public void notifyFolderDeletion(String fullPath) {
		Toast.makeText(getActivity(), "Folder deleted", Toast.LENGTH_SHORT).show();
		File file = new File(fullPath);
		File parentDir = new File(file.getParent());
		adapter = new ArmyListRowAdapter(getActivity(), getData(parentDir.getPath()));
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		
		if (armyName.getText() != null && armyName.getText().length() > 0) {
			String value = armyName.getText().toString();
			
			String validFileName = StorageManager.fixFileNameForSave(value);
			SelectionModelSingleton.getInstance().setArmyFileName(validFileName);
			SelectionModelSingleton.getInstance().setArmyFilePath(
					fullCurrentPath + File.separator + validFileName);

			StorageManager.saveArmyList(
					getActivity(),
					SelectionModelSingleton.getInstance());
			Toast.makeText(getActivity(), "Army saved",
					Toast.LENGTH_SHORT).show();
			
			mListener.onArmySaved();
			dismiss();
		}
			
	}

	public void notifyDirectoryDeletion(ArmyListDirectory directory) {
		adapter.remove(directory);
		adapter.notifyDataSetChanged();
	}
	
}
