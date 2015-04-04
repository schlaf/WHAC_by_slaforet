package com.schlaf.steam.activities.battleresult;

import java.text.DateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.FactionNamesEnum;
import com.schlaf.steam.storage.StorageManager;

public class BattleResultDetailFragment extends Fragment {

	public static final String ID = "BattleResultDetailFragment";
	public static final String TAG = "BattleResultDetailFragment";
	
	private BattleResult currentResult;
	
	public interface ViewBattleResultActivityInterface {
		
		public BattleResult getBattleResult();
		
		/**
		 * card should be opened in new window
		 * @param v
		 */
		public void viewBattleResultInNewActivity(View v);
		
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof ViewBattleResultActivityInterface) {
			Log.d(TAG, "onAttach received " + activity.getClass().getName());
		} else {
			throw new UnsupportedOperationException("BattleResultDetailFragment requires a ViewBattleResultActivityInterface as parent activity");
		}
		super.onAttach(activity);
		
		//fillResult( ((ViewBattleResultActivityInterface) getActivity()).getBattleResult());
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

        if (! ArmySingleton.getInstance().isFullyLoaded()) {
            return;
        }


		currentResult = ((ViewBattleResultActivityInterface) getActivity()).getBattleResult();
		fillResult( ((ViewBattleResultActivityInterface) getActivity()).getBattleResult());
	}

	private void fillResult(BattleResult result) {
		View view = getView();
		
	    TextView player2NameTv = (TextView) view.findViewById(R.id.player2Name);
	    TextView description = (TextView) view.findViewById(R.id.army_description);
	    TextView date = (TextView) view.findViewById(R.id.datePlayed);
	    TextView clock = (TextView) view.findViewById(R.id.clockType);
	    TextView scenario = (TextView) view.findViewById(R.id.scenarioType);
	    TextView victory = (TextView) view.findViewById(R.id.victoryType);
	    EditText notes = (EditText) view.findViewById(R.id.editTextNotes);
	    TextView armyContent = (TextView )  view.findViewById(R.id.armyContent);
	    LinearLayout opponentArmyLayout = (LinearLayout) view.findViewById(R.id.opponentArmyLayout);
	    TextView opponentArmyTitle = (TextView) view.findViewById(R.id.opponentArmyTitle);
	    TextView armyContent2 = (TextView )  view.findViewById(R.id.armyContent2);
	    
	    
	    
	    ImageView imageView = (ImageView) view.findViewById(R.id.icon);
	    ImageView imageView2 = (ImageView) view.findViewById(R.id.icon2);
	    ImageView imageViewVictory = (ImageView) view.findViewById(R.id.victoryStatusImg);
	    
	    player2NameTv.setText("VS. " + result.getPlayer2name());
	    description.setText( result.getDescription());
	    
	    imageView.setImageResource(FactionNamesEnum.getFaction(result.getArmy1().getFactionId()).getLogoResource());
	    if (result.getArmy2() != null) {
	    	imageView2.setImageResource(FactionNamesEnum.getFaction(result.getArmy2().getFactionId()).getLogoResource());
	    	armyContent2.setText(Html.fromHtml(result.getArmy2().getHTMLDescription()));
	    } else {
	    	opponentArmyLayout.setVisibility(View.GONE);
	    	imageView2.setVisibility(View.GONE);
	    	armyContent2.setVisibility(View.GONE);
	    	opponentArmyTitle.setVisibility(View.GONE);
	    }

	    if (result.getWinnerNumber() == BattleResult.PLAYER_1_WINS) {
	    	imageViewVictory.setImageResource(R.drawable.victory); 
	    } else {
	    	imageViewVictory.setImageResource(R.drawable.defeat);
	    }
	    
	    date.setText(result.getBattleDate());
	    clock.setText(result.getClockType());
	    scenario.setText(result.getScenario());
	    victory.setText(result.getVictoryCondition());
	    notes.setText(result.getNotes());
	    armyContent.setText(Html.fromHtml(result.getArmy1().getHTMLDescription()));
	    
	    
	    
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		
		View view = inflater.inflate(R.layout.battle_result_fragment,
				container, false);

		return view;		
	}

	@Override
	public void onPause() {
		super.onPause();
		// save notes
		EditText notes = (EditText) getView().findViewById(R.id.editTextNotes);
		String notesValue = notes.getText().toString();
		
		if (! notesValue.equals(currentResult.getNotes())) {
			currentResult.setNotes(notesValue);
			StorageManager.saveBattleResult(getActivity(), currentResult, true);
		}
		
	}
}
