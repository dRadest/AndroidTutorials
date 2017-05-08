package com.example.android.draddest.settingsexample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

/**
 * SettingsFragment class
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings_example);

        // get a reference to shared preferences
        PreferenceScreen prefScreen = getPreferenceScreen();
        SharedPreferences sharedPref = prefScreen.getSharedPreferences();
        // iterate through all the preferences
        int count = prefScreen.getPreferenceCount();
        for (int i=0; i<count; i++){
            Preference pref = prefScreen.getPreference(i);
            // if preference is not the instance of CheckBoxPreference
            if (!(pref instanceof CheckBoxPreference)){
                // get the value and pass it to the setPreferenceSummary
                String value = sharedPref.getString(pref.getKey(), "");
                setPrefferenceSummary(pref, value);
            }
        }
    }

    // helper method to set the preference summary text
    private void setPrefferenceSummary (Preference pref, String value){
        if (pref instanceof ListPreference){
            ListPreference listPref = (ListPreference) pref;
            int prefIndex = listPref.findIndexOfValue(value);
            if (prefIndex >= 0){
                listPref.setSummary(listPref.getEntries()[prefIndex]);
            }
        } else if (pref instanceof EditTextPreference){
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            if (value.length() > 0){
                editTextPreference.setSummary(value);
            } else {
                editTextPreference.setSummary("Enter text to display");
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (null != pref){
            if (!(pref instanceof CheckBoxPreference)){
                String value = sharedPreferences.getString(pref.getKey(), "");
                setPrefferenceSummary(pref, value);
            }
        }
    }

    // register OnSharedPreferenceChangeListener
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    // unregister OnSharedPreferenceChangeListener
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
