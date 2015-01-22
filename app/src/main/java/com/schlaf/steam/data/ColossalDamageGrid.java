package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.activities.damages.DamageStatus;
import com.schlaf.steam.activities.damages.ModelDamageLine;

public class ColossalDamageGrid extends WarjackLikeDamageGrid {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2943574784945930361L;
	
	private WarjackDamageGrid leftGrid;
	private WarjackDamageGrid rightGrid;
	private ModelDamageLine forceFieldGrid; // optionnal!
	
	/**
	 * keep tracks of boxes with damages, usefull for damage deletion.
	 */
	private List<DamageBox> justDamagedBoxes = new ArrayList<DamageBox>();

	
	
	public ColossalDamageGrid(SingleModel jack) {
		this.model = new MiniModelDescription(jack);
	}
	
	@Override
	public DamageGrid fromString(String damageGridString) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int applyFakeDamages(int damageAmount) {
		// no usage without column
		return 0;
	}

	public int applyFakeDamages(int column, int damageAmount, int secondaryColumn) {
		
		int applied = 0;
		if (damageAmount > 0) {
			
			if (forceFieldGrid != null && forceFieldGrid.getDamagePendingStatus().getRemainingPoints() > 0) {
				// apply first to forcefield;
				applied = forceFieldGrid.applyFakeDamages(damageAmount);
				// just add the latest boxes.
				for (DamageBox dmgBox : forceFieldGrid.getJustDamagedBoxes()) {
					if (! justDamagedBoxes.contains(dmgBox)) {
						justDamagedBoxes.add(dmgBox);
					}
				}
			}
			
			if (applied < damageAmount) {
				if (column >=0 && column < 6) {
					applied += leftGrid.applyFakeDamages(column, damageAmount - applied);
					for (DamageBox dmgBox : leftGrid.getJustDamagedBoxes()) {
						if (! justDamagedBoxes.contains(dmgBox)) {
							justDamagedBoxes.add(dmgBox);
						}
					}
					if (applied == damageAmount) {
						return applied;
					}
				} else if (column >=6 && column <12 ) {
					applied += rightGrid.applyFakeDamages(column-6, damageAmount - applied);
					for (DamageBox dmgBox : rightGrid.getJustDamagedBoxes()) {
						if (! justDamagedBoxes.contains(dmgBox)) {
							justDamagedBoxes.add(dmgBox);
						}
					}
					if (applied == damageAmount) {
						return applied;
					}
				} else {
					return 0;
				}
			}
			
			if (applied < damageAmount) {
				// still damages to apply
				if (getDamagePendingStatus().getRemainingPoints() > 0) {
				// propagate damages to other side
					if (secondaryColumn != -1) {
						// only if secondary column chosen!
						applyFakeDamages(secondaryColumn, damageAmount - applied, -1);
					}
				}
			}
			
			return applied;
		} else {
			// heal
			for (int i = justDamagedBoxes.size() - 1; i >=0 && damageAmount < 0 ; i--) {
				justDamagedBoxes.get(i).setCurrentlyChangePending(false);
				justDamagedBoxes.get(i).setDamagedPending(false);
				damageAmount++;
				applied++;
				justDamagedBoxes.remove(i);
			}
			return applied;
		}
		
		
	}

	@Override
	public void commitFakeDamages() {
		if (forceFieldGrid != null) {
			forceFieldGrid.commitFakeDamages();
		}
		leftGrid.commitFakeDamages();
		rightGrid.commitFakeDamages();
		
		justDamagedBoxes.clear();
		notifyCommit();

	}
	@Override
	public void resetFakeDamages() {
		if (forceFieldGrid != null) {
			forceFieldGrid.resetFakeDamages();
		}
		leftGrid.resetFakeDamages();
		rightGrid.resetFakeDamages();
		justDamagedBoxes.clear();
	}
	@Override
	public int getTotalHits() {
		return forceFieldGrid!=null?forceFieldGrid.getTotalHits():0 + leftGrid.getTotalHits() + rightGrid.getTotalHits();
	}
	
	@Override
	public DamageStatus getDamageStatus() {
		
		DamageStatus leftDmg = leftGrid.getDamageStatus();
		DamageStatus rightDmg = rightGrid.getDamageStatus();
		int hitPoints = leftDmg.getHitPoints() + rightDmg.getHitPoints();
		int damagedPoints = leftDmg.getDamagedPoints() + rightDmg.getDamagedPoints();
		
		if (forceFieldGrid != null) {
			DamageStatus ffDmg = forceFieldGrid.getDamageStatus();	
			hitPoints += ffDmg.getHitPoints();
			damagedPoints += ffDmg.getDamagedPoints();
		}
		
		return new DamageStatus(hitPoints, damagedPoints, "");
	}
	
