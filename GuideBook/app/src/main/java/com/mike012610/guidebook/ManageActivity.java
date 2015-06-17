package com.mike012610.guidebook;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ManageActivity extends BaseActivity{

    private ListView listView;
    private SimpleAdapter adapter;
    private TextView nodata;
    private Button makeGuide;
    private int rec_num = 0;
    private String[] idrec = new String[30];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        super.onCreateDrawer(savedInstanceState);
        listView = (ListView)findViewById(R.id.listView_manage);
        listView.setPadding(0, obtainActionBarHeight(), 0, 0);
        GetUserGuide(((Account) getApplicationContext()).id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ManageActivity.this, GuideInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", idrec[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return true;
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
        builder.setMessage("確定要退出嗎?");
        builder.setTitle("提示");
        builder.setPositiveButton("確認",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ManageActivity.this.finish();
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
            dialog();
            return false;
        }
        return false;
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

    public void GetUserGuide(String id){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",id);
        HttpMethod conn = new HttpMethod("http://140.112.31.159/db/getuserguide",params);
        new get_user_guide().execute(conn);
    }

    private class get_user_guide extends AsyncTask<HttpMethod,Integer,String> {

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
            rec_num = 0;
            final String ID_TITLE = "NAME", ID_SUBTITLE = "LOCATION";
            ArrayList<HashMap<String,String>> guidelist = new ArrayList<HashMap<String,String>>();
            for(int i=0; i< input.length();i++)
            {
                try {
                    tmp = input.getJSONObject(i).getJSONObject("fields");
                    HashMap<String,String> item = new HashMap<String,String>();
                    item.put(ID_TITLE,tmp.getString("name"));
                    item.put(ID_SUBTITLE, tmp.getString("location_name"));
                    guidelist.add(item);
                    idrec[i] =  input.getJSONObject(i).getString("pk");
                    rec_num += 1;
                }catch(Exception e){}
            }
            adapter = new SimpleAdapter(
                    drawerLayout.getContext(),
                    guidelist,
                    android.R.layout.simple_list_item_2,
                    new String[] { "NAME","LOCATION" },
                    new int[] { android.R.id.text1, android.R.id.text2 } );
            listView = (ListView) findViewById(R.id.listView_manage);
            listView.setAdapter(adapter);
        }
    }
}
