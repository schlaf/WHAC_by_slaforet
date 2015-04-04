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
public class FactionViewHolder extends RecyclerView.ViewHolder {

    public CardView card;
    public ImageView logo;
    public TextView title;


    public FactionViewHolder(View itemView) {
        super(itemView);
        card = (CardView) itemView.findViewById(R.id.card_view);
        logo = (ImageView) itemView.findViewById(R.id.icon);
        title = (TextView) itemView.findViewById(R.id.title);

    }
}
