package com.inc.playground.playground;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inc.playground.playground.utils.Constants;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Calendar fragment activity
                return new FragmentCalendar();
            case 1:
                // Map fragment activity
                return new FragmentMap();
            case 2:
                // List fragment activity
                FragmentList fragment1 = new FragmentList();
                Bundle args = new Bundle();
                args.putInt("len",Constants.maxEvents);
                args.putSerializable("events",null);
                fragment1.setArguments(args);
                return fragment1;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}