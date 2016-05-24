package com.inc.playground.playground;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.zaim.decoratecalendarview.DecorateCalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentCalendar extends Fragment implements DecorateCalendarView.OnDecorateCalendarListener {

    private DecorateCalendarView calendarView;
    GlobalVariables globalVariables;
    ArrayList<EventsObject> homeEvents;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.decorate_calendar_view, container, false);
        super.onCreate(savedInstanceState);

        calendarView = (DecorateCalendarView) v.findViewById(R.id.my_calendar);

        this.globalVariables = ((GlobalVariables) getActivity().getApplication());
        homeEvents = this.globalVariables.GetHomeEvents();

        // Set event listener from calendar view
        //calendarView.setOnDecorateCalendarListener((DecorateCalendarView.OnDecorateCalendarListener) this);

        Bundle bundle = new Bundle();
        bundle.putString(DecorateCalendarView.BUNDLE_KEY_BEGINNING_DAY_OF_WEEK, String.valueOf(Calendar.SUNDAY));
        bundle.putString(DecorateCalendarView.BUNDLE_KEY_HOLIDAY_HIGHLIGHT_TYPE, String.valueOf(DecorateCalendarView.BUNDLE_KEY_HOLIDAY_HIGHLIGHT_TYPE));
        //bundle.putString(DecorateCalendarView.BUNDLE_KEY_SELECTED_COLOR, String.valueOf(Color.parseColor("#FFFF4444")));
        //bundle.putString(DecorateCalendarView.BUNDLE_KEY_SELECTED_COLOR, String.valueOf(getResources().getColor(R.color.select_background)));

        calendarView.setOnDecorateCalendarListener(this );
        calendarView.initCalendar(getActivity().getSupportFragmentManager(), bundle);

        return v;
    }

    @Override
    public void onDayClick(Date day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //Toast.makeText(this, format.format(day), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onChangeDisplayMonth(Date date) {
        //SimpleDateFormat format = new SimpleDateFormat("2016-05");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        //Toast.makeText(this, format.format(date), Toast.LENGTH_SHORT).show();

        /*if date in 2014:
            // Decorate cell of day
            for(int i=0;i<homeEvents.size() ;i++){
                EventsObject e =  homeEvents.get(i);
                DateFormat f = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                String stringDate = e.GetDate();
                //ate dat = f.parse(stringDate);
                //if(year.equals(2014) )
                //get the date object

            }
            */


            calendarView.setBottomTextOnDay(28, "Football3", Color.parseColor("#FF99cc00"));
            calendarView.setMiddleTextOnDay(22, "SoccerTau", Color.parseColor("#FFFF4444"));


    }

}



