package com.mobile.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;
    private ImageButton mapConfirm;
    private Bundle bundle;
    private boolean hasLocation;
    private String passedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        hasLocation = bundle.getBoolean("hasLocation");
        passedLocation = bundle.getString("location");


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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
        mapConfirm = findViewById(R.id.mapConfirm);
        mMap.setOnMarkerDragListener(this);

        // Add a marker in Sydney and move the camera
        Color translucentRed = Color.valueOf(0xffff0000);
        LatLng selection;

        if(hasLocation){
            String[] circleLocation = passedLocation.split(" ");

            selection = new LatLng(Double.valueOf(circleLocation[0]), Double.valueOf(circleLocation[1]));




            mCircle = mMap.addCircle(new CircleOptions()
                    .center(selection)
                    .radius(Double.valueOf(circleLocation[2]))
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70,255,0,0)));
        }
        else{

            selection = new LatLng(43.8375, -66.1174);
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(selection)
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70,255,0,0)));
        }

        mMarker = mMap.addMarker(new MarkerOptions().position(selection).title("Center").draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selection, 18));
        mMap.setMaxZoomPreference(22);


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

        mapConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("lat", Double.toString(mMarker.getPosition().latitude));
                returnIntent.putExtra("lng", Double.toString(mMarker.getPosition().longitude));
                returnIntent.putExtra("rad", Double.toString(mCircle.getRadius()));
                setResult(RESULT_OK, returnIntent);
                finish();
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
