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
public class EntryTypeViewHolder extends RecyclerView.ViewHolder {

    public CardView card;
    public ImageView logo;
    public TextView title;


    public EntryTypeViewHolder(View itemView) {
        super(itemView);
        card = (CardView) itemView.findViewById(R.id.card_view);
        title = (TextView) itemView.findViewById(R.id.title);

    }
}
