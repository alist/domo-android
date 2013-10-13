package com.buggycoder.domo.ui;

import android.view.View;
import android.widget.Button;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.SupporteeAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;

@EActivity(R.layout.activity_test)
public class TestActivity extends BaseFragmentActivity {

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

    @ViewById
    Button btnAdviceRequestHelpful;

    @ViewById
    Button btnAdviceRequestThankYou;

    @Bean
    Config config;

    @AfterViews
    protected void afterViews() {

        btnGetOrgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizationAPI.getOrganizations(config);
            }
        });

        btnGetOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganizationAPI.getOrganization(config, "mit");
            }
        });

        btnCheckOrgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OrganizationAPI.checkCode(config, "mit", "mit");
                    OrganizationAPI.checkCode(config, "mit", "mit9");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnNewAdviceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.newAdviceRequest(config, "mit", "mit9", "Hello, I need help with this.");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFetchAdviceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.fetchAdviceRequest(config, "mit", "mit9", "5229eef27c7e323b7500000b", "8b2963cba6aa4bd7bf5cb5f2f3d2334f");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnAdviceRequestHelpful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.markAdviceAttr(config, "mit", "mit9", "5229eef27c7e323b7500000b", "5229f4547c7e323b7500000f", "8b2963cba6aa4bd7bf5cb5f2f3d2334f", SupporteeAPI.Action.HELPFUL, 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnAdviceRequestThankYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SupporteeAPI.markAdviceAttr(config, "mit", "mit9", "5229eef27c7e323b7500000b", "5229f4547c7e323b7500000f", "8b2963cba6aa4bd7bf5cb5f2f3d2334f", SupporteeAPI.Action.THANKYOU, 1);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
