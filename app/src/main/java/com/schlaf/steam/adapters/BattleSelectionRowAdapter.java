/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.schlaf.steam.R;
import com.schlaf.steam.storage.BattleListOrDirectory;

/**
 * adapteur pour présentation des entrée de liste pour sélection d'armée
 * 
 * @author S0085289
 * 
 */
public class BattleSelectionRowAdapter extends ArrayAdapter<BattleListOrDirectory> {

	private final List<BattleListOrDirectory> list;
	private final Activity context;

	public BattleSelectionRowAdapter(Activity context,
			List<BattleListOrDirectory> list) {
		super(context, R.layout.battle_list_selection, list);
		this.context = context;
		this.list = list;
	}

	@Override
	  public int getItemViewType(int position) {
		  return list.get(position).getType().ordinal();
	  }

	@Override
	public int getViewTypeCount() {
		// one for folders, 1 for folder creation, 1 for list descriptor
		return BattleListOrDirectory.TYPES.values().length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflator = context.getLayoutInflater();
		return list.get(position).getView(convertView, parent, inflator);
		
//		if (convertView == null) {
//			LayoutInflater inflator = context.getLayoutInflater();
//			view = inflator.inflate(R.layout.battle_list_selection, null);
//			final ViewHolder viewHolder = new ViewHolder();
//			viewHolder.title = (TextView) view.findViewById(R.id.army_title);
//			viewHolder.description1 = (TextView) view.findViewById(R.id.army_description);
//			viewHolder.description2 = (TextView) view.findViewById(R.id.army_description2);
//			viewHolder.imageFaction1 = (ImageView) view.findViewById(R.id.icon);
//			viewHolder.imageFaction2 = (ImageView) view.findViewById(R.id.icon2);
//			viewHolder.deleteButton = (ImageButton) view
//					.findViewById(R.id.buttonDelete);
//			view.setTag(viewHolder);
//			viewHolder.deleteButton.setTag(descriptor);
//		} else {
//			view = convertView;
//		}
//
//		ViewHolder holder = (ViewHolder) view.getTag();
//		holder.title.setText(descriptor.getTitle());
//		holder.description1.setText(descriptor.getFaction1Description());
//		holder.imageFaction1.setImageResource(descriptor.getFaction1().getLogoResource());
//		
//		if (descriptor.isTwoPlayers()) {
//			holder.description2.setVisibility(View.VISIBLE);
//			holder.imageFaction2.setVisibility(View.VISIBLE);
//			
//			holder.description2.setText(descriptor.getFaction2Description());
//			holder.imageFaction2.setImageResource(descriptor.getFaction2().getLogoResource());
//			
//		} else {
//			holder.description2.setVisibility(View.GONE);
//			holder.imageFaction2.setVisibility(View.GONE);
//		}
//		
//		holder.deleteButton.setFocusable(false);
//		holder.deleteButton.setTag(list.get(position));
//
//		holder.deleteButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				BattleListDescriptor battle = (BattleListDescriptor) v.getTag();
//				((BattleSelector) BattleSelectionRowAdapter.this.context).deleteExistingBattle(battle);
//			}
//		});
//
//		return view;
	}

}
