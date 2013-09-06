package com.buggycoder.domo.events;

import com.buggycoder.domo.lib.PubSub;

/**
 * Created by shirish on 5/9/13.
 */
public abstract class Event {

    public void publish() {
        PubSub.publish(this);
    }
}
