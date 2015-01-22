/**
 * 
 */
package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.util.Log;

import com.schlaf.steam.activities.battle.MiniModelDescription;
import com.schlaf.steam.activities.damages.DamageStatus;

/**
 * @author S0085289
 *
 */
public class WarjackDamageGrid extends WarjackLikeDamageGrid {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6769715584560126726L;

	private static final String TAG = "WarjackDamageGrid";
	
	private DamageColumn[] columns = new DamageColumn[6];
	
	/**
	 * keep tracks of boxes with damages, usefull for damage deletion.
	 */
	private List<DamageBox> justDamagedBoxes = new ArrayList<DamageBox>();

	
	/**
	 * total number of hit points
	 */
	private int hitPoints;
	
	private String damageGridString;
	
	/**
	 * number of damaged boxes
	 */
	private int damagedPoints;
	
	public WarjackDamageGrid(SingleModel jack) {
		this.model = new MiniModelDescription(jack);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new DamageColumn(this);
			columns[i].setId(i);
		}
	}
	
	@Override
	public DamageGrid fromString(String damageGridString) {
		// "x....x.............L..R.LLMCRRxMMCCx"
		this.damageGridString = damageGridString;
		
		char[] charArray = damageGridString.toCharArray();
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
	
	
	public static void main(String[] args) {
		WarjackDamageGrid grid = new WarjackDamageGrid(null);
		grid.fromString("x....x.............L..R.LLMCRRxMMCCx");
	
		System.out.println(grid.toString());
	}

	public DamageColumn[] getColumns() {
		return columns;
	}
	
	public DamageStatus getDamageStatus() {
		int nbHits = 0;
		int nbDamages = 0;
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
		return result;
	}
	
	public DamageStatus getNbHitPointsSystem(WarmachineDamageSystemsEnum system) {
		int nbHits = 0;
		int nbDamages = 0;
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
		
		notifyBoxChanges = false; // prevents events when changing the whole grid
		
		int dmgApplied = 0;

		if (damageAmount > 0) {
			for (DamageColumn column : columns) {
				if (column.getId() == colNumber) {
					dmgApplied = column.applyFakeDamages(damageAmount);
					if (dmgApplied == damageAmount) {
						// all damages applied, done
					}
				}
			}

			if (dmgApplied != damageAmount && isStillAlivePending()) { // si on n'arrive
																// plus é infliger,
																// on stoppe
				// report sur la colonne suivante
				if (colNumber == 6) {
					dmgApplied += applyFakeDamages(0, damageAmount - dmgApplied);
				} else {
					dmgApplied += applyFakeDamages(colNumber + 1, damageAmount - dmgApplied);
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
			
		}
		notifyBoxChanges = true; // now we can notify..
		notifyBoxChange();		
		return dmgApplied;

	}


	@Override
	public int getTotalHits() {
		return hitPoints;
	}

	@Override
	public void commitFakeDamages() {
		
		notifyBoxChanges = false; // prevents events when changing the whole grid
		
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamaged(box.isDamagedPending());
					box.setCurrentlyChangePending(false);
				}
			}
		}
		
		notifyBoxChanges = true; // restaure normal events
		justDamagedBoxes = new ArrayList<DamageBox>();
		notifyCommit();
	}

	@Override
	public void resetFakeDamages() {
		
		notifyBoxChanges = false; // prevents events when changing the whole grid
		for (DamageColumn column : columns) {
			for (DamageBox box : column.getBoxes()) {
				if (box.isCurrentlyChangePending()) {
					box.setDamagedPending(box.isDamaged());
					box.setCurrentlyChangePending(false);
				}
			}
		}
		notifyBoxChanges = true; // restaure normal events
		justDamagedBoxes = new ArrayList<DamageBox>();
		notifyCommit();
	}

	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		if (damageGrid instanceof WarjackDamageGrid) {
			int colnum = 0;
			for (DamageColumn column : columns) {
				Iterator<DamageBox> sourceIt = ((WarjackDamageGrid) damageGrid).getColumns()[colnum].getBoxes().iterator();
				for (DamageBox box : column.getBoxes()) {
					DamageBox source = sourceIt.next();
					box.copyFrom(source);
				}
				colnum ++;
			}
		}
		Log.d(TAG, "damages applied by copy");
		notifyCommit();
	}

	public String getDamageGridString() {
		return damageGridString;
	}

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

	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		DamageColumn currentColumn = columns[0];
		int boxCounter = 0;
		for (int i = 0; i < damageGridString.length(); i++) {
			char car = damageGridString.charAt(i);
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
				currentColumn = columns[Integer.parseInt(""+car)];
				break;
			case '.' : 
				boxCounter ++;
				break;
			case 'X' :
				currentColumn.getBoxes().get(boxCounter).setDamaged(true);
				boxCounter ++;
				break;
			case 'O' : 
				currentColumn.getBoxes().get(boxCounter).setDamaged(false);
				boxCounter ++;
				break;
			}
		}
		
		return this;
	}

	@Override
	public String toStringWithDamageStatus() {
		StringBuffer sb = new StringBuffer(64);
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


