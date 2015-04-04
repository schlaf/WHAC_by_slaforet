package com.schlaf.steam.activities.importation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.SelectionEntryViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by seb on 10/03/15.
 */
public class FileVersionAdapter extends RecyclerView.Adapter<FileVersionViewHolder> {


    public List<FileVersion> getVersions() {
        return versions;
    }

    private List<FileVersion> versions = new ArrayList<FileVersion>();

    @Override
    public FileVersionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_file_import_version, parent, false);
        FileVersionViewHolder mViewHolder=new FileVersionViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(FileVersionViewHolder holder, int position) {

        holder.factionLogo.setImageResource(versions.get(position).faction.getLogoResource());
        holder.factionLabel.setText(versions.get(position).factionName);

        String localVersion = versions.get(position).localVersion;
        String latestVersion = versions.get(position).lastVersion;
        holder.localVersion.setText(localVersion);
        holder.latestVersion.setText(latestVersion);

        if (latestVersion != null && latestVersion.equals(localVersion)) {
            holder.okVersionIcon.setImageResource(R.drawable.victory);
        } else {
            holder.okVersionIcon.setImageResource(R.drawable.defeat);
        }

    }

    @Override
    public int getItemCount() {
        return versions.size() ;
    }
}
