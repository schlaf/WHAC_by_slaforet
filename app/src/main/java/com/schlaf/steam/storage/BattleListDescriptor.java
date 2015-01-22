package com.schlaf.steam.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.ChooseBattleListener;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.data.FactionNamesEnum;

public class BattleListDescriptor implements BattleListOrDirectory,
		Comparable<BattleListDescriptor> {

	private String filePath;
	private String filename;
	private String title;
	private FactionNamesEnum faction1;
	private String faction1Description;
	private FactionNamesEnum faction2;
	private String faction2Description;
	boolean twoPlayers = false;

	class ViewHolder {
		protected TextView title;
		protected TextView description1;
		protected TextView description2;
		protected ImageView imageFaction1;
		protected ImageView imageFaction2;
		protected ImageButton deleteButton;
	}
	
	
	public BattleListDescriptor(BattleStore store) {
		super();
		this.title = store.getTitle();
		this.filename = store.getFilename();

		ArmyStore army1 = store.getArmy(BattleSingleton.PLAYER1);
		ArmyStore army2 = store.getArmy(BattleSingleton.PLAYER2);

		this.faction1 = FactionNamesEnum.getFaction(army1.getFactionId());
		ArmyListDescriptor ald = new ArmyListDescriptor(army1, "");
		faction1Description = ald.getDescription();

		if (army2 != null) {
			this.faction2 = FactionNamesEnum.getFaction(army2.getFactionId());
			ArmyListDescriptor ald2 = new ArmyListDescriptor(army2, "");
			faction2Description = ald2.getDescription();
			twoPlayers = true;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(title).append(" (").append(faction1Description).append(")");
		if (faction2Description != null) {
			sb.append(" -- (").append(faction2Description).append(")");
		}

		return sb.toString();
	}

	private int getOrderingOffsetTwoPlayer() {
		if (isTwoPlayers()) {
			return -10000;
		}
		return 0;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		StringBuffer sb = new StringBuffer();

		return sb.toString();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FactionNamesEnum getFaction1() {
		return faction1;
	}

	public String getFaction1Description() {
		return faction1Description;
	}

	public FactionNamesEnum getFaction2() {
		return faction2;
	}

	public String getFaction2Description() {
		return faction2Description;
	}

	public boolean isTwoPlayers() {
		return twoPlayers;
	}

	@Override
	public int compareTo(BattleListDescriptor another) {
		int firstCompare = getOrderingOffsetTwoPlayer()
				- another.getOrderingOffsetTwoPlayer()
				+ faction1.compareTo(another.getFaction1());
		if (firstCompare == 0) {
			return title.compareTo(another.getTitle());
		} else {
			return firstCompare;
		}
	}

	@Override
	public TYPES getType() {
		return TYPES.BATTLE;
	}

	@Override
	public View getView(View convertView, ViewGroup parent,
			LayoutInflater inflater) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.battle_list_selection, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.army_title);
			viewHolder.description1 = (TextView) convertView
					.findViewById(R.id.army_description);
			viewHolder.description2 = (TextView) convertView
					.findViewById(R.id.army_description2);
			viewHolder.imageFaction1 = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.imageFaction2 = (ImageView) convertView
					.findViewById(R.id.icon2);
			viewHolder.deleteButton = (ImageButton) convertView
					.findViewById(R.id.buttonDelete);
			convertView.setTag(viewHolder);
		} 

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.title.setText(getTitle());
		holder.description1.setText(getFaction1Description());
		holder.imageFaction1.setImageResource(getFaction1().getLogoResource());

		if (isTwoPlayers()) {
			holder.description2.setVisibility(View.VISIBLE);
			holder.imageFaction2.setVisibility(View.VISIBLE);

			holder.description2.setText(getFaction2Description());
			holder.imageFaction2.setImageResource(getFaction2()
					.getLogoResource());

		} else {
			holder.description2.setVisibility(View.GONE);
			holder.imageFaction2.setVisibility(View.GONE);
		}

		holder.deleteButton.setFocusable(false);
		holder.deleteButton.setTag(this);

		holder.deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BattleListDescriptor battle = (BattleListDescriptor) v.getTag();
				((ChooseBattleListener) v.getContext()).onBattleDeleted(battle);
			}
		});

		return convertView;

	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
