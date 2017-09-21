package com.blogspot.gihanmora.childbicycletrackingsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.anastr.speedviewlib.Gauge;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class SpeedIndicator extends AppCompatActivity {
    private AwesomeSpeedometer awesomeSpeedometer;
    private Gauge gauge;
    private TextView mSpeedTextview;


    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_indicator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        awesomeSpeedometer= (AwesomeSpeedometer) findViewById(R.id.awesomeSpeedometer);
        gauge = (Gauge) findViewById(R.id.awesomeSpeedometer);
        // change MAX speed to 320
        awesomeSpeedometer.setMaxSpeed(100);
        // change speed to 140 Km/h
        awesomeSpeedometer.speedTo(0);
        gauge.speedTo(50,500);
        mSpeedTextview= (TextView) findViewById(R.id.speedText);
        mSpeedTextview.setText(awesomeSpeedometer.getSpeed()+"Km/h");

        FirebaseUser user=mFirebaseAuth.getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Customer").child(user.getUid()).child("Speed");

        // Read from the database
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataSnapshot.getValue();
                mSpeedTextview.setText(dataSnapshot.getValue()+"Km/h");
                gauge.speedTo(Integer.parseInt(dataSnapshot.getValue().toString()),500);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

}
