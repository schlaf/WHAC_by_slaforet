/**
 * 
 */
package com.schlaf.steam.activities.managelists;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.StorageManager;

/**
 * @author S0085289
 *
 */
public class ManageArmyListsActivity extends ListActivity {

	public static final int CHOOSE_ARMY_LIST_DIALOG = 126;
	public static final String INTENT_ARMY_NAME = "army_name";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Choose existing army list to edit");
		ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this,
				  android.R.layout.simple_list_item_1, android.R.id.text1, getArmyLabels());
	    setListAdapter(simpleAdapter);
	    
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String result = getFileName(position);

		Intent intent = new Intent(this, EditArmyListActivity.class);
		intent.putExtra(EditArmyListActivity.INTENT_ARMY_FILENAME, result);
		startActivityForResult(intent, EditArmyListActivity.EDIT_ARMY_NAME_DIALOG);
	}

	public String[] getArmyLabels() {
		
		List<ArmyListDescriptor> descriptors = StorageManager.getArmyLists(getApplicationContext());
		String[] result = new String[descriptors.size()];
		
		int i = 0;
		for (ArmyListDescriptor descriptor : descriptors) {
			result[i] = descriptor.toString();
			i++;
		}

		return result;
	}
	
	public String getFileName(int position) {
		
		List<ArmyListDescriptor> descriptors = StorageManager.getArmyLists(getApplicationContext());
		return descriptors.get(position).getFileName();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this,
				  android.R.layout.simple_list_item_1, android.R.id.text1, getArmyLabels());
	    setListAdapter(simpleAdapter);
	}
	
}
