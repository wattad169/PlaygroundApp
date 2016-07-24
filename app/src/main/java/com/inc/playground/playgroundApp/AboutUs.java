package com.inc.playground.playgroundApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by lina on 7/21/2016.
 */
public class AboutUs extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView urlApp = (TextView) findViewById(R.id.urlTxt);
        urlApp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed()
    {
        Intent iv = new Intent(this,MainActivity.class);
        startActivity(iv);
        finish();
    }
}
