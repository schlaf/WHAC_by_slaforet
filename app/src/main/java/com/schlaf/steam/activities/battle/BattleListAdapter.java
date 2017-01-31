/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleListFragment.BattleListInterface;
import com.schlaf.steam.activities.damages.DamageStatus;
import com.schlaf.steam.activities.damages.ModelDamageLine;
import com.schlaf.steam.activities.damages.ViewDamageChangeObserver;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.data.MultiPVUnitGrid;
import com.schlaf.steam.data.WarbeastDamageSpiral;
import com.schlaf.steam.data.WarbeastDamageSpiral.AspectEnum;
import com.schlaf.steam.data.WarjackLikeDamageGrid;
import com.schlaf.steam.data.WarmachineDamageSystemsEnum;

/**
 * @author S0085289
 * 
 */
public class BattleListAdapter extends BaseAdapter {

	Activity parentActivity;

	/**
	 * liste des modéles sélectionnés <br>
	 * replicate from BattleSingleton.getSelectedEntries
	 * 
	 * @see SelectionModelSingleton
	 */
	List<BattleEntry> entries;
	
	/** observers that wait for grid modifications to change layout */
	List<ViewDamageChangeObserver> observers = new ArrayList<ViewDamageChangeObserver>();
	

	public BattleListAdapter(Activity parent, int playerNumber) {
		parentActivity = parent;
		entries = new ArrayList<BattleEntry>(BattleSingleton.getInstance()
				.getEntries(playerNumber).size());
		entries.addAll(BattleSingleton.getInstance().getNonSpecialistEntries(playerNumber));
		Collections.sort(entries);
	}

	public void addObserver(ViewDamageChangeObserver observer) {
		observers.add(observer);
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return entries.size();
	}

	@Override
	public Object getItem(int position) {
		return entries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private void handleDamageDialog(MultiPVModel model) {
		((BattleListInterface) parentActivity).showDamageDialog(model);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) parentActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//if (convertView == null) {
			convertView = inflater.inflate(R.layout.battle_row_group, null);
		//}
		BattleEntry entry = (BattleEntry) getItem(position);

		final TextView tvLabel = (TextView) convertView
				.findViewById(R.id.entry_label);
		tvLabel.setText(entry.getLabel());

		// SmallDamageLineView smallDamageView = (SmallDamageLineView)
		// convertView.findViewById(R.id.smallDamageLineView1);
		// UnitDamageLineView unitDamageView = (UnitDamageLineView)
		// convertView.findViewById(R.id.unitDamageLineView1);
		// JackBeastDamageLineView jackBeastDamageView =
		// (JackBeastDamageLineView)
		// convertView.findViewById(R.id.jackBeastDamageLineView1);
		//
		// smallDamageView.setFocusable(false);
		// unitDamageView.setFocusable(false);
		// jackBeastDamageView.setFocusable(false);

		ImageView section = (ImageView) convertView
				.findViewById(R.id.sectionColoredImage);
		
		
		ImageView spacer = (ImageView) convertView
				.findViewById(R.id.spacerImage);
		ImageView spacerSection = (ImageView) convertView
				.findViewById(R.id.spacerImageSection);
		
		section.setImageResource(entry.getDrawableResource());
		
		if (!entry.isAttached()) {
			spacer.setVisibility(View.GONE);
			spacerSection.setVisibility(View.GONE);
		} else {
			spacer.setVisibility(View.VISIBLE);
			spacerSection.setVisibility(View.VISIBLE);
		}

		final LinearLayout systemsLayout = (LinearLayout) convertView.findViewById(R.id.damage_detail_layout);
		systemsLayout.removeAllViews(); // (1, systemsLayout.getChildCount() - 1);

		final TextView damageTV = (TextView) convertView
				.findViewById(R.id.damageTextView);
		
		if (entry.isAttached()) {
			tvLabel.setTextColor(parentActivity.getResources().getColorStateList(R.color.secondary_text_default_material_light));
		} else {
			tvLabel.setTextColor(parentActivity.getResources().getColorStateList(R.color.primary_text_default_material_light));
		}
		
		TextView defArmTV = (TextView) convertView
				.findViewById(R.id.def_arm_label);
		defArmTV.setVisibility(View.VISIBLE);

		systemsLayout.setVisibility(View.VISIBLE);

		RelativeLayout rowLayout = (RelativeLayout) convertView.findViewById(R.id.layout_row_vertical);
		// rowLayout.removeViews(2, rowLayout.getChildCount() - 2);

		
		if (entry instanceof SingleDamageLineEntry) {
			
			ModelDamageLine line = ((SingleDamageLineEntry) entry)
					.getDamageGrid();
			if (line.getDamageStatus().getRemainingPoints() > 0) {
				damageTV.setText(Html.fromHtml(line.getDamageStatus().toHTMLString()));
			} else {
				
				// texte du nom barré!
				tvLabel.setPaintFlags(tvLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				
				damageTV.setText(" "); //$NON-NLS-1$
				damageTV.setBackgroundResource(R.drawable.undead_icon);
			}
			
			defArmTV.setText(Html.fromHtml(((SingleDamageLineEntry) entry).getModel().getDefArmLabel()));
			
			systemsLayout.removeAllViews(); 
			// systemsLayout.removeViews(1, systemsLayout.getChildCount() - 1);
			
			damageTV.setTag((SingleDamageLineEntry) entry);
			
			damageTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleDamageDialog((SingleDamageLineEntry) v.getTag());
				}
			});
			
