/**
 * 
 */
package com.schlaf.steam.activities.selectlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.Spinner;

import com.schlaf.steam.R;

/**
 * @author S0085289
 * 
 */
public class ChooseArmyOptionsDialog extends DialogFragment {

	public static final int CHOOSE_ARMY_OPTIONS_DIALOG = 384;
	public static final String INTENT_NB_CASTER = "nb_casters";
	public static final String INTENT_NB_POINTS = "nb_points";

	public interface ArmySettingListener {
		public void changeArmySettings(int casterCount, int pointCount);
	}
	
	private ArmySettingListener mListener;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_army_settings);
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// FIRE ZE MISSILES!
				
				Spinner warcasterCountSpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerCasterCount);
				String casterCount = (String) warcasterCountSpinner.getSelectedItem();
				int casterCountInt = Integer.parseInt(casterCount);
				
				Spinner armySizeSpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerArmySize);
				String armySize = (String) armySizeSpinner.getSelectedItem();
				int armySizeInt = Integer.parseInt(armySize);
				
				mListener.changeArmySettings(casterCountInt, armySizeInt);
			}
		});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.army_options, null));
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ArmySettingListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ArmySettingListener");
        }
    }



}
