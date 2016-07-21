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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import com.inc.playground.playground.utils.InitGlobalVariables;
public class Splash extends Activity {
    private static final String TAG = "Splash: ";
    public static final String MY_PREFS_NAME = "Login";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    public static HashMap<String,Bitmap> userToImage = new HashMap<String,Bitmap>();
    public static ArrayList<UserImageEntry> usersList = new ArrayList<UserImageEntry>() ;
    private boolean isReceiverRegistered;
    public static JSONArray getAllUsersResponse;
    public static InitGlobalVariables initInSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Set action bar color
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
        //Check if user is login
        Intent i = new Intent(this, MainActivity.class);
        initInSplash = new InitGlobalVariables(this,i);
        initInSplash.init();



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
        private Intent nextActivity;
        public GetEventsAsyncTask(Context cntx,Intent nextActivityIn){
            this.context = cntx;
            this.nextActivity = nextActivityIn;
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
                ArrayList<EventsObject> eventObjectOnly = NetworkUtilities.eventListToArrayList(eventsFromServerJSON, InitGlobalVariables.globalVariables.GetCurrentLocation());
                InitGlobalVariables.globalVariables.SetHomeEvents(eventObjectOnly);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException nullPoitExc) {/* if responseString was null*/
                Log.e(TAG, "responseString was null from 'get_all_events' call");
            }
            return null;
        }

        @Override //GetEventsAsyncTask class
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            if(NetworkUtilities.onlineException==false && NetworkUtilities.serverException==false && this.nextActivity!=null) { //connection works properly

                this.nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(this.nextActivity );
                ((Activity) this.context).finish();
                Log.d("successful", "successful");
            }
            else{
                InternetErrorGenericToast(this.context, NetworkUtilities.onlineException, NetworkUtilities.serverException);

            }
        }
    }

    public static class GetUserEventsAsyncTask extends AsyncTask<String, Integer, String> {
        private Context context;
        public GetUserEventsAsyncTask(Context cntx){
            this.context = cntx;
        }

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
                    cred.put(NetworkUtilities.USER_ID,InitGlobalVariables.currentUser.GetUserId());
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
                String created_count = JSONUserInfo.getString("created_count");
                eventsFromServerJSON = JSONUserInfo.getJSONArray(Constants.EVENT_ENTRIES);//Todo:update what i get


                userEventsObjects =  NetworkUtilities.allUserEvents(JSONUserInfo, InitGlobalVariables.globalVariables.GetCurrentLocation());
                Set<String> userEvents = new HashSet<>();
                for(EventUserObject eUObject : userEventsObjects ){
                    String eventId = eUObject.GetId(); //currentObject.getString(Constants.EVENT_ID);
                    userEvents.add(eventId);//TODO: need to update the other types of events?
                }
                InitGlobalVariables.currentUser.setUserEventsObjects(userEventsObjects);
                InitGlobalVariables.currentUser.SetUserEvents(userEvents);
                InitGlobalVariables.currentUser.setCreatedNumOfEvents(created_count);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch(NullPointerException nullPoitExc){/* if responseString was null*/
                Log.e(TAG, "responseString was null from 'get_user_info' call");
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "exception that we don't know");
            }
            return null;
        }

        @Override //GetUserEventsAsyncTask
        protected void onPostExecute(String lenghtOfFile) {
            //internt work properly
            if(NetworkUtilities.onlineException==false && NetworkUtilities.serverException==false) {
                super.onPostExecute(lenghtOfFile);
                Log.d("successful", "successful");
            }
            else {
                InternetErrorGenericToast(this.context, NetworkUtilities.onlineException, NetworkUtilities.serverException);
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    public static class GetUsersImages extends AsyncTask<String, Integer, String> {
        public static final String TAG = "GetUsersImages";

        Bitmap imageBitmap;
        ArrayList<Bitmap> usersImages;
        Context thisContext;

        public GetUsersImages(Context thisCon){
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
            JSONObject responseJSON = null;
            try {
                responseJSON = new JSONObject(responseString);
                getAllUsersResponse = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException nullPoitExc) {/* if responseString was null*/
                Log.e(TAG, "responseString was null from 'get_all_users' call");
            }
            return null;
        }

        @Override //GetUsersImages class
        protected void onPostExecute(String lenghtOfFile) {

            if(NetworkUtilities.onlineException==false && NetworkUtilities.serverException==false) {//internet work properly
                // do stuff after posting data
                for(int i=0;i<getAllUsersResponse.length();i++)
                {
                    try {

                        JSONObject currentObject = (JSONObject) getAllUsersResponse.get(i);
                        String fullname = currentObject.getString(Constants.FULLNAME);

                        Bitmap currentImage = new DownloadImageBitmapTask().execute(currentObject.getString(Constants.PHOTO_URL)).get();
                        String userId = currentObject.getString(Constants.ID);
                        UserImageEntry currentUser = new UserImageEntry(fullname, currentImage, userId,currentObject.getString(Constants.PHOTO_URL) );
                        usersList.add(currentUser);
                        userToImage.put(userId, currentImage);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch(NullPointerException e){
                        e.printStackTrace();
                    }
                    super.onPostExecute(lenghtOfFile);

                }
                InitGlobalVariables.globalVariables.SetUsersList(usersList);
                InitGlobalVariables.globalVariables.SetUsersImagesMap(userToImage);
                Log.d(TAG, "getUsersImages.successful" + userToImage.toString());
            }
            else {
                InternetErrorGenericToast(this.thisContext, NetworkUtilities.onlineException ,NetworkUtilities.serverException );
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
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


    /**
     * present appropriate Toast message for internet/Server error
     * @param context
     * @param onlineException
     * @param serverException
     */
    public static void InternetErrorGenericToast(Context context , boolean onlineException,boolean serverException ){
        if(onlineException){//==true
            InternetErrorToast(context);
        }
        else if(serverException){
            serverErrorToast(context);
        }
    }

        /**
         * Present Toast message in case the user has internet connection problems
         * @param context
         */
    public static void InternetErrorToast(Context context){
        String text = "Can't connect to PlayGround service. Please check your internet connection";
        Toast toast = Toast.makeText(context,text,Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Present Toast message in case we have error in server
     * @param context
     */
    public static void serverErrorToast(Context context){
        String text = "Can't connect to PlayGround service at the moment. Sorry .Please try again later";
        Toast toast = Toast.makeText(context,text,Toast.LENGTH_LONG);
        toast.show();
    }



}

