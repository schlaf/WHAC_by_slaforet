/**
 * 
 */
package com.schlaf.steam.activities.selectlist.selection;

import java.io.Serializable;

import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ModelTypeEnum;

/**
 * classe de "model" pour une "ligne" de sélection d'armée (que ce soit un modèle unique ou une unité)
 * @author S0085289
 *
 */
public class SelectionEntry implements Serializable, Comparable<SelectionEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -267589792509476518L;
	/** unique id for this entry */
	private String id;
	
	/** short label (ex : caine1, harbinger, ...) */
	private String shortLabel;
	
	/** full label, with cost, details, ... */
	private String fullLabel;
	
	/** type = caster, beasts, unit, ... useful for grouping */
	private ModelTypeEnum type;
	
	/** indicates at least one is selected */
	protected boolean selected;
	
	/** indicates this entry is selectable (ex. unit attachment, caster attachment, point cost) */
	protected boolean selectable;
	
	
	/** base FA (as per rulebook) */
	protected int baseFA;
	/** actual FA (altered by tier) */
	protected int alteredFA;

	/** means FA=C */
	protected boolean uniqueCharacter; 
	
	protected int baseCost;
	protected int alteredCost;
	
	
	/** actual selection count*/
	protected int countSelected;
	/** total point cost from this row */ 
	protected int costSelected;
	
	private boolean mercenaryOrMinion = false;
	/** if mercenary, hide irrelevant entries*/
	private boolean visible = true;
	
	/** this model is a SR2015 objective */
	private boolean objective = false;
	
	/** card data is complete */
	private boolean completed;
	private String qualification;
	
	public SelectionEntry(ArmyElement baseUnit) {
		this.id = baseUnit.getId();
		this.shortLabel = baseUnit.getName();
		this.fullLabel = baseUnit.getFullName();
		this.type = baseUnit.getModelType();
		this.selectable = true;
		this.baseFA = baseUnit.getFA();
		this.uniqueCharacter = baseUnit.isUniqueCharacter();
		
		if (baseUnit.hasStandardCost()) {
			this.baseCost = baseUnit.getBaseCost();
		} else {
			this.baseCost = baseUnit.getBaseCost();
		}
		
		alteredFA = baseFA;
		completed = baseUnit.isCompleted();
		this.qualification = baseUnit.getQualification();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[id=").append(id).append("-Label=").append(fullLabel).append("-selectable=").append(selectable)
		.append("-selected=").append(selected).append("]");
		return sb.toString();
	}

	public String toHTMLTitleString() {
		
		// FA(<font color=\"white\"><B>2</B></font>) &nbsp Cost(<font color=\"white\"><B>5/8</B></font>
		
		StringBuffer sb = new StringBuffer();
		if (countSelected > alteredFA) {
			sb.append("<font color=\"red\">");
			sb.append(getShortLabel());
			sb.append("</font>"); 
		} else if (countSelected > 0){
			sb.append("<font color=\"green\">");
			sb.append(getShortLabel());
			sb.append("</font>"); 
		} else {
			if (selectable) {
				sb.append("<font color=\"black\">");
			} else {
				sb.append("<font color=\"#696969\">");
			}
			sb.append(getShortLabel());
			sb.append("</font>"); 
		}
		if (! shortLabel.equals(fullLabel)) {
			sb.append("<font color=\"#696969\" size=\"-2\"><i>").append("(").append(fullLabel).append(")").append("</i></font>");
		}
		
		return sb.toString();
	}

	
	public String toHTMLFA() {
		
		int nbcasters = SelectionModelSingleton.getInstance().getNbCasters();
		
		int alteredFAByCasterCount =  alteredFA;
		
		if (nbcasters > 1) {
			if (alteredFA == ArmyElement.C_FA && uniqueCharacter == true) {
				// C entries remain C
			} else if (alteredFA == ArmyElement.MAX_FA) {
				// U entries remain U
			} else {
				alteredFAByCasterCount = alteredFA * nbcasters;	
			}
		}
		
		// FA(<font color=\"white\"><B>2</B></font>) &nbsp Cost(<font color=\"white\"><B>5/8</B></font>
		
		StringBuffer sb = new StringBuffer();
		sb.append("<font color=\"grey\">FA:</font>");
		if (countSelected > alteredFAByCasterCount) {
			sb.append("<font color=\"red\">");
			sb.append(countSelected);
			sb.append("</font>"); 
		} else if (countSelected > 0){
			sb.append("<font color=\"green\">");
			sb.append(countSelected);
			sb.append("</font>"); 
		} 
		// altered FA is blue to notify user...
		if (alteredFA != baseFA) {
			sb.append("<font color=\"blue\">");
			if (countSelected> 0) {
				sb.append("/");
			}
			sb.append(getAlteredFAString(alteredFAByCasterCount));
			sb.append("</font>"); 
		} else {
			if (countSelected > 0) {
				sb.append("<font color=\"darkgrey\">");
				sb.append("/");
				sb.append(getAlteredFAString(alteredFAByCasterCount));
				sb.append("</font>"); 
			} else {
				sb.append("<font color=\"black\">");
				sb.append(getAlteredFAString(alteredFAByCasterCount));
				sb.append("</font>"); 
			}
		}		

		return sb.toString();
	}

	public String toHTMLCostString() {
		
		// FA(<font color=\"white\"><B>2</B></font>) &nbsp Cost(<font color=\"white\"><B>5/8</B></font>
		
		
		StringBuffer sb = new StringBuffer();
		if (alteredCost == baseCost) {
			sb.append("<font color=\"black\">");
		} else {
			sb.append("<font color=\"blue\">");
		}
		if (ModelTypeEnum.WARCASTER == type || ModelTypeEnum.WARLOCK == type) {
			sb.append("+").append(baseCost);
			if (ModelTypeEnum.WARCASTER == type ) {
				sb.append("WJ");
			} else {
				sb.append("WB");	
			}
			sb.append("</font>");
		} else {
			sb.append(alteredCost);
			sb.append("</font>");
			sb.append("<font color=\"grey\">PC</font>");
		}
		return sb.toString();
	}
	
	
	private String getAlteredFAString(int alteredFAByCasterCount) {
		if (alteredFAByCasterCount == ArmyElement.C_FA && uniqueCharacter == true) {
			return "C";
		} else if (alteredFAByCasterCount == ArmyElement.MAX_FA) {
			return "U";
		} else {
			return String.valueOf(alteredFAByCasterCount);
		}
	}


    public String getBaseFAString() {
        if (baseFA == ArmyElement.C_FA && uniqueCharacter == true) {
            return "C";
        } else if (baseFA == ArmyElement.MAX_FA) {
            return "U";
        } else {
            return String.valueOf(baseFA);
        }
    }
	
	/** helps sorting various instances */
	protected int getOrderingOffset() {
		return getType().ordinal() * 5000;
	}
	
	/**
	 * tri sur la base du label de base
	 */
	@Override
	public int compareTo(SelectionEntry another) {
		
		if (isMercenaryOrMinion() && another.isMercenaryOrMinion()) {
			// compare in order : caster, jacks, units, ua, wa, solos
			return getOrderingOffset() - another.getOrderingOffset() + getShortLabel().compareTo(another.getShortLabel()); 
		}
		
		return this.getShortLabel().compareTo(another.getShortLabel());
	}	

	public String getFullLabel() {
		return fullLabel;
	}
	
	public String getDisplayLabel() {
		if (fullLabel.equals(shortLabel)) {
			return fullLabel;
		} else {
			// epic, or something ...
			return shortLabel + "<font color=\"#696969\" size=\"-2\">(" + fullLabel + ")</font>";
		}
	}

	public void setFullLabel(String fullLabel) {
		this.fullLabel = fullLabel;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public int getCostSelected() {
		return costSelected;
	}

	public void setCostSelected(int costSelected) {
		this.costSelected = costSelected;
	}

	public String getId() {
		return id;
	}

	public ModelTypeEnum getType() {
		return type;
	}

	public int getBaseFA() {
		return baseFA;
	}

	public void setBaseFA(int baseFA) {
		this.baseFA = baseFA;
	}

	public int getAlteredFA() {
		return alteredFA;
	}

	public void setAlteredFA(int alteredFA) {
		this.alteredFA = alteredFA;
	}

	public int getCountSelected() {
		return countSelected;
	}

	public void setCountSelected(int countSelected) {
		this.countSelected = countSelected;
	}

	public void addOne() {
		countSelected ++;
		costSelected += baseCost; 
		if (countSelected > 0) {
			setSelected(true);
		}
	}

	public void removeOne() {
		if (countSelected > 0) {
			countSelected --;
			costSelected -= baseCost;
		}
		if (countSelected == 0) {
			setSelected(false);
		}
	}

	public int getBaseCost() {
		return baseCost;
	}

	public int getAlteredCost() {
		return alteredCost;
	}

	public boolean isUniqueCharacter() {
		return uniqueCharacter;
	}

	public void setAlteredCost(int alteredCost) {
		this.alteredCost = alteredCost;
	}

	public boolean isMercenaryOrMinion() {
		return mercenaryOrMinion;
	}

	public void setMercenaryOrMinion(boolean mercenaryOrMinion) {
		this.mercenaryOrMinion = mercenaryOrMinion;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isCompleted() {
		return completed;
	}

	public String getQualification() {
		return qualification;
	}

	public String getShortLabel() {
		return shortLabel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean isObjective() {
		return objective;
	}

	public void setObjective(boolean objective) {
		this.objective = objective;
	}
	
}
