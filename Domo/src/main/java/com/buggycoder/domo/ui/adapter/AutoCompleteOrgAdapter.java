package com.buggycoder.domo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirish on 18/10/13.
 */


public class AutoCompleteOrgAdapter extends ArrayAdapter<Organization> implements Filterable {

    private LayoutInflater mInflater;
    private Config config;

    public AutoCompleteOrgAdapter(final Context context, final Config c) {
        super(context, -1);
        config = c;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final TextView tv;
        if (convertView != null) {
            tv = (TextView) convertView;
        } else {
            tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        tv.setText(getItem(position).getDisplayName());
        return tv;
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