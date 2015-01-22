package com.schlaf.steam.activities.selectlist;

import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Unit;

public class ModelCostCalculator {

	public static int getCost(String elementId) {
		ArmyElement element = ArmySingleton.getInstance().getArmyElement(elementId);
		// SelectionEntry element = SelectionModelSingleton.getInstance().getSelectionEntryById(elementId);
		return element.getBaseCost();
	}

	public static int getUnitCost(String elementId, boolean minSize) {
		ArmyElement element = ArmySingleton.getInstance().getArmyElement(elementId);
		
		if (minSize) {
			return ((Unit)element).getBaseCost();
		} else {
			return ((Unit)element).getFullCost();
		}
	}
	
	public static int getDragoonCost(String elementId, boolean dismountOption) {
		ArmyElement element = ArmySingleton.getInstance().getArmyElement(elementId);
		if (dismountOption) {
			return ((Solo)element).getDismountCost();
		} else {
			return ((Solo)element).getBaseCost();
		}
	}
}
