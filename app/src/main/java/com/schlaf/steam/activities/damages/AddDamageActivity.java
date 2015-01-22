/**
 * 
 */
package com.schlaf.steam.activities.damages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.schlaf.steam.R;

/**
 * @author S0085289
 *
 */
public class AddDamageActivity extends Activity {

	public static final String INTENT_VALUE_DMG = "dmg";
	public static final String INTENT_VALUE_COLUMN = "column";
	
	private int damageValue = 1 ;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setTitle("Apply Damages");
	    setContentView(R.layout.dialog_damages);
	    ((EditText) findViewById(R.id.damageValue)).setText(String.valueOf(damageValue));
	}

	public void plus(View view) {
		damageValue ++;
		((EditText) findViewById(R.id.damageValue)).setText(String.valueOf(damageValue));
	}

	public void less(View view) {
		if (damageValue > 1) {
			damageValue --;
		}
		((EditText) findViewById(R.id.damageValue)).setText(String.valueOf(damageValue));
	}

	
	public void commit(View view) {
		
		int selectedColumnId = ((RadioGroup) findViewById(R.id.radioGroup1)).getCheckedRadioButtonId();
		int idColumn = 0;
		switch(selectedColumnId) {
		case R.id.radioButton1 : idColumn = 1; break; 
		case R.id.radioButton2 : idColumn = 2; break;
		case R.id.radioButton3 : idColumn = 3; break;
		case R.id.radioButton4 : idColumn = 4; break;
		case R.id.radioButton5 : idColumn = 5; break;
		case R.id.radioButton6 : idColumn = 6; break;
		default : ;
		}
		
		Intent intent = new Intent();
		intent.putExtra(INTENT_VALUE_COLUMN, idColumn );
		intent.putExtra(INTENT_VALUE_DMG, damageValue);
		setResult(RESULT_OK, intent);
		this.finish();
	}
	
	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		this.finish();
	}
	
}
