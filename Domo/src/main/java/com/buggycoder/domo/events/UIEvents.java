package com.buggycoder.domo.events;

import com.buggycoder.domo.api.response.MyOrganization;

/**
 * Created by shirish on 19/10/13.
 */
public class UIEvents {

    public static class SlidingMenuItemSelected {
        public MyOrganization selMyOrganization;

        public SlidingMenuItemSelected() {

        }
    }
}
