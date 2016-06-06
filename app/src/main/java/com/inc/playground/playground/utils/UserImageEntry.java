package com.inc.playground.playground.utils;

import android.graphics.Bitmap;

/**
 * Created by mostafawattad on 04/06/2016.
 */
public class UserImageEntry {
    public String fullname;
    public Bitmap image;
    public String id;

    public UserImageEntry(String fullname, Bitmap image, String userid) {
        this.fullname = fullname;
        this.image = image;
        this.id = userid;
    }


}
