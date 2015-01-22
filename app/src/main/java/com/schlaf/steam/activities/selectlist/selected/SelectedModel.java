/**
 * 
 */
package com.schlaf.steam.activities.selectlist.selected;

import java.io.Serializable;

import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

/**
 * any model selected (not caster, not unit or attachment)
 * @author S0085289
 *
 */
public abstract class SelectedModel extends SelectedEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1529759773909714423L;
	
	public SelectedModel(String id, String label) {
		super(id, label);
	}
	
	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		
		SelectionEntry selection = (SelectionEntry) SelectionModelSingleton.getInstance().getSelectionEntryById(getId());
		sb.append(selection.getFullLabel());
		sb.append(getCostString());
		return sb.toString();
	}	
	

}
