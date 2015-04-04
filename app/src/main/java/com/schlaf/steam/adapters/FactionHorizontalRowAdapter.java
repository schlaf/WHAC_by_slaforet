/**
 * 
 */
package com.schlaf.steam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChangeFactionListener;
import com.schlaf.steam.activities.card.CardLibrarySingleton;
import com.schlaf.steam.activities.card.ChooseCardFromLibraryDialog;
import com.schlaf.steam.activities.selectlist.SelectionEntryViewHolder;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;

import java.util.List;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class FactionHorizontalRowAdapter extends RecyclerView.Adapter<FactionViewHolder> {

	private final Context context;
    private ChangeFactionListener parentFragment;
	private final List<Faction> factions;

	public FactionHorizontalRowAdapter(Context context, ChangeFactionListener parentFragment, List<Faction> factions) {
		super();

        this.parentFragment = parentFragment;
		this.context = context;
		this.factions = factions;
	}


    @Override
    public FactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.faction_selection2, parent, false);
        FactionViewHolder mViewHolder=new FactionViewHolder(mView);
        return mViewHolder;

    }

    @Override
    public void onBindViewHolder(final FactionViewHolder holder, int position) {
        holder.card.setTag(factions.get(position));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentFragment.onChangeFaction( (Faction) holder.card.getTag());
            }
        });

        holder.logo.setImageResource(factions.get(position).getEnumValue().getLogoResource());
        holder.title.setText(factions.get(position).getFullName());

        if ( CardLibrarySingleton.getInstance().getFaction() == factions.get(position)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.WhacAccentColorHalfTransparent));
        } else {
            holder.card.setCardBackgroundColor(context.getResources().getColor(android.support.v7.cardview.R.color.cardview_light_background));
        }
    }

    @Override
    public int getItemCount() {
        return factions.size();
    }
}
