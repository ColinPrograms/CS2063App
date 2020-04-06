package com.mobile.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

    public class LocationHelper implements LocationListener {
        // private static final int PERMISSION_CODE = 101;
        // String[] permissions_all={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        private LocationManager locationManager;
        private boolean isGpsLocation;
        private Location loc;
        private boolean isNetworklocation;
        private Context context;

        public LocationHelper(Context context){
            super();
            this.context = context;
        }

        public Location getLocation() {

            getDeviceLocation();
            //showNotification();
            Log.d("Location is being found",loc.getLatitude() + "" + loc.getLongitude());
            return loc;
        }


    private void getDeviceLocation() {

        locationManager=(LocationManager)context.getSystemService(Service.LOCATION_SERVICE);
        isGpsLocation=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworklocation=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGpsLocation && !isNetworklocation){
            // showSettingForLocation();
            getLastlocation();
            Log.d("Location", "Got last location");
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
                loc = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFinalLocation() {
        try{
            if(isGpsLocation){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60,10,this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("Location", "Used GPS");

                }
            }
            else if(isNetworklocation){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60,10,this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("Location", "Used Network");

                }
            }
            else{
                Toast.makeText(context, "Can't Get Location", Toast.LENGTH_SHORT).show();
            }

        }catch (SecurityException e){
            Toast.makeText(context, "Can't Get Location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(context, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
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


    //UNUSED NOTIFICATION SENDING
    /*
    public void showNotification(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(context, "TWO")
                    .setContentTitle("backgroundlocationcheck")
                    .setContentText(loc.getLongitude() + " " + loc.getLatitude())
                    .setAutoCancel(true);

            Notification notification = builder.build();
            notificationManager.notify();

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle("backgroundlocationcheck")
                    .setContentText(loc.getLongitude() + " " + loc.getLatitude())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();


        }
    }

     */
}


