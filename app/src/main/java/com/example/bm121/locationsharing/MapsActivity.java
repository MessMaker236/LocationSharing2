package com.example.bm121.locationsharing;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bm121.locationsharing.Models.Point;
import com.example.bm121.locationsharing.Models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private TextView headerName;

    private DatabaseReference mDatabase;
    private static final String TAG = "NewPostActivity";
    private String username;
    private String useremail;

    String TITLES[] = {"Map","Events","Mail","Shop","Travel"};
    int ICONS[] = {R.drawable.ic_map_black_24dp,android.R.drawable.ic_input_delete,android.R.drawable.ic_menu_call,android.R.drawable.ic_media_play,android.R.drawable.ic_btn_speak_now};

    //String NAME = "Ben Messing";
    String EMAIL = "bm121512@gmail.com";
    int PROFILE = R.drawable.blank_profile_picture;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //create a reference to the database so we can read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //create instance of database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //create a reference to database "points" branch
        DatabaseReference pointsRef = database.getReference("points");

        //method to fetch the username and email
        getUserData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set the toolbar to the one i designed
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set some Nav Drawer Values
        //mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //headerName= (TextView)findViewById(R.id.header_name);


        //listens for open and close events
        mDrawerToggle = new ActionBarDrawerToggle(
                this, //host activity
                mDrawerLayout, //Drawerlayout object
                //R.drawable.ic_drawer, //nav drawer image to replace up caret (needed for appcompat v4
                R.string.drawer_open,   /* "open drawer" description for accessibility */
                R.string.drawer_close) { /* "close drawer" description for accessibility */

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
       // mDrawerList.setAdapter(new ArrayAdapter<String>(this,
       //         R.layout.drawer_list_item, R.id.drawer_itemName, mDrawerTitles));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //listens for any kind of event in database
        pointsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                // A new point has been added, add it to the displayed map
                Point point = dataSnapshot.getValue(Point.class);
                Marker p = plotPoint(point.lat, point.lon, point.author, "red");
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                //called when a child is changed
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                //called when a child is changed
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //called when a point is removed
                //TODO: USE THIS!
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    // Swaps fragments in the main content view
    private void selectItem(int position) {

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen();
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    //used to inflate the custom app/action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }
    //handles user interactions with app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void getUserData() {
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(MapsActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            username = user.username;
                            useremail = user.email;
                            //Say welcome
                            Toast.makeText(MapsActivity.this,
                                    "Welcome " + username,
                                    Toast.LENGTH_SHORT).show();
                            mRecyclerView = (RecyclerView) findViewById(R.id.DrawerRecyclerView); // Assigning the RecyclerView Object to the xml View
                            mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
                            mAdapter = new MyAdapter(TITLES,ICONS,username,useremail,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
                            // And passing the titles,icons,header view name, header view email,
                            // and header view profile picture
                            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
                            mLayoutManager = new LinearLayoutManager(MapsActivity.this);                 // Creating a layout Manager
                            mRecyclerView.setLayoutManager(mLayoutManager);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    //Will manipulate the map once, when it is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //set a listener for map clicks
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            //called when map clicked
            public void onMapClick(final LatLng point){
                final String userId = getUid();
                writeNewPoint(userId, username, "red", point.latitude, point.longitude);
                Toast.makeText(MapsActivity.this,
                        "username =  "  + username + ", email: " + useremail,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void writeNewPoint(String userId, String username, String color, double lat, double lon) {
        // Create new post at /user-points/$userid/$pointid and at
        String key = mDatabase.child("points").push().getKey();
        Point point = new Point(userId, username, color, lat, lon);
        Map<String, Object> pointValues = point.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/points/" + key, pointValues);
        childUpdates.put("/user-points/" + userId + "/" + key, pointValues);

        mDatabase.updateChildren(childUpdates);
    }

    public Marker plotPoint(Double lat, Double lon, String author, String color){
        Log.e(TAG, "plotting point");
        Marker pp = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(author));
        return pp;
    }
}