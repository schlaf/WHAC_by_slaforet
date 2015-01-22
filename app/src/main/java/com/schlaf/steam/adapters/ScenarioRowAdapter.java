/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.data.Mission;

/**
 * classe permettant de mapper une entrée de scénario dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class ScenarioRowAdapter extends ArrayAdapter<Mission> implements
		SpinnerAdapter {

	private final Context context;
	private final List<Mission> scenarii;

	public ScenarioRowAdapter(Context context, List<Mission> scenarii) {
		super(context, R.layout.row_scenario, scenarii);

		Collections.sort(scenarii);
        this.context = context;
        this.scenarii = scenarii;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.row_scenario, parent, false);
		}
		TextView year = (TextView) convertView
				.findViewById(R.id.sr_year);
		TextView number = (TextView) convertView
				.findViewById(R.id.scenario_number);
		TextView title = (TextView) convertView
				.findViewById(R.id.scenario_title);

		year.setText(scenarii.get(position).getType());
		number.setText(scenarii.get(position).getNumber());
		title.setText(scenarii.get(position).getName());

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.row_scenario, parent, false);
		}
		TextView year = (TextView) convertView
				.findViewById(R.id.sr_year);
		TextView number = (TextView) convertView
				.findViewById(R.id.scenario_number);
		TextView title = (TextView) convertView
				.findViewById(R.id.scenario_title);

		year.setText(scenarii.get(position).getType());
		number.setText(scenarii.get(position).getNumber());
		title.setText(scenarii.get(position).getName());

		return convertView;
	}

}
