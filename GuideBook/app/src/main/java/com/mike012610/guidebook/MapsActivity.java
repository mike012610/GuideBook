package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private LatLng NOW = null;
    private GoogleMap map;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);
        moveNow(map);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.addMarker(new MarkerOptions().position(point).title(String.valueOf(count)).snippet("test"));
                count += 1;
            }
        });
        mapfrag.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.addMarker(new MarkerOptions().position(NOW).title("Marker").snippet("now!!!"));
    }


    private void moveNow(GoogleMap map) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);
        NOW = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 16));
    }
}
