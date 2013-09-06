package com.buggycoder.domo.ui;

import android.view.View;
import android.widget.Button;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.request.OrganizationAPI;
import com.buggycoder.domo.api.request.SupporteeAPI;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity {

    @ViewById
    Button btnGetOrgs;

    @ViewById
    Button btnGetOrg;

    @ViewById
    Button btnCheckOrgCode;

    @ViewById
    Button btnNewAdviceRequest;

    @ViewById
    Button btnFetchAdviceRequest;


    @AfterViews
    protected void afterViews() {
        btnGetOrgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizationAPI.getOrganizations();
            }
        });

        btnGetOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizationAPI.getOrganization("/mit");
            }
        });

        btnCheckOrgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OrganizationAPI.checkCode("mit", "mit");
                    OrganizationAPI.checkCode("mit", "mit9");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnNewAdviceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.newAdviceRequest("mit", "mit9", "Hello, I need help with this.");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFetchAdviceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.fetchAdviceRequest("mit", "mit9", "5229eef27c7e323b7500000b", "8b2963cba6aa4bd7bf5cb5f2f3d2334f");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
