package com.schlaf.steam.activities.chrono;

import java.io.Serializable;

import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

public class ChronoData implements Serializable {

	/** serial */
	private static final long serialVersionUID = 6402136808279197489L;
	
	private StringBuilder sBuilder = new StringBuilder(15);
	
	private String playerName;
	/** durée initiale du compte à rebours */
	private long initialPlayerTimeInMillis;
	/** durée restante en millisecondes */
	private long remainingTimeInMillis;
	/** indique si le chrono est en pause */
	private boolean paused;
	
	/** heure système du dernier démarrage du chrono */
	private long lastTimeStart;
	
	private boolean notified20MinutesLeft = false;
	private boolean notified10MinutesLeft = false;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public boolean isPaused() {
		return paused;
	}
	
	public boolean isRunning() {
		return !paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public long getLastTimeStart() {
		return lastTimeStart;
	}

	public void setLastTimeStart(long lastTimeStart) {
		this.lastTimeStart = lastTimeStart;
	}
	
	public String getSavedTimeRemainingString() {
		Log.d("ChronoData", "getSavedTimeRemainingString  - time = " + remainingTimeInMillis);
		return DateUtils.formatElapsedTime(sBuilder, remainingTimeInMillis / 1000);
	}

	public String getTimeRemainingString() {
		long currentTime = SystemClock.elapsedRealtime();
		if (paused) {
			return getSavedTimeRemainingString();
		} else{
			long elapsedTimeSinceLastStart = (currentTime - lastTimeStart);
			long currentRemainingTimeInMillis = remainingTimeInMillis - elapsedTimeSinceLastStart;
			
			// remainingTimeInMillis = currentRemainingTimeInMillis;
			Log.d("ChronoData", "getTimeRemainingString  - time = " + currentRemainingTimeInMillis);
			return DateUtils.formatElapsedTime(sBuilder, currentRemainingTimeInMillis / 1000);	
		}
	}

	public long getTimeRemainingMillis() {
		long currentTime = SystemClock.elapsedRealtime();
		if (paused) {
			return remainingTimeInMillis;
		} else{
			long elapsedTimeSinceLastStart = (currentTime - lastTimeStart);
			long currentRemainingTimeInMillis = remainingTimeInMillis - elapsedTimeSinceLastStart;
			return currentRemainingTimeInMillis;
		}
	}
	
	public long getInitialPlayerTimeInMillis() {
		return initialPlayerTimeInMillis;
	}

	public void setInitialPlayerTimeInMillis(long initialPlayerTimeInMillis) {
		this.initialPlayerTimeInMillis = initialPlayerTimeInMillis;
		remainingTimeInMillis = initialPlayerTimeInMillis;
	}
	
	public void pause(long currentTime) {
		if (!paused) {
			long elapsedTimeSinceLastStart = (currentTime - lastTimeStart);
			remainingTimeInMillis = remainingTimeInMillis - elapsedTimeSinceLastStart;
			lastTimeStart = 0;
			paused = true;
		}
	}
	
	public void startResume(long currentTime) {
		if (paused) {
			lastTimeStart = currentTime;
			paused = false;
		}
	}

	public boolean isNotified20MinutesLeft() {
		return notified20MinutesLeft;
	}

	public void setNotified20MinutesLeft(boolean notified20MinutesLeft) {
		this.notified20MinutesLeft = notified20MinutesLeft;
	}

	public boolean isNotified10MinutesLeft() {
		return notified10MinutesLeft;
	}

	public void setNotified10MinutesLeft(boolean notified10MinutesLeft) {
		this.notified10MinutesLeft = notified10MinutesLeft;
	}
	
}
