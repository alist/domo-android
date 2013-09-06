package com.buggycoder.domo.lib;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class Config {


    @AfterInject
    public void init() {
//        loadAuthProps();
    }

    public void loadAuthProps() {
//        Resources resources = mContext.getResources();
//        AssetManager assetManager = resources.getAssets();
//        try {
//            ObjectMapper mapper = JsonManager.getUnsafeMapper();
//            authConfig = mapper.readValue(assetManager.open("auth.json"), AuthConfig.class);
//            Logger.d("authConfig", authConfig);
//        } catch (IOException e) {
//            System.err.println("Failed to open /assets/auth.json");
//            e.printStackTrace();
//        }
    }
}