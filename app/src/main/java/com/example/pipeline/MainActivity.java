package com.example.pipeline;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.pipeline.db.Events;
import com.example.pipeline.db.EventsAdapter;
import com.example.pipeline.db.EventsReaderDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private EventsAdapter _Adapter;
    private EventsReaderDbHelper _Database = new EventsReaderDbHelper(this);
    private ArrayList<Events> allEvents = new ArrayList<>();

    RecyclerView eventView;
    CoordinatorLayout cLayout;
    LinearLayoutManager linearLayoutManager;


    //method responsible for creating the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //card view initialisation
        cLayout = (CoordinatorLayout) findViewById(R.id.listDock); //layout component for displaying card within the activity
        //recycler view used to fill the  cLayout with dynamic content (depending on events stored)
        eventView = (RecyclerView) findViewById(R.id.event_list);

        linearLayoutManager = new LinearLayoutManager(this); //instantiated to position events within the recycler view
        eventView.setLayoutManager(linearLayoutManager);
        eventView.setHasFixedSize(true);

        //setting the Material toolbar in the layout as the app bar for the activity
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        //assigning Drawer Layout UI component defined in the layout
        drawer = findViewById(R.id.drawer_layout);

        //Floating Action Button initialisation
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        //Event handler for when FAB is clicked within activity
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*passing intent to AddSelectEditActivityHost activity, which will host the necessary fragment (in this case
                AddEventFragment) */
                Intent intent = new Intent(MainActivity.this, AddSelectEditActivityHost.class);
                startActivity(intent); //
            }
        });

        //This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer is open.
        //It animates between these two states as the drawer opens
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle); //adding the toggle to the drawer layout (containing the navigation view)
        toggle.syncState();

        //assigning Navigation View UI component defined in the layout
        NavigationView nav = (NavigationView)findViewById(R.id.navigation);
        //Event handler for when menu item is clicked within the NavigationView
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), ProductivityStatsActivity.class);
                startActivity(intent); //passing intent to class ProductivityStatsActivity and starting activity
                return true;
            }
        });
        nav.setItemIconTintList(null);

    }

    //handles when pressing the device's back button
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); //if drawer with nev view is open, when pressing back, drawer is closed
        } else {
            super.onBackPressed();
        }
    }

    //handles what happens after activity is started, or when back in resume state after exiting paused state
    @Override
    public void onResume() {
        super.onResume();
        //calling listEvents method in db helper and setting allEvents' value to listEvents return
        allEvents = _Database.listEvents();

        //if events are found
        if (allEvents.size() > 0) {
            eventView.setVisibility(View.VISIBLE); //making recycler view visible
            //Populating EventsAdapter (check class EventsAdapter) with events found
            _Adapter = new EventsAdapter(this, allEvents);

            //setting events adapter as an adapter for the recycler view to be able to display all events
            eventView.setAdapter(_Adapter);

        } else {
            eventView.setVisibility(View.GONE); //disabling visibility of recycler view
            //displaying toast notification to show that no events are present
            Toast.makeText(this, "You currently have no events. Start adding now", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating SearchView (with Search drawable icon) in the toolbar - SearchView found in menu --> menu_search.xml
        getMenuInflater().inflate(R.menu.menu_search, menu);

        //setting the search component of id search to MenuItem attribute named search
        MenuItem search = menu.findItem(R.id.search);
        //returning currently set action view (which provides search functionality within app bar, in our case)
        SearchView searchView = (SearchView) search.getActionView(); //(SearchView) MenuItemCompat.getActionView(search); is DEPRECATED
        search(searchView); //passing the SearchView as an argument to method search
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //if events are found in EventsAdapter
                if (_Adapter != null)
                    //when text changes from search view, the new text is passed to filter, getting new records frm db for event searched
                    _Adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    //handles configuration changes - ex.switching from portrait to landscape view
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

