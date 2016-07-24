package com.inc.playground.playgroundApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.playground.playgroundApp.utils.Constants;
import com.inc.playground.playgroundApp.utils.InitGlobalVariables;
import com.inc.playground.playgroundApp.utils.NetworkUtilities;
import com.inc.playground.playgroundApp.utils.UserImageEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.inc.playground.playgroundApp.utils.NetworkUtilities.eventListToArrayList;

/**
 * Created by lina on 7/13/2016.
 */
public class ApproveEventList extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ListView approve_list; //ListView listView;
    public ArrayList<String> approves;
    public EventsObject eventForApprove;
    public GlobalVariables globalVariables;
    public SharedPreferences prefs;
    public TextView requestMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_event_list);
        Intent i = null;
        new InitGlobalVariables(this,i).init();

        this.globalVariables = ((GlobalVariables) getApplication());
        final String MY_PREFS_NAME = "Login";
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        Intent prevIntent = getIntent();
        if(prevIntent.getStringExtra("parent").equals("EventInfo")) {
            eventForApprove = (EventsObject) prevIntent.getSerializableExtra("eventObject");
        }
        else
        {
            JSONObject inputJson;
            JSONArray eventsFromServerJSON = new JSONArray();
            try{
                inputJson = new JSONObject(prevIntent.getStringExtra("inputJson"));
                eventsFromServerJSON = inputJson.getJSONArray("more");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch(NullPointerException nPoitExc){
                nPoitExc.printStackTrace();
            }

            try {
                eventForApprove = eventListToArrayList(eventsFromServerJSON, globalVariables.GetCurrentLocation()).get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        approves = eventForApprove.getApproveList();
        new getList().execute();
    }


    private class getList extends AsyncTask<Integer, Integer,String> { // params , progress, result

        private ProgressDialog dialog = new ProgressDialog(ApproveEventList.this);

        @Override
        protected void onPreExecute() {
            initProgressDialog(dialog);
        }
        @Override
        protected String doInBackground(Integer... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            approve_list = (ListView) findViewById(R.id.list_detail);
            SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)ApproveEventList.this.
                    findViewById(R.id.swipe_refresh_layout);


            if (approves ==  null) {
                Toast.makeText(ApproveEventList.this, "No Requests Found", Toast.LENGTH_LONG).show();
                approve_list.setVisibility(View.INVISIBLE);
            }
            else {
                // Display events
                approve_list.setVisibility(View.VISIBLE);
                ApproveAdapter notificationsAdapter = new ApproveAdapter(ApproveEventList.this, approves);
                notificationsAdapter.notifyDataSetChanged();
                approve_list.setAdapter(notificationsAdapter);

                //swipe listener
                swipeRefreshLayout.setOnRefreshListener(ApproveEventList.this);
                    /*swipeRefreshLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    swipeRefreshLayout.setRefreshing(true);

                                                    //fetchMovies();
                                                }
                                            }
                    );*/


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


    public class ApproveAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<String> data;
        private LayoutInflater inflater = null;

        public ApproveAdapter(Activity activity, ArrayList<String> approveList) {
            this.activity = activity;
            this.data = approveList;
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
                view = inflater.inflate(R.layout.approve_event_item, null);
            }

            ArrayList<UserImageEntry> userImageEntry = globalVariables.GetUsersList();
            UserImageEntry approveUser = null;
            if(userImageEntry != null && ! userImageEntry.isEmpty()) {
                for (int i = 0; i < userImageEntry.size(); i++) {
                    if (userImageEntry.get(i).id.equals(data.get(position))) {
                        approveUser = userImageEntry.get(i);
                        break;
                    }
                }
            }

            TextView approveName = (TextView) view.findViewById(R.id.approveId_txt);
            approveName.setText(approveUser.fullname);

            // set event name
            TextView eventName =(TextView) view.findViewById(R.id.event_nameTxt);
            eventName.setText(eventForApprove.GetName());

            // set picture member for approve
            final String urlUser = approveUser.url;
            ImageView img_profile = (ImageView) view.findViewById(R.id.img_profile);
            Bitmap imageBitmap = globalVariables.GetUsersImagesMap().get(data.get(position));
            img_profile.setImageBitmap(imageBitmap);
            img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EventPhotoUserListener(urlUser).execute();

                }
            });

                    // set request message
                    requestMessage = (TextView) view.findViewById(R.id.request_messageTxt);
            requestMessage.setVisibility(View.INVISIBLE);

                    final Button approveBtn = (Button) view.findViewById(R.id.positive_btn);
                    final Button rejectBtn = (Button) view.findViewById(R.id.negative_btn);


                    approveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HandleEventTask handleEventTask = new HandleEventTask(eventForApprove, "1", data.get(position));
                            handleEventTask.execute((Void) null);
                            approveBtn.setVisibility(View.INVISIBLE);
                            rejectBtn.setVisibility(View.INVISIBLE);
                            requestMessage.setVisibility(View.VISIBLE);
                            requestMessage.setText("Request accepted");
                            globalVariables.updateEventInEvents(eventForApprove.GetId(), data.get(position));

                        }
                    });

            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HandleEventTask handleEventTask = new HandleEventTask(eventForApprove, "0", data.get(position));
                    handleEventTask.execute((Void) null);
                    approveBtn.setVisibility(View.INVISIBLE);
                    rejectBtn.setVisibility(View.INVISIBLE);
                    requestMessage.setVisibility(View.VISIBLE);
                    requestMessage.setText("Request removed");
                    globalVariables.updateEventInEvents(eventForApprove.GetId(), data.get(position));


                }
            });

                    return view;
                }
            }

            @Override
    public void onRefresh() {
        Log.i("Enter on refresh", "");

        SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)ApproveEventList.this.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(true);
        ApproveAdapter notificationsAdapter = new ApproveAdapter(ApproveEventList.this,approves);
                new InitGlobalVariables(this,null).init();

        notificationsAdapter.notifyDataSetChanged();
        approve_list.setAdapter(notificationsAdapter);

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
        window.setLayout(800, 420);
    }

    @Override
    public void onBackPressed()
    {
        Intent iv = new Intent(ApproveEventList.this,MainActivity.class);
        startActivity(iv);
        finish();
    }



    public class HandleEventTask extends AsyncTask<Void, Void, String> {
        /*handle 3 requests: 1.approve 2. ignore  */

        private String responseString;
        String join_status,approveId;
        private EventsObject currentEvent;

        public HandleEventTask(EventsObject currentEvent, String join_status, String approveId) {
            this.currentEvent = currentEvent;
            this.join_status = join_status;
            this.approveId = approveId;
            assert (join_status.equals("0") || join_status.equals("1"));
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            JSONObject cred = new JSONObject();
            if (prefs.getString("userid", null) != null) {
                //If the user is logged in
                String userId = prefs.getString("userid", null);

                try {//Send request to server for eventTask
                    cred.put(NetworkUtilities.TOKEN, userId);
                    cred.put("user_id", this.approveId);
                    cred.put("event_id", currentEvent.GetId());
                    cred.put("join_status", join_status);

                    responseString = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/resolve_join_request_response/");
                } catch (JSONException | UnsupportedEncodingException | NullPointerException e) {
                    e.printStackTrace();
                }
                if (responseString == null) {
                    Log.i("TESTID", currentEvent.GetId());
                }

                //Check response
                JSONObject myObject = null;
                String responseStatus = null;
                try {
                    myObject = new JSONObject(responseString);
                    responseStatus = myObject.getString(Constants.RESPONSE_STATUS);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }

            } else {
                // If user is not logged -> in send to login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String responseString) {
        }

    }

    public class EventPhotoUserListener extends AsyncTask<String, String, String> {
        int i;
        String photoUrl;

        EventPhotoUserListener(String photoUrl) {
            this.photoUrl = photoUrl;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
           /*server call   */
            String userProfileResponseStr = "";
            try {
                JSONObject cred = new JSONObject();
                try {
                    cred.put(NetworkUtilities.TOKEN, "StubToken");
                    cred.put(NetworkUtilities.PHOTO_URL, photoUrl);
                    userProfileResponseStr = NetworkUtilities.doPost(cred, NetworkUtilities.BASE_URL + "/get_user_by_photo/");

                } catch (JSONException|UnsupportedEncodingException e) {
                }
            } catch (Exception ex) {
                userProfileResponseStr = "";
            }
            // Convert string received from server to JSON
            JSONObject userInfoFroServer = null;
            JSONObject responseJSON = null;
            try {

                responseJSON = new JSONObject(userProfileResponseStr);
                userInfoFroServer = responseJSON.getJSONObject(Constants.RESPONSE_MESSAGE);

                Intent iv = new Intent(ApproveEventList.this,
                        com.inc.playground.playgroundApp.upLeft3StripesButton.
                                MyProfile.class);

                JSONArray eventEntries = userInfoFroServer.getJSONArray(Constants.EVENT_ENTRIES);

                ArrayList<EventsObject> memeberEvents = NetworkUtilities.eventUserListToArrayList(eventEntries, globalVariables.GetCurrentLocation(), Constants.EVENT_ENTRIES);

                ArrayList<ArrayList<EventsObject>> allEvents = Splash.eventsTypesfromJson(userProfileResponseStr, InitGlobalVariables.globalVariables.GetCurrentLocation());
                ArrayList<EventsObject> events  = allEvents.get(0);
                ArrayList<EventsObject> events_wait4approval = allEvents.get(1);
                ArrayList<EventsObject> events_decline = allEvents.get(2);
                iv.putExtra("events", events);
                iv.putExtra("events_wait4approval", events_wait4approval);
                iv.putExtra("events_decline", events_decline);

                iv.putExtra("name", userInfoFroServer.getString("fullname"));
                iv.putExtra("createdNumOfEvents",userInfoFroServer.getString("created_count"));
                iv.putExtra("photoUrl", photoUrl);
                startActivity(iv);
//                finish();


            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }
        //Log.d("EVent info", "getMembersUrls.sucessful" + membersImagesUrls);
    }

}
