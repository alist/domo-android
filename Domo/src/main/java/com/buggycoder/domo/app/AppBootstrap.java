package com.buggycoder.domo.app;

import android.content.Context;

import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.db.DaoManager;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.RequestManager;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.api.Scope;

/**
 * Created by shirish on 5/9/13.
 */

@EBean(scope = Scope.Singleton)
public class AppBootstrap {

    @RootContext
    Context context;

    public AppBootstrap() {
    }

    @AfterInject
    public void init() {
        RequestManager.init(context);
        initDb();
        Logger.d("AppBootstrap.init");
    }

    public void initDb() {
        DatabaseHelper.init(context);
        DaoManager daoManager = DatabaseHelper.getDaoManager();
        daoManager.registerDaoClass(Organization.class);
    }
}
