package com.inc.playground.playgroundApp.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by idanaroz on 07-Jul-16.
 */
public interface EventsObjectInterface {

    public String GetId();


    public void SetId(String id);
    public String GetName();

    public void SetName(String name);

    public String GetCreatorId();

    public void SetCreatorId(String creatorId);

    public String GetFormattedLocation();

    public void SetFormattedLocation(String location);

    public HashMap<String, String> GetLocation();

    public void SetPosition(String lat, String lon);

    public String GetType();

    public void SetType(String type);

    public String GetSize();

    public void SetSize(String size);

    public String GetDate();

    public void SetDate(String date);

    public String GetStartTime();

    public void SetStartTime(String startTime);

    public String GetEndTime() ;

    public void SetEndTime(String endTime);

    public String GetDescription();

    public void SetDescription(String description) ;

    public String GetStatus();

    public void SetStatus(String status);

    public String GetDistance();

    public void SetDistance(String distance);

    public ArrayList<String> GetMembers();

    public void SetMembers(ArrayList<String> members);

    public String getIsPublic();

    public void setIsPublic(String isPublic);





}
