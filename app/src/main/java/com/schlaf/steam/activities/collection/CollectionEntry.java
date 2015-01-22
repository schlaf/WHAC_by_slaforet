package com.schlaf.steam.activities.collection;

import java.io.Serializable;

public class CollectionEntry implements Serializable, Comparable<CollectionEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2702028367102801706L;
	/** army element reference */
	private String id;
	
	private String label;
	/** count possessed */
	private int possessed;
	/** count painted */
	private int painted;
	
	public CollectionEntry(String id, String label) {
		this.id = id;
		this.label = label;
	}
	
	public int getPossessed() {
		return possessed;
	}
	public void setPossessed(int possessed) {
		this.possessed = possessed;
	}
	public int getPainted() {
		return painted;
	}
	public void setPainted(int painted) {
		this.painted = painted;
	}
	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int compareTo(CollectionEntry another) {
		return label.compareTo(another.getLabel());
	}
	
}
