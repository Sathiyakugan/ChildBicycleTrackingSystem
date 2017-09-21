package com.blogspot.gihanmora.childbicycletrackingsystem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class SetLocation extends AppCompatActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener ,GoogleMap.OnMarkerDragListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener {
    protected static final String TAG = "Customer Map Activity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mlocationRequest;
    private LatLng mMyLocation;
    private GoogleMap mMap;
    private SeekBar seek_bar;
    private TextView text_view_radius;
    private Button mLocationSetButton;
    private Circle circle;
    private Marker marker;
    private Marker mMarkerInitial;
    private int mCircleRadius=10;
    private Button mButtonSpeedIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // in Activity Context
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                // ...
                .attachTo(actionButton)
                .build();

        mButtonSpeedIndicator= (Button) findViewById(R.id.SpeedIndicator);
        mButtonSpeedIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SetLocation.this,SpeedIndicator.class);
                startActivity(intent);
                finish();
            }
        });

        setLocationButton();
        setseekbar( );

    }

    private void setLocationButton() {
        mLocationSetButton=(Button)findViewById(R.id.setlocationbtn);
        mLocationSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker marker = null;
                MarkerOptions markerOptions;
                if(marker!=null){
                    marker.remove();
                }
                String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerLocation");
                GeoFire geofire = new GeoFire(ref);
                mMyLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                geofire.setLocation(userID,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.eye);
                markerOptions = new MarkerOptions().position(mMyLocation)
                        .title("Current Location")
                        .snippet("Thinking of finding some thing...")
                        .icon(icon);
                mMap.addMarker(markerOptions);
//                mLocationSetButton.setText("Getting your Driver..");

            }
        });

    }

    public void setseekbar( ){
        seek_bar = (SeekBar)findViewById(R.id.seekbar);
        text_view_radius =(TextView)findViewById(R.id.radius);
        text_view_radius.setText("0m");


        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        mCircleRadius=progress;
                        text_view_radius.setText("" + progress+"m");
                        // Drawing circle on the map
                        drawCircle(mMyLocation,progress);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view_radius.setText("" + progress_value+"m");
                    }
                }
        );

    }

    private void drawCircle(LatLng point,int radius){
        if(circle!=null){
            circle.remove();

        }
        // Instantiating CircleOptions to draw a circle around the marker,
        // Specifying the center of the circle
        // Radius of the circle
        // Border color of the circle
        // Fill color of the circle
        // Border width of the circle
        // Adding the circle to the GoogleMap
        circle=mMap.addCircle(new CircleOptions().center(point).radius(radius).strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
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
        else if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent =new Intent(SetLocation.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        // Creating a LatLng object for the current location
        LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
        if(mMarkerInitial==null){
            mMarkerInitial=mMap.addMarker( new MarkerOptions()
                    .position(latLng)
                    .title("Location")
                    .snippet("First Marker"));
            mMarkerInitial.showInfoWindow();
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
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


        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub
        mMyLocation=marker.getPosition();
        drawCircle(mMyLocation,mCircleRadius);

    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        mMyLocation=marker.getPosition();
        // TODO Auto-generated method stub
        drawCircle(mMyLocation,mCircleRadius);
        LatLng dragPosition = arg0.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);
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
        if (marker!=null){
            marker.remove();
        }
        //create new marker when user long clicks
        marker=mMap.addMarker(new MarkerOptions()
                .position(arg0)
                .draggable(true));
        mMarkerInitial.remove();
        mMyLocation=marker.getPosition();

    }
}
