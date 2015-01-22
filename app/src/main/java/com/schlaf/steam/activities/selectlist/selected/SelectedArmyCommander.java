package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;
import java.util.List;

import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;

/**
 * caster/warlock selected, must use the subclass
 * @author S0085289
 *
 */
public abstract class SelectedArmyCommander extends SelectedEntry implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7880643404579967694L;

	private static int orderingOffset = SelectedSection.orderingOffsetCaster + 10;
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
	
	public SelectedArmyCommander(String id, String label) {
		super(id, label);
	}
	
	/** warcaster/lock attachment */
	SelectedModel attachment; 
	
	public SelectedModel getAttachment() {
		return attachment;
	}

	public void setAttachment(SelectedModel attachment) {
		this.attachment = attachment;
	}

	@Override 
	public int getTotalCost() {
		int result = 0; // Warcasters are free! 
		if (getAttachment() != null) {
			result += getAttachment().getCost();
		}
		for (SelectedModel attached : getAttachedModels()) {
			result += attached.getCost();
		}
		return result;
	}
	
	
	public abstract List<SelectedModel> getAttachedModels();
	
	/**
	 * return true if at least one instance of this model is attached to this caster
	 * @param modelId
	 * @return
	 */
	public boolean hasModelInAttachment(String modelId) {
		for (SelectedModel model : getAttachedModels()) {
			if (model.getId().equals(modelId)) {
				return true;
			}
		}
		if (getAttachment() != null && getAttachment().getId().equals(modelId)) {
			return true;
		}
		return false;
	}
	
	public int getModelCountInAttachement(String modelId) {
		int result = 0;
		for (SelectedModel model : getAttachedModels()) {
			if (model.getId().equals(modelId)) {
				result ++;
			}
		}
		if (getAttachment() != null && getAttachment().getId().equals(modelId)) {
			result++;
		}
		return result;
	}
	
	@Override
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		ArmyElement element = ArmySingleton.getInstance().getArmyElement(getId());
		
		sb.append(element.getName());
		if (getAttachment() != null) {
			sb.append(" + 1 attachment");
		}
		sb.append(" + ").append(getAttachedModels().size()).append(" models");
		return sb.toString();
	}
		
	@Override
	public int getModelCount() {
		int result = 1;
		if (getAttachment() != null) {
			result += getAttachment().getModelCount();
		}
		for (SelectedModel attached : getAttachedModels()) {
			result += attached.getModelCount();
		}
		return result;

	}
	
}
