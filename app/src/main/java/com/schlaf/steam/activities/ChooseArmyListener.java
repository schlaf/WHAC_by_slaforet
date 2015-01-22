package com.schlaf.steam.activities;

import com.schlaf.steam.storage.ArmyListDescriptor;
import com.schlaf.steam.storage.ArmyListDirectory;

public interface ChooseArmyListener {
	public void onArmyListSelected(ArmyListDescriptor army);

	public void onArmyDirectoryDeleted(ArmyListDirectory directory);

	public void onArmyListDeleted(ArmyListDescriptor army);
}
