package com.inc.playground.playground;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.inc.playground.playground.utils.InitGlobalVariables;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.inc.playground.playground.utils.NetworkUtilities.eventListToArrayList;

/**
 * Created by lina on 7/10/2016.
 */
public class NotificationsList extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ListView notifications_list; //ListView listView;
    private ArrayList<NotificationObject> notifications;
    private GlobalVariables globalVariables;
    EventsObject curEvent = new EventsObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list);
        Intent i = null;
        new InitGlobalVariables(this,i).init();

        this.globalVariables = ((GlobalVariables) getApplication());
        notifications = globalVariables.GetNotifications();

        new getList().execute();
    }


    private class getList extends AsyncTask<Integer, Integer,String> { // params , progress, result

        private ProgressDialog dialog = new ProgressDialog(NotificationsList.this);

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
            notifications_list = (ListView) findViewById(R.id.list_detail);
            SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)NotificationsList.this.
                    findViewById(R.id.swipe_refresh_layout);


            if (notifications !=  null) {
                if(globalVariables.GetNotifications().size() == 0) {
                    Toast.makeText(NotificationsList.this, "No Notifications Found", Toast.LENGTH_LONG).show();
                    notifications_list.setVisibility(View.INVISIBLE);
                }

            else {
                    // Display events
                    notifications_list.setVisibility(View.VISIBLE);
                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(NotificationsList.this, notifications);//homeEvents= globalVariable.currentuserevents
                    notificationsAdapter.notifyDataSetChanged();
                    notifications_list.setAdapter(notificationsAdapter);

                    //swipe listener
                    swipeRefreshLayout.setOnRefreshListener(NotificationsList.this);
                    /*swipeRefreshLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    swipeRefreshLayout.setRefreshing(true);

                                                    //fetchMovies();
                                                }
                                            }
                    );*/


                    notifications_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            if(! notifications.get(position).getTitle().contains("canceled"))
                            {
                                Intent intent = new Intent(NotificationsList.this, EventInfo.class);
                                intent.putExtra("eventObject", notifications.get(position).getEvent());
                                startActivity(intent);
                                finish();
                                notifications.remove(notifications.get(position));
                            }
                            else
                            {
                                Intent intent = new Intent(NotificationsList.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                notifications.remove(notifications.get(position));
                            }


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


    public class NotificationsAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<NotificationObject> data;
        private LayoutInflater inflater = null;

        public NotificationsAdapter(Activity activity, ArrayList<NotificationObject> notifications) {
            this.activity = activity;
            this.data = notifications;
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

            TextView eventName =(TextView) view.findViewById(R.id.event_nameTxt);
            TextView notificationDescription = (TextView) view.findViewById(R.id.notification_descriptionTxt);


            if(data.get(position).getTitle().contains("canceled"))
            {
                eventName.setText(data.get(position).getTitle());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NotificationsList.this, MainActivity.class);
                        startActivity(intent);
                        notifications.remove(data.get(position));
                    }
                });

            }
            else
            {

                JSONArray eventsFromServerJSON = new JSONArray();
                try {
                    eventsFromServerJSON = data.get(position).getInputJson().getJSONArray("more");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    curEvent = eventListToArrayList(eventsFromServerJSON, globalVariables.GetCurrentLocation()).get(0);
                    data.get(position).setEvent(curEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                eventName.setText(curEvent.GetName());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NotificationsList.this, EventInfo.class);
                        intent.putExtra("eventObject", data.get(position).getEvent());
                        intent.putExtra("parentActivity","NotificationList");
                        startActivity(intent);
                        notifications.remove(data.get(position));
                    }
                });
            }

            notificationDescription.setText(data.get(position).getDescription());




            return view;
        }
    }

    @Override
    public void onRefresh() {
        Log.i("Enter on refresh", "");

        SwipeRefreshLayout swipeRefreshLayout =  (SwipeRefreshLayout)NotificationsList.this.
                findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setRefreshing(true);

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
        Intent iv = new Intent(NotificationsList.this,MainActivity.class);
        startActivity(iv);
        finish();
    }

}

