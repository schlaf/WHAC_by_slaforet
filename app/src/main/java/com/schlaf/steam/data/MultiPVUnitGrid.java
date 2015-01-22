package com.schlaf.steam.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.schlaf.steam.activities.damages.DamageStatus;
import com.schlaf.steam.activities.damages.ModelDamageLine;

public class MultiPVUnitGrid extends DamageGrid {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1361881329712566530L;
	
	
	List<ModelDamageLine> damageLines = new ArrayList<ModelDamageLine>();
	
	@Override
	public DamageGrid fromString(String damageGridString) {
		return null;
	}

	@Override
	public int applyFakeDamages(int damageAmount) {
		throw new UnsupportedOperationException("can not apply damage without selecting model");
	}

	@Override
	public int applyFakeDamages(int column, int damageAmount) {
		int applied = damageLines.get(column).applyFakeDamages(damageAmount);
		notifyBoxChange();
		return applied;
		
	}

	@Override
	public void commitFakeDamages() {
		for (ModelDamageLine line : damageLines) {
			line.commitFakeDamages();
		}
		notifyBoxChange();
		notifyCommit();
	}

	@Override
	public void resetFakeDamages() {
		for (ModelDamageLine line : damageLines) {
			line.resetFakeDamages();
		}
		notifyBoxChange();
	}


	@Override
	public int getTotalHits() {
		return 0;
	}

	@Override
	public DamageStatus getDamageStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ModelDamageLine> getDamageLines() {
		return damageLines;
	}
	
	public List<ModelDamageLine> getMultiPvDamageLines() {
		List<ModelDamageLine> result = new ArrayList<ModelDamageLine>();
		for (ModelDamageLine line : damageLines) {
			if (line.getTotalHits() > 1) {
				result.add(line);
			}
		}
		return result;
	}

	public void setDamageLines(List<ModelDamageLine> damageLines) {
		this.damageLines = damageLines;
	}

	@Override
	public void copyStatusFrom(DamageGrid damageGrid) {
		if (damageGrid instanceof MultiPVUnitGrid) {
			
			List<ModelDamageLine> distantLines = ((MultiPVUnitGrid) damageGrid).getDamageLines();
			
			int i = 0;
			for (ModelDamageLine line : damageLines) {
				line.copyStatusFrom(distantLines.get(i));
				i++;
			}
			notifyCommit();

		}
		
	}

	@Override
	public boolean heal1point() {
		return false;
	}

	@Override
	public boolean heal1point(int column) {
		boolean healed = damageLines.get(column).heal1point();
		if (healed) {
			notifyBoxChange();
		}
		return healed;
	}

	@Override
	public DamageGrid fromStringWithDamageStatus(String damageGridString) {
		StringTokenizer stk = new StringTokenizer(damageGridString, "/");
		while (stk.hasMoreTokens()) {
			String modelString = stk.nextToken();
			ModelDamageLine line = new ModelDamageLine(0);
			line.fromStringWithDamageStatus(modelString);
			damageLines.add(line);
		}
		return this;
	}

	@Override
	public String toStringWithDamageStatus() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (ModelDamageLine model : damageLines) {
			if (!first) {
				sb.append("/");
			}
			first = false;
			sb.append(model.toStringWithDamageStatus());
		}
		return sb.toString();
	}

}
