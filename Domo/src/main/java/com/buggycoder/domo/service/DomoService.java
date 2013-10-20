package com.buggycoder.domo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.SupporteeAPI;
import com.buggycoder.domo.app.Config_;
import com.buggycoder.domo.events.UIEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by shirish on 21/10/13.
 */

public class DomoService extends Service {

    private static final String TAG = DomoService.class.getSimpleName();

    private Timer timer;
    AtomicBoolean isRunning = new AtomicBoolean(false);

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            isRunning.set(true);
            Logger.d("Fetching updates");
            SupporteeAPI.fetchUpdates(Config_.getInstance_(DomoService.this));
            isRunning.set(false);
        }

        @Override
        public boolean cancel() {
            isRunning.set(false);
            return super.cancel();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("Starting service");

        PubSub.subscribe(this);

        timer = new Timer("FetchUpdates");
        int refreshIntvl = getResources().getInteger(R.integer.refresh_interval);
        timer.schedule(updateTask, 1000L, refreshIntvl  * 1000L);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("Killing service");

        PubSub.unsubscribe(this);

        timer.cancel();
        timer = null;
    }

    protected void onEvent(UIEvents.AppInBackground o) {
        Logger.d("Stopping service");
        stopSelf();
    }
}