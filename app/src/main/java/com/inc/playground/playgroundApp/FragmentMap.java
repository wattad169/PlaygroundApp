package com.inc.playground.playgroundApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inc.playground.playgroundApp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment that launches other parts of the demo application.
 */
public class FragmentMap extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    GlobalVariables globalVariables;
    ArrayList<EventsObject> homeEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude

        this.globalVariables = ((GlobalVariables) getActivity().getApplication());
        if(globalVariables.getFilterEvents() != null && globalVariables.getFilterEvents().size() != 0 )
        {
            homeEvents = globalVariables.getFilterEvents();
        }
        else
        {
            homeEvents = this.globalVariables.GetHomeEvents();
        }
        HashMap<String, Double> currentLocation = this.globalVariables.GetCurrentLocation();
        if (currentLocation!=null){
            double myLat = currentLocation.get(Constants.LOCATION_LAT);
            double myLon = currentLocation.get(Constants.LOCATION_LON);
            if (homeEvents!= null){
                for(int i=0;i<homeEvents.size();i++){
                    EventsObject current = homeEvents.get(i);
                    double latitude = Double.parseDouble(current.GetLocation().get(Constants.LOCATION_LAT));
                    double longitude = Double.parseDouble(current.GetLocation().get(Constants.LOCATION_LON));
                // create marker
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(latitude, longitude)).title(current.GetName());
                // Changing marker icon
                    marker.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                // adding marker
                    googleMap.addMarker(marker);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(myLat, myLon)).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                // Perform any camera updates here
            }}}
//        double latitude = 17.385044;
//        double longitude = 78.486671;
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}