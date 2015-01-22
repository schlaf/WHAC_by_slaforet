package com.schlaf.steam.activities.dice;

import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.schlaf.steam.R;

public class DiceRollFragment extends DialogFragment {

	private static String[] diceValues = new String[] { "2D", "3D", "4D", "5D", "6D"};
	private static String[] discardValues = new String[] { "0D", "1D", "2D"};
	private static String[] scoresValues = new String[] { "3+","4+","5+","6+","7+","8+","9+",
		"10+","11+","12+","13+","14+","15+",
		"16+","17+","18+","19+","20+"};
	
	DecimalFormat formatter = new DecimalFormat("00.0'%'");

	
	Button calculateButton;
	TextView textViewPercentageHit;
	TextView textViewPercentageCritical;
	Spinner dices; 
	Spinner dicesDiscard; 
	Spinner spinnerScores;
	Spinner spinnerDiscardMethod;
	
	private int nbDices;
	private int nbDiscard;
	DiceRoller.RemoveStrategy strategy;
	private int score;
	
	private DiceRoller roller = new DiceRoller();
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d("DiceRollFragment", "onCreateDialog");

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dice_title);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View view = inflater.inflate(R.layout.dice_roll_fragment, null);

		calculateButton = (Button) view.findViewById(R.id.buttonCalculate);
		textViewPercentageHit = (TextView) view.findViewById(R.id.textViewPercentageHit);
		textViewPercentageCritical = (TextView) view.findViewById(R.id.textViewPercentageCritical);
		
		dices = (Spinner) view.findViewById(R.id.spinnerDices);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, diceValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dices.setAdapter(adapter);
		dices.setSelection(0);
		
		dicesDiscard = (Spinner) view.findViewById(R.id.spinnerDiscardDices);
		ArrayAdapter<String> adapterDiscard = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, discardValues);
		adapterDiscard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dicesDiscard.setAdapter(adapterDiscard);
		dicesDiscard.setSelection(0);
		
		spinnerScores  = (Spinner) view.findViewById(R.id.spinnerScores);
		ArrayAdapter<String> adapterScores = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, scoresValues);
		adapterScores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerScores.setAdapter(adapterScores);
		
		spinnerDiscardMethod = (Spinner) view.findViewById(R.id.spinnerDiscardMethod);
		ArrayAdapter<String> adapterMethod = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1, new String[] {getResources().getString(R.string.lowest), getResources().getString(R.string.biggest)} );
		adapterMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDiscardMethod.setAdapter(adapterMethod);
		
		spinnerDiscardMethod.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				if (position == 0) {
					strategy = DiceRoller.RemoveStrategy.REMOVE_LOWEST;
				} else {
					strategy = DiceRoller.RemoveStrategy.REMOVE_BIGGEST;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				strategy = DiceRoller.RemoveStrategy.REMOVE_NONE;
			}
		});
		

		
		calculateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (dices.getSelectedItemPosition() != AbsListView.INVALID_POSITION) {
					int p =  dices.getSelectedItemPosition();
					nbDices = p + 2;
				}
				
				if (dicesDiscard.getSelectedItemPosition() != AbsListView.INVALID_POSITION) {
					int p =  dicesDiscard.getSelectedItemPosition();
					nbDiscard = p ;
				}
				
				if (spinnerScores.getSelectedItemPosition() != AbsListView.INVALID_POSITION) {
					int p =  spinnerScores.getSelectedItemPosition();
					score = p + 3 ;
				}
				
				if (nbDices > 0 && nbDiscard < nbDices && score >= 3) {
					roller.setThreshold(score);
					roller.rollDices(nbDices, nbDiscard, strategy);
					
					textViewPercentageHit.setText(formatter.format(roller.getPercentageHit()));
					textViewPercentageCritical.setText(formatter.format(roller.getPercentageCritical()));
				}
				
			}
		});


		builder.setView(view);
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
}
