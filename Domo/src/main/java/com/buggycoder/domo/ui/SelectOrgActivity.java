package com.buggycoder.domo.ui;

import android.content.Intent;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;

import org.androidannotations.annotations.EActivity;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 22/10/13.
 */

@EActivity(R.layout.activity_selorg)
public class SelectOrgActivity extends BaseFragmentActivity {

    public static final String EXTRA_ORG_ID = "orgId";
    public static final String EXTRA_SUCCESS = "success";
    public static final String EXTRA_ERROR = "error";


    protected void onEventMainThread(OrganizationEvents.CheckOrgCodeResult o) {
        Logger.d("F:CheckOrgCodeResult");

        if (o.result.hasError) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_SUCCESS, false);
            intent.putExtra(EXTRA_ERROR, o.result.errors.get(0));
            setResult(0, intent);
            finish();
            return;
        }

        MyOrganization myOrg = o.result.getResponse();
        Logger.dump(myOrg);

        if (myOrg.getCode().length() > 0) {
            Crouton.makeText(this, "Membership verified.", Style.INFO).show();
            Intent intent = new Intent();
            intent.putExtra(EXTRA_SUCCESS, true);
            intent.putExtra(EXTRA_ORG_ID, o.result.getResponse().getId());
            setResult(1, intent);
            finish();
        }
    }
}
