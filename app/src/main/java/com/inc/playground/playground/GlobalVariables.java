package com.inc.playground.playground;

import android.app.Application;
import android.content.Context;

import com.inc.playground.playground.utils.GPSTracker;

import java.util.HashMap;

/**
 * Created by lina on 5/11/2016.
 */
public class GlobalVariables extends Application {

    public HashMap<String, Double> currentLocation;

    private GPSTracker gps;

    private HashMap<String, EventsObject> homeEvents;

    public HashMap<String, Double> GetCurrentLocation(){
        return this.currentLocation;
    }

    public GPSTracker GetGPS(){
        return this.gps;
    }

    public void SetCurrentLocation(HashMap<String, Double> currentLocation){
        this.currentLocation = currentLocation;
    }

    public void InitGPS(Context activityFrom){
        this.gps = new GPSTracker(activityFrom);
    }

    public HashMap<String, EventsObject> GetHomeEvents(){
        return this.homeEvents;
    }
    
    public void SetHomeEvents(HashMap<String, EventsObject> events){
        this.homeEvents = events;
    }

}
