package com.schlaf.steam.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by seb on 29/03/15.
 */
public class PreStartActivity extends Activity{

    public static Boolean RESTART_CLEAN = false;
    private static final String TAG = "PreStartActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        RESTART_CLEAN = true;
        restartMain();
    }

    @Override
    public void onRestart() {
        Log.e(TAG, "onRestart");
        super.onRestart();
        restartMain();
    }


    public void restartMain(){
        Log.e(TAG, "restartMain");
        if(RESTART_CLEAN == true){
            Log.e(TAG, "restartMain : restart from clean");
            Intent mainIntent = new Intent(this, StartActivity.class);
            // mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP  );
            startActivity(mainIntent);
            finish();
        }else{
            Log.e(TAG, "restartMain : no need to clean : OK");
            finish();
        }
        RESTART_CLEAN = false;
    }

}
