package com.example.android.draddest.settingsexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Button button;
    private TextView textView;
    private boolean mShowButton;
    private String mDisplayText;

    @ColorInt
    private int backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "Button preference: " + mShowButton, Toast.LENGTH_SHORT).show();
            }
        });

        setupSharedPreferences();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // method to setup background and button depending on preferences
    private void setupSharedPreferences(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String backgroundKey = getString(R.string.pref_background_key);
        String prefColor = sharedPrefs.getString(backgroundKey,
                getString(R.string.pref_background_default_value));
        setBackgroundColor(prefColor);

        setButtonVisibility(sharedPrefs);

        setDisplayText(sharedPrefs);
        // register OnSharedPreferenceChangeListener
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("show_button")){
            setButtonVisibility(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_background_key))){
            String prefColor = sharedPreferences.getString(key,
                    getString(R.string.pref_background_default_value));
            setBackgroundColor(prefColor);
        } else if (key.equals("edit_text_pref_key")){
            setDisplayText(sharedPreferences);
        }
    }

    // helper method to set the background color
    public void setBackgroundColor(String newColorKey){
        if (newColorKey.equals(getString(R.string.pref_background_green_value))){
            backgroundColor = ContextCompat.getColor(this, R.color.backgroundGreen);
        } else if ((newColorKey.equals(getString(R.string.pref_background_yellow_value)))){
            backgroundColor = ContextCompat.getColor(this, R.color.backgroundYellow);
        } else if (newColorKey.equals(getString(R.string.pref_background_red_value))){
            backgroundColor = ContextCompat.getColor(this, R.color.backgroundRed);
        } else {
            backgroundColor = ContextCompat.getColor(this, R.color.backgroundDefault);
        }

        getWindow().getDecorView().setBackgroundColor(backgroundColor);
    }

    // helper method to display the text from edit text preference
    private void setDisplayText (SharedPreferences sp){
        mDisplayText = sp.getString("edit_text_pref_key", "Hello World");
        if (TextUtils.isEmpty(mDisplayText) || null == mDisplayText){
            mDisplayText = "Enter text in settings";
        }
        textView.setText(mDisplayText);
    }

    private void setButtonVisibility (SharedPreferences sp){
        mShowButton = sp.getBoolean("show_button", true);
        if (!mShowButton){
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }


}
