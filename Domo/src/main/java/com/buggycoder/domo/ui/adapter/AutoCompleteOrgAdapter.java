package com.buggycoder.domo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.ui.adapter.view.OrgItemView;
import com.buggycoder.domo.ui.adapter.view.OrgItemView_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirish on 18/10/13.
 */

@EBean
public class AutoCompleteOrgAdapter extends ArrayAdapter<Organization> implements Filterable {

    private LayoutInflater mInflater;

    @RootContext
    Context context;

    @Bean
    Config config;


    public AutoCompleteOrgAdapter(final Context context) {
        super(context, -1);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        OrgItemView orgItemView;
        if (convertView == null) {
            orgItemView = OrgItemView_.build(context);
        } else {
            orgItemView = (OrgItemView) convertView;
        }

        orgItemView.bind(getItem(position));
        return orgItemView;
    }


    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                List<Organization> orgList = null;
                if (constraint != null) {
                    try {
                        orgList = OrganizationAPI.filterOrganizations(config, constraint.toString());
                    } catch (IOException e) {
                    }
                }
                if (orgList == null) {
                    orgList = new ArrayList<Organization>();
                }

                final FilterResults filterResults = new FilterResults();
                filterResults.values = orgList;
                filterResults.count = orgList.size();

                return filterResults;
            }

            @Override
            protected void publishResults(final CharSequence constraint, final FilterResults results) {
                clear();
                for (Organization org : (List<Organization>) results.values) {
                    add(org);
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(final Object resultValue) {
                return resultValue == null ? "" : ((Organization) resultValue).getDisplayName();
            }
        };
        return myFilter;
    }


}