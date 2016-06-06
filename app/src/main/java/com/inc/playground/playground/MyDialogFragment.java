package com.inc.playground.playground;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.playground.playground.R;
import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.DownloadImageBitmapTask;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.UserImageEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyDialogFragment extends DialogFragment {
    GlobalVariables globalVariables;
    String eventId;
    String userId;
    String inviteeId;
    String inviteeName;
    View thisview;
    public MyDialogFragment(String eventIdIn, String userIdIn) {
        this.eventId = eventIdIn;
        this.userId = userIdIn;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout outerLayout = (RelativeLayout) inflater.inflate(R.layout.simple_dropdown_item_2line,null);
        globalVariables = ((GlobalVariables) getActivity().getApplication());
        ArrayList<UserImageEntry> usersList = globalVariables.GetUsersList();

        AutoCompleteTextView autoComplete = ( AutoCompleteTextView) outerLayout.findViewById(R.id.autoCompleteSearch);
        InviteArrayAdapter adapter = new InviteArrayAdapter(getActivity().getBaseContext(), R.layout.autocomplete_layout,usersList);
        /** Defining an itemclick event listener for the autocompletetextview */
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                /** Each item in the adapter is a HashMap object.
                 *  So this statement creates the currently clicked hashmap object
                 * */
                UserImageEntry selectedUser = (UserImageEntry) arg0.getAdapter().getItem(position);
                inviteeName = selectedUser.fullname;
                /** Getting a reference to the TextView of the layout file activity_main to set Currency */


                inviteeId = selectedUser.id;


            }
        };

        class MyListener implements DialogInterface.OnClickListener {
            MyListener() {
            }
            public void onClick(DialogInterface dialog, int which) {
                new SendInvite(getActivity(),inviteeId).execute();
                Toast.makeText(getActivity(),"Invitaion sent to " +inviteeName,Toast.LENGTH_SHORT).show();
            }

        }
        MyListener listener = new MyListener();
        /** Setting the itemclick event listener */
        autoComplete.setOnItemClickListener(itemClickListener);
        /** Setting the adapter to the listView */
        autoComplete.setAdapter(adapter);
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.invite_48)
                .setTitle("Invite Friend")
                .setNegativeButton("Invite", listener)
                .setView(outerLayout)
                .create();



    }
    public class SendInvite extends AsyncTask<String, String, String> {
        public static final String TAG = "GetUsersImages";

        Context thisContext;
        String invitee;
        SendInvite(Context thisCon, String inviteeIn){
            thisContext = thisCon;
            invitee = inviteeIn;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseString;
            try {
                JSONObject cred = new JSONObject();
                String userToken = userId;
                try {
                    cred.put(NetworkUtilities.TOKEN, userToken);
                    cred.put("event_id", eventId);
                    cred.put(Constants.INVITEE, inviteeId);
                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                }
                responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/invite_user_to_event/");

            } catch (Exception ex) {
                Log.e(TAG, "getMembersUrls.doInBackground: failed to doPost");
                Log.i(TAG, ex.toString());
                responseString = "";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data

        }
    }



}
