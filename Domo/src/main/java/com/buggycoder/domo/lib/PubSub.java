package com.buggycoder.domo.lib;

import de.greenrobot.event.EventBus;

/**
 * Created by shirish on 26/7/13.
 */
public class PubSub {

    public static <T> void subscribe(T o) {
        subscribe(o, true);
    }

    public static <T> void subscribe(T o, boolean sticky) {
        Logger.ps("subscribe %s", o.getClass().getSimpleName());
        try {
            if (sticky) {
                EventBus.getDefault().registerSticky(o);
            } else {
                EventBus.getDefault().register(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void unsubscribe(T o) {
        Logger.ps("unsubscribe %s", o.getClass().getSimpleName());
        try {
            EventBus.getDefault().unregister(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void publish(T o) {
        Logger.ps("publish %s", o.getClass().getSimpleName());
        try {
            EventBus.getDefault().post(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
