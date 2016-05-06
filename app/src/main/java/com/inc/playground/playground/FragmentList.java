package com.inc.playground.playground;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.ads.InterstitialAd;
import com.inc.playground.playground.utils.AlertDialogManager;
import com.inc.playground.playground.utils.ConnectionDetector;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

/**
 * Created by mostafawattad on 30/04/2016.
 */
public class FragmentList extends Fragment {
    ListView list_fav;
    SQLiteDatabase db;
    HashMap<String,EventsObject> FileList;
    Cursor cur = null;
    ProgressDialog progressDialog;
    String store_id, name, address, distance;
    int start = 0;
    InterstitialAd mInterstitialAd;
    boolean interstitialCanceled;
    ConnectionDetector cd;
    AlertDialogManager alert = new AlertDialogManager();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // call the onPostExecute function in getList class from favourite activity from the other application
        new getList().execute();

        return rootView;
    }

}
