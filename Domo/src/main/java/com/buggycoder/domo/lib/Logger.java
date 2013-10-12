package com.buggycoder.domo.lib;

import android.util.Log;

/**
 * Created by shirish on 14/6/13.
 */
public class Logger {


    private static final String TAG = "Domo";

    private Logger() {
    }

    public static void ps(String msg, Object... args) {
//        msg = "_pubsub_ | " + msg;
//        d(msg, args);
    }

    public static void dd(String msg, Object... args) {
        d(msg, args);
    }

    public static void d(String msg, Object... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.d(TAG, msg);
    }

    public static void e(Throwable t, String msg, Object... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg, args);
        }
        Log.e(TAG, msg, t);
    }

    public static void dump(Object o) {
        if (o == null) {
            Logger.d("Logger.dump: null");
            return;
        }

        try {
            Logger.d(o.getClass().getSimpleName() + " | " + JsonManager.getUnsafeMapper().writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