			line.registerObserver(new ViewDamageChangeObserver(this) {
				@Override
				public void onChangeDamageStatus(DamageGrid grid) {
					// do nothing until committed
				}

				@Override
				public void onApplyCommitOrCancel(DamageGrid grid) {
					damageTV.setText(Html.fromHtml(grid.getDamageStatus().toHTMLString()));
					
					if (grid.getDamageStatus().getRemainingPoints() == 0) {
						// texte du nom barré!
						tvLabel.setPaintFlags(tvLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						damageTV.setText(" "); //$NON-NLS-1$
						damageTV.setBackgroundResource(R.drawable.undead_icon);
					} else {
						tvLabel.setPaintFlags(tvLabel.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
						damageTV.setBackgroundResource(R.drawable.empty);
					}
				}
			});
			
		} else if (entry instanceof MultiPVUnit) {
			damageTV.setText(""); //$NON-NLS-1$
			defArmTV.setVisibility(View.GONE);
			systemsLayout.setVisibility(View.GONE);
			
			MultiPVUnit unit = (MultiPVUnit) entry;
			
			MultiPVUnitGrid grid = (MultiPVUnitGrid) unit.getDamageGrid();
			
			grid.setDamageLines(new ArrayList<ModelDamageLine>());
			
			if (unit.isLeaderAndGrunts()) {
				// juste one line for every body
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(10, 2, 2, 2);

				View row = inflater.inflate(R.layout.row_unit_leader_and_grunts, null);

				LinearLayout unitDamagesLayout = (LinearLayout) row.findViewById(R.id.linearLayoutUnitDamages);
				// preserve image space + def/arm label
				// unitDamagesLayout.removeViews(2, unitDamagesLayout.getChildCount() - 2);
				
				TextView defArmUnitTV = (TextView) row.findViewById(R.id.tvDefArm);
				
				boolean first = true;
				int modelNumber = 0;
				for (SingleDamageLineEntry model : unit.getModels()) {
					if (first) {
						defArmUnitTV.setText(Html.fromHtml(model.getModel().getDefArmLabel()));
						first = false;
					}
					
					ModelDamageLine damageLine = model.getDamageGrid();
					grid.getDamageLines().add(damageLine);
					
					final TextView boxMember = (TextView) inflater.inflate(R.layout.boxunitmemberstatus, null);
					if ( damageLine.getDamageStatus().getHitPoints() > 1) {
						// display only if multi-PV
						boxMember.setText(Html.fromHtml(damageLine.getDamageStatus().toHTMLString()));
						boxMember.setTag(unit);
						boxMember.setTag(R.id.modelgrid, Integer.valueOf(modelNumber));
						unitDamagesLayout.addView(boxMember, params);	
						
						boxMember.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Integer modelNum = (Integer) v.getTag(R.id.modelgrid);
								BattleSingleton.getInstance().setModelNumber(modelNum.intValue());
								handleDamageDialog( (MultiPVUnit)  v.getTag());
							}
						});
						
						damageLine.registerObserver(new ViewDamageChangeObserver(this) {
							@Override
							public void onChangeDamageStatus(DamageGrid grid) {
								// do nothing until committed
							}

							@Override
							public void onApplyCommitOrCancel(DamageGrid grid) {
								boxMember.setText(Html.fromHtml(grid
										.getDamageStatus().toHTMLString()));
							}
						});
					}
					modelNumber ++;
				}
				RelativeLayout.LayoutParams paramsForLine = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				paramsForLine.addRule(RelativeLayout.BELOW, R.id.entry_label);
				rowLayout.addView(row, paramsForLine);
			} else {
				int modelNumber = 0;
				
				int addBelowValue = R.id.entry_label;
				
				// one line for each different model with more than one PV
				for (SingleDamageLineEntry model : unit.getModels()) {
					ModelDamageLine damageLine = model.getDamageGrid();
					grid.getDamageLines().add(damageLine);
					
					View row = inflater.inflate(R.layout.row_unit_member, null);

					final TextView gruntTitle = (TextView) row.findViewById(R.id.tvTitle);
					final TextView gruntDefArm = (TextView) row.findViewById(R.id.tvDefArm);
					final TextView gruntHits = (TextView) row.findViewById(R.id.tvHitpointsStatus);

					// indent only if first level
					if (entry.isAttached()) {
						row.findViewById(R.id.spacerImage).setVisibility(View.GONE);
						gruntTitle.setTextColor(parentActivity.getResources().getColor(R.color.secondary_text_default_material_light));
					} else {
						row.findViewById(R.id.spacerImage).setVisibility(
								View.VISIBLE);
						gruntTitle.setTextColor(parentActivity.getResources().getColor(R.color.primary_text_default_material_light));
					}


					gruntDefArm.setText(Html.fromHtml(model.getModel().getDefArmLabel()));
					gruntTitle.setText(model.getLabel());

					if (damageLine.getTotalHits() > 1) {
						
						if (damageLine.getDamageStatus().getRemainingPoints() == 0) {
							// texte du nom barré!
							gruntTitle.setPaintFlags(tvLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						}
						
						
						gruntHits.setTag(unit);
						gruntHits.setTag(R.id.modelgrid, Integer.valueOf(modelNumber));
						gruntHits.setText(Html.fromHtml(damageLine
								.getDamageStatus().toHTMLString()));
						gruntHits.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Integer modelNum = (Integer) v.getTag(R.id.modelgrid);
								BattleSingleton.getInstance().setModelNumber(modelNum.intValue());

								handleDamageDialog( (MultiPVUnit)  v.getTag());
							}
						});

						damageLine.registerObserver(new ViewDamageChangeObserver(this) {
							@Override
							public void onChangeDamageStatus(DamageGrid grid) {
								// do nothing until committed
							}

							@Override
							public void onApplyCommitOrCancel(DamageGrid grid) {
								gruntHits.setText(Html.fromHtml(grid
										.getDamageStatus().toHTMLString()));
								
								if (grid.getDamageStatus().getRemainingPoints() == 0) {
									// texte du nom barré!
									gruntTitle.setPaintFlags(gruntTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
								} else {
									gruntTitle.setPaintFlags(gruntTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
								}
							}
							
						});
					} else {
						gruntHits.setVisibility(View.GONE);
					}
					
					modelNumber ++;

					RelativeLayout.LayoutParams paramsForLine = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					paramsForLine.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
					paramsForLine.addRule(RelativeLayout.BELOW, addBelowValue );
					int lineId = addBelowValue+1;
					row.setId(lineId);
					rowLayout.addView(row, paramsForLine);
					
					addBelowValue = lineId; // to add next line under this one
				}
			}
			
		} else if (entry instanceof JackEntry) {
			JackEntry jack = (JackEntry) entry;
			WarjackLikeDamageGrid grid = jack.getDamageGrid();
			
			damageTV.setText(Html.fromHtml(jack.getDamageGrid().getDamageStatus().toHTMLString()));
			defArmTV.setText(Html.fromHtml(((JackEntry) entry).getModel().getDefArmLabel()));
			
			generateWarjackDamageLine(grid, systemsLayout, inflater);
						
			damageTV.setTag(jack);
			damageTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleDamageDialog((JackEntry) v.getTag());
				}
			});
			
			systemsLayout.setTag(jack);
			systemsLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleDamageDialog((JackEntry) v.getTag());
				}
			});
