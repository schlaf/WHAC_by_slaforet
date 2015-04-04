/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.card.ViewCardFragment.ViewCardActivityInterface;
import com.schlaf.steam.activities.selectlist.selected.BeastCommander;
import com.schlaf.steam.activities.selectlist.selected.JackCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedArmyCommander;
import com.schlaf.steam.activities.selectlist.selected.SelectedBattleEngine;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedItem;
import com.schlaf.steam.activities.selectlist.selected.SelectedJourneyManWarcaster;
import com.schlaf.steam.activities.selectlist.selected.SelectedLesserWarlock;
import com.schlaf.steam.activities.selectlist.selected.SelectedModel;
import com.schlaf.steam.activities.selectlist.selected.SelectedObjective;
import com.schlaf.steam.activities.selectlist.selected.SelectedSection;
import com.schlaf.steam.activities.selectlist.selected.SelectedSection.SectionTypeEnum;
import com.schlaf.steam.activities.selectlist.selected.SelectedSolo;
import com.schlaf.steam.activities.selectlist.selected.SelectedSoloMarshal;
import com.schlaf.steam.activities.selectlist.selected.SelectedUA;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.selectlist.selected.SelectedWA;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarbeast;
import com.schlaf.steam.activities.selectlist.selected.SelectedWarjack;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;
import com.schlaf.steam.adapters.ModelFlingGestureListener;
import com.schlaf.steam.data.ModelTypeEnum;

/**
 * adapteur pour presentation des entree de liste pour selection d'armee (cete e
 * selectionner)
 * 
 * @author S0085289
 * 
 */
public class ListSelectedAdapter extends BaseExpandableListAdapter {


	private Activity activity;
	/** liste des modeles selectionnes 
	 * <br> replicate from SelectionModelSingleton.get
	 * @see SelectionModelSingleton
	 */
	List<SelectedEntry> entries = SelectionModelSingleton.getInstance().getSelectedEntries();
	
	/** liste des groupes, calcules e partir des modeles disponibles */
	List<SelectedItem> groups; // casters, then units, then solos
	
	/**
	 * constructeur avec la liste initiale de modeles
	 * 
	 * @param activity
	 */
	public ListSelectedAdapter(Activity activity) {
		if ( ! (activity instanceof ArmySelectionChangeListener)) {
			throw new UnsupportedOperationException("ListSelectedAdapter must receive a ArmySelectionChangeListener as parent activity");
		}
		if ( ! (activity instanceof ViewCardActivityInterface)) {
			throw new UnsupportedOperationException("ListSelectedAdapter must receive a ViewCardActivityInterface as parent activity");
		}

		this.activity = activity;

		createGroupsFromModels();
	}

	/**
	 * e partir de la liste des models, extrait les groupes disponibles
	 * 1 caster = 1 groupe (contient caster attachment + jacks/beasts)
	 * 1 unite = 1 groupe ( contient unit attachment + Weapon attachment)
	 * 1 solo = 1 groupe
	 * 1 BE = 1 groupe
	 * les warjacks/beasts (hors Avatar) sont dans le groupe du caster
	 */
	private void createGroupsFromModels() {

		groups = new ArrayList<SelectedItem>();
		HashMap<String, SelectedItem> sectionGroups = new HashMap<String, SelectedItem>();

		for (SelectedEntry entry : entries) {
			
			if (entry instanceof SelectedArmyCommander) {
				SelectedSection commanderSection = new SelectedSection(SectionTypeEnum.CASTER);
				if (! sectionGroups.containsKey("Commanders")) {
					sectionGroups.put("Commanders", commanderSection);
				}
				groups.add(entry);
			}
			
			if (entry instanceof SelectedUnit) {
				SelectedSection commanderSection = new SelectedSection(SectionTypeEnum.UNIT);
				if (! sectionGroups.containsKey("Units")) {
					sectionGroups.put("Units", commanderSection);
				}
				groups.add(entry);
			}
			
			if (entry instanceof SelectedSolo && ! (entry instanceof SelectedObjective)) {
				SelectedSection commanderSection = new SelectedSection(SectionTypeEnum.SOLO);
				if (! sectionGroups.containsKey("Solos")) {
					sectionGroups.put("Solos", commanderSection);
				}
				groups.add(entry);
			}
			
			if (entry instanceof SelectedObjective) {
				SelectedSection commanderSection = new SelectedSection(SectionTypeEnum.OBJECTIVE);
				if (! sectionGroups.containsKey("Objectives")) {
					sectionGroups.put("Objectives", commanderSection);
				}
				groups.add(entry);
			}
			
			
			if (entry instanceof SelectedBattleEngine) {
				SelectedSection commanderSection = new SelectedSection(SectionTypeEnum.BATTLE_ENGINE);
				if (! sectionGroups.containsKey("Battle Engines")) {
					sectionGroups.put("Battle Engines", commanderSection);
				}
				groups.add(entry);
			}
		}
		
		groups.addAll(sectionGroups.values());

		Collections.sort(groups);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		SelectedEntry group = (SelectedEntry) groups.get(groupPosition);
		
		if (group instanceof SelectedArmyCommander) {
			SelectedModel attachment = ((SelectedArmyCommander) group).getAttachment();
			if (attachment != null ) {
				if (childPosition == 0) {
					return attachment;
				} else {
					return ((SelectedArmyCommander) group).getAttachedModels().get(childPosition-1); 
				}
			} else {
				return ((SelectedArmyCommander) group).getAttachedModels().get(childPosition);	
			}
		}
		if (group instanceof SelectedUnit) {
			return ((SelectedUnit) group).getChilds().get(childPosition);
		}
		if (group instanceof JackCommander) {
			return ((JackCommander) group).getJacks().get(childPosition);
		}
		if (group instanceof BeastCommander) {
			return ((BeastCommander) group).getBeasts().get(childPosition);
		}
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 1000 + childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final SelectedEntry entry = (SelectedEntry) getChild(
				groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_army_selected_child,
					null);
		}
		
