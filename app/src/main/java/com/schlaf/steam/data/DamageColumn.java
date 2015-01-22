package com.schlaf.steam.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * column damage for warmachine grid (from top to bottom
 * @author S0085289
 *
 */
public class DamageColumn implements Serializable {
	/** serial */
	private static final long serialVersionUID = 7108244888127130419L;
	int id;
	
	private WarjackLikeDamageGrid parent;
	
	
	List<DamageBox> boxes = new ArrayList<DamageBox>();
	
	public DamageColumn(WarjackLikeDamageGrid parentGrid) {
		this.parent = parentGrid;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<DamageBox> getBoxes() {
		return boxes;
	}
	public void setBoxes(List<DamageBox> boxes) {
		this.boxes = boxes;
	}
	
	/**
	 * applies damages, return count of damages applied on this column
	 * @param dmg
	 * @return
	 */
	public int applyDamages(int dmg) {
		int dmgApplied = 0;
		for (DamageBox box : boxes) {
			if (dmgApplied < dmg) {
				if (!box.getSystem().equals(
						WarmachineDamageSystemsEnum.EMPTY)) {
					if (!box.isDamaged()) {
						box.setDamaged(true);
						parent.getJustDamagedBoxes().add(box);
						dmgApplied++;
					}

				}
			}
		}
		return dmgApplied;
	}

	/**
	 * applies damages, return count of damages applied on this column
	 * @param dmg
	 * @return
	 */
	public int applyFakeDamages(int dmg) {
		int dmgApplied = 0;
		if (dmg > 0) {
			for (DamageBox box : boxes) {
				if (dmgApplied < dmg) {
					if (!box.getSystem().equals(
							WarmachineDamageSystemsEnum.EMPTY)) {
						if (!box.isDamaged() && !box.isDamagedPending()) {
							box.setDamagedPending(true);
							box.setCurrentlyChangePending(true);
							parent.getJustDamagedBoxes().add(box);
							dmgApplied++;
						}

					}
				}
			}
		} else {
			// do nothing, this is done from parent...
		}
		return dmgApplied;
	}
}
