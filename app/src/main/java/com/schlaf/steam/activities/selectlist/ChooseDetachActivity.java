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
import com.schlaf.steam.activities.selectlist.selected.Entry;
import com.schlaf.steam.activities.selectlist.selected.JackCommander;
import com.schlaf.steam.activities.selectlist.selection.SelectionEntry;
import com.schlaf.steam.data.ModelTypeEnum;

/**
 * @author S0085289
 * 
 */
public class ChooseDetachActivity extends Activity implements
		OnCheckedChangeListener {

	public static final int CHOOSE_DETACH_DIALOG = 2056;
	public static final String INTENT_ELEMENT_ID = "element_id";
	public static final String INTENT_ELEMENT_NUMBER = "element_number";
	public static final String INTENT_FROM_LIST = "com.schlaf.steam.activities.ChooseDetachActivity.from";

	private String selectionId;

	private List<Entry> entriesToDetach = new ArrayList<Entry>();

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_choose_delete);

		selectionId = getIntent().getStringExtra(INTENT_ELEMENT_ID);

		SelectionEntry selection = SelectionModelSingleton.getInstance()
				.getSelectionEntryById(selectionId);

		setTitle("Delete model with multiple attachments");

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(selection.getFullLabel()
				+ " is attached to these entries : choose from which one to remove");

		RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroupDelete);
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
				RadioGroup.LayoutParams.WRAP_CONTENT,
				RadioGroup.LayoutParams.WRAP_CONTENT);

		 entriesToDetach = (List<Entry>) getIntent()
				.getSerializableExtra(INTENT_FROM_LIST);

		int i = 0;
		for (Entry entry : entriesToDetach) {
			RadioButton newRadioButton = new RadioButton(this);
			newRadioButton.setText(entry.getLabel());
			newRadioButton.setId(i);
			radiogroup.addView(newRadioButton, layoutParams);
			i++;
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

		SelectionEntry selection = SelectionModelSingleton.getInstance()
				.getSelectionEntryById(selectionId);
		
		if (selection.getType().equals(ModelTypeEnum.WARJACK)) {
			List<JackCommander> modelsWithThisJack = SelectionModelSingleton.getInstance().warjackDeletionChoices(selection.getId());
			SelectionModelSingleton.getInstance().removeWarjack(selection.getId(), modelsWithThisJack.get(selectedEntry));
		}
		
		
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		this.finish();
	}

}
