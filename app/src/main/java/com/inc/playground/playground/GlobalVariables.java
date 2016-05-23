package com.inc.playground.playground;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.inc.playground.playground.utils.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lina on 5/11/2016.
 */
public class GlobalVariables extends Application {

    public HashMap<String, Double> currentLocation;

    private GPSTracker gps;

    private ArrayList<EventsObject> homeEvents;

    private Bitmap userPictureBitmap=null;

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

    public ArrayList<EventsObject> GetHomeEvents(){
        return this.homeEvents;
    }

    public void SetHomeEvents(ArrayList<EventsObject> events){
        this.homeEvents = new ArrayList<>();
        this.homeEvents = events;
    }

    public void SetUserPictureBitMap(Bitmap userBitMap){
        this.userPictureBitmap = userBitMap;
    }
    public Bitmap GetUserPictureBitMap(){
        return this.userPictureBitmap;
    }

}
