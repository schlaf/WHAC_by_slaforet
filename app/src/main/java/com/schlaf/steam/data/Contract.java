package com.schlaf.steam.data;

import java.util.ArrayList;

public class Contract implements Comparable<Contract>, ModelRestrictor {

	private String contractId;

	private String title;
	private String description;
	private String factionId;

	private TierBenefit benefit;
	
	private String htmlDescription = null;

	private ArrayList<TierEntry> onlyModels = new ArrayList<TierEntry>();
	
	@Override
	public boolean isAllowedModel(String id) {
		for (TierEntry allowed : onlyModels) {
			if (allowed.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAllowedModel(int level, String id) {
		return isAllowedModel(id);
	}

	@Override
	public int compareTo(Contract another) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[contract : ").append(title).append(" - onlyModels = [");
		for (TierEntry model : onlyModels) {
			sb.append(model.toString()).append(" ");
		}
		sb.append("]]");
		return sb.toString();
	}
	
	public String toHtmlString() {
		if (htmlDescription == null) {

			StringBuffer sb = new StringBuffer(1024);
			
			sb.append("<b> Description : </b><br>")
				.append(description)
				.append("<br>");
			sb.append("<b>Allowed elements : </b>");
			for (TierEntry entry :  onlyModels) {
				sb.append("<br> -");
				sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
			}
			
			sb.append("<br><b>Benefits : </b>");
			for (TierFACostBenefit alteration : getBenefit().getAlterations()) {
				sb.append("<br>");
				sb.append(ArmySingleton.getInstance().getArmyElement(alteration.getEntry().getId()).getFullName());
				if (alteration instanceof TierCostAlteration) {
					sb.append(" : -").append( ((TierCostAlteration) alteration).getCostAlteration() ).append("PC");	
				}
				if (alteration instanceof TierFAAlteration) {
					sb.append(" : +").append( ((TierFAAlteration) alteration).getFaAlteration() ).append("FA");	
				}
			}
			for (TierFreeModel freeModel : getBenefit().getFreebies()) {
				sb.append("<br>One of (");
				boolean first = true;
				for (TierEntry entry : freeModel.getFreeModels()) {
					if (!first) {
						sb.append(", ");
						first = false;
					}
					sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
				}
				sb.append(") for free");
			}
			if (getBenefit().getInGameEffect() != null) {
				sb.append("<br>").append(getBenefit().getInGameEffect());
			}
			
			htmlDescription = sb.toString();
		}
		return htmlDescription;
	}
	
	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFactionId() {
		return factionId;
	}

	public void setFactionId(String factionId) {
		this.factionId = factionId;
	}

	public String getHtmlDescription() {
		return htmlDescription;
	}

	public ArrayList<TierEntry> getOnlyModels() {
		return onlyModels;
	}

	public TierBenefit getBenefit() {
		return benefit;
	}

	public void setBenefit(TierBenefit benefit) {
		this.benefit = benefit;
	}
	
}
