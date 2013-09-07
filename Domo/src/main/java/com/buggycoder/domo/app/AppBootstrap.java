package com.buggycoder.domo.app;

import android.content.Context;

import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.db.DaoManager;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.RequestManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by shirish on 5/9/13.
 */

@EBean(scope = EBean.Scope.Singleton)
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
        daoManager.registerDaoClass(AdviceRequest.class);
        daoManager.registerDaoClass(Advice.class);
    }
}
