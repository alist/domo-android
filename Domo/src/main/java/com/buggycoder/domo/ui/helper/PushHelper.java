package com.buggycoder.domo.ui.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.PushAPI;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.app.Config_;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.db.Prefs;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.util.AsyncExecutor;

/**
 * Created by shirish on 20/10/13.
 */
public class PushHelper {

    BaseFragmentActivity activity;

    // Push
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    final String SENDER_ID;
    GoogleCloudMessaging gcm;
    Dialog googlePlayServiceDialog;

    public PushHelper(BaseFragmentActivity activity) {
        this.activity = activity;
        SENDER_ID = activity.getString(R.string.push_sender_id);
    }

    public void closeDialog() {
        if(googlePlayServiceDialog != null && googlePlayServiceDialog.isShowing()) {
            googlePlayServiceDialog.dismiss();
        }
    }

    public void checkState(boolean silent) {
        if (checkPlayServices(silent)) {
            Context context = activity.getApplicationContext();
            gcm = GoogleCloudMessaging.getInstance(activity);
            String regId = getRegistrationId(context);

            if (regId.isEmpty() || shouldRefreshRegId(context)) {
                Logger.d("registerInBackground");
                registerInBackground();
            } else if(!Prefs.getBoolean(context, Prefs.Keys.PUSH_REG_COMPLETE, false)) {
                Logger.d("sendRegistrationIdToBackend");
                sendRegistrationIdToBackend(context, regId);
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
            Logger.e(e);
        }

        return -1;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices(boolean silent) {

        if(googlePlayServiceDialog != null && googlePlayServiceDialog.isShowing()) {
            return false;
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (!silent && GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                googlePlayServiceDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST);
                googlePlayServiceDialog.show();
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
        final SharedPreferences prefs = Prefs.getSharedPreferences(context);
        String registrationId = prefs.getString(Prefs.Keys.PUSH_REG_ID, "");
        if (registrationId.isEmpty()) {
            Logger.d("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Prefs.Keys.APP_VER, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Logger.dump("App version changed.");
            return "";
        }
        return registrationId;
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
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regId = gcm.register(SENDER_ID);
                    sendRegistrationIdToBackend(context, regId);

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    Logger.e(ex);
                }
            }
        });
    }


    private void sendRegistrationIdToBackend(final Context context, final String regId) {

        AsyncExecutor.create().execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {

                final Config config = (Config) Config_.getInstance_(activity);

                if(Prefs.getBoolean(context, Prefs.Keys.PUSH_REG_COMPLETE, false)) {
                    // update regId
                    Logger.d("push.update");
                    PushAPI.update(config, regId);
                    return;
                }

                // register device
                try {
                    Dao<MyOrganization, String> myOrganizationDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
                    List<MyOrganization> myOrgList = myOrganizationDao.queryBuilder().limit(1L).query();

                    if(myOrgList.size() > 0) {
                        Logger.d("push.register");
                        MyOrganization myOrg = myOrgList.get(0);
                        PushAPI.register(config, regId, myOrg.getOrgURL(), myOrg.getCode());
                    }

                } catch (UnsupportedEncodingException e) {
                    Logger.e(e);
                } catch (SQLException e) {
                    Logger.e(e);
                }
            }
        });
    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = Prefs.getSharedPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Prefs.Keys.PUSH_REG_ID, regId);
        editor.putInt(Prefs.Keys.APP_VER, appVersion);
        editor.commit();
    }

    private boolean shouldRefreshRegId(Context context) {
        final SharedPreferences prefs = Prefs.getSharedPreferences(context);
        long regTs = prefs.getLong(Prefs.Keys.PUSH_REG_TS, 0);
        if(regTs == 0) {
            return true;
        }

        long diff = new Date().getTime() - regTs;
        int days = Math.round(diff / (1000 * 60 * 60 * 24));

        int configRefreshInterval = context.getResources().getInteger(R.integer.push_refresh_interval_days);
        if(days >= configRefreshInterval) {
            return true;
        }

        Logger.d("Last refreshed %d days ago", days);
        return false;
    }
}
