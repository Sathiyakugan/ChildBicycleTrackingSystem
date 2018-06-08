package com.blogspot.gihanmora.childbicycletrackingsystem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.HashMap;
import java.util.Map;

public class Main_Menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {
    private static final String TAG = "MainMenu";
    private ImageButton mSetLocationButton;
    private ImageButton mSetSpeedButton;
    private ImageView muserProfile;
    private TextView mUsername;
    private TextView mUserDesc;
    private FirebaseAuth mFirebaseAuth;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mlocationRequest;
    private LatLng mMyLocation = new LatLng(0.0, 0.0);
    private GoogleMap mMap;
    private org.adw.library.widgets.discreteseekbar.DiscreteSeekBar seek_bar;
    private TextView text_view_radius;
    private Button mLocationSetButton;
    private Circle circle;
    private Marker marker;
    private Marker mMarkerInitial;
    private int mCircleRadius = 10;
    private boolean flagsit = true;
    com.github.clans.fab.FloatingActionMenu materialDesignFAM;
    com.github.clans.fab.FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3, floatingActionButton4, floatingActionButton5, floatingActionButton6;
    private DatabaseReference mCustomerDetailsDatabase;
    private String userID;
    private int sppedLimit;

    private String mName;
    private String mEmail;
    private String mProfileImageUrl;

    private TextView mNameField, mEmailField;
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private int zoomLevel;
    private boolean zoomFlag = true;

    private String status;