	public DamageStatus getDamagePendingStatus() {
		DamageStatus leftDmg = leftGrid.getDamagePendingStatus();
		DamageStatus rightDmg = rightGrid.getDamagePendingStatus();
		int hitPoints = leftDmg.getHitPoints() + rightDmg.getHitPoints();
		int damagedPoints = leftDmg.getDamagedPoints() + rightDmg.getDamagedPoints();
		
		if (forceFieldGrid != null) {
			DamageStatus ffDmg = forceFieldGrid.getDamagePendingStatus();
			hitPoints += ffDmg.getHitPoints() ;
			damagedPoints += ffDmg.getDamagedPoints();
		}
		return new DamageStatus(hitPoints, damagedPoints, "");
	}
	
	public WarjackDamageGrid getLeftGrid() {
		return leftGrid;
	}
	public void setLeftGrid(WarjackDamageGrid leftGrid) {
		this.leftGrid = leftGrid;
	}
	public WarjackDamageGrid getRightGrid() {
		return rightGrid;
	}
	public void setRightGrid(WarjackDamageGrid rightGrid) {
		this.rightGrid = rightGrid;
	}

	public List<WarmachineDamageSystemsEnum> getSystems() {
		ArrayList<WarmachineDamageSystemsEnum> result = new ArrayList<WarmachineDamageSystemsEnum>();
		for (DamageColumn column : leftGrid.getColumns()) {
			for (DamageBox box : column.getBoxes()) {
				if (! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					if (! result.contains(box.getSystem())) {
						result.add(box.getSystem());
					}
				}
			}
		}
		for (DamageColumn column : rightGrid.getColumns()) {
			for (DamageBox box : column.getBoxes()) {
				if (! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					if (! result.contains(box.getSystem())) {
						result.add(box.getSystem());
					}
				}
			}
		}

		if (forceFieldGrid != null) {
			result.add(WarmachineDamageSystemsEnum.FORCE_FIELD);
		}
		return result;	
	}

	public DamageStatus getNbHitPointsSystem(WarmachineDamageSystemsEnum system) {
		
		if (system == WarmachineDamageSystemsEnum.FORCE_FIELD) {
			return forceFieldGrid.getDamageStatus();
		}
		
		DamageStatus leftDmg = leftGrid.getNbHitPointsSystem(system);
		DamageStatus rightDmg = rightGrid.getNbHitPointsSystem(system);
		int hitPoints = leftDmg.getHitPoints() + rightDmg.getHitPoints();
		int damagedPoints = leftDmg.getDamagedPoints() + rightDmg.getDamagedPoints();
		return new DamageStatus(hitPoints, damagedPoints, "");

	}

	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		ColossalDamageGrid source = (ColossalDamageGrid) damageGrid;
		if (source.getForceFieldGrid() != null) {
			forceFieldGrid.copyStatusFrom(source.getForceFieldGrid());
		}
		leftGrid.copyStatusFrom(source.getLeftGrid());
		rightGrid.copyStatusFrom(source.getRightGrid());
		notifyCommit();
	}

	public ModelDamageLine getForceFieldGrid() {
		return forceFieldGrid;
	}

	public void setForceFieldGrid(ModelDamageLine forceFieldGrid) {
		this.forceFieldGrid = forceFieldGrid;
	}

	@Override
	public List<DamageBox> getJustDamagedBoxes() {
		return justDamagedBoxes;
	}

	@Override
	public int applyFakeDamages(int column, int damageAmount) {
		return applyFakeDamages(column, damageAmount, -1);
	}
	
	@Override
	public boolean heal1point() {
		return false;
	}

	@Override
	public boolean heal1point(int colNumber) {
		if (colNumber >=0 && colNumber < 6) {
			return leftGrid.heal1point(colNumber);
		} else if (colNumber >=6 && colNumber <12 ) {
			return rightGrid.heal1point(colNumber-6);
		}
		return false;
	}	
	
	public boolean heal1pointForceField() {
		boolean healed = forceFieldGrid.heal1point();
		return healed;
	}

	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		StringTokenizer stk = new StringTokenizer(damageGridString, "/");
		
		if (forceFieldGrid != null) {
			String ffDamages = stk.nextToken();
			forceFieldGrid.fromStringWithDamageStatus(ffDamages);
		}
		String leftDamages = stk.nextToken();
		leftGrid.fromStringWithDamageStatus(leftDamages);
		String rightDamages = stk.nextToken();
		rightGrid.fromStringWithDamageStatus(rightDamages);
		
		return this;
	}

	@Override
	public String toStringWithDamageStatus() {
		
		StringBuffer sb = new StringBuffer(64);
		
		if (forceFieldGrid != null) {
			sb.append(forceFieldGrid.toStringWithDamageStatus());
			sb.append("/");
		}
		
		sb.append(leftGrid.toStringWithDamageStatus());
		sb.append("/");
		sb.append(rightGrid.toStringWithDamageStatus());
		
		return sb.toString();
	}	
}
