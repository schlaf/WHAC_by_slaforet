package com.schlaf.steam.activities.importation;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.schlaf.steam.R;
import com.schlaf.steam.adapters.ImportFileRowAdapter;
import com.schlaf.steam.storage.StorageManager;

public class FilesToImportFragment extends Fragment {

	public static final String ID = "filesToImport";
	
	ImportFileRowAdapter adapter;
	
	ImportFileListener listener; // parent activity

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<File> filesWhac = StorageManager.getDataFilesToImport(getActivity(), StorageManager.WHAC_SUBDIR);
		List<File> filesTierWhac = StorageManager.getTiersFilesToImport(getActivity(), StorageManager.WHAC_SUBDIR);
		filesWhac.addAll(filesTierWhac);
		
		List<File> filesDownload = StorageManager.getDataFilesToImport(getActivity(), StorageManager.DOWNLOAD_SUBDIR);
		List<File> filesDownloadTier = StorageManager.getTiersFilesToImport(getActivity(), StorageManager.DOWNLOAD_SUBDIR);
		filesDownload.addAll(filesDownloadTier);
	    adapter = new ImportFileRowAdapter(getActivity(), filesWhac, filesDownload);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		Log.d("FilesToImportFragment", "FilesToImportFragment.onCreateView");
		
		View view = inflater.inflate(R.layout.files_to_import_fragment,
				container, false);

		return view;		
	}

	
	@Override
	public void onAttach(Activity activity) {
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ExpandableListView listView = (ExpandableListView) getView().findViewById(R.id.expandableListView1);
		listView.setAdapter(adapter);
		listView.expandGroup(0);
		listView.expandGroup(1);
	}


}
