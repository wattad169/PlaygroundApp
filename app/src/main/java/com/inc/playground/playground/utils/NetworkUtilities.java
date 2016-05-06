
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
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.accounts.Account;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.inc.playground.playground.EventsObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                }
            }
            if ((resopnseString != null) && (resopnseString.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                return resopnseString;
            } else {
                Log.e(TAG, "Error authenticating" + resp.getStatusLine());
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
            return null;
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }
    }
    public static Double calculateDistance(float originLon,float originLat,float distanceLon,float distanceLat){
        final HttpResponse resp;
        String apiUrl = Constants.apiKey.replace("X1",Float.toString(originLon));
        apiUrl = apiUrl.replace("Y1",Float.toString(originLat));
        apiUrl = apiUrl.replace("X2",Float.toString(distanceLon));
        apiUrl = apiUrl.replace("Y2",Float.toString(distanceLat));
        apiUrl = apiUrl.replace("APIKEY",Constants.apiKey);
        HttpGet http_client = new HttpGet(apiUrl);

        try {
            resp = getHttpClient().execute(http_client);
            String resopnseString = null;
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream istream = (resp.getEntity() != null) ? resp.getEntity().getContent()
                        : null;
                if (istream != null) {
                    BufferedReader ireader = new BufferedReader(new InputStreamReader(istream));
                    resopnseString = ireader.readLine().trim();
                }
            }
            if ((resopnseString != null) && (resopnseString.length() > 0)) {
                Log.v(TAG, "Successful authentication");
                JSONObject convertedResponse = new JSONObject(resopnseString);
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
        } finally {
            Log.v(TAG, "getAuthtoken completing");
        }

    }
    public static HashMap<Integer,EventsObject> eventListToHashMap(JSONArray jsonInput) throws JSONException {


        for(int i=0 ; i<jsonInput.length();i++){
            JSONObject currentObject = (JSONObject) jsonInput.get(i);
            String eventId = currentObject.getString(Constants.EVENT_ID);
            EventsObject currentEvent = new EventsObject();
            currentEvent.setEvent_id(eventId);
            currentEvent.setName(currentObject.getString(Constants.EVENT_NAME));
            currentEvent.setAddress(currentObject.getString(Constants.EVENT_LOCATION));

            // get your lon and lat from the getMylocation function in Utils.java
            currentEvent.setDistance(calculateDistance());


        }





    }



}