package com.buggycoder.domo.ui;

import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.buggycoder.domo.R;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

/**
 * Created by shirish on 7/9/13.
 */

@EActivity(R.layout.activity_splash)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class SplashActivity extends BaseFragmentActivity {

    @ViewById
    Button btnGetAdvice;

    @AfterViews
    protected void afterViews() {
        btnGetAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(TestActivity_.class, true);
            }
        });
    }
}
