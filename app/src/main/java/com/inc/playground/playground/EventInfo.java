package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
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

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToArrayList;

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
    ImageButton shareButton;

    public static final String TAG = "EventInfoActivity";
    //DahanLina

    EventsObject currentEvent;
    HashMap<String, String> currentLocation;
    TextView viewName, viewDateEvent, viewStartTime, viewEndTime, viewLocation, viewSize, viewCurrentSize, viewEventDescription, viewPlay;
    ImageView typeImg;
    JSONArray membersImagesUrls;
    private handleEventTask JoinEventsTask = null;
    public LeaveHandleEventTask LeaveEventTask = null;
    public SharedPreferences prefs;
    LinearLayout membersList;
    User currentUser;
    ToggleButton playButton;
    Bitmap imageBitmap;
    Set<String> userEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
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
        viewLocation = (TextView) findViewById(R.id.event_formatted_location);
        viewSize = (TextView) findViewById(R.id.event_max_size);
        viewCurrentSize = (TextView) findViewById(R.id.current_size);
        viewEventDescription = (TextView) findViewById(R.id.event_description);
        typeImg = (ImageView) findViewById(R.id.type_img);
        playButton = (ToggleButton) findViewById(R.id.playing_btn);
        viewPlay = (TextView) findViewById(R.id.Play_txt);
        shareButton = (ImageButton) findViewById(R.id.share_btn);

        membersList = (LinearLayout) findViewById(R.id.members_list);
        new GetMembersImages(this).execute();
        gps = new GPSTracker(EventInfo.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitudecur = gps.getLatitude();
            longitudecur = gps.getLongitude();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();

        }


        setdata();

// btn_fvrt = (Button) findViewById(R.id.btn_fvrt);
//		btn_fvrt1 = (Button) findViewById(R.id.btn_fvrt1);


