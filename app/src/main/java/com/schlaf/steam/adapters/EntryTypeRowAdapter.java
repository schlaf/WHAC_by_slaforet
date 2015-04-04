/**
 * 
 */
package com.schlaf.steam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChangeEntryTypeListener;
import com.schlaf.steam.activities.card.CardLibrarySingleton;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.ModelTypeEnumTranslated;

import java.util.List;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class EntryTypeRowAdapter extends RecyclerView.Adapter<EntryTypeViewHolder> {

	private final Context context;
    ChangeEntryTypeListener parentFragment;
	private final List<ModelTypeEnumTranslated> entryTypes;

	public EntryTypeRowAdapter(Context context, ChangeEntryTypeListener parentFragment, List<ModelTypeEnumTranslated> entryTypes) {
		super();
        this.parentFragment = parentFragment;
		this.context = context;
		this.entryTypes = entryTypes;
	}


    @Override
    public EntryTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_type_selection, parent, false);
        EntryTypeViewHolder mViewHolder=new EntryTypeViewHolder(mView);
        return mViewHolder;

    }

    @Override
    public void onBindViewHolder(final EntryTypeViewHolder holder, int position) {
        holder.title.setText(entryTypes.get(position).toString());

        holder.card.setTag(entryTypes.get(position));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentFragment.onChangeModelType((ModelTypeEnumTranslated) holder.card.getTag());
            }
        });

        if ( CardLibrarySingleton.getInstance().getEntryType() == entryTypes.get(position).getType()) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.WhacAccentColorHalfTransparent));
        } else {
            holder.card.setCardBackgroundColor(context.getResources().getColor(android.support.v7.cardview.R.color.cardview_light_background));
        }

    }

    @Override
    public int getItemCount() {
        return entryTypes.size();
    }
}
