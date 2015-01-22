/**<ul>
 * <li>BlueTooth</li>
 * <li>com.android2ee.android.tuto.communication.bluetooth</li>
 * <li>2 juil. 2013</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.schlaf.steam.activities.bluetooth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.battle.BattleCommunicationObject;
import com.schlaf.steam.activities.battle.CommAction;
import com.schlaf.steam.activities.damages.BluetoothDamageChangeObserver;
import com.schlaf.steam.data.DamageGrid;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to be a service that handles the bluetooth communication exchanges between
 *        the devices.
 */
public class CommunicationService extends Service implements BluetoothDamageChangeObserver {
	
	private static final String TAG = "CommunicationService";
	
	/**
	 * Boolean to create the communication thread only once
	 */
	boolean alreadyManagedCommunication = false;

	/**
	 * The thread that manage the data exchange between the both device
	 */
	ConnectedReadThread connectedReadThread;
	
	ConnectedWriteThread connectedWriteThread;
	
	/**
	 * Kill the thread
	 */
	private AtomicBoolean threadDead=new AtomicBoolean(false);
	/******************************************************************************************/
	/** Connection with the activity **************************************************************************/
	/******************************************************************************************/
	/**
	 * The action of the intent sent from this service to the bound activity
	 */
	public static final String BLUETOOTH_COMMUNICATION_INTENT_ACTION = "com.android2ee.android.tuto.communication.bluetooth.service.intent.communication.thread";
	/**
	 * The Intent sent from this service to the bound activity
	 */
	private final Intent bluetoothCommunicationIntent = new Intent(BLUETOOTH_COMMUNICATION_INTENT_ACTION);
	/**
	 * The name of the property of the intent that carry the string message
	 */
	public static final String BLUETOOTH_MESSAGE = "bluetoothMessage";

