package com.mobile.app;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    DatabaseHelper mydb = new DatabaseHelper(this);

    private IBinder binder = new MusicBinder();

    MediaPlayer player;
    boolean playing = false;

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
        return player.isPlaying();
    }

    int count = 0;
    public void play(){
        Cursor cursor = mydb.getTableRows("playlist1");
        if(!playing){
            int size = cursor.getCount();
            if(count>=size){
                count = 0;
            }
            cursor.moveToPosition(count);
            String uriS = cursor.getString(3);
            Uri uri = Uri.parse(uriS);
            try{
                player.setDataSource(this,uri);
                player.prepareAsync();
            }catch(IOException e){

            }
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    count++;
                    Log.e("serviceee",count +"prepared");
                    playing = true;
                }
                });
        }else{
            Log.e("serviceee",count +"else");
            player.start();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play();
            }
        });
    }

    public void pause(){
        player.pause();
    }
}
