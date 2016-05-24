package com.inc.playground.playground.utils;


import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class User {
	String userId , Status;
	Set <String> userEvents;

	public String GetUserId() { return this.userId; }

	public void SetUserId(String userId) { this.userId = userId; }
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void SetUserEvents(Set <String> userEvents) {
		this.userEvents = new HashSet<>();
		this.userEvents = userEvents; }

	public Set <String> GetUserEvents() { return this.userEvents; }
}
