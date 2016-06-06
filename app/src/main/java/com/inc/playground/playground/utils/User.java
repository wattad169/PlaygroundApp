package com.inc.playground.playground.utils;


import com.inc.playground.playground.EventsObject;

import java.util.ArrayList;
import java.util.Set;

public class User {


	String photoUrl;
	String name;
	String email; //Unusable
	int createdNumOfEvents;
	int joinNumOfEvents;
	String userId , Status;
	Set <String> userEvents;

	ArrayList<EventsObject> userEventsObjects;

	public String getEmail() {
		return email;
	}
	public ArrayList<EventsObject> getUserEventsObjects() {
		return userEventsObjects;
	}

	public void setUserEventsObjects(ArrayList<EventsObject> userEventsObjects) {
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

	public int getCreatedNumOfEvents() {
		return createdNumOfEvents;
	}

	public void setCreatedNumOfEvents(int createdNumOfEvents) {
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
		this.userEvents = userEvents;

	}

	public Set <String> GetUserEvents() { return this.userEvents; }
}
