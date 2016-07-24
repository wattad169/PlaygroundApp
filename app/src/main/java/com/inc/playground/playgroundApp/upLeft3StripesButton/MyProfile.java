package com.inc.playground.playgroundApp.upLeft3StripesButton;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inc.playground.playgroundApp.EventsObject;
import com.inc.playground.playgroundApp.FragmentList;
import com.inc.playground.playgroundApp.GlobalVariables;
import com.inc.playground.playgroundApp.MainActivity;
import com.inc.playground.playgroundApp.R;
import com.inc.playground.playgroundApp.utils.DownloadImageBitmapTask;
import com.inc.playground.playgroundApp.utils.User;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by idanaroz on 03-Jun-16.
 */
public class MyProfile extends FragmentActivity {
/* must send to this class data e.g :       iv.putExtra("name",currentUser.getName());*/
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
    ArrayList<EventsObject> userEvents;
    ProgressDialog progressBar;

    MainActivity.AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    FragmentPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    TextView userPlayed;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalVariables = ((GlobalVariables) this.getApplication());//critical !
        bundle = getIntent().getExtras();
        setPlayGroundActionBar();
        currentUser = globalVariables.GetCurrentUser();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(MyProfile.this, "Playground", "Loading...");


        //yarden, lina, i am tring to put loading spinner when we move to this screen (spinner)
//        spinner = (ProgressBar)findViewById(R.id.progressBar);
        // spinner.setVisibility(View.VISIBLE);
        setContentView(R.layout.profile);
        currentUser = globalVariables.GetCurrentUser();
        if (currentUser != null) { // the user is login
            userEvents = (ArrayList<EventsObject>) bundle.getSerializable("events") ;

        }

        //set name
        TextView user_profile_name = (TextView) findViewById(R.id.user_profile_name);
        user_profile_name.setText(bundle.getString("name").replace("%20","  "));//name should be as facebook?
        //set created_count
        TextView createdCount_textView = (TextView) findViewById(R.id.countCreateTxt);
        createdCount_textView.setText(createdCount_textView.getText()+
                (bundle.getString("createdNumOfEvents")));
        userPlayed = (TextView) findViewById(R.id.countPlayedTxt);
        Intent intent = getIntent();
        ArrayList<EventsObject> eventsUser = (ArrayList<EventsObject>) intent.getSerializableExtra("events");
        userPlayed.setText(String.valueOf(eventsUser.size()));
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
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mCustomPagerAdapter = new FragmentPagerAdapter(userEvents);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
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

        prefs = this.getSharedPreferences("Login", this.MODE_PRIVATE);
        userLoginId = prefs.getString("userid", null);
        SharedPreferences prefs = this.getSharedPreferences("Login", 0);

        return rootView;

    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent next = new Intent(getApplication(),MainActivity.class);
        startActivity(next);
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

    public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter
    {
        final int PAGE_COUNT = 1 ;
        private Set<EventsObject> userEventsSet;
        private final String[] PAGE_TITLES =
                {
                        "User Events",
                };

        public FragmentPagerAdapter(ArrayList<EventsObject> currentEvents)
        {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            try {
                switch (position) {
                    case 0:
                        FragmentList fragment1 = new FragmentList();
                        Bundle args = new Bundle();
                        args.putInt("len", userEvents.size());
                        args.putSerializable("events", userEvents);
                        args.putBoolean("isMain", false);
                        Intent intent = getIntent();
                        args.putSerializable("events", (ArrayList<EventsObject>) intent.getSerializableExtra("events"));
                        args.putSerializable("events_wait4approval", (ArrayList<EventsObject>) intent.getSerializableExtra("events_wait4approval"));
                        args.putSerializable("events_decline", (ArrayList<EventsObject>) intent.getSerializableExtra("events_decline"));
                        fragment1.setArguments(args);
                        return fragment1;
                    default:
                        return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}