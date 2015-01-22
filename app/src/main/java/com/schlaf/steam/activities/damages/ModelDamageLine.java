package com.schlaf.steam.activities.damages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.data.DamageBox;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.WarmachineDamageSystemsEnum;

/**
 * classe for multi-hitpoints model without grid (single damage line)
 * @author S0085289
 *
 */
public class ModelDamageLine  extends DamageGrid implements Serializable {

	/** serial */
	private static final long serialVersionUID = -4870901852655167025L;
	
	List<DamageBox> boxes = new ArrayList<DamageBox>();
	
	/**
	 * keep tracks of boxes with damages, usefull for damage deletion.
	 */
	private List<DamageBox> justDamagedBoxes = new ArrayList<DamageBox>();
	
	/**
	 * create a new line by copy (no pointer reference to old line)
	 * @param line
	 */
	public ModelDamageLine(ModelDamageLine line) {
		for (DamageBox box :  line.getBoxes()) {
			DamageBox newBox = new DamageBox(WarmachineDamageSystemsEnum.HULL.getCode(), this);
			newBox.setDamaged(box.isDamaged());
			newBox.setDamagedPending(box.isDamagedPending());
			newBox.setCurrentlyChangePending(box.isCurrentlyChangePending());
			boxes.add(newBox);
		}
	}
	
	/**
	 * create an empty damage line
	 * @param hitPoints
	 */
	public ModelDamageLine(SingleModel model) {
		this.model = new MiniModelDescription(model);
		int hitPoints = model.getHitpoints().getTotalHits();
		for (int i = 0; i< hitPoints; i++) {
			boxes.add(new DamageBox(WarmachineDamageSystemsEnum.HULL.getCode(), this));
		}
	}
	
	/**
	 * create an empty damage line
	 * @param hitPoints
	 */
	public ModelDamageLine(MiniModelDescription model, int nbHits) {
		this.model = model;
		for (int i = 0; i< nbHits; i++) {
			boxes.add(new DamageBox(WarmachineDamageSystemsEnum.HULL.getCode(), this));
		}
	}	
	
	/**
	 * creates a pre-damaged damage line
	 * @param hitPoints
	 * @param damaged
	 */
	public ModelDamageLine(int hitPoints, int damaged) {
		for (int i = 0; i< hitPoints; i++) {
			DamageBox box = new DamageBox(WarmachineDamageSystemsEnum.HULL.getCode(), this);
			if (i < (hitPoints - damaged) ) {
				box.setDamaged(false);
			} else {
				box.setDamaged(true);
			}
			boxes.add(box);	
		}
	}
	
	/**
	 * creates a pre-damaged damage line
	 * @param hitPoints
	 * @param damaged
	 */
	public ModelDamageLine(int hitPoints) {
		for (int i = 0; i< hitPoints; i++) {
			DamageBox box = new DamageBox(WarmachineDamageSystemsEnum.HULL.getCode(), this);
			boxes.add(box);	
		}
	}
	

	public List<DamageBox> getBoxes() {
		return boxes;
	}

	@Override
	public DamageGrid fromString(String damageGridString) {
		return null;
	}


	/**
	 * returns a percentage value of total damaged hits vs. total hits.
	 * @return
	 */
	public float percentageDead() {
		float nbHits = 0;
		float nbDamage = 0;
		for (DamageBox box : boxes) {
			nbHits ++;
			if (box.isDamaged()) {
				nbDamage ++;
			}
		}
		return (nbDamage / nbHits);
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (DamageBox box : boxes) {
			sb.append(box.toString());
		}
		return sb.toString();
	}

