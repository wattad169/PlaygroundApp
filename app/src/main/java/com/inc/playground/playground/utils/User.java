package com.inc.playground.playground.utils;


import com.inc.playground.playground.EventsObject;

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
	String userId , Status;
	Set <String> userEvents;

	ArrayList<EventUserObject> userEventsObjects;

	public String getEmail() {
		return email;
	}
	public ArrayList<EventUserObject> getUserEventsObjects() {
		return userEventsObjects;
	}

	public void setUserEventsObjects(ArrayList<EventUserObject> userEventsObjects) {
		this.userEventsObjects = userEventsObjects;
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
	//mostafa i think you should use in create
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



	public String GetUserId() { return this.userId; }

	public void SetUserId(String userId) { this.userId = userId; }

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void SetUserEvents(Set <String> userEvents) {
		//can someone add detail here? what the string include? the name of the event?
		this.userEvents = new HashSet<>();
		this.userEvents = userEvents;

	}

	public Set <String> GetUserEvents() { return this.userEvents; }
}