		// association de la vue et du model
		HashMap<String, SelectedEntry> model = new HashMap<String, SelectedEntry>();
		model.put("group", (SelectedEntry) getGroup(groupPosition));
		model.put("child", entry);
		convertView.setTag(entry);
		
		drawEntryView(convertView, entry);
		
		// gesture listener, flip --> to select, <-- to unselect
//		final ModelFlingGestureListener flingListener = new ModelFlingGestureListener(this, convertView, entry, activity);
//		convertView.setOnTouchListener(flingListener);
		ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.buttonDelete);
		deleteButton.setTag(model);
		deleteButton.setFocusable(false);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				@SuppressWarnings("unchecked")
				HashMap<String, SelectedEntry> model = (HashMap<String, SelectedEntry>) v.getTag();
				SelectedEntry group = model.get("group");
				SelectedEntry child = model.get("child");
				
				((ArmySelectionChangeListener) activity).onEntryRemoved(child); // to play animation
				
				SelectionModelSingleton.getInstance().removeSelectedSubEntry(group, child);
				
				v.postDelayed(new Runnable() {
				    @Override
				    public void run() {
				    	// wait 380ms until changing display, to allow ending of animations
				    	notifyDataSetChanged();
				    	((ArmySelectionChangeListener) ListSelectedAdapter.this.activity).notifyArmyChange();
				    }
				}, 380);
			}
		});

        ToggleButton specialistButton = (ToggleButton) convertView.findViewById(R.id.specialistToggleButton);
        specialistButton.setTag(model);
        specialistButton.setFocusable(false);
        specialistButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                @SuppressWarnings("unchecked")
                HashMap<String, SelectedEntry> model = (HashMap<String, SelectedEntry>) v.getTag();
                SelectedEntry group = model.get("group");
                SelectedEntry child = model.get("child");
                ((ArmySelectionChangeListener) activity).onChangeSpecialistValue(child, ((ToggleButton) v).isChecked()); // to play animation
            }
        });


		return convertView;
	}

	protected void drawEntryView(View convertView,
			final SelectedEntry model) {
		if (model instanceof SelectedSolo) {
			// maybe an UA
			SelectionSolo selection = (SelectionSolo) SelectionModelSingleton.getInstance().getSelectionEntryById(model.getId());
		}
		if (model instanceof SelectedBattleEngine) {
		}
		if (model instanceof SelectedObjective) {
		}

        ((ToggleButton) convertView.findViewById(R.id.specialistToggleButton)).setChecked(model.isSpecialist());

		TextView tvDetail = (TextView) convertView.findViewById(R.id.entry_detail);
		tvDetail.setText(Html.fromHtml(model.toFullString()));
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		SelectedItem group = groups.get(groupPosition);
		
		if (group instanceof SelectedSection) {
			return 0;
		}
		
		if (group instanceof SelectedArmyCommander) {
			int addAttachment = 0;
			if (((SelectedArmyCommander) group).getAttachment() != null) {
				addAttachment = 1;
			}
			return ((SelectedArmyCommander) group).getAttachedModels().size() + addAttachment;
		}
		if (group instanceof SelectedUnit) {
			return ((SelectedUnit) group).getChilds().size();
		}
		if (group instanceof SelectedSoloMarshal) {
			return ((SelectedSoloMarshal) group).getJacks().size();
		}
		if (group instanceof SelectedJourneyManWarcaster) {
			return ((SelectedJourneyManWarcaster) group).getJacks().size();
		}
		if (group instanceof SelectedLesserWarlock) {
			return ((SelectedLesserWarlock) group).getBeasts().size();
		}
		if (group instanceof SelectedSolo) { // by the way, not a solo marshall...
			return 0; 
		}
		if (group instanceof SelectedBattleEngine) { 
			return 0; 
		}
		return 0;
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
		return groupPosition * 1000;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		SelectedItem maybeSection = (SelectedItem) getGroup(groupPosition);
		
		if (maybeSection instanceof SelectedSection) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_army_selected_section,
					null);
			TextView tvLabel = (TextView) convertView.findViewById(R.id.section_label);
			tvLabel.setText(((SelectedSection)maybeSection).getLabelId());
			
			return convertView;
		} 

		SelectedEntry group = (SelectedEntry) maybeSection;
		
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.row_army_selected_group,
					null);

		convertView.setTag(group);
		
        ((ToggleButton) convertView.findViewById(R.id.specialistToggleButton)).setVisibility(View.VISIBLE);
        // convertView.findViewById(R.id.tvSpecialist).setVisibility(View.VISIBLE);

		if (group instanceof SelectedArmyCommander) {
            ((ToggleButton) convertView.findViewById(R.id.specialistToggleButton)).setVisibility(View.INVISIBLE);
            // convertView.findViewById(R.id.tvSpecialist).setVisibility(View.INVISIBLE);
		} else if (group instanceof SelectedUnit) {
		} else if (group instanceof SelectedSolo) {
		}

        ((ToggleButton) convertView.findViewById(R.id.specialistToggleButton)).setChecked(group.isSpecialist());

		TextView tvLabel = (TextView) convertView.findViewById(R.id.def_arm_label);
		// tvLabel.setTextAppearance(activity, FactionNamesEnum.MENOTH.getStyleResource());
		tvLabel.setText(Html.fromHtml(group.getLabel()));
		
		TextView tvDetail = (TextView) convertView.findViewById(R.id.detail);
		tvDetail.setText(Html.fromHtml(group.getModelCountString() + group.getCostString()));
		
		
		ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.buttonDelete);
		deleteButton.setTag(group);
		deleteButton.setFocusable(false);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SelectedEntry entry = (SelectedEntry) v.getTag();
				SelectionModelSingleton.getInstance().removeSelectedEntry(entry);
				
				ArrayList<SelectedEntry> childs = new ArrayList<SelectedEntry>();
				if (entry instanceof SelectedArmyCommander) {
					SelectedModel attachment = ((SelectedArmyCommander) entry).getAttachment();
					if (attachment != null ) {
						childs.add(attachment);
					}
				}
				if (entry instanceof SelectedUnit) {
					for ( SelectedEntry child : ((SelectedUnit) entry).getChilds()) {
						childs.add(child);
					} 
				}
				if (entry instanceof JackCommander ) {
					for ( SelectedEntry child : ((JackCommander) entry).getJacks()) {
						childs.add(child);
					} 
				}

				if (entry instanceof BeastCommander ) {
					for ( SelectedEntry child : ((BeastCommander) entry).getBeasts()) {
						childs.add(child);
					} 
				}
				
				for (SelectedEntry child : childs) {
					((ArmySelectionChangeListener) activity).onEntryRemoved(child); // to play animation	
				}
				
				((ArmySelectionChangeListener) activity).onEntryRemoved(entry); // to play animation	
				
				v.postDelayed(new Runnable() {
				    @Override
				    public void run() {
				    	// wait 380ms until changing display, to allow ending of animations
				    	notifyDataSetChanged();
				    	((ArmySelectionChangeListener) ListSelectedAdapter.this.activity).notifyArmyChange();
				    }
				}, 380);
				
				
				
			}
		});


        ToggleButton specialistButton = (ToggleButton) convertView.findViewById(R.id.specialistToggleButton);
        specialistButton.setTag(group);
        specialistButton.setFocusable(false);
        specialistButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectedEntry group = (SelectedEntry) v.getTag();
                ((ArmySelectionChangeListener) activity).onChangeSpecialistValue(group, ((ToggleButton) v).isChecked()); // to play animation
            }
        });

		convertView.setFocusable(false);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// on ne selectionne qu'avec les boutons dedies
		return true;
	}

	@Override
	public void notifyDataSetChanged() {
		createGroupsFromModels();
		super.notifyDataSetChanged();
	}


}