	/******************************************************************************************/
	/** Constructors & LifeCycle **************************************************************************/
	/******************************************************************************************/
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");		
		// Kill the thread
		CommunicationProtocolSingleton.getInstance().setConnected(false);
		// BattleSingleton.getInstance().setPlayer1ArmyTransmitted(false);
		connectedReadThread.interrupt();
		connectedWriteThread.interrupt();
		threadDead.set(true);
		alreadyManagedCommunication=false;
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		initialize();
		return super.onStartCommand(intent, flags, startId);
	}

	/******************************************************************************************/
	/** Initialization **************************************************************************/
	/******************************************************************************************/
	/**
	 * Before the service runs really
	 */
	private void initialize() {
		// Launch the Thread and all the communication stuff
		threadDead.set(false);
		manageConnectedSocket();
		CommunicationProtocolSingleton protocol = CommunicationProtocolSingleton.getInstance(); // make sure
		protocol.setConnected(true);
		
		
		BattleCommunicationObject initMessage = new BattleCommunicationObject();
		initMessage.setAction(CommAction.INIT_BLUETOOTH);
		bluetoothCommunicationIntent.putExtra(BLUETOOTH_MESSAGE, initMessage);
		// launch the intent it will be catched by the bound activity
		sendBroadcast(bluetoothCommunicationIntent);

	}

	/******************************************************************************************/
	/** Binder **************************************************************************/
	/******************************************************************************************/
	/**
	 * The binder use to bind this and the activity
	 */
	private final Binder binder = new LocalBinder();

	/**
	 * @author Mathias Seguy (Android2EE)
	 * @goals
	 *        This class aims to define a local binder
	 */
	public class LocalBinder extends Binder {
		/**
		 * @return the instance of the service
		 */
		public CommunicationService getService() {
			return CommunicationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		initialize();
		return binder;
	}

	/******************************************************************************************/
	/** Public methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * Send a Message through Bluetooth
	 * 
	 * @param Message
	 */
	public void sendMessage(BattleCommunicationObject message) {
		Log.e(TAG, "sendMessage : message = " + message.toString());
		connectedWriteThread.write(message);
	}

	/*************************************************************************************/
	/** Managing data exchange **************************************************************/
	/*************************************************************************************/

	/**
	 * * This method is called when a Bluetooth socket is created and the attribute bluetoothSocket
	 * is instanciated
	 */
	private void manageConnectedSocket() {
		Log.e(TAG, "manageConnectedSocket");
		if (!alreadyManagedCommunication) {
			// launch the connected thread
			connectedReadThread = new ConnectedReadThread();
			connectedReadThread.start();
			
			connectedWriteThread = new ConnectedWriteThread();
			connectedWriteThread.start();
			alreadyManagedCommunication = true;
		}

	}

	/**
	 * The thread that manages communication
	 */
	private class ConnectedWriteThread extends Thread {

	    protected static final int HEADER_MSB = 0x10;
	    protected static final int HEADER_LSB = 0x55;
	    
	    ArrayBlockingQueue<BattleCommunicationObject> queue = new ArrayBlockingQueue<BattleCommunicationObject>(50);
	    
	    public void run() {
			Log.e(TAG, "Start : ConnectedThread run");
			
			// Keep listening to the InputStream until an exception occurs
			while (!threadDead.get()) {
				try {
					
					BattleCommunicationObject message = queue.take();
					
					// Log.e(TAG, "write " + message.toString());
					OutputStream outputStream = SteamPunkRosterApplication.getInstance().getOutputSocketStream();
					
					 // Send the header control first
	                outputStream.write(HEADER_MSB);
	                outputStream.write(HEADER_LSB);

	                byte[] messageBytes = message.getBytes();
	                
	                // write size
	                outputStream.write(BluetoothTransferUtils.intToByteArray(messageBytes.length));

	                // write digest
	                byte[] digest = BluetoothTransferUtils.getDigest(messageBytes);
	                outputStream.write(digest);

	                // now write the data
	                outputStream.write(messageBytes);
	                outputStream.flush();
					
					// Write bytes in the output socket
				} catch (IOException e) {
					
					BattleCommunicationObject statusMessage = new BattleCommunicationObject();
					statusMessage.setAction(CommAction.BLUETOOTH_STATUS_MESSAGE);
					statusMessage.setBluetoothStatusMessage("failed to send data");
					// Add that string to the intent that will be launched
					bluetoothCommunicationIntent.putExtra(BLUETOOTH_MESSAGE, statusMessage);
					// launch the intent it will be catched by the bound activity
					sendBroadcast(bluetoothCommunicationIntent);
					threadDead.set(true);
				} catch (InterruptedException e) {
					Log.e(TAG, "ConnectedWriteThread InterruptedException when getting message from queue", e);
					threadDead.set(true);
				}
				
			}
	    }

		
		/* Call this from the main Activity to send data to the remote device */
		public void write(BattleCommunicationObject message) {
			try {
				queue.put(message);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "ConnectedWriteThread error when putting message in queue", e);
			}
		}
	}
	
	/**
	 * The thread that manages communication
	 */
	private class ConnectedReadThread extends Thread {
		
		public class ProgressData {
		    public long totalSize = 0;
		    public long remainingSize = 0;
		}
		
		protected static final int CHUNK_SIZE = 4192;
	    protected static final int HEADER_MSB = 0x10;
	    protected static final int HEADER_LSB = 0x55;
		
		/* (non-Javadoc) * @see java.lang.Thread#run() */
		public void run() {
			Log.e(TAG, "Start : ConnectedThread run");
			
			// Keep listening to the InputStream until an exception occurs
			while (!threadDead.get()) {
				InputStream inputStream = SteamPunkRosterApplication.getInstance().getInputSocketStream();
				Log.e(TAG, "ConnectedThread : we got the input stream");

				try {
					// Read from the InputStream
					ByteArrayOutputStream baos = new ByteArrayOutputStream(CHUNK_SIZE);
					
					byte[] headerBytes = new byte[22];
		            byte[] digest = new byte[16];
		            int headerIndex = 0;
					
					boolean waitingForHeader = true;
					
					boolean indexAndDataOK = false;
					
					ProgressData progress = new ProgressData();
					while (true) {
						
						if (waitingForHeader) {
		                    byte[] header = new byte[1];
		                    // Log.e(TAG, "Blocking read call");
		                    inputStream.read(header, 0, 1);
		                    // Log.e(TAG, "Received Header Byte: " + header[0]);
		                    headerBytes[headerIndex++] = header[0];

		                    if (headerIndex == 22) {
		                        if ((headerBytes[0] == HEADER_MSB) && (headerBytes[1] == HEADER_LSB)) {
		                            // Log.e(TAG, "Header Received. Now obtaining length");
		                            byte[] dataSizeBuffer = new byte[4]; 
		                            System.arraycopy(headerBytes, 2, dataSizeBuffer, 0, 4);
		                            progress.totalSize = BluetoothTransferUtils.byteArrayToInt(dataSizeBuffer);
		                            progress.remainingSize = progress.totalSize;
		                            // Log.e(TAG, "Data size expected = " + progress.totalSize);
		                            //digest = Arrays.copyOfRange(headerBytes, 6, 22);
		                            waitingForHeader = false;
		                        } else {
		                            Log.e(TAG, "Did not receive correct header. Closing socket");
		                            // socket.close();
		                            // handler.sendEmptyMessage(MessageType.INVALID_HEADER);
		                            break;
		                        }
		                    }

		                } else {
		                    // Read the data from the stream in chunks
		                    byte[] buffer ;
		                    if (progress.remainingSize > CHUNK_SIZE) {
		                    	buffer = new byte[CHUNK_SIZE];
		                    } else {
		                    	buffer = new byte[ (int) progress.remainingSize];
		                    }
		                    // Log.e(TAG, "Waiting for data. Expecting " + progress.remainingSize + " more bytes.");
		                    int bytesRead = inputStream.read(buffer);
		                    // Log.e(TAG, "Read " + bytesRead + " bytes into buffer");
		                    baos.write(buffer, 0, bytesRead);
		                    progress.remainingSize -= bytesRead;
//		                    sendProgress(progressData);
		                    if (progress.remainingSize <= 0) {
		                    	indexAndDataOK = true;
		                        break;
		                    }
		                }
					}
					
					if (indexAndDataOK) {
						// the baos is full.
						byte[] result = baos.toByteArray();
						ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(result));
						try {
							BattleCommunicationObject receivedMessage = (BattleCommunicationObject) ois.readObject();
							// Add that string to the intent that will be launched
							bluetoothCommunicationIntent.putExtra(BLUETOOTH_MESSAGE, receivedMessage);
							// launch the intent it will be catched by the bound activity
							sendBroadcast(bluetoothCommunicationIntent);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
					
				} catch (IOException e) {
					Log.e(TAG, "IO error in ConnectedThread run", e);
					
					BattleCommunicationObject statusMessage = new BattleCommunicationObject();
					statusMessage.setAction(CommAction.BLUETOOTH_STATUS_MESSAGE);
					statusMessage.setBluetoothStatusMessage("Error in bluetooth channel : connection is reset");
					// Add that string to the intent that will be launched
					bluetoothCommunicationIntent.putExtra(BLUETOOTH_MESSAGE, statusMessage);
					// launch the intent it will be catched by the bound activity
					sendBroadcast(bluetoothCommunicationIntent);
					
					break;
				} 
			}
		}

		/* Call this from the main Activity to shutdown the connection */
		public void cancel() {
			try {
				// Close the socket
				if (SteamPunkRosterApplication.getInstance().getBluetoothSocket() != null) {
					SteamPunkRosterApplication.getInstance().getBluetoothSocket().close();
				}
			} catch (IOException e) {
				Log.e("CommunicationActivity", "ConnectedThread cancel", e);
			}
		}
	}

	@Override
	public void onChangeDamageStatus(DamageGrid grid) {
		Log.e(TAG, "received change damage, do nothing (yet)");
		
	}

	@Override
	public void onApplyCommitOrCancel(DamageGrid grid) {
		Log.e(TAG, "received damage commit, transmit");
		BattleCommunicationObject message = new BattleCommunicationObject();
		message.setAction(CommAction.MODIFY_DAMAGE_GRID);
		message.setDamageGrid(grid);
		sendMessage(message);
	}

}
