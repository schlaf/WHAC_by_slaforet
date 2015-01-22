package com.schlaf.steam.activities.bluetooth;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import android.os.SystemClock;
import android.util.Log;

import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.battle.BattleCommunicationObject;
import com.schlaf.steam.activities.battle.BattleEntry;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.battle.CommAction;
import com.schlaf.steam.activities.battle.MultiPVModel;
import com.schlaf.steam.data.DamageGrid;
import com.schlaf.steam.storage.ArmyStore;

public class CommunicationProtocolSingleton {

	public enum ConnectStatusEnum {
		NOT_CONNECTED,
		CONNECTED,
	}
	
	public enum DataStatusEnum {
		HELLO,
		ARMY_NOT_SENT,
		SENDING_ARMY,
		SENDING_ENTRIES,
		ARMY_SENT,
		RECEIVING_ARMY,
		ARMY_RECEIVED,
		WAITING,
	}

	private static final String TAG = "CommunicationProtocolSingleton";
	
	private static CommunicationProtocolSingleton singleton;
	
	private CommunicationProtocolSingleton() {
		currentConnectStatus = ConnectStatusEnum.NOT_CONNECTED;
		currentDataStatus = DataStatusEnum.HELLO;
	}
	
	public static CommunicationProtocolSingleton getInstance() {
		if (singleton == null) {
			singleton = new CommunicationProtocolSingleton();
		}
		return singleton;
	}
	
	private ConnectStatusEnum currentConnectStatus;
	private DataStatusEnum currentDataStatus;
	
	
	private Queue<BattleCommunicationObject> queueBattleEntries = new LinkedList<BattleCommunicationObject>();
	BattleCommunicationObject messageToSend;
	
	public void setConnected(boolean connected) {
		if (connected) {
			currentConnectStatus = ConnectStatusEnum.CONNECTED;
		} else {
			currentConnectStatus = ConnectStatusEnum.NOT_CONNECTED;
		}
		
		
		queueBattleEntries.clear();
		
        List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER1);
        
        int uniqueId = 1;
        
