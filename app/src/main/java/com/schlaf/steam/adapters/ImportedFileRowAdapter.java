/**
 * 
 */
package com.schlaf.steam.adapters;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.importation.ImportFileListener;

/**
 * classe permettant de mapper une entrée de fichier à importer dans une liste de sélection
 * @author S0085289
 *
 */
public class ImportedFileRowAdapter extends ArrayAdapter<File> {
	
	  private final Context context;
	  private final List<File> files;

	  public ImportedFileRowAdapter(Context context,  List<File> files) {
	    super(context, R.layout.imported_file_selection, files);
		Collections.sort(files);
	    this.context = context;
	    this.files = files;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    if (convertView == null) {
		    convertView = inflater.inflate(R.layout.imported_file_selection, parent, false);
	    }
	    TextView title = (TextView) convertView.findViewById(R.id.file_title);
	    TextView description = (TextView) convertView.findViewById(R.id.file_description);
	    title.setText(files.get(position).getName());
	    description.setText( files.get(position).length() + "bytes");

	    ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.buttonDelete);
	    convertView.setTag(files.get(position));
		deleteButton.setTag(files.get(position));
		deleteButton.setFocusable(false);

		  deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					File file = (File) v.getTag();
					((ImportFileListener) context).onImportedFileDeleted(file);
				}
			});
		
	    return convertView;
	  }
	  
	
}
