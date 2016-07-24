package com.inc.playground.playgroundApp;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inc.playground.playgroundApp.utils.Constants;
import com.inc.playground.playgroundApp.utils.CustomMarker;
import com.inc.playground.playgroundApp.utils.DownloadImageBitmapTask;
import com.inc.playground.playgroundApp.utils.GPSTracker;
import com.inc.playground.playgroundApp.utils.InitGlobalVariables;
import com.inc.playground.playgroundApp.utils.NetworkUtilities;
import com.inc.playground.playgroundApp.utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by lina on 5/13/2016.
 */


public class EventInfo extends FragmentActivity {
    ProgressDialog progressDialog;
    ArrayList<EventsObject> rest;
    View layout12;

    String Error;

    Button btn_fvrt, btn_fvrt1;
    CustomMarker customMarkerOne;
    private HashMap<CustomMarker, Marker> markersHashMap;
    private Iterator<Map.Entry<CustomMarker, Marker>> iter;
    private CameraUpdate cu;
    GPSTracker gps;
    double latitudecur;
    double longitudecur;
    GoogleMap googleMap;
    GlobalVariables globalVariables;
    ImageButton shareButton,moreButton;

    public static final String TAG = "EventInfoActivity";
    //DahanLina

    EventsObject currentEvent;
    TextView viewName, viewDateEvent, viewStartTime, viewEndTime, viewCurMembers, viewLocation, viewEventDescription, viewPlay, viewStatus, fullView;
    ImageView typeImg, statusImg;
    JSONArray membersImagesUrls;
    private HandleEventTask handleEventTask = null;
    //private HandleEventTask LeaveEventTask = null;
    public SharedPreferences prefs;
    LinearLayout membersList;
    User currentUser;
    ToggleButton playButton;
    Bitmap imageBitmap;
    int minSize,maxSize,currenctSize;
    boolean falgForJoin = true;



    ScrollView mainLayout;
    public ArrayList<String> urlList = new ArrayList<>(); //saves the url of members by order

    public String parentActivity; // lina add

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        prefs = getSharedPreferences("Login", MODE_PRIVATE);

        setPlayGroundActionBar();
        Intent intent = getIntent();

        parentActivity = intent.getStringExtra("parentActivity");
        if(parentActivity == null) parentActivity = " ";
        currentEvent = (EventsObject) intent.getSerializableExtra("eventObject");
        currentUser = globalVariables.GetCurrentUser();
        viewName = (TextView) findViewById(R.id.event_name);
        viewDateEvent = (TextView) findViewById(R.id.event_date);
        viewStartTime = (TextView) findViewById(R.id.event_start_time);
        viewEndTime = (TextView) findViewById(R.id.event_end_time);
        viewCurMembers = (TextView) findViewById(R.id.cur_membersTxt);
        viewLocation = (TextView) findViewById(R.id.event_formatted_location);
        viewEventDescription = (TextView) findViewById(R.id.event_description);
        typeImg = (ImageView) findViewById(R.id.type_img);
        playButton = (ToggleButton) findViewById(R.id.playing_btn);
        viewPlay = (TextView) findViewById(R.id.Play_txt);
        fullView = (TextView) findViewById(R.id.fullTxt);
        moreButton = (ImageButton) findViewById(R.id.more_btn);
        shareButton = (ImageButton) findViewById(R.id.share_btn);
        viewStatus = (TextView) findViewById(R.id.statusTxt);
        statusImg = (ImageView) findViewById(R.id.statusImg);
        membersList = (LinearLayout) findViewById(R.id.members_list);
        mainLayout = (ScrollView) findViewById(R.id.mainLayout);

