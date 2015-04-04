package com.schlaf.steam.activities.importation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;

/**
 * Created by seb on 10/03/15.
 */
public class FileVersionViewHolder extends RecyclerView.ViewHolder {

    ImageView factionLogo;
    TextView factionLabel;
    TextView localVersion;
    TextView latestVersion;
    ImageView okVersionIcon;

    public FileVersionViewHolder(View itemView) {
        super(itemView);

        factionLogo = (ImageView) itemView.findViewById(R.id.factionLogo);
        factionLabel = (TextView) itemView.findViewById(R.id.factionLabel);
        localVersion = (TextView) itemView.findViewById(R.id.localVersion);
        latestVersion = (TextView) itemView.findViewById(R.id.latestVersion);
        okVersionIcon = (ImageView) itemView.findViewById(R.id.okVersionIcon);
    }
}
