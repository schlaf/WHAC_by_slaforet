package com.schlaf.steam.activities.selectlist;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SelectionGroupAdapter extends RecyclerView.Adapter<SelectionViewHolder> {

	private static final String TAG = "SelectionGroupAdapter";

	HashMap<SelectionGroup, List<SelectionEntry>> sortedModels = new HashMap<SelectionGroup, List<SelectionEntry>>();

	/** use strict FA rules */
	boolean strictFA = true;

	// true = liste des groupes, false = choix des entrée dans le groupe
	SelectionGroup selectedGroup;
	/** liste des groupes, calculés à partir des modèles disponibles */
	List<SelectionGroup> groups = new ArrayList<SelectionGroup>();



	public SelectionGroupAdapter() {
		super();
		setHasStableIds(true);
		groups = SelectionModelSingleton.getInstance().createGroupsFromModels();
	}
	
	
	@Override
	public long getItemId (int position) {
        return groups.get(position).hashCode();
	}
	
	

	
	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount");
        return groups.size();
	}

	@Override
	public void onBindViewHolder(SelectionViewHolder holder, final int position) {
        bindGroup( (SelectionGroupViewHolder) holder, position);
	}

	private void selectGroup(int position, View v) {

        SelectionGroup group = groups.get(position);

        ((ArmySelectionChangeListener)  v.getContext()).selectedGroup(group);

		selectedGroup = groups.get(position);
	}

    /**
     * force recalculation of all groups
     */
    public void resetGroups() {
        groups = SelectionModelSingleton.getInstance().createGroupsFromModels();
        notifyDataSetChanged();
    }

	private void bindGroup(final SelectionGroupViewHolder holder, final int position) {

        final SelectionGroup group = (SelectionGroup) groups.get(position);
        holder.tvLabel.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.primary_text_light));
        holder.image.setVisibility(View.VISIBLE);
        holder.imageExpand.setVisibility(View.VISIBLE);

        // holder.imageExpand.setImageResource(R.drawable.expander_open_holo_dark);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // play transition
                final Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.push_left_out);
                anim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });

                v.startAnimation(anim);

                selectGroup(position, v);
            }
        });
        holder.tvLabel.setText(group.getType().getTitle());
        holder.image.setImageResource(group.getFaction().getLogoResource());



    }


	@Override
	public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_army_selection_group, parent, false);
        SelectionGroupViewHolder mViewHolder=new SelectionGroupViewHolder(mView);
        return mViewHolder;
	}

}
