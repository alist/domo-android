package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment;
import com.buggycoder.domo.ui.fragment.SelectOrgFragment_;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_home)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class HomeActivity extends BaseFragmentActivity {

    private static final String TAG_FRAG_SELORG = "selorg";

    @ViewById
    Button btnAddCommunity;

    SlidingMenu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    protected void afterViews() {
        Logger.d("afterViews");
        setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        btnAddCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelOrgFragment().show(getSupportFragmentManager(), TAG_FRAG_SELORG);
            }
        });

    }

    protected SelectOrgFragment getSelOrgFragment() {
        SelectOrgFragment selOrgDialog = (SelectOrgFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAG_SELORG);

        if (selOrgDialog == null) {
            selOrgDialog = new SelectOrgFragment_();
            selOrgDialog.setRetainInstance(true);
        } else {
            Logger.d("not null");
        }

        return selOrgDialog;
    }

    protected void onEventMainThread(OrganizationEvents.CheckOrgCodeResult o) {
        Logger.d("F:CheckOrgCodeResult");

        SelectOrgFragment selOrgDialog = getSelOrgFragment();
        if (selOrgDialog != null && selOrgDialog.isVisible()) {
            selOrgDialog.dismiss();
        }

        if (o.result.hasError) {
            Crouton.makeText(this, o.result.errors.get(0), Style.ALERT).show();
            return;
        }

        MyOrganization myOrg = o.result.getResponse();
        Logger.dump(myOrg);

        if (myOrg.getCode().length() > 0) {
            Crouton.makeText(this, "Membership verified.", Style.INFO).show();

//            Bundle bundle = new Bundle();
//            bundle.putString(GetAdviceActivity.KEY_ORGURL, myOrg.getOrgURL());
//            bundle.putString(GetAdviceActivity.KEY_ORGCODE, myOrg.getCode());
//            bundle.putString(GetAdviceActivity.KEY_ORGDISPLAYNAME, myOrg.getDisplayName());
//
//            openActivity(GetAdviceActivity_.class, bundle, true);
        }
    }


}
