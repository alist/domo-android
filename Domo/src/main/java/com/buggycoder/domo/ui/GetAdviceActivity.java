package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Window;
import com.buggycoder.domo.R;
import com.buggycoder.domo.api.SupporteeAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.events.SupporteeEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 7/9/13.
 */

@EActivity(R.layout.activity_getadvice)
public class GetAdviceActivity extends BaseFragmentActivity {

    @ViewById
    TextView tvOrgDisplayName;

    @ViewById
    Button askAdviceSubmit;

    @ViewById
    EditText etQuery;

    @Bean
    Config config;

    @Extra
    String orgURL, orgCode, orgDisplayName;


    @AfterViews
    protected void afterViews() {
        getSlidingMenuHelper().setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        tvOrgDisplayName.setText(orgDisplayName);
        askAdviceSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAdviceRequest();
            }
        });
    }

    @Background
    protected void newAdviceRequest() {
        try {
            Logger.d("newAdviceRequest");
            SupporteeAPI.newAdviceRequest(config, orgURL, orgCode, etQuery.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void onEventMainThread(final SupporteeEvents.GetAdviceResult o) {
        Logger.d("onEventMainThread");
        etQuery.setText("");

        if (o.result.hasError) {
            Crouton.makeText(GetAdviceActivity.this, o.result.errors.get(0), Style.ALERT).show();
            return;
        }

        Crouton.makeText(GetAdviceActivity.this, "Posted successfully", Style.INFO).show();
        MyQuestionsActivity_.intent(GetAdviceActivity.this).start();
        finish();
    }

}
