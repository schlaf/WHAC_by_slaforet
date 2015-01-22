package com.schlaf.steam.activities.selectlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;

public class SelectionGroupViewHolder extends SelectionViewHolder {

	ImageView imageBack;
	ImageView imageExpand;
	TextView tvLabel;
	ImageView image;

	
	public SelectionGroupViewHolder(View itemView) {
		super(itemView);
		imageBack = (ImageView) itemView.findViewById(R.id.imageBack);
		imageExpand = (ImageView) itemView.findViewById(R.id.imageExpandCollapse);
		tvLabel = (TextView) itemView.findViewById(R.id.def_arm_label);
		image = (ImageView) itemView.findViewById(R.id.groupImage);
	}

}
