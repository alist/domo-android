package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by shirish on 7/9/13.
 */

@EActivity(R.layout.activity_splash)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class SplashActivity extends BaseFragmentActivity {

    @ViewById
    Button btnGetAdvice;

    @AfterViews
    protected void afterViews() {
        btnGetAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAdvice();
            }
        });
    }

    @Background
    protected void getAdvice() {
        try {
            Dao<MyOrganization, String> myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            List<MyOrganization> myOrgs = myOrgDao.queryForAll();
            if (myOrgs.size() > 0) {

                Bundle bundle = new Bundle();
                bundle.putString(GetAdviceActivity.KEY_ORGURL, myOrgs.get(0).getOrgURL());
                bundle.putString(GetAdviceActivity.KEY_ORGCODE, myOrgs.get(0).getCode());
                bundle.putString(GetAdviceActivity.KEY_ORGDISPLAYNAME, myOrgs.get(0).getDisplayName());
                openActivity(GetAdviceActivity_.class, bundle, true);

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        openActivity(CodeCheckActivity_.class, true);
    }
}
