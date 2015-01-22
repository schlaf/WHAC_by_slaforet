/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.collection.CollectionSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.adapters.ModelFlingGestureListener;
import com.schlaf.steam.data.Faction.GameSystem;
import com.schlaf.steam.data.FactionNamesEnum;

/**
 * adapteur pour présentation des entrées de liste pour sélection d'armée
 * 
 * @author S0085289
 * 
 */
public class ListSelectionAdapter extends BaseExpandableListAdapter {

	private static final int CHOOSE_UNIT_DIALOG = 503;	

	private Activity activity;
	/** liste des groupes, calculés é partir des modéles disponibles */
	List<SelectionGroup> groups;

	HashMap<SelectionGroup, List<SelectionEntry>> sortedModels = new HashMap<SelectionGroup, List<SelectionEntry>>();

	/** use strict FA rules */
	boolean strictFA = true;
	

	/**
	 * constructeur avec la liste initiale de modeles
	 * 
	 * @param activity
	 * @param models
	 * @param faction
	 */
	public ListSelectionAdapter(Activity activity) {
		if ( ! (activity instanceof ArmySelectionChangeListener)) {
			throw new UnsupportedOperationException("ListSelectionAdapter must receive a ArmySelectionChangeListener as parent activity");
		}
		this.activity = activity;
		createGroupsFromModels();
	}

