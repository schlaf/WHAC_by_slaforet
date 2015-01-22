package com.schlaf.steam.data;

import java.util.ArrayList;

public interface Restrictable {

	/** casters or warlock or ... to which this entry can be attached */
	public ArrayList<String> getAllowedEntriesToAttach();
	
	/** list of tier in which this entry can be present (else, masked) */
	public ArrayList<String> getTiersInWhichAllowedToAppear();
	
}
