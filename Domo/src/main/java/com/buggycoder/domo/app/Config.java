package com.buggycoder.domo.app;


import android.content.Context;
import android.content.res.Resources;

import com.buggycoder.domo.R;
import com.buggycoder.domo.lib.Logger;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean(scope = EBean.Scope.Singleton)
public class Config {

    private static interface API_ROOT {
        final String ORGANIZATION = "/organizations/";
        final String ADVICEREQUEST = "/organizations/%s/advicerequest/";
        final String PUSH = "/push/";
    }

    @RootContext
    Context context;
    String apiRoot;


    @AfterInject
    public void init() {
        loadApiConfig();
        Logger.d("Config.init");
    }

    private void loadApiConfig() {
        Resources r = context.getResources();
        apiRoot = r.getString(R.string.api_root);
    }

    public String getOrganizationsApi() {
        return apiRoot + API_ROOT.ORGANIZATION;
    }

    public String getSupporteeApi(String orgUrl) {
        return apiRoot + String.format(API_ROOT.ADVICEREQUEST, orgUrl);
    }

    public String getPushApi() {
        return apiRoot + API_ROOT.PUSH;
    }


}