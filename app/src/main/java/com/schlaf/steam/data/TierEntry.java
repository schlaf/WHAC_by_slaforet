package com.schlaf.steam.data;

public class TierEntry {

	private String id;

	public TierEntry(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String toString() {
		return id;
	}
}