//			defArmTV.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// do nothing...
//				}
//			});

			
			grid.registerObserver(new ViewDamageChangeObserver(this) {
				@Override
				public void onChangeDamageStatus(DamageGrid grid) {
					// do nothing until committed
				}

				@Override
				public void onApplyCommitOrCancel(DamageGrid grid) {
					damageTV.setText(Html.fromHtml(grid.getDamageStatus().toHTMLString()));
					systemsLayout.removeAllViews(); 
					// systemsLayout.removeViews(1, systemsLayout.getChildCount() - 1);
					generateWarjackDamageLine((WarjackLikeDamageGrid) grid, systemsLayout, inflater);
				}
			});
			
		} else if (entry instanceof BeastEntry) {
			BeastEntry beast = (BeastEntry) entry;
			WarbeastDamageSpiral spiral = beast.getDamageGrid();
			
			damageTV.setText(Html.fromHtml(beast.getDamageGrid().getDamageStatus().toHTMLString()));
			defArmTV.setText(Html.fromHtml(((BeastEntry) entry).getModel().getDefArmLabel()));
			
			generateWarbeastDamageLine(spiral, systemsLayout, inflater);
						
			damageTV.setTag(beast);
			damageTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleDamageDialog((BeastEntry) v.getTag());
				}
			});
			
			systemsLayout.setTag(beast);
			systemsLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleDamageDialog((BeastEntry) v.getTag());
				}
			});
