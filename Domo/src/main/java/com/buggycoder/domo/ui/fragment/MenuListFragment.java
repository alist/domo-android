package com.buggycoder.domo.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.events.UIEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.ui.OrgActivity;
import com.buggycoder.domo.ui.OrgActivity_;
import com.buggycoder.domo.ui.adapter.MenuAdapter;
import com.buggycoder.domo.ui.base.BaseListFragment;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.menu_list)
public class MenuListFragment extends BaseListFragment {

    @Bean
    MenuAdapter menuAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setEnablePubSub(true);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(menuAdapter);
        loadMyOrganizations();
    }

    protected void onEventMainThread(OrganizationEvents.MyOrganizationsUpdate o) {
        Logger.d("MyOrganizationsUpdate");
        loadMyOrganizations();
    }


    @Background
    protected void loadMyOrganizations() {
        try {
            Dao<MyOrganization, String> myOrgDao = DatabaseHelper.getDaoManager().getDao(MyOrganization.class);
            displayMyOrganizations(myOrgDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @UiThread
    protected void displayMyOrganizations(List<MyOrganization> myOrganizationList) {

        if(myOrganizationList == null) {
            myOrganizationList = new ArrayList<MyOrganization>();
        }

        menuAdapter.clear();

        for(MyOrganization o : myOrganizationList) {
            menuAdapter.add(o);
        }

        menuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MyOrganization selMyOrg = (MyOrganization) menuAdapter.getItem(position);
        if(selMyOrg != null) {

            UIEvents.SlidingMenuItemSelected selMenuItem = new UIEvents.SlidingMenuItemSelected();
            selMenuItem.selMyOrganization = selMyOrg;
            PubSub.publish(selMenuItem);

            PubSub.unsubscribe(this);
            OrgActivity_.intent(getSherlockActivity()).orgId(selMyOrg.getId()).start();
        }
    }

}