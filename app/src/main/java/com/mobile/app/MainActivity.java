package com.mobile.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends Activity {
    MusicService mService;
    boolean bound = false;
    DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent toPlaylists = new Intent(this, CreatePlaylist.class);
        startActivity(toPlaylists);
    }

    public void playbtnClick(View v) {
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

    }
}
