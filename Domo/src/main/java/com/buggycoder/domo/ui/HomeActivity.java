package com.buggycoder.domo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment_;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.RootContext;
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

    Dao<MyOrganization, String> myOrgDao = null;

    private static final String MSG_WAIT = "Please wait...";
    private static final String MSG_NO_COMM = "You have no communities.";


    @AfterViews
    protected void afterViews() {
        setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        joinStatus.setText(MSG_WAIT);

        try {
            myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showMyOrganizations();

        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelOrgFragment();
            }
        });
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
            showMyOrganizations();
        }
    }

    @Background
    protected void showMyOrganizations() {

        List<MyOrganization> myOrganizationList = null;
        try {
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

        postToUi(new Runnable() {
            @Override
            public void run() {
                llCommunities.removeAllViewsInLayout();

                View v;
                TextView tv;

                for(final MyOrganization o : finalMyOrganizationList) {
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


}
