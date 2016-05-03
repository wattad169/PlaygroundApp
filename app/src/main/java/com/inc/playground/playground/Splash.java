package com.inc.playground.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


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
					sleep(2000);
					Intent i = new Intent(getBaseContext(), Home.class);
					startActivity(i);
					finish();
				} catch (Exception e) {

				}

			}
		};
		th.start();
	}
}
