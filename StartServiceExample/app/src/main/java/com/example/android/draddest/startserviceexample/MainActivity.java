package com.example.android.draddest.startserviceexample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    /* action tag for the intent to start service */
    public static final String ACTION_INCREMENT_COUNT = "increment-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dimiss-notification";

    /* id variables for pending intent and notification */
    private static final int INCREMENT_PENDING_INTENT_ID = 211;
    private static final int INCREMENT_NOTIFICATION_ID = 187;
    private static final int DISMISS_PENDING_INTENT_ID = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* find the views and implement the logic for updating the counter and the display */
        mDisplayTextView = (TextView) findViewById(R.id.textView);
        /* set the appropriate text in the UI */
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
        incrementCountIntent.setAction(ACTION_INCREMENT_COUNT);
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

    /* method invoked when the notification button is clicked */
    public void displayNotification (View view){
        issueNotification();
    }

    /* This method will create the pending intent which will trigger when
        the notification is pressed. This pending intent should open up the MainActivity. */
    private static PendingIntent contentIntent (){
        // intent that opens MainActivity
        Intent startActivityIntent = new Intent(mContext, MainActivity.class);
        // return PendingIntent using getActivity
        return PendingIntent.getActivity(mContext,
                INCREMENT_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /* This method is necessary to decode a bitmap needed for the notification */
    private static Bitmap largeIcon(){
        // get a Resources object from the context
        Resources res = mContext.getResources();
        /* return a bitmap using BitmapFactory.decodeResource */
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.plus_add_green);
        return largeIcon;
    }

    /* This method will create a notification for charging */
    public static void issueNotification (){
        // use builder to describe notifation that:
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext)
                        // - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        // - has ic_exposure_plus_1_black_24dp as the small icon
                        .setSmallIcon(R.drawable.ic_exposure_plus_1_black_24dp)
                        // - uses icon returned by the largeIcon helper method as the large icon
                        .setLargeIcon(largeIcon())
                        // - sets the title
                        .setContentTitle("Increment Time")
                        // - sets the text
                        .setContentText("Click on this notification to open MainActivity")
                        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                        .setStyle(new NotificationCompat.BigTextStyle().
                                bigText("Increment Time"))
                        // - sets the notification defaults to vibrate (add permission to Manifest!)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        // - uses the content intent returned by the contentIntent helper method for the contentIntent
                        .setContentIntent(contentIntent())
                        // - has action to dismiss the notification
                        .addAction(dismissrAction())
                        // * has action to increment the count
                        .addAction(incrementAction())
                        // - automatically cancels the notification when the notification is clicked
                        .setAutoCancel(true);
        /*If the build version is greater than JELLY_BEAN, set the notification's priority
         to PRIORITY_HIGH */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        /* display notification using NotificationManager */
        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(INCREMENT_NOTIFICATION_ID, notificationBuilder.build());
    }

    /* helper method to clear all notifications */
    public static void clearAllNotifications(){
        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /* helper method that returns action to dismiss notification */
    private static NotificationCompat.Action dismissrAction(){
        // intent to launch IncrementCountIntentService
        Intent dismissIntent = new Intent(mContext, IncrementCountIntentService.class);
        // action of the intent designates we want to dismiss the notification
        dismissIntent.setAction(ACTION_DISMISS_NOTIFICATION);
        // PendingIntent from the intent to launch IncrementCountIntentService
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                mContext,
                DISMISS_PENDING_INTENT_ID,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Action for the user to ignore the notification (and dismiss it)
        NotificationCompat.Action dismissAction = new NotificationCompat.Action(
                R.drawable.ic_cancel_black_24dp,
                "Dismiss",
                ignoreReminderPendingIntent);
        // return the action
        return dismissAction;
    }


    /* helper method that returns action to increment count from notification */
    public static NotificationCompat.Action incrementAction(){
        // Intent to launch IncrementCountIntentService
        Intent incrementCountIntent = new Intent(mContext, IncrementCountIntentService.class);
        // action of the intent designates we want to increment the count
        incrementCountIntent.setAction(ACTION_INCREMENT_COUNT);
        // PendingIntent from the intent to launch IncrementCountIntentService
        PendingIntent dismissPendingIntent = PendingIntent.getService(
                mContext,
                INCREMENT_PENDING_INTENT_ID,
                incrementCountIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Action for the user to tell us they've incremented the count
        NotificationCompat.Action incrementAction = new NotificationCompat.Action(
                R.drawable.ic_exposure_plus_1_black_24dp,
                "Increment",
                dismissPendingIntent);
        // return the action
        return incrementAction;
    }
}
