package com.inc.playground.playground.utils;

import android.content.Context;

import com.inc.playground.playground.Splash;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public List<Double> getMyLocation(Context activityFrom){
        GPSTracker gps;
        gps = new GPSTracker(activityFrom);
        double latitude = 0;
        double longitude = 0;
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        ArrayList<Double> outputList = new ArrayList<Double>();
        outputList.add(longitude);
        outputList.add(latitude);
        return outputList;
    }
}