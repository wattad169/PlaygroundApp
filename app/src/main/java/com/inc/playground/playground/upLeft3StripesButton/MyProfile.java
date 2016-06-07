package com.inc.playground.playground.upLeft3StripesButton;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.inc.playground.playground.AddEvent;
import com.inc.playground.playground.EventInfo;
import com.inc.playground.playground.EventsObject;
import com.inc.playground.playground.FragmentList;
import com.inc.playground.playground.GlobalVariables;
import com.inc.playground.playground.Login;
import com.inc.playground.playground.MainActivity;
import com.inc.playground.playground.R;
import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by idanaroz on 03-Jun-16.
 */
public class MyProfile extends Activity {
/* must send to this class data e.g :                 iv.putExtra("name",currentUser.getName());*/
//Todo change name to userprofile

    public static GlobalVariables globalVariables;
    /*my variables*/
    public ProgressBar spinner;     //for loading
    ListView listView;              //ListView listView;
    ArrayList<EventsObject> myEventsArrayList;
    Bundle bundle;

    public static final String TAG = "MyProfileActivity";

    /*Yarden and lina variables:*/
    ListView events_list; //ListView listView;
    ArrayList<EventsObject> homeEvents;

    public SharedPreferences prefs;
    String userLoginId;
    User currentUser;
    Set<String> userEvents;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        globalVariables = ((GlobalVariables) this.getApplication());//critical !
        bundle = getIntent().getExtras();
        setPlayGroundActionBar();
        currentUser = globalVariables.GetCurrentUser();


        //yarden, lina, i am tring to put loading spinner when we move to this screen (spinner)
        super.onCreate(savedInstanceState);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
       // spinner.setVisibility(View.VISIBLE);
        setContentView(R.layout.profile);

        //set name
        TextView user_profile_name = (TextView) findViewById(R.id.user_profile_name);
        user_profile_name.setText(bundle.getString("name").replace("%20","  "));//name should be as facebook?
        //set createdCount
        TextView createdCount_textView = (TextView) findViewById(R.id.countCreateTxt);
        createdCount_textView.setText(createdCount_textView.getText()+
                String.valueOf(bundle.getInt("createdNumOfEvents")));
        //set url
        ImageView user_profile_photo = (ImageView) findViewById(R.id.user_profile_photo);
        Bitmap imageBitmap=null;
        String picture_url= bundle.getString("photoUrl");
        if(imageBitmap==null){
            Log.i("myProfile", "downloading");
            try {
                imageBitmap = new DownloadImageBitmapTask().execute(picture_url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        else {
            Log.i("myProfile","Image found");
        }
        user_profile_photo.setImageBitmap(imageBitmap);
        /*Error setting url - need fix */
        //user_profile_photo.setImageBitmap(getBitmapFromURL("https://www.facebook.com/photo.php?fbid=10153556040874658&set=a.429615654657.232270.798789657&type=3&theater"));






    }
    @Override
    protected void onStart() {
        super.onStart();
        homeEvents = (ArrayList<EventsObject>) bundle.getSerializable("userEventsObjects");    //currentUser.getUserEventsObjects();
        //prefs = this.getSharedPreferences("Login", this.MODE_PRIVATE);
        //userLoginId = prefs.getString("userid", null);

        new getList().execute();
       // spinner.setVisibility(View.INVISIBLE);


        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            /*copied with change from list_fragment*/
        View rootView = inflater.inflate(R.layout.profile, container, false);

        currentUser = globalVariables.GetCurrentUser();
        homeEvents = (ArrayList<EventsObject>) bundle.getSerializable("userEventsObjects") ;
        prefs = this.getSharedPreferences("Login", this.MODE_PRIVATE);
        userLoginId = prefs.getString("userid", null);
        //final String MY_PREFS_NAME = "Login";
        SharedPreferences prefs = this.getSharedPreferences("Login", 0);
        if (currentUser != null) { // the user is login
            userEvents = currentUser.GetUserEvents();
        }
        new getList().execute();
        return rootView;

    }

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO ?
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            //            if (progressDialog.isShowing()) {
            //                progressDialog.dismiss();
            events_list = (ListView) findViewById(R.id.my_events_list);

//            SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)
//                    findViewById(R.id.swipe_refresh_layout);


            if (homeEvents != null) {
                if (homeEvents.size() == 0) {// If no events are found
                    //Toast.makeText(getApplicationContext(), "No Events Found", Toast.LENGTH_LONG).show();
                    events_list.setVisibility(View.INVISIBLE);
                } else {
                    // Display events
                    events_list.setVisibility(View.VISIBLE);
                    HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(MyProfile.this, homeEvents);//homeEvents= globalVariable.currentuserevents
                    homeEventsAdapter.notifyDataSetChanged();
                    events_list.setAdapter(homeEventsAdapter);

                    //swipe listener
                    //swipeRefreshLayout.setOnRefreshListener(FragmentList.this);
                        /*swipeRefreshLayout.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        swipeRefreshLayout.setRefreshing(true);

                                                        //fetchMovies();
                                                    }
                                                }
                        );*/


                    events_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(getApplicationContext(), EventInfo.class);
                            intent.putExtra("eventObject", homeEvents.get(position));
                            startActivity(intent);
                            finish();

                        }
                    });
                }
            }
        }


    }


    public class HomeEventsAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<EventsObject> data;
        private LayoutInflater inflater = null;

        public HomeEventsAdapter(Activity activity, ArrayList<EventsObject> homeEvents) {
            this.activity = activity;
            this.data = homeEvents;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;


            if (convertView == null) {
                view = inflater.inflate(R.layout.fragment_list_item, null);
                if (position % 2 == 0) {
                    view.setBackground(getResources().getDrawable(R.drawable.pg_cell_first));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.pg_cell_first_b));
                }
            }
            Typeface fontText = Typeface.createFromAsset(getAssets(), "sansation.ttf");
            Typeface fontText2 = Typeface.createFromAsset(getAssets(), "kimberly.ttf");
            Typeface fontText3 = Typeface.createFromAsset(getAssets(), "crayon.ttf");
            // update type icon according to event type
            String uri = "@drawable/pg_" + data.get(position).GetType() + "_icon";
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
            Drawable typeDrawable = getResources().getDrawable(imageResource);
            typeImg.setImageDrawable(typeDrawable);

            TextView eventName = (TextView) view.findViewById(R.id.event_name);
            eventName.setText(data.get(position).GetName());
