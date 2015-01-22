/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.adapters.ChooseAttachEntryRowAdapter;

/**
 * @author S0085289
 * 
 */
public class ChooseAttachActivity extends DialogFragment implements OnItemClickListener {

	
	private ListView listView;
	ChooseAttachEntryRowAdapter adapter;
	
	ChooseAttachInterface parentActivity;

	public interface ChooseAttachInterface {
		public SelectionEntry getModelToAttach();
		
		public void setTargetModelForAttachment(SelectedEntry selectedModel);
	}
	

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    
		// Create the AlertDialog object and return it
	    View view = inflater.inflate(R.layout.choose_attach_to_dialog, null);
	    
	    listView = (ListView) view.findViewById(android.R.id.list);		
	    
	    
	    SelectionEntry selection = parentActivity.getModelToAttach();
	    
	    TextView tvTitle = (TextView) view.findViewById(R.id.modelToAttachTitle);
	    tvTitle.setText(selection.getFullLabel());
	    
		
		List<SelectedEntry> lists = SelectionModelSingleton
				.getInstance().modelsToWhichAttach(selection);

	    adapter = new ChooseAttachEntryRowAdapter(getActivity(), lists);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
	    builder.setView(view);
		return builder.create();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		parentActivity.setTargetModelForAttachment( (SelectedEntry) listView.getItemAtPosition(position));
		dismiss();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		Log.d("ExistingArmiesFragment", "onAttach");
		super.onAttach(activity);
		if (activity instanceof ChooseAttachInterface) {
			parentActivity = (ChooseAttachInterface) activity;
		} else {
			throw new UnsupportedOperationException(activity.toString()
					+ " must implemenet ChooseAttachInterface");
		}
	}


}
