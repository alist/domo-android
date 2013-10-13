package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.fragment.CodeCheckFragment;
import com.buggycoder.domo.ui.fragment.CodeCheckFragment_;
import com.buggycoder.domo.ui.fragment.ErrorFragment;
import com.buggycoder.domo.ui.fragment.ErrorFragment_;
import com.buggycoder.domo.ui.fragment.WaitFragment;
import com.buggycoder.domo.ui.fragment.WaitFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

/**
 * Created by shirish on 7/9/13.
 */

@EActivity(R.layout.activity_codecheck)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class CodeCheckActivity extends BaseFragmentActivity {

    @ViewById
    FrameLayout fragmentContainer;

    @Bean
    Config config;

    @AfterViews
    protected void afterViews() {
        WaitFragment waitFragment = new WaitFragment_();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, waitFragment).commit();
        fetchOrganizations();
    }

    @Background
    protected void fetchOrganizations() {
        OrganizationAPI.getOrganizations(config);
    }

    protected void onEventMainThread(OrganizationEvents.GetOrganizationsResult o) {
        if (o.result.hasError) {
            Bundle args = new Bundle();
            args.putString(ErrorFragment.KEY_ERROR, o.result.errors.get(0));

            ErrorFragment errorFragment = new ErrorFragment_();
            errorFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, errorFragment)
                    .commit();
            return;
        }

        showOrgCodeCheck();
    }


    protected void showOrgCodeCheck() {
        CodeCheckFragment codeCheckFragment = new CodeCheckFragment_();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, codeCheckFragment)
                .commit();
    }
}
