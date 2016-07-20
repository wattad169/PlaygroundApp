
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.inc.playground.playground.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.inc.playground.playground.EventsObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import java.util.HashMap;

/**
 * Provides utility methods for communicating with the server..
 */

final public class NetworkUtilities {
    /** The tag used to log to adb console. */
    private static final String TAG = "NetworkUtilities";
    /** POST parameter name for the user's account name */
    public static final String PARAM_USERNAME = "user_name";
    /** POST parameter name for the user's password */
    public static final String PARAM_PASSWORD = "password";
    /** POST parameter name for the user's authentication token */
    public static final String PARAM_AUTH_TOKEN = "authtoken";
    /** POST parameter name for the client's last-known sync state */
    public static final String PARAM_SYNC_STATE = "syncstate";
    /** POST parameter name for the sending client-edited contact info */
    public static final String PARAM_CONTACTS_DATA = "contacts";
    /** Timeout (in ms) we specify for each http request */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** Base URL for the v2 Sample Sync Service */
    public static final String BASE_URL = "https://sportbuddy-1261.appspot.com";
    /** URI for authentication service */
    public static final String AUTH_URI = BASE_URL + "/login/";
    /** URI for sync service */
    public static final String SYNC_CONTACTS_URI = BASE_URL + "/sync";

    public static final String TOKEN = "token";

    public static final String USER_ID = "user_id";

    public static boolean onlineException = false;//indicate if the user have internet problems

    public static boolean serverException = false;//indicate if we have problem in the server

    public static final String PHOTO_URL = "photo_url";
    private static double originLon;
    private static double originLat;
    private static double distanceLon;
    private static double distanceLat;


