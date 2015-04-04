package com.schlaf.steam.activities.importation;

import android.view.View;

import java.io.File;

public interface ImportFileListener {
	
	
	public void onImportFileSelected(File file);

	public void onImportFileDeleted(File file);
	
	public void onImportedFileDeleted(File file);

    public void checkVersions(View v);
}
