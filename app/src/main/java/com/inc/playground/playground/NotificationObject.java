package com.inc.playground.playground;

import org.json.JSONObject;

/**
 * Created by lina on 7/13/2016.
 */
public class NotificationObject {
    EventsObject event;
    String description, type, title;
    JSONObject inputJson;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventsObject getEvent() {
        return event;
    }

    public void setEvent(EventsObject event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public JSONObject getInputJson() {
        return inputJson;
    }

    public void setInputJson(JSONObject inputJson) {
        this.inputJson = inputJson;
    }
}


