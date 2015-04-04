package com.schlaf.steam.activities.card;


public class CardLibraryRowData implements Comparable<CardLibraryRowData> {
	String id;
	String label;
	String qualification;
    String FA;
    String cost;
    String modelCount;
    boolean showModelCount;

	public CardLibraryRowData(String id, String label, String qualification, String FA, String cost, String modelCount, boolean showModelCount) {
		this.id = id;
		this.label = label;
		this.qualification = qualification;
        this.FA = FA;
        this.cost = cost;
        this.modelCount = modelCount;
        this.showModelCount = showModelCount;
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
	
    public String getFA() {
        return FA;
    }

    public String getCost() {
        return cost;
    }

    public String getModelCount() {
        return modelCount;
    }

    public boolean isShowModelCount() {
        return showModelCount;
    }

    @Override
	public int compareTo(CardLibraryRowData another) {
		return label.compareTo(another.getLabel());
	}
}
