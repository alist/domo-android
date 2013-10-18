package com.buggycoder.domo.ui;

import android.view.Window;
import android.widget.ImageView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.sql.SQLException;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_org_home)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class OrgActivity extends BaseFragmentActivity {

    @ViewById
    ImageView orgBanner;

    @Extra
    String orgId;

    @Bean
    Config config;

    Dao<MyOrganization, String> myOrgDao;
    MyOrganization myOrg;


    @AfterViews
    protected void afterViews() {
        loadOrganization();
    }

    @Background
    protected void loadOrganization() {
        try {
            myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            myOrg = myOrgDao.queryForId(orgId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(myOrg != null) {
            postToUi(new Runnable() {
                @Override
                public void run() {
                    Picasso
                        .with(OrgActivity.this)
                        .load(config.getSiteRoot() + myOrg.getBannerURL())
                        .resize(500, 200)
                        .centerCrop()
                        .into(orgBanner);
                }
            });
        }
    }

}
