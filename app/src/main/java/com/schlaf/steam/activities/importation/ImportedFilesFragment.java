package com.schlaf.steam.activities.importation;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.schlaf.steam.adapters.ImportedFileRowAdapter;
import com.schlaf.steam.storage.StorageManager;

public class ImportedFilesFragment extends ListFragment {

	public static final String ID = "ImportedFilesFragment";
	private static final String TAG = "ImportedFilesFragment";
	
	private ArrayList<File> files;
	ArrayAdapter<File> adapter;
	
	ImportFileListener listener; // parent activity
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		files = StorageManager.getImportedDataFiles(getActivity());
	    adapter = new ImportedFileRowAdapter(getActivity(), files);
	    setListAdapter(adapter);
	    
	    

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// listener.onArmyListSelected(lists.get(position));
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.d(TAG, "onAttach");
		super.onAttach(activity);
		if (activity instanceof ImportFileListener) {
			listener = (ImportFileListener) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implement ImportFileListener");
		}
		
	}
	
	public void notifyFileDeletion(File file) {
		adapter.remove(file);
		adapter.notifyDataSetChanged();
	}
	
	public void notifyFileImported(File file) {
		if (adapter.getPosition(file) == -1) {
			adapter.add(file);

			adapter.sort(new Comparator<File>() {

				@Override
				public int compare(File lhs, File rhs) {
					return lhs.compareTo(rhs);
				}
				
			});

		}
		
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (files == null || files.isEmpty()) {
			setEmptyText("No files have been imported.");	
		}
	}


}
