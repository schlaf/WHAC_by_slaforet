/**
 * 
 */
package com.schlaf.steam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.ViewConfiguration;

import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.bluetooth.CommunicationService;
import com.schlaf.steam.activities.card.CardLibrarySingleton;
import com.schlaf.steam.activities.selectlist.SelectionModelSingleton;
import com.schlaf.steam.data.ArmySingleton;
import com.schlaf.steam.data.RulesSingleton;

/**
 * @author S0085289 classe de base de l'application.
 */
public class SteamPunkRosterApplication extends Application {

	private static final String TAG = "SteamPunkRosterApplication";
	
	public SteamPunkRosterApplication() {
		super();
	}

	/** player name, useful for sharing data on the internet */
	private String playerName;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/******************************************************************************************/
	/** Attributes **************************************************************************/
	/******************************************************************************************/
	/**
	 * The unique UUID of the application to etablish a connection
	 */
	public static final UUID MY_UUID = UUID
			.fromString("fa87c0e0-dfef-11de-8a39-0800200c9a66");
	/**
	 * The unique bluetooth server name use for connection
	 */
	public static final String MY_BLUETOOTH_SERVER_NAME = "WHAC_BlueToothServer";
	/**
	 * The BlueTooth socket
	 */
	private BluetoothSocket bluetoothSocket;
	/**
	 * The input stream from the socket
	 */
	private InputStream inputSocketStream = null;
	/**
	 * The output stream from the socket
	 */
	private OutputStream outputSocketStream = null;
	/**
	 * The selected device to connect with
	 */
	private BluetoothDevice remoteDevice = null;
	/**
	 * The name of this device
	 */
	private String thisDeviceName = null;
	/**
	 * The intent that launches and stops the commmunication service
	 */
	private Intent communicationServiceIntent;
	
	/** bluetooth is optional, don't activate always! */
	private boolean usesBluetooth = false;
	
	private boolean iAmTheServer = false;
	
	/******************************************************************************************/
	/** Access Every Where **************************************************************************/
	/******************************************************************************************/
	/**
	 * instance of this
	 */
	private static SteamPunkRosterApplication instance;

	
	// maintain every singleton in memory of the application
	private static SelectionModelSingleton selectionModelSingleton;
	private static BattleSingleton battleSingleton;
	private static ArmySingleton armySingleton;
	private static CardLibrarySingleton cardSingleton;
	private static RulesSingleton rulesSingleton;
	
	/**
	 * @return The instance of the application
	 */
	public static SteamPunkRosterApplication getInstance() {
		return instance;
	}

