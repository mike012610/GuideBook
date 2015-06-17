package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadRouteActivity extends BaseActivity implements OnMapReadyCallback {

    private String lat;
    private String lng;
    private NodeInfo[] rec;

    private Intent intent;
    private Bundle bundle;
    private Marker current_Marker;
    private int idrec;
    private String current_id;
    private Map<Marker, NodeInfo> rec3 = new HashMap<Marker, NodeInfo>();

    public static int a = 10;
    public ListView listView;
    private ArrayAdapter listAdapter;
    //private RecFragment tmp;

    private LatLng NOW = null;
    private GoogleMap map;
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
    private String guide_id;
    private boolean expanded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_route);
        onCreateDrawer(savedInstanceState);
        a=3;
        slide_Layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slide_Layout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
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
        intent = this.getIntent();
        bundle = intent.getExtras();
        guide_id = bundle.getString("id");
        lat = bundle.getString("lat");
        lng = bundle.getString("lng");
        NOW = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
        listView = (ListView) findViewById(R.id.list_view2);

        MapFragment mapfrag = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapfrag.getMap();
        map.setMyLocationEnabled(true);
        //map.setPadding(0, obtainActionBarHeight(), 0, 0);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                current_Marker = marker;
                TextView tv = (TextView) findViewById(R.id.order);
                tv.setText(String.valueOf(rec3.get(marker).order));
                EditText et = (EditText) findViewById(R.id.new_node_name);
                et.setText(rec3.get(marker).name);
                et = (EditText) findViewById(R.id.new_node_intro);
                et.setText(rec3.get(marker).info);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                map.setOnMapClickListener(panel_disable);
                return true;
            }
        });
        mapfrag.getMapAsync(this);
        get_route();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(expanded == false) {
                finish();
                return false;
            }
            else {
                slide_Layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                expanded = false;
            }
        }
        return false;
    }
    @Override
    protected void onCreateDrawer(Bundle savedInstanceState)
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(R.string.menu);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //mDragListView.setPadding(0, obtainActionBarHeight(), 0, 0);
        /*
        ((FrameLayout)findViewById(R.id.container)).setPadding(0, obtainActionBarHeight(), 0, 0);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        tmp = RecFragment.newInstance();
        transaction.replace(R.id.container, tmp, "fragment").commit();
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }
    public void get_route() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("guide_id", guide_id);
        HttpMethod conn = new HttpMethod("http://140.112.31.159/db/getroute",params);
        new GetRoute().execute(conn);
    }

    private class GetRoute extends AsyncTask<HttpMethod,Integer,String> {

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
            LatLng[] route_rec=new LatLng[input.length()];
            JSONObject tmp;
            double lat;
            double lng;
            String name;
            int order;
            String info;
            Marker marker;
            LatLng begin=null,end=null;
            String[] mItemArray = new String[input.length()];;
            rec = new NodeInfo[input.length()];
            for(int i=0; i< input.length();i++)
            {
                try {
                    tmp = input.getJSONObject(i).getJSONObject("fields");
                    name = tmp.getString("name");
                    lat = Double.parseDouble(tmp.getString("lat"));
                    lng = Double.parseDouble(tmp.getString("lng"));
                    order = Integer.parseInt(tmp.getString("order"));
                    info = tmp.getString("info");
                    marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
                    rec[order] = new NodeInfo(i,name,tmp.getString("lat"),tmp.getString("lng"),info,order);
                    rec3.put(marker, rec[order]);
                    mItemArray[order]=rec[order].name;
                    route_rec[order] = new LatLng(lat,lng);
                }catch(Exception e){}
            }
            listAdapter = new ArrayAdapter(getActionBar().getThemedContext(),android.R.layout.simple_list_item_1,mItemArray);
            listView.setAdapter(listAdapter);
            for(int i=0;i<input.length()-1;i++) {
                String url =  getDirectionsUrl(route_rec[i], route_rec[i+1]);
                new DownloadTask().execute(url);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    drawerLayout.closeDrawers();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(rec[position].lat),Double.parseDouble(rec[position].lng)), 16));
                }
            });

        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        moveNow(map);
    }

    private void moveNow(GoogleMap map) {
        GPSTracker gps = new GPSTracker(LoadRouteActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 16));
    }

/////////////////////////////////////

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        //waypoints,116.32885,40.036675

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
                + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        System.out.println("url:" + strUrl + "---->   downloadurl:" + data);
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                System.out.println("do in background:" + routes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(3);

                // Changing the color polyline according to the mode
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    public class DirectionsJSONParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and
         * longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return routes;
        }

        /**
         * Method to decode polyline points Courtesy :
         * jeffreysambells.com/2010/05/27
         * /decoding-polylines-from-google-maps-direction-api-with-java
         * */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }
}
