package com.buggycoder.domo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.buggycoder.domo.R;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_home)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class OrgActivity extends BaseFragmentActivity {


    @AfterViews
    protected void afterViews() {
    }


}
