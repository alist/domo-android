package com.buggycoder.domo.ui.base;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.buggycoder.domo.events.Network;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class BaseFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onResume() {
        Logger.d("onResume: " + this.getLocalClassName());
        PubSub.subscribe(this);
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
        PubSub.unsubscribe(this);
        Intent intent = new Intent(this, clazz);
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
}