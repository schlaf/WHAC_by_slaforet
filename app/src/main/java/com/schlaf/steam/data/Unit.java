package com.schlaf.steam.data;

import java.util.List;

/**
 * définition compléte d'une unité (valeur de base + attachments)
 * @author S0085289
 *
 */
public class Unit extends ArmyElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3436198356141506311L;
	private int baseCost;
	private int baseNumberOfModels;
	private int fullCost;
	private int fullNumberOfModels;
	
	/** true if unit model count may vary */
	private boolean variableSize;
	
	private boolean unitAttachmentAllowed;
	private boolean weaponAttachmentAllowed;
	private boolean jackMarshall;
	private boolean jackMarshallViaUA = false;
	
	UnitAttachment unitAttachment;
	WeaponAttachment weaponAttachment;
	
	/** max number of weapon attachments allowed */
	private int maxWAAllowed;

	/**
	 * return true if one or more models in the unit (including UA & WA) may have many hitpoints
	 * @return
	 */
	public boolean hasMultiPVModels() {
		for (SingleModel model : getModels()) {
			if (model.getHitpoints().getTotalHits() > 1) {
				return true;
			}
		}
		if (unitAttachment != null) {
			for (SingleModel model : unitAttachment.getModels()) {
				if (model.getHitpoints().getTotalHits() > 1) {
					return true;
				}
			}
		}
		if (weaponAttachment != null) {
			for (SingleModel model : weaponAttachment.getModels()) {
				if (model.getHitpoints().getTotalHits() > 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	@Override
	public ModelTypeEnum getModelType() {
		// TODO Auto-generated method stub
		return ModelTypeEnum.UNIT;
	}

	public int getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(int baseCost) {
		this.baseCost = baseCost;
	}

	public int getBaseNumberOfModels() {
		return baseNumberOfModels;
	}

	public void setBaseNumberOfModels(int baseNumberOfModels) {
		this.baseNumberOfModels = baseNumberOfModels;
	}

	public int getFullCost() {
		return fullCost;
	}

	public void setFullCost(int fullCost) {
		this.fullCost = fullCost;
	}

	public int getFullNumberOfModels() {
		return fullNumberOfModels;
	}

	public void setFullNumberOfModels(int fullNumberOfModels) {
		this.fullNumberOfModels = fullNumberOfModels;
	}

	public boolean isUnitAttachmentAllowed() {
		return unitAttachmentAllowed;
	}

	public void setUnitAttachmentAllowed(boolean unitAttachmentAllowed) {
		this.unitAttachmentAllowed = unitAttachmentAllowed;
	}

	public boolean isWeaponAttachmentAllowed() {
		return weaponAttachmentAllowed;
	}

	public void setWeaponAttachmentAllowed(boolean weaponAttachmentAllowed) {
		this.weaponAttachmentAllowed = weaponAttachmentAllowed;
	}

	public boolean isJackMarshall() {
		return jackMarshall;
	}

	public void setJackMarshall(boolean jackMarshall) {
		this.jackMarshall = jackMarshall;
	}

	public UnitAttachment getUnitAttachment() {
		return unitAttachment;
	}

	public void setUnitAttachment(UnitAttachment unitAttachment) {
		this.unitAttachment = unitAttachment;
	}

	@Override
	public boolean hasStandardCost() {
		return true;
	}

	public boolean isVariableSize() {
		return variableSize;
	}

	public void setVariableSize(boolean variableSize) {
		this.variableSize = variableSize;
	}

	public WeaponAttachment getWeaponAttachment() {
		return weaponAttachment;
	}

	public void setWeaponAttachment(WeaponAttachment weaponAttachment) {
		this.weaponAttachment = weaponAttachment;
	}
	

	public int getMaxWAAllowed() {
		return maxWAAllowed;
	}

	public void setMaxWAAllowed(int maxWAAllowed) {
		this.maxWAAllowed = maxWAAllowed;
	}

	public boolean isJackMarshallViaUA() {
		return jackMarshallViaUA;
	}

	public void setJackMarshallViaUA(boolean jackMarshallViaUA) {
		this.jackMarshallViaUA = jackMarshallViaUA;
	}


	@Override
	public String getCostString() {
		if (variableSize) {
			return baseCost + "/" + fullCost;
		} else {
			return String.valueOf(baseCost);
		}
	}
	
	

}
