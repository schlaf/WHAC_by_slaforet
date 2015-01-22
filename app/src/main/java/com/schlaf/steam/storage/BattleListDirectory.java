package com.schlaf.steam.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseBattleListener;

public class BattleListDirectory implements BattleListOrDirectory,
		Comparable<BattleListDirectory> {

	private String fullpath; // the real path 
	private String textualPath; // the path minus the startup /app/data/data/com....
	boolean virtual = false;

	public BattleListDirectory(String fullpath, String textualPath) {
		this(fullpath, false, textualPath);
	}
	
	public BattleListDirectory(String fullpath, boolean virtual, String textualPath) {
		this.fullpath = fullpath;
		this.virtual = virtual;
		this.textualPath = textualPath;
	}

	@Override
	public TYPES getType() {
		return TYPES.DIRECTORY;
	}

	@Override
	public View getView(View convertView, ViewGroup parent,
			LayoutInflater inflater) {
		if (convertView == null) {
			// instantiate new view
			convertView = inflater.inflate(R.layout.army_list_folder, parent,
					false);
		}

		TextView title = (TextView) convertView
				.findViewById(R.id.directoryNameTV);
		if (virtual) {
			title.setText("..");
		} else {
			title.setText(textualPath);
		}
		

		ImageButton deleteButton = (ImageButton) convertView
				.findViewById(R.id.buttonDelete);
		if (virtual) {
			deleteButton.setVisibility(View.INVISIBLE);
		} else {
			deleteButton.setVisibility(View.VISIBLE);
		}
		convertView.setTag(this);
		deleteButton.setTag(this);
		deleteButton.setFocusable(false);

		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ChooseBattleListener) v.getContext()).onBattleDirectoryDeleted( (BattleListDirectory) v.getTag());
			}
		});

		return convertView;
	}

	@Override
	public int compareTo(BattleListDirectory another) {
		if (fullpath == null) {
			return -1;
		} else if (another.getFullpath() == null) {
			return 1;
		}
		return fullpath.compareTo(another.getFullpath());
	}


	public boolean isVirtual() {
		return virtual;
	}

	public String getFullpath() {
		return fullpath;
	}

	public String getTextualPath() {
		return textualPath;
	}
	
}
