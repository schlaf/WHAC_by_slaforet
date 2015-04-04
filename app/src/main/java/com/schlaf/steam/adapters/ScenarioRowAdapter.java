/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.steamroller.ChooseScenarioDialog;
import com.schlaf.steam.activities.steamroller.SteamRollerSingleton;
import com.schlaf.steam.data.Mission;

/**
 * classe permettant de mapper une entrée de scénario dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class ScenarioRowAdapter extends RecyclerView.Adapter<ScenarioViewHolder>  {

	private final Context context;
    private final ChooseScenarioDialog parentFragment;
	private final List<Mission> scenarii;

	public ScenarioRowAdapter(Context context, ChooseScenarioDialog parentFragment, List<Mission> scenarii) {
		super();
		Collections.sort(scenarii);
        this.context = context;
        this.parentFragment = parentFragment;
        this.scenarii = scenarii;
    }

    @Override
    public ScenarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_scenario, parent, false);
        ScenarioViewHolder mViewHolder=new ScenarioViewHolder(mView);
        return mViewHolder;

    }

    @Override
    public void onBindViewHolder(final ScenarioViewHolder holder, final int position) {
        holder.number.setText(scenarii.get(position).getNumber());
        holder.title.setText(scenarii.get(position).getName());
        holder.map.setImageResource(scenarii.get(position).getMapResourceId());

        holder.container.setTag(scenarii.get(position));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mission mission = (Mission) holder.container.getTag();
                parentFragment.showScenario(mission, position);
            }
        });

        if ( SteamRollerSingleton.getInstance().getCurrentMission() == scenarii.get(position)) {
            holder.container.setCardBackgroundColor(context.getResources().getColor(R.color.WhacAccentColorHalfTransparent));
        } else {
            holder.container.setCardBackgroundColor(context.getResources().getColor(android.support.v7.cardview.R.color.cardview_light_background));
        }

    }

    @Override
    public int getItemCount() {
        return scenarii.size();
    }
}
