package com.buggycoder.domo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.ui.adapter.view.MenuOrgItemView;
import com.buggycoder.domo.ui.adapter.view.MenuOrgItemView_;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by shirish on 19/10/13.
 */
@EBean
public class MenuAdapter extends ArrayAdapter<MyOrganization> {

    @RootContext
    Context context;

    Dao<MyOrganization, String> myOrgDao;


    public MenuAdapter(Context context) {
        super(context, -1);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        MenuOrgItemView orgItemView;
        if (convertView == null) {
            orgItemView = MenuOrgItemView_.build(context);
        } else {
            orgItemView = (MenuOrgItemView) convertView;
        }

        orgItemView.bind(getItem(position));
        return orgItemView;
    }

}