/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selected.SelectedEntry;
import com.schlaf.steam.activities.selectlist.selected.SelectedUnit;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;

/**
 * @author S0085289
 * 
 */
public class ChooseDeleteActivity extends Activity implements OnCheckedChangeListener {

	public static final int CHOOSE_DELETE_DIALOG = 2013;
	public static final String INTENT_ELEMENT_ID = "element_id";
	public static final String INTENT_ELEMENT_NUMBER = "element_number";

	private String selectionId;
	
	private List<SelectedEntry> selectionToDelete = new ArrayList<SelectedEntry>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_choose_delete);

		selectionId = getIntent().getStringExtra(INTENT_ELEMENT_ID);

		SelectionEntry selection = SelectionModelSingleton.getInstance()
				.getSelectionEntryById(selectionId);

		
		setTitle("Choose which element to delete");

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(selection.getFullLabel());


		RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroupDelete);
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
	                RadioGroup.LayoutParams.WRAP_CONTENT,
	                RadioGroup.LayoutParams.WRAP_CONTENT);

		if (selection instanceof SelectionUnit) {
			List<SelectedUnit> unitsToDelete = SelectionModelSingleton.getInstance().unitDeletionChoices(selection.getId());

			selectionToDelete.addAll(unitsToDelete);
			
			int i = 0;
			for (SelectedUnit unit : unitsToDelete) {
				RadioButton newRadioButton = new RadioButton(this);
				newRadioButton.setText(unit.toFullString());
				newRadioButton.setId(i);
				radiogroup.addView(newRadioButton, layoutParams);
				i++;
			}
		
		}
		
		radiogroup.setOnCheckedChangeListener(this);

	}

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		this.finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int selectedEntry = ((RadioGroup) findViewById(R.id.radioGroupDelete))
				.getCheckedRadioButtonId();

		SelectedEntry toDelete = selectionToDelete.get(selectedEntry);
		SelectionModelSingleton.getInstance().removeUnit((SelectedUnit) toDelete);
		
		Intent intent = new Intent();
		intent.putExtra(INTENT_ELEMENT_ID, selectionId);
		intent.putExtra(INTENT_ELEMENT_NUMBER, selectedEntry);

		setResult(RESULT_OK, intent);
		this.finish();
    }

}
