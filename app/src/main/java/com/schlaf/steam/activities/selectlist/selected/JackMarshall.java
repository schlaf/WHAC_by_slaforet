package com.schlaf.steam.activities.selectlist.selected;

/**
 * interface for any model with jackMarshall ability
 * @author S0085289
 *
 */
public interface JackMarshall extends JackCommander {

	public static final int NORMAL_MAX_OF_JACKS = 2;
	
	/**
	 * return the max number of jacks that can be affected to this entry
	 * @return
	 */
	public int getMaxJacks();
	
	public void setMaxJacks(int nbJacks);
}
