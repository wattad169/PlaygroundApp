package com.inc.playground.playground;

import android.app.ActionBar;
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
import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.CustomMarker;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;
import com.inc.playground.playground.utils.EventUserObject;
import com.inc.playground.playground.utils.GPSTracker;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
    TextView viewName, viewDateEvent, viewStartTime, viewEndTime, viewCurMembers, viewLocation, viewMinSize, viewCurrentSize, viewMaxSize, viewEventDescription, viewPlay, viewStatus;
    ImageView typeImg, statusImg;
    JSONArray membersImagesUrls;
    private HandleEventTask handleEventTask = null;
    //private HandleEventTask LeaveEventTask = null;
    public SharedPreferences prefs;
    LinearLayout membersList;
    User currentUser;
    ToggleButton playButton;
    Bitmap imageBitmap;
    Set<String> userEvents;
    ScrollView mainLayout;
    public ArrayList<String> urlList = new ArrayList<>(); //saves the url of members by order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        prefs = getSharedPreferences("Login", MODE_PRIVATE);

        setPlayGroundActionBar();
        Intent intent = getIntent();
        currentEvent = (EventsObject) intent.getSerializableExtra("eventObject");
        currentUser = globalVariables.GetCurrentUser();
        viewName = (TextView) findViewById(R.id.event_name);
        viewDateEvent = (TextView) findViewById(R.id.event_date);
        viewStartTime = (TextView) findViewById(R.id.event_start_time);
        viewEndTime = (TextView) findViewById(R.id.event_end_time);
        viewCurMembers = (TextView) findViewById(R.id.cur_membersTxt);
        viewLocation = (TextView) findViewById(R.id.event_formatted_location);
        viewMinSize = (TextView) findViewById(R.id.event_min_size);
        viewCurrentSize = (TextView) findViewById(R.id.event_current_size);
        viewMaxSize = (TextView) findViewById(R.id.event_max_size);
        viewEventDescription = (TextView) findViewById(R.id.event_description);
        typeImg = (ImageView) findViewById(R.id.type_img);
        playButton = (ToggleButton) findViewById(R.id.playing_btn);
        viewPlay = (TextView) findViewById(R.id.Play_txt);

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
        setdata();

    }

    private void setdata() {
        double latitude = 0, longitude = 0;
        try {
            HashMap<String, String> location = this.currentEvent.GetLocation();
            latitude = Double.parseDouble(location.get("lat"));
            longitude = Double.parseDouble(location.get("lon"));
        } catch (NumberFormatException e) {
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

        Typeface fontText = Typeface.createFromAsset(getAssets(), "kimberly.ttf");
        viewName.setTypeface(fontText);

        // Set event view values
        viewName.setText(currentEvent.GetName());
        viewDateEvent.setText(currentEvent.GetDate());
        viewStartTime.setText(currentEvent.GetStartTime());
        viewEndTime.setText(currentEvent.GetEndTime());
        viewLocation.setText(currentEvent.GetFormattedLocation());
        viewMinSize.setText(currentEvent.GetSize());
        viewMaxSize.setText(currentEvent.getMaxSize());
        viewEventDescription.setText(currentEvent.GetDescription());
        viewStatus.setVisibility(View.INVISIBLE);
        statusImg.setVisibility(View.INVISIBLE);
        if (currentUser != null) { // the user is login
            userEvents = currentUser.GetUserEvents();
            if (!userEvents.isEmpty()) {
                if (userEvents.contains(currentEvent.GetId())) {//play in the event
                    if(currentUser.GetUserId().equals(currentEvent.GetCreatorId())){//creator
                        playButton.setChecked(true);//Todo change the tringle to other picture for manager
                        playButton.setTextColor(Color.parseColor("#000000"));
                        viewPlay.setText("Hosting");
                        viewPlay.setTextColor(Color.parseColor("#000000"));
                    }
                    else { //regular member
                        playButton.setChecked(true);
                        //playButton.setClickable(false); -avoid it: make it impossbile to click on the button
                        viewPlay.setText("Playing");
                        viewPlay.setTextColor(Color.parseColor("#104E8B"));
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
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                android.app.Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                                String inputText = "asd";
                                DialogFragment newFragment = new MyDialogFragment(currentEvent.GetId(), currentUser.GetUserId());
                                newFragment.show(ft, "dialog");
                                break;
                            case R.id.share_calendar:
                                Calendar cal = Calendar.getInstance();
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra("beginTime", cal.getTimeInMillis());
                                intent.putExtra("allDay", true);
                                intent.putExtra("rrule", "FREQ=YEARLY");
                                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                                intent.putExtra("title", "A Test Event from android app");
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
                                    intent.putExtra("eventObject",currentEvent);
                                    startActivity(intent);
                                    break;
                                case R.id.cancel_event_id:

                                    break;
                                case R.id.join_requests:

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
//
//	class CustomPagerAdapter extends PagerAdapter {
//
//		Context mContext;
//		LayoutInflater mLayoutInflater;
//
//		public CustomPagerAdapter(Context context) {
//			mContext = context;
//			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		@Override
//		public int getCount() {
//			return separated.length;
//		}
//
//		@Override
//		public boolean isViewFromObject(View view, Object object) {
//			return view == ((RelativeLayout) object);
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			View itemView = mLayoutInflater.inflate(R.layout.image_pager, container, false);
//
//			imageView = (ImageView) itemView.findViewById(R.id.image_page_fliper);
//			imageView.setImageResource(R.drawable.detail_page_loadimg);
//			Log.d("position", "" + separated[position].replace("[", "").replace("]", "").replace("\"", ""));
//			String imageurl = getString(R.string.link) + "uploads/store/full/"
//					+ separated[position].replace("[", "").replace("]", "").replace("\"", "");
//			// String imageurl = getResources().getString(R.string.liveurl) +
//			// "uploads/" + separated[position].replace("[", "").replace("]",
//			// "");
//			Log.d("imageurl", imageurl);
//			ImageLoader imgLoader = new ImageLoader(Detailpage.this);
//			imgLoader.DisplayImage(imageurl.replace(" ", "%20"), imageView);
//			// new DownloadImageTask(imageView).execute(imageurl);
//			container.addView(itemView);
//
//			return itemView;
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView((RelativeLayout) object);
//		}
//	}
//
//	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//		ImageView bmImage;
//		Bitmap mIcon11;
//
//		public DownloadImageTask(ImageView bmImage) {
//			this.bmImage = bmImage;
//		}
//
//		@Override
//		protected Bitmap doInBackground(String... urls) {
//			String urldisplay = urls[0];
//
//			try {
//				InputStream in = new java.net.URL(urldisplay).openStream();
//				mIcon11 = BitmapFactory.decodeStream(in);
//			} catch (Exception e) {
//				Log.e("Error", "" + e.getMessage());
//				e.printStackTrace();
//			}
//			return mIcon11;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			bmImage.setImageBitmap(result);
//		}
//	}

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
                String text = "Event was canceled";
                Toast toast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG);
                toast.show();
                eventTask = "cancel_event";
                viewPlay.setText("cancel");
                viewPlay.setTextColor(Color.parseColor("#D0D0D0"));
                finish();
            }
            else{ //leave_event
                eventTask = "leave_event";
                x.setChecked(false);
                viewPlay.setText("Play");
                viewPlay.setTextColor(Color.parseColor("#D0D0D0"));
                //remove member picture
                membersList.removeView(findMemberPhoto());
                userEvents.remove(currentEvent.GetId());//remove current event from userEvents
                viewCurrentSize.setText(Integer.toString(membersList.getChildCount()));
            }
        }
        else {//join event
            eventTask = "join_event";
            x.setChecked(true);
            viewPlay.setText("Playing");
            viewPlay.setTextColor(Color.parseColor("#104E8B"));
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
            viewCurrentSize.setText(Integer.toString(membersImagesUrls.length() + 1));

            //set status ("game on!" or not)
            if (Integer.valueOf((String) viewCurrentSize.getText()) == Integer.valueOf((String) viewMinSize.getText())) {
                viewStatus.setVisibility(View.VISIBLE);
                statusImg.setVisibility(View.VISIBLE);
                //            mainLayout.setBackgroundColor(Color.parseColor("#98fb98"));
            }
            if (Integer.valueOf((String) viewCurrentSize.getText()) == Integer.valueOf((String) viewMaxSize.getText())) {
                //TODO : set not clickable for join to event -> only for leave

            }
            if (userEvents == null) userEvents = new HashSet<>();
            userEvents.add(currentEvent.GetId());
        }

        currentUser.SetUserEvents(userEvents);
        globalVariables.SetCurrentUser(currentUser);
        /*Server side update */
        handleEventTask = new HandleEventTask(currentEvent, eventTask);
        handleEventTask.execute((Void) null);
}

    public class HandleEventTask extends AsyncTask<Void, Void, String> {
        /*handle 3 requests: 1.join_event 2. leave_event 3. cancel_events (the whole event) */

        private String responseString;
        String eventTask;
        private EventsObject currentEvent;
        public HandleEventTask(EventsObject currentEvent,String eventTask) {
            this.currentEvent = currentEvent;
            this.eventTask = eventTask;
            assert(eventTask.equals("join_event") || eventTask.equals("leave_event")||eventTask.equals("cancel_event") );
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
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                catch(NullPointerException nPiotExc){
                    nPiotExc.printStackTrace();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch(NullPointerException nPiotExc){
                    nPiotExc.printStackTrace();
                }

                if (myObject != null && responseStatus != null) {
                    if (responseStatus.equals(Constants.RESPONSE_OK.toString())) {
                        //Todo-Idan :cover the new eventTask that we have now: leave and cancel
                        handleEventTask = null;
                        //TODO YD Switch toggle button text to "playing"
                    } else {
                        handleEventTask= null;
                        //TODO YD override toggle method -> not to switch text to "playing"
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
                } catch (ExecutionException e) {
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
                } catch (JSONException e) {
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
            viewCurrentSize.setText(Integer.toString(membersImagesUrls.length()));
            viewCurMembers.setText(Integer.toString(membersImagesUrls.length()));
            if (Integer.valueOf((String) viewCurrentSize.getText()) == Integer.valueOf((String) viewMinSize.getText())) {
                viewStatus.setVisibility(View.VISIBLE);
                statusImg.setVisibility(View.VISIBLE);
//                mainLayout.setBackgroundColor(Color.parseColor("#98fb98"));
            }
            if (Integer.valueOf((String) viewCurrentSize.getText()) == Integer.valueOf((String) viewMaxSize.getText())) {
                //TODO : set not clickable for join to event -> only for leave
            }
            for (int i = 0; i < membersImagesUrls.length(); i++) {
                try {
                    photoURL = membersImagesUrls.getString(i);
                    imageBitmap = new DownloadImageBitmapTask().execute(photoURL).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bitmap newMember = getRoundedShape(imageBitmap);
                ImageView member = new ImageView(thisContext);
                member.setImageBitmap(newMember);
                //member.setImageResource(R.drawable.pg_time);
                member.getAdjustViewBounds();
                urlList.add(photoURL);
                member.setId(i);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
//                member.setLayoutParams(layoutParams);
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
            catch(Exception e)
            {

            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            progressBar.setProgress(values[0]);
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

                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                } catch (UnsupportedEncodingException e) {
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
                        com.inc.playground.playground.upLeft3StripesButton.
                                MyProfile.class);

                JSONArray eventEntries = userInfoFroServer.getJSONArray("eventsEntries");
                ArrayList<EventUserObject> memeberEvents = NetworkUtilities.eventUserListToArrayList(eventEntries, globalVariables.GetCurrentLocation(), "eventsEntries");

                iv.putExtra("name", userInfoFroServer.getString("fullname"));
                iv.putExtra("createdNumOfEvents",userInfoFroServer.getString("createdCount"));
                iv.putExtra("photoUrl", photoUrl);
                iv.putExtra("userEventsObjects", memeberEvents);//for profile
                startActivity(iv);
//                finish();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        //Log.d("EVent info", "getMembersUrls.successful" + membersImagesUrls);
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
}


