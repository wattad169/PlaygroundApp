package com.inc.playground.playground;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.UserImageEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilterDialogFragment extends DialogFragment {
    GlobalVariables globalVariables;
    String eventId;
    String userId;
    String inviteeId;
    String inviteeName;
    View thisview;

    @SuppressLint("FilterDialogFragment")
    public FilterDialogFragment(String eventIdIn, String userIdIn) {
        this.eventId = eventIdIn;
        this.userId = userIdIn;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout outerLayout = (RelativeLayout) inflater.inflate(R.layout.simple_dropdown_item_2line,null);

        class MyListener implements DialogInterface.OnClickListener {
            MyListener() {
            }
            public void onClick(DialogInterface dialog, int which) {
                new SendInvite(getActivity(),inviteeId).execute();
                Toast.makeText(getActivity(),"Invitaion sent to " +inviteeName,Toast.LENGTH_SHORT).show();
            }
        }
        MyListener listener = new MyListener();

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
