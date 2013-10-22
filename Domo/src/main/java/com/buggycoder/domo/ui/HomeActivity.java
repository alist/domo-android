package com.buggycoder.domo.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.SupporteeAPI;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.helper.PushHelper;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.sql.SQLException;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_home)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class HomeActivity extends BaseFragmentActivity {

    public static final int ACTIVITY_RESULT_JOIN_COMM = 100;

    @ViewById
    Button btnAddCommunity;

    @ViewById
    LinearLayout llCommunities;

    @ViewById
    TextView joinStatus;

    @SystemService
    LayoutInflater layoutInflater;

    @Bean
    Config config;

    @Extra
    boolean isExplicitStart;

    PushHelper pushHelper;

    private static final String MSG_WAIT = "Please wait...";
    private static final String MSG_NO_COMM = "You have no communities.";

    @AfterViews
    protected void afterViews() {

        if(!isExplicitStart) {
            try {
                Dao<MyOrganization, String> myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
                List<MyOrganization> myOrgsList = myOrgDao.queryBuilder().limit(1L).query();
                if(myOrgsList != null && myOrgsList.size() > 0) {
                    OrgActivity_.intent(HomeActivity.this).orgId(myOrgsList.get(0).getId()).start();
                    finish();
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        pushHelper = new PushHelper(this);
        pushHelper.checkState(false);

        getSlidingMenuHelper().setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);


        joinStatus.setText(MSG_WAIT);
        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOrgActivity_.intent(HomeActivity.this).startForResult(ACTIVITY_RESULT_JOIN_COMM);
            }
        });

//        btnAddCommunity.setEnabled(false);
        showMyOrganizations();
    }


    @Override
    protected void onResume() {
        super.onResume();
        pushHelper.checkPlayServices(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pushHelper.closeDialog();
    }


    protected void onEventMainThread(OrganizationEvents.GetOrganizationsResult o) {
        Logger.d("F:GetOrganizationsResult");
        btnAddCommunity.setEnabled(true);
    }



    @Background
    protected void showMyOrganizations() {

        List<MyOrganization> myOrganizationList = null;
        try {
            Dao<MyOrganization, String> myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            myOrganizationList = myOrgDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(myOrganizationList == null || myOrganizationList.size() == 0) {
            postToUi(new Runnable() {
                @Override
                public void run() {
                    joinStatus.setText(MSG_NO_COMM);
                }
            });

            return;
        }

        final List<MyOrganization> finalMyOrganizationList = myOrganizationList;

        SupporteeAPI.fetchUpdates(config);

        postToUi(new Runnable() {
            @Override
            public void run() {
                llCommunities.removeAllViewsInLayout();

                View v;
                TextView tv;

                for (final MyOrganization o : finalMyOrganizationList) {
                    v = (View) layoutInflater.inflate(android.R.layout.simple_list_item_1, null, true);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OrgActivity_.intent(HomeActivity.this).orgId(o.getId()).start();
                        }
                    });
                    tv = (TextView) v.findViewById(android.R.id.text1);
                    tv.setText(o.getDisplayName());
                    tv.setTag(o.getId());
                    llCommunities.addView(v);
                }
            }
        });

    }

    @OnActivityResult(ACTIVITY_RESULT_JOIN_COMM)
    protected void onJoinCommunityResult(int resultCode, Intent data){

        if(data == null) {
            return;
        }

        if(resultCode == 0){
            Crouton.makeText(this, data.getStringExtra(SelectOrgActivity.EXTRA_ERROR), Style.ALERT).show();
            return;
        } else if(resultCode == 1) {
            pushHelper.checkState(true);
            showMyOrganizations();
            OrgActivity_.intent(HomeActivity.this).orgId(data.getStringExtra(SelectOrgActivity.EXTRA_ORG_ID)).start();
            return;
        }
    }

}
