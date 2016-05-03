package com.inc.playground.playground;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;


public class Home extends FragmentActivity implements ActionBar.TabListener {
    Button menuButtonOpen,menuButtonClose;
    DrawerLayout menuLayout;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Top Rated", "Games", "Movies" };
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mPlanetTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//
//        menuButtonOpen = (Button) findViewById(R.id.settings_menu);
//        menuButtonClose = (Button) findViewById(R.id.menu_close);
//        menuButtonClose.setVisibility(View.INVISIBLE);
//        menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
//        menuLayout.setVisibility(View.VISIBLE);
//        menuLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.menu_layout);
//        mDrawerList = (ListView)findViewById(R.id.left_drawer);


//        MenuItemAdapter menuAdapter = new MenuItemAdapter(this);
//
//        menuAdapter.add(new MenuItem("Hello"));
//        menuAdapter.add(new MenuItem("World"));
//        menuAdapter.add(new MenuItem("Parsnips"));
//        menuAdapter.add(new MenuItem("Turnips"));
//
//        mDrawerList.setAdapter(menuAdapter);
//
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.addstore,
                R.string.app_name,
                R.string.appbar_scrolling_view_behavior)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        if (savedInstanceState ==  null)
//        {
//            selectItem(0);
//        }
//        // drawer open
//        menuButtonOpen.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Animation slide = AnimationUtils.loadAnimation(Home.this, R.anim.lefttoright);
//                menuLayout.startAnimation(slide);
////                menuButtonClose.setVisibility(View.VISIBLE);
//                menuButtonOpen.setVisibility(View.INVISIBLE);
//                menuLayout.setVisibility(View.VISIBLE);
//                menuLayout.openDrawer(Gravity.LEFT);
//            }
//
//        });
//
//        // close drawer
//        menuButtonClose.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Animation slide = AnimationUtils.loadAnimation(Home.this, R.anim.righttoleft);
//                menuLayout.startAnimation(slide);
//                // mDrawerLayout.setVisibility(View.VISIBLE);
//                menuButtonClose.setVisibility(View.INVISIBLE);
//                menuButtonOpen.setVisibility(View.VISIBLE);
//                menuLayout.closeDrawer(Gravity.LEFT);
//                menuLayout.setVisibility(View.INVISIBLE);
//            }
//
//        });
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {

            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        Animation slide = AnimationUtils.loadAnimation(Home.this, R.anim.lefttoright);
//        menuLayout.startAnimation(slide);
////                menuButtonClose.setVisibility(View.VISIBLE);
//        menuButtonOpen.setVisibility(View.INVISIBLE);
//        menuLayout.setVisibility(View.VISIBLE);
//        menuLayout.openDrawer(Gravity.LEFT);
//    }
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


//
}
