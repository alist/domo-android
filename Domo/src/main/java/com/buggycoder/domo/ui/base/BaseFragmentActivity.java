package com.buggycoder.domo.ui.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.buggycoder.domo.R;
import com.buggycoder.domo.api.PushAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.app.Config_;
import com.buggycoder.domo.events.Network;
import com.buggycoder.domo.events.UIEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment_;
import com.buggycoder.domo.ui.helper.PushHelper;
import com.buggycoder.domo.ui.helper.SlidingMenuHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public abstract class BaseFragmentActivity extends SherlockFragmentActivity {

    PushHelper pushHelper;
    SlidingMenuHelper slidingMenuHelper;

    Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pushHelper = new PushHelper(this);
        slidingMenuHelper = new SlidingMenuHelper(this);
    }

    @Override
    protected void onResume() {
        Logger.d("onResume: " + this.getLocalClassName());
        PubSub.subscribe(this);
        pushHelper.checkPlayServices();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Logger.d("onPause: " + this.getLocalClassName());
        PubSub.unsubscribe(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

    protected void openActivity(Class clazz, boolean finish) {
        openActivity(clazz, null, finish);
    }

    protected void openActivity(Class clazz, Bundle extras, boolean finish) {
        PubSub.unsubscribe(this);
        Intent intent = new Intent(this, clazz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    protected void onEvent(Object o) {
    }

    protected void onEventMainThread(Object o) {
    }

    protected void onEventMainThread(Network.NetworkChange o) {
        Crouton.makeText(this,
                o.isConnected ? "Connected" : "Disconnected",
                o.isConnected ? Style.INFO : Style.ALERT).show();
    }

    protected void onEventMainThread(UIEvents.SlidingMenuItemSelected o) {
        slidingMenuHelper.closeSlidingMenu(false);
    }


    protected void postToUi(Runnable r) {
        handler.post(r);
    }

    @Override
    public void onBackPressed() {
        if(!slidingMenuHelper.handleMenuShowing()) {
            super.onBackPressed();
        }
    }

    public PushHelper getPushHelper() {
        return pushHelper;
    }

    public SlidingMenuHelper getSlidingMenuHelper() {
        return slidingMenuHelper;
    }
}