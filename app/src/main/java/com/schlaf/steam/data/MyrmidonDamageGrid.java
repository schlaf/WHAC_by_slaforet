package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.activities.damages.DamageStatus;

public class MyrmidonDamageGrid extends WarjackLikeDamageGrid {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4110646027607735945L;
	private DamageColumn[] forceFieldColumns = new DamageColumn[2];
	private DamageColumn[] columns = new DamageColumn[6];
	
	/**
	 * keep tracks of boxes with damages, usefull for damage deletion.
	 */
	private List<DamageBox> justDamagedBoxes = new ArrayList<DamageBox>();
	
	/**
	 * total number of hit points
	 */
	private int hitPoints;
	
	/**
	 * number of damaged boxes
	 */
	private int damagedPoints;
	
	public MyrmidonDamageGrid(SingleModel jack) {
		this.model = new MiniModelDescription(jack);
		for (int i = 0; i < forceFieldColumns.length; i++) {
			forceFieldColumns[i] = new DamageColumn(this);
			forceFieldColumns[i].setId(i);
		}
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new DamageColumn(this);
			columns[i].setId(i);
		}
	}
	
	@Override
	public DamageGrid fromString(String damageGridString) {
		// "10-x....x.............L..R.LLMCRRxMMCCx"
		StringTokenizer stk = new StringTokenizer(damageGridString, "-");
		String forceFielBoxesCount = stk.nextToken();
		int forceFieldSize = Integer.parseInt(forceFielBoxesCount);
		String columnsString = stk.nextToken();
		
		for (int i = 0 ; i < forceFieldSize/2; i++) {
			forceFieldColumns[0].getBoxes().add(new DamageBox(WarmachineDamageSystemsEnum.FORCE_FIELD.getCode(), this));
			forceFieldColumns[1].getBoxes().add(new DamageBox(WarmachineDamageSystemsEnum.FORCE_FIELD.getCode(), this));
		}
		char[] charArray = columnsString.toCharArray();
		for (int i = 0 ; i < charArray.length; i++) {
			// compute column
			int columnId = i%6;
			columns[columnId].getBoxes().add(new DamageBox(String.valueOf(charArray[i]), this));
		}
		
		getDamageStatus();
		return this;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(64);
		for (DamageColumn column : forceFieldColumns) {
			for (DamageBox box : column.getBoxes()) {
				sb.append(box.getSystem().getCode());
			}
			sb.append("\n");
		}
		sb.append("______\n");
		
		for (int i = 0; i < 6; i++) {
			for (DamageColumn column : columns) {
				sb.append(column.getBoxes().get(i).getSystem().getCode());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * return true if at least one damageCircle not damaged in the branch
	 * @return
	 */
	public boolean isStillAlive() {
		DamageStatus status = getDamageStatus();
		if (status.getDamagedPoints() < status.getHitPoints()) {
			return true;
		}
		return false;
	}
	
	/**
	 * return true if at least one damageCircle not damaged && not damagedPending in the branch
	 * @return
	 */
	public boolean isStillAlivePending() {
		DamageStatus status = getDamagePendingStatus();
		if (status.getDamagedPoints() < status.getHitPoints()) {
			return true;
		}
		return false;
	}
	
	
	public DamageStatus getDamageStatus() {
		int nbHits = 0;
		int nbDamages = 0;
		
		for (DamageColumn column : forceFieldColumns) {
			for (DamageBox box : column.getBoxes()) {
				nbHits++;
				if (box.isDamaged()) {
					nbDamages++;
				}
			}
		}
		
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					nbHits ++;
					if (box.isDamaged()) {
						nbDamages ++;
					}
				}
				
			}
		}
		
		hitPoints = nbHits;
		damagedPoints = nbDamages;
		
		return new DamageStatus(hitPoints, damagedPoints, "");
	}

	public DamageStatus getDamagePendingStatus() {
		int nbHits = 0;
		int nbDamages = 0;
		for (DamageColumn column : forceFieldColumns) {
			for (DamageBox box : column.getBoxes()) {
				nbHits++;
				if (box.isDamaged() || ( box.isCurrentlyChangePending() && box.isDamagedPending())) {
					nbDamages++;
				}
			}
		}
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					nbHits ++;
					if (box.isDamaged() || ( box.isCurrentlyChangePending() && box.isDamagedPending())) {
						nbDamages ++;
					}
				}
				
			}
		}
		
		hitPoints = nbHits;
		damagedPoints = nbDamages;
		
		return new DamageStatus(hitPoints, damagedPoints, "");
	}

	@Override
	public DamageStatus getNbHitPointsSystem(WarmachineDamageSystemsEnum system) {

		int nbHits = 0;
		int nbDamages = 0;

		if (system.equals(WarmachineDamageSystemsEnum.FORCE_FIELD)) {
			for (DamageColumn column : forceFieldColumns) {
				for (DamageBox box : column.getBoxes()) {
					if (box.getSystem().equals(system)) {
						nbHits ++;
						if (box.isDamaged()) {
							nbDamages ++;
						}
					}
				}
			}
		} else {
			for (DamageColumn column : columns) {
				for (DamageBox box : column.getBoxes()) {
					
					if (box.getSystem().equals(system)) {
						nbHits ++;
						if (box.isDamaged()) {
							nbDamages ++;
						}
					}
					
				}
			}
		}
		
		
		hitPoints = nbHits;
		damagedPoints = nbDamages;
		
		return new DamageStatus(hitPoints, damagedPoints, system.getCode());
	}

	@Override
	public int applyFakeDamages(int damageAmount) {
		return applyFakeDamages(0, damageAmount);
	}

	@Override
	public int applyFakeDamages(int colNumber, int damageAmount) {
		int dmgApplied = 0;


		if (damageAmount > 0) {
			
			int damagesStillToApply = damageAmount; 
			

			// first apply to force field
			for (DamageColumn column : forceFieldColumns) {
				int dmgAppliedToFF = column.applyFakeDamages(damagesStillToApply);
				damagesStillToApply = damagesStillToApply - dmgAppliedToFF;
				dmgApplied += dmgAppliedToFF;
			}

			if (damagesStillToApply == 0) {
				return dmgApplied;
			}
			
			// force field full, apply to columns
			for (DamageColumn column : columns) {
				if (column.getId() == colNumber) {
					int dmgColumn = column.applyFakeDamages(damagesStillToApply);
					dmgApplied += dmgColumn;
					damagesStillToApply -= dmgColumn;
					if (dmgApplied == damageAmount) {
						// all damages applied, donne
						return dmgApplied;
					}
				}
			}

			if (dmgApplied != damageAmount && isStillAlivePending()) { // si on n'arrive
																// plus é infliger,
																// on stoppe
				// report sur la colonne suivante
				if (colNumber == 6) {
					int dmgColumn = applyFakeDamages(0, damagesStillToApply);
					dmgApplied += dmgColumn;
					damagesStillToApply -= dmgColumn;
				} else {
					int dmgColumn = applyFakeDamages(colNumber + 1, damagesStillToApply);
					dmgApplied += dmgColumn;
					damagesStillToApply -= dmgColumn;
				}
			}
		} else {
			
			for (int i = justDamagedBoxes.size() - 1; i >=0 && damageAmount < 0 ; i--) {
				justDamagedBoxes.get(i).setCurrentlyChangePending(false);
				justDamagedBoxes.get(i).setDamagedPending(false);
				damageAmount++;
				dmgApplied++;
				justDamagedBoxes.remove(i);
			}

			
//			// funky.. search for continuous damage from base column, then remove damages backward...
//			ArrayList<DamageBox> damagedBoxToCleanList = new ArrayList<DamageBox>();
//			
//			// start with column at current column number...
//			for (DamageColumn column : columns) {
//				if (column.getId() >= colNumber) {
//					for (DamageBox box : column.getBoxes()) {
//						if (box.isCurrentlyChangePending() && box.isDamagedPending()) {
//							damagedBoxToCleanList.add(box);
//						}
//					}
//				}
//			}
//			// and continue from first column...
//			for (DamageColumn column : columns) {
//				if (column.getId() < colNumber) {
//					for (DamageBox box : column.getBoxes()) {
//						if (box.isCurrentlyChangePending() && box.isDamagedPending()) {
//							damagedBoxToCleanList.add(box);
//						}
//					}
//				}
//			}
//			
//			for (int i = damagedBoxToCleanList.size() -1; i >=0; i--) {
//				DamageBox box = damagedBoxToCleanList.get(i);
//				if (dmgApplied < -damageAmount) {
//					box.setCurrentlyChangePending(false);
//					box.setDamagedPending(false);
//					dmgApplied ++;	
//				}
//			}
			
		}
		
		notifyBoxChange();		
		return dmgApplied;

	}


	@Override
	public int getTotalHits() {
		return hitPoints;
	}

	@Override
	public void commitFakeDamages() {
		for (DamageColumn column : forceFieldColumns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamaged(box.isDamagedPending());
					box.setCurrentlyChangePending(false);
				}
			}
		}
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamaged(box.isDamagedPending());
					box.setCurrentlyChangePending(false);
				}
			}
		}
		
		notifyCommit();

	}

	@Override
	public void resetFakeDamages() {
		for (DamageColumn column : forceFieldColumns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamagedPending(box.isDamaged());
					box.setCurrentlyChangePending(false);
				}
			}
		}
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamagedPending(box.isDamaged());
					box.setCurrentlyChangePending(false);
				}
			}
		}
	}

	/**
	 * return the systems in this grid
	 * @return
	 */
	public List<WarmachineDamageSystemsEnum> getSystems() {
		
		ArrayList<WarmachineDamageSystemsEnum> result = new ArrayList<WarmachineDamageSystemsEnum>();
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (! box.getSystem().equals(WarmachineDamageSystemsEnum.EMPTY)) {
					if (! result.contains(box.getSystem())) {
						result.add(box.getSystem());
					}
				}
			}
		}
		result.add(WarmachineDamageSystemsEnum.FORCE_FIELD); // cause this is a myrmidon!
		return result;
	}

	public DamageColumn[] getForceFieldColumns() {
		return forceFieldColumns;
	}

	public DamageColumn[] getColumns() {
		return columns;
	}

	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		MyrmidonDamageGrid source = (MyrmidonDamageGrid) damageGrid;

		int colnum = 0;
		for (DamageColumn column : columns) {
			Iterator<DamageBox> sourceIt = source.getColumns()[colnum].getBoxes().iterator();
			for (DamageBox box : column.getBoxes()) {
				DamageBox sourceBox = sourceIt.next();
				box.copyFrom(sourceBox);
			}
			colnum ++;
		}
		
		colnum = 0;
		for (DamageColumn column : forceFieldColumns) {
			Iterator<DamageBox> sourceIt = source.getForceFieldColumns()[colnum].getBoxes().iterator();
			for (DamageBox box : column.getBoxes()) {
				DamageBox sourceBox = sourceIt.next();
				box.copyFrom(sourceBox);
			}
			colnum ++;
		}
		
		notifyCommit();
	}

	@Override
	public List<DamageBox> getJustDamagedBoxes() {
		return justDamagedBoxes;
	}
	
	@Override
	public boolean heal1point() {
		return false;
	}

	@Override
	public boolean heal1point(int colNumber) {
		boolean healed = false;
		for (DamageColumn column : columns) {
			if (column.getId() == colNumber) {
				for (int i = column.getBoxes().size() - 1; i >= 0 && !healed; i--) {
					DamageBox box = column.getBoxes().get(i);
					if (box.isDamaged() && ! box.isCurrentlyChangePending()) {
						box.setCurrentlyChangePending(true);
						box.setDamagedPending(false);
						healed = true;
					}
				}
			}
		}
		return healed;
	}
	
	public boolean heal1pointForceField() {
		boolean healed = false;
		for (int ffC = forceFieldColumns.length - 1; ffC >=0; ffC--) {
			for (int i = forceFieldColumns[ffC].getBoxes().size() - 1; i >= 0 && !healed; i--) {
				DamageBox box = forceFieldColumns[ffC].getBoxes().get(i);
				if (box.isDamaged() && ! box.isCurrentlyChangePending()) {
					box.setCurrentlyChangePending(true);
					box.setDamagedPending(false);
					healed = true;
				}
			}
		}
		return healed;
	}

	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		DamageColumn currentColumnFF = forceFieldColumns[0];
		DamageColumn currentColumnHull = columns[0];
		int boxCounter = 0;
		
		StringTokenizer stk = new StringTokenizer(damageGridString, "/");
		String gridForceField = stk.nextToken();
		String gridHull = stk.nextToken();
		
		for (int i = 0; i < gridForceField.length(); i++) {
			char car = gridForceField.charAt(i);
			switch (car) {
			case ':' :
				boxCounter = 0; // new column
				break;
			case '0' :
			case '1' :
				currentColumnFF = forceFieldColumns[Integer.parseInt(""+car)];
				break;
			case '.' : 
				boxCounter ++;
				break;
			case 'X' :
				currentColumnFF.getBoxes().get(boxCounter).setDamaged(true);
				boxCounter ++;
				break;
			case 'O' : 
				currentColumnFF.getBoxes().get(boxCounter).setDamaged(false);
				boxCounter ++;
				break;
			}
		}
		
		for (int i = 0; i < gridHull.length(); i++) {
			char car = gridHull.charAt(i);
			switch (car) {
			case ':' :
				boxCounter = 0; // new column
				break;
			case '0' :
			case '1' :
			case '2' :
			case '3' :
			case '4' :
			case '5' :
				currentColumnHull = columns[Integer.parseInt(""+car)];
				break;
			case '.' : 
				boxCounter ++;
				break;
			case 'X' :
				currentColumnHull.getBoxes().get(boxCounter).setDamaged(true);
				boxCounter ++;
				break;
			case 'O' : 
				currentColumnHull.getBoxes().get(boxCounter).setDamaged(false);
				boxCounter ++;
				break;
			}
		}
		
		return this;	
	}

	@Override
	public String toStringWithDamageStatus() {
		StringBuffer sb = new StringBuffer(64);
		
		for (DamageColumn column : forceFieldColumns) {
			sb.append(column.getId()).append(":");
			for (DamageBox box : column.getBoxes()) {
				if (box.getSystem() == WarmachineDamageSystemsEnum.EMPTY) {
					sb.append(".");
				} else {
					sb.append(box.isDamaged()?"X":"O");	
				}
			}
		}
		
		sb.append("/");
		
		for (DamageColumn column : columns) {
			sb.append(column.getId()).append(":");
			for (DamageBox box : column.getBoxes()) {
				if (box.getSystem() == WarmachineDamageSystemsEnum.EMPTY) {
					sb.append(".");
				} else {
					sb.append(box.isDamaged()?"X":"O");	
				}
			}
		}
		return sb.toString();
	}	
}
