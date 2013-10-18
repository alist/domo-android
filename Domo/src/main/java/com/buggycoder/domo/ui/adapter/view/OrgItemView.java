package com.buggycoder.domo.ui.adapter.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.Organization;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 18/10/13.
 */
@EViewGroup(R.layout.row_ac_org)
public class OrgItemView extends LinearLayout {

    @ViewById
    TextView orgDisplayName;

    public OrgItemView(Context context) {
        super(context);
    }

    public void bind(Organization org) {
        orgDisplayName.setText(org.getDisplayName());
    }
}
