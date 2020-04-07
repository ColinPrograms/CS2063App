package com.mobile.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    MusicService mService;
    boolean bound = false;
    DatabaseHelper mydb = new DatabaseHelper(this);
    Receiver myreceiver = new Receiver();
    ImageButton playButton;
    private GoogleMap mMap;


    LocationHelper locationHelper;


    private static final int PERMISSION_CODE = 101;
    String[] permissions_all={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.playbtn);

        locationHelper = new LocationHelper(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        createNotificationChannel();
        ImageButton nextbtn = findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.next();
            }
        });
        registerReceiver(myreceiver,new IntentFilter("songInfo"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

    }
    private class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String artist = bundle.getString("artist");
            String song = bundle.getString("songName");
            TextView arttxt = findViewById(R.id.artisttxt);
            TextView songtxt = findViewById(R.id.songnametxt);
            arttxt.setText(artist);
            songtxt.setText(song);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent musicServiceIntent = new Intent(this, MusicService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(musicServiceIntent);
        } else {
            startService(musicServiceIntent);
        }

        bindService(musicServiceIntent,connection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            mService = binder.getService();
            bound = true;
            if(mService.isPlaying()){
                playButton.setImageResource(R.drawable.iconfinder_icon_pause);
            }
            mService.getSongInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void playlistsBtnClick(View v){
        if(!checkPermission()){
            requestPermission();
        }else{
            Intent toPlaylists = new Intent(this, PlaylistsActivity.class);
            startActivity(toPlaylists);
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void playbtnClick(View v) {
        if(!checkPermission()){
            requestPermission();
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Cursor playlists = mydb.getTableRows("playlists_table");
                int playSize = playlists.getCount();
                if(playSize > 0){
                    if(!bound){
                        Intent musicServiceIntent = new Intent(this, MusicService.class);
                        bindService(musicServiceIntent,connection, Context.BIND_AUTO_CREATE);
                    }
                    if(!mService.isPlaying()) {
                        mService.play();
                        if(mService.getPlaylistSize() > 0){
                            playButton.setImageResource(R.drawable.iconfinder_icon_pause);
                        }
                    }else{
                        mService.pause();
                        playButton.setImageResource(R.drawable.iconfinder_icon_play);
                    }
                }else{
                    Toast t = Toast.makeText(this,"No Playlists Exist",Toast.LENGTH_SHORT);
                    t.show();
                }
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            Log.d("service", "button pressed");
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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "All";
            String description = "All notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ONE", name, importance);
            channel.setDescription(description);

            channel.setSound(null,null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //UNUSED NOTIFICATION UPDATER
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LocationChange";
            String description = "locationchange";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TWO", name, importance);
            channel.setDescription(description);

            channel.setSound(null,null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

         */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng selection = new LatLng(43.8375, -66.1174);
        ArrayList<PlaylistObject> playlistObjects = LocationFinder.createPlaylistArrayList(mydb);
        Log.d("Embedded Map","" + playlistObjects.size());

        for(int i = 0; playlistObjects.size() > i; i++){
            PlaylistObject playlistObject = playlistObjects.get(i);
            selection = new LatLng(playlistObject.getLat(), playlistObject.getLng());
            mMap.addCircle(new CircleOptions()
                    .center(selection)
                    .radius(playlistObject.getRad())
                    .strokeColor(Color.argb(70,200,0,200))
                    .fillColor(Color.argb(50,200,0,200)));
        }

        locationHelper = new LocationHelper(this);
        if(checkPermission()){
            Location loc = locationHelper.getLocation();
            selection = new LatLng(loc.getLatitude(),loc.getLongitude());
        }

        mMap.addCircle(new CircleOptions()
                .center(selection)
                .radius(30)
                .strokeColor(Color.argb(0,200,0,200))
                .fillColor(Color.argb(255,200,0,200)));
        //mMap.addMarker(new MarkerOptions().position(selection));
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selection, 14));
        mMap.setMaxZoomPreference(22);
    }

}
