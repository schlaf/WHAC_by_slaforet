package com.schlaf.steam.activities.selectlist.selected;

import java.util.List;


/**
 * interface for every model/unit that can have one or more jack in custody (warcaster, junior, jack marshall)
 * @author S0085289
 *
 */
public interface JackCommander extends Entry {

	/**
	 * the list of jacks currently attached to the model
	 * @return
	 */
	public List<SelectedWarjack> getJacks();
	
	/**
	 * return true if at least one instance of this jack in attached list
	 * @param modelId
	 * @return boolean
	 */
	public boolean hasJackInAttachment(String jackId);
	
	/**
	 * remove first occurence of jack with id
	 * @param jackId
	 */
	public void removeJack(String jackId);
	
}
