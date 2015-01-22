package com.schlaf.steam.activities;

import com.schlaf.steam.storage.BattleListDescriptor;
import com.schlaf.steam.storage.BattleListDirectory;

public interface ChooseBattleListener {
	public void onBattleSelected(BattleListDescriptor battle);

	public void onBattleDeleted(BattleListDescriptor battle);

	public void onBattleDirectoryDeleted(final BattleListDirectory directory);
}
