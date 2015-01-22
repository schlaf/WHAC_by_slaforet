/**
 * 
 */
package com.schlaf.steam.activities.chrono;

/**
 * @author S0085289
 * 
 */
public class ChronoRunnable implements Runnable {

	
	public interface ChronoObserver {
		public void notifyCurrentChronoValue(String chronoValue); 
	}
	
	/** total elapsed time (number of millisecond) */
	private long timeElapsed = 0;
	
	long currentTimeElapsed = 0;
	
	private ChronoObserver observer;

	/**
	 * time in millis of last start - every time chrono is un-paused, this value
	 * is recalculated from System.currentTimeMillis()
	 */
	private long startTime;

	private int minuteInitialValue;

	/** if true, stop chrono */
	private boolean paused = true;

	@Override
	public void run() {

		while (true) {
			try {
				if (!paused) {
					
					long currentTime = System.currentTimeMillis();
					long elapsedTimeSinceLastStart = currentTime - startTime;
					currentTimeElapsed = timeElapsed + elapsedTimeSinceLastStart;
					
					if ( observer != null) {
						observer.notifyCurrentChronoValue(getCountDownString());
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

	}

	public void pause() {
		paused = true;

		long currentTime = System.currentTimeMillis();
		long elapsedTimeSinceLastStart = currentTime - startTime;

		timeElapsed += elapsedTimeSinceLastStart;
	}

	/**
	 * start or re-start chrono
	 */
	public void go() {
		paused = false;
		startTime = System.currentTimeMillis();
	}

	public void resetChrono() {
		pause();
		timeElapsed = 0;
	}
	
	public void setInitialMinuteCount(int minuteCount) {
		minuteInitialValue = minuteCount;
	}

	public boolean isPaused() {
		return paused;
	}

	public String getCountDownString() {
		int secondsEllapsed = (int) (currentTimeElapsed / 1000);
		int minutesRemaining = minuteInitialValue - (secondsEllapsed / 60 + 1 );
		int secondsRemaining = ( (minuteInitialValue - minutesRemaining) * 60) - secondsEllapsed;
		return minutesRemaining + ":" + secondsRemaining;
	}

	public void setObserver(ChronoObserver observer) {
		this.observer = observer;
	}
	
}
