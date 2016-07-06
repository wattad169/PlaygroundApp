package com.inc.playground.playground;


import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by mostafawattad on 30/04/2016.
 */

public class FragmentList extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    ListView events_list; //ListView listView;
    ArrayList<EventsObject> homeEvents;; //List<Movie> movieList;
    //SwipeListAdapter adapter (in code already - HomeEventsAdapter homeEventsAdapter)


    ProgressDialog progressDialog;
    GlobalVariables globalVariables;
    private HandleEventTask myEventsTask = null;
    //private LeaveHandleEventTask LeaveEventTask = null; //not needed
    public SharedPreferences prefs ;
    ImageButton filterButton;
    Boolean isOK = true;
    String userLoginId;
    User currentUser;
    Set<String> userEvents;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        this.globalVariables = ((GlobalVariables) getActivity().getApplication());

        homeEvents = this.globalVariables.GetHomeEvents();
        prefs = getActivity().getSharedPreferences("Login",getActivity().MODE_PRIVATE);
        userLoginId = prefs.getString("userid", null);
        currentUser = globalVariables.GetCurrentUser();
        GlobalVariables globalVariables;
        final String MY_PREFS_NAME = "Login"; //idan question why we chose that name??
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, 0);

        if(currentUser != null ) { // the user is login
            userEvents = currentUser.GetUserEvents();
        }
        filterButton = (ImageButton) rootView.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent iv = new Intent(getActivity().getApplicationContext(),
                    FilterActivity.class );
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_down, R.anim.slide_up).toBundle();
                startActivity(iv,bndlanimation);

                }});


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
            SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)getActivity().
            findViewById(R.id.swipe_refresh_layout);
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
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent iv = new Intent(getActivity().getApplicationContext(), AddEvent.class);
                    startActivity(iv);
                    getActivity().finish();
                }
            });
            if (prefs.getString("userid", null) != null){
                fab.setVisibility(View.VISIBLE);

            }

            if (homeEvents !=  null) {
                if (homeEvents.size() == 0) {// If no events are found
                    Toast.makeText(getActivity().getApplicationContext(), "No Events Found", Toast.LENGTH_LONG).show();
                    events_list.setVisibility(View.INVISIBLE);
                } else {
                    // Display events
                    events_list.setVisibility(View.VISIBLE);
                    HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(getActivity(), homeEvents);//homeEvents= globalVariable.currentuserevents
                    homeEventsAdapter.notifyDataSetChanged();
                    events_list.setAdapter(homeEventsAdapter);

                    //swipe listener
                    swipeRefreshLayout.setOnRefreshListener(FragmentList.this);
                    /*swipeRefreshLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    swipeRefreshLayout.setRefreshing(true);

                                                    //fetchMovies();
                                                }
                                            }
                    );*/




                    events_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(getActivity().getApplicationContext(), EventInfo.class);
                            intent.putExtra("eventObject", homeEvents.get(position));
                            startActivity(intent);
                            getActivity().finish();

                        }
                    });
                }
            }}
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                view = inflater.inflate(R.layout.fragment_list_item, null);
                if(position%2==0)
                {
                    view.setBackground(getResources().getDrawable(R.drawable.pg_cell_first));
                }else {
                    view.setBackground(getResources().getDrawable(R.drawable.pg_cell_first_b));
                }
            }
            Typeface fontText = Typeface.createFromAsset(getActivity().getAssets(),"sansation.ttf");
            Typeface fontText2 = Typeface.createFromAsset(getActivity().getAssets(),"kimberly.ttf");
            Typeface fontText3 = Typeface.createFromAsset(getActivity().getAssets(),"crayon.ttf");
           // update type icon according to event type
            String uri = "@drawable/pg_" + data.get(position).GetType()+ "_icon";
            int imageResource = getResources().getIdentifier(uri,null,getActivity().getPackageName());
            ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
            Drawable typeDrawable = getResources().getDrawable(imageResource);
            typeImg.setImageDrawable(typeDrawable);

            TextView eventName = (TextView) view.findViewById(R.id.event_name);
            eventName.setText(data.get(position).GetName());
//            eventName.setTypeface(fontText);

            TextView formattedLocation = (TextView) view.findViewById(R.id.formatted_loctaion_txt);
            formattedLocation.setText(data.get(position).GetFormattedLocation());
//            formattedLocation.setTypeface(fontText3);

            TextView eventDate = (TextView) view.findViewById(R.id.date_txt);
            eventDate.setText(data.get(position).GetDate());
//            eventDate.setTypeface(fontText3);

            TextView starTime = (TextView) view.findViewById(R.id.start_time_txt);
            starTime.setText(data.get(position).GetStartTime());
//            startTime.setTypeface(fontText3);

            TextView eventDistance = (TextView) view.findViewById(R.id.distance_txt);
            eventDistance.setText(data.get(position).GetDistance());
