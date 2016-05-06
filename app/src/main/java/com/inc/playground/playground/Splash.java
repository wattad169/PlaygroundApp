package com.inc.playground.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import com.inc.playground.playground.utils.GPSTracker;


public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		
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
