package com.buggycoder.domo.app;

import android.app.Application;

import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.helper.AppStateHelper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

/**
 * Created by shirish on 14/6/13.
 */

@EApplication
public class MainApplication extends Application {

    @Bean
    Config config;

    @Bean
    AppBootstrap appBootstrap;

    AppStateHelper appStateHelper;


    @AfterInject
    protected void init() {
        Logger.d("App.init: " + this.getPackageName());
        appStateHelper = new AppStateHelper();
    }

    public Config getConfig() {
        return config;
    }

    public AppStateHelper getAppStateHelper() {
        return appStateHelper;
    }
}