    private NetworkUtilities() {
    }
    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    }
    /**
     * Connects to the SampleSync test server, authenticates the provided
     * username and password.
     *
     * @return String The authentication token returned by the server (or null)
     */
    public static String doPost(JSONObject cred, String apiUrl) throws UnsupportedEncodingException {
        final HttpResponse resp;

        Log.i(TAG, cred.toString());
        StringEntity params = null;

        params = new StringEntity(cred.toString());


//        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(PARAM_USERNAME, username));
//        params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
        final HttpEntity entity;
//        try {
//            entity = new UrlEncodedFormEntity(params);
//        } catch (final UnsupportedEncodingException e) {
//            // this should never happen.
//            throw new IllegalStateException(e);
//        }
        Log.i(TAG, "Authenticating to: " + apiUrl);
        final HttpPost post = new HttpPost(apiUrl);
        post.addHeader("content-type", "application/json");
        post.setEntity(params);
        try {
            resp = getHttpClient().execute(post);
            String resopnseString = null;
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream istream = (resp.getEntity() != null) ? resp.getEntity().getContent()
                        : null;
                if (istream != null) {
                    BufferedReader ireader = new BufferedReader(new InputStreamReader(istream));
                    resopnseString = ireader.readLine().trim();
                    Log.i("DOPOST",resopnseString);
                }
            }
            if ((resopnseString != null) && (resopnseString.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                return resopnseString;
            } else {
                Log.e(TAG, "Error authenticating" + resp.getStatusLine());
                serverException = true;
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            onlineException= true; // will be used in Splash to detect errors in internet connections
            return null;
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }
    }
    //source: http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    public static Double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        //calculate air distance
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return Math.round(d * 10d) / 10d; //round 1 point after digit
    }
    //helper function to calculate distance
    static double deg2rad(double deg) {

        return deg * (Math.PI/180);
    }

    //This function has bug. It use google api(therefore added 2 -not to be used)
    public static Double calculateDistance2(double originLon, double originLat, double distanceLon, double distanceLat){
        final HttpResponse resp;

        BigDecimal originLon_tune_bg =new BigDecimal(originLon, MathContext.DECIMAL64);
        BigDecimal originLat_tune_bg =new BigDecimal(originLat, MathContext.DECIMAL64);
        BigDecimal distanceLon_tune_bg =new BigDecimal(distanceLon, MathContext.DECIMAL64);
        BigDecimal distanceLat_tune_bg =new BigDecimal(distanceLat, MathContext.DECIMAL64);


        String str_originLon_tune  =  (originLon_tune_bg+"").substring(0,6);
        String str_originLat_tune  =  (originLat_tune_bg+"").substring(0,6);
        String str_distanceLon_tune=  (distanceLon_tune_bg+"").substring(0, 6);
        String str_distanceLat_tune=  (distanceLat_tune_bg+"").substring(0, 6);

/*
        String originLon_tune = String.format("%.3f", ( originLon));
        String originLat_tune =  String.format("%.3f", (originLat));
        String distanceLon_tune =String.format("%.3f", (distanceLon));
        String distanceLat_tune =String.format("%.3f", (distanceLat)) ;
*/

        String apiUrl = Constants.apiGetDistanceUrl.replace("X1",
                str_originLon_tune);
        apiUrl = apiUrl.replace("Y1",str_originLat_tune);
        apiUrl = apiUrl.replace("X2",str_distanceLon_tune);
        apiUrl = apiUrl.replace("Y2",str_distanceLat_tune);
        apiUrl = apiUrl.replace("APIKEY",Constants.apiKey);
        HttpGet http_client = new HttpGet(apiUrl);

        try {
            resp = getHttpClient().execute(http_client);
            String resopnseString = "";
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream istream = (resp.getEntity() != null) ? resp.getEntity().getContent()
                        : null;
                if (istream != null) {
                    BufferedReader ireader = new BufferedReader(new InputStreamReader(istream));
                    String buf;
                    while((buf =ireader.readLine() ) != null ){
                        resopnseString += buf;
                    }
                }
            }
            if ((resopnseString != null) && (resopnseString.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                Log.i(TAG, resopnseString);//resopnseString = "{"
                JSONObject convertedResponse = new JSONObject(resopnseString); //"{"
                Double distanceKm = new Double((convertedResponse.getJSONObject("rows").getJSONObject("elements").getJSONObject("distance").getInt("value"))/1000);
                return distanceKm;

            } else {
                Log.e(TAG, "Error authenticating" + resp.getStatusLine());
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 2.0;
    }


    /**
     *
     * @param jsonObject
     * @param currentLocation
     * @return
     * @throws JSONException
     */
    public static EventUserObject fillEventObject(JSONObject jsonObject ,HashMap<String, Double> currentLocation) throws JSONException {
        String eventId = jsonObject.getString(Constants.EVENT_ID);
        EventUserObject currentEvent = new EventUserObject();

        currentEvent.SetId(eventId);
        currentEvent.SetName(jsonObject.getString(Constants.EVENT_NAME));
        currentEvent.SetFormattedLocation(jsonObject.getString(Constants.EVENT_LOCATION));
        currentEvent.SetType(jsonObject.getString(Constants.EVENT_TYPE));
        currentEvent.SetSize(jsonObject.getString(Constants.EVENT_MIN_SIZE));
        currentEvent.setMaxSize(jsonObject.getString(Constants.EVENT_MAX_SIZE));
        currentEvent.SetDate(jsonObject.getString(Constants.EVENT_DATE));
        currentEvent.SetStartTime(jsonObject.getString(Constants.START_TIME));
        currentEvent.SetEndTime(jsonObject.getString(Constants.END_TIME));
        currentEvent.SetDescription(jsonObject.getString(Constants.EVENT_DESCRIPTION));
        currentEvent.SetCreatorId(jsonObject.getString(Constants.CREATED_BY));
        currentEvent.setIsPublic(jsonObject.getString(Constants.IS_PUBLIC));
        // find distance
        Log.d(TAG,currentLocation.toString());
        double currentLon  = currentLocation.get(Constants.LOCATION_LON);
        double  currentLat= currentLocation.get(Constants.LOCATION_LAT);
        String eventLon  = jsonObject.getJSONObject("location").getString(Constants.LOCATION_LON);
        String eventLat = jsonObject.getJSONObject("location").getString(Constants.LOCATION_LAT);
        currentEvent.SetPosition(eventLat, eventLon);
        currentEvent.SetDistance(Double.toString(calculateDistance(currentLon, currentLat, Double.parseDouble(eventLon), Double.parseDouble(eventLat))));//change order

        ArrayList<String> members = new ArrayList<>();
        JSONArray membersJson = jsonObject.getJSONArray(Constants.EVENT_MEMBERS);
        for(int i=0 ; i<membersJson.length();i++){
            members.add(membersJson.getString(i));
        }
        currentEvent.SetMembers(members);
        return currentEvent;
    }

    /**
     *  Converts JSON Object containing events info to ArrayList of events objects
     * @param jsonInput - The JSON object received from server
     * @param currentLocation   - Location of phone (for calculating distance)
     * @return ArrayList containing event objects
     * @throws JSONException
     */
    public static ArrayList<EventsObject> eventListToArrayList(JSONArray jsonInput,HashMap<String, Double> currentLocation) throws JSONException {
        ArrayList<EventsObject> events = new ArrayList<EventsObject>();
        Log.i("testi",String.valueOf(jsonInput.length()));
        for(int i=0 ; i<jsonInput.length();i++){
            //Fill the EventObject with data from the JSON
            JSONObject currentObject = (JSONObject) jsonInput.get(i);
            EventsObject currentEvent = fillEventObject(currentObject , currentLocation);
            // add event to  ArrayList<EventsObject>
            events.add(currentEvent);
        }
        return events;
    }



    /**
     *
     * @param jsonInput
     * @param currentLocation
     * @param whichEventTable
     * @return
     * @throws JSONException
     */
    public static ArrayList<EventUserObject> eventUserListToArrayList(JSONArray jsonInput,HashMap<String, Double> currentLocation ,String whichEventTable ) throws JSONException {
        assert( whichEventTable.equals(Constants.EVENTS_WAIT4APPROVAL)||
                whichEventTable.equals(Constants.EVENTS_DECLINE) ||
                whichEventTable.equals(Constants.EVENT_ENTRIES));
        ArrayList<EventUserObject> events = new ArrayList<EventUserObject>();
        Log.i("testi",String.valueOf(jsonInput.length()));
        for(int i=0 ; i<jsonInput.length();i++){
            //Fill the EventObject with data from the JSON
            JSONObject currentObject = (JSONObject) jsonInput.get(i);
            EventUserObject currentEvent = fillEventObject(currentObject , currentLocation);
            currentEvent.setEventUserStatus(whichEventTable);
            // add event ArrayList<EventUserObject>
            events.add(currentEvent);
        }
        return events;
    }

    /**
     *
     * @param jsonObject
     * @param currentLocation
     * @return ArrayList<EventUserObject> - all the events types of the login user
     * @throws JSONException
     */
    public static ArrayList<EventUserObject> allUserEvents(JSONObject jsonObject,HashMap<String, Double> currentLocation) throws JSONException {
        ArrayList<EventUserObject> eventsUserObject = new ArrayList<EventUserObject>();
        String[] values= {Constants.EVENT_ENTRIES,"events_decline","events_wait4approval"};
        ArrayList<EventUserObject> eventsList;
        JSONArray jsonArray;
        String whichTable;
        for(int i=0;i<values.length; i++){
            whichTable = values[i];
            jsonArray = jsonObject.getJSONArray(whichTable);//need to check null?
            eventsList = eventUserListToArrayList(jsonArray ,currentLocation , whichTable );
            if(eventsList!=null) {
                eventsUserObject.addAll(eventsList);
            }
            eventsList.clear();
        }
        return eventsUserObject;
    }

}