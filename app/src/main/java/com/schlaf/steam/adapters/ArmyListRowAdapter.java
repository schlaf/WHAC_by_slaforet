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

import com.schlaf.steam.R;
import com.schlaf.steam.storage.ArmyListOrDirectory;

/**
 * classe permettant de mapper une entrée de Liste d'armée ou répertoire dans une liste de sélection
 * @author S0085289
 *
 */
public class ArmyListRowAdapter extends ArrayAdapter<ArmyListOrDirectory> {
	
	  public ArmyListRowAdapter(Context context,  List<ArmyListOrDirectory> armies) {
	    super(context, R.layout.army_list_selection, armies);
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) getContext()
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    return getItem(position).getView(convertView, parent, inflater);
	  }
	  
	  @Override
	  public int getItemViewType(int position) {
		  return getItem(position).getType().ordinal();
	  }

	@Override
	public int getViewTypeCount() {
		// one for folders, 1 for folder creation, 1 for list descriptor
		return ArmyListOrDirectory.TYPES.values().length;
	}
	  
	  
	  
	
}