	/**
	 * é partir de la liste des models, extrait les groupes disponibles
	 * (filtrage beast/jacks, units, ...)
	 */
	private void createGroupsFromModels() {

		groups = new ArrayList<SelectionGroup>();

		for (SelectionEntry model : SelectionModelSingleton.getInstance().getSelectionModels()) {

			SelectionGroup groupNormal = new SelectionGroup(model.getType(), SelectionModelSingleton.getInstance().getFaction());
			
			SelectionGroup groupMercOrMinions = null;
			if (SelectionModelSingleton.getInstance().getFaction().getGameSystem() == GameSystem.WARMACHINE) {
				groupMercOrMinions = new SelectionGroup(model.getType(), FactionNamesEnum.MERCENARIES);
			} else {
				groupMercOrMinions = new SelectionGroup(model.getType(), FactionNamesEnum.MINIONS);
			}
				
				

			if (model.isVisible()) {
				if (model.isMercenaryOrMinion()) {
					if ( ! groups.contains(groupMercOrMinions)) {
						groups.add(groupMercOrMinions);
						sortedModels.put(groupMercOrMinions,
								new ArrayList<SelectionEntry>());
					}
				} else {
					if ( ! groups.contains(groupNormal)) {
						groups.add(groupNormal);
						sortedModels.put(groupNormal,
								new ArrayList<SelectionEntry>());
					}
				}
			}


			if (SelectionModelSingleton.getInstance().getCurrentTiers() != null) {
				// if tiers, do not add models that are not allowed
				if (SelectionModelSingleton.getInstance().getCurrentTiers().isAllowedModel(SelectionModelSingleton.getInstance().getCurrentTiersLevel(), model.getId())) {
					if (model.isVisible()) {
						if (model.isMercenaryOrMinion()) {
							sortedModels.get(groupMercOrMinions).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
				} else {
					// not allowed, but maybe already selected?
					if (model.isSelected()) {
						model.setAlteredFA(0);
						if (model.isMercenaryOrMinion()) {
							sortedModels.get(groupMercOrMinions).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
				}
			} else if (SelectionModelSingleton.getInstance().getCurrentContract() != null) {
				// if contract, do not add models that are not allowed
				if (SelectionModelSingleton.getInstance().getCurrentContract().isAllowedModel(model.getId())) {
					if (model.isVisible()) {
						sortedModels.get(groupMercOrMinions).add(model);
					}
				} else {
					// not allowed, but maybe already selected?
					if (model.isSelected()) {
						model.setAlteredFA(0);
						if (model.isMercenaryOrMinion()) {
							sortedModels.get(groupMercOrMinions).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
				}
			} else {
				if (model.isMercenaryOrMinion()) {
					if (model.isVisible()) {
						sortedModels.get(groupMercOrMinions).add(model);
					}
				} else {
					sortedModels.get(groupNormal).add(model);	
				}
			}
			
		}
		
		// filter groups with no models
		List<SelectionGroup> groupsToDelete = new ArrayList<SelectionGroup>();
		for (SelectionGroup group : groups) {
			if ( sortedModels.get(group).isEmpty()) {
				groupsToDelete.add(group);
			}
		}
		groups.removeAll(groupsToDelete);

		Collections.sort(groups);
		for (SelectionGroup group : sortedModels.keySet()) {
			Collections.sort(sortedModels.get(group));
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		SelectionGroup group = groups.get(groupPosition);
		return sortedModels.get(group).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return ((SelectionEntry) getChild(groupPosition, childPosition)).getId().hashCode();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final SelectionEntry entry = (SelectionEntry) getChild(
				groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_army_selection_child,
					null);
		}
		
		
		boolean recycleViewForSameEntry = false;
		
		if (convertView.getTag() == entry) {
			recycleViewForSameEntry = true;
			// this view has the same tag than the target entry.
		}

//		convertView.findViewById(R.id.fond_degrade).setBackgroundResource(
//				faction.getDegradeResource());

		// association de la vue et du model
		convertView.setTag(entry);
		
		drawEntryView(convertView, entry);

		// gesture listener, flip --> to selected, <-- to selection
		final ModelFlingGestureListener flingListener = new ModelFlingGestureListener(convertView, activity);
		convertView.setOnTouchListener(flingListener);
		ImageButton addButton = (ImageButton) convertView.findViewById(R.id.addEntryButton);
		addButton.setTag(entry);
		addButton.setTag(R.id.addEntryButton, convertView);
		addButton.setOnClickListener(new OnClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View v) {
				SelectionEntry entry = (SelectionEntry) v.getTag();
				boolean directlyAdded =  ((ArmySelectionChangeListener) ListSelectionAdapter.this.activity).onModelAdded( entry);
				
				
				if (directlyAdded && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					
					v.postDelayed(new Runnable() {
					    @Override
					    public void run() {
					    	// wait 500ms until changing display, to allow ending of animations
					    	notifyDataSetChanged();
							// notify ONLY after playing the animation
							((ArmySelectionChangeListener) ListSelectionAdapter.this.activity).notifyArmyChange();
					    }
					}, 850);
					
					final View rowView = (View) v.getTag(R.id.addEntryButton);
					
					rowView.setHasTransientState(true);
					
					// play transition
					final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.push_right_out);
					final Animation anim2 = AnimationUtils.loadAnimation(activity, R.anim.slide_top_to_bottom);
					anim.setAnimationListener(new Animation.AnimationListener() {              

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							rowView.startAnimation(anim2);
						}
					});
					
					anim2.setAnimationListener(new Animation.AnimationListener() {              

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							rowView.setHasTransientState(false);
						}
					});
					
					
					rowView.startAnimation(anim);
				} else if (directlyAdded) {
					// don't play animation, but notify
			    	notifyDataSetChanged();
					((ArmySelectionChangeListener) ListSelectionAdapter.this.activity).notifyArmyChange();
				}
				 
			}
		});
		if (! entry.isSelectable()) {
			if (addButton.getVisibility() == View.VISIBLE){
				if (recycleViewForSameEntry) {
					// show animation to disappear button only if this is the same entry! do not animate if the row is recycled from another entry
					Animation anim = AnimationUtils.loadAnimation(activity, R.anim.shrink_fade_out);
					addButton.startAnimation(anim);
				}
			}
			addButton.setVisibility(View.INVISIBLE);
		} else {
			addButton.setVisibility(View.VISIBLE);
		}
		
		
		return convertView;
	}

	protected void drawEntryView(View convertView,
			final SelectionEntry model) {
		TextView tvLabel = (TextView) convertView.findViewById(R.id.model_label);
		TextView tvCost = (TextView) convertView.findViewById(R.id.entry_cost);
		TextView tvFA = (TextView) convertView.findViewById(R.id.entry_fa);
		ImageView completedImage = (ImageView) convertView.findViewById(R.id.imageContentComplete);
		ImageView imageCollection = (ImageView) convertView.findViewById(R.id.imageCollection); 
		TextView ownedTV = (TextView) convertView.findViewById(R.id.tfcollectionowned);
 
		tvLabel.setText(Html.fromHtml(model.toHTMLTitleString()));
		tvCost.setText(Html.fromHtml(model.toHTMLCostString()));
		tvFA.setText(Html.fromHtml(model.toHTMLFA()));

		if (model.isCompleted()) {
			completedImage.setVisibility(View.VISIBLE);	
		} else {
			completedImage.setVisibility(View.INVISIBLE);
		}
		
		if ( CollectionSingleton.getInstance().getOwnedMap().get(model.getId()) != null && 
				CollectionSingleton.getInstance().getOwnedMap().get(model.getId()).intValue() > 0) {
			imageCollection.setVisibility(View.VISIBLE);
			ownedTV.setVisibility(View.VISIBLE);
			ownedTV.setText(String.valueOf(CollectionSingleton.getInstance().getOwnedMap().get(model.getId()).intValue()));
			
		} else {
			imageCollection.setVisibility(View.INVISIBLE);
			ownedTV.setVisibility(View.INVISIBLE);
		}
		
		
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		SelectionGroup group = groups.get(groupPosition);
		return sortedModels.get(group).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return getGroup(groupPosition).hashCode() * 100;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		SelectionGroup group = (SelectionGroup) getGroup(groupPosition);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_army_selection_group,
					null);
		}

		
		ImageView imageExpand = (ImageView) convertView.findViewById(R.id.imageExpandCollapse);
		if (isExpanded) {
			imageExpand.setImageResource(R.drawable.expander_close_holo_dark);
		} else {
			imageExpand.setImageResource(R.drawable.expander_open_holo_dark);
		}
		
		// to handle left/right swipe
//		final ModelFlingGestureListener flingListener = new ModelFlingGestureListener(convertView, activity);
//		convertView.setOnTouchListener(flingListener);

		
		TextView tvLabel = (TextView) convertView.findViewById(R.id.def_arm_label);
		tvLabel.setText(group.getType().getTitle());
		ImageView image = (ImageView) convertView.findViewById(R.id.groupImage);
		image.setImageResource(group.getFaction().getLogoResource());	

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// on ne sélectionne qu'avec les boutons dédiés
		return false;
	}

	/**
	 * open the dialog box to selection unit size
	 * @param model
	 */
	public void askForUnitOptions(SelectionEntry model) {
		
		((PopulateArmyListActivity)activity).startActivityForResult(
				new Intent(activity, ChooseUnitSizeActivity.class),
				CHOOSE_UNIT_DIALOG);
		
	}

	@Override
	public void notifyDataSetChanged() {
		createGroupsFromModels();
		super.notifyDataSetChanged();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

}
