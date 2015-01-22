package com.schlaf.steam.activities.damages;


/**
 * base class for every widget displaying damage status with variable column
 * @author S0085289
 *
 */
public interface ColumnChangeObserver {

	/**
	 * do something on grid value change
	 * @param grid
	 */
	public void onChangeColumn(ColumnChangeNotifier gridView) ;
	
	
}
