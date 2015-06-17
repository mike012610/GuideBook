package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GuideInfoActivity extends Activity{
    private LinearLayout view;
    private Intent intent;
    private Bundle bundle;
    private String guide_id;
    private String lat;
    private String lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_info);

        view = (LinearLayout) findViewById(R.id.guide_info_view);
        intent = this.getIntent();
        bundle = intent.getExtras();
        guide_id = bundle.getString("id");
        getguideinfo();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guide_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.see_route) {
            Intent intent = new Intent();
            intent.setClass(GuideInfoActivity.this, UpdateRouteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", guide_id);
            bundle.putString("lat", lat);
            bundle.putString("lng", lng);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.update_route) {
            Intent intent = new Intent();
            intent.setClass(GuideInfoActivity.this, UpdateRouteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", guide_id);
            bundle.putString("lat", lat);
            bundle.putString("lng", lng);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getguideinfo(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",guide_id);
        HttpMethod conn = new HttpMethod("http://140.112.31.159:8000/db/getguideinfo",params);
        new get_guideinfo().execute(conn);
    }

    private class get_guideinfo extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jObj=null;
            if(result != null) {
                try {
                    jObj = new JSONObject(result);
                    handle_jobj(jObj);
                }catch(Exception e){throw new RuntimeException(e);
                }
            }
        }

        private void handle_jobj(JSONObject input){
            try {
                JSONObject tmp = input.getJSONObject("fields");
                ((TextView) findViewById(R.id.guide_name)).setText(tmp.getString("name"));
                ((TextView) findViewById(R.id.guide_author)).setText(tmp.getString("author_name"));
                ((TextView) findViewById(R.id.guide_location)).setText(tmp.getString("location_name"));
                ((TextView) findViewById(R.id.guide_intro)).setText(tmp.getString("intro"));
                lat=tmp.getString("lat");
                lng=tmp.getString("lng");
            }catch(Exception e){}
        }
    }
}
