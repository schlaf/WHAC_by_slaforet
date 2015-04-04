package com.schlaf.steam.activities.selectlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.collection.CollectionSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.data.Faction.GameSystem;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.data.ModelTypeEnum;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionViewHolder> {

	private static final String TAG = "SelectionAdapter"; 

	HashMap<SelectionGroup, List<SelectionEntry>> sortedModels = new HashMap<SelectionGroup, List<SelectionEntry>>();

	/** use strict FA rules */
	boolean strictFA = true;
	
	boolean modeSelectGroup = true;
	// true = liste des groupes, false = choix des entrée dans le groupe
	SelectionGroup selectedGroup;
	/** liste des groupes, calculés à partir des modèles disponibles */
	List<SelectionGroup> groups;
	/** liste des entrées dispo pour le groupe sélectionné */
	private List<SelectionEntry> selectionEntries = new ArrayList<SelectionEntry>();
	
	
	private static final int VIEW_TYPE_GROUP = 1;
	private static final int VIEW_TYPE_ENTRY = 2;
	
	
	public SelectionAdapter() {
		super();
		setHasStableIds(true);
		createGroupsFromModels();
	}
	
	
	@Override
	public long getItemId (int position) {
		
		if (modeSelectGroup) {
			return groups.get(position).hashCode();
		} else {
			if (position == 0) {
				return selectedGroup.hashCode();
			} else {
				return selectionEntries.get(position-1).hashCode();
			}
		}
		
	}
	
	
	@Override
	public int getItemViewType(int position) {
        // for example
        // Note that unlike ListView adapters, types need not be contiguous
		if (modeSelectGroup) {
			return VIEW_TYPE_GROUP;	
		} else {
			if (position == 0) {
				return VIEW_TYPE_GROUP;	
			} else {
				return VIEW_TYPE_ENTRY;	
			}

		}
        
    }	
	

	/**
	 * à partir de la liste des models, extrait les groupes disponibles
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
				
			SelectionGroup groupObjectives = new SelectionGroup(ModelTypeEnum.OBJECTIVE, FactionNamesEnum.OBJECTIVES_SR2015);
				

			if (model.isVisible()) {
				if (model.isObjective()) {
					if ( ! groups.contains(groupObjectives)) {
						groups.add(groupObjectives);
						sortedModels.put(groupObjectives,
								new ArrayList<SelectionEntry>());
					}
					
				} else if (model.isMercenaryOrMinion()) {
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
						} else if (model.isObjective()) {
							sortedModels.get(groupObjectives).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
				} else {
					// not allowed, but maybe already selected?
					if (model.isSelected() && ! model.isObjective()) {
						model.setAlteredFA(0);
						if (model.isMercenaryOrMinion()) {
							sortedModels.get(groupMercOrMinions).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
                    // objectives always allowed!
                    if (model.isObjective()) {
                        sortedModels.get(groupObjectives).add(model);
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
					if (model.isSelected() && ! model.isObjective()) {
						model.setAlteredFA(0);
						if (model.isMercenaryOrMinion()) {
							sortedModels.get(groupMercOrMinions).add(model);
						} else {
							sortedModels.get(groupNormal).add(model);	
						}
					}
                    // objectives always allowed!
                    if (model.isObjective()) {
                        sortedModels.get(groupObjectives).add(model);
                    }
                }
			} else {
				if (model.isMercenaryOrMinion()) {
					if (model.isVisible()) {
						sortedModels.get(groupMercOrMinions).add(model);
					}
				} else if (model.isObjective()) {
					sortedModels.get(groupObjectives).add(model);
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
	public int getItemCount() {
		Log.d(TAG, "getItemCount");
		if (modeSelectGroup) {
			return groups.size();
		} else {
			return 1 + selectionEntries.size();
		}
	}

	@Override
	public void onBindViewHolder(SelectionViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		switch (getItemViewType(position)) {
		case VIEW_TYPE_GROUP: 
			bindGroup( (SelectionGroupViewHolder) holder, position);
			break;
		case VIEW_TYPE_ENTRY:
			bindEntry( (SelectionEntryViewHolder) holder, position);
			break;
		}
		
	}

	private void bindEntry(SelectionEntryViewHolder holder, int position) {
		// TODO Auto-generated method stub
		final SelectionEntry model = selectionEntries.get(position-1); // i-1 cause 0 is the group
		
		holder.tvLabel.setText(Html.fromHtml(model.toHTMLTitleString()));
		holder.tvCost.setText(Html.fromHtml(model.toHTMLCostString()));
		holder.tvFA.setText(Html.fromHtml(model.toHTMLFA()));

		if (model.isCompleted()) {
			holder.completedImage.setVisibility(View.VISIBLE);	
		} else {
			holder.completedImage.setVisibility(View.INVISIBLE);
		}
		
		if ( CollectionSingleton.getInstance().getOwnedMap().get(model.getId()) != null && 
				CollectionSingleton.getInstance().getOwnedMap().get(model.getId()).intValue() > 0) {
			holder.imageCollection.setVisibility(View.VISIBLE);
			holder.ownedTV.setVisibility(View.VISIBLE);
			holder.ownedTV.setText(String.valueOf(CollectionSingleton.getInstance().getOwnedMap().get(model.getId()).intValue()));
			
		} else {
			holder.imageCollection.setVisibility(View.INVISIBLE);
			holder.ownedTV.setVisibility(View.INVISIBLE);
		}

		if (model.isSelectable()) {
			holder.addButton.setVisibility(View.VISIBLE);
		} else {
			holder.addButton.setVisibility(View.INVISIBLE);
		}
		
		holder.addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectionEntry entry = (SelectionEntry) model;
				boolean directlyAdded =  ((ArmySelectionChangeListener) v.getContext() ).onModelAdded( entry);
				
				if (directlyAdded && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					// wait 500ms until changing display, to allow ending of animations
					notifyDataSetChanged();
					// notify ONLY after playing the animation
					((ArmySelectionChangeListener) v.getContext()).notifyArmyChange();
				} else if (directlyAdded) {
					// don't play animation, but notify
			    	notifyDataSetChanged();
					((ArmySelectionChangeListener)  v.getContext()).notifyArmyChange();
				}
			}		
		});
		
		holder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ArmySelectionChangeListener)  v.getContext()).viewSelectionDetail(model);
			}
		});
		
		
	}
	
	private void selectGroup(int position, View v) {

        ((ArmySelectionChangeListener)  v.getContext()).selectedGroup(null);


		selectedGroup = groups.get(position);
		modeSelectGroup = false;
		

		for (int i = groups.size() - 1; i >=0 ; i--) {
			if (i != position) {
				notifyItemRemoved(i);
			} 
		}

		if (position > 0) {
			notifyItemRangeRemoved(0, position);
		}
		if (position < groups.size() - 1) {
			notifyItemRangeRemoved(position + 1, groups.size() - position + 1 );
		}
		
		
		
		selectionEntries = sortedModels.get(selectedGroup);
		
		notifyItemChanged(0);
		
		notifyItemRangeInserted(1, selectionEntries.size());
		
	}

    /**
     * force recalculation of all groups
     */
    public void resetGroups() {
        modeSelectGroup = true;
        createGroupsFromModels();
        notifyDataSetChanged();
    }

	public void backToGroups(SelectionGroup oldGroupSelected, View v) {

        ((ArmySelectionChangeListener)  v.getContext()).unselectedGroup();

        modeSelectGroup = true;
		
		createGroupsFromModels();
		
		if (selectionEntries.size() > 0) {
			notifyItemRangeRemoved(1, selectionEntries.size());
		}
		
		selectionEntries = new ArrayList<SelectionEntry>();
		
		// find current group id
		int groupPosition = 0;
		
		for (int i = 0; i< groups.size(); i++) {
			if (groups.get(i).equals(oldGroupSelected)) {
				groupPosition = i;
			} 
		}
		
		if (groupPosition > 0) {
			notifyItemRangeInserted(0, groupPosition);
		}
		if (groupPosition < groups.size() - 1) {
			notifyItemRangeInserted(groupPosition + 1, groups.size() - groupPosition + 1 );
		}
		
		notifyItemChanged(groupPosition);
		
	}

	private void bindGroup(final SelectionGroupViewHolder holder, final int position) {
		
		if (modeSelectGroup) {
			final SelectionGroup group = (SelectionGroup) groups.get(position);
			holder.tvLabel.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.primary_text_dark));
			// we can select a group
			//holder.imageBack.setVisibility(View.GONE);
			holder.image.setVisibility(View.VISIBLE);
			holder.imageExpand.setVisibility(View.VISIBLE);
			// holder.imageExpand.setImageResource(R.drawable.expander_open_holo_dark);
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Animation animShow = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.abc_fade_in);
					//holder.imageBack.startAnimation(animShow);
					// select current group
					selectGroup(position, v);
				}
			});
			holder.tvLabel.setText(group.getType().getTitle());
			holder.image.setImageResource(group.getFaction().getLogoResource());	
		} else {
			final SelectionGroup group = (SelectionGroup) selectedGroup;
			holder.tvLabel.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.WhacAccentColor));
			
			// we can go back to group list
			//holder.imageBack.setVisibility(View.VISIBLE);
			holder.imageExpand.setVisibility(View.GONE);
			holder.image.setVisibility(View.GONE);
			final Animation animFade = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.abc_fade_out);
			
			holder.imageExpand.setImageResource(R.drawable.expander_close_holo_dark);
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//holder.imageBack.startAnimation(animFade);
					// select current group
					backToGroups(group, v);
				}
			});
			holder.tvLabel.setText(group.getType().getTitle());
			holder.image.setImageResource(group.getFaction().getLogoResource());	
		}
		
		
		
	}


	@Override
	public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		
		if (viewType == VIEW_TYPE_GROUP) {
			View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_army_selection_group, parent, false);
			SelectionGroupViewHolder mViewHolder=new SelectionGroupViewHolder(mView);
			return mViewHolder;
		} else {
			View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_army_selection_child, parent, false);
			SelectionEntryViewHolder mViewHolder=new SelectionEntryViewHolder(mView);
			return mViewHolder;
		}
	}

}
