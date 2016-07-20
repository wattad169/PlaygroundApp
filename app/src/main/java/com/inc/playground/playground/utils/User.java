package com.inc.playground.playground.utils;


import com.inc.playground.playground.EventsObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

public class User {


    String photoUrl;
    String name;
    String email; //Unusable
    String createdNumOfEvents;
    int joinNumOfEvents;
    String userId, Status;

    Set<String> userEvents;//do we really need it?

    //represents event status for the user
    ArrayList<EventsObject> events;
    ArrayList<EventsObject> events_wait4approval;
    ArrayList<EventsObject> events_decline;

    Set<String> favouritesId;//new feature!

    public ArrayList<EventsObject> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<EventsObject> events) {
        this.events = events;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(Set<String> userEvents) {
        this.userEvents = userEvents;
    }

    public ArrayList<EventsObject> getEvents_wait4approval() {
        return events_wait4approval;
    }

    public void setEvents_wait4approval(ArrayList<EventsObject> events_wait4approval) {
        this.events_wait4approval = events_wait4approval;
    }

    public ArrayList<EventsObject> getEvents_decline() {
        return events_decline;
    }

    public void setEvents_decline(ArrayList<EventsObject> events_decline) {
        this.events_decline = events_decline;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }


    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCreatedNumOfEvents() {
        return createdNumOfEvents;
    }

    public void setCreatedNumOfEvents(String createdNumOfEvents) {
        this.createdNumOfEvents = createdNumOfEvents;
    }

    //ArrayList<EventsObject> userEventsObjects;

    public int getJoinNumOfEvents() {
        return joinNumOfEvents;
    }

    public void setJoinNumOfEvents(int joinNumOfEvents) {
        this.joinNumOfEvents = joinNumOfEvents;
    }

    public String GetUserId() {
        return this.userId;
    }


    public void SetUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void SetUserEvents(Set<String> userEvents) {
        //can someone add detail here? what the string include? the name of the event?
        this.userEvents = userEvents;

    }

    public Set<String> GetUserEvents() {
        return this.userEvents;
    }

    public Set<String> getFavouritesId() {
        return favouritesId;
    }

    public void setFavouritesId(Set<String> favouritesId) {
        this.favouritesId = favouritesId;
    }

    public void set3eventTypes(ArrayList<EventsObject> events,ArrayList<EventsObject> events_wait4approval ,
                                ArrayList<EventsObject> events_decline  ){
        this.events = events;
        this.events_wait4approval = events_wait4approval;
        this.events_decline = events_decline;
    }

    /**
     *
     * @param events
     * @param events_wait4approval
     * @param events_decline
     * @return the Hashset<String> of all event ids
     */
    public static Set<String> extractAllEventIds(ArrayList<EventsObject> events,ArrayList<EventsObject> events_wait4approval ,
                                          ArrayList<EventsObject> events_decline ){

        Set<String> eventIds = new HashSet<>();
        for(EventsObject e : events){
            eventIds.add( e.GetId());
        }
        for(EventsObject e : events_wait4approval){
            eventIds.add(e.GetId());
        }
        for(EventsObject e : events_decline){
            eventIds.add(e.GetId());
        }
        return eventIds;
    }

}
