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
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MakeGuideActivity extends Activity {

    private Button setRoute;
    private String guide_name;
    private String guide_intro;
    private String author;
    private String location_id;
    private Bundle bundle;
    private Intent intent;
    private LinearLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_guide);

        view = (LinearLayout) findViewById(R.id.new_guide_view);
        intent = this.getIntent();
        bundle = intent.getExtras();
        author = ((Account)getApplicationContext()).id;
        location_id = bundle.getString("id");

        setRoute = (Button)findViewById(R.id.setRoute);
        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = ((Account)getApplicationContext()).account;
                SetGuideInfo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_guide, menu);
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

    public void SetGuideInfo(){
        guide_name = toUtf8(((EditText)findViewById(R.id.new_guide_name)).getText().toString());
        guide_intro = toUtf8(((EditText)findViewById(R.id.new_guide_intro)).getText().toString());

        Map<String, String> params = new HashMap<String, String>();
        params.put("name",guide_name);
        params.put("intro",guide_intro);
        params.put("location_id",location_id);
        params.put("author_id",author);
        HttpMethod conn = new HttpMethod("http://140.112.31.159/db/setguideinfo",params);
        new setGuideInfoTask().execute(conn);
    }

    private class setGuideInfoTask extends AsyncTask<HttpMethod,Integer,String> {

        @Override
        protected String doInBackground(HttpMethod... param) {
            String ans = param[0].connect();
            return ans;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                MakeGuideActivity.this.setResult(RESULT_OK, intent);
                MakeGuideActivity.this.finish();
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
