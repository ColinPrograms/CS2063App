package com.mobile.app;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {
    DatabaseHelper mydb = new DatabaseHelper(this);
    LocationHelper locationHelper = new LocationHelper(this);
    //MainActivity mainActivity = new MainActivity();

    private IBinder binder = new MusicBinder();

    MediaPlayer player;
    private String playlistName = "dummy";
    boolean playing = false;
    boolean nextWhilePaused = false;

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    public boolean isPlaying(){
        boolean playing = false;
        try{
            playing = player.isPlaying();
        }catch(Exception e){

        }
        return playing;
    }

    int count = 0;
    public void play(){

        String newPlaylistName = LocationFinder.findPlaylist(mydb, locationHelper.getLocation());
        if(!newPlaylistName.equals(playlistName)){
            count = 0;
            playlistName = newPlaylistName;
        }
        Cursor cursor = mydb.getTableRows(playlistName);



        int size = cursor.getCount();
        if(count>=size){
            count = 0;
        }
        if(!playing || nextWhilePaused){
            cursor.moveToPosition(count);
            String uriS = cursor.getString(3);
            Uri uri = Uri.parse(uriS);
            try{
                player.reset();
                player.setDataSource(this,uri);
                player.prepareAsync();
            }catch(IOException e){

            }
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    playing = true;
                    nextWhilePaused = false;
                }
                });
        }else{
            player.start();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playing = false;
                count++;
                play();
            }
        });
    }
    public void next(){
        if(isPlaying()){
            count++;
            player.stop();
            play();
        }else{
            nextWhilePaused = true;
            count++;
        }
    }

    public void pause(){
        player.pause();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, "ONE")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
        return START_NOT_STICKY;
    }
}
