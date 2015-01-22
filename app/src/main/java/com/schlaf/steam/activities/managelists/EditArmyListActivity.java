package com.schlaf.steam.activities.managelists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.schlaf.steam.R;
import com.schlaf.steam.storage.StorageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * classe d'édition d'une liste d'armée sauvée. <br>
 * options : save/clone/delete 
 * @author S0085289
 *
 */
public class EditArmyListActivity extends Activity {

	public static int EDIT_ARMY_NAME_DIALOG = 453;
	public static String INTENT_ARMY_FILENAME = "army_filename";

	private String initialFileName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTitle("Edit existing army list");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.manage_army_list);

		EditText armyNameTextField = (EditText) findViewById(R.id.army_name);

		String fileName = getIntent().getStringExtra(INTENT_ARMY_FILENAME);

		initialFileName = fileName;

		armyNameTextField.setText(fileName);

		super.onCreate(savedInstanceState);
	}

	public void rename(View v) {

		EditText armyNameTextField = (EditText) findViewById(R.id.army_name);
		String newFileName = armyNameTextField.getText().toString();

		if (initialFileName.equals(newFileName)) {
			Toast.makeText(getApplicationContext(), "Same filename; army no renamed",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			this.finish();			
		}
		
		if (StorageManager.renameArmyList(getApplicationContext(), initialFileName, newFileName)) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			Toast.makeText(getApplicationContext(), "Army renamed",
					Toast.LENGTH_SHORT).show();
			this.finish();
		} else {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			Toast.makeText(getApplicationContext(), "Army rename failed",
					Toast.LENGTH_SHORT).show();

			this.finish();
		}
		
	}

	public void copyAsNew(View v) {

		EditText armyNameTextField = (EditText) findViewById(R.id.army_name);

		Intent intent = new Intent();

		if ( StorageManager.copyArmyList(getApplicationContext(), initialFileName , armyNameTextField.getText().toString())) {
			setResult(RESULT_OK, intent);
			this.finish();
		} else {
			Toast.makeText(getApplicationContext(), "Copy army failed",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void delete(View v) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Delete army?");
		alert.setMessage("you'll lose this army data");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditArmyListActivity.this.deleteArmyfile();
			}

		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						Toast.makeText(getApplicationContext(),
								"Army not deleted", Toast.LENGTH_SHORT).show();
					}
				});

		alert.show();

	}

	private void deleteArmyfile() {
		
		if (StorageManager.deleteArmyList(getApplicationContext(), initialFileName)) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			Toast.makeText(getApplicationContext(), "Army deleted",
					Toast.LENGTH_SHORT).show();
			this.finish();
		} else {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			Toast.makeText(getApplicationContext(), "Army deletion failed",
					Toast.LENGTH_SHORT).show();

			this.finish();
		}
	}

}
