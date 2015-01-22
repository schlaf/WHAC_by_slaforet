package com.schlaf.steam.activities.battleplanner;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.SharedPreferences;

import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.selectlist.selected.SpellCaster;
import com.schlaf.steam.data.Capacity;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Spell;

public class BattlePlanningEntry {

	private String modelUniqueId;
	private String label;

	private boolean featOrMiniFeatAvailable = false;
	private boolean powerAttackAvailable = false;
	private boolean spellsAvailable = false;

	private boolean doFeat;
	private boolean doSpell;
	private boolean doSpecial;
	
	private ActionEnum actionMove; // one of move & hit, move & shoot, run, charge, slam, trample
	
	private PowerAttackEnum powerAttack;
	private List<PlanningSpell> spells = new ArrayList<PlanningSpell>();
	private List<String> specialActions = new ArrayList<String>();

	private String specialActionChosen = null;
	
	public BattlePlanningEntry(BattleEntry entry, SharedPreferences prefs) {

		modelUniqueId = entry.getId();
		label = entry.getLabel();
		
		
		String actionsForModel = prefs.getString(modelUniqueId, "");
		if (actionsForModel != null && actionsForModel.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(actionsForModel, ";");
			while (tokenizer.hasMoreTokens()) {
				specialActions.add(tokenizer.nextToken());
			}
		}
		
		
		for (SingleModel model : entry.getReference().getModels()) {
			if (model.getCapacities() != null && ! model.getCapacities().isEmpty()) {
				for (Capacity capa : model.getCapacities()) {
					if ("*Action".equals(capa.getType())) {
						specialActions.add("*Action " + capa.getTitle());
					}
					if ("Order".equals(capa.getType())) {
						specialActions.add("Order " + capa.getTitle());
					}
					if ("*Attack".equals(capa.getType())) {
						specialActions.add("*Attack " + capa.getTitle());
					}
				}
			}
		}
		
		switch (entry.getReference().getModelType()) {
		case WARCASTER:
		case WARLOCK:
			featOrMiniFeatAvailable = true;
			break;
		case WARJACK:
		case COLOSSAL:
		case WARBEAST:
		case GARGANTUAN:
			powerAttackAvailable = true;
			break;
		default:
			// rien
		}
		
		if (entry.getReference() instanceof SpellCaster) {
			List<Spell> spellsList = ((SpellCaster) entry.getReference()).getSpells();
			if (spellsList != null && ! spellsList.isEmpty()) {
				spells = new ArrayList<PlanningSpell>(spellsList.size());
				for (Spell spell : spellsList) {
					spells.add(new PlanningSpell(spell));
				}
				spellsAvailable = true;
			}
			
		}
	}

	public String getLabel() {
		return label;
	}

	public boolean isFeatOrMiniFeatAvailable() {
		return featOrMiniFeatAvailable;
	}

	public boolean isPowerAttackAvailable() {
		return powerAttackAvailable;
	}

	public boolean isSpellsAvailable() {
		return spellsAvailable;
	}

	public boolean isDoFeat() {
		return doFeat;
	}

	public PowerAttackEnum getPowerAttack() {
		return powerAttack;
	}

	public void setDoFeat(boolean doFeat) {
		this.doFeat = doFeat;
	}
	
	public void flipFeat() {
		this.doFeat = !doFeat;
	}


	public void setPowerAttack(PowerAttackEnum powerAttack) {
		this.powerAttack = powerAttack;
	}

	public List<String> getSpecialActions() {
		return specialActions;
	}

	public boolean isDoSpell() {
		return doSpell;
	}

	public void setDoSpell(boolean doSpell) {
		this.doSpell = doSpell;
	}
	
	public void flipSpell() {
		this.doSpell = !doSpell;
	}

	public boolean isDoSpecial() {
		return doSpecial;
	}

	public void setDoSpecial(boolean doSpecial) {
		this.doSpecial = doSpecial;
	}
	
	public void flipSpecial() {
		this.doSpecial = !doSpecial;
	}

	public ActionEnum getActionMove() {
		return actionMove;
	}

	public void setActionMove(ActionEnum actionMove) {
		this.actionMove = actionMove;
	}

	public List<PlanningSpell> getSpells() {
		return spells;
	}

	public void setSpells(List<PlanningSpell> spells) {
		this.spells = spells;
	}

	public String getSpecialActionChosen() {
		return specialActionChosen;
	}

	public void setSpecialActionChosen(String specialActionChosen) {
		this.specialActionChosen = specialActionChosen;
	}
	
	

}