//			defArmTV.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// do nothing...
//				}
//			});

			
			spiral.registerObserver(new ViewDamageChangeObserver(this) {
				@Override
				public void onChangeDamageStatus(DamageGrid grid) {
					// do nothing until committed
				}

				@Override
				public void onApplyCommitOrCancel(DamageGrid grid) {
					damageTV.setText(Html.fromHtml(grid.getDamageStatus().toHTMLString()));
					systemsLayout.removeAllViews();
					// systemsLayout.removeViews(1, systemsLayout.getChildCount() - 1);
					generateWarbeastDamageLine((WarbeastDamageSpiral) grid, systemsLayout, inflater);
				}
			});
			
		} else {
			// solo with no hitpoints...
			MiniModelDescription description = new MiniModelDescription(entry.getReference().getModels().get(0));
			defArmTV.setText(Html.fromHtml(description.getDefArmLabel()));
			damageTV.setText(""); //$NON-NLS-1$
		}


		convertView.setFocusable(false);
		return convertView;
	}
	
	private void generateWarjackDamageLine(WarjackLikeDamageGrid grid, LinearLayout systemsLayout, LayoutInflater inflater ) {
		TextView boxOK = null;
		TextView boxDown = null;
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(2, 2, 2, 2);
		
		for (WarmachineDamageSystemsEnum system : grid.getSystems()) {
			DamageStatus status = grid.getNbHitPointsSystem(system);
			if (status.getDamagedPoints() < status.getHitPoints()) {
				boxOK = (TextView) inflater.inflate(R.layout.boxsystemok, null);
				boxOK.setText(system.getCode());
				systemsLayout.addView(boxOK, params);	
			} else {
				boxDown = (TextView) inflater.inflate(R.layout.boxsystemdown, null);
				boxDown.setText(system.getCode());
				systemsLayout.addView(boxDown, params);	
			}

		}
	}

	
	private void generateWarbeastDamageLine(WarbeastDamageSpiral spiral, LinearLayout systemsLayout, LayoutInflater inflater ) {
		TextView boxOK = null;
		TextView boxDown = null;
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(2, 2, 2, 2);
		
		for (AspectEnum aspect : spiral.getBranches().keySet()) {
			DamageStatus status = spiral.getNbHitPointsAspect(aspect);
			if (status.getDamagedPoints() < status.getHitPoints()) {
				boxOK = (TextView) inflater.inflate(R.layout.boxsystemok, null);
				boxOK.setText(aspect.name().substring(0, 2));
				systemsLayout.addView(boxOK, params);	
			} else {
				boxDown = (TextView) inflater.inflate(R.layout.boxsystemdown, null);
				boxDown.setText(aspect.name().substring(0, 2));
				systemsLayout.addView(boxDown, params);	
			}

		}
	}

	public void cleanDamageObservers(int playerNumber) {
		for (ViewDamageChangeObserver observer : observers) {
			List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(playerNumber);
			for (BattleEntry entry : entries) {
				if (entry.hasDamageGrid()) {
					((MultiPVModel) entry).getDamageGrid().removeObserver(observer);
				}
			}

		}
		
	}
	
}
