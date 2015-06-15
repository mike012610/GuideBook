package com.mike012610.guidebook;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback{
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
    private Marker new_loc=null;

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
                TextView tv = (TextView) findViewById(R.id.location_name);
                tv.setText(marker.getTitle());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                map.setOnMapClickListener(panel_disable);
                return true;
            }
        });

        mapfrag.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(resultCode){
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                Toast.makeText(drawerLayout.getContext(), "success", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        moveNow(map);
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
                            if(new_loc!=null)
                                new_loc.remove();
                            new_loc = map.addMarker(new MarkerOptions().position(point).title(String.valueOf(count)).snippet("test"));
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
            case R.id.add:
                if(new_loc==null)
                    break;
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, MakeLocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("lat", String.valueOf(new_loc.getPosition().latitude));
                bundle.putString("lng", String.valueOf(new_loc.getPosition().longitude));
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
                break;
            case R.id.search:
                LatLng center = map.getCameraPosition().target;
                String lat = String.valueOf(center.latitude);
                String lng = String.valueOf(center.longitude);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat",lat);
                params.put("lng",lng);
                HttpMethod conn = new HttpMethod("http://140.112.31.159:8000/db/localsearch",params);
                new search().execute(conn);
                break;
            default:
                break;
        }
        return true;
    }
    private class search extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONArray jArray=null;
            String test = convertStandardJSONString(result);
            if(result != null) {
                try {
                    jArray = new JSONArray(test);
                    handle_jarray(jArray);
                }catch(Exception e){throw new RuntimeException(e);
                }
            }
            int a = 3;
            int b = 4;
        }

        private void handle_jarray(JSONArray input){
            JSONObject tmp;
            double lat;
            double lng;
            String name;
            String id;
            for(int i = 0;i<marker_num;i++)
                search_result[i].remove();
            marker_num = 0;
            for(int i=0; i< input.length();i++)
            {
                try {
                    tmp = input.getJSONObject(i).getJSONObject("fields");
                    name = tmp.getString("name");
                    lat = Double.parseDouble(tmp.getString("lat"));
                    lng = Double.parseDouble(tmp.getString("lng"));
                    id = input.getJSONObject(i).getString("pk");
                    search_result[i] = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name).snippet(id));
                    marker_num++;
                }catch(Exception e){}
            }

        }
    }

    public String convertStandardJSONString(String origin){
        String data_json = origin.replace("\\u", "#u");
        data_json = data_json.replace("\\", "");
        data_json = data_json.replace(" ", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        data_json = data_json.replace("\"[", "[");
        data_json = data_json.replace("]\",", "],");
        data_json = data_json.replace("]\"", "]");
        data_json = data_json.replace("#u", "\\u");
        return data_json;
    }
}
