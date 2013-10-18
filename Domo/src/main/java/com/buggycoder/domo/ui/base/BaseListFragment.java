package com.buggycoder.domo.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 18/10/13.
 */
public class BaseListFragment extends SherlockListFragment {

    boolean enablePubSub = false;

    @Override
    public void onResume() {
        if(enablePubSub) {
            PubSub.subscribe(this);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if(enablePubSub) {
            PubSub.unsubscribe(this);
        }

        super.onPause();
    }

    public boolean isPubSubEnabled() {
        return enablePubSub;
    }

    public void setEnablePubSub(boolean enablePubSub) {
        this.enablePubSub = enablePubSub;
    }

    protected void openActivity(Class clazz) {
        openActivity(clazz, null, false);
    }

    protected void openActivity(Class clazz, Bundle extras, boolean finish) {
        SherlockFragmentActivity context = getSherlockActivity();
        PubSub.unsubscribe(this);
        Intent intent = new Intent(context, clazz);
        if (extras != null) {
            intent.putExtras(extras);
        }

        context.startActivity(intent);
        if (finish) {
            context.finish();
        }
    }


    protected void onEvent(Object o) {

    }

    protected void onEventMainThread(Object o) {

    }
}