//		txt_header = (TextView) findViewById(R.id.txt_header);
//		txt_header.setTypeface(tfh);

    }


    private void setdata() {
        double latitude = 0, longitude = 0;
        try {
            HashMap<String, String> location = this.currentEvent.GetLocation();
            latitude = Double.parseDouble(location.get("lat"));
            longitude = Double.parseDouble(location.get("lon"));
        } catch (NumberFormatException e) {
            // TODO: handle exception
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

        // Set event view values
        viewName.setText(currentEvent.GetName());
        viewDateEvent.setText(currentEvent.GetDate());
        viewStartTime.setText(currentEvent.GetStartTime());
        viewEndTime.setText(currentEvent.GetEndTime());
        viewLocation.setText(currentEvent.GetFormattedLocation());
        viewSize.setText(currentEvent.GetSize());
        viewEventDescription.setText(currentEvent.GetDescription());


        if (currentUser != null) { // the user is login
            userEvents = currentUser.GetUserEvents();
            if (!userEvents.isEmpty()) {
                if (userEvents.contains(currentEvent.GetId())) {
                    playButton.setChecked(true);
                    playButton.setClickable(false);
                    viewPlay.setText("Playing");
                    viewPlay.setTextColor(Color.parseColor("#00ced1"));
                }
            }
        }

        String uri = "@drawable/pg_" + currentEvent.GetType();
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
                        }
//

                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
//		CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(Detailpage.this);
//		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
//		mViewPager.setAdapter(mCustomPagerAdapter);

//        Button btn_call = (Button) findViewById(R.id.btn_video);
//        btn_call.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO YD Implement
////				String uri = "tel:" + temp_Obj3.getPhoneno();
////				Intent i = new Intent(Intent.ACTION_DIAL);
////				i.setData(Uri.parse(uri));
////				startActivity(i);
//            }
//        });
//
//        Button btn_share = (Button) findViewById(R.id.btn_share);
//        btn_share.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO YD Implement
//				Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "download");
//				Intent share = new Intent(android.content.Intent.ACTION_SEND);
//				share.setType("text/plain");
//				share.setType("image/jpeg");
//				share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//				share.putExtra(Intent.EXTRA_SUBJECT, currentEvent.GetName());
//				share.putExtra(Intent.EXTRA_STREAM, imageUri);
//				share.putExtra(Intent.EXTRA_TEXT,
//						"https://play.google.com/store/apps/details?id=" + EventInfo.this.getPackageName() + "\n"
//								+ "Email: " + Html.fromHtml(temp_Obj3.getEmail()) + "\n" + "Address: " + Html.fromHtml(temp_Obj3.getAddress()));
//				startActivity(Intent.createChooser(share, "Share link!"));
//            }
//        });

//        Button btn_map = (Button) findViewById(R.id.btn_map);
//        btn_map.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // // TODO YD Implement
//				/*
//				 * Intent iv = new Intent(Detailpage.this, MainActivity.class);
//				 * iv.putExtra("lat", "" + temp_Obj3.getLat());
//				 * iv.putExtra("lng", "" + temp_Obj3.getLongi());
//				 * iv.putExtra("nm", "" + temp_Obj3.getName());
//				 * iv.putExtra("ad", "" + temp_Obj3.getAddress());
//				 * iv.putExtra("id", "" + temp_Obj3.getStore_id());
//				 * iv.putExtra("rate", "" + rating); // iv.putExtra("curlat", ""
//				 * + curlat); // iv.putExtra("curlng", "" + curlng);
//				 * startActivity(iv);
////				 */
////				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
////						Uri.parse("http://maps.google.com/maps?saddr=" + temp_Obj3.getLat() + "," + temp_Obj3.getLongi()
////								+ "&daddr=" + latitudecur + "," + longitudecur));
////				intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
////				startActivity(intent);
//            }
//        });
//
//        Button btn_web = (Button) findViewById(R.id.btn_web);
//        btn_web.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO YD Implement
//// 				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp_Obj3.getWebsite()));
////				startActivity(browserIntent);
//            }
//        });
//
//        Button btn_mail = (Button) findViewById(R.id.btn_book);
//        btn_mail.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO YD Implement
////				Intent iv = new Intent(Detailpage.this, Setting.class);
////				iv.putExtra("email", "" + temp_Obj3.getEmail());
////				iv.putExtra("namec", "" + temp_Obj3.getName());
////				iv.putExtra("address", "" + temp_Obj3.getAddress());
////				iv.putExtra("phone", "" + temp_Obj3.getPhoneno());
////
////				startActivity(iv);
//            }
//        });

//        Button btn_review = (Button) findViewById(R.id.btn_review);
//        btn_review.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                //  TODO YD Implement
////				Intent iv = new Intent(getApplicationContext(), Review.class);
////				iv.putExtra("id", "" + temp_Obj3.getStore_id());
////				startActivity(iv);
//            }
//        });

//        btn_fvrt.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
        // TODO YD Implement
//				btn_fvrt1.setVisibility(View.VISIBLE);
//				btn_fvrt.setVisibility(View.INVISIBLE);
//				myDbHelpel = new DBAdapter(Detailpage.this);
//				try {
//					myDbHelpel.createDataBase();
//				} catch (IOException io) {
//					throw new Error("Unable TO Create DataBase");
//				}
//				try {
//					myDbHelpel.openDataBase();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//				db = myDbHelpel.getWritableDatabase();
//				ContentValues values = new ContentValues();
//
//				values.put("store_id", temp_Obj3.getStore_id());
//				values.put("name", temp_Obj3.getName());
//				values.put("address", temp_Obj3.getAddress());
//
//				values.put("distance", homedistance);
//
//				db.insert("favourite", null, values);
//
//				myDbHelpel.close();
//            }
//        });

//        btn_fvrt1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
        // TODO YD Implement
        // btn_fvrt.setVisibility(View.VISIBLE);
//				btn_fvrt1.setVisibility(View.INVISIBLE);
//
//				DBAdapter myDbHelper = new DBAdapter(Detailpage.this);
//				myDbHelper = new DBAdapter(Detailpage.this);
//				try {
//					myDbHelper.createDataBase();
//				} catch (IOException e) {
//
//					e.printStackTrace();
//				}
//
//				try {
//
//					myDbHelper.openDataBase();
//
//				} catch (SQLException sqle) {
//					sqle.printStackTrace();
//				}
//
//				int i = 1;
//				db = myDbHelper.getWritableDatabase();
//
//				cur = db.rawQuery("Delete from favourite where store_id =" + temp_Obj3.getStore_id() + ";", null);
//				if (cur.getCount() != 0) {
//					if (cur.moveToFirst()) {
//						do {
//							Getsetfav obj = new Getsetfav();
//
//							store_id = cur.getString(cur.getColumnIndex("store_id"));
//							name1 = cur.getString(cur.getColumnIndex("name"));
//							address = cur.getString(cur.getColumnIndex("address"));
//
//							distance = cur.getString(cur.getColumnIndex("distance"));
//
//							obj.setName(name1);
//							obj.setAddress(address);
//							obj.setStore_id(store_id);
//							obj.setDistance(distance);
//
//							FileList.add(obj);
//
//						} while (cur.moveToNext());
//					}
//				}
//				cur.close();
//				db.close();
//				myDbHelper.close();
//            }
//        });
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
//        if(!x.isChecked()) {
//            LeaveEventTask = new LeaveHandleEventTask(currentEvent);
//            LeaveEventTask.execute((Void)null);
//        viewPlay.setText("Play");
//        viewPlay.setTextColor(Color.parseColor("#1874cd"));
//        }
//        else {//join
        JoinEventsTask = new handleEventTask(currentEvent);
        JoinEventsTask.execute((Void) null);

        ImageView member = new ImageView(this);
        member.setImageResource(R.drawable.pg_time);
        viewPlay.setText("Playing");
        viewPlay.setTextColor(Color.parseColor("#00ced1"));

        member.setImageBitmap(globalVariables.GetUserPictureBitMap());
        membersList.addView(member);
        x.setClickable(false);
        viewCurrentSize.setText(Integer.toString(membersImagesUrls.length() + 1));

        if (userEvents == null)
            userEvents = new HashSet<>();
        userEvents.add(currentEvent.GetId());
        currentUser.SetUserEvents(userEvents);
        globalVariables.SetCurrentUser(currentUser);
//        }
    }

    public class LeaveHandleEventTask extends AsyncTask<Void, Void, String> {

        //        private Context context;
        private EventsObject currentEvent;

        public LeaveHandleEventTask(EventsObject currentEvent) {
            this.currentEvent = currentEvent;

        }

        private String responseString;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject cred = new JSONObject();
            if (prefs.getString("userid", null) != null) {
                //If the user is logged in
                String userId = prefs.getString("userid", null);

                try {//Send request to server for joining event
                    cred.put(NetworkUtilities.TOKEN, userId);
                    cred.put("event_id", currentEvent.GetId());
                    responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/leave_event/");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myObject != null && responseStatus != null) {
                    if (responseStatus.equals(Constants.RESPONSE_OK.toString())) {
                        LeaveEventTask = null;
                        //TODO YD Switch toggle button text to "playing"
                    } else {
                        LeaveEventTask = null;
                        //TODO YD override toggle method -> not to switch text to "playing"
                    }
                }

            } else {
                // If user is not logged -> in send to login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            return null;

        }

        @Override
        protected void onPostExecute(final String responseString) {


        }


    }


    public class handleEventTask extends AsyncTask<Void, Void, String> {

        //        private Context context;
        private EventsObject currentEvent;

        public handleEventTask(EventsObject currentEvent) {
            this.currentEvent = currentEvent;

        }

        private String responseString;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject cred = new JSONObject();
            if (prefs.getString("userid", null) != null) {
                //If the user is logged in
                String userId = prefs.getString("userid", null);

                try {//Send request to server for joining event
                    cred.put(NetworkUtilities.TOKEN, userId);
                    cred.put("event_id", currentEvent.GetId());
                    responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/join_event/");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myObject != null && responseStatus != null) {
                    if (responseStatus.equals(Constants.RESPONSE_OK.toString())) {
                        JoinEventsTask = null;
                        //TODO YD Switch toggle button text to "playing"
                    } else {
                        JoinEventsTask = null;
                        //TODO YD override toggle method -> not to switch text to "playing"
                    }
                }

            } else {
                // If user is not logged -> in send to login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            return null;

        }

        @Override
        protected void onPostExecute(final String responseString) {


        }


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent next = new Intent(getApplication(), MainActivity.class);
        startActivity(next);
        finish();
    }

    public class GetMembersImages extends AsyncTask<String, String, String> {
        int i;
        String photoURL;

        Context thisContext;

        GetMembersImages(Context thisCon) {
            thisContext = thisCon;

        }

        @Override
        protected void onPreExecute() {
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

            for (i = 0; i < membersImagesUrls.length(); i++) {
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
                ImageView member = new ImageView(thisContext);
                member.setImageBitmap(imageBitmap);
                //member.setImageResource(R.drawable.pg_time);
                member.getAdjustViewBounds();
                member.setId(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
                member.setLayoutParams(layoutParams);


                //added for listener
                member.setOnClickListener(new View.OnClickListener(){
                      @Override
                      public void onClick(View v) {
                          // new changes
                          new EventPhotoUserListener(photoURL).execute();
                      }
                  }

                );

                membersList.addView(member);

            }

            Log.d(TAG, "getMembersUrls.successful" + membersImagesUrls.toString());
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
                ArrayList<EventsObject> memeberEvents = NetworkUtilities.eventListToArrayList(eventEntries , globalVariables.GetCurrentLocation());

                iv.putExtra("name", userInfoFroServer.getString("fullname"));
                iv.putExtra("createdNumOfEvents",0);//Todo:replace 0 with Integer.parseInt( userInfoFroServer.getString("createdCount")))  ;//MIGHT BE STRING !!!
                iv.putExtra("photoUrl", photoUrl);
                iv.putExtra("userEventsObjects", memeberEvents);//
                startActivity(iv);

                finish();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


            //Log.d("EVent info", "getMembersUrls.successful" + membersImagesUrls);
        }
    }


