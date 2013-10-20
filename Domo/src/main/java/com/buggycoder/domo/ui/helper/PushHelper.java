package com.buggycoder.domo.ui.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.PushAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.app.Config_;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.greenrobot.event.util.AsyncExecutor;

/**
 * Created by shirish on 20/10/13.
 */
public class PushHelper {

    public static final String PREF_REG_ID = "reg-id";
    public static final String PREF_APP_VER = "app-ver";
    public static final String PREF_APP_PACKAGE = "com.buggycoder.domo";
    BaseFragmentActivity activity;

    // Push
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    final String SENDER_ID;
    GoogleCloudMessaging gcm;


    public PushHelper(BaseFragmentActivity activity) {
        this.activity = activity;
        SENDER_ID = activity.getString(R.string.push_sender_id);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            String regId = getRegistrationId(activity.getApplicationContext());

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Logger.d("No valid Google Play Services APK found.");
        }
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Logger.d("This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PREF_REG_ID, "");
        if (registrationId.isEmpty()) {
            Logger.d("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PREF_APP_VER, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Logger.dump("App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return activity.getSharedPreferences(PREF_APP_PACKAGE, Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    protected void registerInBackground() {
        final Context context = activity.getApplicationContext();
        AsyncExecutor.create().execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
            }
        });
    }


    private void sendRegistrationIdToBackend(String regId) {
        try {
            PushAPI.register((Config) Config_.getInstance_(activity), regId, "mit", "mit9");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Logger.d("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_REG_ID, regId);
        editor.putInt(PREF_APP_VER, appVersion);
        editor.commit();
    }
}