//            eventName.setTypeface(fontText);

            TextView formattedLocation = (TextView) view.findViewById(R.id.formatted_loctaion_txt);
            formattedLocation.setText(data.get(position).GetFormattedLocation());
//            formattedLocation.setTypeface(fontText3);

            TextView eventDate = (TextView) view.findViewById(R.id.date_txt);
            eventDate.setText(data.get(position).GetDate());
//            eventDate.setTypeface(fontText3);

            TextView starTime = (TextView) view.findViewById(R.id.start_time_txt);
            starTime.setText(data.get(position).GetStartTime());
//            startTime.setTypeface(fontText3);

            TextView eventDistance = (TextView) view.findViewById(R.id.distance_txt);
            eventDistance.setText(data.get(position).GetDistance());
//            eventDistance.setTypeface(fontText3);

            TextView kmTxt = (TextView) view.findViewById(R.id.kmTxt);
//            kmTxt.setTypeface(fontText3);

            final TextView playTxt = (TextView) view.findViewById(R.id.play_txt);
            playTxt.setVisibility(View.INVISIBLE);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), EventInfo.class);
                    intent.putExtra("eventObject", data.get(position));
                    startActivity(intent);
                    finish();
                }
            });*/

            ToggleButton playButton = (ToggleButton) view.findViewById(R.id.join);
            playButton.setVisibility(View.INVISIBLE);
//            playButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ToggleButton curplayButton = (ToggleButton) v;
////                    if(curplayButton.isChecked())//leave event
////                    {
////                        LeaveEventTask = new LeaveHandleEventTask(data.get(position));
////                        LeaveEventTask.execute((Void) null);
////                    playTxt.setText("Play");
////                    playTxt.setTextColor(Color.parseColor("#1874cd"));
////                    }
////                    else
////                    {
//                    myEventsTask = new handleEventTask(data.get(position));
//                    myEventsTask.execute((Void) null);
//                    curplayButton.setClickable(false);
//                    playTxt.setText("Playing");
//                    playTxt.setTextColor(Color.parseColor("#00ced1"));
////                    }
//                }});

            return view;

        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        //Intent next = new Intent(getApplication(),MainActivity.class);
        //startActivity(next);
        finish();
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
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
    }

}















