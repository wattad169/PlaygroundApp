package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    public static final String TAG = "EventInfoActivity";
    //DahanLina

    EventsObject currentEvent;
    HashMap<String, String> currentLocation;
    TextView viewName, viewDateEvent, viewStartTime, viewEndTime, viewLocation, viewSize, viewStatus, viewEventDescription;
    private handleEventTask myEventsTask = null;
    public SharedPreferences prefs ;
    LinearLayout membersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(activity_event_info);
        setContentView(R.layout.activity_event_info);
        prefs = getSharedPreferences("Login", MODE_PRIVATE);

       
        setPlayGroundActionBar();
        Intent intent = getIntent();
        currentEvent = (EventsObject) intent.getSerializableExtra("eventObject");
        viewName = (TextView) findViewById(R.id.event_name);
        viewDateEvent = (TextView) findViewById(R.id.event_date);
        viewStartTime = (TextView) findViewById(R.id.event_start_time);
        viewEndTime = (TextView) findViewById(R.id.event_end_time);
        viewLocation = (TextView) findViewById(R.id.event_formatted_location);
        viewSize = (TextView) findViewById(R.id.event_max_size);
        viewEventDescription = (TextView) findViewById(R.id.event_description);


//        // TODO type image


        //TODO pictures of the members YD
        membersList = (LinearLayout)findViewById(R.id.members_list);

        for(int i=0;i<8;i++)
        {
            ImageView member = new ImageView(this);

            member.setImageResource(R.drawable.pg_time);
            member.setId(i);
            membersList.addView(member);
        }


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
        double latitude = 0,longitude = 0;
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
       // viewStartTime.setText(currentEvent.GetStartTime());
        //viewEndTime.setText(currentEvent.GetEndTime());
        viewLocation.setText(currentEvent.GetFormattedLocation());
        //viewSize.setText(currentEvent.GetSize());
        //
        viewEventDescription.setText(currentEvent.GetDescription());

        // TODO YD Add event name to toolbar title

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
    
    
    
    public void onPlayClick(View v){
        ToggleButton x = (ToggleButton)v;

        myEventsTask = new handleEventTask(currentEvent);
        myEventsTask.execute((Void) null);

        ImageView member = new ImageView(this);
        member.setImageResource(R.drawable.pg_time);

        member.setImageBitmap(globalVariables.GetUserPictureBitMap());
        membersList.addView(member);

        x.setClickable(false);
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
                if(responseString == null) {

                    Log.i("TESTID",currentEvent.GetId());
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
                        myEventsTask = null;
                        //TODO YD Switch toggle button text to "playing"
                    } else {
                        myEventsTask = null;
                        //TODO YD override toggle method -> not to switch text to "playing"
                    }
                }

            }
            else
            {
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
    public void setPlayGroundActionBar(){
        String userLoginId,userFullName,userEmail,userPhoto;
        Bitmap imageBitmap =null;

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

