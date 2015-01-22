/**
 * 
 */
package com.schlaf.steam.data;

import com.schlaf.steam.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author S0085289
 *
 */
public enum ModelTypeEnum {

	WARCASTER(R.string.warcaster),
	WARLOCK(R.string.warlock),
	WARJACK(R.string.warjacks),
	WARBEAST(R.string.warbeasts),
	UNIT(R.string.units),
	UNIT_ATTACHMENT(R.string.unit_attachments),
	WEAPON_ATTACHMENT(R.string.weapon_attachments),
	SOLO(R.string.solos),
	BATTLE_ENGINE(R.string.battle_engines),
	COLOSSAL(R.string.colossals),
	GARGANTUAN(R.string.gargantuans),
	SINGLE_MODEL_INCLUDED_ELSEWHERE(R.string.other), 
	MERCENARY_ELEMENTS(R.string.mercenaries),
	OBJECTIVE(R.string.objective);
	
	private int title;
	private static List<ModelTypeEnum> entriesTypeForCardSearch;
	
	static {
		entriesTypeForCardSearch = new ArrayList<ModelTypeEnum>();
		entriesTypeForCardSearch.add(WARCASTER);
		entriesTypeForCardSearch.add(WARLOCK);
		entriesTypeForCardSearch.add(WARJACK);
		entriesTypeForCardSearch.add(WARBEAST);
		entriesTypeForCardSearch.add(UNIT);
		entriesTypeForCardSearch.add(UNIT_ATTACHMENT);
		entriesTypeForCardSearch.add(WEAPON_ATTACHMENT);
		entriesTypeForCardSearch.add(SOLO);
		entriesTypeForCardSearch.add(BATTLE_ENGINE);
		entriesTypeForCardSearch.add(COLOSSAL);
		entriesTypeForCardSearch.add(GARGANTUAN);
	}
	
	private ModelTypeEnum(int titleId) {
		this.title = titleId;
	}
	
	public int getTitle() {
		return title;
	}
	
	public String toString() {
		return name();
	}
	
	
	public static List<ModelTypeEnum> getEntriesTypeForCardSearch() {
		return entriesTypeForCardSearch;
	}
}
