package com.schlaf.steam.data;

public class ModelTypeEnumTranslated {

	private ModelTypeEnum type;
	private String translation;
	
	public ModelTypeEnumTranslated(ModelTypeEnum type, String translation) {
		this.type = type;
		this.translation = translation;
	}
	
	public String toString() {
		return translation;
	}

	public ModelTypeEnum getType() {
		return type;
	}
}
