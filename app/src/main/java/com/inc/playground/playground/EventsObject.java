package com.inc.playground.playground;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.EventsObjectInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mostafawattad on 06/05/2016.
 */
public class EventsObject implements Serializable ,EventsObjectInterface {
    String id, name, formattedLocation, type, size, date, startTime, endTime, description, status, distance;

    String isPublic;
    ArrayList<String> members;

    // TODO ADD EVENT MEMBERS (A LIST CONTAINING USERS)
    String creatorId;
    HashMap<String, String> location= new HashMap<String, String>();
    // TODO add GetDistance method
    public String GetId() { return id; }


    public void SetId(String id) { this.id = id; }
    public String GetName() { return name; }

    public void SetName(String name) { this.name = name; }

    public String GetCreatorId() { return creatorId; }

    public void SetCreatorId(String creatorId) { this.creatorId = creatorId; }

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

    public ArrayList<String> GetMembers(){ return members; }

    public void SetMembers(ArrayList<String> members){ this.members = members; }

    public String getIsPublic() { // Todo:check that we use it
        return isPublic;
    }

    public void setIsPublic(String isPublic) {// Todo:check that we use it
        this.isPublic = isPublic;
    }






}
