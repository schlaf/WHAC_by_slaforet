/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;

/**
 * classe permettant de mapper une entrée de choix d'armée dans une liste de sélection
 * @author S0085289
 *
 */
public class ChooseAttachEntryRowAdapter extends ArrayAdapter<SelectedEntry> {
	
	  private final Context context;
	  private final List<SelectedEntry> results;

	  public ChooseAttachEntryRowAdapter(Context context,  List<SelectedEntry> results) {
	    super(context, R.layout.choose_attach_entry_row, results);
	    this.context = context;
	    this.results = results;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    if (convertView == null) {
		    convertView = inflater.inflate(R.layout.choose_attach_entry_row, parent, false);
	    }
	    
	    SelectedEntry entry = results.get(position);
	    
	    TextView entryName = (TextView) convertView.findViewById(R.id.entryName);
	    TextView entryDescription = (TextView) convertView.findViewById(R.id.entryDetails);
	    
	    entryName.setText(entry.getLabel());
	    entryDescription.setText( entry.getAttachString());
	    
	    return convertView;
	  }
	  
	
}