    private int checksit;
    private LatLng childsLocation;
    private int childSpeed = 0;
    private Marker bicycylemarker;
    private int finalRadius = 0;
    private GeoQuery geoquery;
//    private RelativeLayout sit;
//    private LinearLayout notsit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "onCreate: Im in");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCustomerDetailsDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Customer")
                .child(userID);

//        sit = (RelativeLayout) findViewById(R.id.sit);
//        notsit = (LinearLayout) findViewById(R.id.notsit);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.social_floating_menu);
        floatingActionButton1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.speed);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.tip);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.help);
        materialDesignFAM.bringToFront();
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Intent intent = new Intent(Main_Menu.this, SpeedIndicator.class);
                startActivity(intent);

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked


            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked

            }
        });


        View header = navigationView.getHeaderView(0);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mEmailField = (TextView) header.findViewById(R.id.userDesc);
        mNameField = (TextView) header.findViewById(R.id.user_Name);
        mProfileImage = (de.hdodenhof.circleimageview.CircleImageView) header.findViewById(R.id.userProfile);
        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CustomerProfile = new Intent(Main_Menu.this, Customer.class);
                startActivity(CustomerProfile);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setLocationButton();
        setseekbar();

        DatabaseReference speedRef = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Maxspeed");


        speedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                sppedLimit = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        DatabaseReference bikeRef = FirebaseDatabase.getInstance().getReference().child("bike").child("status");

        bikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                status = dataSnapshot.getValue().toString();
                if (status.equals("Not sit")) {
                    Showalert("child is not Using the bicycle");
                    //
                } else {
                    checksit = Integer.parseInt(status.substring(0, 1));
                    int checkzero = Integer.parseInt(status.substring(1, 2) + "");



                    if (status.length() > 17) {
                        if (status.length() > 17 && status.length() < 20) {
                            try {
                                childSpeed = Integer.parseInt(status.substring(18)) / 100;
                            } catch (Exception e) {
                                System.out.print("Caught the NullPointerException");
                            }

                        }
                    }

                    if (checkzero != 0) {
                        if (bicycylemarker != null) {
                            bicycylemarker.remove();
                        }

                        Double lat = Double.parseDouble(status.substring(1, 9) + "");
                        Double lon = Double.parseDouble(status.substring(9, 18) + "");
                        childsLocation = new LatLng(lat, lon);

                        bicycylemarker = mMap.addMarker(new MarkerOptions().position(childsLocation)
                                .title("Child Location")
                                .snippet("Your child is here..").icon(BitmapDescriptorFactory.fromResource(R.drawable.kk)));

                        if (checksit == 1 && flagsit) {
                            flagsit = false;
                            Showalert("Child is Fallen");
                            postnotification("Im notification", "Child is Fallen");
                        }
                        if (checksit == 0) {
                            flagsit = true;
                        }


                        Location locationA = new Location("point A");

                        locationA.setLatitude(mMyLocation.latitude);
                        locationA.setLongitude(mMyLocation.longitude);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(childsLocation.latitude);
                        locationB.setLongitude(childsLocation.longitude);
                        float distance = locationA.distanceTo(locationB);
//                        float [] results = new float[5];
//                        Location.distanceBetween(mMyLocation.latitude, mMyLocation.longitude, childsLocation.latitude, childsLocation.longitude, results);


                        if (distance > finalRadius && !(finalRadius == 0) && !(distance == 0)) {
                            Showalert("Your Child is Out of boundary");
                            postnotification("Alert", "Your Child is Out of boundary");
                        }


                        if (sppedLimit < childSpeed) {
                            Showalert("Your Child is Crossing the speed limit childSpeed" + childSpeed + "sppedLimit" + sppedLimit);
                            postnotification("Alert", "Your Child is Crossing the speed limit");
                        }
                    }
                    else{
                        childsLocation = new LatLng(6.9302392, 79.8682612);
                        if (bicycylemarker != null) {
                            bicycylemarker.remove();
                        }
                        bicycylemarker = mMap.addMarker(new MarkerOptions().position(childsLocation)
                                .title("Child Location")
                                .snippet("Your child is here..").icon(BitmapDescriptorFactory.fromResource(R.drawable.kk)));

                        if (checksit == 1 && flagsit) {
                            flagsit = false;
                            Showalert("Child is Fallen");
                            postnotification("Im notification", "Child is Fallen");
                        }
                        if (checksit == 0) {
                            flagsit = true;
                        }


                        Location locationA = new Location("point A");

                        locationA.setLatitude(mMyLocation.latitude);
                        locationA.setLongitude(mMyLocation.longitude);

                        Location locationB = new Location("point B");

                        locationB.setLatitude(childsLocation.latitude);
                        locationB.setLongitude(childsLocation.longitude);

                        float distance = locationA.distanceTo(locationB);
//                        float [] results = new float[5];
//                        Location.distanceBetween(mMyLocation.latitude, mMyLocation.longitude, childsLocation.latitude, childsLocation.longitude, results);


                        if (distance > finalRadius && !(finalRadius == 0) && !(distance == 0)) {
                            Showalert("Your Child is Out of boundary");
                            postnotification("Alert", "Your Child is Out of boundary");
                        }


                        if (sppedLimit < childSpeed) {
                            Showalert("Your Child is Crossing the speed limit childSpeed" + childSpeed + "sppedLimit" + sppedLimit);
                            postnotification("Alert", "Your Child is Crossing the speed limit");
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__menu, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/376227335860239")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/karthikofficialpage")); //catches and opens a url to the desired page
        }
    }

    public static Intent getOpenTwitterIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.twitter.android", 0); //Checks if Twitter is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/drkarthiik")); //Trys to make intent with Twitter's's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/drkarthiik")); //catches and opens a url to the desired page
        }
    }

    public static Intent getOpenLinkdinIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.linkedin.android", 0); //Checks if Linkdin is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/karthikm128")); //Trys to make intent with Linkdin's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/karthikm128")); //catches and opens a url to the desired page
        }
    }

    public static Intent getOpenGPlusIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.google.android.apps.plus", 0); //Checks if G+ is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/u/0/+KarthikM128")); //Trys to make intent with G+'s URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/u/0/+KarthikM128")); //catches and opens a url to the desired page
        }
    }

    public static Intent getOpenInstagramIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.instagram.android", 0); //Checks if Instagram is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/accounts/login/")); //Trys to make intent with Instagram's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/accounts/login/")); //catches and opens a url to the desired page
        }
    }

    public static Intent getOpenYouTubeIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.google.android.youtube", 0); //Checks if YT is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/karthikm128")); //Trys to make intent with YT's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/karthikm128")); //catches and opens a url to the desired page
        }
    }

    private void setLocationButton() {
        mLocationSetButton = (Button) findViewById(R.id.setlocationbtn);
        mLocationSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker marker = null;
                MarkerOptions markerOptions;
                if (marker != null) {
                    marker.remove();
                }
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerLocation");
                GeoFire geofire = new GeoFire(ref);
                mMyLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                geofire.setLocation(userID, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.man);
                markerOptions = new MarkerOptions().position(mMyLocation)
                        .title("Current Location")
                        .snippet("Thinking of finding some thing...")
                        .icon(icon);
                mMap.addMarker(markerOptions);
//                mLocationSetButton.setText("Getting your Driver..");

            }
        });

    }

    public void setseekbar() {


        seek_bar = (org.adw.library.widgets.discreteseekbar.DiscreteSeekBar) findViewById(R.id.seekbar);
        text_view_radius = (TextView) findViewById(R.id.radius);
        text_view_radius.setText("0m");
//        seek_bar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                Toast.makeText(getApplicationContext(),"Set the Location by dragging it",
//                        Toast.LENGTH_SHORT).show();
//                if(mMyLocation!=null){
//                    return true;
//                }
//                else{
//                    return  false;
//                }
//
//            }
//        });

        seek_bar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value;
            }
        });

        if (isLocationServiceEnabled(getApplicationContext())) {
            seek_bar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                int progress_value;

                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    progress_value = value;
                    mCircleRadius = value;
                    text_view_radius.setText("" + value + "m");
                    // Drawing circle on the map
                    if (mMyLocation != null) {
                        drawCircle(mMyLocation, value);
                    }
                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                    finalRadius = seekBar.getProgress();
                }
            });
        }

