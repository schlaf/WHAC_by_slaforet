/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.util.EventListener;

import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

/**
 * @author S0085289
 *
 */
public interface ArmySelectionChangeListener extends EventListener {

	/**
	 * returns true if directly adds the model, return false if a dialog bos appears (to ask for attachment or unit size)
	 * @param model
	 * @return
	 */
	public boolean onModelAdded(SelectionEntry model);
	
	public void onEntryRemoved(SelectedEntry entry);

    public void onChangeSpecialistValue(SelectedEntry entry, boolean isSpecialist);
	
	public void viewSelectionDetail(SelectionEntry model);

    public void selectedGroup(SelectionGroup group);

    public void unselectedGroup();

	public void notifyArmyChange();


}
