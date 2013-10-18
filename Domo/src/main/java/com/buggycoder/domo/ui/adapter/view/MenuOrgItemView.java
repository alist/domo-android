package com.buggycoder.domo.ui.adapter.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 19/10/13.
 */
@EViewGroup(R.layout.menu_row)
public class MenuOrgItemView extends LinearLayout {

    @ViewById
    ImageView menuOrgIcon;
    @ViewById
    TextView menuOrgDisplayName;
    Context context;

    public MenuOrgItemView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(MyOrganization org) {
        menuOrgDisplayName.setText(org.getDisplayName());
        String orgBannerURL = org.getBannerURL();
        if (!TextUtils.isEmpty(orgBannerURL)) {
            Picasso.with(context)
                .load(orgBannerURL)
                .resize(100, 100)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher)
                .into(menuOrgIcon);
        }
    }
}