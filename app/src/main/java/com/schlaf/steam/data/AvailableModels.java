package com.schlaf.steam.data;

public class AvailableModels {

	public enum TierModelType {
        WARCASTERS, WARLOCKS, WARJACKS,WARBEASTS,UNITS,SOLOS,BATTLE_ENGINES;
	}
	
	private TierModelType type;
	private String models;

	public AvailableModels(String type, String models) {
		this.type = TierModelType.valueOf(type);
		this.models = models;
	}

	public TierModelType getType() {
		return type;
	}

	public String getModels() {
		return models;
	}
	
	
}
