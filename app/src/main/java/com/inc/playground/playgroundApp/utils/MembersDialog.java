package com.inc.playground.playgroundApp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.inc.playground.playgroundApp.FilterActivity;
import com.inc.playground.playgroundApp.GlobalVariables;
import com.inc.playground.playgroundApp.InviteArrayAdapter;
import com.inc.playground.playgroundApp.R;

import java.util.ArrayList;

/**
 * Created by lina on 7/9/2016.
 */
public class MembersDialog extends DialogFragment {
    GlobalVariables globalVariables;
    public String memberIDSelected;
    public  AutoCompleteTextView autoComplete;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout outerLayout = (RelativeLayout) inflater.inflate(R.layout.simple_dropdown_item_2line,null);
        globalVariables = ((GlobalVariables) getActivity().getApplication());
        ArrayList<UserImageEntry> usersList = globalVariables.GetUsersList();

        autoComplete = ( AutoCompleteTextView) outerLayout.findViewById(R.id.autoCompleteSearch);
        InviteArrayAdapter adapter = new InviteArrayAdapter(getActivity().getBaseContext(), R.layout.autocomplete_layout,usersList);
        /** Defining an itemclick event listener for the autocompletetextview */
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                /** Each item in the adapter is a HashMap object.
                 *  So this statement creates the currently clicked hashmap object
                 * */
                UserImageEntry selectedUser = (UserImageEntry) arg0.getAdapter().getItem(position);

                /** Getting a reference to the TextView of the layout file activity_main to set Currency */
                memberIDSelected = selectedUser.id;


            }
        };

        class MyListener implements DialogInterface.OnClickListener {
            MyListener() {
            }
            public void onClick(DialogInterface dialog, int which) {
                FilterActivity per = (FilterActivity) getActivity();
                per.membersTxt.setSummary(autoComplete.getText().toString());
                per.memberID = memberIDSelected;

            }


        }
        MyListener listener = new MyListener();
        /** Setting the itemclick event listener */
        autoComplete.setOnItemClickListener(itemClickListener);
        /** Setting the adapter to the listView */
        autoComplete.setAdapter(adapter);
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.invite_48)
                .setTitle("Find member")
                .setNegativeButton("Filter", listener)
                .setView(outerLayout)
                .create();
    }




}
