package com.inc.playground.playground.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

final public class DownloadImageBitmapTask extends AsyncTask<String, Void, Bitmap> {
    // This asyncTask only downloads the image bitmap given a url.
    // The DownloadImageTask downloads and sets the image value also.

    Bitmap mIcon11;
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
        returnMethod(result);
    }
    private Bitmap returnMethod(Bitmap myValue) {
        //handle value
        return myValue;
    }

}

