package com.inc.playground.playground;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.User;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by mostafawattad on 06/05/2016.
 */
public class EventsObject implements Serializable{
    String id, name, formattedLocation, type, size, date, startTime, endTime, description, status, distance;
    // TODO ADD EVENT MEMBERS (A LIST CONTAINING USERS)
    User creator;

    HashMap<String, String> location= new HashMap<String, String>();
    // TODO add GetDistance method
    public String GetId() { return id; }

    public void SetId(String id) { this.id = id; }

    public String GetName() { return name; }

    public void SetName(String name) { this.name = name; }

    public User GetCreator() { return creator; }

    public void SetCreator(User creator) { this.creator = creator; }

    public String GetFormattedLocation() { return formattedLocation; }

    public void SetFormattedLocation(String location) { this.formattedLocation = location; }

    public HashMap<String, String> GetLocation() { return this.location; }

    public void SetPosition(String lat, String lon) {
        this.location.clear();
        this.location.put(Constants.LOCATION_LAT, lat);
        this.location.put(Constants.LOCATION_LON, lon);
    }

    public String GetType() { return type; }

    public void SetType(String type) { this.type = type; }

    public String GetSize() { return size; }

    public void SetSize(String size){ this.size = size;	}

    public String GetDate() { return date; }

    public void SetDate(String date){ this.date = date;	}

    public String GetStartTime() { return startTime; }

    public void SetStartTime(String startTime) { this.startTime = startTime; }

    public String GetEndTime() { return endTime; }

    public void SetEndTime(String endTime) { this.endTime = endTime; }

    public String GetDescription() { return description; }

    public void SetDescription(String description) { this.description = description; }

    public String GetStatus() { return status; }

    public void SetStatus(String status) { this.status = status; }

    public String GetDistance() { return distance; }

    public void SetDistance(String distance) { this.distance= distance; }





}