        new GetMembersImages(this).execute();
        gps = new GPSTracker(EventInfo.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitudecur = gps.getLatitude();
            longitudecur = gps.getLongitude();
        }
        else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        fullView.setVisibility(View.INVISIBLE);
        setdata();

    }

    private void setdata() {
        double latitude = 0, longitude = 0;
        try {
            HashMap<String, String> location = this.currentEvent.GetLocation();
            latitude = Double.parseDouble(location.get("lat"));
            longitude = Double.parseDouble(location.get("lon"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // TODO: handle exception how? lina,yarden, mostafa?
        }

        Log.d("location", "" + latitude + longitude);

        GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment))
                .getMap();

        LatLng position = new LatLng(latitude, longitude);
        customMarkerOne = new CustomMarker("markerOne", latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions().position(

                new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title(this.currentEvent.GetName() + this.currentEvent.GetFormattedLocation());

        Marker newMark = googleMap.addMarker(markerOption);

        addMarkerToHashMap(customMarkerOne, newMark);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        minSize = Integer.parseInt(currentEvent.GetSize());
        maxSize = Integer.parseInt(currentEvent.getMaxSize());

        Typeface fontText = Typeface.createFromAsset(getAssets(), "kimberly.ttf");
        viewName.setTypeface(fontText);

        // Set event view values
        viewName.setText(currentEvent.GetName());
        viewDateEvent.setText(currentEvent.GetDate());
        viewStartTime.setText(currentEvent.GetStartTime());
        viewEndTime.setText(currentEvent.GetEndTime());
        viewLocation.setText(currentEvent.GetFormattedLocation());
        viewEventDescription.setText(currentEvent.GetDescription());
        viewStatus.setVisibility(View.INVISIBLE);
        statusImg.setVisibility(View.INVISIBLE);
        if (currentUser != null) { // the user is login


            //user belong to some kind of event
            if (! (currentUser.getEvents().isEmpty()&& currentUser.getEvents_wait4approval().isEmpty() && currentUser.getEvents_decline().isEmpty() )) {
                if (User.userEventsContainEventId(currentUser.getEvents(), currentUser.getEvents_wait4approval(), currentUser.getEvents_decline(), currentEvent.GetId())) {//user in the event
                    if(currentUser.GetUserId().equals(currentEvent.GetCreatorId())){//creator
//                        playButton.setChecked(true);//Todo change the tringle to other picture for manager - lina?
                        playButton.setVisibility(View.INVISIBLE);
                        playButton.setTextColor(Color.parseColor("#000000"));
                        viewPlay.setText("Hosting");
                        viewPlay.setTextColor(Color.parseColor("#000000"));
                    }
                    else { //regular member
                        //regular event
                        if(currentUser.GetUserEvents().contains(currentEvent.GetId())) {
                            playButton.setChecked(true);
                            //playButton.setClickable(false); -avoid it: make it impossbile to click on the button
                            viewPlay.setText("Playing");
                            viewPlay.setTextColor(Color.parseColor("#104E8B"));
                        }
                        //events_wait4approval
                        if(User.eventsObjectContainEvent(currentUser.getEvents_wait4approval() , currentEvent.GetId())){
                            playButton.setVisibility(View.INVISIBLE);
                            viewPlay.setText("Waiting");
                            viewPlay.setTextColor(Color.parseColor("#104E8B"));
                        }
                        //events_decline
                        if(User.eventsObjectContainEvent(currentUser.getEvents_decline() , currentEvent.GetId()) ){
                            playButton.setVisibility(View.INVISIBLE);
                            viewPlay.setText("Declined");
                            viewPlay.setTextColor(Color.parseColor("#0xffff0000"));
                        }

                    }
                }
            }
        }

        String uri = "@drawable/pg_" + currentEvent.GetType() + "_icon";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable typeDrawable = getResources().getDrawable(imageResource);
        typeImg.setImageDrawable(typeDrawable);


        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(EventInfo.this, shareButton);
                setForceShowIcon(popup);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.share_menu_in_event_info, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.share_invite:
                                if (currentEvent.getIsPublic().equals("0")) {
                                    toastGeneral(getApplicationContext(), "You cannot invite users to private event", Toast.LENGTH_SHORT);
                                } else {
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    android.app.Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                                    if (prev != null) {
                                        ft.remove(prev);
                                    }
                                    ft.addToBackStack(null);
                                    String inputText = "asd";
                                    DialogFragment newFragment = new MyDialogFragment(currentEvent.GetId(), currentUser.GetUserId());
                                    newFragment.show(ft, "dialog");
                                }

                                break;
                            case R.id.share_calendar:
                                Calendar cal = Calendar.getInstance();
                                int date[] = parseDate(currentEvent.GetDate());
                                int startTime[] = parseHour(currentEvent.GetStartTime());
                                int endTime[] = parseHour(currentEvent.GetEndTime());
                                cal.set(date[0], date[1], date[2], startTime[0], startTime[1]);
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                                cal.set(date[0], date[1], date[2], endTime[0], endTime[1]);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis());
                                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                                intent.putExtra("title", currentEvent.GetName());
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
        if(currentUser.GetUserId().equals(currentEvent.GetCreatorId())){
            //show the more menu that contains cancel/edit event for the creator only

            moreButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(EventInfo.this, moreButton);
                    setForceShowIcon(popup);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.more_menu_in_event_info, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.edit_event_id:
                                    Intent intent = new Intent(EventInfo.this, EditEvent.class);
                                    intent.putExtra("eventObject", currentEvent);
                                    startActivity(intent);
                                    break;
                                case R.id.cancel_event_id:

                                    //dialog (cance/ok)
                                    final Dialog alertDialog = new Dialog(EventInfo.this);
                                    alertDialog.setContentView(R.layout.cancel_event_dialog);
                                    alertDialog.setTitle("Cancel event");
                                    alertDialog.findViewById(R.id.cancelEvent_ok_btn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //toast
                                            toastGeneral(getApplicationContext(), "Event was canceled", Toast.LENGTH_SHORT);
                                            //cancel event
                                            String eventTask = "cancel_event";
                                            new HandleEventTask(currentEvent, eventTask).execute((Void) null);
                                            finish();
                                        }
                                    });

                                    alertDialog.findViewById(R.id.cancelEvent_cancel_btn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case R.id.join_requests:
                                    Intent approveIntent = new Intent(EventInfo.this, ApproveEventList.class);
                                    approveIntent.putExtra("eventObject", currentEvent);
                                    approveIntent.putExtra("parent","EventInfo");
                                    startActivity(approveIntent);
                                    finish();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                }
            });//closing the setOnClickListener method
        }
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        markersHashMap.put(customMarker, marker);
    }

    public void setUpMarkersHashMap() {
        if (markersHashMap == null) {
            markersHashMap = new HashMap<CustomMarker, Marker>();
        }
    }

    public void zoomToMarkers(View v) {
        zoomAnimateLevelToFitMarkers(120);
    }

    public void zoomAnimateLevelToFitMarkers(int padding) {
        iter = markersHashMap.entrySet().iterator();
        LatLngBounds.Builder b = new LatLngBounds.Builder();

        LatLng ll = null;
        while (iter.hasNext()) {
            Map.Entry mEntry = iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());

            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        Log.d("bounds", "" + bounds);

        // Change the padding as per needed
        cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 400, 17);
        googleMap.animateCamera(cu);

    }
    public void onPlayClick(View v) {
        ToggleButton x = (ToggleButton) v;
        /*user photo */
        String eventTask = null;
        if (!x.isChecked()) {
            assert (currentUser!=null);
            if(currentUser.GetUserId().equals(currentEvent.GetCreatorId())) {//cancel event
                //toast
                toastGeneral(getApplicationContext(), "Event was canceled", Toast.LENGTH_SHORT);
                eventTask = "cancel_event";
                viewPlay.setText("cancel");
                viewPlay.setTextColor(Color.parseColor("#D0D0D0"));
                finish();
            }
            else{ //leave_event
                eventTask = "leave_event";
                toastGeneral(getApplicationContext(), "You left the event", Toast.LENGTH_SHORT);
                x.setChecked(false);
                viewPlay.setText("Play");
                viewPlay.setTextColor(Color.parseColor("#D0D0D0"));
                //remove member picture
                membersList.removeView(findMemberPhoto());
                currenctSize = membersList.getChildCount();
                viewCurMembers.setText(String.valueOf(currenctSize));
                if (currenctSize < minSize) {
                    viewStatus.setVisibility(View.INVISIBLE);
                    statusImg.setVisibility(View.INVISIBLE);
                }
                else
                {
                    viewStatus.setVisibility(View.VISIBLE);
                    statusImg.setVisibility(View.VISIBLE);
                }
                //update set
                Set<String> setEvents = currentUser.GetUserEvents();
                setEvents.remove(currentEvent.GetId());
                currentUser.SetUserEvents(setEvents);
                // update list
                ArrayList<EventsObject> eventsUser = currentUser.getEvents();
                for(int i =0 ; i<eventsUser.size() ; i++ ) {
                    EventsObject e = eventsUser.get(i);
                    if (e.GetId().equals(currentEvent.GetId())) {
                        eventsUser.remove(i);
                        break;
                    }
                }
                currentUser.setEvents(eventsUser);

//                currentUser.removeSpecificEventById(currentEvent.GetId());//remove current event from the specific record
                //update global variables
                globalVariables.SetCurrentUser(currentUser);

            }
        } else {//join event

            //private event
            if (currentEvent.getIsPublic().equals("0")) {
                eventTask = "request_join_event";
                x.setChecked(true); //change icon to waiting
                viewPlay.setText("Waiting");//for approval
                toastGeneral(getApplicationContext(), "Waiting for event manager approval", Toast.LENGTH_SHORT);
                //Todo: change icon to question mark

                //update event to events_wait4approval
                ArrayList<EventsObject> temp = currentUser.getEvents_wait4approval();
                temp.add(currentEvent);
                currentUser.setEvents_wait4approval(temp);
                viewCurMembers.setText(String.valueOf(currenctSize));
                globalVariables.SetCurrentUser(currentUser);

            }
            //public event
            else {
                eventTask = "join_event";

            }
            //update global user events list - for join
            //Todo : update
//            currentUser.getUserEventsObjects().add(currentEvent);

//            currentUser.SetUserEvents(userEvents);
            globalVariables.SetCurrentUser(currentUser);

        }

        /*Server side update */
        handleEventTask = new HandleEventTask(currentEvent, eventTask);
        handleEventTask.execute((Void) null);

        if(eventTask.equals("join_event") && falgForJoin)
        {
            x.setChecked(true);
            viewPlay.setText("Playing");
            viewPlay.setTextColor(Color.parseColor("#104E8B"));
            toastGeneral(getApplicationContext(), "You joined the event !" , Toast.LENGTH_SHORT);
            //add member picture
            ImageView member = new ImageView(this);
            member.setImageResource(R.drawable.pg_time);
            Bitmap currentImgae = getRoundedShape(globalVariables.GetUserPictureBitMap());
            member.setImageBitmap(currentImgae);
            member.setId(membersImagesUrls.length() + 1);
            urlList.add(currentUser.getPhotoUrl());
            membersList.addView(member);
            //add for this new member listener
            member.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              // new changes
                                              new EventPhotoUserListener(currentUser.getPhotoUrl()).execute();
                                          }
                                      }

            );
            currenctSize = membersList.getChildCount();
            viewCurMembers.setText(String.valueOf(currenctSize));

            //set status ("game on!" or not)
            if (currenctSize >= minSize) {
                viewStatus.setVisibility(View.VISIBLE);
                statusImg.setVisibility(View.VISIBLE);
            }
            if (currenctSize == maxSize) {

                //TODO : set not clickable for join to event -> only for leave

            }
            //update set
            Set<String> setEvents = currentUser.GetUserEvents();
            setEvents.add(currentEvent.GetId());
            currentUser.SetUserEvents(setEvents);
            // update list
            ArrayList<EventsObject> temp2 = currentUser.getEvents();
            temp2.add(currentEvent);
            currentUser.setEvents(temp2);
            //update global variables
            globalVariables.SetCurrentUser(currentUser);
        }
        else if (eventTask.equals("join_event") && !falgForJoin)
        {
            toastGeneral(getApplicationContext(), "Join canceled, full event", Toast.LENGTH_SHORT);
        }
}

    public class HandleEventTask extends AsyncTask<Void, Void, String> {
        /*handle 3 requests: 1.join_event 2. leave_event 3. cancel_events (the whole event) */

        private String responseString;
        String eventTask;
        private EventsObject currentEvent;
        public HandleEventTask(EventsObject currentEvent,String eventTask) {
            this.currentEvent = currentEvent;
            this.eventTask = eventTask;
            assert(eventTask.equals("join_event") || eventTask.equals("leave_event")||
                    eventTask.equals("cancel_event")||eventTask.equals("request_join_event") );
        }

        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject cred = new JSONObject();
            if (prefs.getString("userid", null) != null) {
                //If the user is logged in
                String userId = prefs.getString("userid", null);

                try {//Send request to server for eventTask
                    cred.put(NetworkUtilities.TOKEN, userId);
                    cred.put("event_id", currentEvent.GetId());
                    //Todo : should ask the creator to join
                    //cred.put(<creator> , )
                    responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/" + eventTask+ "/");//'eventTask' can be: leave/cancel/join event
                } catch (JSONException|UnsupportedEncodingException|NullPointerException e) {
                    e.printStackTrace();
                }
                if (responseString == null) {
                    Log.i("TESTID", currentEvent.GetId());
                }

                //Check response
                JSONObject myObject = null;
                String responseStatus = null;
                try {
                    myObject = new JSONObject(responseString);
                    responseStatus = myObject.getString(Constants.RESPONSE_STATUS);
                } catch (JSONException|NullPointerException e) {
                    e.printStackTrace();
                }

                if (myObject != null && responseStatus != null) {
                    if (responseStatus.equals(Constants.RESPONSE_OK.toString())) {
                        if(eventTask.equals("join_event")) // when event is not full
                        {
                            falgForJoin = true;
                        }
                        handleEventTask = null;
                    } else if (responseStatus.equals(Constants.RESPONSE_NOK.toString())){
                        if(eventTask.equals("join_event")) // when event is full and we cant join
                        {
                            falgForJoin = false;
                        }
                        handleEventTask= null;
                    }
                }

            }
            else {
                // If user is not logged -> in send to login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String responseString) {}

    }

    public void setPlayGroundActionBar() {
        String userLoginId, userFullName, userEmail, userPhoto;
        Bitmap imageBitmap = null;

        final ActionBar actionBar = getActionBar();
        final String MY_PREFS_NAME = "Login";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        globalVariables = ((GlobalVariables) this.getApplication());
        if (prefs.getString("userid", null) != null) {
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
            if (imageBitmap == null) {
                Log.i(TAG, "downloading");
                try {
                    imageBitmap = new DownloadImageBitmapTask().execute(userPhoto).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException|NullPointerException e) {
                    e.printStackTrace();
                }

            } else {
                Log.i(TAG, "Image found");
            }
            img_profile.setImageBitmap(imageBitmap);
            globalVariables.SetUserPictureBitMap(imageBitmap); // Make the imageBitMap global to all activities to avoid downloading twice
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
    }

    public class GetMembersImages extends AsyncTask<String, Integer, String> {
        int i;
        String photoURL;

        Context thisContext;
        private ProgressDialog dialog = new ProgressDialog(EventInfo.this);

        GetMembersImages(Context thisCon) {
            thisContext = thisCon;
        }

        @Override
        protected void onPreExecute() {
            initProgressDialog(dialog);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseString;
            try {
                JSONObject cred = new JSONObject();
                String userToken = "StubToken";//TODO Replace with real token
                try {
                    cred.put(NetworkUtilities.TOKEN, userToken);
                    cred.put("event_id", currentEvent.GetId());
                } catch (JSONException|NullPointerException e) {
                    Log.i(TAG, e.toString());
                }
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_members_urls/");

            } catch (Exception ex) {
                Log.e(TAG, "getMembersUrls.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }
            // Convert string received from server to JSON array
            JSONArray eventsFromServerJSON = null;
            JSONObject responseJSON = null;
            try {
                responseJSON = new JSONObject(responseString);
                eventsFromServerJSON = responseJSON.getJSONArray(Constants.RESPONSE_MESSAGE);
                membersImagesUrls = eventsFromServerJSON;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            currenctSize = membersImagesUrls.length();
            viewCurMembers.setText(String.valueOf(currenctSize));
            if (currenctSize >= minSize) {
                viewStatus.setVisibility(View.VISIBLE);
                statusImg.setVisibility(View.VISIBLE);
            }
            if (currenctSize == maxSize) {
                if(! User.eventsObjectContainEvent(currentUser.getEvents(), currentEvent.GetId()))
                {
                    playButton.setVisibility(View.INVISIBLE);
                    viewPlay.setVisibility(View.INVISIBLE);
                    fullView.setVisibility(View.VISIBLE);
                }
                else if(playButton.isChecked())
                {
                    playButton.setClickable(true);
                }
                else
                {
                    playButton.setClickable(false);
                    toastGeneral(getApplicationContext(), "The event is full", Toast.LENGTH_SHORT);
                }
            }
            for (int i = 0; i < membersImagesUrls.length(); i++) {
                try {
                    photoURL = membersImagesUrls.getString(i);
                    imageBitmap = new DownloadImageBitmapTask().execute(photoURL).get();
                } catch (InterruptedException|ExecutionException|JSONException e) {
                    e.printStackTrace();
                }
                Bitmap newMember = getRoundedShape(imageBitmap);
                ImageView member = new ImageView(thisContext);
                member.setImageBitmap(newMember);
                member.getAdjustViewBounds();
                urlList.add(photoURL);
                member.setId(i);
                member.setPadding(10, 1, 10, 1);
                membersList.addView(member);

            }
            // adding Listener for evrey memnber
            for(int i=0;i<membersList.getChildCount();i++)
            {
                final String currentUrl = urlList.get(i);
                membersList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     // new changes
                                                                     new EventPhotoUserListener(currentUrl).execute();
                                                                 }
                                                             }

                );
            }

            Log.d(TAG, "getMembersUrls.successful" + membersImagesUrls.toString());

            try
            {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                // do your Display and data setting operation here
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    public class EventPhotoUserListener extends AsyncTask<String, String, String> {
        int i;
        String photoUrl;

        EventPhotoUserListener(String photoUrl) {
            this.photoUrl = photoUrl;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
           /*server call   */
            String userProfileResponseStr = "";
            try {
                JSONObject cred = new JSONObject();
                try {
                    cred.put(NetworkUtilities.TOKEN, "StubToken");
                    cred.put(NetworkUtilities.PHOTO_URL, photoUrl);
                    userProfileResponseStr = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_user_by_photo/");

                } catch (JSONException|UnsupportedEncodingException e) {
                    Log.i(TAG, e.toString());
                }
            } catch (Exception ex) {
                Log.e(TAG, "getUserEvents.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                userProfileResponseStr = "";
            }
            // Convert string received from server to JSON
            JSONObject userInfoFroServer = null;
            JSONObject responseJSON = null;
            try {

                responseJSON = new JSONObject(userProfileResponseStr);
                userInfoFroServer = responseJSON.getJSONObject(Constants.RESPONSE_MESSAGE);

                Intent iv = new Intent(EventInfo.this,
                        com.inc.playground.playgroundApp.upLeft3StripesButton.
                                MyProfile.class);

                JSONArray eventEntries = userInfoFroServer.getJSONArray(Constants.EVENT_ENTRIES);

                ArrayList<EventsObject> memeberEvents = NetworkUtilities.eventUserListToArrayList(eventEntries, globalVariables.GetCurrentLocation(), Constants.EVENT_ENTRIES);

                ArrayList<ArrayList<EventsObject>> allEvents = Splash.eventsTypesfromJson(userProfileResponseStr, InitGlobalVariables.globalVariables.GetCurrentLocation());
                ArrayList<EventsObject> events  = allEvents.get(0);
                ArrayList<EventsObject> events_wait4approval = allEvents.get(1);
                ArrayList<EventsObject> events_decline = allEvents.get(2);
                iv.putExtra("events", events);
                iv.putExtra("events_wait4approval", events_wait4approval);
                iv.putExtra("events_decline", events_decline);

                iv.putExtra("name", userInfoFroServer.getString("fullname"));
                iv.putExtra("createdNumOfEvents",userInfoFroServer.getString("created_count"));
                iv.putExtra("photoUrl", photoUrl);
                startActivity(iv);
//                finish();


            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }
        //Log.d("EVent info", "getMembersUrls.sucessful" + membersImagesUrls);
    }

    /**
     *
     * remove from url_list user url
     * @return ImageView object on member list
     */
    public ImageView findMemberPhoto() {
        String photoUrl;
        for(int i=0; i< membersList.getChildCount() ;i++){
            photoUrl = urlList.get(i);
            if(photoUrl.equals(currentUser.getPhotoUrl())){
                urlList.remove(i);
                return (ImageView) membersList.getChildAt(i);
            }
        }
        assert(3<1) ; // we should not get here !
        return null;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 150;
        int targetHeight = 150;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    private void initProgressDialog(ProgressDialog dialog)
    {
        String message = "Loading ...";
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f),0,spanMessage.length(),0);
        spanMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#104e8b")), 0, spanMessage.length(), 0);
        dialog.setTitle("Please wait");
        dialog.setMessage(spanMessage);
        dialog.setIcon(R.drawable.pg_loading);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(800, 420);
    }
    /**
     * input: e.g : "2016-07-19"
     * @param date
     * @return int[year, month ,day]
     */
    public static int[] parseDate(String date){
        int firstUnderScore  = date.indexOf("-");
        int secondUnderScore = date.indexOf("-",firstUnderScore+1 );
        int dateArr[]= new int[3];
        dateArr[0] = Integer.parseInt(date.substring(0 ,firstUnderScore ));
        dateArr[1] = Integer.parseInt(date.substring(firstUnderScore+1 ,secondUnderScore ))-1;//month act weird - need decrement
        dateArr[2] = Integer.parseInt(date.substring(secondUnderScore+1 ,date.length()));
        return dateArr;
    }

    /**
     * input: e.g : "19:55"
     * @param hour
     * @return int[hour(24), min]
     */
    public static int[] parseHour(String hour){
        int colonIndex  = hour.indexOf(":");
        int hourArr[]= new int[2];
        hourArr[0] = Integer.parseInt(hour.substring(0, colonIndex));
        hourArr[1] = Integer.parseInt(hour.substring(colonIndex+1 ,hour.length()));
        return hourArr;
    }


    public static void toastGeneral(Context c, String text , int toastLength){
        Toast toast = Toast.makeText(c,text,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onBackPressed()
    {
        if(parentActivity.equals("NotificationList"))
        {
            Intent iv = new Intent(this,NotificationsList.class);
            startActivity(iv);
            finish();
        }
        else
        {
            Intent iv = new Intent(this,MainActivity.class);
            startActivity(iv);
            finish();
        }

    }
}



