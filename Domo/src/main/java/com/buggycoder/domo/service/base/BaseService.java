package com.buggycoder.domo.service.base;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 16/6/13.
 */
public abstract class BaseService extends Service {
    private final IBinder mBinder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service.START_STICKY is used for services which are explicit started or stopped.
        // If these services are terminated by the Android system, they are restarted if sufficient resource are available again.
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PubSub.subscribe(this);
    }

    @Override
    public void onDestroy() {
        PubSub.unsubscribe(this);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        BaseService getService() {
            return BaseService.this;
        }
    }
}
