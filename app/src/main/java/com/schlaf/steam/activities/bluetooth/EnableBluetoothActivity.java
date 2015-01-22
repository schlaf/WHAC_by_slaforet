package com.schlaf.steam.activities.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals This class aims to be the main-launcher activity of the application.
 *        It's a simple activity that detects if Bluetooth is exists and is
 *        enable. If it exists and is not enable, then it try to enable it and
 *        after it launches the Discovery activity. If the bluetooth is not on
 *        the device or the user doesn't want to enable it, the activity just
 *        die.
 */
public class EnableBluetoothActivity extends Activity {
	/**
	 * The Bluetooth Adapter
	 */
	private BluetoothAdapter bluetoothAdapter;
	/**
	 * The unique Request code send with the Intent
	 * BluetoothAdapter.ACTION_REQUEST_ENABLE when starting the activty to
	 * enable bluetooth
	 */
	private final int REQUEST_ENABLE_BT = 11021974;
	/**
	 * A boolean to know if BlueTooth is enabled
	 */
	boolean blueToothEnable = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// settingUpBluetooth
		if (supportBluetooth()) {
			// Then Enable bluetooth
			enableBluetooth();
		} else {
			finish();
		}
	}

	/***************************************************************************************/
	/** ENABLE BlueTooth *******************************************************************/
	/***************************************************************************************/
	/**
	 * * Instanciate the bluetoothadapter *
	 * 
	 * @return true is device support bluetooth
	 */
	private boolean supportBluetooth() {
		boolean isBluetooth = false;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null) {
			// the device supports BlueTooth
			isBluetooth = true;
		}
		return isBluetooth;
	}

	/**
	 * Test if bluetooth is enabled, If the Bluetooth is not enable, then ask
	 * the user to set it enable
	 */
	private void enableBluetooth() {
		blueToothEnable = bluetoothAdapter.isEnabled();
		if (!blueToothEnable) {
			// Ask the user to set enable the Bluetooth
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			// just call the next activity
			Intent discoverDeviceIntent = new Intent(this,
					DiscoverDevicesActivity.class);
			startActivity(discoverDeviceIntent);
			// and die
			finish();
		}
	}

	/*
	 *  * (non-Javadoc) @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// The Activty ACTION_REQUEST_ENABLE returns
		if (requestCode == REQUEST_ENABLE_BT) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// The result is ok
				enableBluetooth();
				break;
			case Activity.RESULT_CANCELED:
			default:
				// the result is KO
				blueToothEnable = false;
				// and die
				finish();
				break;
			}
		}
	}


}
