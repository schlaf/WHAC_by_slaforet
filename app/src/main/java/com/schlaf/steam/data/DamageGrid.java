package com.schlaf.steam.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.activities.damages.BluetoothDamageChangeObserver;
import com.schlaf.steam.activities.damages.DamageChangeObserver;
import com.schlaf.steam.activities.damages.DamageStatus;

public abstract class DamageGrid implements Serializable {

	/** serial */
	private static final long serialVersionUID = 6848255206338757302L;
	
	private static final String TAG = "DamageGrid";
	private boolean D = false;

	/** the model that has this grid */
	protected MiniModelDescription model;
	
	protected int uniqueId; // to emit changes trough bluetooth
	
	transient List<DamageChangeObserver> observers = new ArrayList<DamageChangeObserver>();
	
	// whether notify of every box change.. 
	protected boolean notifyBoxChanges = true;
	
	/**
	 * create the grid/spiral from String
	 * @param damageGridString
	 * @return
	 */
	public abstract DamageGrid fromString(String damageGridString);
	
	/**
	 * create a damageGrid with damages, where boxes/circles "ok" are with O and "filled" with X.
	 * @param damageGridString
	 * @return
	 */
	public abstract DamageGrid fromStringWithDamageStatus(String damageGridString);
	
	/**
	 * returns a string wich holds the status of the damagegrid
	 * @return
	 */
	public abstract String toStringWithDamageStatus();
	
	/**
	 * ajoute des faux dommages (pending) é la ligne existante
	 * @param colNumber
	 * @param nbDamage
	 * @return nb damages applied
	 */
	public abstract int applyFakeDamages(int damageAmount);
	
	/**
	 * ajoute des faux dommages (pending) é la grilleexistante
	 * @param colNumber
	 * @param nbDamage
	 * @return nb damages applied
	 */
	public abstract int applyFakeDamages(int column, int damageAmount);
	
	/**
	 * heal the last damaged box
	 * @return
	 */
	public abstract boolean heal1point();

	/**
	 * heal the last damaged box of column
	 * @return
	 */

	public abstract boolean heal1point(int column);

	/**
	 * transform fake damages to real ones.
	 */
	public abstract void commitFakeDamages();
	
	/**
	 * erase all fake damages
	 */
	public abstract void resetFakeDamages();
	
	/**
	 * must be called whenever an operation is made on a damageBox 
	 */
	public final void notifyBoxChange() {
		if (D) Log.d(TAG, "notifyBoxChange");
		if (notifyBoxChanges) {
			if (observers != null) {
				for (DamageChangeObserver observer : observers) {
					observer.onChangeDamageStatus(this); 
				}
			}
		}
	}
	
	public final void notifyCommit() {
		if (observers != null) {
			Log.e("DamageGrid", "notify to " + observers.size() + " observers");
			for (DamageChangeObserver observer : observers) {
				observer.onApplyCommitOrCancel(this);
			}
		}
	}

	/**
	 * register an observer of this grid
	 * @param observer
	 */
	public void registerObserver(DamageChangeObserver observer) {
		if (observers == null) {
			observers = new ArrayList<DamageChangeObserver>();
		}
		if (! observers.contains(observer)) {
			// make sure no observer is registered twice
			observers.add(observer);	
		}
		
	}
	
	public void removeObserver(DamageChangeObserver observer) {
		if (observers != null) {
			observers.remove(observer);	
		}
	}
	
	public void removeBluetoothObservers() {
		if (observers != null) {
			Log.e("DamageGrid", "removing BT observers");
			for (DamageChangeObserver observer : observers) {
				if (observer instanceof BluetoothDamageChangeObserver) {
					observers.remove(observer);
					Log.e("DamageGrid", "1 BT observer removed");
					break; // never more than one bluetooth observer!
				}
			}
		}
		
	}
	
	public void removeObservers() {
		if (observers != null) {
			observers.clear();
		}
	}
	
	/**
	 * return total of hits
	 */
	public abstract int getTotalHits() ;
	
	/**
	 * return damage status
	 * @return
	 */
	public abstract DamageStatus getDamageStatus();
	

	public MiniModelDescription getModel() {
		return model;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int newId) {
		if (uniqueId != 0) {
			throw new UnsupportedOperationException("unique id for DamageGrid must be set only once! ");
		}
		this.uniqueId = newId; 
	}

	public abstract void copyStatusFrom(DamageGrid damageGrid) ;

}
