package com.buggycoder.domo.ui;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
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
    TextView orgDisplayName;

    @ViewById
    ImageView orgBanner;

    @ViewById
    Button askAdvice;

    @ViewById
    Button btnMyQuestions;

    @Extra
    String orgId;
    @Bean
    Config config;

    @ViewById
    ImageView menuToggle;

    Dao<MyOrganization, String> myOrgDao;
    MyOrganization myOrg;

    @AfterViews
    protected void afterViews() {
        getSlidingMenuHelper().setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);
        loadOrganization();

        askAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetAdviceActivity_.intent(OrgActivity.this)
                        .orgURL(myOrg.getOrgURL())
                        .orgCode(myOrg.getCode())
                        .orgDisplayName(myOrg.getDisplayName())
                        .start();
            }
        });

        btnMyQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyQuestionsActivity_.intent(OrgActivity.this).organization(myOrg.getId()).start();
                MyQuestionsActivity_.intent(OrgActivity.this).start();
            }
        });

        menuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSlidingMenuHelper().toggleSlidingMenu();
            }
        });
    }

    @Background
    protected void loadOrganization() {
        try {
            myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            myOrg = myOrgDao.queryForId(orgId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (myOrg != null) {
            postToUi(new Runnable() {
                @Override
                public void run() {
                    orgDisplayName.setText(myOrg.getDisplayName());
                    String orgBannerURL = myOrg.getBannerURL();
                    if (!TextUtils.isEmpty(orgBannerURL)) {
                        Picasso.with(OrgActivity.this)
                            .load(myOrg.getBannerURL())
                            .resize(500, 200)
                            .placeholder(R.drawable.ic_launcher)
                            .centerCrop()
                            .into(orgBanner);
                    }
                }
            });
        }
    }

}
