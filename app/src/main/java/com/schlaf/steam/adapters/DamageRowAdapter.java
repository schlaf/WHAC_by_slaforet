/**
 * 
 */
package com.schlaf.steam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.damages.SelectableDamageValue;

/**
 * classe permettant de mapper une entrée de DOMMAGES dans une liste de sélection
 * 
 * @author S0085289
 * 
 */
public class DamageRowAdapter extends ArrayAdapter<SelectableDamageValue> {

	private final Context context;
	private final SelectableDamageValue[] damages;

	public DamageRowAdapter(Context context, SelectableDamageValue[] damages) {
		super(context, R.layout.damage_selection, damages);
		this.context = context;
		this.damages = damages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.damage_selection, parent, false);
		}
		
		// RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.contentDamage);
		TextView textView = (TextView) convertView.findViewById(R.id.textView1);

		textView.setText(damages[position].getDamageString());

		if (damages[position].isChecked()) {
			textView.setBackgroundResource(R.drawable.btn_keyboard_key_trans_pressed_on);
		} else {
			textView.setBackgroundResource(R.drawable.btn_keyboard_key_fulltrans_normal);
		}
		
		convertView.setClickable(false);

		return convertView;
	}

}
