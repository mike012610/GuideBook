package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MakeLocationActivity extends Activity {

    private String lat;
    private String lng;
    private String name;
    private Button bt;
    private LinearLayout view;
    private Intent intent;
    private Bundle bundle;
    private TextView latView;
    private TextView lngView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_location);

        intent = this.getIntent();
        bundle = intent.getExtras();
        latView = (TextView)findViewById(R.id.location_new_lat);
        latView.setText(bundle.getString("lat"));
        lngView = (TextView)findViewById(R.id.location_new_lng);
        lngView.setText(bundle.getString("lng"));

        view = (LinearLayout) findViewById(R.id.new_location_view);
        bt = (Button) findViewById(R.id.new_location_submit);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat = latView.getText().toString();
                lng = lngView.getText().toString();
                name = toUtf8(((EditText)findViewById(R.id.location_new_name)).getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("lat",lat);
                params.put("lng",lng);
                HttpMethod conn = new HttpMethod("http://140.112.31.159:8000/db/makelocation",params);
                new MakeLocation().execute(conn);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MakeLocation extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                MakeLocationActivity.this.setResult(RESULT_OK, intent);
                MakeLocationActivity.this.finish();
            }
            else
                Toast.makeText(view.getContext(), "!!!!!", Toast.LENGTH_LONG).show();
        }
    }

    public static String toUtf8(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        }
        catch(Exception e){}
        return null;
    }
}
