package com.example.android.draddest.startserviceexample;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Service that extends from firebase job service
 */

public class IncrementFirebaseJobService extends JobService {
    private AsyncTask mBackgroundTask;
    /* executes task that sends out notification on a background thread */
    @Override
    public boolean onStartJob(final JobParameters job) {
        /* by default, jobs are executed on main thread
         * to do our work on a background thread, we use AsyncTask anonymously
         */
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                MainActivity.issueNotificationOnWifi();
                return null;
            }

            /* this method gets called when AsyncTask is finished
             *  it's used to report when job is finished
             */
            @Override
            protected void onPostExecute(Object o) {
                /* JobServices need to tell the system when the job is done by calling jobFinished() */
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    /* gets called if the requirements of the job are no longer met
     * for e.g. Wi-Fi connection gets lost while downloading
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        /* if mBackgroundTask valid, cancel it */
        if (mBackgroundTask != null){
            mBackgroundTask.cancel(true);
        }
        /* as soon as the conditions are re-met, the job should be retried*/
        return true;
    }
}
