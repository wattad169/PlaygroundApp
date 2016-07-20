package com.inc.playground.playground;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.User;
import com.inc.playground.playground.utils.UserImageEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by lina on 5/11/2016.
 */
public class GlobalVariables extends Application {

    public HashMap<String, Double> currentLocation;

    public HashMap<String, Bitmap> usersImagesMap;

    private GPSTracker gps;

    private ArrayList<EventsObject> homeEvents;

    private ArrayList<UserImageEntry> usersList;

    private Bitmap userPictureBitmap=null;

    private User currentUser;

    private ArrayList<NotificationObject> notifications = new ArrayList<>();

    private ArrayList<EventsObject> filterEvents;

    public HashMap<String, Double> GetCurrentLocation(){
        return this.currentLocation;
    }

    public GPSTracker GetGPS(){
        return this.gps;
    }

    public void SetCurrentLocation(HashMap<String, Double> currentLocation){ this.currentLocation = currentLocation; }

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

    public void SetCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public User GetCurrentUser() {return this.currentUser; }

    public void SetUsersImagesMap(HashMap<String,Bitmap> usersImagesMapIn){
        this.usersImagesMap = usersImagesMapIn;
    }
    public HashMap<String,Bitmap> GetUsersImagesMap(){
        return this.usersImagesMap;
    }

    public void SetUsersList(ArrayList<UserImageEntry> usersListIn){
        this.usersList=usersListIn;
    }
    public ArrayList<UserImageEntry> GetUsersList(){
        return this.usersList;
    }

    public void SetNotifications(ArrayList<NotificationObject> notifications){
        this.notifications = new ArrayList<>();
        this.notifications = notifications;
    }

    public ArrayList<NotificationObject> GetNotifications(){
        return this.notifications;
    }

    public ArrayList<EventsObject> getFilterEvents() {
        return filterEvents;
    }

    public void setFilterEvents(ArrayList<EventsObject> filterEvents) {
        this.filterEvents = new ArrayList<>();
        this.filterEvents = filterEvents;
    }


    public enum EventStatus {//eventObject optional status
        EXPIRED, OPEN ,LIVE
    }


}
