package com.buggycoder.domo.app;

import android.app.Application;

import com.buggycoder.domo.lib.Config;
import com.buggycoder.domo.lib.Logger;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EApplication;

/**
 * Created by shirish on 14/6/13.
 */

@EApplication
public class MainApplication extends Application {

    @Bean
    Config config;

    @Bean
    AppBootstrap appBootstrap;

    @AfterInject
    protected void init() {
        Logger.d("App.init: " + this.getPackageName());
    }

    public Config getConfig() {
        return config;
    }
}
