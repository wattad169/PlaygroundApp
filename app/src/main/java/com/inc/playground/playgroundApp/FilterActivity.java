package com.inc.playground.playgroundApp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.inc.playground.playgroundApp.utils.DatePreference;
import com.inc.playground.playgroundApp.utils.MembersDialog;

import java.util.ArrayList;

public class FilterActivity extends PreferenceActivity {
    public static final String TAG = "EventInfoActivity";
    //    private static ArrayList<EventsObject> beforeFilterEvents;
    private static ArrayList<EventsObject> afterFilterEvents;
    private GlobalVariables globalVariables;
    ListPreference typePicker, distancePicker;
    DatePreference datePicker;
    CheckBoxPreference myEventsOnly, myCreatedEventsOnly;
    public Preference membersTxt;
    public String memberID = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.filter);

        datePicker = (DatePreference) findPreference("date_picker");
        datePicker.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //your code to change values.
                datePicker.setSummary((String) newValue);
                return true;
            }
        });

        typePicker = (ListPreference) findPreference("type_picker");
        typePicker.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //your code to change values.
                typePicker.setSummary((String) newValue);
                return true;
            }
        });

        myEventsOnly = (CheckBoxPreference) findPreference("myEvents_picker");
        myCreatedEventsOnly = (CheckBoxPreference) findPreference("myCreatedEvents_picker");

        membersTxt = (Preference) findPreference("member_picker");
        membersTxt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                String inputText = "asd";
                MembersDialog newFragment = new MembersDialog();
                newFragment.show(ft, "dialog");
                return true;
            }
        });

        globalVariables = ((GlobalVariables) getApplication());


        distancePicker = (ListPreference) findPreference("distance_picker");
        distancePicker.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //your code to change values.
                distancePicker.setSummary((String) newValue);
                return true;
            }
        });

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        if (prefs.getString("userid", null) == null)
        {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removePreference(myEventsOnly);
            preferenceScreen.removePreference(myCreatedEventsOnly);
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        afterFilterEvents = new ArrayList<EventsObject>();
        if (myEventsOnly.isChecked()) {
            if (globalVariables.GetCurrentUser() != null) {
                ArrayList<EventsObject> beforeFilterEvents = this.globalVariables.GetCurrentUser().getEvents();
                for (EventsObject curEvent : beforeFilterEvents) {
                    doFilter(afterFilterEvents, curEvent);
                }
            } else {
                Intent iv = new Intent(this, MainActivity.class);
                iv.putExtra("parent", "filter");
                iv.putExtra("events", afterFilterEvents);
                startActivity(iv);
                finish();
            }
        } else {
            afterFilterEvents = new ArrayList<>();
            ArrayList<EventsObject> beforeFilterEvents = this.globalVariables.GetHomeEvents();
            for (EventsObject curEvent : beforeFilterEvents) {
                doFilter(afterFilterEvents, curEvent);
            }
        }

        globalVariables.setFilterEvents(afterFilterEvents);
        Intent iv = new Intent(this, MainActivity.class);
        iv.putExtra("parent", "filter");
        iv.putExtra("events", afterFilterEvents);
        startActivity(iv);
        finish();
    }


    private void doFilter(ArrayList<EventsObject> afterFilterEvents, EventsObject curEvent) {
        boolean isFiltred = false;

        if (globalVariables.GetCurrentUser() != null && myCreatedEventsOnly.isChecked()) {
            isFiltred = true;
            if (curEvent.GetCreatorId().equals(globalVariables.GetCurrentUser().GetUserId()))
                afterFilterEvents.add(curEvent);
        }
        if (datePicker.getSummary() != null) {
            if (isFiltred) {
                if (!curEvent.GetDate().equals(datePicker.getSummary()))
                    afterFilterEvents.remove(curEvent);
            } else {
                isFiltred = true;
                if (curEvent.GetDate().equals(datePicker.getSummary()))
                    afterFilterEvents.add(curEvent);
            }
        }

        if (typePicker.getSummary() != null) {
            if (isFiltred) {
                if (!curEvent.GetType().equals(typePicker.getSummary()))
                    afterFilterEvents.remove(curEvent);
            } else {
                isFiltred = true;
                if (curEvent.GetType().equals(typePicker.getSummary()))
                    afterFilterEvents.add(curEvent);
            }
        }

        if (distancePicker.getSummary() != null) {
            if (isFiltred) {
                if(distancePicker.getSummary().equals("Walking distance (up to 2 km)"))
                {
                    if (!(Double.valueOf(curEvent.GetDistance()) < 2))
                        afterFilterEvents.remove(curEvent);
                }
                else if(distancePicker.getSummary().equals("Local area (up to 10 km)"))
                {
                    if (!(Double.valueOf(curEvent.GetDistance()) < 10))
                        afterFilterEvents.remove(curEvent);
                }
                else if(distancePicker.getSummary().equals("Over 10 km"))
                {
                    if (!(Double.valueOf(curEvent.GetDistance()) > 10))
                        afterFilterEvents.remove(curEvent);
                }

            } else {
                isFiltred = true;
                if(distancePicker.getSummary().equals("Walking distance (up to 2 km)"))
                {
                    if (Double.valueOf(curEvent.GetDistance()) < 2)
                        afterFilterEvents.add(curEvent);
                }
                else if(distancePicker.getSummary().equals("Local area (up to 10 km)"))
                {
                    if (Double.valueOf(curEvent.GetDistance()) < 10)
                        afterFilterEvents.add(curEvent);
                }
                else if(distancePicker.getSummary().equals("Over 10 km"))
                {
                    if (Double.valueOf(curEvent.GetDistance()) > 10)
                        afterFilterEvents.add(curEvent);
                }
            }
        }

        if (membersTxt.getSummary() != null && ( ! membersTxt.getSummary().equals("Looking for somebody's events?"))) {
            if(isFiltred)
            {
                if(! curEvent.GetMembers().contains(memberID))
                    afterFilterEvents.remove(curEvent);

            } else {
                isFiltred = true;
                if(curEvent.GetMembers().contains(memberID))
                    afterFilterEvents.add(curEvent);
            }
        }
        if (!isFiltred) {
            afterFilterEvents.add(curEvent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Filter Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.inc.playground.playground/http/host/path")
        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Filter Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.inc.playground.playground/http/host/path")
        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }
    }


}