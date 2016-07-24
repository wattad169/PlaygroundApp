package com.inc.playground.playgroundApp.utils;

import android.graphics.Bitmap;

/**
 * Created by mostafawattad on 04/06/2016.
 */
public class UserImageEntry {
    public String fullname;
    public Bitmap image;
    public String id;
    public String url;

    public UserImageEntry(String fullname, Bitmap image, String userid, String url) {
        this.fullname = fullname;
        this.image = image;
        this.id = userid;
        this.url = url;
    }


}
