package com.inc.playground.playground;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.inc.playground.playground.utils.DownloadImageBitmapTask;

import java.util.concurrent.ExecutionException;

public class FilterActivity extends PreferenceActivity {
    public static final String TAG = "EventInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Display the fragment as the main content.
//        getFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new SettingsFragment())
//                .commit();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.filter);
//        Preference button = (Preference)findPreference("exitlink");
        Button buttonOne = (Button) findViewById(R.id.btn_register);
        ListView v = getListView();
        v.addFooterView(buttonOne);
//        setContentView(R.layout.buttons_layout);
//        Button buttonOne = (Button) findViewById(R.id.btn_register);
//
//        buttonOne.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                Log.i("testbuttonfilter","catch");
//            }
//        });
//
//        if(button!=null){
//            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    Log.i("testbuttonfilter","catch");
//                    return true;
//                }
//            });
//        }

    }
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }
    }

    public void setPlayGroundActionBar(){
        String userLoginId,userFullName,userEmail,userPhoto;
        Bitmap imageBitmap =null;
        GlobalVariables globalVariables;
        final ActionBar actionBar = getActionBar();
        final String MY_PREFS_NAME = "Login";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        globalVariables = ((GlobalVariables) this.getApplication());
        if (prefs.getString("userid", null) != null){
            userLoginId = prefs.getString("userid", null);
            userFullName = prefs.getString("fullname", null);
            userEmail = prefs.getString("emilid", null);
            userPhoto = prefs.getString("picture", null);
            actionBar.setCustomView(R.layout.actionbar_custom_view_home);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            ImageView img_profile = (ImageView) findViewById(R.id.img_profile_action_bar);
            imageBitmap = globalVariables.GetUserPictureBitMap();
            if(imageBitmap==null){
                Log.i(TAG,"downloading");
                try {
                    imageBitmap = new DownloadImageBitmapTask().execute(userPhoto).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            else {
                Log.i(TAG,"Image found");
            }
            img_profile.setImageBitmap(imageBitmap);
            globalVariables.SetUserPictureBitMap(imageBitmap); // Make the imageBitMap global to all activities to avoid downloading twice
        }
    }

}
