package com.inc.playground.playground.utils;

import com.inc.playground.playground.EventsObject;

/**
 * Created by idanaroz on 07-Jul-16.
 */
public class EventUserObject extends EventsObject {

/*EventObject which relevant for the specific user  */

    String eventUserStatus; // can be "events_edited" or "events_wait4approval" ...

    public String getEventUserStatus() {// Todo:check that we use it
        return eventUserStatus;
    }

    public void setEventUserStatus(String eventUserStatus) {// Todo:check that we use it
        this.eventUserStatus = eventUserStatus;
    }



}
