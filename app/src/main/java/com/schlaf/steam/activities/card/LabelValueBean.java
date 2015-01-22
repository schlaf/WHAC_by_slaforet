package com.schlaf.steam.activities.card;


public class LabelValueBean implements Comparable<LabelValueBean> {
	String id;
	String label;

	public LabelValueBean(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return label;
	}

	@Override
	public int compareTo(LabelValueBean another) {
		return label.compareTo(another.toString());
	}
}
