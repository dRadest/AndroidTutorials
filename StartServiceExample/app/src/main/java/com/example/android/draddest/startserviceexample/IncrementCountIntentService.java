package com.example.android.draddest.startserviceexample;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Class that extends from IntentService and contains logic for service behavior
 */

public class IncrementCountIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public IncrementCountIntentService() {
        super("IncrementCountIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction().equals(MainActivity.ACTION_INCREMENT_COUNT)){
            MainActivity.incrementCount();
            // we add this call so notification gets dismissed after we increment the count
            MainActivity.clearAllNotifications();
        } else if (intent.getAction().equals(MainActivity.ACTION_DISMISS_NOTIFICATION)){
            MainActivity.clearAllNotifications();
        }
    }
}
