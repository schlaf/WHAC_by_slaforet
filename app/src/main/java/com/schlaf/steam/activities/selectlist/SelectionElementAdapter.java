package com.schlaf.steam.activities.selectlist;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.collection.CollectionSingleton;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;

import java.util.ArrayList;
import java.util.List;

public class SelectionElementAdapter extends RecyclerView.Adapter<SelectionViewHolder> {

	private static final String TAG = "SelectionElementAdapter";

	/** liste des entrées dispo pour le groupe sélectionné */
	private List<SelectionEntry> selectionEntries = new ArrayList<SelectionEntry>();


	public SelectionElementAdapter() {
		super();

        selectionEntries = SelectionModelSingleton.getInstance().getModelsForCurrentGroup();

		setHasStableIds(true);
	}

    public void updateEntries() {
        selectionEntries = SelectionModelSingleton.getInstance().getModelsForCurrentGroup();
    }
	
	@Override
	public long getItemId (int position) {
		Log.d(TAG, "getItemId" + position);
        return selectionEntries.get(position).hashCode();

	}
	
	@Override
	public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return selectionEntries.size();
	}

	@Override
	public void onBindViewHolder(SelectionViewHolder holder, final int position) {
			bindEntry( (SelectionEntryViewHolder) holder, position);
	}

	private void bindEntry(SelectionEntryViewHolder holder, final int position) {
		// TODO Auto-generated method stub
        Log.d(TAG, "bindEntry" + position);
		SelectionEntry model = selectionEntries.get(position);
		
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
			holder.ownedTV.setText(String.valueOf(CollectionSingleton.getInstance().getOwnedMap().get(model.getId()).intValue())
            );
			
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
				final SelectionEntry entry = (SelectionEntry) selectionEntries.get(position);
				boolean directlyAdded =  ((ArmySelectionChangeListener) v.getContext() ).onModelAdded( entry);
				
				if (directlyAdded && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					// wait 500ms until changing display, to allow ending of animations
                    notifyItemChanged(position);
					// notify ONLY after playing the animation
					((ArmySelectionChangeListener) v.getContext()).notifyArmyChange();
				} else if (directlyAdded) {
					// don't play animation, but notify
                    notifyItemChanged(position);
					((ArmySelectionChangeListener)  v.getContext()).notifyArmyChange();
				}
			}		
		});
		
		holder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                final SelectionEntry entry = (SelectionEntry) selectionEntries.get(position);
				((ArmySelectionChangeListener)  v.getContext()).viewSelectionDetail(entry);
			}
		});
		
		
	}
	
	public void backToGroups(View v) {

        ((ArmySelectionChangeListener)  v.getContext()).unselectedGroup();

	}


	@Override
	public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_army_selection_child, parent, false);
        SelectionEntryViewHolder mViewHolder=new SelectionEntryViewHolder(mView);
        return mViewHolder;
	}

}
