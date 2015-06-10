package com.mike012610.guidebook;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;

/**
 * Created by mike012610 on 2015/6/7.
 */
public class BaseActivity extends Activity
{
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] layers;
    private ActionBarDrawerToggle drawerToggle;
    private Map map;
    private static int now = 0;

    protected void onCreateDrawer(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        layers = getResources().getStringArray(R.array.drawer_menu);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setPadding(0, obtainActionBarHeight(), 0, 0);
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


        //View header = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
        //drawerList.addHeaderView(header, null, false);
        drawerList.setAdapter(new ArrayAdapter<String>(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, android.R.id.text1,layers));
        //View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
        //        R.layout.drawer_list_footer, null, false);
        //drawerList.addFooterView(footerView);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                select_activity(pos);
                //map.drawerClickEvent(pos);
            }
        });
    }

    private void select_activity(int pos) {
        Intent intent = new Intent();
        if(pos == now)
            return;
        now = pos;
        switch(pos){
            case 0:
                intent.setClass(this, MapsActivity.class);
                break;
            case 1:
                intent.setClass(this, ManageActivity.class);
                break;
        }
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
