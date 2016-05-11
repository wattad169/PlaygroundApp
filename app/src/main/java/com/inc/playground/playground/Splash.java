package com.inc.playground.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.Utils;


public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // how use global variable location
		GlobalVariables globalVariables = ((GlobalVariables) this.getApplication());
		setContentView(R.layout.activity_splash);
		globalVariables.InitGPS(Splash.this);
		globalVariables.SetCurrentLocation(Utils.getMyLocation(globalVariables.GetGPS()));


		
		Thread th = new Thread() {
			@Override
			public void run() {
				try {

					//WebView wv = (WebView) findViewById(R.id.webview);
					//wv.loadUrl("file:///android_asset/splashimage.gif");

					//GPSTracker gps = ((GlobalVariables) this.getApplication()).InitGPS();
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
