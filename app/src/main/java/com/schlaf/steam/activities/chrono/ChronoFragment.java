package com.schlaf.steam.activities.chrono;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.schlaf.steam.R;
import com.schlaf.steam.activities.battle.BattleSingleton;
import com.schlaf.steam.activities.chrono.ChronoRunnable.ChronoObserver;

public class ChronoFragment extends Fragment implements
		OnClickListener, ChronoObserver {

	
	private static final int TICK_WHAT = 2;
	private static final int TICK_END_SOUND = 4;
	private static final String TAG = "ChronoFragment";
	
	public static final String ID = "chronoFragment";
	
	int endClocksoundId;
	int dingDingSoundId;
	private static final boolean D = false;
	
	ChronoActivityInterface parentActivity;

	SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

	/**
	 * internal handler for chrono threading
	 * @author S0085289
	 *
	 */
	private static class ChronoHandler extends Handler {
		
		private final WeakReference<ChronoFragment> mFragment;
		
		public ChronoHandler(ChronoFragment parentFragment) {
			mFragment = new WeakReference<ChronoFragment>(parentFragment);
		}
		
		public void handleMessage(Message m) {
			if (m.what == TICK_WHAT) {
				if (BattleSingleton.getInstance().getPlayer1Chrono().isRunning() || 
						BattleSingleton.getInstance().getPlayer2Chrono().isRunning() ) {
					// enqueue message to treat in one second
					if (D) Log.d("ChronoHandler", "handleMessage - enqueue new message");
					sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
				}
			}
			
			
			boolean endClock = false;
			if (m.what == TICK_END_SOUND) {
				endClock = true;
			}
			
			ChronoFragment fragment = mFragment.get();
			if (fragment != null) {
				fragment.updateDisplay();
				fragment.updateNotification();
				if (endClock) {
					fragment.endSound();
				}					
			}

		}
	}
	
	ImageButton buttonPauseAll;

	CountDownView countDownView1;
	CountDownView countDownView2;
	
	LinearLayout player1TimeZone;
	LinearLayout player2TimeZone;
	
	ImageButton shrinkButton;
	ImageButton expandButton;

	ChronoHandler mHandler;

	public interface ChronoActivityInterface {
		public void setInitialMinuteCount(int nbMinutes);
		
		public void openChronoConfig(View v);
		
		public void playPause(int playerNumber);
		
		public void pause();

	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof ChronoActivityInterface) {
			parentActivity = (ChronoActivityInterface) activity;
			if (D)  Log.d("ChronoFragment", "onAttach received "
					+ activity.getClass().getName());
		} else {
			throw new UnsupportedOperationException(
					"ChronoFragment requires a ChronoActivityInterface as parent activity");
		}
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (D) Log.d("ChronoFragment", "ChronoFragment.onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		endClocksoundId = soundPool.load(getActivity(), R.raw.buzzerburn, 1);
		dingDingSoundId = soundPool.load(getActivity(), R.raw.boxing_bell, 1);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (D) Log.d("ChronoFragment", "ChronoFragment.onCreateView");

		mHandler = new ChronoHandler(this);
		
		View view = inflater
				.inflate(R.layout.chrono_fragment, container, false);

		buttonPauseAll = (ImageButton) view.findViewById(R.id.pauseButton);

		buttonPauseAll.setOnClickListener(this);

		countDownView1 = (CountDownView) view.findViewById(R.id.countDownView1);
		countDownView1.setColor(CountDownView.RED);
		countDownView1.setOnClickListener(this);
		
		countDownView2 = (CountDownView) view.findViewById(R.id.countDownView2);
		countDownView2.setColor(CountDownView.BLUE);
		countDownView2.setOnClickListener(this);
		
		updateRunning();
		
		return view;
	}
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



	@Override
	public void onClick(View v) {
		
		boolean shouldStartRunning = false;
		boolean everythingWasStopped = false;
		if (BattleSingleton.getInstance().getPlayer1Chrono().isPaused() &&
					BattleSingleton.getInstance().getPlayer2Chrono().isPaused()) {
			everythingWasStopped = true;
			// think to start the ticker!
		}
		
		if (v.getId() == R.id.pauseButton) {
			BattleSingleton.getInstance().getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
			parentActivity.pause();
		}
		
		if ( v.getId() == R.id.countDownView1 || v.getId() == R.id.countDownView2) {
		
			shouldStartRunning = true;
			if (BattleSingleton.getInstance().getPlayer1Chrono().isPaused() &&
					BattleSingleton.getInstance().getPlayer2Chrono().isPaused()) {
				// all paused, start the one clicked
				if (v.getId() == R.id.countDownView1) {
					BattleSingleton.getInstance().getPlayer1Chrono().startResume(SystemClock.elapsedRealtime());
					parentActivity.playPause(BattleSingleton.PLAYER1);
				}
				if (v.getId() == R.id.countDownView2) {
					BattleSingleton.getInstance().getPlayer2Chrono().startResume(SystemClock.elapsedRealtime());
					parentActivity.playPause(BattleSingleton.PLAYER2);
				}
				

			} else {
				// flip flop
				if (BattleSingleton.getInstance().getPlayer1Chrono().isPaused()) {
					BattleSingleton.getInstance().getPlayer1Chrono().startResume(SystemClock.elapsedRealtime());
					BattleSingleton.getInstance().getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
					parentActivity.playPause(BattleSingleton.PLAYER1);
				} else {
					BattleSingleton.getInstance().getPlayer2Chrono().startResume(SystemClock.elapsedRealtime());
					BattleSingleton.getInstance().getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
					parentActivity.playPause(BattleSingleton.PLAYER2);
				}
			}
		}
			
		
		if (everythingWasStopped) {
			if (shouldStartRunning) {
				updateRunning();
			}
		}
		updateDisplay(); // just in case.
		// 
	}

	@Override
	public void notifyCurrentChronoValue(final String chronoValue) {

	}

	private void updateRunning() {
		if (D) Log.d("ChronoFragment", "updateRunning");
		if (BattleSingleton.getInstance().getPlayer1Chrono().isRunning()  || 
				BattleSingleton.getInstance().getPlayer2Chrono().isRunning() ) {
			dispatchChronometerTick();
			
			// update view every second, update notif every 10 seconds
			mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT),
					1000);
//			mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_NOTIF),
//					1000);
		} else {
			// no chrono running, no need to calculate times...
			if (D) Log.d("ChronoFragment", "handleMessage - delete queue");
			mHandler.removeMessages(TICK_WHAT);
		}
		updateDisplay();
		
	}

	private void dingDing() {
		
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Activity.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		soundPool.play(dingDingSoundId, volume, volume, 1, 0, 1f);
		
	}
	
	
	private void endClock() {
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Activity.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		
		soundPool.play(dingDingSoundId, volume, volume, 1, 0, 1f);
		
		mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_END_SOUND),
				3000);
		
	}
	
	public void endSound() {
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Activity.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		
		soundPool.play(endClocksoundId, volume, volume, 1, 0, 1f);
		
	}
	
	public void updateNotification() {
		
		String time1String = BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingString();
		String time2String = BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingString();
		
		
		String displayNotificationString = null ;
		if (BattleSingleton.getInstance().getPlayer1Chrono().isRunning()) {
			int remainingSeconds = (int) BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingMillis() / 1000;
			String color = "<font>";
			if (remainingSeconds < 20 * 60) {
				color = "<font color=\"#F88017\">";
			}
			if (remainingSeconds < 10 * 60) {
				color = "<font color=\"red\">";
			}
			if (getActivity() != null) { // if rotate, current activity may be null
                displayNotificationString = getActivity().getString(R.string.p1_time) + color + time1String + "</font>";
            }
		} else if (BattleSingleton.getInstance().getPlayer2Chrono().isRunning()) {
			int remainingSeconds = (int) BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingMillis() / 1000;
			String color = "<font>";
			if (remainingSeconds < 20 * 60) {
				color = "<font color=\"#F88017\">";
			}
			if (remainingSeconds < 10 * 60) {
				color = "<font color=\"red\">";
			}
            if (getActivity() != null) { // if rotate, current activity may be null
                displayNotificationString = getActivity().getString(R.string.p2_time) + color + time2String + "</font>";
            }
		} else {
            if (getActivity() != null) { // if rotate, current activity may be null
                displayNotificationString = getActivity().getString(R.string.paused);
            }
		}
			
		// build notification
		// the addAction re-use the same intent to keep the example short
		if (getActivity() != null) {
			
			((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(Html.fromHtml(displayNotificationString));
//			Notification n  = new NotificationCompat.Builder(getActivity())
//	        // .setTicker(displayNotificationString)
//	        .setSmallIcon(R.drawable.clock)
//	        .setNumber(remainingMinutes)
//	        .setAutoCancel(true)
//	        .setOngoing(true)
//	        .build();
//	    
//			NotificationManager notificationManager = 
//					(NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
//
//			notificationManager.notify(13541, n); 
		}
				
	}
	
	public void updateDisplay() {
		
		Log.d("ChronoFragment", "updateDisplay");
		
		if (BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingMillis() < 0) {
			BattleSingleton.getInstance().getPlayer1Chrono().pause(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer1Chrono().setInitialPlayerTimeInMillis(0);
			endClock();
		} else if (! BattleSingleton.getInstance().getPlayer1Chrono().isNotified20MinutesLeft() ) {
			if (BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingMillis() < 1200000) {
				dingDing();
				BattleSingleton.getInstance().getPlayer1Chrono().setNotified20MinutesLeft(true);
			}
		} else if (! BattleSingleton.getInstance().getPlayer1Chrono().isNotified10MinutesLeft() ) {
			if (BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingMillis() < 600000) {
				dingDing();
				BattleSingleton.getInstance().getPlayer1Chrono().setNotified10MinutesLeft(true);
			}
		}

		if (BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingMillis() < 0) {
			BattleSingleton.getInstance().getPlayer2Chrono().pause(SystemClock.elapsedRealtime());
			BattleSingleton.getInstance().getPlayer2Chrono().setInitialPlayerTimeInMillis(0);
			endClock();
		} else if (! BattleSingleton.getInstance().getPlayer2Chrono().isNotified20MinutesLeft() ) {
			if (BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingMillis() < 1200000) {
				dingDing();
				BattleSingleton.getInstance().getPlayer2Chrono().setNotified20MinutesLeft(true);
			}
		} else if (! BattleSingleton.getInstance().getPlayer2Chrono().isNotified10MinutesLeft() ) {
			if (BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingMillis() < 600000) {
				dingDing();
				BattleSingleton.getInstance().getPlayer2Chrono().setNotified10MinutesLeft(true);
			}
		}


		countDownView1.updateTime(BattleSingleton.getInstance().getPlayer1Chrono().getTimeRemainingMillis(), BattleSingleton.getInstance().getPlayer1Chrono().isRunning());
		countDownView2.updateTime(BattleSingleton.getInstance().getPlayer2Chrono().getTimeRemainingMillis(), BattleSingleton.getInstance().getPlayer2Chrono().isRunning());

	}
	
	/**
	 * notify the fragment that it should display the chrono with animation.
	 */
	public void notifyStartAnimation() {
		updateRunning();
	}
	
	
	void dispatchChronometerTick() {
		// BattleSingleton.getInstance().setPlayer1remainingTimeInMillis(0);
	}

	
}