	/******************************************************************************************/
	/** Managing LifeCycle **************************************************************************/
	/******************************************************************************************/

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("MApplication:onCreate", "Application is create");
		instance = this;
		communicationServiceIntent = new Intent(getApplicationContext(),
				CommunicationService.class);
		
		
		selectionModelSingleton = SelectionModelSingleton.getInstance();
		battleSingleton = BattleSingleton.getInstance();
		armySingleton = ArmySingleton.getInstance();
		cardSingleton = CardLibrarySingleton.getInstance();
		rulesSingleton = RulesSingleton.getInstance();
		
		
		// this hack is to prevent the "overflow" button in actionbar to be hidden if there is a physical menu button
		try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
		
	}

	/******************************************************************************************/
	/** Try managing socket in/out put here **************************************************************************/
	/******************************************************************************************/
	/**
	 * @param bluetoothSocket
	 *            the bluetoothSocket to set
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public final void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
//		Log.e(TAG, "BlueToothSocket set :" + bluetoothSocket
//				+ " is connected " + bluetoothSocket.isConnected());
		// First store it
		syncBluetoothSocketModifier(true, bluetoothSocket);
		// Then get the input and output streams
		try {
			syncInputSocketStream(true,syncBluetoothSocketModifier(false, null).getInputStream());
			outputSocketStream = this.bluetoothSocket.getOutputStream();
		} catch (IOException e) {
			Log.e("MApplication:onCreate", "setBluetoothSocket", e);
		}
		// then launch the communication service
		startService();
	}

	/**
	 * Start the service
	 */
	public void startService() {
		startService(communicationServiceIntent);
	}

	/**
	 * Stop the service
	 */
	public void stopService() {
		stopService(communicationServiceIntent);
	}

	/**
	 * Ensure that modifications of the Socket is ThreadSafe
	 * 
	 * @param set
	 *            if true then this function act like a setter
	 * @param bluetoothSocket
	 *            the socket to set
	 * @return bluetoothSocket
	 */
	private synchronized BluetoothSocket syncBluetoothSocketModifier(
			boolean set, BluetoothSocket bluetoothSocket) {
		if (set) {
			this.bluetoothSocket = bluetoothSocket;
		}
		return this.bluetoothSocket;
	}

	/**
	 * Ensure that modification of the inputSocketStream is ThreadSafe
	 * 
	 * @param set
	 *            if true then this function act like a setter
	 * @param inputSocketStream
	 *            the inputSocketStream to set
	 * @return inputSocketStream
	 */
	private synchronized InputStream syncInputSocketStream(boolean set,
			InputStream inputSocketStream) {
		if (set) { 
			this.inputSocketStream = inputSocketStream;
		}
		return this.inputSocketStream;
	}

	/**
	 * Ensure that modification of the outputSocketStream is ThreadSafe
	 * 
	 * @param set
	 *            if true then this function act like a setter
	 * @param outputSocketStream
	 *            the outputSocketStream to set
	 * @return outputSocketStream
	 */
	private synchronized OutputStream syncOutputSocketStream(boolean set,
			OutputStream outputSocketStream) {
		if (set) {
			this.outputSocketStream = outputSocketStream;
		}
		return this.outputSocketStream;
	}

	/**
	 * @return the inputSocketStream
	 */
	public final InputStream getInputSocketStream() {
		return syncInputSocketStream(false, null);
	}

	/**
	 * @return the outputSocketStream
	 */
	public final OutputStream getOutputSocketStream() {
		return syncOutputSocketStream(false, null);
	}

	/**
	 * @return the bluetoothSocket
	 */
	public final BluetoothSocket getBluetoothSocket() {
		return syncBluetoothSocketModifier(false, null);
	}

	/**
	 * ReleaseBlueToothSocket: Stop CommunicationService Close socket Finish
	 * communication Activity Set Socket and its io to null
	 */
	public final void resetBluetoothSocket() {
		Log.e(TAG, "resetBluetoothSocket : the socket was " + bluetoothSocket);
		stopService();
		if (getBluetoothSocket() != null) {
			try {
				// then close
				getBluetoothSocket().close();
			} catch (IOException e) {
				Log.e(TAG, "resetBluetoothSocket ", e);
			}
		}
		usesBluetooth = false;
		
		syncBluetoothSocketModifier(true, null);
		syncInputSocketStream(true, null);
		syncOutputSocketStream(true, null);
	}


	/******************************************************************************************/
	/** Get/set **************************************************************************/
	/******************************************************************************************/
	/**
	 * @return the remoteDevice
	 */
	public final BluetoothDevice getRemoteDevice() {

		return remoteDevice;
	}

	/**
	 * @param remoteDevice
	 *            the remoteDevice to set
	 */
	public final void setRemoteDevice(BluetoothDevice selectedDevice) {
		this.remoteDevice = selectedDevice;
	}

	/**
	 * @return the thisDeviceName
	 */
	public final String getThisDeviceName() {
		return thisDeviceName;
	}

	/**
	 * @param thisDeviceName
	 *            the thisDeviceName to set
	 */
	public final void setThisDeviceName(String thisDeviceName) {
		this.thisDeviceName = thisDeviceName;
	}

	public boolean isUsesBluetooth() {
		return usesBluetooth;
	}

	public void setUsesBluetooth(boolean usesBluetooth) {
		this.usesBluetooth = usesBluetooth;
	}

	public boolean isiAmTheServer() {
		return iAmTheServer;
	}

	public void setiAmTheServer(boolean iAmTheServer) {
		this.iAmTheServer = iAmTheServer;
	}

}
