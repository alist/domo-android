package com.buggycoder.domo.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.buggycoder.domo.app.MainApplication;
import com.buggycoder.domo.events.Network;
import com.buggycoder.domo.events.UIEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.service.DomoService;
import com.buggycoder.domo.ui.helper.SlidingMenuHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public abstract class BaseFragmentActivity extends SherlockFragmentActivity {

    SlidingMenuHelper slidingMenuHelper;

    Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, DomoService.class));
        slidingMenuHelper = new SlidingMenuHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("onResume: " + this.getLocalClassName());
        PubSub.subscribe(this);

        MainApplication app = (MainApplication) getApplication();
        if(app != null) {
            app.getAppStateHelper().stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("onPause: " + this.getLocalClassName());
        PubSub.unsubscribe(this);

        MainApplication app = (MainApplication) getApplication();
        if(app != null) {
            app.getAppStateHelper().start();
        }
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

    public SlidingMenuHelper getSlidingMenuHelper() {
        return slidingMenuHelper;
    }
}