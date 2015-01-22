package com.schlaf.steam.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface BattleListOrDirectory {

	public enum TYPES {
		DIRECTORY, 
		BATTLE;
	}
	
	public TYPES getType();

	public View getView(View convertView, ViewGroup parent, LayoutInflater inflater);	
}
