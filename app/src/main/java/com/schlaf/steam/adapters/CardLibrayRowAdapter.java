/**
 *
 */
package com.schlaf.steam.adapters;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.card.CardLibraryRowData;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * @author S0085289
 *
 */
public class CardLibrayRowAdapter extends ArrayAdapter<CardLibraryRowData> {

    private final Context context;
    private final List<CardLibraryRowData> cards;

    public CardLibrayRowAdapter(Context context,  List<CardLibraryRowData> cards) {
        super(context, R.layout.row_card_library, cards);

        Collections.sort(cards);
        this.context = context;
        this.cards = cards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_card_library, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.card_title);
        TextView description = (TextView) convertView.findViewById(R.id.card_subtitle);
        TextView fa = (TextView) convertView.findViewById(R.id.card_fa);
        TextView modelCount = (TextView) convertView.findViewById(R.id.card_unit_size);
        TextView modelsLabel = (TextView) convertView.findViewById(R.id.card_models_label);
        TextView cost = (TextView) convertView.findViewById(R.id.card_cost);

        description.setText(cards.get(position).getQualification());
        title.setText(Html.fromHtml(cards.get(position).getLabel()));
        fa.setText(cards.get(position).getFA());
        if (cards.get(position).isShowModelCount()) {
            modelCount.setText(cards.get(position).getModelCount());
            modelsLabel.setVisibility(View.VISIBLE);
        } else {
            modelCount.setText("");
            modelsLabel.setVisibility(View.INVISIBLE);
        }
        cost.setText(cards.get(position).getCost());

        return convertView;
    }


}
