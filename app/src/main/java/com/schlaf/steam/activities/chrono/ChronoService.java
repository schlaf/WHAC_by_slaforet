package com.schlaf.steam.activities.chrono;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChronoService extends Service {

	ChronoRunnable r1 = new ChronoRunnable();
	ChronoRunnable r2 = new ChronoRunnable();
	
	Thread t1;
	Thread t2;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

}
