/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inc.playground.playground;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.InitGlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToArrayList;

public class MyGcmListenerService extends GcmListenerService {
    public static GlobalVariables globalVariables;

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
        JSONObject responseJSON = null;
        if(! title.contains("canceled"))
        {
            String eventToDisplay = data.getString("more");
            try{
                responseJSON = new JSONObject(eventToDisplay);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch(NullPointerException nPoitExc){
                nPoitExc.printStackTrace();
            }
            Log.d(TAG, "more: " + eventToDisplay);
        }

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        sendNotification(message, title, responseJSON);

//        if(eventToDisplay!=null){

//        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message,String title,JSONObject inputJson) {

        globalVariables = ((GlobalVariables) this.getApplication());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent iv = new Intent();
        // if cancel event
        if(title.contains("requested"))
        {
            iv = new Intent(MyGcmListenerService.this,ApproveEventList.class);
            iv.putExtra("parent","MyGcm");
            iv.putExtra("inputJson", inputJson.toString() );
        }
        else
        {
            ArrayList<NotificationObject> notificationList = globalVariables.GetNotifications();
            EventsObject curEvent = new EventsObject();
            NotificationObject curNotification = new NotificationObject();
            curNotification.setDescription(message);
            curNotification.setTitle(title);
            curNotification.setInputJson(inputJson);
            notificationList.add(curNotification);
            globalVariables.SetNotifications(notificationList);

            iv = new Intent(MyGcmListenerService.this,NotificationsList.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, iv,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
