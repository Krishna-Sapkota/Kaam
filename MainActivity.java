package com.project.krishna.kaam;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.project.krishna.kaam.navigations.AllTask;
import com.project.krishna.kaam.navigations.CompletedTask;
import com.project.krishna.kaam.navigations.TodayTask;

import net.danlew.android.joda.JodaTimeAndroid;

public class MainActivity extends AppCompatActivity {
    private static final String SAVED_FRAG = "FRAG";
    private Toolbar toolbar;
    private FloatingActionButton addTaskButton;
    BottomNavigationView navigation;
    AllTask allTask, tTask, cTask;
    String currentFragment;
    boolean activityCreated = false;
    Bundle fragmentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);
        addTaskButton = findViewById(R.id.fab);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        allTask = new AllTask();
        tTask = new TodayTask();
        cTask = new CompletedTask();
        activityCreated = false;

        fragmentState = savedInstanceState;

        currentFragment = getString(R.string.screen_all);


        if (savedInstanceState != null) {
            activityCreated = true;
            AllTask savedFrag;
            savedFrag = (AllTask) getSupportFragmentManager().getFragment(savedInstanceState, SAVED_FRAG);

            if (savedFrag.getTag().equals(getString(R.string.screen_all))) {
                allTask = savedFrag;
                loadFragment(allTask, savedFrag.getTag());

            }
            if (savedFrag.getTag().equals(getString(R.string.screen_today))) {
                tTask = savedFrag;
                loadFragment(tTask, savedFrag.getTag());

            }
            if (savedFrag.getTag().equals(getString(R.string.screen_com))) {

                cTask = savedFrag;
                loadFragment(cTask, savedFrag.getTag());

            }
        }


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskActivity = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskActivity);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));


        navigation.setOnNavigationItemSelectedListener(new NavigationListener());

        toolbar.setTitle(getString(R.string.all_title));
        if (savedInstanceState == null)
            loadFragment(allTask, getString(R.string.screen_all));


    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment, tag);
        transaction.commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AllTask all = (AllTask) getSupportFragmentManager().findFragmentByTag(getString(R.string.screen_all));
        TodayTask today = (TodayTask) getSupportFragmentManager().findFragmentByTag(getString(R.string.screen_today));
        CompletedTask com = (CompletedTask) getSupportFragmentManager().findFragmentByTag(getString(R.string.screen_com));
        if (all != null && all.isVisible()) {
            allTask = all;
        }
        if (today != null && today.isVisible()) {
            allTask = today;
        }
        if (com != null && com.isVisible()) {
            allTask = com;

        }
        Log.i("ONSAVE", "ONSAVEINSTANCESTATE");

        getSupportFragmentManager().putFragment(outState, SAVED_FRAG, allTask);


    }


    class NavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            if (navigation.getSelectedItemId() != item.getItemId()) {

                switch (item.getItemId()) {
                    case R.id.navigation_all:
                        toolbar.setTitle(getString(R.string.title_all));

                        loadFragment(allTask, getString(R.string.screen_all));


                        return true;

                    case R.id.navigation_today:

                        toolbar.setTitle(getString(R.string.screen_today));

                        loadFragment(tTask, getString(R.string.screen_today));


                        return true;

                    case R.id.navigation_complete:
                        toolbar.setTitle(getString(R.string.screen_com));


                        loadFragment(cTask, getString(R.string.screen_com));

                        return true;

                }
            }
            return false;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (item.getItemId() == R.id.settings) {
            Intent settingsActivity = new Intent(this, PreferenceActivity.class);
            startActivity(settingsActivity);
        }
        if (item.getItemId() == R.id.about) {
            Intent settingsActivity = new Intent(this, About.class);
            startActivity(settingsActivity);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        allTask = new AllTask();
        cTask = new CompletedTask();
        tTask = new TodayTask();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }


}

