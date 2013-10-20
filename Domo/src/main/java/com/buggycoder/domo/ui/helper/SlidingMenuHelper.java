package com.buggycoder.domo.ui.helper;

import com.buggycoder.domo.R;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by shirish on 20/10/13.
 */
public class SlidingMenuHelper {

    BaseFragmentActivity activity;

    SlidingMenu menu = null;
    boolean useSlidingMenu = false;


    public SlidingMenuHelper(BaseFragmentActivity activity) {
        this.activity = activity;
    }

    public boolean handleMenuShowing() {
        if (useSlidingMenu && menu != null && menu.isMenuShowing()) {
            menu.toggle();
            return true;
        }
        return false;
    }

    public void closeSlidingMenu(boolean animate) {
        if(useSlidingMenu && menu != null && menu.isMenuShowing()) {
            menu.showContent(animate);
        }
    }


    public void setSlidingMenu(int fragmentResId, int menuSide) {
        useSlidingMenu = true;

        // configure the SlidingMenu
        menu = new SlidingMenu(activity);
        menu.setMode(menuSide);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadowright);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(fragmentResId);
    }

    public void toggleSlidingMenu() {
        if(useSlidingMenu && menu != null) {
            menu.toggle();
        }
    }

}
