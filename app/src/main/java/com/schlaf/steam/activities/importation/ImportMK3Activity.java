package com.schlaf.steam.activities.importation;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.SteamPunkRosterApplication;
import com.schlaf.steam.activities.LoadActivityInterface;
import com.schlaf.steam.activities.StartInitializeThread;
import com.schlaf.steam.storage.StorageManager;

public class ImportMK3Activity extends ActionBarActivity implements LoadActivityInterface {

    public static final String IMPORT_FREQUENCY = "IMPORT_FREQUENCY";
    public static final String IMPORT_LANGAGE = "IMPORT_LANGAGE";
    public static final String IMPORT_NETWORK = "IMPORT_NETWORK";

    public static final String FREQUENCY_KEY  ="frequency";
    public static final String NEVER = "NEVER";
    public static final String ONCE_PER_WEEK = "ONCE_PER_WEEK";
    public static final String ALWAYS = "ALWAYS";


    public static final String LANGAGE_KEY = "langage";
    public static final String US_EN = "whac";
    public static final String US_EN_v2 = "whac_v2";
    public static final String FR_FR = "whac_fr";
    public static final String LAST_IMPORT_DATE_MS = "last_import_date" ; // date of last import in ms (to convert in date with new Date(ms))


    public static final String NETWORK_KEY = "network";
    public static final String MOBILE = "MOBILE";
    public static final String WIFI = "WIFI";

    StartInitializeThread initThread;
    ProgressDialog progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.content_import_mk3, null);

        setContentView(view);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setTitle("WHAC Import preferences");


        // load prefs and display radio buttons
        ((RadioButton)view.findViewById(R.id.radioButtonNever)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonOncePerWeek)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonAlways)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonUS)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonFR)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonWifi)).setChecked(false);
        ((RadioButton)view.findViewById(R.id.radioButtonMobile)).setChecked(false);


        SharedPreferences frequencyPrefs = getApplication().getSharedPreferences(IMPORT_FREQUENCY, Context.MODE_PRIVATE);
        switch (frequencyPrefs.getString(FREQUENCY_KEY, NEVER) ) {
            case NEVER : {
                ((RadioButton)view.findViewById(R.id.radioButtonNever)).setChecked(true);
                break;
            }
            case ONCE_PER_WEEK : {
                ((RadioButton)view.findViewById(R.id.radioButtonOncePerWeek)).setChecked(true);
                break;
            }
            case ALWAYS : {
                ((RadioButton)view.findViewById(R.id.radioButtonAlways)).setChecked(true);
                break;
            }
        }

        SharedPreferences langagePrefs = getApplication().getSharedPreferences(IMPORT_LANGAGE, Context.MODE_PRIVATE);
        switch (langagePrefs.getString(LANGAGE_KEY, US_EN) ) {
            case US_EN : {
                ((RadioButton)view.findViewById(R.id.radioButtonUS)).setChecked(true);
                break;
            }
            case FR_FR : {
                ((RadioButton)view.findViewById(R.id.radioButtonFR)).setChecked(true);
                break;
            }
        }

        SharedPreferences networkPrefs = getApplication().getSharedPreferences(IMPORT_NETWORK, Context.MODE_PRIVATE);
        switch (networkPrefs.getString(NETWORK_KEY, WIFI) ) {
            case WIFI : {
                ((RadioButton)view.findViewById(R.id.radioButtonWifi)).setChecked(true);
                break;
            }
            case MOBILE : {
                ((RadioButton)view.findViewById(R.id.radioButtonMobile)).setChecked(true);
                break;
            }
        }

    }

    public void updateNow(View v) {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.loading_data));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(8);

        initThread = new StartInitializeThread(this, (SteamPunkRosterApplication) getApplication(), progressBar);
        initThread.execute();

    }

    public void onRadioNetworkPrefsClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SharedPreferences networkPrefs = getApplication().getSharedPreferences(IMPORT_NETWORK, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = networkPrefs.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonMobile: {
                if (checked) {
                    editor.putString(NETWORK_KEY, MOBILE);
                }
                break;
            }
            case R.id.radioButtonWifi: {
                if (checked) {
                    editor.putString(NETWORK_KEY, WIFI);
                }
                break;
            }
        }


        editor.commit();
    }

    public void onRadioDownloadPrefsClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SharedPreferences frequencyPrefs = getApplication().getSharedPreferences(IMPORT_FREQUENCY, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = frequencyPrefs.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonNever: {
                if (checked) {
                    // Pirates are the best
                    editor.putString(FREQUENCY_KEY, NEVER);
                    Toast.makeText(this, "never", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.radioButtonOncePerWeek: {
                if (checked) {
                    editor.putString(FREQUENCY_KEY, ONCE_PER_WEEK);
                    // Ninjas rule
                    Toast.makeText(this, "once per week", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.radioButtonAlways: {
                if (checked) {
                    editor.putString(FREQUENCY_KEY, ALWAYS);
                    // Ninjas rule
                    Toast.makeText(this, "always", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }


        editor.commit();
    }

    public void onRadioDownloadLangClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SharedPreferences langPrefs = getApplication().getSharedPreferences(IMPORT_LANGAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = langPrefs.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonUS: {
                if (checked) {
                    editor.putString(LANGAGE_KEY, US_EN);
                    Toast.makeText(this, "english chosen", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.radioButtonFR: {
                if (checked) {
                    editor.putString(LANGAGE_KEY, FR_FR);
                    // Ninjas rule
                    Toast.makeText(this, "french chosen", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }


        editor.commit();
    }

    @Override
    public void blockButtons() {

    }

    @Override
    public boolean forceDownload() {
        return true;
    }

    public void restaureButtons() {
        // do nothing
    }
}
