package com.schlaf.steam.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ArmyListOrDirectory {

	public enum TYPES {
		CREATE_DIR,
		DIRECTORY, 
		ARMY;
	}
	
	public TYPES getType();

	public View getView(View convertView, ViewGroup parent, LayoutInflater inflater);	
}
