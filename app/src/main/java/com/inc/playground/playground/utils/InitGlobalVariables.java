package com.inc.playground.playground.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.inc.playground.playground.GlobalVariables;
import com.inc.playground.playground.MainActivity;
import com.inc.playground.playground.Splash;




/**
 * Created by mostafawattad on 19/07/2016.
 */
public class InitGlobalVariables {
    public static GlobalVariables globalVariables;
    public static User currentUser;
    public static Context requestClass;
    public static final String MY_PREFS_NAME = "Login";
    public static Intent nextIntent;
    public InitGlobalVariables(Context request,Intent nextIntentIn){
        this.requestClass = request;
        this.nextIntent = nextIntentIn;

    }
    public static void init(){
        //Check if user is login
        SharedPreferences prefs = InitGlobalVariables.requestClass.getSharedPreferences(MY_PREFS_NAME, InitGlobalVariables.requestClass.MODE_PRIVATE);
        currentUser = new User();

        globalVariables = ((GlobalVariables) InitGlobalVariables.requestClass.getApplicationContext());
        globalVariables.SetCurrentUser(currentUser);
        if (prefs.getString("fullname", null) != null){
            String userName = prefs.getString("fullname", null);
            currentUser.setName(userName);
        }

        if (prefs.getString("userid", null) != null)
        { // Get users events
            String userLoginId = prefs.getString("userid", null);
            currentUser.SetUserId(userLoginId);
            // Create server call
            Splash.GetUserEventsAsyncTask taskUserEvents = new Splash.GetUserEventsAsyncTask(InitGlobalVariables.requestClass);
            taskUserEvents.execute();
            //call to GetEventsAsyncTask and GetUsersImages from: onPostExcute (to handle internet connecetions)
        }
        globalVariables.InitGPS(InitGlobalVariables.requestClass);
        globalVariables.SetCurrentLocation(Utils.getMyLocation(globalVariables.GetGPS()));

        new Splash.GetEventsAsyncTask(InitGlobalVariables.requestClass,InitGlobalVariables.nextIntent ).execute();
        new Splash.GetUsersImages(InitGlobalVariables.requestClass).execute();

    }

}
