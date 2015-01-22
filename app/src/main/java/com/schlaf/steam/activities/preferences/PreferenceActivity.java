package com.schlaf.steam.activities.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;

import com.schlaf.steam.R;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private final String[] mAutoSummaryFields = { "pref_key_timer_minutes",
			"pref_list_building_army_size", "pref_list_building_caster_count" }; // change here
	private final int mEntryCount = mAutoSummaryFields.length;
	private Preference[] mPreferenceEntries;
	private String[] sumaries;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mPreferenceEntries = new Preference[mEntryCount];
		sumaries = new String[mEntryCount];
		for (int i = 0; i < mEntryCount; i++) {
			mPreferenceEntries[i] = getPreferenceScreen().findPreference(
					mAutoSummaryFields[i]);
			sumaries[i] = mPreferenceEntries[i].getSummary().toString();

		}
		// getSupportActionBar().setTitle(R.string.preferences);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		for (int i = 0; i < mEntryCount; i++) {
			updateSummary(mAutoSummaryFields[i]); // initialization
		}
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this); // register
																	// change
																	// listener
		updateSummaries();
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); // unregister
		// change
		// listener
	}

	private void updateSummary(String key) {
		for (int i = 0; i < mEntryCount; i++) {
			if (key.equals(mAutoSummaryFields[i])) {
				if (mPreferenceEntries[i] instanceof EditTextPreference) {
					Log.d("PreferenceActivity", "EditTextPreference"); 
					final EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
					if (currentPreference.getText() != null) {
						mPreferenceEntries[i].setSummary(sumaries[i].replace("!replace!",
								currentPreference.getText()));
					}
				} else if (mPreferenceEntries[i] instanceof ListPreference) {
					Log.d("PreferenceActivity", "ListPreference"); 
					final ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
					if (currentPreference.getEntry() != null) {
						mPreferenceEntries[i].setSummary(sumaries[i].replace("!replace!",
								currentPreference.getEntry()));
					}
					mPreferenceEntries[i].notifyDependencyChange(true);
				}
				break;
			}
		}
	}

	private void updateSummaries() {
		for (int i = 0; i < mEntryCount; i++) {
			if (mPreferenceEntries[i] instanceof EditTextPreference) {
				Log.d("PreferenceActivity", "EditTextPreference"); 
				final EditTextPreference currentPreference = (EditTextPreference) mPreferenceEntries[i];
				if (currentPreference.getText() != null) {
					mPreferenceEntries[i].setSummary(sumaries[i].replace("!replace!",
							currentPreference.getText()));
				}
			} else if (mPreferenceEntries[i] instanceof ListPreference) {
				Log.d("PreferenceActivity", "ListPreference"); 
				final ListPreference currentPreference = (ListPreference) mPreferenceEntries[i];
				if (currentPreference.getEntry() != null) {
					mPreferenceEntries[i].setSummary(sumaries[i].replace("!replace!",
							currentPreference.getEntry()));
				}
				mPreferenceEntries[i].notifyDependencyChange(true);
			}
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d("PreferenceActivity", "onSharedPreferenceChanged : key = " + key );
		// updateSummary(key);
		onContentChanged();
	}

}
