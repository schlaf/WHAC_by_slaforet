/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.selectlist.selection.SelectionSolo;

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
public class ChooseDismountOptionActivity extends Activity implements OnCheckedChangeListener {

	public static final int CHOOSE_DISMOUNT_OPTIONS_DIALOG = 808;
	public static final String INTENT_SOLO_ID = "solo_id";
	public static final String INTENT_DISMOUNT_OPTION = "dismount_option";

	private String soloId;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.dialog_unit_options);
	    
	    soloId = getIntent().getStringExtra(INTENT_SOLO_ID);
	    SelectionSolo soloModel = (SelectionSolo) SelectionModelSingleton.getInstance().getSelectionEntryById(soloId);
	    
	    setTitle("Choose dragoon option" );
	    
	    TextView tv = (TextView) findViewById(R.id.textView1);
	    tv.setText(soloModel.getFullLabel());
	    
	    RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroupUnitSize);
	    
	    RadioButton rbMin = (RadioButton) findViewById(R.id.radioButtonMin);
	    RadioButton rbMax = (RadioButton) findViewById(R.id.radioButtonMax);
	    
	    rbMin.setText("basic (" + soloModel.getBaseCost() + " PC)");
	    rbMin.setChecked(false);
	    rbMax.setText("with dismount option (" + soloModel.getDismountCost() + " PC)");
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
		boolean dismount = false;
		switch(selectedColumnId) {
		case R.id.radioButtonMin : dismount = false; break; 
		case R.id.radioButtonMax : dismount = true; break;
		default : ;
		}
		
		
		Intent intent = new Intent();
		intent.putExtra(INTENT_SOLO_ID, soloId );
		intent.putExtra(INTENT_DISMOUNT_OPTION, dismount);

		setResult(RESULT_OK, intent);
		this.finish();

	}	
	
}