//            eventDistance.setTypeface(fontText3);

            TextView kmTxt = (TextView) view.findViewById(R.id.kmTxt);
//            kmTxt.setTypeface(fontText3);

            final TextView playTxt = (TextView) view.findViewById(R.id.play_txt);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity().getApplicationContext(), EventInfo.class);
                    intent.putExtra("eventObject", data.get(position));
                    startActivity(intent);
//                    getActivity().finish();
                }
            });

            ToggleButton playButton = (ToggleButton)view.findViewById(R.id.join);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton curplayButton = (ToggleButton) v;
                    String eventTask = null;
                    if (!curplayButton.isChecked()) {
                        assert (currentUser!=null);
                        if(currentUser.GetUserId().equals(data.get(position).GetCreatorId())){//cancel event
                            eventTask = "cancel_event";
                            playTxt.setText("cancel");
                        }
                        else { //leave event
                            eventTask = "leave_event";
                            playTxt.setText("Play");//Todo  update eventInfo
                            curplayButton.setTextColor(Color.parseColor("#D0D0D0"));//how to set triangle gray?
                            playTxt.setTextColor(Color.parseColor("#D0D0D0"));
                        }
                    }
                    else { //join event
                        //curplayButton.setClickable(true);
                        eventTask = "join_event";
                        playTxt.setText("Playing");
                        curplayButton.setTextColor  (Color.parseColor("#104E8B"));//how to set triangle blue?
                        playTxt.setTextColor(Color.parseColor("#104E8B"));
                    }
                    /*Server side update */
                    myEventsTask = new HandleEventTask(data.get(position), eventTask);
                    myEventsTask.execute((Void) null);
                    //curplayButton.setChecked(!curplayButton.isChecked());
                }});

            //TODO check if userLoginId is on members event
            if(currentUser != null ) {
                if (!userEvents.isEmpty()) {
                    if (userEvents.contains(data.get(position).GetId())) {
                        //playButton.setClickable(false);
                        playButton.setChecked(true);
                        playTxt.setText("Playing");
                        playTxt.setTextColor(Color.parseColor("#104E8B"));
                    }
                    else{// if user dont play
                        playButton.setChecked(false);
                        playTxt.setText("Play");
                        playTxt.setTextColor(Color.parseColor("#D0D0D0"));
                    }
                }
            }
            return view;
        }
    }

    public class HandleEventTask extends AsyncTask<Void, Void, String> {
        //        private Context context; //Todo : can we delete it ?
        private EventsObject currentEvent;
        String eventTask;
        public HandleEventTask(EventsObject currentEvent, String eventTask) {
            this.currentEvent = currentEvent;
            this.eventTask = eventTask;
        }
        private String responseString;

        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject cred = new JSONObject();
            if (prefs.getString("userid", null) != null) {
                //If the user is logged in
                String userId = prefs.getString("userid", null);

                try {//Send request to server for joining event
                    cred.put(NetworkUtilities.TOKEN, userId);
                    cred.put("event_id", currentEvent.GetId());
                    responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/" + eventTask + "/");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(responseString == null) {

                    Log.i("TESTID",currentEvent.GetId());
                }

                //Check response
                JSONObject myObject = null;
                String responseStatus = null;
                try {
                    myObject = new JSONObject(responseString);
                    responseStatus = myObject.getString(Constants.RESPONSE_STATUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myObject != null && responseStatus != null) {
                    if (responseStatus.equals(Constants.RESPONSE_OK.toString())) {
                        myEventsTask = null;
                        isOK = true;
                        if(eventTask.equals("join_event")) userEvents.add(currentEvent.GetId());
                        else if (eventTask.equals("leave_event")) userEvents.remove(currentEvent.GetId());
                        //userEvents include events if the user is the creator?
                        currentUser.SetUserEvents(userEvents);
                        //TODO YD Switch toggle button text to "playing"
                    } else {
                        myEventsTask = null;
                        isOK = false;
                        //TODO YD override toggle method -> not to switch text to "playing"
                    }
                }
            }
            else
            {
                // If user is not logged -> in send to login activity
                Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }

            return null;
        }
        @Override
        protected void onPostExecute(final String responseString) {}
    }

    @Override
    public void onRefresh() {
        Log.i("Enter on refresh", "");

        SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)getActivity().
                findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setRefreshing(true);

        Splash.GetEventsAsyncTask getEventsAsyncTask = new Splash.GetEventsAsyncTask(this.getContext());
        getEventsAsyncTask.execute();
        HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(  getActivity(),globalVariables.GetHomeEvents() );

        //userEventsAdapter userEventsAdapter = new userEventsAdapter(  getActivity(),globalVariables.GetHomeEvents() );

        homeEventsAdapter.notifyDataSetChanged();
        events_list.setAdapter(homeEventsAdapter);

        swipeRefreshLayout.setRefreshing(false);
    }

}
