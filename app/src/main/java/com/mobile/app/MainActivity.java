package com.mobile.app;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    MusicService mService;
    boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
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

    }
}
