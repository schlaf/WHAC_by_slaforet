/**
 * 
 */
package com.schlaf.steam.adapters;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.importation.ImportFileListener;

/**
 * classe permettant de mapper une entrée de fichier é importer dans une liste de sélection
 * @author S0085289
 *
 */
public class ImportFileRowAdapter extends BaseExpandableListAdapter {
	
	  private final Context context;

	  private String[] groups = new String[]{"WHAC dir", "Downloads"};
	  
	  private HashMap<String, List<File>> entries = new HashMap<String, List<File>>();
	  
	  public ImportFileRowAdapter(Context context,  List<File> whacFiles, List<File> downloadedFiles) {
	    super();
	    entries.put(groups[0], whacFiles);
	    entries.put(groups[1], downloadedFiles);
	    this.context = context;
	  }


	@Override
	public int getGroupCount() {
		return 2;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return entries.get(groups[groupPosition]).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return entries.get(groups[groupPosition]).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 100 + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_import_file_directory,
					null);
		}
		TextView tvLabel = (TextView) convertView.findViewById(R.id.section_label);
		tvLabel.setText(groups[groupPosition]);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		File file = (File) getChild(groupPosition, childPosition);
	    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    if (convertView == null) {
			    convertView = inflater.inflate(R.layout.import_file_selection, parent, false);
		    }
		    TextView title = (TextView) convertView.findViewById(R.id.file_title);
		    TextView description = (TextView) convertView.findViewById(R.id.file_description);
		    title.setText(file.getName());
		    description.setText( file.length() + "bytes");

		    ImageButton importButton = (ImageButton) convertView.findViewById(R.id.importButton);
		    convertView.setTag(file);
		    importButton.setTag(file);
		    importButton.setFocusable(false);

		    importButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					File file = (File) v.getTag();
					((ImportFileListener) context).onImportFileSelected(file);
				}
			});
		    
		    ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
			deleteButton.setTag(file);
			deleteButton.setFocusable(false);

			  deleteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						File file = (File) v.getTag();
						((ImportFileListener) context).onImportFileDeleted(file);
					}
				});
			
		    return convertView;

	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}


	public void remove(File file) {
		for (String group : entries.keySet()) {
			if (entries.get(group).contains(file)) {
				entries.get(group).remove(file);
				break;
			}
		}
		notifyDataSetChanged();
	}
	  
	
}
