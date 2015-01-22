package com.schlaf.steam.activities.card;


public class CardLibraryRowData implements Comparable<CardLibraryRowData> {
	String id;
	String label;
	String qualification;
	private boolean completed;

	public CardLibraryRowData(String id, String label, String qualification, boolean completed) {
		this.id = id;
		this.label = label;
		this.qualification = qualification;
		this.completed = completed;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
	
	public String getQualification() {
		return qualification;
	}
	
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public int compareTo(CardLibraryRowData another) {
		return label.compareTo(another.getLabel());
	}
}
