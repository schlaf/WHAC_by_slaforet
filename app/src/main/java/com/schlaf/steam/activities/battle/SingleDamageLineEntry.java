package com.schlaf.steam.activities.battle;

import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.SingleModel;

/**
 * used for single-line-damage models : warriors, Battle-engine, objectives, ...
 * @author S0085289
 *
 */
public class SingleDamageLineEntry extends MultiPVModel {

	private ModelDamageLine damageGrid;
	
	/** when in unit, must be ordered */
	private int order;
	
	
	
	/** serial */
	private static final long serialVersionUID = 8535111131294165725L;

	public SingleDamageLineEntry(SingleModel entry, ArmyElement reference, int entryCounter) {
		super(entry, reference, entry.getName(), entryCounter);
		label = entry.getName(); 
		damageGrid = new ModelDamageLine(entry);		
	}
	
	/**
	 * create an entry from a reference which contains only one model
	 * @param reference
	 */
	public SingleDamageLineEntry(ArmyElement reference, int entryCounter) {
		this(reference.getModels().get(0), reference, entryCounter);
		label = reference.getFullName();
	}

	
	/**
	 * to use when "leader + grunts" to override basic label
	 * @param entry
	 * @param reference
	 * @param isLeader
	 */
	public SingleDamageLineEntry(SingleModel entry, int order, ArmyElement reference, boolean isLeader, int entryCounter) {
		super(entry, reference, (reference.getName() + (isLeader?" Leader":("Grunt # " + order))), entryCounter);
		label = isLeader?"Leader":("Grunt # " + order);
		MiniModelDescription description = new MiniModelDescription(entry);
		description.setName(label);
		damageGrid = new ModelDamageLine(description, entry.getHitpoints().getTotalHits());		
	}

	public SingleDamageLineEntry(SingleModel entry, int order, ArmyElement reference, int entryCounter) {
		this(entry, reference, entryCounter);
		this.order = order;
		label = model.getName() + " #" + this.order;
	}
	
	public SingleDamageLineEntry(MiniModelDescription desc, int order, ArmyElement reference, int entryCounter, ModelDamageLine damageGrid) {
		super(desc, reference, desc.getName(), entryCounter);
		this.order = order;
		this.damageGrid = damageGrid;
	}

	@Override
	public ModelDamageLine getDamageGrid() {
		return damageGrid;
	}


}
