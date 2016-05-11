package com.inc.playground.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToHashMap;


public class Splash extends Activity {
    private static final String TAG = "Splash: ";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get events from server
        GlobalVariables globalVariables = ((GlobalVariables) this.getApplication());
        setContentView(R.layout.activity_splash);
        globalVariables.InitGPS(Splash.this);
        globalVariables.SetCurrentLocation(Utils.getMyLocation(globalVariables.GetGPS()));
        // Create server call
        String eventsFromServerString;
        JSONObject cred = new JSONObject();
        String userToken = "StubToken";//TODO Replace with real token
        try {

            try {
                cred.put(NetworkUtilities.TOKEN, userToken);
            } catch (JSONException e) {
                Log.i(TAG, e.toString());
            }
            eventsFromServerString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_all_events/");

    } catch (Exception ex) {
            Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
            Log.i(TAG, ex.toString());
            eventsFromServerString = "";
    }
        // Convert string received from server to JSON array
        JSONArray eventsFromServerJSON = null;
        try {
            eventsFromServerJSON = new JSONArray(eventsFromServerString);
            globalVariables.SetHomeEvents(eventListToHashMap(eventsFromServerJSON, globalVariables.GetCurrentLocation()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
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
}
