package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateRouteActivity extends BaseActivity implements OnMapReadyCallback {

    private String lat;
    private String lng;

    private Intent intent;
    private Bundle bundle;
    private Marker current_Marker;
    private int idrec;
    private String current_id;
    private Map<Marker, String> rec = new HashMap<Marker, String>();
    private Map<String, NodeInfo> rec2 = new HashMap<String, NodeInfo>();
    private Map<Marker, NodeInfo> rec3 = new HashMap<Marker, NodeInfo>();
    private Map<NodeInfo, Marker> rec4 = new HashMap<NodeInfo, Marker>();

    public static int a = 10;
    public DragListView mDragListView;
    //private RecFragment tmp;
    private GoogleMap.OnMapClickListener origin = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {
            Marker tmp_mark = map.addMarker(new MarkerOptions().position(point).title(String.valueOf(marker_num)));
            NodeInfo tmp_node = new NodeInfo(idrec,tmp_mark.getTitle(), String.valueOf(point.latitude), String.valueOf(point.longitude), "",marker_num);
            rec.put(tmp_mark, tmp_mark.getTitle());
            rec2.put(tmp_mark.getTitle(), tmp_node);
            rec3.put(tmp_mark,tmp_node);
            rec4.put(tmp_node,tmp_mark);
            marker_num += 1;
            idrec+=1;
        }
    };

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
            map.setOnMapClickListener(origin);
        }
    };
    private String guide_id;
    private boolean expanded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_route);
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
        Button bt = (Button) findViewById(R.id.new_node_save);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp_name="";
                String tmp_info="";

                EditText et = (EditText) findViewById(R.id.new_node_name);
                tmp_name = et.getText().toString();
                et = (EditText) findViewById(R.id.new_node_intro);
                tmp_info = et.getText().toString();
                NodeInfo tmp_node = rec3.get(current_Marker);
                rec4.remove(tmp_node);
                tmp_node.name = tmp_name;
                tmp_node.info = tmp_info;
                rec3.put(current_Marker,tmp_node);
                rec4.put(tmp_node,current_Marker);

                String old_title = rec.get(current_Marker);
                rec.put(current_Marker,tmp_name);

                rec2.remove(old_title);
                rec2.put(tmp_name, tmp_node);

                Toast.makeText(UpdateRouteActivity.this, "save success", Toast.LENGTH_SHORT).show();
            }
        });

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

        map.setOnMapClickListener(origin);

        mapfrag.getMapAsync(this);
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
        layers = getResources().getStringArray(R.array.drag_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        layers = getResources().getStringArray(R.array.drawer_menu);
        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView)
            {
                addlist();
                getActionBar().setTitle(R.string.menu);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDragListView = (DragListView) findViewById(R.id.drag_list_view);
        //mDragListView.setPadding(0, obtainActionBarHeight(), 0, 0);
        mDragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                Marker tmp_marker;
                NodeInfo[] tmp_rec = new NodeInfo[marker_num];
                int i = 0;
                for (NodeInfo value : rec3.values()) {
                    tmp_rec[i] = value;
                    i++;
                }
                for (NodeInfo value : tmp_rec) {
                    if (value.order < fromPosition && value.order >= toPosition) {
                        tmp_marker = rec4.get(value);
                        rec4.remove(value);
                        rec3.remove(tmp_marker);
                        rec2.remove(value.name);
                        value.order += 1;
                        rec4.put(value, tmp_marker);
                        rec3.put(tmp_marker, value);
                        rec2.put(value.name, value);
                    } else if (value.order > fromPosition && value.order <= toPosition) {
                        tmp_marker = rec4.get(value);
                        rec4.remove(value);
                        rec3.remove(tmp_marker);
                        rec2.remove(value.name);
                        value.order -= 1;
                        rec4.put(value, tmp_marker);
                        rec3.put(tmp_marker, value);
                        rec2.put(value.name, value);
                    } else if (value.order == fromPosition) {
                        tmp_marker = rec4.get(value);
                        rec4.remove(value);
                        rec3.remove(tmp_marker);
                        rec2.remove(value.name);
                        value.order = toPosition;
                        rec4.put(value, tmp_marker);
                        rec3.put(tmp_marker, value);
                        rec2.put(value.name, value);
                    }
                }
                int b = 0;
            }
        });
        mDragListView.setCustomDragItem(new MyDragItem(UpdateRouteActivity.this, R.layout.list_item));
        mDragListView.setLayoutManager(new LinearLayoutManager(UpdateRouteActivity.this));
        mDragListView.setCanDragHorizontally(false);
        /*
        ((FrameLayout)findViewById(R.id.container)).setPadding(0, obtainActionBarHeight(), 0, 0);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        tmp = RecFragment.newInstance();
        transaction.replace(R.id.container, tmp, "fragment").commit();
        */
    }

    public void addlist(){
        String[] tmp = new String[marker_num];
        for (NodeInfo value : rec3.values()) {
            tmp[value.order] = value.name;
        }
        ArrayList<Pair<Long, String>> mItemArray;
        mItemArray = new ArrayList<>();
        for (int i = 0; i < marker_num; i++) {
            mItemArray.add(new Pair<>(Long.valueOf(i),tmp[i]));
        }
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.image, false);
        mDragListView.setAdapter(listAdapter, true);
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
        switch (item.getItemId()) {
            case R.id.update_route:
                update_route();
                finish();
                break;
            default:
                break;
        }
        return true;
    }
    public void NodetoJson(JSONObject in,NodeInfo in2){
        try {
            in.put("name", in2.name);
            in.put("lat", in2.lat);
            in.put("lng", in2.lng);
            in.put("order", String.valueOf(in2.order));
            in.put("info", in2.info);
        }catch(Exception e){throw (RuntimeException)e;}
    }

    public void update_route() {
        NodeInfo[] tmp_rec = new NodeInfo[marker_num];
        JSONObject tmp = new JSONObject();
        JSONArray jarray = new JSONArray();
        int i = 0;
        for (NodeInfo value : rec3.values()) {
            tmp = new JSONObject();
            NodetoJson(tmp,value);
            jarray.put(tmp);
        }
        String in = jarray.toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("data",in);
        params.put("guide_id", guide_id);
        params.put("author_id",((Account)getApplicationContext()).id);
        //params.put("lng",lng);
        HttpMethod conn = new HttpMethod("http://140.112.31.159/db/setroute",params);
        new UpdateRoute().execute(conn);
    }

    private class UpdateRoute extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
        }
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
    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
        }
    }


}
