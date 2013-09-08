package com.buggycoder.domo.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 16/6/13.
 */
public abstract class BaseFragment extends SherlockFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PubSub.subscribe(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        PubSub.unsubscribe(this);
        super.onDestroy();
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
