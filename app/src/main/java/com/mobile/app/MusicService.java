package com.mobile.app;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service {
    DatabaseHelper mydb = new DatabaseHelper(this);
    LocationHelper locationHelper = new LocationHelper(this);

    private IBinder binder = new MusicBinder();

    MediaPlayer player;
    private String playlistName = "dummy";
    boolean playing = false;
    boolean nextWhilePaused = false;
    private String currentSong = "";
    private String currentArtist = "";

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
    int size = 0;
    int getPlaylistSize(){
        return size;
    }
    public void play(){
        String newPlaylistName = LocationFinder.findPlaylist(mydb, locationHelper.getLocation());
        if(!newPlaylistName.equals(playlistName)){
            count = 0;
            playlistName = newPlaylistName;
        }
        Cursor cursor = mydb.getTableRows(playlistName);
        size = cursor.getCount();
        if(count>=size){
            count = 0;
        }
        if(size>0){
            if(!playing || nextWhilePaused){
                cursor.moveToPosition(count);
                String uriS = cursor.getString(3);
                Uri uri = Uri.parse(uriS);
                try{
                    player.reset();
                    player.setDataSource(this,uri);
                    player.prepareAsync();
                    currentSong = cursor.getString(1);
                    currentArtist = cursor.getString(2);
                    getSongInfo();
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
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playing = false;
                play();
            }
        });
    }

    public void getSongInfo(){
        Intent intent = new Intent("songInfo");
        intent.putExtra("songName", currentSong);
        intent.putExtra("artist", currentArtist);
        sendBroadcast(intent);
    }
    public void next(){
        if(isPlaying()){
            count++;
            player.stop();
            play();
        }else{
            Cursor cursor = mydb.getTableRows(playlistName);
            nextWhilePaused = true;
            count++;
            size = cursor.getCount();
            if(count>=size){
                count = 0;
            }
            cursor.moveToPosition(count);
            currentSong = cursor.getString(1);
            currentArtist = cursor.getString(2);
            getSongInfo();
        }
    }

    public void pause(){
        player.pause();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, "ONE")
                    .setContentTitle("SongScape")
                    .setContentText("Listening for playlists!")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_stat_name);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("SongScape")
                    .setContentText("Listening for playlists!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_stat_name);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
        return START_NOT_STICKY;
    }
}
