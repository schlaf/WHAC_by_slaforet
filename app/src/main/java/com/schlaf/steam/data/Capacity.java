package com.schlaf.steam.data;

import java.io.Serializable;

public class Capacity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -865576751861601766L;
	
	private String title;
	private String type;
	private String label;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toHTMLLabel() {
		String result = label.replace("\n", "");
		return result;
	}
	
	
}