//        seek_bar.setOnSeekBarChangeListener(
//                new SeekBar.OnSeekBarChangeListener() {
//
//                    int progress_value;
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        progress_value = progress;
//                        mCircleRadius=progress;
//                        text_view_radius.setText("" + progress+"m");
//                        // Drawing circle on the map
//                        if(mMyLocation!=null){
//                            drawCircle(mMyLocation,progress);
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view_radius.setText("" + progress_value+"m");
//                    }
//                }
//        );

    }

    private void drawCircle(LatLng point, int radius) {
        if (circle != null) {
            circle.remove();

        }
        // Instantiating CircleOptions to draw a circle around the marker,
        // Specifying the center of the circle
        // Radius of the circle
        // Border color of the circle
        // Fill color of the circle
        // Border width of the circle
        // Adding the circle to the GoogleMap
        circle = mMap.addCircle(new CircleOptions().center(point).radius(radius).strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel(circle)), 2000, null);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        //create initial marker


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent speedChangeIntent = new Intent(this, SpeedChange.class);
            startActivity(speedChangeIntent);
        } else if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Main_Menu.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        FirebaseDatabase.getInstance()
                .getReference()
                .child("latitude")
                .setValue("Lat: "+mLastLocation.getLatitude()+" Lon : " +mLastLocation.getLongitude());
        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMarkerInitial == null) {
            mMarkerInitial = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Location")
                    .snippet("First Marker"));
            mMarkerInitial.showInfoWindow();
            mMyLocation = mMarkerInitial.getPosition();
        }


        if (zoomFlag) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            zoomFlag = false;
        }

    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /*
    * Called by Google Play services if the connection to GoogleApiClient drops because of an
    * error.
    */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("collectorAvailable");
//        GeoFire geofire = new GeoFire(ref);
//        geofire.removeLocation(userID);

//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub
        mMyLocation = marker.getPosition();
        drawCircle(mMyLocation, mCircleRadius);

    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        mMyLocation = marker.getPosition();
        // TODO Auto-generated method stub
        drawCircle(mMyLocation, mCircleRadius);
        LatLng dragPosition = arg0.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dragPosition));
        Toast.makeText(getApplicationContext(), "Marker Dragged..!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
    }


    @Override
    public void onMapLongClick(LatLng arg0) {
        // TODO Auto-generated method stub
        if (marker != null) {
            marker.remove();
        }
        //create new marker when user long clicks
        marker = mMap.addMarker(new MarkerOptions()
                .position(arg0)
                .draggable(true));
        mMarkerInitial.remove();
        mMyLocation = marker.getPosition();

    }

    private void getUserInfo() {
        mCustomerDetailsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("email") != null) {
                        mEmail = map.get("email").toString();
                        mEmailField.setText(mEmail);
                    }
                    if (map.get("profileImageUrl") != null) {
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                } else if (!dataSnapshot.exists()) {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        mProfileImageUrl = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
                        mName = mFirebaseAuth.getCurrentUser().getDisplayName();
                        mEmail = mFirebaseAuth.getCurrentUser().getEmail();

                        mNameField.setText(mName);
                        mEmailField.setText(mEmail);
                        Map userInfo = new HashMap();
                        userInfo.put("name", mName);
                        userInfo.put("email", mEmail);
                        mCustomerDetailsDatabase.updateChildren(userInfo);

                        muserProfile.setImageResource(R.drawable.avathar);
                        if (mProfileImageUrl != null) {
                            Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public int getZoomLevel(Circle circle) {
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public void Showalert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Menu.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


        //Create Notification using NotificationCompat.Builder
    public void postnotification(String title, String msg) {
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this, SpeedIndicator.class);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.spped)
                // Set Ticker Message
                .setTicker("Notification")
                // Set Title
                .setContentTitle(title)
                // Set Text
                .setContentText(msg)
                // Add an Action Button below Notification
                .addAction(R.mipmap.ic_launcher, "Action Button", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (Double) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }


    public boolean isLocationServiceEnabled(Context context) {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

}
