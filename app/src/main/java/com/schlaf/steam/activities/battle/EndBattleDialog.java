/**
 * 
 */
package com.schlaf.steam.activities.battle;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.PreferenceConstants;

/**
 * @author S0085289
 * 
 */
public class EndBattleDialog extends DialogFragment {

	public interface EndBattleListener {
		public void endBattle(int winnerNumber, String player2name, String clockType, String scenario, String victoryCondition, String notes);
	}
	
	private EndBattleListener mListener;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.savebattle));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// FIRE ZE MISSILES!
				
				Spinner winnerSpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerVictor);
				int winnerPosition = winnerSpinner.getSelectedItemPosition();
				
				AutoCompleteTextView player2Text = (AutoCompleteTextView) getDialog().findViewById(R.id.editTextPlayer2name);
				String player2name = player2Text.getText().toString();
				if (player2name == null || player2name.length() == 0 || player2name.trim().length() == 0) {
					player2name = getResources().getString(R.string.unknown);
				}
				
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String players = sharedPref.getString(PreferenceConstants.OPPONENTS_LIST, ""); //$NON-NLS-1$

				ArrayList<String> opponents = new ArrayList<String>();
				StringTokenizer stk = new StringTokenizer(players, ";"); //$NON-NLS-1$
				while (stk.hasMoreTokens()) {
					String opponent = stk.nextToken();
					opponents.add(opponent);
				}
				
				if ( ! opponents.contains(player2name) ) {
					if (players != null && players.length() > 0) {
						players = players + ";" + player2name;	 //$NON-NLS-1$
					} else {
						players = player2name;
					}
					
					Editor editor = sharedPref.edit();
					editor.putString(PreferenceConstants.OPPONENTS_LIST, players);
					editor.commit();
				}

				
						
				Spinner clockSpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerUserClock);
				String clock = (String) clockSpinner.getSelectedItem();
				
				Spinner scenarioSpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerScenario);
				String scenario = (String) scenarioSpinner.getSelectedItem();
				
				Spinner victorySpinner = (Spinner) getDialog().findViewById(R.id.icsSpinnerVictoryCondition);
				String victory = (String) victorySpinner.getSelectedItem();
				
				EditText notesText = (EditText) getDialog().findViewById(R.id.editTextNotes);
				String notes = notesText.getText().toString();
				
				
				mListener.endBattle(winnerPosition, player2name, clock, scenario, victory, notes);
			}
		});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View result = inflater.inflate(R.layout.end_battle_dialog, null);
		// Create the AlertDialog object and return it
		
		AutoCompleteTextView player2text = (AutoCompleteTextView) result.findViewById(R.id.editTextPlayer2name);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String players = sharedPref.getString(PreferenceConstants.OPPONENTS_LIST, ""); //$NON-NLS-1$

		ArrayList<String> opponents = new ArrayList<String>();
		StringTokenizer stk = new StringTokenizer(players, ";"); //$NON-NLS-1$
		while (stk.hasMoreTokens()) {
			String opponent = stk.nextToken();
			opponents.add(opponent);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, opponents);

		player2text.setAdapter(adapter);
		player2text.setThreshold(1); // activate at first char...

		builder.setView(result);
		
		return builder.create();
	}
	
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EndBattleListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EndBattleListener"); //$NON-NLS-1$
        }
    }

	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
	}



}
