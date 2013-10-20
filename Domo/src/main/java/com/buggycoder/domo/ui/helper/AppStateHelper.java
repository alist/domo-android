package com.buggycoder.domo.ui.helper;

import com.buggycoder.domo.events.UIEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shirish on 21/10/13.
 */
public class AppStateHelper {

    private Timer mActivityTransitionTimer = new Timer();
    private TimerTask mActivityTransitionTimerTask = newTimerTask();

    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

    public void start() {
        mActivityTransitionTimer.schedule(mActivityTransitionTimerTask, MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stop() {
        if (mActivityTransitionTimerTask != null) {
            mActivityTransitionTimerTask.cancel();
            mActivityTransitionTimerTask = newTimerTask();
        }

        if (mActivityTransitionTimer != null) {
            mActivityTransitionTimer.cancel();
            mActivityTransitionTimer = new Timer();
        }

        wasInBackground = false;
    }

    private TimerTask newTimerTask() {
        return new TimerTask() {
            public void run() {
                Logger.d("Publishing background event");
                PubSub.publish(new UIEvents.AppInBackground());
                AppStateHelper.this.wasInBackground = true;
            }
        };
    }

}
