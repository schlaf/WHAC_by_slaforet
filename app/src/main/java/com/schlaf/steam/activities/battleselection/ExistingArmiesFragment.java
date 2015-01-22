package com.schlaf.steam.activities.battleselection;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseArmyListener;
import com.schlaf.steam.adapters.ArmyListRowAdapter;
import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;
import com.schlaf.steam.storage.ArmyListOrDirectory;
import com.schlaf.steam.storage.StorageManager;

public class ExistingArmiesFragment extends ListFragment {

	ArmyListRowAdapter adapter;
	
	ChooseArmyListener listener; // parent activity
	private TextView currentDirTV;
	
	private String fullCurrentPath; // currently selected directory
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    adapter = new ArmyListRowAdapter(getActivity(), getData());
	    setListAdapter(adapter);
	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create the AlertDialog object and return it
	    View view = inflater.inflate(R.layout.choose_army_list, null);
	    
	    // listView = (ListView) view.findViewById(android.R.id.list);		
		adapter = new ArmyListRowAdapter(getActivity(), getData());
		setListAdapter(adapter);
		
		currentDirTV = (TextView) view.findViewById(R.id.currentDirTV);
		currentDirTV.setText("/");
		
		fullCurrentPath = StorageManager.getArmyListsPath(getActivity());	
		
		return view;
	}
	
	
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		ArmyListOrDirectory entry = (ArmyListOrDirectory) listView.getItemAtPosition(position);
		
		if (entry.getType() == ArmyListOrDirectory.TYPES.ARMY) {
			listener.onArmyListSelected( (ArmyListDescriptor) entry);
		} else if (entry.getType() == ArmyListOrDirectory.TYPES.DIRECTORY){
			adapter = new ArmyListRowAdapter(getActivity(), getData( ((ArmyListDirectory) entry).getFullpath()));
			setListAdapter(adapter);
			
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

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.d("ExistingArmiesFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof ChooseArmyListener) {
			listener = (ChooseArmyListener) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implemenet ChooseArmyListener");
		}
	}
	
	public void notifyArmyListDeletion(ArmyListDescriptor listDescriptor) {
		adapter.remove(listDescriptor);
		adapter.notifyDataSetChanged();
	}

	public void notifyFolderDeleted(ArmyListDirectory directory) {
		adapter.remove(directory);
		adapter.notifyDataSetChanged();
	}

}
