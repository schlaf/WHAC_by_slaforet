/**
 * 
 */
package com.schlaf.steam.activities.chrono;

import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.chrono.ChronoFragment.ChronoActivityInterface;

/**
 * @author S0085289
 * 
 */
public class ChronoConfigDialog extends DialogFragment {

	ChronoActivityInterface listener;
	
	Spinner hourPicker ;
	Spinner minutePicker; 

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("ChronoConfigDialog", "onCreateDialog");
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.chrono_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				String hours = (String) hourPicker.getSelectedItem();
				String minutes = (String) minutePicker.getSelectedItem();
				
				int hoursValue = hours!=null?Integer.valueOf(hours):0;
				int minutesValue = minutes!=null?Integer.valueOf(minutes):0;
				
				int minutesTotal = hoursValue * 60 + minutesValue; 
				listener.setInitialMinuteCount(minutesTotal);
			}
		});

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.chrono_dialog_config_spinner_fragment,
				null);

		hourPicker = (Spinner) view.findViewById(R.id.pickerHour);
		minutePicker = (Spinner) view.findViewById(R.id.pickerMinutes);
		
		
		List<String> hoursList = new ArrayList<String>();
		hoursList.add("0");
		hoursList.add("1");
		hoursList.add("2");
		ArrayAdapter<String> adapterHour = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, hoursList);
		adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		hourPicker.setAdapter(adapterHour);
		
		List<String> minutesList = new ArrayList<String>();
		for (int i = 0; i < 60; i++) {
			minutesList.add(String.valueOf(i));	
		}
		ArrayAdapter<String> adapterMinute = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, minutesList);
		adapterMinute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		minutePicker.setAdapter(adapterMinute);

		
		
//		((EditText) view.findViewById(R.id.inputMinutes)).setText("60");
//		
//		view.findViewById(R.id.button0).setOnClickListener(this);
//		view.findViewById(R.id.button1).setOnClickListener(this);
//		view.findViewById(R.id.button2).setOnClickListener(this);
//		view.findViewById(R.id.button3).setOnClickListener(this);
//		view.findViewById(R.id.button4).setOnClickListener(this);
//		view.findViewById(R.id.button5).setOnClickListener(this);
//		view.findViewById(R.id.button6).setOnClickListener(this);
//		view.findViewById(R.id.button7).setOnClickListener(this);
//		view.findViewById(R.id.button8).setOnClickListener(this);
//		view.findViewById(R.id.button9).setOnClickListener(this);
//		view.findViewById(R.id.buttonBackspace).setOnClickListener(this);
		

		builder.setView(view);
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("ChronoConfigDialog", "onAttach");
		super.onAttach(activity);
		if (activity instanceof ChronoActivityInterface) {
			listener = (ChronoActivityInterface) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement BattleListInterface");
		}

	}

}
