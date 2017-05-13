package com.example.android.draddest.startserviceexample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import static android.R.attr.action;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static Context mContext;

    /* views used */
    private Button mStartServiceButton;
    private TextView mDisplayTextView;
    private Button mNotificationButton;

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

    /* constant variables for the scheduled job */
    private static final int NOTIFICATION_MINUTES = 1;
    private static final int NOTIFICATION_SECONDS = (int) TimeUnit.MINUTES.toSeconds(NOTIFICATION_MINUTES);
    private static final int FLEXTIME_SECONDS = NOTIFICATION_SECONDS;

    /* static boolean to keep track if job has been initialized */
    private static boolean sJobInitialized;

    /* instance variables for dynamic broadcast receiver */
    IntentFilter mWifiIntentFilter;
    BroadcastReceiver mWifiReceiver;

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
        mNotificationButton = (Button) findViewById(R.id.notification_button);

        mContext = getApplicationContext();

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        /* schedule the job */
        scheduleNotificationsOnWifi();

        /* instantiate intent filter */
        mWifiIntentFilter = new IntentFilter();
        /* add action indicating there was a change in connectivity */
        mWifiIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        /* instantiate broadcast receiver */
        mWifiReceiver = new WifiBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* register our receiver */
        registerReceiver(mWifiReceiver, mWifiIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiReceiver);
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
        String textToDisplay = getResources().getQuantityString(R.plurals.service_started_count,
                mCountServiceCalled, mCountServiceCalled);
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

    /* This method will create a notification when the button is pressed */
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

    /* we use this method to create a notification with job scheduler*/
    public static void issueNotificationOnWifi (){
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
                        .setContentTitle("Increment Time On WiFi")
                        // - sets the text
                        .setContentText("Click on this notification to open MainActivity")
                        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                        .setStyle(new NotificationCompat.BigTextStyle().
                                bigText("Increment Time On WiFi"))
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

    /* helper method to create the job and schedule it */
    synchronized private void scheduleNotificationsOnWifi(){
        // if job's already started, we bail early
        if (sJobInitialized) return;

        /* create dispatcher */
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* create a job and build it */
        Job wifiNotificationsJob = dispatcher.newJobBuilder()
                // set service to IncrementFirebaseJobService
                .setService(IncrementFirebaseJobService.class)
                // unique tag for the job
                .setTag("wifi_notifications_tag")
                // executes only when on a unmetered network (WiFi)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                // job only persists until reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // job recurrs
                .setRecurring(true)
                // set execution window in which the system executes the job
                .setTrigger(Trigger.executionWindow(
                        NOTIFICATION_SECONDS,
                        NOTIFICATION_SECONDS + FLEXTIME_SECONDS))
                // if the job's ever remade, replace old with new one
                .setReplaceCurrent(true)
                .build();
        /* schedule the job */
        dispatcher.schedule(wifiNotificationsJob);
        sJobInitialized = true;
    }

    /* helper method to change the visibility of the notification button */
    private void showNotificationButton(boolean onWifi){
        if (onWifi){
            mNotificationButton.setVisibility(View.GONE);
        } else {
            mNotificationButton.setVisibility(View.VISIBLE);
        }
    }

    /* inner clas for our broadcast receiver */
    private class WifiBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /* boolean variable to use in showNotificationButton() */
            boolean onWifi = false;
            /* get the action in intent */
            final String action = intent.getAction();
            /* if action the same as the one registered in intent filter */
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                /* get the ConnectivityManager and the NetworkInfo */
                ConnectivityManager conMan = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                /* requires permission ACCESS_NETWORK_STATE in the manifest */
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                /* if it's not null and there is a connection */
                if (netInfo != null && netInfo.isConnected()){
                    /* we can check if it's of the type WiFi */
                    onWifi = (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
                }
            }
            showNotificationButton(onWifi);
        }
    }
}
