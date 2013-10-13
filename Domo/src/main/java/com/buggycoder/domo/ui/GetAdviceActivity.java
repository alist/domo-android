package com.buggycoder.domo.ui;

import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.buggycoder.domo.R;
import com.buggycoder.domo.api.SupporteeAPI;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.events.SupporteeEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.io.UnsupportedEncodingException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 7/9/13.
 */

@EActivity(R.layout.activity_getadvice)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class GetAdviceActivity extends BaseFragmentActivity {

    public static final String KEY_ORGURL = "orgURL";
    public static final String KEY_ORGCODE = "orgCode";
    public static final String KEY_ORGDISPLAYNAME = "orgDisplayName";

    @ViewById
    TextView tvOrgDisplayName;

    @ViewById
    EditText etQuery;

    @Bean
    Config config;

    String orgURL, orgCode;

    @AfterViews
    protected void afterViews() {
        setSupportProgressBarIndeterminateVisibility(false);
        setProgressBarIndeterminate(false);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            orgURL = args.getString(KEY_ORGURL);
            orgCode = args.getString(KEY_ORGCODE);
            tvOrgDisplayName.setText(args.getString(KEY_ORGDISPLAYNAME));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.getadvice, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Logger.d("menu:" + item.getItemId() + " | " + item.getTitle());

        switch (item.getItemId()) {
            case R.id.mnuGetAdvice:
                setSupportProgressBarIndeterminateVisibility(true);
                setProgressBarIndeterminate(true);
                newAdviceRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    protected void onEventMainThread(SupporteeEvents.GetAdviceResult o) {
        setSupportProgressBarIndeterminateVisibility(false);
        setProgressBarIndeterminate(false);
        etQuery.setText("");

        if (o.result.hasError) {
            Crouton.makeText(this, o.result.errors.get(0), Style.ALERT).show();
            return;
        }

        Crouton.makeText(this, "Posted successfully", Style.INFO).show();
    }
}
