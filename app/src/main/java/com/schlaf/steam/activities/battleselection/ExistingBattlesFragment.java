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
import com.schlaf.steam.activities.ChooseBattleListener;
import com.schlaf.steam.adapters.BattleSelectionRowAdapter;
import com.schlaf.steam.storage.BattleListDescriptor;
import com.schlaf.steam.storage.BattleListDirectory;
import com.schlaf.steam.storage.BattleListOrDirectory;
import com.schlaf.steam.storage.StorageManager;

public class ExistingBattlesFragment extends ListFragment {

	List<BattleListOrDirectory> list;
	BattleSelectionRowAdapter adapter;
	
	ChooseBattleListener listener; // parent activity
	
	private TextView currentDirTV;
	private String fullCurrentPath; // currently selected directory
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    adapter = new BattleSelectionRowAdapter(getActivity(), getData());

	    setListAdapter(adapter);
	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create the AlertDialog object and return it
	    View view = inflater.inflate(R.layout.choose_battle_list, null);
	    
	    // listView = (ListView) view.findViewById(android.R.id.list);		
		adapter = new BattleSelectionRowAdapter(getActivity(), getData());
		setListAdapter(adapter);
		
		currentDirTV = (TextView) view.findViewById(R.id.currentDirTV);
		currentDirTV.setText("/");
		
		fullCurrentPath = StorageManager.getBattlesPath(getActivity());	
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.d("ExistingArmiesFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof ChooseArmyListener) {
			listener = (ChooseBattleListener) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implement ChooseBattleListener");
		}
	}
	
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		
		// Toast.makeText(getActivity(), "Battle selected " , Toast.LENGTH_SHORT).show();
		
		BattleListOrDirectory entry = (BattleListOrDirectory) listView.getItemAtPosition(position);
		
		if (entry.getType() == BattleListOrDirectory.TYPES.BATTLE) {
			listener.onBattleSelected( (BattleListDescriptor) entry);
		} else if (entry.getType() == BattleListOrDirectory.TYPES.DIRECTORY){
			adapter = new BattleSelectionRowAdapter(getActivity(), getData( ((BattleListDirectory) entry).getFullpath()));
			listView.setAdapter(adapter);
			
			
			String rootPath = StorageManager.getBattlesPath(getActivity());
			
			fullCurrentPath = ((BattleListDirectory) entry).getFullpath();
			
			String visualPath = fullCurrentPath.replace(rootPath, "");
			
			if (visualPath.length() == 0) {
				visualPath = "/";
			}
					
			currentDirTV.setText(visualPath);

		}
		
//		// open battle activity
//		Intent intent = new Intent(getActivity(), BattleActivity.class);
//		intent.putExtra(BattleActivity.INTENT_ARMY, list.get(position).getFilename());
//		intent.putExtra(BattleActivity.INTENT_CREATE_BATTLE_FROM_ARMY, false);
//		
//		startActivity(intent);
		
	}
	
	private List<BattleListOrDirectory> getData() {
		
		List<BattleListOrDirectory> result = new ArrayList<BattleListOrDirectory>();
		result.addAll(StorageManager.getBattleListDirectories(getActivity()));
		result.addAll(StorageManager.getBattleLists(getActivity()));

		return result;
	}
	
	public List<BattleListOrDirectory> getData(String fullPath) {
		List<BattleListOrDirectory> result = new ArrayList<BattleListOrDirectory>();
		
		result.addAll(StorageManager.getBattleListDirectories(getActivity(), fullPath));
		result.addAll(StorageManager.getBattleLists(getActivity(), fullPath));
		
		return  result;
	}

	public void refresh() {
		list= getData();
		adapter.notifyDataSetInvalidated();
	    adapter = new BattleSelectionRowAdapter(getActivity(), list);
	    setListAdapter(adapter);
	}
	
	public void notifyBattleListDeletion(BattleListDescriptor listDescriptor) {
		adapter.remove(listDescriptor);
		adapter.notifyDataSetChanged();
	}
	
	public void notifyFolderDeletion(BattleListDirectory folder) {
		adapter.remove(folder);
		adapter.notifyDataSetChanged();
	}
	
}
