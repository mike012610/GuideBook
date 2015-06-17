package com.mike012610.guidebook;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback{

    private ListView listView;
    private SimpleAdapter adapter;
    private TextView nodata;
    private Button makeGuide;
    private String current_id;
    private String ref_lat;
    private String ref_lng;
    private boolean expanded = false;
    private int[] id_tmprec;
    private String[] aid_tmprec;

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
        slide_Layout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelExpanded(View panel) {
               expanded = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                expanded = false;
            }

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }
            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }

        });
        makeGuide = (Button) findViewById(R.id.make_guide);
        makeGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, MakeGuideActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", current_id);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });

        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                nodata = (TextView) findViewById(R.id.no_data);
                nodata.setVisibility(View.GONE);
                TextView tv = (TextView) findViewById(R.id.location_name);
                tv.setText(marker.getTitle());
                current_id = marker.getSnippet();
                listView = (ListView) findViewById(R.id.guide_list);
                listView.setAdapter(null);
                GetLoction(current_id);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                ref_lat = String.valueOf(marker.getPosition().latitude);
                ref_lng = String.valueOf(marker.getPosition().longitude);
                slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                map.setOnMapClickListener(panel_disable);
                return true;
            }
        });

        mapfrag.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(resultCode){
            case RESULT_OK:
                if(requestCode == 0) {
                    Bundle bundle = data.getExtras();
                    Toast.makeText(drawerLayout.getContext(), "success", Toast.LENGTH_LONG).show();
                    break;
                }
                else if(requestCode == 1) {
                    Bundle bundle = data.getExtras();
                    Toast.makeText(drawerLayout.getContext(), "success2", Toast.LENGTH_LONG).show();
                    break;
                }
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

    protected void dialog() {
        AlertDialog.Builder builder = new Builder(MapsActivity.this);
        builder.setMessage("確定要退出嗎?");
        builder.setTitle("提示");
        builder.setPositiveButton("確認",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MapsActivity.this.finish();
                        //onBackPressed ();
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(expanded == false) {
                dialog();
                return false;
            }
            else {
                slide_Layout.setPanelState(PanelState.COLLAPSED);
                expanded = false;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
                NOW = map.getCameraPosition().target;
                String lat = String.valueOf(NOW.latitude);
                String lng = String.valueOf(NOW.longitude);
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat",lat);
                params.put("lng",lng);
                HttpMethod conn = new HttpMethod("http://140.112.31.159/db/localsearch",params);
                new search().execute(conn);
                break;
            default:
                break;
        }
        return true;
    }

    public void GetLoction(String id){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",id);
        HttpMethod conn = new HttpMethod("http://140.112.31.159/db/getlocationguide",params);
        new get_location_guide().execute(conn);
    }

    private class get_location_guide extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONArray jArray=null;
            if(result != null) {
                try {
                    jArray = new JSONArray(result);
                    handle_jarray(jArray);
                }catch(Exception e){throw new RuntimeException(e);
                }
            }
        }

        private void handle_jarray(JSONArray input){
            JSONObject tmp;
            if(input.length()==0) {
                nodata = (TextView) findViewById(R.id.no_data);
                nodata.setVisibility(View.VISIBLE);
                return;
            }
            id_tmprec = new int[input.length()];
            aid_tmprec = new String[input.length()];
            final String ID_TITLE = "NAME", ID_SUBTITLE = "AUTHOR";
            ArrayList<HashMap<String,String>> guidelist = new ArrayList<HashMap<String,String>>();
            for(int i=0; i< input.length();i++)
            {
                try {
                    tmp = input.getJSONObject(i).getJSONObject("fields");
                    HashMap<String,String> item = new HashMap<String,String>();
                    item.put(ID_TITLE,tmp.getString("name"));
                    item.put(ID_SUBTITLE,tmp.getString("author_name"));
                    id_tmprec[i] = Integer.parseInt(input.getJSONObject(i).getString("pk"));
                    aid_tmprec[i] = tmp.getString("author_id");
                    guidelist.add(item);
                }catch(Exception e){}
            }
            adapter = new SimpleAdapter(
                    slide_Layout.getContext(),
                    guidelist,
                    android.R.layout.simple_list_item_2,
                    new String[] { "NAME","AUTHOR" },
                    new int[] { android.R.id.text1, android.R.id.text2 } );
            listView = (ListView) findViewById(R.id.guide_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.setClass(MapsActivity.this, GuideInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", String.valueOf(id_tmprec[position]));
                    bundle.putString("author_id", aid_tmprec[position]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
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
            String test = result;
            if(result != null) {
                try {
                    jArray = new JSONArray(test);
                    handle_jarray(jArray);
                }catch(Exception e){throw new RuntimeException(e);
                }
            }
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

}
