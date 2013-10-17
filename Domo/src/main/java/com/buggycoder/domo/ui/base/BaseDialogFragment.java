package com.buggycoder.domo.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 18/10/13.
 */
public class BaseDialogFragment extends SherlockDialogFragment {

    boolean enablePubSub = false;
    boolean isRetained = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(enablePubSub) {
            PubSub.subscribe(this);
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(enablePubSub) {
            PubSub.unsubscribe(this);
        }
        super.onDismiss(dialog);
    }


    @Override
    public void onDestroyView() {
        if(getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public boolean isPubSubEnabled() {
        return enablePubSub;
    }

    public void setEnablePubSub(boolean enablePubSub) {
        this.enablePubSub = enablePubSub;
    }

}
