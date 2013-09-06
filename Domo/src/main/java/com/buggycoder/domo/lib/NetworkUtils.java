package com.buggycoder.domo.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/*
 * Copyright 2011 Peter Kuterna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by shirish on 17/6/13.
 */

public class NetworkUtils {

    /**
     * Check if connected to a WiFi network.
     */
    public static boolean isConnectedToWifi(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    /**
     * Check if connected to a network (WiFi included).
     */
    public static boolean isConnected(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static NetworkType getNetworkType(Context context) {
        NetworkInfo networkInfo
                = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null) {
            int networkType = networkInfo.getType();
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.WIFI;
            } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
                return NetworkType.MOBILE;
            } else if (networkType == 6) {  // ConnectivityManager.TYPE_WIMAX since API level 8
                return NetworkType.WIMAX;
            }
        }
        return NetworkType.NONE;
    }

    public enum NetworkType {
        WIFI,
        MOBILE,
        WIMAX,
        NONE
    }

}