	@Override
	public int applyFakeDamages(int damageAmount) {
		int damageApplied = 0;
		if (damageAmount > 0) {
			// add
			for (DamageBox box : boxes) {
				
				if (damageApplied < damageAmount) {
					if (! box.isDamaged() && ! box.isCurrentlyChangePending()) {
						box.setCurrentlyChangePending(true);
						box.setDamagedPending(true);
						damageApplied ++;
						justDamagedBoxes.add(box);
					}
					if (! box.isDamagedPending() && box.isCurrentlyChangePending()) {
						box.setCurrentlyChangePending(false);
						box.setDamagedPending(true);
						damageApplied ++;
						justDamagedBoxes.add(box);
					}
				}
			}
		} else {
			// remove
			for (int i = boxes.size() - 1; i >= 0; i--) {
				DamageBox box = boxes.get(i);
				if (damageApplied < - damageAmount) {
					if (box.isDamaged() && ! box.isCurrentlyChangePending()) {
						box.setCurrentlyChangePending(true);
						box.setDamagedPending(false);
						damageApplied ++;
					}
					if (box.isDamagedPending() && box.isCurrentlyChangePending()) {
						box.setCurrentlyChangePending(false);
						box.setDamagedPending(false);
						damageApplied ++;
					}
				}
			}
		}
		return damageApplied;
	}

	@Override
	public int applyFakeDamages(int column, int damageAmount) {
		throw new UnsupportedOperationException("can not apply applyFakeDamages(int column, int damageAmount) to a ModelDamageLine");
	}


	@Override
	public int getTotalHits() {
		return boxes.size();
	}


	@Override
	public DamageStatus getDamageStatus() {
		int nbHits = 0;
		int nbDamages = 0;
		for (DamageBox box : boxes) {
			if (!box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
				nbHits++;
				if (box.isDamaged()) {
					nbDamages++;
				}
			}
		}
		
		return new DamageStatus(nbHits, nbDamages, "");
	}
	
	public DamageStatus getDamagePendingStatus() {
		int nbHits = 0;
		int nbDamages = 0;
		for (DamageBox box : boxes) {
			if (!box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
				nbHits++;
				if ((box.isDamagedPending() && box.isCurrentlyChangePending()) || box.isDamaged()) {
					nbDamages++;
				}
			}
		}
		
		return new DamageStatus(nbHits, nbDamages, "");
	}

	@Override
	public void commitFakeDamages() {
		for (DamageBox box :  boxes) {
			if (box.isCurrentlyChangePending()) {
				box.setDamaged(box.isDamagedPending());
				box.setCurrentlyChangePending(false);
			}
		}
		notifyBoxChange();
		justDamagedBoxes = new ArrayList<DamageBox>();
		notifyCommit();
	}

	@Override
	public void resetFakeDamages() {
		for (DamageBox box :  boxes) {
			if (box.isCurrentlyChangePending()) {
				box.setDamagedPending(box.isDamaged());
				box.setCurrentlyChangePending(false);
			}
		}
		justDamagedBoxes = new ArrayList<DamageBox>();
		notifyBoxChange();
	}

	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		if (damageGrid instanceof ModelDamageLine) {
			Iterator<DamageBox> sourceIt = ((ModelDamageLine) damageGrid).getBoxes().iterator();
			
			for (DamageBox box : boxes) {
				DamageBox source = sourceIt.next();
				box.copyFrom(source);
			}
		}
		notifyCommit();
	}

	public List<DamageBox> getJustDamagedBoxes() {
		return justDamagedBoxes;
	}

	@Override
	public boolean heal1point() {
		boolean healed = false;
		for (int i = boxes.size() - 1; i >= 0 && !healed; i--) {
			DamageBox box = boxes.get(i);
			if (box.isDamaged() && ! box.isCurrentlyChangePending()) {
				box.setCurrentlyChangePending(true);
				box.setDamagedPending(false);
				healed = true;
			}
		}
		return healed;
	}

	@Override
	public boolean heal1point(int column) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		
		for (int i = 0; i < damageGridString.length() && i < boxes.size(); i++) { // boxes.size to avoid overflow !stupid
			char car = damageGridString.charAt(i);
			switch (car) {
			case 'X' :
				boxes.get(i).setDamaged(true);
				break;
			case 'O' : 
				boxes.get(i).setDamaged(false);
				break;
			}
		}
		
		return this;
	}

	@Override
	public String toStringWithDamageStatus() {
		StringBuffer sb = new StringBuffer();
		
		for (DamageBox db : boxes) {
			sb.append(db.isDamaged() ? 'X' : 'O');
		}
		return sb.toString();
	}

	
}
