package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment_;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
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

    private static final String TAG_FRAG_SELORG = "selorg";

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
    Dao<Organization, String> orgDao = null;

    private static final String MSG_WAIT = "Please wait...";
    private static final String MSG_NO_COMM = "You have no communities.";



    @AfterViews
    protected void afterViews() {
        Logger.d("afterViews");
        setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        joinStatus.setText(MSG_NO_COMM);

        try {
            myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            orgDao = DatabaseHelper.getDaoManager().getDao(Organization.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        checkLocalOrganizations();
        fetchAllOrganizations();

        showMyOrganizations();

        btnAddCommunity.setEnabled(false);
        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelOrgFragment().show(getSupportFragmentManager(), TAG_FRAG_SELORG);
            }
        });
    }

    @Background
    protected void checkLocalOrganizations() {
        if(orgDao == null) {
            return;
        }

        try {
            final long orgCount = orgDao.countOf();
            postToUi(new Runnable() {
                @Override
                public void run() {
                    if(orgCount == 0) {
                        btnAddCommunity.setEnabled(false);
                    } else {
                        btnAddCommunity.setEnabled(true);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Background
    protected void fetchAllOrganizations() {
        OrganizationAPI.getOrganizations(config);
    }

    protected SelectOrgFragment getSelOrgFragment() {
        SelectOrgFragment selOrgDialog = (SelectOrgFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_SELORG);

        if (selOrgDialog == null) {
            selOrgDialog = new SelectOrgFragment_();
            selOrgDialog.setRetainInstance(true);
        } else {
            Logger.d("not null");
        }

        return selOrgDialog;
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

//            Bundle bundle = new Bundle();
//            bundle.putString(GetAdviceActivity.KEY_ORGURL, myOrg.getOrgURL());
//            bundle.putString(GetAdviceActivity.KEY_ORGCODE, myOrg.getCode());
//            bundle.putString(GetAdviceActivity.KEY_ORGDISPLAYNAME, myOrg.getDisplayName());
//
//            openActivity(GetAdviceActivity_.class, bundle, true);
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

        if(myOrganizationList != null && myOrganizationList.size() > 0) {

            final List<MyOrganization> finalMyOrganizationList = myOrganizationList;

            postToUi(new Runnable() {
                @Override
                public void run() {
                    llCommunities.removeAllViewsInLayout();

                    View v;
                    TextView tv;

                    for(MyOrganization o : finalMyOrganizationList) {
                        v = (View) layoutInflater.inflate(android.R.layout.simple_list_item_1, null, true);
                        tv = (TextView) v.findViewById(android.R.id.text1);
                        tv.setText(o.getDisplayName());
                        tv.setTag(o.getId());
                        llCommunities.addView(v);
                    }
                }
            });

        }

    }


}
