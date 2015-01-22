/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battleplanner.ActionEnum;
import com.schlaf.steam.activities.battleplanner.BattlePlanningEntry;
import com.schlaf.steam.activities.battleplanner.PlanningSpell;
import com.schlaf.steam.data.ModelTypeEnum;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * @author S0085289
 *
 */
public class BattlePlannerRowAdapter extends ArrayAdapter<BattlePlanningEntry> {
	
	  private final Context context;

	  public BattlePlannerRowAdapter(Context context,  List<BattlePlanningEntry> entries) {
	    super(context, R.layout.battle_planning_row, entries);
	    this.context = context;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    if (convertView == null) {
		    convertView = inflater.inflate(R.layout.battle_planning_row, parent, false);
	    }
	    
	    BattlePlanningEntry entry = getItem(position);
	    
	    
	    TextView modelTv = (TextView) convertView.findViewById(R.id.modelLabel);
	    modelTv.setText(entry.getLabel());
	    
	    // movement group
	    final ImageButton moveHit = (ImageButton) convertView.findViewById(R.id.button_move_hit);
	    final ImageButton moveShoot = (ImageButton) convertView.findViewById(R.id.button_move_shoot);
	    final ImageButton moveRun = (ImageButton) convertView.findViewById(R.id.button_move_run);
	    final ImageButton moveCharge = (ImageButton) convertView.findViewById(R.id.button_charge);
	    final ImageButton moveSlam = (ImageButton) convertView.findViewById(R.id.button_slam);
	    final ImageButton moveTrample = (ImageButton) convertView.findViewById(R.id.button_trample);
	    final ImageButton specialAction = (ImageButton) convertView.findViewById(R.id.button_special);
	    
	    if (entry.isPowerAttackAvailable()) {
		    moveSlam.setVisibility(View.VISIBLE);
		    moveTrample.setVisibility(View.VISIBLE);
	    } else {
		    moveSlam.setVisibility(View.GONE);
		    moveTrample.setVisibility(View.GONE);
	    }
	    
	    moveHit.setTag(entry);
	    moveShoot.setTag(entry);
	    moveRun.setTag(entry);
	    moveCharge.setTag(entry);
	    moveSlam.setTag(entry);
	    moveTrample.setTag(entry);
	    
	    OnClickListener groupMoveListener = new OnClickListener() {
			@Override
			public void onClick(View buttonView) {
				boolean unSelectedOthers = false;
				if (buttonView.isSelected()) {
					buttonView.setSelected(false);
					buttonView.setBackgroundResource(R.drawable.unselected_action_background);
					((BattlePlanningEntry) buttonView.getTag()).setActionMove(null);
				} else {
					unSelectedOthers = true;
				}
				if (unSelectedOthers) {
					moveHit.setSelected(false);
					moveHit.setBackgroundResource(R.drawable.unselected_action_background);
					moveShoot.setSelected(false);
					moveShoot.setBackgroundResource(R.drawable.unselected_action_background);
					moveRun.setSelected(false);
					moveRun.setBackgroundResource(R.drawable.unselected_action_background);
					moveCharge.setSelected(false);
					moveCharge.setBackgroundResource(R.drawable.unselected_action_background);
					moveSlam.setSelected(false);
					moveSlam.setBackgroundResource(R.drawable.unselected_action_background);
					moveTrample.setSelected(false);
					moveTrample.setBackgroundResource(R.drawable.unselected_action_background);
					
					if (buttonView == moveHit) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.MOVE_AND_HIT);
					}
					if (buttonView == moveShoot) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.MOVE_AND_SHOOT);
					}
					if (buttonView == moveRun) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.RUN);
					}
					if (buttonView == moveCharge) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.CHARGE);
					}
					if (buttonView == moveSlam) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.SLAM);
					}
					if (buttonView == moveTrample) {
						((BattlePlanningEntry) buttonView.getTag()).setActionMove(ActionEnum.TRAMPLE);
					}
					
					buttonView.setSelected(true);
					buttonView.setBackgroundResource(R.drawable.selected_action_background);

				}
				
				
			}
		};
	    
		moveHit.setOnClickListener(groupMoveListener);
		moveShoot.setOnClickListener(groupMoveListener);
		moveRun.setOnClickListener(groupMoveListener);
		moveCharge.setOnClickListener(groupMoveListener);
		moveSlam.setOnClickListener(groupMoveListener);
		moveTrample.setOnClickListener(groupMoveListener);
	    
		unselectButton(moveHit);
		unselectButton(moveShoot);
		unselectButton(moveRun);
		unselectButton(moveCharge);
		unselectButton(moveSlam);
		unselectButton(moveTrample);
		
		if (entry.getActionMove() != null) {
			switch (entry.getActionMove()) {
			case MOVE_AND_HIT:
				selectButton(moveHit);
				break;
			case MOVE_AND_SHOOT:
				selectButton(moveShoot);
				break;
			case RUN:
				selectButton(moveRun);
				break;
			case CHARGE:
				selectButton(moveCharge);
				break;
			case SLAM:
				selectButton(moveSlam);
				break;
			case TRAMPLE:
				selectButton(moveTrample);
				break;
			}
		}
		
	    if (entry.isFeatOrMiniFeatAvailable()) {
	    	ImageButton featButton = (ImageButton) convertView.findViewById(R.id.button_feat);
	    	featButton.setTag(entry);

	    	featButton.setVisibility(View.VISIBLE);
	    	if (entry.isDoFeat()) {
	    		selectButton(featButton); 
	    	} else {
	    		unselectButton(featButton);
	    	}
	    	featButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View buttonView) {
					((BattlePlanningEntry) buttonView.getTag()).flipFeat();
					if (((BattlePlanningEntry) buttonView.getTag()).isDoFeat()) {
						selectButton((ImageButton)buttonView);
					} else {
						unselectButton((ImageButton)buttonView);
					}
				}
			});
	    } else {
	    	convertView.findViewById(R.id.button_feat).setVisibility(View.GONE);
	    }
	    
	    // special actions
	    specialAction.setTag(entry);
	    specialAction.setTag(R.id.battle_plan_row, convertView);
	    if (entry.isDoSpecial()) {
    		selectButton(specialAction); 
    		handleSpecialVisibility(true, convertView, entry);
    	} else {
    		unselectButton(specialAction);
    		handleSpecialVisibility(false, convertView, entry);
    	}
	    specialAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View buttonView) {
				((BattlePlanningEntry) buttonView.getTag()).flipSpecial();
				View convertView = (View) buttonView.getTag(R.id.battle_plan_row);

				if (((BattlePlanningEntry) buttonView.getTag()).isDoSpecial()) {
					selectButton((ImageButton)buttonView);
					handleSpecialVisibility(true, convertView, ((BattlePlanningEntry) buttonView.getTag()));
				} else {
					unselectButton((ImageButton)buttonView);
					handleSpecialVisibility(false, convertView, ((BattlePlanningEntry) buttonView.getTag()));
				}
			}
		});
	    
	    if (entry.isSpellsAvailable()) {
	    	ImageButton spellButton = (ImageButton) convertView.findViewById(R.id.button_spell);
	    	spellButton.setTag(entry);

	    	spellButton.setVisibility(View.VISIBLE);
	    	if (entry.isDoSpell()) {
	    		selectButton(spellButton); 
	    		handleSpellVisibility(true, convertView, entry);
	    	} else {
	    		unselectButton(spellButton);
	    		handleSpellVisibility(false, convertView, entry);
	    	}
	    	spellButton.setTag(R.id.battle_plan_row, convertView);
	    	spellButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View buttonView) {
					((BattlePlanningEntry) buttonView.getTag()).flipSpell();
					View convertView = (View) buttonView.getTag(R.id.battle_plan_row);

					if (((BattlePlanningEntry) buttonView.getTag()).isDoSpell()) {
						selectButton((ImageButton)buttonView);
						handleSpellVisibility(true, convertView, ((BattlePlanningEntry) buttonView.getTag()));
					} else {
						unselectButton((ImageButton)buttonView);
						handleSpellVisibility(false, convertView, ((BattlePlanningEntry) buttonView.getTag()));
					}
				}
			});
	    	
	    	
	    	// associate checkbox to spell
	    	CheckBox cb01 = ((CheckBox)convertView.findViewById(R.id.checkBox01));
	    	CheckBox cb02 = ((CheckBox)convertView.findViewById(R.id.checkBox02));
	    	CheckBox cb03 = ((CheckBox)convertView.findViewById(R.id.checkBox03));
	    	CheckBox cb04 = ((CheckBox)convertView.findViewById(R.id.checkBox04));
	    	CheckBox cb05 = ((CheckBox)convertView.findViewById(R.id.checkBox05));
	    	CheckBox cb06 = ((CheckBox)convertView.findViewById(R.id.checkBox06));

	    	OnCheckedChangeListener checkedListener = new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					((PlanningSpell) buttonView.getTag()).doLaunch = isChecked;
				}
			};

	    	
	    	switch (entry.getSpells().size()) {
			case 6:
				cb06.setTag(entry.getSpells().get(5));
				cb06.setOnCheckedChangeListener(checkedListener);
			case 5:
				cb05.setTag(entry.getSpells().get(4));
				cb05.setOnCheckedChangeListener(checkedListener);
			case 4:
				cb04.setTag(entry.getSpells().get(3));
				cb04.setOnCheckedChangeListener(checkedListener);
			case 3:
				cb03.setTag(entry.getSpells().get(2));
				cb03.setOnCheckedChangeListener(checkedListener);
			case 2:
				cb02.setTag(entry.getSpells().get(1));
				cb02.setOnCheckedChangeListener(checkedListener);
			case 1:
				cb01.setTag(entry.getSpells().get(0));
				cb01.setOnCheckedChangeListener(checkedListener);
	    	}
	    	
	    	
	    	
	    	
	    } else {
	    	convertView.findViewById(R.id.button_spell).setVisibility(View.GONE);
	    	convertView.findViewById(R.id.spellsLine1).setVisibility(View.GONE);
	    	convertView.findViewById(R.id.spellsLine2).setVisibility(View.GONE);
	    	convertView.findViewById(R.id.spellsLine3).setVisibility(View.GONE);
	    }
	    
	    return convertView;
	  }

	private void handleSpecialVisibility(boolean specialVisible,
			View convertView, BattlePlanningEntry entry) {
		if (specialVisible) {
			convertView.findViewById(R.id.specialActionLine).setVisibility(
					View.VISIBLE);
			Spinner actions = (Spinner) convertView
					.findViewById(R.id.spinnerSpecialAction); 
			ArrayAdapter<String> adapterEntryType = new ArrayAdapter<String>(
					getContext(), android.R.layout.simple_spinner_item,
					android.R.id.text1, entry.getSpecialActions());
			adapterEntryType
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			actions.setAdapter(adapterEntryType);
			
			// restore selected value from model
			if (entry.getSpecialActionChosen() != null) {
				int indexFound = entry.getSpecialActions().indexOf(entry.getSpecialActionChosen());
				if ( indexFound != -1) {
					actions.setSelection(indexFound); 
				} else {
					actions.setSelection(NO_SELECTION);
				}
			} else {
				actions.setSelection(NO_SELECTION);
			}
			
			
			actions.setTag(entry);
			actions.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					String actionChosen = (String) parent.getItemAtPosition(position);
					
					((BattlePlanningEntry)parent.getTag()).setSpecialActionChosen(actionChosen);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					((BattlePlanningEntry)parent.getTag()).setSpecialActionChosen(null);
				}
			});
			
			
		} else {
			convertView.findViewById(R.id.specialActionLine).setVisibility(
					View.GONE);
		}
	}
	  
	private void handleSpellVisibility(boolean spellsVisible, View convertView,
			BattlePlanningEntry entry) {
		if (spellsVisible) {
			switch (entry.getSpells().size()) {
			case 6:
				((CheckBox)convertView.findViewById(R.id.checkBox06)).setText(entry.getSpells().get(5).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox06)).setChecked(entry.getSpells().get(5).doLaunch);
			case 5:
				convertView.findViewById(R.id.spellsLine3).setVisibility(View.VISIBLE);
				((CheckBox)convertView.findViewById(R.id.checkBox05)).setText(entry.getSpells().get(4).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox05)).setChecked(entry.getSpells().get(4).doLaunch);
			case 4:
				((CheckBox)convertView.findViewById(R.id.checkBox04)).setText(entry.getSpells().get(3).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox04)).setChecked(entry.getSpells().get(3).doLaunch);
			case 3:
				convertView.findViewById(R.id.spellsLine2).setVisibility(View.VISIBLE);
				((CheckBox)convertView.findViewById(R.id.checkBox03)).setText(entry.getSpells().get(2).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox03)).setChecked(entry.getSpells().get(2).doLaunch);
			case 2:
				((CheckBox)convertView.findViewById(R.id.checkBox02)).setText(entry.getSpells().get(1).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox02)).setChecked(entry.getSpells().get(1).doLaunch);
			case 1:
				convertView.findViewById(R.id.spellsLine1).setVisibility(View.VISIBLE);
				((CheckBox)convertView.findViewById(R.id.checkBox01)).setText(entry.getSpells().get(0).getDescription());
				((CheckBox)convertView.findViewById(R.id.checkBox01)).setChecked(entry.getSpells().get(0).doLaunch);
			default:
				break;
			}
			
			switch (entry.getSpells().size()) {
			case 5:
				((CheckBox)convertView.findViewById(R.id.checkBox06)).setVisibility(View.GONE);
				break;
			case 4:
				convertView.findViewById(R.id.spellsLine3).setVisibility(View.GONE);
				break;
			case 3:
				((CheckBox)convertView.findViewById(R.id.checkBox04)).setVisibility(View.GONE);
				convertView.findViewById(R.id.spellsLine3).setVisibility(View.GONE);
				break;
			case 2:
				convertView.findViewById(R.id.spellsLine2).setVisibility(View.GONE);
				break;
			case 1:
				((CheckBox)convertView.findViewById(R.id.checkBox02)).setVisibility(View.GONE);
				convertView.findViewById(R.id.spellsLine3).setVisibility(View.GONE);
				convertView.findViewById(R.id.spellsLine2).setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} else {
			convertView.findViewById(R.id.spellsLine3).setVisibility(View.GONE);
			convertView.findViewById(R.id.spellsLine2).setVisibility(View.GONE);
			convertView.findViewById(R.id.spellsLine1).setVisibility(View.GONE);
		}
	}

	private void selectButton(ImageButton buttonView) {
		buttonView.setBackgroundResource(R.drawable.selected_action_background);
	}
	
	private void unselectButton(ImageButton buttonView) {
		buttonView.setBackgroundResource(R.drawable.unselected_action_background);
	}
	  
	
}
