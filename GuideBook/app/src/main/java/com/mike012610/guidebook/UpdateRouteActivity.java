package com.mike012610.guidebook;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class UpdateRouteActivity extends Activity implements OnMapReadyCallback {

    private ListView listView;
    private SimpleAdapter adapter;
    private Button makeGuide;
    private String current_id;

    private LatLng NOW = null;
    private GoogleMap map;
    private int count = 0;
    private boolean marker = false;
    private int marker_num = 0;
    private Marker[] search_result = new Marker[20];
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
        setContentView(R.layout.update_make_route);
        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);
        map.setPadding(0, obtainActionBarHeight(), 0, 0);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                TextView tv = (TextView) findViewById(R.id.location_name);
                tv.setText(marker.getTitle());
                current_id = marker.getSnippet();
                listView = (ListView) findViewById(R.id.guide_list);
                listView.setAdapter(null);
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
    }

    private void moveNow(GoogleMap map) {
        GPSTracker gps = new GPSTracker(UpdateRouteActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        NOW = new LatLng(gps.getLatitude(),gps.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 16));
    }

    public int obtainActionBarHeight() {
        int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int textSize = a.getDimensionPixelSize(0, -1);
        a.recycle();

        return textSize;
    }
}
