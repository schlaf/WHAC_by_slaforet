/**
 * 
 */
package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.schlaf.steam.activities.selectlist.ModelCostCalculator;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.Unit;

/**
 * @author S0085289
 *
 * selected unit
 */
public class SelectedUnit extends SelectedEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1574173811720663533L;

	private static int orderingOffset = SelectedSection.orderingOffsetUnit + 10;
	
	/** helps sorting various instances */
	public int getOrderingOffset() {
		return orderingOffset;
	}
	
	public SelectedUnit(String id, String label,  boolean minSize) {
		super(id, label);
		this.minSize = minSize;
	}
	
	/** the unit is at min size */
	private boolean minSize;
	
	/** ranking officer (for mercs units only) */
	private SelectedRankingOfficer rankingOfficer;
	
	/** generic attachment, like the soulless escort */
	private SelectedSolo soloAttachment;
	
	/** the unique unit attachment */
	private SelectedUA unitAttachment;
	
	/** the weapon attachment (may have many models) */
	private List<SelectedWA> weaponAttachments = new ArrayList<SelectedWA>();

	
	/**
	 * return the concatenation of UA, WA and eventually warjacks of warbeasts attached
	 * @return
	 */
	public List<SelectedEntry> getChilds() {
		List<SelectedEntry> result = new ArrayList<SelectedEntry>();
		if (rankingOfficer != null) {
			result.add(rankingOfficer);
		}
		if (soloAttachment != null) {
			result.add(soloAttachment);
		}
		if (unitAttachment != null) {
			result.add(unitAttachment);
		}
		result.addAll(weaponAttachments);
		return result;
	}
	
	
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		
		SelectionUnit selection = (SelectionUnit) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		sb.append(selection.getFullLabel());
		sb.append(getModelCountString());
		sb.append(getCostString());
		return sb.toString();

