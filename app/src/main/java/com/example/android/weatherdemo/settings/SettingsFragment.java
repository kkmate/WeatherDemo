package com.example.android.weatherdemo.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.weatherdemo.R;

/**
 * Created by tomaki on 25/08/2017.
 */

/**
 * SettingsFragment shows and handles user's settings and preferences made on the
 * settings screen.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Set summary for preference on preference screens.
     */
    private void setSummary(Preference preference, Object value){
        String valueStr = value.toString();
        String summary;
        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int selectedIndex = listPreference.findIndexOfValue(valueStr);
            summary = ((selectedIndex >= 0) ?
                    listPreference.getEntries()[selectedIndex].toString() : "");
        }else if (preference instanceof CheckBoxPreference){
            summary = "";
        }else if (preference instanceof EditTextPreference){
            summary = valueStr;
        }else{
            summary = "";
        }
        preference.setSummary(summary);
    }

    /**
     * Register listener for preference change events.
     */
    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Unregister listener for preference change events.
     */
    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Sets summaries for preferences.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setSummary(p, value);
            }
        }
    }

    /**
     * Updates summaries for preferences when this callback is called.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);
        if (preference != null){
            setSummary(preference, sharedPreferences.getString(s,""));
        }
    }
}
