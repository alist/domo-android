package com.buggycoder.domo.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shirish on 14/10/13.
 */
public class Prefs {

    public interface Keys {
        public static final String PUSH_REG_ID = "push-reg-id";
        public static final String PUSH_REG_COMPLETE = "push-reg-complete";
        public static final String PUSH_REG_TS = "push-reg-ts";
        public static final String PUSH_SUBSCRIBER_ID = "push-subscriber-id";
        public static final String PUSH_DEVICE_ID = "push-device-id";
        public static final String APP_VER = "app-ver";
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void put(Context context, String key, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String get(Context context, String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(key, defaultValue);
    }
}

