package com.mike012610.guidebook;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback{
    private LatLng NOW = null;
    private GoogleMap map;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer(savedInstanceState);
        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.addMarker(new MarkerOptions().position(point).title(String.valueOf(count)).snippet("test"));
                count += 1;
            }
        });
        map.setPadding(0, obtainActionBarHeight(), 0, 0);
        mapfrag.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        moveNow(map);
        map.addMarker(new MarkerOptions().position(NOW).title("Marker").snippet("now!!!"));
    }

    private void moveNow(GoogleMap map) {
        GPSTracker gps = new GPSTracker(MapsActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        NOW = new LatLng(gps.getLatitude(),gps.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 16));
    }

}
