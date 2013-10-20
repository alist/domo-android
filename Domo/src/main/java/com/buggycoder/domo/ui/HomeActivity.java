package com.buggycoder.domo.ui;

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
import com.buggycoder.domo.ui.fragment.SelectOrgFragment;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment_;
import com.buggycoder.domo.ui.helper.PushHelper;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
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

    public static final String TAG_FRAG_SELORG = "selorg";

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


    @Override
    protected void onResume() {
        super.onResume();
        pushHelper.checkPlayServices();
    }


    @AfterViews
    protected void afterViews() {

        pushHelper = new PushHelper(this);
        pushHelper.checkState();

        if(!isExplicitStart) {
            try {
                Dao<MyOrganization, String> myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
                List<MyOrganization> myOrgsList = myOrgDao.queryBuilder().limit(1L).query();
                if(myOrgsList != null && myOrgsList.size() > 0) {
                    OrgActivity_.intent(HomeActivity.this).orgId(myOrgsList.get(0).getId()).start();
                    finish();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        getSlidingMenuHelper().setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);


        joinStatus.setText(MSG_WAIT);
        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelOrgFragment();
            }
        });

        showMyOrganizations();
    }


    protected void onEventMainThread(OrganizationEvents.GetOrganizationsResult o) {
        Logger.d("F:GetOrganizationsResult");
        btnAddCommunity.setEnabled(true);
    }


    protected void onEventMainThread(OrganizationEvents.CheckOrgCodeResult o) {
        Logger.d("F:CheckOrgCodeResult");

        SelectOrgFragment selOrgDialog = getSelOrgFragment();
        if (selOrgDialog != null && selOrgDialog.isVisible()) {
            selOrgDialog.dismiss();
        }

        if (o.result.hasError) {
            Crouton.makeText(this, o.result.errors.get(0), Style.ALERT).show();
            return;
        }

        MyOrganization myOrg = o.result.getResponse();
        Logger.dump(myOrg);

        if (myOrg.getCode().length() > 0) {
            Crouton.makeText(this, "Membership verified.", Style.INFO).show();
            pushHelper.checkState();
            showMyOrganizations();
        }
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

    public SelectOrgFragment getSelOrgFragment() {
        SelectOrgFragment selOrgDialog = (SelectOrgFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_SELORG);

        if (selOrgDialog == null) {
            selOrgDialog = new SelectOrgFragment_();
            selOrgDialog.setRetainInstance(true);
        } else {
            Logger.d("not null");
        }

        return selOrgDialog;
    }

    public void showSelOrgFragment() {
        getSelOrgFragment().show(getSupportFragmentManager(), TAG_FRAG_SELORG);
    }

}
