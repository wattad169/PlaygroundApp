package com.inc.playground.playground;

import android.os.Bundle;
import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class FilterActivity extends PreferenceActivity {

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

}
