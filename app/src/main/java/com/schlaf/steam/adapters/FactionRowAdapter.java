/**
 * 
 */
package com.schlaf.steam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.data.Faction;
import com.schlaf.steam.data.FactionNamesEnum;

/**
 * classe permettant de mapper une entrée de faction dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class FactionRowAdapter extends ArrayAdapter<Faction> {

	private final Context context;
	private final Faction[] factions;

	public FactionRowAdapter(Context context, Faction[] factions) {
		super(context, R.layout.faction_selection, factions);
		this.context = context;
		this.factions = factions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.faction_selection, parent,
					false);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.def_arm_label);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		textView.setText(factions[position].getFullName());
		// Change the icon for Windows and iPhone
		// Faction.GameSystem system = factions[position].getSystem();
		// if (system.equals(Faction.GameSystem.WARMACHINE)) {
		// imageView.setImageResource(R.drawable.khador);
		// } else {
		// imageView.setImageResource(R.drawable.hordes);
		// }

		imageView.setImageResource(FactionNamesEnum.getFaction(
				factions[position].getId()).getLogoResource());

		return convertView;
	}

}
