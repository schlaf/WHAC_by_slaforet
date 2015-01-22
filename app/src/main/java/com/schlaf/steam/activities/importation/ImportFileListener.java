package com.schlaf.steam.activities.importation;

import java.io.File;

public interface ImportFileListener {
	
	
	public void onImportFileSelected(File file);

	public void onImportFileDeleted(File file);
	
	public void onImportedFileDeleted(File file);
}
