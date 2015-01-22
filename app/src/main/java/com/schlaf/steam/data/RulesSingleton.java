package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.HashMap;

public class RulesSingleton {

	private static RulesSingleton singleton;
	
	private HashMap<String, ArrayList<RuleCostAlteration>> costRules = new HashMap<String, ArrayList<RuleCostAlteration>>();
	private HashMap<String, ArrayList<RuleFAAlteration>> faRules = new HashMap<String, ArrayList<RuleFAAlteration>>();
	
	
	public static final RulesSingleton getInstance() {
		if (singleton == null) {
			singleton = new RulesSingleton();
		}
		return singleton;
	}


	public HashMap<String, ArrayList<RuleCostAlteration>> getCostRules() {
		return costRules;
	}


	public void setCostRules(
			HashMap<String, ArrayList<RuleCostAlteration>> costRules) {
		this.costRules = costRules;
	}


	public HashMap<String, ArrayList<RuleFAAlteration>> getFaRules() {
		return faRules;
	}


	public void setFaRules(HashMap<String, ArrayList<RuleFAAlteration>> faRules) {
		this.faRules = faRules;
	}


}
