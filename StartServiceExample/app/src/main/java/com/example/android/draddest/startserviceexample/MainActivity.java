package com.example.android.draddest.startserviceexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static Context mContext;

    /* views used */
    private Button mStartServiceButton;
    private TextView mDisplayTextView;

    /* member variable to keep track of how many times the button was clicked */
    private static int mCountServiceCalled;

    /* shared preferences string key for the counter */
    private static final String SERVICE_CALLED_PREF_KEY = "Service Called Key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* find the views and implement the logic for updating the counter and the display */
        mDisplayTextView = (TextView) findViewById(R.id.textView);
        updateView();
        mStartServiceButton = (Button) findViewById(R.id.button);
        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServiceWithIntent();
            }
        });

        mContext = getApplicationContext();

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    /* helper method that increments the count of how many times the button has been clicked
    *  and puts it into shared preferences
    */
    public static void incrementCount (){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        // get the count from SharedPreferences
        mCountServiceCalled = prefs.getInt(SERVICE_CALLED_PREF_KEY, 0);
        // increment the count
        mCountServiceCalled++;
        // put it back into SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SERVICE_CALLED_PREF_KEY, mCountServiceCalled);
        editor.apply();
    }

    /* helper method that updates the view when button is clicked
    *  with the data saved in shared preferences
    */
    private void updateView(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCountServiceCalled = prefs.getInt(SERVICE_CALLED_PREF_KEY, 0);
        String textToDisplay = "Button clicked: " + mCountServiceCalled + " times";
        mDisplayTextView.setText(textToDisplay);
    }

    private void startServiceWithIntent(){
        Intent incrementCountIntent = new Intent(this, IncrementCountIntentService.class);
        startService(incrementCountIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
