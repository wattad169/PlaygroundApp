package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;
import com.inc.playground.playground.utils.EventUserObject;
import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;
import com.inc.playground.playground.utils.UserImageEntry;
import com.inc.playground.playground.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Splash extends Activity {
    private static final String TAG = "Splash: ";
    public static final String MY_PREFS_NAME = "Login";
    public static GlobalVariables globalVariables;
    public User currentUser;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;

    JSONArray getAllUsersResponse;
    HashMap<String,Bitmap> userToImage = new HashMap<String,Bitmap>();
    ArrayList<UserImageEntry> usersList = new ArrayList<UserImageEntry>() ;
    private boolean isReceiverRegistered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set action bar color
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
        //Check if user is login
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        currentUser = new User();

        if (prefs.getString("fullname", null) != null){
            String userName = prefs.getString("fullname", null);
            currentUser.setName(userName);
        }

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
        new GetEventsAsyncTask(this).execute();
        new GetUsersImages(this).execute();

    }
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    public static class GetEventsAsyncTask extends AsyncTask<String, String, String> {
        private Context context;
        GetEventsAsyncTask(Context cntx){
            this.context = cntx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String allEventsResponseString;
            String userProfileResponseString;
            try {
                JSONObject cred = new JSONObject();
                String userToken = "StubToken";//TODO Replace with real token

                try {
                    cred.put(NetworkUtilities.TOKEN, userToken);
                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                }
                //"get all events"
                allEventsResponseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_all_events/");


            } catch (Exception ex) {
                Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                allEventsResponseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON = null;
            JSONObject responseJSON= null;
            try {
                responseJSON = new JSONObject(allEventsResponseString);
                eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);//does that need change? (UserobjectEvents?)
                ArrayList<EventsObject> eventObjectOnly = NetworkUtilities.eventListToArrayList(eventsFromServerJSON, globalVariables.GetCurrentLocation());
                globalVariables.SetHomeEvents(eventObjectOnly);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            Intent i = new Intent(this.context, MainActivity.class);
            this.context.startActivity(i);
            ((Activity)this.context).finish();
            Log.d("successful", "successful");
        }
    }

    public class GetUserEventsAsyncTask extends AsyncTask<String, String, String> {
        ArrayList<EventUserObject> userEventsObjects;
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
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_user_info/");

            } catch (Exception ex) {
                Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON;
            JSONObject responseJSON , JSONUserInfo;
            try {

                responseJSON = new JSONObject(responseString);
                JSONUserInfo = responseJSON.getJSONObject(Constants.RESPONSE_MESSAGE);
                String createdCount = JSONUserInfo.getString("createdCount");
                eventsFromServerJSON = JSONUserInfo.getJSONArray(Constants.EVENT_ENTRIES);//Todo:update what i get


                userEventsObjects =  NetworkUtilities.allUserEvents(JSONUserInfo, globalVariables.GetCurrentLocation());
                Set<String> userEvents = new HashSet<>();
                for(EventUserObject eUObject : userEventsObjects ){
                    String eventId = eUObject.GetId(); //currentObject.getString(Constants.EVENT_ID);
                    userEvents.add(eventId);//TODO: need to update the other types of events?
                }
                currentUser.setUserEventsObjects(userEventsObjects);
                currentUser.SetUserEvents(userEvents);
                currentUser.setCreatedNumOfEvents(createdCount);
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
    public class GetUsersImages extends AsyncTask<String, String, String> {
        public static final String TAG = "GetUsersImages";

        Bitmap imageBitmap;
        ArrayList<Bitmap> usersImages;
        Context thisContext;

        GetUsersImages(Context thisCon){
            thisContext = thisCon;

        }
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
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_all_users/");

            } catch (Exception ex) {
                Log.e(TAG, "getMembersUrls.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON = null;
            JSONObject responseJSON= null;
            try {
                responseJSON = new JSONObject(responseString);
                getAllUsersResponse = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            for(int i=0;i<getAllUsersResponse.length();i++)
            {
                try {

                    JSONObject currentObject = (JSONObject) getAllUsersResponse.get(i);
                    String fullname = currentObject.getString(Constants.FULLNAME);

                    Bitmap currentImage = new DownloadImageBitmapTask().execute(currentObject.getString(Constants.PHOTO_URL)).get();
                    String userId = currentObject.getString(Constants.ID);
                    UserImageEntry currentUser = new UserImageEntry(fullname,currentImage,userId) ;
                    usersList.add(currentUser);
                    userToImage.put(fullname, currentImage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            globalVariables.SetUsersList(usersList);
            globalVariables.SetUsersImagesMap(userToImage);
            Log.d(TAG, "getUsersImages.successful" + userToImage.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }
}

