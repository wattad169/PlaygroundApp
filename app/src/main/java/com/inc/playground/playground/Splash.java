package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;
import com.inc.playground.playground.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToArrayList;


public class Splash extends Activity {
    private static final String TAG = "Splash: ";
    public static final String MY_PREFS_NAME = "Login";
    public static GlobalVariables globalVariables;
    public User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set action bar color
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
        //Check if user is login
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        currentUser = new User();
        if (prefs.getString("userid", null) != null)
        { // Get users events
            String userLoginId = prefs.getString("userid", null);
            currentUser.SetUserId(userLoginId);
            // Create server call
            new GetUserEventsAsyncTask().execute();
        }
        // Get events from server
        globalVariables = ((GlobalVariables) this.getApplication());
        setContentView(R.layout.activity_splash);
        globalVariables.InitGPS(Splash.this);
        globalVariables.SetCurrentLocation(Utils.getMyLocation(globalVariables.GetGPS()));
        // Create server call
        new GetEventsAsyncTask().execute();
        Thread th = new Thread() {
            @Override
            public void run() {
                try {

                    //WebView wv = (WebView) findViewById(R.id.webview);
                    //wv.loadUrl("file:///android_asset/splashimage.gif");
                    // call eventListToHashMap from NetwrokUtiltites and putExtra hasmash to MainActivity
                    sleep(2000);
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {

                }

            }
        };
        th.start();
    }
    public class GetEventsAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseString;
            try {
                JSONObject cred = new JSONObject();
                String userToken = "StubToken";//TODO Replace with real token
                try {
                    cred.put(NetworkUtilities.TOKEN, userToken);
                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                }
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_all_events/");

            } catch (Exception ex) {
                Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON = null;
            JSONObject responseJSON= null;
            try {
                responseJSON = new JSONObject(responseString);
                eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);
                globalVariables.SetHomeEvents(eventListToArrayList(eventsFromServerJSON, globalVariables.GetCurrentLocation()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            Log.d("successful", "successful");
        }
    }

    public class GetUserEventsAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseString;
            try {
                JSONObject cred = new JSONObject();
                try {
                    cred.put(NetworkUtilities.TOKEN,"StubToken");
                    cred.put(NetworkUtilities.USER_ID,currentUser.GetUserId());
                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                }
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_events_by_user/");

            } catch (Exception ex) {
                Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON = null;
            JSONObject responseJSON= null;
            try {
                responseJSON = new JSONObject(responseString);
                eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);
                Set<String> userEvents = new HashSet<>();
                for(int i=0 ; i<eventsFromServerJSON.length();i++){
                    JSONObject currentObject = (JSONObject) eventsFromServerJSON.get(i);
                    String eventId = currentObject.getString(Constants.EVENT_ID);
                    userEvents.add(eventId);
                }
                currentUser.SetUserEvents(userEvents);
                globalVariables.SetCurrentUser(currentUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            Log.d("successful", "successful");
        }
    }
}

