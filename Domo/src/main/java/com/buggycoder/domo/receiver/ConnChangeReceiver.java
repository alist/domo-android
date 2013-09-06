package com.buggycoder.domo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.buggycoder.domo.events.Network;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.NetworkUtils;


/**
 * Created by shirish on 17/6/13.
 */
public class ConnChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = NetworkUtils.isConnected(context);
        new Network.NetworkChange(NetworkUtils.getNetworkType(context), isConnected).publish();
        Logger.d("onReceive: " + isConnected);
    }
}
