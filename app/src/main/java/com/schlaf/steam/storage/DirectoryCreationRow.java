package com.schlaf.steam.storage;

import java.io.File;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseFileToSaveDialog.ChooseFileToSaveListener;

public class DirectoryCreationRow implements ArmyListOrDirectory,
		Comparable<DirectoryCreationRow> {

	private String parentPath; // the directory in which create subdir
	Activity parentActivity;

	public DirectoryCreationRow(Activity parentActivity, String parentPath) {
		this.parentActivity= parentActivity; 
		this.parentPath= parentPath;
	}

	@Override
	public TYPES getType() {
		return TYPES.CREATE_DIR;
	}

	@Override
	public View getView(View convertView, ViewGroup parent,
			LayoutInflater inflater) {
		if (convertView == null) {
			// instantiate new view
			convertView = inflater.inflate(R.layout.create_folder_row, parent,
					false);
		}

		final EditText folderNameET = (EditText) convertView
				.findViewById(R.id.directoryNameEditText);
		folderNameET.setText("");

		ImageButton createButton = (ImageButton) convertView
				.findViewById(R.id.buttonCreateFolder);
		convertView.setTag(this);
		createButton.setTag(this);
		createButton.setFocusable(false);

		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//StorageManager.createArmyListDirectory(, subPath)
				String newFolderName = folderNameET.getText().toString();
				String newFileName = parentPath + File.separator + newFolderName;
				((ChooseFileToSaveListener) parentActivity).onDirectoryCreated(newFileName);
			}
		});

		return convertView;
	}

	@Override
	public int compareTo(DirectoryCreationRow another) {
		return parentPath.compareTo(another.getParentPath());
	}

	public String getParentPath() {
		return parentPath;
	}

}
