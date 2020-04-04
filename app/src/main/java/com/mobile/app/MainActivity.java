package com.mobile.app;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity implements LocationListener {
    MusicService mService;
    boolean bound = false;

    LocationManager locationManager;
    boolean isGpsLocation;
    Location loc;
    boolean isNetworklocation;


    private static final int PERMISSION_CODE = 101;
    String[] permissions_all={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(!checkPermission()){
            requestPermission();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
        bindService(musicServiceIntent,connection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void playlistsBtnClick(View v){
        Intent toPlaylists = new Intent(this, PlaylistsActivity.class);
        startActivity(toPlaylists);
    }

    public void playbtnClick(View v) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if(!bound){
                Intent musicServiceIntent = new Intent(this, MusicService.class);
                bindService(musicServiceIntent,connection, Context.BIND_AUTO_CREATE);
            }
            if(!mService.isPlaying()) {
                mService.play();
                v.setBackgroundResource(android.R.drawable.ic_media_pause);
            }else{
                mService.pause();
                v.setBackgroundResource(android.R.drawable.ic_media_play);
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }


        if(checkPermission()){
            getLocation();
            Toast.makeText(this, loc.getLatitude() + " " + loc.getLongitude(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,permissions_all,PERMISSION_CODE);
    }

    private boolean checkPermission() {
        for(int i=0;i<permissions_all.length;i++){
            int result= ContextCompat.checkSelfPermission(MainActivity.this,permissions_all[i]);
            if(result== PackageManager.PERMISSION_GRANTED){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public void getLocation() {
            if(checkPermission()){
                getDeviceLocation();
            }
            else{
                requestPermission();
            }
    }



    private void getDeviceLocation() {
        //now all permission part complete now let's fetch location
        locationManager=(LocationManager)getSystemService(Service.LOCATION_SERVICE);
        isGpsLocation=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworklocation=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGpsLocation && !isNetworklocation){
            // showSettingForLocation();
            getLastlocation();
        }
        else{
            getFinalLocation();
        }
    }

    private void getLastlocation() {
        if(locationManager!=null) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,false);
                Location location=locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFinalLocation() {
        try{
            if(isGpsLocation){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10,this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        this.loc = loc;
                    }
                }
            }
            else if(isNetworklocation){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10,MainActivity.this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null){
                        this.loc = loc;
                    }
                }
            }
            else{
                Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
            }

        }catch (SecurityException e){
            Toast.makeText(this, "Can't Get Location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
