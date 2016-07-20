package com.inc.playground.playground;


import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.inc.playground.playground.utils.Constants;
import com.inc.playground.playground.utils.InitGlobalVariables;
import com.inc.playground.playground.utils.NetworkUtilities;
import com.inc.playground.playground.utils.User;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.*;
import java.util.Set;

/**
 * Created by mostafawattad on 30/04/2016.
 */

public class FragmentList extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView events_list; //ListView listView;
    private List<EventsObject> homeEvents;
    private GlobalVariables globalVariables;
    private HandleEventTask myEventsTask = null;
    public SharedPreferences prefs ;
    private ImageButton filterButton;
    private Boolean isOK = true;
    private String userLoginId;
    private User currentUser;
    private Set<String> userEvents;
    private EditText inputSearch;
    private int eventSize = 0;


    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        // avoiding opening Keyboard automatically
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.globalVariables = ((GlobalVariables) getActivity().getApplication());
        eventSize = Math.min(Constants.maxEvents, this.globalVariables.GetHomeEvents().size());
        homeEvents = this.globalVariables.GetHomeEvents().subList(0, eventSize);
        if(getActivity().getIntent().getStringExtra("parent") != null && getActivity().getIntent().getStringExtra("parent").equals("filter"))
        {
            homeEvents = (ArrayList<EventsObject>) getActivity().getIntent().getSerializableExtra("events");
        }

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
                getActivity().finish();
                Intent iv = new Intent(getActivity().getApplicationContext(),
                        FilterActivity.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_down, R.anim.slide_up).toBundle();
                startActivity(iv, bndlanimation);
            }
        });



        new getList().execute();

        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(cs.toString().equals(""))
                {
                    HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(getActivity(), homeEvents);//homeEvents= globalVariable.currentuserevents
                    homeEventsAdapter.notifyDataSetChanged();
                    events_list.setAdapter(homeEventsAdapter);
                }
                else
                {
                    int textlength = cs.length();
                    ArrayList<EventsObject> tempArrayList = new ArrayList<EventsObject>();
                    for(EventsObject c: homeEvents){
                        if (textlength <= c.GetName().length()) {
                            if (c.GetName().toLowerCase().contains(cs.toString().toLowerCase())) {
                                tempArrayList.add(c);
                            }
                        }
                    }
                    HomeEventsAdapter homeEventsAdapter = new HomeEventsAdapter(getActivity(), tempArrayList);//homeEvents= globalVariable.currentuserevents
                    homeEventsAdapter.notifyDataSetChanged();
                    events_list.setAdapter(homeEventsAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        return rootView;
    }

    private class getList extends AsyncTask<Integer, Integer,String> { // params , progress, result

        private ProgressDialog dialog = new ProgressDialog(FragmentList.this.getActivity());

        @Override
        protected void onPreExecute() {
            initProgressDialog(dialog);
        }
        @Override
        protected String doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
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
            }
            try
            {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                // do your Display and data setting operation here
            }
            catch(Exception e)
            {

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    public class HomeEventsAdapter extends BaseAdapter {

        private Activity activity;
        private List<EventsObject> data;
        private LayoutInflater inflater = null;

        public HomeEventsAdapter(Activity activity, List<EventsObject> homeEvents) {
//            this.data = new ArrayList<EventsObject>();
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

           // update type icon according to event type
            String uri = "@drawable/pg_" + data.get(position).GetType()+ "_icon";
            int imageResource = getResources().getIdentifier(uri,null,getActivity().getPackageName());
            ImageView typeImg = (ImageView) view.findViewById(R.id.type_img);
            Drawable typeDrawable = getResources().getDrawable(imageResource);
            typeImg.setImageDrawable(typeDrawable);

            TextView eventName = (TextView) view.findViewById(R.id.event_name);
            eventName.setText(data.get(position).GetName());

            TextView formattedLocation = (TextView) view.findViewById(R.id.formatted_loctaion_txt);
            formattedLocation.setText(data.get(position).GetFormattedLocation());

            TextView eventDate = (TextView) view.findViewById(R.id.date_txt);

            eventDate.setText(data.get(position).GetDate());

            TextView starTime = (TextView) view.findViewById(R.id.start_time_txt);
            starTime.setText(data.get(position).GetStartTime());

            TextView eventDistance = (TextView) view.findViewById(R.id.distance_txt);
            eventDistance.setText(data.get(position).GetDistance());

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
                            EventInfo.toastGeneral(getContext(),"You left the event", Toast.LENGTH_SHORT);
                            playTxt.setText("Play");//Todo  update eventInfo
                            curplayButton.setTextColor(Color.parseColor("#D0D0D0"));//how to set triangle gray?
                            playTxt.setTextColor(Color.parseColor("#D0D0D0"));
                        }
                    }
                    else { //join event
                        //Todo if private
                        //Todo if public
                        //curplayButton.setClickable(true);
                        eventTask = "join_event";
                        playTxt.setText("Playing");
                        curplayButton.setTextColor(Color.parseColor("#104E8B"));//how to set triangle blue?
                        playTxt.setTextColor(Color.parseColor("#104E8B"));
                        EventInfo.toastGeneral(getContext(), "You joined the event!", Toast.LENGTH_SHORT);
                    }
                    /*Server side update */
                    myEventsTask = new HandleEventTask(data.get(position), eventTask);
                    myEventsTask.execute((Void) null);
                    //curplayButton.setChecked(!curplayButton.isChecked());
                }});

            //TODO check if userLoginId is on members event
            if(currentUser != null && userEvents !=null ) {
                if (!userEvents.isEmpty()) {
                    if (userEvents.contains(data.get(position).GetId())) {
                        //playButton.setClickable(false);
                        if(currentUser.GetUserId().equals(data.get(position).GetCreatorId())) {//cancel event
                            //it's the creator
                            playButton.setVisibility(View.INVISIBLE);
                            playTxt.setText("Boss");
                            playTxt.setTextColor(Color.parseColor("#000000"));
                            //not allowed to cancel from here
                        }
                        else {
                            playButton.setChecked(true);
                            playTxt.setText("Playing");
                            playTxt.setTextColor(Color.parseColor("#104E8B"));
                        }
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

        protected void onPreExecute() {
//            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
//            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

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
                        isOK = true;
                        if(eventTask.equals("join_event")) {
                            userEvents.add(currentEvent.GetId());
                            //update also array<eventsObject>
                        }
                        else if (eventTask.equals("leave_event")) {
                            userEvents.remove(currentEvent.GetId());
                        //update also array<eventsObject>
                        }
                        else if(eventTask.equals("request_join_event")){
                            userEvents.add(currentEvent.GetId());
                            //update also array<eventsObject>
                        }
                        currentUser.SetUserEvents(userEvents);
                        //currentUser.Set() update also array<eventsObject>
                    } else {
                        isOK = false;
                        //TODO YD override toggle method -> not to switch text to "playing"
                    }
                    myEventsTask = null;
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
        protected void onPostExecute(final String responseString) {
//            progressBar.setVisibility(View.GONE);
        }

//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
    }

    @Override
    public void onRefresh() {
        Log.i("Enter on refresh", "");

        SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)getActivity().
                findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setRefreshing(true);
        Intent i = new Intent(this.getActivity(), MainActivity.class);
        Splash.GetEventsAsyncTask getEventsAsyncTask = new Splash.GetEventsAsyncTask(this.getContext(),i);
        getEventsAsyncTask.execute();

        swipeRefreshLayout.setRefreshing(false);

    }

    private void initProgressDialog(ProgressDialog dialog)
    {
        String message = "Loading ...";
        SpannableString spanMessage = new SpannableString(message);
        spanMessage.setSpan(new RelativeSizeSpan(1.2f),0,spanMessage.length(),0);
        spanMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#104e8b")), 0, spanMessage.length(), 0);
        dialog.setTitle("Please wait");
        dialog.setMessage(spanMessage);
        dialog.setIcon(R.drawable.pg_loading);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(800,420);
    }

}
