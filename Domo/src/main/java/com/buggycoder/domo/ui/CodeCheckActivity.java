package com.buggycoder.domo.ui;

import android.view.Window;
import android.widget.FrameLayout;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.request.OrganizationAPI;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.fragment.CodeCheckFragment;
import com.buggycoder.domo.ui.fragment.CodeCheckFragment_;
import com.buggycoder.domo.ui.fragment.WaitFragment;
import com.buggycoder.domo.ui.fragment.WaitFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
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

    @AfterViews
    protected void afterViews() {
        WaitFragment waitFragment = new WaitFragment_();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, waitFragment).commit();
        fetchOrganizations();
    }

    @Background
    protected void fetchOrganizations() {
        OrganizationAPI.getOrganizations();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        showOrgCodeCheck();
    }

    @UiThread
    protected void showOrgCodeCheck() {
        CodeCheckFragment codeCheckFragment = new CodeCheckFragment_();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, codeCheckFragment)
                .commit();
    }
}
