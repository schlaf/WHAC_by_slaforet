package com.schlaf.steam.data;


public enum WarmachineDamageSystemsEnum {
	EMPTY("x"), // this is a empty box --> not to allow damage
	HULL("."),
	LEFT("L"),
	RIGHT("R"),
	CORTEX("C"),
	MOVEMENT("M"),
	SUB_CORTEX("S"),
	HEAD("H"),
	INTERFACE("I"),
	BODY("B"),
	ARC_NODE("A"),
	GENERATOR("G"),
	FORCE_FIELD("F");
	
	private String code;
	private WarmachineDamageSystemsEnum(String code) {
		this.code = code;
	}
	
	public static WarmachineDamageSystemsEnum fromCode(String code) {
		for (WarmachineDamageSystemsEnum system : values()) {
			if (system.code.equals(code)) {
				return system;
			}
		}
		return EMPTY;
	}
	
	public String getCode() {
		return code;
	}
}
