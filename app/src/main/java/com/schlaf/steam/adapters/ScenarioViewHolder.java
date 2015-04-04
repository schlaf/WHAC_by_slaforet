package com.schlaf.steam.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;

/**
 * Created by seb on 27/03/15.
 */
public class ScenarioViewHolder extends RecyclerView.ViewHolder {

    public CardView container;
    public TextView number;
    public TextView title;
    public ImageView map;


    public ScenarioViewHolder(View itemView) {
        super(itemView);
        container = (CardView) itemView.findViewById(R.id.card_view);
        number = (TextView) itemView
                .findViewById(R.id.scenario_number);
        title = (TextView) itemView
                .findViewById(R.id.scenario_title);
        map = (ImageView) itemView.findViewById(R.id.scenario_map);
    }

}
