package com.buggycoder.domo.ui.base;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;
import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 16/6/13.
 */
public abstract class BaseFragment extends SherlockFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PubSub.subscribe(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PubSub.unsubscribe(this);
    }

//
//    protected void openActivity(Class clazz) {
//        openActivity(clazz, false);
//    }

//    protected void openActivity(Class clazz, boolean finish) {
//        SherlockFragmentActivity context = getSherlockActivity();
//        PubSub.unsubscribe(this);
//        Intent intent = new Intent(context, clazz);
//        context.startActivity(intent);
//        if (finish) {
//            context.finish();
//        }
//    }


    protected void onEvent(Object o) {
    }

    protected void onEventMainThread(Object o) {
    }
}