        for (BattleEntry entry : entries) {
            BattleCommunicationObject sendEntry = new BattleCommunicationObject();
            sendEntry.setAction(CommAction.ADD_ENTRY);
            sendEntry.setBattleEntry(entry);
            
            
            if (entry.hasDamageGrid()) {
            	// assign unique ID to grid, to ensure no mismatch
            	if (((MultiPVModel)entry).getDamageGrid().getUniqueId() == 0) {
            		((MultiPVModel)entry).getDamageGrid().setUniqueId(uniqueId ++);	
            	}
            }
            enqueueBattleEntry(sendEntry);
        }

		
	}
	
	/**
	 * calculate the next message to send depending on current status.
	 * @return
	 */
	
	public BattleCommunicationObject getNextMessageToSend() {
		
		Log.e(TAG, "getNextMessageToSend");
		
		if (! SteamPunkRosterApplication.getInstance().isiAmTheServer()  && currentDataStatus == DataStatusEnum.HELLO) {
			return null;
			// if i a not the server, i do not initiate communication
		}
		
        BattleCommunicationObject out = new BattleCommunicationObject();
        
		if (currentConnectStatus == ConnectStatusEnum.NOT_CONNECTED) {
			currentDataStatus = DataStatusEnum.HELLO; // reset to first step of communication
			Log.e(TAG, "not connected, no message to send");
			return null;
		}
		
		// we are connected.. try to communicate
		
		switch (currentDataStatus) {
		case HELLO :
	        out.setAction(CommAction.HELLO);
	        Log.e(TAG, "send HELLO");
			break;
		case ARMY_NOT_SENT : 
	        out.setAction(CommAction.START_ARMY_LIST);
	        Log.e(TAG, "send START_ARMY");
	        break;
		case SENDING_ARMY:
			return getArmyToSend();
		case SENDING_ENTRIES :
			return getNextEntryToSend();
		case ARMY_SENT:
			Log.e(TAG, "send END_ARMY");
			if (BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER2).isEmpty()) {
				out.setAction(CommAction.SEND_ME_YOUR_LIST);
			} else {
				// we already have the 2nd player list
				currentDataStatus = DataStatusEnum.WAITING;
				return null;
			}
			break;
		case RECEIVING_ARMY: 
			// do nothing
			break;
		case ARMY_RECEIVED:
			// do nothing
			return null;
		case WAITING:
			return null;	
		default:
			return null;
		}
		
		messageToSend = out;
		
		return out;
	}
	
	private BattleCommunicationObject getArmyToSend() {
		BattleCommunicationObject result = new BattleCommunicationObject();
		result.setAction(CommAction.SEND_ARMY_STORE);
		
		result.setArmyStore(BattleSingleton.getInstance().getArmy(BattleSingleton.PLAYER1));
		
		messageToSend = result;
		
		return messageToSend;
		
	}
	
	private BattleCommunicationObject getNextEntryToSend() {
		messageToSend = queueBattleEntries.peek();
		
		if (messageToSend == null) {
			// no more entries, we have completed the army sending
			BattleCommunicationObject result = new BattleCommunicationObject();
			result.setAction(CommAction.END_ARMY_LIST);
		
			messageToSend = result;
		}
		
		return messageToSend;
	}
	
	public void sentMessageHasBeenReceived(UUID id) {
		Log.e(TAG, "message receveid with id " + id.toString());
		CommAction action = null;
		if (messageToSend != null && messageToSend.getUniqueId().equals(id)) {
			action = messageToSend.getAction();
			messageToSend = null; // received, so we delete
		} else {
			return;
		}
		Log.e(TAG, "previous action was " + action);
		
		
		
		switch (action) {
		case ADD_ENTRY:
			break;
		case BLUETOOTH_STATUS_MESSAGE:
			break;
		case CHRONO_PAUSE:
			currentDataStatus = DataStatusEnum.WAITING;
			break;
		case END_ARMY_LIST:
			// opponent has received our army
			currentDataStatus = DataStatusEnum.ARMY_SENT;
			break;
		case HELLO:
			// opponent has received our hello and wait for army
			currentDataStatus = DataStatusEnum.ARMY_NOT_SENT;
			Log.e(TAG, "our new status = ARMY_NOT_SENT");
			break;
		case MODIFY_DAMAGE_GRID:
			currentDataStatus = DataStatusEnum.WAITING;
			break;
		case PLAYER1_PLAY:
			currentDataStatus = DataStatusEnum.WAITING;
			break;
		case PLAYER2_PLAY:
			currentDataStatus = DataStatusEnum.WAITING;
			break;
		case RESPONSE_OK:
			// should not happen
			break;
		case START_ARMY_LIST:
			// opponent knows we have started to send our army
			currentDataStatus = DataStatusEnum.SENDING_ARMY;
			Log.e(TAG, "our new status = SENDING_ARMY");
			break;
		case SEND_ARMY_STORE:
			currentDataStatus = DataStatusEnum.SENDING_ENTRIES;
			Log.e(TAG, "our new status = SENDING_ENTRIES");
			break;
		case SEND_ME_YOUR_LIST:
			currentDataStatus = DataStatusEnum.WAITING;
			Log.e(TAG, "our new status = WAITING");
			break;
		default:
			break;
		
		}
		
		// if the message we send was a battle entry, remove from queue
		BattleCommunicationObject obj = queueBattleEntries.peek();
		if (obj != null) {
			if (obj.getUniqueId().equals(id)) {
				queueBattleEntries.remove();	
			}
		}
	}
	
	
	public void enqueueBattleEntry(BattleCommunicationObject msg) {
		queueBattleEntries.add(msg);
	}


	public void okNowWait() {
		currentDataStatus = DataStatusEnum.WAITING;
	}
	
	/**
	 * treat the message and return the next message to send
	 * @param readMessage
	 * @return BattleCommunicationObject the reponse or the next message
	 */
	public BattleCommunicationObject handleInComingMessage(BattleCommunicationObject readMessage) {
		
		BattleCommunicationObject response = new BattleCommunicationObject();
		
		Log.e(TAG, "handleInComingMessage");
		
		// by default, we send back an "ack" to opponent...
		response.setAction(CommAction.RESPONSE_OK);
		// with the same ID..
		response.setUniqueId(readMessage.getUniqueId());
		
        switch (readMessage.getAction()) {
        case RESPONSE_OK:
        	// we receive an "ack" from opponent, send next message if necessary
        	Log.e(TAG, "this is an ACK for UID = " + readMessage.getUniqueId());
        	sentMessageHasBeenReceived(readMessage.getUniqueId());
        	return getNextMessageToSend();
        case INIT_BLUETOOTH:
        	Log.e(TAG, "INIT_BLUETOOTH");
        	currentConnectStatus = ConnectStatusEnum.CONNECTED;
        	currentDataStatus = DataStatusEnum.HELLO;
        	return null;
        case HELLO:
        	// the other part sends an hello, they will next send the army
        	break;
        case START_ARMY_LIST:
        	currentDataStatus = DataStatusEnum.RECEIVING_ARMY;
        	Log.e(TAG, "received START_ARMY_LIST");
        	BattleSingleton.getInstance().startLoadingArmy2();
        	break;
        case SEND_ARMY_STORE:
        	Log.e(TAG, "received SEND_ARMY_STORE");
        	ArmyStore army = readMessage.getArmyStore();
        	BattleSingleton.getInstance().setArmy(army, BattleSingleton.PLAYER2);
        	break;
        case ADD_ENTRY: 
        	Log.e(TAG, "received ADD_ENTRY");
        	BattleSingleton.getInstance().addArmy2Entry(readMessage.getBattleEntry());
        	break;
        case END_ARMY_LIST:
        	Log.e(TAG, "received END_ARMY_LIST");
        	BattleSingleton.getInstance().finishLoadingArmy2();
        	currentDataStatus = DataStatusEnum.ARMY_RECEIVED;
        	break;
        case SEND_ME_YOUR_LIST:
        	Log.e(TAG, "received SEND_ME_YOUR_LIST");
        	if (! queueBattleEntries.isEmpty()) {
	        	currentDataStatus = DataStatusEnum.ARMY_NOT_SENT;
	        	return getNextMessageToSend();
        	} else {
        		currentDataStatus = DataStatusEnum.WAITING;
        		return null;
        	}
        case MODIFY_DAMAGE_GRID:
        	handleIncomingDamageFromBT(readMessage.getDamageGrid());
        	break;
        case BLUETOOTH_STATUS_MESSAGE:
//        	blueToothDisconnect();
        	currentConnectStatus = ConnectStatusEnum.NOT_CONNECTED;
        	currentDataStatus = DataStatusEnum.HELLO;
        	return null;
        case PLAYER1_PLAY:
        case PLAYER2_PLAY:
        case CHRONO_PAUSE:
        	handleIncomingChronoEvent(readMessage); 
        	break;
        }
        
        return response;

		
		
	}
	
	
    private void handleIncomingDamageFromBT(DamageGrid damageGrid) {

    	List<BattleEntry> entries = BattleSingleton.getInstance().getEntries(BattleSingleton.PLAYER2);

    	int targetId = damageGrid.getUniqueId();
    	Log.e(TAG, "target id = " + targetId);

    	for (BattleEntry entry : entries) {
    		Log.d(TAG, "found player2 entries = " + entry.toString());
    		if (entry.hasDamageGrid()) {
    			DamageGrid targetGrid = ((MultiPVModel) entry).getDamageGrid();
    			Log.d(TAG, "grid id = " + targetGrid.getUniqueId());
    			if (targetId == targetGrid.getUniqueId()) {
    				Log.d(TAG, "found target grid = " + targetGrid);
    				targetGrid.copyStatusFrom(damageGrid);
    			}
    		}
    	}
    }
    
    
 	private void handleIncomingChronoEvent(BattleCommunicationObject readMessage) {
		
		boolean shouldStartChrono = BattleSingleton.getInstance().getPlayer1Chrono().isPaused() && BattleSingleton.getInstance().getPlayer2Chrono().isPaused();
		
		if (readMessage.getAction() == CommAction.PLAYER1_PLAY) {
			// Toast.makeText(this, "player 1 - start", Toast.LENGTH_SHORT).show();
			BattleSingleton.getInstance().getPlayer1Chrono().startResume(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
		}

		if (readMessage.getAction() == CommAction.PLAYER2_PLAY) {
			// Toast.makeText(this, "player 2 - start", Toast.LENGTH_SHORT).show();
			BattleSingleton.getInstance().getPlayer2Chrono().startResume(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
		}
		if (readMessage.getAction() == CommAction.CHRONO_PAUSE) {
			// Toast.makeText(this, "Chrono - pause", Toast.LENGTH_SHORT).show();
			BattleSingleton.getInstance().getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
		}
		
		
		// updateChronoLayout(shouldStartChrono);

	}

	public ConnectStatusEnum getCurrentConnectStatus() {
		return currentConnectStatus;
	}

	public DataStatusEnum getCurrentDataStatus() {
		return currentDataStatus;
	}
    
    
}
