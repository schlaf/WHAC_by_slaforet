package com.schlaf.steam.activities.damages;

public interface ColumnChangeNotifier {

	/**
	 * register an observer of this grid
	 * @param observer
	 */
	public void registerColumnObserver(ColumnChangeObserver observer);
	
	public void deleteColumnObserver(ColumnChangeObserver observer);
}
