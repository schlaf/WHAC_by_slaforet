/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.io.Serializable;

import android.graphics.Color;

import com.schlaf.steam.R;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.BattleEngine;
import com.schlaf.steam.data.Solo;
import com.schlaf.steam.data.Unit;
import com.schlaf.steam.data.UnitAttachment;
import com.schlaf.steam.data.Warbeast;
import com.schlaf.steam.data.Warcaster;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.Warlock;
import com.schlaf.steam.data.WeaponAttachment;

/**
 * 
 * parent class for any army entry
 * 
 * @author S0085289
 *
 */
public class BattleEntry implements Serializable, Comparable<BattleEntry> {

	/** serial 
	 */
	private static final long serialVersionUID = -5714431928636582300L;
	
	protected String id;
	protected String label;
	
	protected int uniqueId;
	
	/** this entry is attached to another */
	protected boolean attached = false;
	/** unique id of parent model */
	protected int parentId;
	/** childs (jacks, beast, attachments, multi-PV members of unit) */
	// protected transient ArrayList<BattleEntry> childs = new ArrayList<BattleEntry>();
	
	/** army reference */
	protected transient ArmyElement reference;
	
	/** helps sorting various instances */
	protected int getOrderingOffset() {
		
		if (reference instanceof Warcaster || reference instanceof Warlock) {
			return -10000;
		} 
		if (reference instanceof Unit || reference instanceof UnitAttachment || reference instanceof WeaponAttachment ) {
			return -5000;
		}
		if (reference instanceof Solo ) {
			return -1000;
		} 
		if (reference instanceof BattleEngine) {
			return -500;
		}
		
		return 0;
	}	
	
	public int getDrawableResource() {
		
		if (isAttached()) {
			if (reference instanceof Warjack || reference instanceof Warbeast ) {
				return R.drawable.gradient_background_jackbeast;
			}
			if (reference instanceof Solo ) {
				return R.drawable.gradient_background_jackbeast;
			} 
			if (reference instanceof Unit || reference instanceof UnitAttachment || reference instanceof WeaponAttachment ) {
				return R.drawable.gradient_background_unit;
			}
		} else {
			if (reference instanceof Warcaster || reference instanceof Warlock) {
				return R.drawable.gradient_background_caster;
			} 
			if (reference instanceof Warjack || reference instanceof Warbeast ) {
				return R.drawable.gradient_background_jackbeast;
			}
			if (reference instanceof Unit || reference instanceof UnitAttachment || reference instanceof WeaponAttachment ) {
				return R.drawable.gradient_background_unit;
			}
			if (reference instanceof Solo ) {
				return R.drawable.gradient_background_solo;
			} 
			if (reference instanceof BattleEngine) {
				return R.drawable.gradient_background_solo;
			}
		}
		
		
		
		
		return Color.BLACK;
	}
	
	public BattleEntry(ArmyElement entry, int entryCounter) {
		uniqueId = entryCounter;
		id = entry.getId();
		label = entry.getFullName();
		reference = entry;
	}

	public String getLabel() {
		return label;
	}
	
	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	public String getId() {
		return id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

//	public ArrayList<BattleEntry> getChilds() {
//		return childs;
//	}

	/**
	 * ordering offset helps macro-sorting. first casters, then units, then others
	 */
	@Override
	public int compareTo(BattleEntry another) {
		// Log.d("BattleEntry", "comparing " + this.toString() + " to " + another.toString());
	
		int result = 0;
		int fakeParentId = (parentId == 0 ? getUniqueId() : parentId);
		int otherFakeParentId = (another.getParentId() == 0 ? another.getUniqueId() : another.getParentId());
		
		result = fakeParentId - otherFakeParentId; // parents are pre-sorted
		if ( result == 0) {
			result = getOrderingOffset() - another.getOrderingOffset();
			if (result == 0) {
				result = uniqueId- another.getUniqueId();
			}
		} 
		return result;
		
//		if ( !this.isAttached() && !another.isAttached()) {
//			// compare "groups"
//			result = getOrderingOffset() - another.getOrderingOffset() + (int) Math.signum(uniqueId- another.getUniqueId());
//			return result;
//		}
//		if ( this.isAttached() && another.isAttached() &&
//				this.getParentId() == another.getParentId()) {
//			// compare childs of same parent
//			result = uniqueId - another.getUniqueId();
//			return result;
//		}
//		
//		result = uniqueId - another.getUniqueId();
//		return result;
		
//		if (another.isAttached() && another.getParentId() == this.uniqueId) {
//			// child always after parent
//			result = -1;
//		} else if (this.isAttached() && this.getParentId() == another.getUniqueId() ) {
//			// child always after parent
//			result =  1;
//		} else if (another.isAttached() && !isAttached()) {
//			// sort relatively to other's parent
//			result =  uniqueId - another.getParentId();
//		} else if (! another.isAttached() && isAttached()) {
//			// sort relatively to my parent
//			result =  getParentId() - another.getUniqueId();
//		} else if ( another.isAttached() && isAttached() ) {
//			// sort relatively to respective parents
//			result =  getParentId() - another.getParentId();
//		}
		
		// Log.d("BattleEntry", "result = " + result);
		// return result;
		
	}

	public ArmyElement getReference() {
		return reference;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[BattleEntry:id=").append(id).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

	public void setReference(ArmyElement reference) {
		this.reference = reference;
	}

	public int getUniqueId() {
		return uniqueId;
	}
	
	public boolean hasDamageGrid() {
		return false;
	}
	
}
