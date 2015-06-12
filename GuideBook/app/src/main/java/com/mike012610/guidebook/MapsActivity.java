package com.mike012610.guidebook;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback{
    private LatLng NOW = null;
    private GoogleMap map;
    private int count = 0;
    private boolean marker = false;
    private SlidingUpPanelLayout slide_Layout;
    private GoogleMap.OnMapClickListener  panel_disable = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            map.setOnMapClickListener(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onCreateDrawer(savedInstanceState);

        slide_Layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);
        map.setPadding(0, obtainActionBarHeight(), 0, 0);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                TextView tv = (TextView) findViewById(R.id.location_name);
                tv.setText("set success");
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                map.setOnMapClickListener(panel_disable);
                return true;
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_marker:
                if (!marker) {
                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            map.addMarker(new MarkerOptions().position(point).title(String.valueOf(count)).snippet("test"));
                            count += 1;
                        }
                    });
                    marker = true;
                }
                else {
                    map.setOnMapClickListener(null);
                    marker = false;
                }
                break;
        }
        return true;
    }

    private class MakeLocation extends AsyncTask<HttpMethod,Integer,String>{

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(drawerLayout.getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
