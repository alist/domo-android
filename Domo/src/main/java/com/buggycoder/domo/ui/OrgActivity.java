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

    SlidingMenu menu;

    @AfterViews
    protected void afterViews() {
        final ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        ab.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_title, null);
        ab.setCustomView(view);

        // configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadowright);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.frag_menu);
    }

    public void onBackPressed(){

        if (menu.isMenuShowing()) {
            menu.toggle();
        } else {
            super.onBackPressed();
        }
    }

}
