package com.schlaf.steam.activities.battle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import android.util.Log;

import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.storage.ArmyStore;

public class BattleCommunicationObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3807958778896755860L;

	private static final transient String TAG = "BattleCommunicationObject";
	
	private UUID uniqueId;
	
	private ArmyStore armyStore;
	
	private BattleEntry battleEntry;
	
	private DamageGrid damageGrid;

	private CommAction action;
	
	/* time left on chrono, depends on CommAction.PLAYERX_PAUSE/PLAY) */
	private long timeLeft;
	
	/** message to treat bluetooth status, like communication errors */
	private String bluetoothStatusMessage;

	public BattleCommunicationObject() {
		uniqueId = UUID.randomUUID();
	}
	
	public BattleEntry getBattleEntry() {
		return battleEntry;
	}

	public void setBattleEntry(BattleEntry battleEntry) {
		this.battleEntry = battleEntry;
	}

	public CommAction getAction() {
		return action;
	}

	public void setAction(CommAction action) {
		this.action = action;
	}

	public DamageGrid getDamageGrid() {
		return damageGrid;
	}

	public void setDamageGrid(DamageGrid damageGrid) {
		this.damageGrid = damageGrid;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(action).append("-"); //$NON-NLS-1$
		if (battleEntry != null) {
			sb.append(battleEntry.toString());
		}
		if (damageGrid != null) {
			sb.append(damageGrid.toString());
		}
		return sb.toString();
	}

	public byte[] getBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(output);
			oos.writeObject(this);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "failed getBytes on BattleCommunicationObject", e);
			return new byte[0];
		}
		
		return output.toByteArray();
	}

	public long getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}

	public String getBluetoothStatusMessage() {
		return bluetoothStatusMessage;
	}

	public void setBluetoothStatusMessage(String bluetoothStatusMessage) {
		this.bluetoothStatusMessage = bluetoothStatusMessage;
	}

	public ArmyStore getArmyStore() {
		return armyStore;
	}

	public void setArmyStore(ArmyStore armyStore) {
		this.armyStore = armyStore;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

}
