package com.mobile.app;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        **/

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
        Intent intent = getIntent();
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);

        // Add a marker in Sydney and move the camera
        LatLng selection = new LatLng(45.966425, -66.645813);
        mMarker = mMap.addMarker(new MarkerOptions().position(selection).title("Center").draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selection, 18));
        mMap.setMaxZoomPreference(22);
        Color translucentRed = Color.valueOf(0xffff0000);
        mCircle = mMap.addCircle(new CircleOptions()
                .center(selection)
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70,255,0,0)));

        ImageButton mImageButtonIncrease = findViewById(R.id.radIncrease);
        mImageButtonIncrease.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCircle.setRadius(mCircle.getRadius() + 15);
            }
        });

        ImageButton mImageButtonDecrease = findViewById(R.id.radDecrease);
        mImageButtonDecrease.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mCircle.getRadius() >= 15)
                    mCircle.setRadius(mCircle.getRadius() - 15);
            }
        });

    }


    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mCircle.setCenter(mMarker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mCircle.setCenter(mMarker.getPosition());
    }


}
