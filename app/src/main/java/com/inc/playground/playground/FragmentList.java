package com.inc.playground.playground;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.melnykov.fab.ScrollDirectionListener;
import com.google.android.gms.ads.InterstitialAd;
import com.inc.playground.playground.utils.AlertDialogManager;
import com.inc.playground.playground.utils.ConnectionDetector;
import com.melnykov.fab.ScrollDirectionListener;

import java.util.ArrayList;

/**
 * Created by mostafawattad on 30/04/2016.
 */
public class FragmentList extends Fragment{
    ListView events_list;
    ArrayList<EventsObject> homeEvents;
    ProgressDialog progressDialog;
    GlobalVariables globalVariables;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        this.globalVariables = ((GlobalVariables) getActivity().getApplication());
        homeEvents = this.globalVariables.GetHomeEvents();
        new getList().execute();

        return rootView;
    }

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO ?
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        protected void onPostExecute(Integer result) {

//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
                events_list = (ListView) getActivity().findViewById(R.id.list_detail);
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                fab.attachToListView(events_list, new ScrollDirectionListener() {
                    @Override
                    public void onScrollDown() {
                        Log.d("ListViewFragment", "onScrollDown()");
                    }

                    @Override
                    public void onScrollUp() {
                        Log.d("ListViewFragment", "onScrollUp()");
                    }
                }, new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        Log.d("ListViewFragment", "onScrollStateChanged()");
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        Log.d("ListViewFragment", "onScroll()");
                    }
                });
                if (homeEvents.size() == 0) {// If no events are found
                    Toast.makeText(getActivity().getApplicationContext(),"No Events Found", Toast.LENGTH_LONG).show();
                    events_list.setVisibility(View.INVISIBLE);
                }
                else {
                    // Display events
                    events_list.setVisibility(View.VISIBLE);
                    HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(getActivity(), homeEvents);
                    homeEventsAdapter.notifyDataSetChanged();
                    events_list.setAdapter(homeEventsAdapter);
                    events_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(getActivity().getApplicationContext(), EventInfo.class);
                            intent.putExtra("eventObject", homeEvents.get(position));
                            startActivity(intent);
                        }
                    });
                }

//            }
        }
    }

    public class HomeEventsAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<EventsObject> data;
        private LayoutInflater inflater = null;

        public HomeEventsAdapter(Activity activity, ArrayList<EventsObject> homeEvents) {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                view = inflater.inflate(R.layout.fragment_list_item, null);
            }//TODO YD update event icon according to event type
//            try {
//                Spanned namefirst = Html.fromHtml(data.get(position).GetName());
//                String s = String.valueOf(namefirst).substring(0, 1).toUpperCase();
//
//                TextView txt_first = (TextView) view.findViewById(R.id.txt_first);
//                txt_first.setText("" + Html.fromHtml(s));
//            } catch (StringIndexOutOfBoundsException e) {
//                // TODO: handle exception
//                e.printStackTrace();
//            }

            TextView eventName = (TextView) view.findViewById(R.id.event_name);
            eventName.setText(data.get(position).GetName());

            TextView formattedLocation = (TextView) view.findViewById(R.id.formatted_loctaion_txt);
            formattedLocation.setText(data.get(position).GetFormattedLocation());

            TextView eventDate = (TextView) view.findViewById(R.id.date_txt);
            eventDate.setText(data.get(position).GetDate());
//            TODO uncomment once start time is added to the db
//            TextView starTime = (TextView) view.findViewById(R.id.start_time_txt);
//            starTime.setText(data.get(position).GetStartTime());

            TextView eventDistance = (TextView) view.findViewById(R.id.distance_txt);
            eventDistance.setText(data.get(position).GetDistance());

            return view;
        }
    }

}
