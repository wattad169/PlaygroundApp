/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inc.playground.playground;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.inc.playground.playground.upLeft3StripesButton.MyProfile;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;
import com.inc.playground.playground.utils.User;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private CharSequence mDrawerTitle;
    public static GlobalVariables globalVariables;
    private CharSequence mTitle;
    public static final String MY_PREFS_NAME = "Login";
    public static final String TAG = "MainActivity";
    EditText inputTextField;
    private static String APP_ID = "1609067259420394";
    //Integer CreatedNumOfEvents; //CreatedNumOfEvents of the accout

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */

    ViewPager mViewPager;
    Toolbar toolbar = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        globalVariables = ((GlobalVariables) getApplication());
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        setPlayGroundActionBar();
        //set actionBar color
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryColor)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.secondaryColor)));
        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setTabListener(this));
        }
        actionBar.getTabAt(0).setIcon(R.drawable.pg_list_view);
        actionBar.getTabAt(1).setIcon(R.drawable.pg_map_view);
        actionBar.getTabAt(2).setIcon(R.drawable.pg_calendar_view);
        //NavigationDrawer handling (e.g the list from leftside):
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.pg_menu,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);

        // all linear layout from slider menu

        /*Home button */
        LinearLayout ll_Home = (LinearLayout) findViewById(R.id.ll_home);
        ll_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // new changes
                Intent iv = new Intent(MainActivity.this,
                                MainActivity.class);
                startActivity(iv);
                finish();
            }
        });
        /*Login button */
        LinearLayout ll_Login = (LinearLayout) findViewById(R.id.ll_login);
        ll_Login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // new changes
                LinearLayout ll_Login = (LinearLayout) v;
                TextView loginTxt = (TextView) findViewById(R.id.login_txt);
                if(loginTxt.getText().equals("Login")) {
                    Intent iv = new Intent(MainActivity.this, Login.class);
                    startActivity(iv);
                    finish();
                }
                else if(loginTxt.getText().equals("Logout")) {
                    final Dialog alertDialog = new Dialog(MainActivity.this);
                    alertDialog.setContentView(R.layout.logout_dilaog);
                    alertDialog.setTitle("Logout");
                    alertDialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear();
                            editor.commit();
                            ImageView loginImg = (ImageView) findViewById(R.id.login_img);
                            TextView loginTxt = (TextView) findViewById(R.id.login_txt);
                            loginTxt.setText("Login");
                            loginImg.setImageResource(R.drawable.pg_action_lock_open);


                            globalVariables = ((GlobalVariables) getApplication());
                            globalVariables.SetCurrentUser(null);
                            globalVariables.SetUserPictureBitMap(null);
                            globalVariables.SetUsersList(null);
                            globalVariables.SetUsersImagesMap(null);

                            Intent iv = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(iv);
                            finish();
                        }
                    });

                    alertDialog.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent iv = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(iv);
                            finish();
                        }
                    });

                    alertDialog.show();  //<-- See This!
                        }

                    }
                });

        /*Setting button*/
        LinearLayout ll_Setting = (LinearLayout) findViewById(R.id.ll_settings);
        ll_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // new changes
                Intent iv = new Intent(MainActivity.this,
                        com.inc.playground.playground.upLeft3StripesButton.
                                SettingsActivity.class);
                startActivity(iv);
                finish();
            }
        });

        /*My profile button*/
        LinearLayout ll_my_profile = (LinearLayout) findViewById(R.id.ll_my_profile);
        ll_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new changes
                globalVariables = ((GlobalVariables) getApplication());
                User currentUser = globalVariables.GetCurrentUser();
                if(currentUser == null) {
                    Toast.makeText(MainActivity.this, "You are not logged in", Toast.LENGTH_LONG).show();
                }
                else {

                    Intent iv = new Intent(MainActivity.this,
                            com.inc.playground.playground.upLeft3StripesButton.
                                    MyProfile.class);

                    //for my profile
                    iv.putExtra("name", currentUser.getName());
                    iv.putExtra("createdNumOfEvents", currentUser.getCreatedNumOfEvents());
                    iv.putExtra("userEventsObjects", currentUser.getUserEventsObjects());//ArrayList<EventsObject>
                    iv.putExtra("photoUrl", currentUser.getPhotoUrl());
                    startActivity(iv);
                }

            }
        });

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
            globalVariables.GetCurrentUser().setPhotoUrl(userPhoto);
            actionBar.setCustomView(R.layout.actionbar_custom_view_home);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            ImageView logo_image = (ImageView) findViewById(R.id.img_profile_action_bar);
            logo_image.setBackgroundResource(R.drawable.pg_logo2);
            ImageView img_profile = (ImageView) findViewById(R.id.profile_image);
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

            TextView loginTxt = (TextView) findViewById(R.id.login_txt);
            ImageView loginImg = (ImageView) findViewById(R.id.login_img);
            globalVariables.SetUserPictureBitMap(imageBitmap); // Make the imageBitMap global to all activities to avoid downloading twice
            loginTxt.setText("Logout");
            loginImg.setImageResource(R.drawable.pg_action_lock_close);
            // Register to notifications
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new FragmentList();
                case 1:
                    return new FragmentMap();
                //idan 20.5
                case 2:
                    return new FragmentCalendar();
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "List View";
            }
            if(position==1){
                return "Map View";
            }
            if(position==2){
                return "calendar";
            }
            return "";
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Bitmap mIcon11;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "" + e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }


}
