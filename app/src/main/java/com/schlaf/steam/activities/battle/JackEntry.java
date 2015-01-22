package com.schlaf.steam.activities.battle;

import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.Colossal;
import com.schlaf.steam.data.ColossalDamageGrid;
import com.schlaf.steam.data.Myrmidon;
import com.schlaf.steam.data.MyrmidonDamageGrid;
import com.schlaf.steam.data.SingleModel;
import com.schlaf.steam.data.Warjack;
import com.schlaf.steam.data.WarjackDamageGrid;
import com.schlaf.steam.data.WarjackLikeDamageGrid;

public class JackEntry extends MultiPVModel {

	/** serial */
	private static final long serialVersionUID = 6770974809897123347L;
	/** damage grid */
	private WarjackLikeDamageGrid damageGrid;
	
	
	public JackEntry(ArmyElement jack, int entryCounter) {
		this(jack, null, entryCounter);
		attached = false;
	}
	
	
	public JackEntry(ArmyElement jack, BattleEntry parent, int entryCounter) {
		super(jack.getModels().get(0), jack, jack.getFullName(), entryCounter);
		
		if (parent != null) {
			attached = true;
			this.parentId = parent.getUniqueId();	
		} else {
			attached = false;
		}
		
		if (jack instanceof Colossal) {
			SingleModel model = jack.getModels().get(0);
			ColossalDamageGrid colossalGrid = new ColossalDamageGrid(model);
			
			WarjackDamageGrid leftGrid = new WarjackDamageGrid(model);
			leftGrid.fromString(((Colossal) jack).getLeftGrid());
			WarjackDamageGrid rightGrid = new WarjackDamageGrid(model);
			rightGrid.fromString(((Colossal) jack).getRightGrid());
			
			if ( ((Colossal)jack).isMyrmidon()) {
				ModelDamageLine forceFieldGrid = new ModelDamageLine(new MiniModelDescription(model), ((Colossal)jack).getForceField());
				colossalGrid.setForceFieldGrid(forceFieldGrid);
			}
			
			colossalGrid.setLeftGrid(leftGrid);
			colossalGrid.setRightGrid(rightGrid);

			damageGrid = colossalGrid;
			
		} else if( jack instanceof Myrmidon) {
			damageGrid = new MyrmidonDamageGrid(jack.getModels().get(0));
			damageGrid.fromString( ((Myrmidon) jack).getGrid());
		} else if( jack instanceof Warjack) {
			damageGrid = new WarjackDamageGrid( ((Warjack) jack).getModels().get(0));
			damageGrid.fromString( ((Warjack) jack).getGrid());
		} else {
            // probably Karchev... do nothing, this is done in KarchevEntry constructor
        }
		
	}

	public WarjackLikeDamageGrid getDamageGrid() {
		return damageGrid;
	}

}
