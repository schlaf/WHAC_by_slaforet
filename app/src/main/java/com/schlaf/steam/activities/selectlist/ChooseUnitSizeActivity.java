/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selection.SelectionUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author S0085289
 *
 */
public class ChooseUnitSizeActivity extends Activity implements OnCheckedChangeListener {

	public static final int CHOOSE_UNIT_OPTIONS_DIALOG = 804;
	public static final String INTENT_UNIT_ID = "unit_id";
	public static final String INTENT_MIN_SIZE = "min_size";

	private String unitId;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.dialog_unit_options);
	    
	    unitId = getIntent().getStringExtra(INTENT_UNIT_ID);
	    SelectionUnit unitModel = (SelectionUnit) SelectionModelSingleton.getInstance().getSelectionEntryById(unitId);
	    
	    setTitle(R.string.choose_unit_size );
	    
	    TextView tv = (TextView) findViewById(R.id.textView1);
	    tv.setText(unitModel.getFullLabel());
	    
	    RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroupUnitSize);
	    
	    RadioButton rbMin = (RadioButton) findViewById(R.id.radioButtonMin);
	    RadioButton rbMax = (RadioButton) findViewById(R.id.radioButtonMax);
	    
	    rbMin.setText(unitModel.getMinSize() + getResources().getString(R.string._models_) + unitModel.getMinCost() + " PC)");
	    rbMin.setChecked(false);
	    rbMax.setText(unitModel.getMaxSize() + getResources().getString(R.string._models_) + unitModel.getMaxCost() + " PC)");
	    rbMax.setChecked(false);
	    
	    radiogroup.setOnCheckedChangeListener(this);
	}

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		this.finish();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		int selectedColumnId = ((RadioGroup) findViewById(R.id.radioGroupUnitSize)).getCheckedRadioButtonId();
		boolean minSize = true;
		switch(selectedColumnId) {
		case R.id.radioButtonMin : minSize = true; break; 
		case R.id.radioButtonMax : minSize = false; break;
		default : ;
		}
		
		
		Intent intent = new Intent();
		intent.putExtra(INTENT_UNIT_ID, unitId );
		intent.putExtra(INTENT_MIN_SIZE, minSize );

		setResult(RESULT_OK, intent);
		this.finish();

	}	
	
}
