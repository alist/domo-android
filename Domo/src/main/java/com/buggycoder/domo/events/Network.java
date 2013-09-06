package com.buggycoder.domo.events;


import com.buggycoder.domo.lib.NetworkUtils;

import java.util.Date;

/**
 * Created by shirish on 17/6/13.
 */
public class Network {

    public static class NetworkChange extends Event {
        public static final int DUPLICATE_THRESHOLD = 1000;
        public static NetworkChange lastNetworkChange;

        public NetworkUtils.NetworkType networkType;
        public boolean isConnected;
        public long timestamp;

        public NetworkChange(NetworkUtils.NetworkType networkType, boolean connected) {
            this.networkType = networkType;
            isConnected = connected;
            timestamp = new Date().getTime();
        }

        public boolean isDuplicate() {
            return (lastNetworkChange != null
                    && lastNetworkChange.isConnected == this.isConnected
                    && lastNetworkChange.networkType.equals(this.networkType)
                    && lastNetworkChange.timestamp - this.timestamp <= DUPLICATE_THRESHOLD);
        }

        @Override
        public void publish() {
            if (isDuplicate()) return;
            super.publish();
            NetworkChange.lastNetworkChange = this;
        }

    }
}
