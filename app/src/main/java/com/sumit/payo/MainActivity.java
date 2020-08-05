package com.sumit.payo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static MainActivity inst;
//    ArrayList<String> smsMessagesList = new ArrayList<String>();
//    ListView smsListView;
//    ArrayAdapter arrayAdapter;
//    TextView expense, income;
//    Double totalExpense, totalIncome;

    public static MainActivity instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);



//        smsListView = (ListView) findViewById(R.id.SMSList);
//        expense = findViewById(R.id.txtExpense);
//        income = findViewById(R.id.txtIncome);
//        totalExpense = 0.0;
//        totalIncome = 0.0;
//
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
//        smsListView.setAdapter(arrayAdapter);


        // Add SMS Read Permision At Runtime
        // Todo : If Permission Is Not GRANTED
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            // Todo : If Permission Granted Then Show SMS
            //refreshSmsInbox();
            setTitle("Dashboard");
            Fragment fragment = new Dashboard();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment,null).commit();

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();


        NavigationView nv= (NavigationView) findViewById(R.id.nav_view1);
        Menu m=nv.getMenu();

        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.dashboard) {
            fragmentClass= Dashboard.class;
            setTitle("Dashboard");
        }
        else if (id == R.id.monthlyReport) {
            fragmentClass= MonthlyReport.class;
            setTitle("Monthly Report");
        }
        else if (id == R.id.dailyReport) {
            fragmentClass= DailyReport.class;
            setTitle("Daily Report");
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
            drawer.closeDrawer(GravityCompat.START);


        } catch (Exception x) {
            x.printStackTrace();
        }
        return true;
    }




}