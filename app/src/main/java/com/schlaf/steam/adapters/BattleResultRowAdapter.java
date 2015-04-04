/**
 * 
 */
package com.schlaf.steam.adapters;

import java.text.DateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleResult;
import com.schlaf.steam.activities.battleresult.BattleResultsActivity;
import com.schlaf.steam.data.FactionNamesEnum;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * @author S0085289
 *
 */
public class BattleResultRowAdapter extends ArrayAdapter<BattleResult> {
	
	  private final Context context;
	  private final List<BattleResult> results;

	  public BattleResultRowAdapter(Context context,  List<BattleResult> results) {
	    super(context, R.layout.battle_result_selection, results);
	    this.context = context;
	    this.results = results;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    if (convertView == null) {
		    convertView = inflater.inflate(R.layout.battle_result_selection, parent, false);
	    }
	    
	    BattleResult result = results.get(position);
	    
	    TextView player2NameTv = (TextView) convertView.findViewById(R.id.player2Name);
	    TextView description = (TextView) convertView.findViewById(R.id.army_description);
	    TextView battleDate = (TextView) convertView.findViewById(R.id.battleDate);
	    ImageView imageView1 = (ImageView) convertView.findViewById(R.id.icon1);
	    ImageView imageView2 = (ImageView) convertView.findViewById(R.id.icon2);
	    ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
	    ImageView imageViewVictory = (ImageView) convertView.findViewById(R.id.victoryStatusImg);
	    
	    player2NameTv.setText("vs. " + result.getPlayer2name());
	    description.setText( result.getDescription());
	    battleDate.setText(result.getBattleDate());
	    
	    imageView.setImageResource(FactionNamesEnum.getFaction(result.getArmy1().getFactionId()).getLogoResource());
	    if (result.getArmy2() == null) {
	    	imageView1.setVisibility(View.GONE);
	    	imageView2.setVisibility(View.GONE);
	    	imageView.setImageResource(FactionNamesEnum.getFaction(result.getArmy1().getFactionId()).getLogoResource());
	    	imageView.setVisibility(View.VISIBLE);
	    } else {
	    	imageView.setImageResource(FactionNamesEnum.getFaction(result.getArmy2().getFactionId()).getLogoResource());
	    	imageView.setVisibility(View.GONE);
	    	imageView1.setVisibility(View.VISIBLE);
	    	imageView2.setVisibility(View.VISIBLE);
	    	imageView1.setImageResource(FactionNamesEnum.getFaction(result.getArmy1().getFactionId()).getLogoResource());
	    	imageView2.setImageResource(FactionNamesEnum.getFaction(result.getArmy2().getFactionId()).getLogoResource());
	    }

	    if (result.getWinnerNumber() == BattleResult.PLAYER_1_WINS) {
	    	imageViewVictory.setImageResource(R.drawable.victory); 
	    } else {
	    	imageViewVictory.setImageResource(R.drawable.defeat);
	    }
	    
	    ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.buttonDelete);
	    convertView.setTag(results.get(position));
		deleteButton.setTag(results.get(position));
		deleteButton.setFocusable(false);

		  deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((BattleResultsActivity) context).deleteBattleResult((BattleResult) v.getTag());
				}
			});
		
	    return convertView;
	  }
	  
	
}