//		sb.append(" [");
//		if (selection.isVariableSize()) {
//			if (minSize) {
//				sb.append(selection.getMinSize());
//			} else {
//				sb.append(selection.getMaxSize());
//			}
//			
//		} else {
//			sb.append(selection.getMinCost());
//		}
//		sb.append(" models");
//		
//		if (unitAttachment != null) {
//			ArmyElement ua = ArmySingleton.getInstance().getArmyElement(unitAttachment.getId());
//			sb.append(" + UA");
//		}
//		if (weaponAttachments != null && weaponAttachments.size() > 0) {
//			ArmyElement wa = ArmySingleton.getInstance().getArmyElement(weaponAttachments.get(0).getId());
//			sb.append(" + ").append(weaponAttachments.size()).append(" WA");
//		}
//		sb.append("]");
//
//		StringBuffer sbCost = new StringBuffer();
//		sbCost.append("[").append(getTotalCost());
//		sbCost.append("PC]");
//	
//		sb.append(sbCost.toString());
//		return sb.toString();
	}
	
	public String getModelCountString() {
		StringBuffer sb = new StringBuffer();
		SelectionUnit selection = (SelectionUnit) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		sb.append(" [");
		if (selection.isVariableSize()) {
			if (minSize) {
				sb.append(selection.getMinSize());
			} else {
				sb.append(selection.getMaxSize());
			}
		} else {
			sb.append(selection.getMinSize());
		}
		sb.append(" models");
		if (rankingOfficer != null) {
			sb.append(" + ranking officer");
		}
		if (unitAttachment != null) {
			ArmyElement ua = ArmySingleton.getInstance().getArmyElement(unitAttachment.getId());
			sb.append(" + UA");
		}
		if (weaponAttachments != null && weaponAttachments.size() > 0) {
			// ArmyElement wa = ArmySingleton.getInstance().getArmyElement(weaponAttachments.get(0).getId());
			sb.append(" + ").append(weaponAttachments.size()).append(" WA");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	
	public String getAttachString() {
		
		StringBuffer sb = new StringBuffer();
		SelectionUnit selection = (SelectionUnit) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		if (selection.isVariableSize()) {
			if (minSize) {
				sb.append("Min. unit");
			} else {
				sb.append("Max. unit");
			}
		} else {
			sb.append("Unit");
		}
		if (rankingOfficer != null) {
			sb.append(" + ranking officer");
		}
		if (unitAttachment != null) {
			sb.append(" + UA");
		}
		if (weaponAttachments != null && weaponAttachments.size() > 0) {
			sb.append(" + ").append(weaponAttachments.size()).append(" WA");
		}
		
		return sb.toString();
	}
	
	public String getCostString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (isTiersAltered()) {
			sb.append("<font color=\"blue\">");
		}
		sb.append(getCost()).append("PC");
		if (isTiersAltered()) {
			sb.append("</font>");
		}
		if (rankingOfficer != null) {
			sb.append("+RA(");
			if (rankingOfficer.isTiersAltered()) {
				sb.append("<font color=\"blue\">");
			}
			sb.append(rankingOfficer.getCost());
			if (rankingOfficer.isTiersAltered()) {
				sb.append("</font>");
			}
			sb.append(")");
		}
		if (unitAttachment != null) {
			sb.append("+UA(");
			if (unitAttachment.isTiersAltered()) {
				sb.append("<font color=\"blue\">");
			}
			sb.append(unitAttachment.getCost());
			if (unitAttachment.isTiersAltered()) {
				sb.append("</font>");
			}
			sb.append(")");
		}
		if (weaponAttachments != null && weaponAttachments.size() > 0) {
			sb.append(" + ").append(weaponAttachments.size()).append(" WA(");
			if (weaponAttachments.get(0).isTiersAltered()) {
				sb.append("<font color=\"blue\">");
			}
			sb.append(weaponAttachments.get(0).getCost() * weaponAttachments.size());
			if (weaponAttachments.get(0).isTiersAltered()) {
				sb.append("</font>");
			}
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	public int getCost() {
		if (isTiersAltered()) {
			return getRealCost();
		}
		return ModelCostCalculator.getUnitCost(getId(), minSize);
	}

	
	public boolean isEquivalentTo(SelectedUnit other) {

		// same id
		if (! other.getId().equals(this.getId())) {
			return false;
		}

		// same size
		if ( other.isMinSize() != this.isMinSize()) {
			return false;
		}

		// same UA or null for both
		if (other.getUnitAttachment() != null) {
			if (this.getUnitAttachment() == null) {
				return false;
			}
		} else {
			if (this.getUnitAttachment() != null) {
				return false;
			}
		}
		
		// same WA count or null/empty for both
		if (other.getWeaponAttachments() != null && other.getWeaponAttachments().size() > 0) {
			if (this.getWeaponAttachments() == null || this.getWeaponAttachments().size() != other.getWeaponAttachments().size()) {
				return false;
			}
		} else {
			if (this.getWeaponAttachments() != null && this.getWeaponAttachments().size() > 0) {
				return false;
			}
		}
		
		return true;
	}

	public int getModelCount() {
		
		ArmyElement element = ArmySingleton.getInstance().getArmyElement(getId());
		Unit unit = (Unit) element;
		int modelCount = 0;
		if (unit.isVariableSize()) {
			if (isMinSize()) {
				modelCount = unit.getBaseNumberOfModels();
			} else {
				modelCount = unit.getFullNumberOfModels();
			}
		} else {
			modelCount = unit.getBaseNumberOfModels();
		}
		
		if (rankingOfficer != null) {
			modelCount ++;
		}

		if (unitAttachment != null) {
			ArmyElement ua = ArmySingleton.getInstance().getArmyElement(unitAttachment.getId());
			modelCount += ua.getModels().size();
		}
		if (weaponAttachments != null && weaponAttachments.size() > 0) {
			modelCount += weaponAttachments.size();
		}
		return modelCount;
	}
	
	

	
	@Override
	public int getTotalCost() {
		int result = getCost();
		if (rankingOfficer != null) {
			// ArmyElement ua = ArmySingleton.getInstance().getArmyElement(unitAttachment.getId());
			result += rankingOfficer.getCost();
		}
		if (unitAttachment != null) {
			// ArmyElement ua = ArmySingleton.getInstance().getArmyElement(unitAttachment.getId());
			result += unitAttachment.getCost();
		}
		if (weaponAttachments != null && weaponAttachments.size() > 0) {
			for (SelectedWA wa : weaponAttachments) {
				result += wa.getCost() ;	
			}
		}
		if (soloAttachment != null ){
			result += soloAttachment.getCost();
		}
		return result;
	}
		
	public boolean isMinSize() {
		return minSize;
	}

	public void setMinSize(boolean minSize) {
		this.minSize = minSize;
	}

	public SelectedUA getUnitAttachment() {
		return unitAttachment;
	}

	public void setUnitAttachment(SelectedUA unitAttachment) {
		this.unitAttachment = unitAttachment;
	}

	public List<SelectedWA> getWeaponAttachments() {
		return weaponAttachments;
	}

	public void setWeaponAttachments(List<SelectedWA> weaponAttachments) {
		this.weaponAttachments = weaponAttachments;
	}

	public SelectedRankingOfficer getRankingOfficer() {
		return rankingOfficer;
	}

	public void setRankingOfficer(SelectedRankingOfficer rankingOfficer) {
		this.rankingOfficer = rankingOfficer;
	}

	public SelectedSolo getSoloAttachment() {
		return soloAttachment;
	}

	public void setSoloAttachment(SelectedSolo soloAttachment) {
		this.soloAttachment = soloAttachment;
	}


}
