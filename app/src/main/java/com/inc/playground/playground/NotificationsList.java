package com.inc.playground.playground;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by lina on 7/10/2016.
 */
public class NotificationsList extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.notification_list, container, false);



        return rootView;
    }

    public class NotificationsAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<EventsObject> data;
        private LayoutInflater inflater = null;

        public NotificationsAdapter(Activity activity, ArrayList<EventsObject> homeEvents) {
            this.activity = activity;
            this.data = homeEvents;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = inflater.inflate(R.layout.notification_item, null);
            }

            TextView notificationType = (TextView) view.findViewById(R.id.notification_typeTxt);
            TextView eventName =(TextView) view.findViewById(R.id.event_nameTxt);
            TextView notificationDescription = (TextView) view.findViewById(R.id.notification_descriptionTxt);

            Button positiveBtn = (Button) view.findViewById(R.id.positive_btn);
            Button negativeBtn = (Button) view.findViewById(R.id.negative_btn);

            String type = "";
            switch (type){
                case "invited":
                    break;
                case "join":
                    break;
                case "edited":
                    break;
                case "decline":
                    break;
                case "game_on":
                    break;
            }
            return view;
        }
    }
}

