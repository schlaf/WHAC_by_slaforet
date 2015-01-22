package com.schlaf.steam.data;

public interface ModelRestrictor {

	public boolean isAllowedModel(String id) ;
	
	public boolean isAllowedModel(int level, String id) ;
	
}
