/**
 * 
 */
package com.schlaf.steam.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.collection.CollectionEntry;
import com.schlaf.steam.activities.collection.CollectionSingleton;
import com.schlaf.steam.data.ArmyElement;
import com.schlaf.steam.data.ArmySingleton;

/**
 * adapteur pour présentation des entrée de collection d'armée
 * 
 * @author S0085289
 * 
 */
public class CollectionEntryRowAdapter extends ArrayAdapter<CollectionEntry> {

	class ViewHolder {
		protected String id;
		protected TextView title;
		protected TextView numberOwned;
		protected TextView numberPainted;
		
		protected int owned = 0;
		protected int painted = 0;
		
		protected void updateOwnedValue() {
			if (owned < 0) { owned = 0; };
			numberOwned.setText(String.valueOf(owned));
			if (painted > owned) {painted = owned;};
			numberPainted.setText(String.valueOf(painted));
			CollectionSingleton.getInstance().getOwnedMap().put(id, Integer.valueOf(owned));
			CollectionSingleton.getInstance().getPaintedMap().put(id, Integer.valueOf(painted));
			
		}
		
		protected void updatePaintedValue() {
			if (painted < 0) { painted = 0; };
			if (painted > owned) {owned = painted;};
			numberPainted.setText(String.valueOf(painted));
			numberOwned.setText(String.valueOf(owned));
			CollectionSingleton.getInstance().getOwnedMap().put(id, Integer.valueOf(owned));
			CollectionSingleton.getInstance().getPaintedMap().put(id, Integer.valueOf(painted));
		}
	}

	private final List<CollectionEntry> list;
	private final Activity context;

	public CollectionEntryRowAdapter(Activity context,
			List<CollectionEntry> list) {
		super(context, R.layout.collection_row, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		
		CollectionEntry descriptor = list.get(position);
		
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.collection_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			 
			viewHolder.title = (TextView) view.findViewById(R.id.army_title);
			viewHolder.numberOwned = (TextView) view.findViewById(R.id.numberOwned);
			viewHolder.numberPainted = (TextView) view.findViewById(R.id.numberPainted);
			
			Button ownedMore = (Button) view.findViewById(R.id.buttonmore_owned);
			ownedMore.setTag(viewHolder);
			ownedMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((ViewHolder) v.getTag()).owned ++;
					((ViewHolder) v.getTag()).updateOwnedValue();
				}
			});
			Button ownedLess = (Button) view.findViewById(R.id.buttonless_owned);
			ownedLess.setTag(viewHolder);
			ownedLess.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((ViewHolder) v.getTag()).owned --;
					((ViewHolder) v.getTag()).updateOwnedValue();
				}
			});
			
			
			Button paintedMore = (Button) view.findViewById(R.id.buttonmore_painted);
			paintedMore.setTag(viewHolder);
			paintedMore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((ViewHolder) v.getTag()).painted ++;
					((ViewHolder) v.getTag()).updatePaintedValue();
				}
			});
			Button paintedLess = (Button) view.findViewById(R.id.buttonless_painted);
			paintedLess.setTag(viewHolder);
			paintedLess.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((ViewHolder) v.getTag()).painted --;
					((ViewHolder) v.getTag()).updatePaintedValue();
				}
			});
			
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		
		ArmyElement armyElement = ArmySingleton.getInstance().getArmyElement(descriptor.getId());
		holder.id = descriptor.getId();
		holder.title.setText(armyElement.getFullName());
		
		holder.numberOwned.setFocusable(true);
		holder.numberOwned.setTag(list.get(position));
		holder.numberOwned.setText("0");
		holder.numberPainted.setFocusable(true);
		holder.numberPainted.setText("0");
		
		
		Integer ownedCount = CollectionSingleton.getInstance().getOwnedMap().get(descriptor.getId());
		if (ownedCount == null) {
			holder.owned = 0;
		} else {
			holder.owned = ownedCount.intValue(); 
		}
		// holder.updateOwnedValue(); <-- causes painted value to 0 since painted not yet set!
		
		Integer paintedCount = CollectionSingleton.getInstance().getPaintedMap().get(descriptor.getId());
		if (paintedCount == null) {
			holder.painted = 0;
		} else {
			holder.painted = paintedCount.intValue(); 
		}
		holder.updatePaintedValue();
		
		return view;
	}

}
