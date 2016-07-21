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



    Set<String> userWaitingForApproveIds;



    Set<String> userDeclinedIds;
    //represents event status for the user
    ArrayList<EventsObject> events = new ArrayList<>() ;
    ArrayList<EventsObject> events_wait4approval = new ArrayList<>();
    ArrayList<EventsObject> events_decline = new ArrayList<>();

    Set<String> favouritesId;//new feature!


    public Set<String> getUserDeclinedIds() {
        return userDeclinedIds;
    }

    public void setUserDeclinedIds(Set<String> userDeclinedIds) {
        this.userDeclinedIds = userDeclinedIds;
    }

    public Set<String> getUserWaitingForApproveIds() {
        return userWaitingForApproveIds;
    }

    public void setUserWaitingForApproveIds(Set<String> userWaitingForApproveIds) {
        this.userWaitingForApproveIds = userWaitingForApproveIds;
    }
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
     * @param eventId
     * @return String of the type of eventId Or null if the user don't have reference for the eventid
     */
    public static String eventUserType(ArrayList<EventsObject> events,ArrayList<EventsObject> events_wait4approval ,
                               ArrayList<EventsObject> events_decline, String eventId  ){
        for(EventsObject e : events){
            if(e.GetId().equals(eventId)){
                return Constants.EVENTS_EDITED;
            }
        }
        for(EventsObject e : events_wait4approval){
            if(e.GetId().equals(eventId)){
                return Constants.EVENTS_WAIT4APPROVAL;            }
        }

        for(EventsObject e : events_decline){
            if(e.GetId().equals(eventId)){
                return Constants.EVENTS_DECLINE;
            }
        }
        return null;
    }

    /**
     *
     * @param events
     * @param eventId
     * @return true if the array<EventObject> contain the eventid
     */
    public static boolean eventsObjectContainEvent(ArrayList<EventsObject> events, String eventId  ){
        for(EventsObject e : events){
            if(e.GetId().equals(eventId)){
                return true;
            }
        }
        return false;
    }


    public static boolean userEventsContainEventId(ArrayList<EventsObject> events,ArrayList<EventsObject> events_wait4approval ,
                                                   ArrayList<EventsObject> events_decline, String eventId ){
        return eventsObjectContainEvent(events, eventId)||
                eventsObjectContainEvent(events_wait4approval, eventId)||
                eventsObjectContainEvent(events_decline,eventId);
    }

    /**
     *`
     * @param eventId
     * @return True if it removed
     */
    public boolean removeSpecificEventById(String eventId ){
        if( eventsObjectContainEvent(events,eventId) ){
            for(int i =0 ; i<events.size() ; i++ ){
                EventsObject e = events.get(i);
                if(e.equals(eventId)){
                    events.remove(i);
                    return true;
                }
            }
        }
        if( eventsObjectContainEvent(events_wait4approval,eventId) ){
            for(int i =0 ; i<events.size() ; i++ ){
                EventsObject e = events_wait4approval.get(i);
                if(e.equals(eventId)){
                    events.remove(i);
                    return true;
                }
            }
        }
        if( eventsObjectContainEvent(events_decline,eventId) ){
            for(int i =0 ; i<events.size() ; i++ ){
                EventsObject e = events_decline.get(i);
                if(e.equals(eventId)){
                    events.remove(i);
                    return true;
                }
            }
        }
        return false;
    }



    /**
     *
     * @param events

     * @return the Hashset<String> of all event ids
     */
    public static Set<String> extractAllEventIds(ArrayList<EventsObject> events){

        Set<String> eventIds = new HashSet<>();
        for(EventsObject e : events){
            eventIds.add( e.GetId());
        }

        return eventIds;
    }

}
