package com.schlaf.steam.data;

import java.util.ArrayList;

public class Tier implements Comparable<Tier>, ModelRestrictor {

	private String id;
	private String casterId;
	private String title;
	private String factionId;
	
	private String htmlDescription = null;
	
	private ArrayList<AvailableModels> availableModels = new ArrayList<AvailableModels>(4);
	private ArrayList<TierLevel> levels = new ArrayList<TierLevel>(4);

	
	public ArrayList<TierEntry> getAllowedModels() {
		return levels.get(0).getOnlyModels();
	}
	
	@Override
	public boolean isAllowedModel(int level, String id) {
		if (level == 0) { // not yet calculated?
			level = 1;
		}
		ArrayList<TierEntry> alloweds = levels.get(level - 1).getOnlyModels();
		for (TierEntry allowed : alloweds) {
			if (allowed.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCasterId() {
		return casterId;
	}

	public void setCasterId(String casterId) {
		this.casterId = casterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<TierLevel> getLevels() {
		return levels;
	}
	
	
	public static Tier generateMockTiers() {
		Tier tiers = new Tier();
		tiers.setId("id");
		tiers.setCasterId("pseverius");
		tiers.setTitle("northern crusade");
		
		TierLevel level1 = new TierLevel();
		
		ArrayList<TierEntry> onlyModels = new ArrayList<TierEntry>();
		TierEntry entry1 = new TierEntry("pseverius");
		TierEntry entry2 = new TierEntry("Castigator");
		TierEntry entry3 = new TierEntry("revenger");
		TierEntry entry31 = new TierEntry("REPENTER");
		TierEntry entry4 = new TierEntry("errants");
		TierEntry entry5 = new TierEntry("templeflameguard");
		TierEntry entry6 = new TierEntry("ua_errants");
		TierEntry entry7 = new TierEntry("ua_templeflameguard");
		TierEntry entry8 = new TierEntry("Templar");
		
		
		
		
		onlyModels.add(entry1);
		onlyModels.add(entry2);
		onlyModels.add(entry3);
		onlyModels.add(entry31);
		onlyModels.add(entry4);
		onlyModels.add(entry5);
		onlyModels.add(entry6);
		onlyModels.add(entry7);
		onlyModels.add(entry8);
		
		
		ArrayList<TierEntryGroup> mustHaveModels1 = new ArrayList<TierEntryGroup>();
		TierEntryGroup group1 = new TierEntryGroup();
		group1.setMinCount(1);
		group1.setLabel("at least severius");
		group1.getEntries().add(new TierEntry("pseverius"));
		mustHaveModels1.add(group1);
		level1.getMustHaveModels().addAll(mustHaveModels1);
		
		
		TierBenefit benefit1 = new TierBenefit() ;
		
		level1.setLevel(1);
		level1.setBenefit(benefit1);
		level1.getOnlyModels().clear();
		level1.getOnlyModels().addAll(onlyModels);
		
		TierLevel level2 = new TierLevel();
		
		ArrayList<TierEntryGroup> mustHaveModels2 = new ArrayList<TierEntryGroup>();
		
		TierEntryGroup group2 = new TierEntryGroup();
		group2.setMinCount(2);
		group2.setLabel("at least 2 of templeflameguard/errants");
		group2.getEntries().add(new TierEntry("templeflameguard"));
		group2.getEntries().add(new TierEntry("errants"));
		
		mustHaveModels2.add(group2);
		level2.getOnlyModels().addAll(onlyModels);
		level2.getMustHaveModels().addAll(mustHaveModels2);
		level2.setLevel(2);

		TierLevel level3 = new TierLevel();
		
		
		ArrayList<TierEntryGroup> mustHaveModels3 = new ArrayList<TierEntryGroup>();
		
		TierEntryGroup group3 = new TierEntryGroup();
		group3.setMinCount(3);
		group3.setLabel("at least 3 of revenger");
		group3.getEntries().add(new TierEntry("revenger"));
		mustHaveModels3.add(group3);
		
		level3.getOnlyModels().addAll(onlyModels);
		level3.getMustHaveModels().addAll(mustHaveModels3);
		level3.setLevel(3);
		
		TierBenefit benefit = new TierBenefit() ;
		
		TierFAAlteration alteration = new TierFAAlteration();
		alteration.setEntry(new TierEntry("errants"));
		alteration.setFaAlteration(2);
		benefit.getAlterations().add(alteration);
		level2.setBenefit(benefit);
		

		TierBenefit benefit3 = new TierBenefit() ;
		
		TierCostAlteration alteration3 = new TierCostAlteration();
		alteration3.setEntry(new TierEntry("REPENTER"));
		alteration3.setCostAlteration(1);
		benefit3.getAlterations().add(alteration3);
		level3.setBenefit(benefit3);
		level3.setBenefit(benefit3);
		
		TierFreeModel freeby3 = new TierFreeModel();
		freeby3.getFreeModels().add(new TierEntry("ua_errants"));
		freeby3.getFreeModels().add(new TierEntry("ua_templeflameguard"));
		freeby3.getFreeModels().add(new TierEntry("Templar"));
		
		benefit3.getFreebies().add(freeby3);
		
		tiers.getLevels().add(level1);
		tiers.getLevels().add(level2);
		tiers.getLevels().add(level3);
		
		return tiers;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(512);
		sb.append("[tiers caster=").append(casterId).append(" - title = ").append(title);
		for (TierLevel level : levels) {
			sb.append(level.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String toHtmlString() {
		if (htmlDescription == null) {

			StringBuffer sb = new StringBuffer(1024);
			
			
			
			sb.append("<b> Caster : </b><br>")
				.append(ArmySingleton.getInstance().getArmyElement(casterId).getFullName())
				.append("<br>");
			sb.append("<b>Allowed elements : </b>");
			for (TierEntry entry :  levels.get(0).getOnlyModels()) {
				sb.append("<br> -");
				sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
			}

			for (TierLevel level : levels) {
				sb.append("<br><b><u>Level ").append(level.getLevel());
				sb.append("</u></b><br>");
				
				if (! level.getMustHaveModels().isEmpty()) {
					sb.append("<b>Must have : </b>");
					for (TierEntryGroup entryGroup :  level.getMustHaveModels()) {
						sb.append("<br>");
						sb.append(entryGroup.getMinCount()).append(" among {");
						boolean first = true;
						for (TierEntry entry :  entryGroup.getEntries()) {
							if (!first) {
								sb.append(", ");
							}
							first = false;
							sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
						}
						sb.append("}");
					}
				}
				
				if (! level.getMustHaveModelsInMarshal().isEmpty()) {
					sb.append("<b>Must have (marshalled): </b>");
					for (TierEntryGroup entryGroup :  level.getMustHaveModelsInMarshal()) {
						sb.append("<br>");
						sb.append(entryGroup.getMinCount()).append(" among {");
						boolean first = true;
						for (TierEntry entry :  entryGroup.getEntries()) {
							if (!first) {
								sb.append(", ");
							}
							first = false;
							sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
						}
						sb.append("}");
					}
				}
				
				sb.append("<br><b>Benefits : </b>");
				boolean described = false; // at least one benefit described... 
				for (TierFACostBenefit alteration : level.getBenefit().getAlterations()) {
					described = true;
					sb.append("<br>");
					sb.append(ArmySingleton.getInstance().getArmyElement(alteration.getEntry().getId()).getFullName());
					if (alteration instanceof TierCostAlteration) {
						sb.append(" : -").append( ((TierCostAlteration) alteration).getCostAlteration() ).append("PC");	
					}
					if (alteration instanceof TierFAAlteration) {
						if ( ((TierFAAlteration) alteration).getFaAlteration() == ArmyElement.MAX_FA) {
							sb.append(" FA:U");
						} else {
							sb.append(" : +").append( ((TierFAAlteration) alteration).getFaAlteration() ).append("FA");	
						}
							
					}
				}
				for (TierFreeModel freeModel : level.getBenefit().getFreebies()) {
					described = true;
					sb.append("<br>One of (");
					boolean first = true;
					for (TierEntry entry : freeModel.getFreeModels()) {
						if (!first) {
							sb.append(", ");
						}
						first = false;
						sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
					}
					sb.append(") for free");
					if (freeModel.getForEach() != null && freeModel.getForEach().size() > 0) {
						sb.append(" for each of (");
						boolean firstA = true;
						for (TierEntry entry : freeModel.getForEach()) {
							if (!firstA) {
								sb.append(", ");
							}
							firstA = false;
							sb.append(ArmySingleton.getInstance().getArmyElement(entry.getId()).getFullName());
						}
						sb.append(")");
					}
				}
				if (level.getBenefit().getInGameEffect() != null && level.getBenefit().getInGameEffect().trim().length() > 0) {
					sb.append("<br>").append(level.getBenefit().getInGameEffect());
					described = true;
				}
				
				if (!described) {
					sb.append("<br>Ingame effect");
				}
				
				sb.append("<br>");
			}
			
			htmlDescription = sb.toString();
		}
		return htmlDescription;
		
	}

	public String getFactionId() {
		return factionId;
	}

	public void setFactionId(String factionId) {
		this.factionId = factionId;
	}

	@Override
	public int compareTo(Tier another) {
		return title.compareTo(another.getTitle());
	}

	@Override
	public boolean isAllowedModel(String id) {
		return isAllowedModel(0, id);
	}

	public ArrayList<AvailableModels> getAvailableModels() {
		return availableModels;
	}

	public void setAvailableModels(ArrayList<AvailableModels> availableModels) {
		this.availableModels = availableModels;
	}
	

	
}
