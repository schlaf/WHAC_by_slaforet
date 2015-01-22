package com.schlaf.steam.activities.selectlist;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;

public class SelectionEntryViewHolder extends SelectionViewHolder {

	
	ImageButton addButton;
	TextView tvLabel;
	TextView tvCost;
	TextView tvFA;
	ImageView completedImage;
	ImageView imageCollection; 
	TextView ownedTV;
	
	public SelectionEntryViewHolder(View itemView) {
		super(itemView);

		addButton = (ImageButton) itemView.findViewById(R.id.addEntryButton);
		tvLabel = (TextView) itemView.findViewById(R.id.model_label);
		tvCost = (TextView) itemView.findViewById(R.id.entry_cost);
		tvFA = (TextView) itemView.findViewById(R.id.entry_fa);
		completedImage = (ImageView) itemView.findViewById(R.id.imageContentComplete);
		imageCollection = (ImageView) itemView.findViewById(R.id.imageCollection); 
		ownedTV = (TextView) itemView.findViewById(R.id.tfcollectionowned);

	}

}
